package org.swdc.llm;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Pointer;
import org.swdc.llama.core.*;
import org.swdc.llm.exceptions.ChatException;
import org.swdc.llm.exceptions.EmbeddingException;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * LLamaCpp的嵌入模型类。
 * 本类使用嵌入生成模型生成文本的嵌入向量，可以用于相似度搜索以及存储到
 * 向量数据库中。
 */
public class LLMEmbeddingModel {

    /**
     * 嵌入模型对象。
     */
    private llama_model model;

    /**
     * 嵌入模型的词汇表。
     */
    private llama_vocab vocab;

    /**
     * 嵌入模型的上下文。
     */
    private llama_context context;

    /**
     * 模型参数对象
     */
    private LLMParameter parameter;

    /**
     * 模型文件。
     */
    private File modelFile;

    /**
     * 模型加载的进度监控器
     */
    private volatile Consumer<Float> progressMonitor;

    /**
     * 嵌入向量的归一化方式。
     */
    private int normalize = CommonUtils.NORMALIZE_EUCLIDEAN;

    /**
     * 模型加载进度回调。
     */
    private final llama_progress_callback progressCallback = new llama_progress_callback(){
        @Override
        public boolean call(float progress, Pointer user_data) {
            if(progressMonitor == null) {
                return true;
            }
            progressMonitor.accept(progress);
            return true;
        }
    };

    /**
     * 构造方法，创建一个嵌入模型。
     * @param parameter 模型参数对象。
     * @param embedModel 嵌入模型文件。
     */
    public LLMEmbeddingModel(LLMParameter parameter, File embedModel) {

        this.parameter = parameter;
        this.modelFile = embedModel;

    }

    /**
     * 设置嵌入向量的归一化方式。
     * @param normalize 归一化方式。
     */
    public void setEmbeddingNormalize(int normalize) {
        this.normalize = normalize;
    }

    /**
     * 加载嵌入模型。
     */
    public void load() {
        load(null);
    }

    /**
     * 加载嵌入模型。
     * @param progressMonitor 进度监控器。
     * @return 加载是否成功。
     */
    public synchronized boolean load(Consumer<Float> progressMonitor) {

        if (model != null && !model.isNull()) {
            return true;
        }

        if (this.modelFile == null || !this.modelFile.exists() || this.modelFile.isDirectory()) {
            return false;
        }

        if (parameter == null) {
            return false;
        }

        llama_model_params params = LLamaCore.llama_model_default_params();
        params.n_gpu_layers(parameter.getGpuLayers());
        params.use_mmap(parameter.isMemorySwap());
        if (progressMonitor != null) {
            this.progressMonitor = progressMonitor;
            params.progress_callback(progressCallback);
        }

        model = LLamaCore.llama_load_model_from_file(modelFile.getAbsolutePath(), params);
        if (model == null || model.isNull()) {
            // 失败。
            return false;
        }
        // 加载词汇表
        this.vocab = LLamaCore.llama_model_get_vocab(model);

        // 初始化上下文参数，并创建新的上下文
        llama_context_params ctxParams = LLamaCore.llama_context_default_params();
        ctxParams.n_ctx(parameter.getContextSize());
        ctxParams.n_batch(parameter.getBatchSize());
        ctxParams.n_ubatch(parameter.getBatchSize());
        ctxParams.n_threads(parameter.getThreads());
        ctxParams.embeddings(true);
        ctxParams.kv_unified(true);

        this.context = LLamaCore.llama_new_context_with_model(model, ctxParams);
        if (context == null || context.isNull()) {
            unload();
            return false;
        }

        if (LLamaCore.llama_model_has_decoder(model) && LLamaCore.llama_model_has_encoder(model)) {
            // 双向模型，不支持embedding。
            unload();
            return false;
        }

        int poolType = LLamaCore.llama_pooling_type(context);
        if (poolType == LLamaCore.LLAMA_POOLING_TYPE_RANK) {
            // 不是embedding模型，是Rank模型。
            unload();
            return false;
        }

        return true;
    }


    public float[] embedding(String text) {
        FloatPointer embedding = createEmbedding(text);
        if (embedding.isNull()) {
            return null;
        }
        float[] result = new float[(int)(embedding.capacity())];
        embedding.get(result);
        embedding.close();
        return result;
    }

    /**
     * 生成文本的嵌入向量。
     * @param text 文本内容。
     * @return 嵌入向量指针。
     */
    private FloatPointer createEmbedding(String text) {

        if (text == null || text.isEmpty()) {
            return null;
        }

        // 将文本按行分割，并过滤掉空行。
        List<String> lineList = Arrays.stream(text.split("\n"))
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());

        // 对文本进行分词，并存入数组中。
        String[] lines = lineList.toArray(new String[0]);
        IntPointer[] tokenized = new IntPointer[lines.length];

        for (int index = 0; index < lines.length; index++) {
            // 对提供的prompt的每一句进行分词，并存入数组中。
            IntPointer tokens = tokenize(lines[index], true, true);
            tokenized[index] = tokens;
        }

        // 创建batch对象，稍后用于添加token和计算embedding。
        llama_batch batch = LLamaCore.llama_batch_init(
                parameter.getBatchSize(),
                0,
                1);

        // 每个序列有多少个嵌入向量。
        int n_embd_count = 0;
        // 计算嵌入向量的数量。
        int pooling_type = LLamaCore.llama_pooling_type(context);
        if (pooling_type == LLamaCore.LLAMA_POOLING_TYPE_NONE) {
            // 每一个token对应应嵌入的时候。
            for (int index = 0; index < tokenized.length; index++) {
                IntPointer tokens = tokenized[index];
                n_embd_count = n_embd_count + (int)tokens.capacity();
            }
        } else {
            // 每个序列一个嵌入向量。
            n_embd_count = tokenized.length;
        }

        // 嵌入深度，所谓嵌入深度，就是一个token在模型内部具有多少维度。
        // 大语言模型的向量是一个高维向量，所有的维度放在一起才能表达一个完整的向量，
        // 以三维的空间向量为例，需要有x，y，z三个维度才能表达一个向量，而这里需要n_embd个维度。
        int n_embd = LLamaCore.llama_n_embd(model);
        // 嵌入向量的大小，嵌入深度乘以嵌入向量的数量。
        int outsize = n_embd_count * n_embd * Pointer.sizeof(FloatPointer.class);
        // 申请内存，构造指针用于存储生成的嵌入向量。
        FloatPointer out = new FloatPointer(Pointer.malloc(outsize));
        out.limit(outsize);
        out.capacity((long) n_embd * n_embd_count);
        Pointer.memset(out, 0, outsize);

        // llamacpp在一次生成中可以处理多个序列，这是它最大能处理的序列数量。
        // 这里会一次性送入多行到多个序列中，最终一口气生成一个批次的向量，如此效率会比较高。
        int maxSeq = (int)LLamaCore.llama_max_parallel_sequences();
        // 当前使用的序列id
        int currentSeq = 0;
        // 已经处理的token数量。
        int tokensProceed = 0;
        // batch的基础position，也就是token所在的位置。
        EmbeddingException exception = null;

        for (int index = 0; index < tokenized.length; index++) {

            // 当前的token序列
            IntPointer tokens = tokenized[index];
            // 当前的token数量
            int n_tokens = (int) tokens.capacity();
            if (batch.n_tokens() + n_tokens > parameter.getBatchSize() || currentSeq >= maxSeq) {
                try {
                    // batch装满了，需要生成嵌入向量。
                    FloatPointer outTarget = new FloatPointer(out);
                    outTarget.position((long) tokensProceed * n_embd);
                    multipleSeqDecode(batch, outTarget,n_embd, normalize);
                    if (pooling_type == LLamaCore.LLAMA_POOLING_TYPE_NONE) {
                        // 每个token一个嵌入向量。
                        tokensProceed = tokensProceed + n_tokens;
                    } else {
                        // 每个序列一个嵌入向量。
                        tokensProceed = tokensProceed + currentSeq;
                    }
                    // 重置batch，以便开始下一个批次。
                    currentSeq = 0;
                    CommonUtils.resetBatch(batch);

                } catch (EmbeddingException e) {
                    exception = e;
                    break;
                }

            }

            CommonUtils.fillEmbdBatch(
                    batch,
                    tokens,
                    0,
                    n_tokens,
                    currentSeq
            );

            currentSeq ++;

        }

        try {
            FloatPointer outTarget = new FloatPointer(out);
            outTarget.position((long) tokensProceed * n_embd);
            multipleSeqDecode(batch, outTarget,n_embd, normalize);
        } catch (EmbeddingException e) {
            exception = e;
        }

        LLamaCore.llama_batch_free(batch);
        for (IntPointer token : tokenized) {
            token.close();
        }
        if (exception != null) {
            out.close();
            throw new RuntimeException(exception);
        }

        return out;
    }

    private void multipleSeqDecode(llama_batch batch, FloatPointer out, int n_embd, int embdNorm) throws EmbeddingException {

        llama_memory_i memoryI = LLamaCore.llama_get_memory(context);
        LLamaCore.llama_memory_clear(memoryI,true);

        if(LLamaCore.llama_decode(context,batch) < 0) {
            throw new EmbeddingException("failed to decode!");
        }

        int poolingType = LLamaCore.llama_pooling_type(context);

        for (int index = 0; index < batch.n_tokens(); index++) {

            if (batch.logits().get(index) == 0) {
                continue;
            }
            int embdPos = 0;
            FloatPointer embd = null;

            if (poolingType == LLamaCore.LLAMA_POOLING_TYPE_NONE) {
                embd = LLamaCore.llama_get_embeddings_ith(context,index);
                embdPos = index;
                if (embd == null || embd.isNull()) {
                    throw new EmbeddingException("failed to get embedding!");
                }
            } else {
                embd = LLamaCore.llama_get_embeddings_seq(
                        context, batch.seq_id(index).get(0)
                );
                embdPos = batch.seq_id(index).get(0);
                if (embd == null || embd.isNull()) {
                    throw new EmbeddingException("failed to get embedding!");
                }
            }
            FloatPointer outTarget = new FloatPointer(out);
            outTarget.position((long) embdPos * n_embd);
            CommonUtils.normalize(embd, outTarget, n_embd, embdNorm);

        }



    }


    private IntPointer tokenize(String prompt, boolean addSpecial, boolean parseSpecial) {

        if (prompt == null) {
            return null;
        }

        int n_tokens = prompt.length();
        if (addSpecial) {
            n_tokens += 2;
        }

        byte[] promptBytes = prompt.getBytes(StandardCharsets.UTF_8);
        BytePointer promptBuf = new BytePointer(Pointer.malloc(promptBytes.length));
        Pointer.memset(promptBuf, 0, prompt.getBytes().length);
        promptBuf.put(promptBytes);

        // 分配内存，用于存放token化后的结果
        IntPointer tokens = new IntPointer(Pointer.malloc(
                (long)(n_tokens) * Pointer.sizeof(IntPointer.class)
        ));
        // 清空内存，防止有残留数据影响。
        Pointer.memset(tokens, 0, (long) n_tokens * Pointer.sizeof(IntPointer.class));
        n_tokens = LLamaCore.llama_tokenize(
                vocab,
                promptBuf,
                promptBytes.length,
                tokens,
                n_tokens,
                addSpecial,
                parseSpecial
        );

        if (n_tokens == Integer.MIN_VALUE) {
            promptBuf.close();
            tokens.close();
            throw new ChatException("prompt is too large!");
        } else if (n_tokens < 0) {
            n_tokens = -n_tokens;
            tokens.close();
            tokens = new IntPointer(Pointer.malloc((long) n_tokens * Pointer.sizeof(IntPointer.class)));
            Pointer.memset(tokens, 0, (long) n_tokens * Pointer.sizeof(IntPointer.class));
            int check = LLamaCore.llama_tokenize(
                    vocab,
                    promptBuf,
                    promptBytes.length,
                    tokens,
                    n_tokens,
                    addSpecial,
                    parseSpecial
            );
            if (check != n_tokens) {
                promptBuf.close();
                tokens.close();
                throw new ChatException("tokenize error!");
            }
        }

        int realFilledSize = n_tokens * Pointer.sizeof(IntPointer.class);
        tokens.limit(realFilledSize);
        tokens.capacity(n_tokens);
        return tokens;

    }


    public synchronized void unload() {
        if (context != null && !context.isNull()) {
            LLamaCore.llama_free(context);
            this.context = null;
        }
        if (model != null && !model.isNull()) {
            LLamaCore.llama_free_model(model);
            this.model = null;
        }
    }

}
