package org.swdc.llm;

import org.bytedeco.javacpp.*;
import org.swdc.llama.core.*;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Embeddings 类用于生成嵌入向量。
 * 参考LLama.cpp的embeddings.cpp以及commons.cpp文件。
 * Commons.cpp提供了一些常用的函数，但是这些函数没有公开到动态库中，因此这里需要使用java
 * 重写这些函数。
 */
public class Embeddings implements Closeable {


    private static class Pair<K,V> {
        private K key;
        private V value;
        Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }

    private llama_model model;

    private llama_context context;

    private llama_vocab vocab;

    public Embeddings(File embeddingModel) {

        if (embeddingModel == null || !embeddingModel.exists()) {
            throw new RuntimeException("embedding model file not found");
        }

        // 初始化模型参数
        llama_model_params params = LLamaCore.llama_model_default_params();
        // 启用mmap内存交换
        params.use_mmap(true);
        // 设置GPU层数为0，即不使用GPU加速
        params.n_gpu_layers(0);

        // 加载模型
        model = LLamaCore.llama_load_model_from_file(
                embeddingModel.getAbsolutePath(),
                params
        );

        vocab = LLamaCore.llama_model_get_vocab(model);
        if (LLamaCore.llama_model_has_encoder(model) && LLamaCore.llama_model_has_decoder(model)) {
            throw new RuntimeException("computing embeddings in encoder-decoder models is not supported");
        }

        // 初始化上下文参数
        llama_context_params llama_context_params = LLamaCore.llama_context_default_params();
        llama_context_params.n_ctx(2048);
        llama_context_params.n_batch(1024);
        llama_context_params.n_threads(4);
        llama_context_params.embeddings(true);

        // 初始化上下文
        context = LLamaCore.llama_init_from_model(model,llama_context_params);

    }

    public List<float[]> generateEmbeddings(String prompts, String splitStr, int n_batch, int normalize) {

        if (splitStr == null || splitStr.isEmpty()) {
            splitStr = "\n";
        }

        List<Pair<IntPointer,Integer>> promptTokens = new ArrayList<>();
        String[] promptsArr = prompts.split(splitStr);
        for (String prompt: promptsArr) {
            Pair<IntPointer,Integer> tokens = common_tokenize(prompt, true, true);
            if (tokens == null) {
                continue;
            }
            promptTokens.add(tokens);
        }

        Pair<IntPointer,Integer> last = promptTokens.get(promptTokens.size() - 1);

        if(last.getValue() == 0) {
            System.err.println("last token in the prompt is not SEP");
        } else {
            int lastToken = last.getKey().get(last.getValue() - 1);
            boolean isLastTokenSep = lastToken != LLamaCore.llama_vocab_sep(vocab);
            if (!isLastTokenSep) {
                System.err.println("last token in the prompt is not SEP");
            }
        }


        int n_embd_count = 0;
        boolean noPooling = LLamaExt.ext_llama_pooling_type(context) == 0;
        if (noPooling) {
            for (Pair<IntPointer,Integer> promptToken: promptTokens) {
                n_embd_count += (promptToken.getValue());
            }
        } else {
            n_embd_count = promptTokens.size();
        }

        llama_batch batch = LLamaCore.llama_batch_init(n_batch,0,1);

        int n_embed = LLamaCore.llama_n_embd(model);
        FloatPointer emb = new FloatPointer(Pointer.malloc(
                (long) n_embed * n_embd_count * Pointer.sizeof(FloatPointer.class)
        ));
        Pointer.memset(emb, 0, (long) n_embed * n_embd_count * Pointer.sizeof(FloatPointer.class));

        // break into batches
        // number of embeddings already stored
        int e = 0;
        // number of prompts in current batch
        int s = 0;
        for (Pair<IntPointer,Integer> inp : promptTokens) {

            int n_tokens = inp.getValue();
            if (batch.n_tokens() + n_tokens > n_batch) {
                FloatPointer out = emb.getPointer((long) e * n_embed);
                batch_decode(batch, out, n_embed, normalize);
                e += noPooling ? batch.n_tokens() : s;
                s = 0;
                common_batch_clear(batch);
            }

            batch_add_seq(batch, inp, s);
            s = s + 1;
        }

        // final batch
        FloatPointer out = emb.getPointer((long) e * n_embed);
        batch_decode(batch, out, n_embed, normalize);

        List<float[]> embeddings = new ArrayList<>();
        for (int i = 0; i < n_embd_count; i ++) {

            float[] embedding = new float[n_embed];
            FloatPointer pointer = out.getPointer((long) i * n_embed);
            pointer.get(embedding);

            embeddings.add(embedding);
        }

        emb.close();
        for (Pair<IntPointer,Integer> inp : promptTokens) {
            inp.getKey().close();
        }
        promptTokens.clear();
        LLamaCore.llama_batch_free(batch);

        return embeddings;
    }

    /**
     * 将序列添加到批次中。
     *
     * 参照 LLama.cpp项目的embedded.cpp文件中的同名函数编写。
     *
     * @param batch 批次对象。
     * @param tokens tokens 包含要添加的令牌序列。
     * @param seqId 序列的ID。
     */
    private void batch_add_seq(llama_batch batch, Pair<IntPointer,Integer> tokens, int seqId) {
        for (int i = 0; i < tokens.getValue(); i++) {
            int token = tokens.getKey().get(i);
            common_batch_add(
                    batch, token, i, List.of(seqId), true
            );
        }
    }

    /**
     * 将令牌添加到批次中。
     * 参照 LLama.cpp项目的commons.cpp文件中的同名函数编写。
     *
     * @param batch 批次对象。
     * @param token 要添加的令牌。
     * @param pos   位置。
     * @param seqIds 序列ID列表。
     * @param logits 是否记录logits。
     */
    private void common_batch_add(llama_batch batch,int token,int pos,List<Integer> seqIds, boolean logits) {

        int batchPos = batch.n_tokens();
        Pointer seq = batch.seq_id(batchPos);
        if(seq == null || seq.isNull()) {
            throw new RuntimeException("llama_batch size exceeded");
        }

        batch.token().put(batchPos, token);
        batch.pos().put(batchPos, pos);
        batch.n_seq_id().put(batchPos, seqIds.size());
        for(int i = 0; i < seqIds.size(); i++) {
            IntPointer pSeq = new IntPointer(
                    batch.seq_id().get(batchPos)
            );
            pSeq.put(i, seqIds.get(i));
        }

        batch.n_tokens(batch.n_tokens() + 1);
        batch.logits().put(batchPos, (byte) (logits ? 1 : 0));

    }

    /**
     * 清除批次。
     * 参照 LLama.cpp项目的commons.cpp文件中的同名函数编写。
     *
     * @param batch 批次对象。
     */
    private void common_batch_clear(llama_batch batch) {
        batch.n_tokens(0);
    }

    /**
     * 解码批次。
     * 参照 LLama.cpp项目的embedded.cpp文件中的同名函数编写。
     *
     * @param batch    批次对象。
     * @param output   输出指针。
     * @param n_embed  嵌入维度。
     * @param embd_norm 嵌入归一化类型。
     */
    private void batch_decode(llama_batch batch, FloatPointer output, int n_embed, int embd_norm) {

        boolean noPooling = LLamaExt.ext_llama_pooling_type(context) == 0;

        // clear previous kv_cache values (irrelevant for embeddings)
        LLamaCore.llama_kv_cache_clear(context);

        if (LLamaCore.llama_model_has_encoder(model) && !LLamaCore.llama_model_has_decoder(model)) {
            // encoder-only model
            if(LLamaCore.llama_encode(context, batch) < 0) {
                System.err.println("error during llama_encode");
            }
        } else if (!LLamaCore.llama_model_has_encoder(model) && LLamaCore.llama_model_has_decoder(model)) {
            // decoder-only model
            if(LLamaCore.llama_decode(context, batch) < 0) {
                System.err.println("error during llama_decode");
            }
        }

        for (int i = 0; i < batch.n_tokens(); i++) {
            if(batch.logits() == null || batch.logits().get(i) == 0) {
                continue;
            }
            FloatPointer embd = null;
            int embd_pos = 0;

            if (noPooling) {
                embd = LLamaCore.llama_get_embeddings_ith(context, i);
                embd_pos = i;
                if (embd == null || embd.isNull()) {
                    throw new RuntimeException("failed to get token embeddings");
                }
            } else {
                int seq = batch.seq_id(i).get();
                embd = LLamaCore.llama_get_embeddings_seq(context, seq);
                embd_pos = seq;
                if (embd == null || embd.isNull()) {
                    throw new RuntimeException("failed to get sequence embeddings");
                }
            }

            FloatPointer dst = output.getPointer((long) embd_pos * n_embed);
            common_normalize(embd, dst, n_embed, embd_norm);
        }


    }

    /**
     * 归一化嵌入。
     * 参照 LLama.cpp项目的commons.cpp文件中的同名函数编写。
     *
     * @param inp 输入嵌入。
     * @param out 输出嵌入。
     * @param n   嵌入维度。
     * @param embd_norm 嵌入归一化类型。
     */
    private void common_normalize(FloatPointer inp, FloatPointer out,int n, int embd_norm) {

        double sum = 0.0;
        switch (embd_norm) {
            case -1: {
                // no normalisation
                sum = 1.0;
                break;
            }
            case 0: {
                // max absolute
                for (int i = 0; i < n; i++) {
                    float v = inp.get(i);
                    if (sum < Math.abs(v)) {
                        sum = Math.abs(v);
                    }
                }
                sum /= 32760.0; // make an int16 range
                break;
            }
            case 2: {
                // euclidean
                for (int i = 0; i < n; i++) {
                    float v = inp.get(i);
                    sum += v * v;
                }
                sum = Math.sqrt(sum);
                break;
            }
            default: {
               // p-norm (euclidean is p-norm p=2)
                for (int i = 0; i < n; i++) {
                    float v = inp.get(i);
                    sum += Math.pow(Math.abs(v), embd_norm);
                }
                sum = Math.pow(sum, 1.0 / embd_norm);
                break;
            }
        }

        double norm = sum > 0.0 ? 1.0 / sum : 0.0f;
        for (int i = 0; i < n; i++) {
            out.put(i, (float) (inp.get(i) * norm));
        }

    }

    /**
     * 将文本转换为token。
     * 参照 LLama.cpp项目的commons.cpp文件中的同名函数编写。
     *
     * @param prompt        文本。
     * @param add_special   是否添加特殊token。
     * @param parse_special 是否解析特殊token。
     * @return token指针和数量。
     */
    private Pair<IntPointer,Integer> common_tokenize(String prompt,boolean add_special, boolean parse_special) {

        byte[] promptBytes = prompt.getBytes(StandardCharsets.UTF_8);

        BytePointer promptBuf = new BytePointer(Pointer.malloc(promptBytes.length));
        Pointer.memset(promptBuf, 0, prompt.getBytes().length);
        promptBuf.put(promptBytes);

        int n_tokens = -LLamaCore.llama_tokenize(
                vocab,
                promptBuf,
                promptBytes.length,
                (IntPointer) null,
                0,
                add_special,
                parse_special
        );
        IntPointer tokens = new IntPointer(Pointer.malloc(
                (long)(n_tokens) * Pointer.sizeof(IntPointer.class)
        ));
        Pointer.memset(tokens, 0, (long) n_tokens * Pointer.sizeof(IntPointer.class));

        if(LLamaCore.llama_tokenize(
                vocab,
                promptBuf,
                promptBytes.length,
                tokens,
                n_tokens,
                add_special,
                parse_special
        ) < 0) {
            tokens.close();
            promptBuf.close();
            return null;
        }

        return new Pair<>(tokens, n_tokens);
    }

    @Override
    public void close() throws IOException {
        if (context != null) {
            LLamaCore.llama_free(context);
            context = null;
        }
        if (model != null) {
            LLamaCore.llama_free_model(model);
            model = null;
        }
        vocab = null;
    }

}
