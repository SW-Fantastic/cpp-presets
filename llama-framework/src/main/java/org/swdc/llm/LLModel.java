package org.swdc.llm;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Pointer;
import org.swdc.llama.core.*;
import org.swdc.llama.core.ggml.ggml_log_callback;
import org.swdc.llm.exceptions.ChatException;
import org.swdc.llm.prompts.PromptRole;
import org.swdc.llm.prompts.Prompts;

import java.io.Closeable;
import java.io.File;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LLModel implements Closeable {

    private llama_model model;

    private llama_context context;

    private llama_vocab vocab;

    private llama_sampler sampler;

    private int systemPromptPos = 0;

    private int llamaPosOffset = 0;

    private File modelFile;

    private LLMParameter parameter;

    private List<ChatMessage> messages;

    private ChatPrompt prompt;

    private String systemPrompt;

    private volatile Consumer<Float> progressMonitor;

    private static BiConsumer<Integer, String> ggmlLogMonitor = null;

    private static final ggml_log_callback logCallback = new ggml_log_callback() {

        @Override
        public void call(int level, BytePointer text, Pointer user_data) {
            if (ggmlLogMonitor != null) {
                String textStr = text.getString(StandardCharsets.UTF_8);
                ggmlLogMonitor.accept(level, textStr);
            }
        }

    };

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
     * LLModal类的构造函数。
     *
     * 用于创建LLModal对象，初始化其参数和模型文件。
     *
     * @param parameter 参数对象，包含模型加载所需的配置信息。
     * @param modelFile 模型文件，包含模型数据的文件对象。
     */
    public LLModel(LLMParameter parameter, File modelFile) {
        this.parameter = parameter;
        this.modelFile = modelFile;
    }

    public synchronized boolean load() {
        return load(null);
    }

    /**
     * 加载模型
     *
     * @return 如果模型加载成功，返回true；否则返回false
     */
    public synchronized boolean load(Consumer<Float> progressMonitor) {
        // 验证模型是否已经加载，如果已加载则直接返回true
        if (this.model != null && !this.model.isNull()) {
            return true;
        }
        // 验证模型文件是否存在，如果不存在则直接返回false
        if (this.modelFile == null || !this.modelFile.exists() || this.modelFile.isDirectory()) {
            return false;
        }
        // 验证参数是否为空，如果为空则直接返回false
        if (parameter == null) {
            return false;
        }

        // 初始化模型参数，并加载模型文件
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
        ctxParams.n_threads(parameter.getThreads());

        this.context = LLamaCore.llama_new_context_with_model(model, ctxParams);
        if (context == null || context.isNull()) {
            unload();
            return false;
        }

        // 初始化采样器参数，并创建新的采样器
        llama_sampler_chain_params sampler_chain_params = LLamaCore.llama_sampler_chain_default_params();
        sampler = LLamaCore.llama_sampler_chain_init(sampler_chain_params);
        if (sampler == null || sampler.isNull()) {
            unload();
            return false;
        }

        if (parameter.getMinP() != null && parameter.getMinKeepP() != null) {
            LLamaCore.llama_sampler_chain_add(sampler,LLamaCore.llama_sampler_init_min_p(parameter.getMinP(),parameter.getMinKeepP()));
        }

        if (parameter.getTopK() != null) {
            LLamaCore.llama_sampler_chain_add(sampler, LLamaCore.llama_sampler_init_top_k(parameter.getTopK()));
        }

        if (parameter.getTopP() != null && parameter.getMinKeepTopP() != null) {
            LLamaCore.llama_sampler_chain_add(sampler, LLamaCore.llama_sampler_init_top_p(parameter.getTopP(), parameter.getMinKeepTopP()));
        }

        if (parameter.getTemp() != null) {
            LLamaCore.llama_sampler_chain_add(sampler, LLamaCore.llama_sampler_init_temp(parameter.getTemp()));
        }

        LLamaCore.llama_sampler_chain_add(sampler,LLamaCore.llama_sampler_init_dist(parameter.getSeeds()));

        // 获取聊天模板。
        if (parameter.getPrompt() == null) {
            // 如果没有提供聊天模板，则从模型中获取默认的聊天模板。
            String template = LLamaCore.llama_model_chat_template(model,(String) null);
            this.prompt = Prompts.getByModel(template);
            if (prompt == null) {
                // 如果没有提供聊天模板，则抛出异常。
                unload();
                return false;
            }
        } else {
            // 如果提供了聊天模板，则使用提供的聊天模板。
            this.prompt = parameter.getPrompt();
        }

        this.messages = new ArrayList<>();

        return true;
    }


    public synchronized String chat(String prompt) {
        return this.chat(prompt, System.out);
    }


    public synchronized void contextReset() {

        llama_memory_i memoryI = LLamaCore.llama_get_memory(context);
        LLamaCore.llama_memory_clear(memoryI, true);
        llamaPosOffset = 0;
        if (systemPrompt != null) {
            generateText(context, vocab, sampler, systemPrompt, null);
            systemPromptPos = LLamaCore.llama_memory_seq_pos_max(memoryI,0);
        }

    }

    /**
     * 与模型进行聊天
     *
     * @param prompt 用户输入的聊天内容
     * @param stream 输出流，用于将聊天内容输出到控制台或其他输出设备
     * @return 模型生成的回复内容
     * @throws ChatException 如果加载模型失败，或者生成出现问题会抛出 ChatException 异常
     */
    public synchronized String chat(String prompt, PrintStream stream) {

        if(this.model == null || this.context == null) {
            if(!load()) {
                throw new ChatException("Failed to load model.");
            }
        }

        ChatMessage chat = new ChatMessage(PromptRole.USER, prompt);
        messages.add(chat);

        String promptText = this.prompt.prompt(List.of(chat), true);
        //String promptText = template.compile(messages).trim();
        String result = generateText(context, vocab, sampler, promptText, stream);

        ChatMessage message = new ChatMessage(PromptRole.ASSISTANT, result);
        messages.add(message);

        return result;

    }

    public synchronized void addSystemPrompt(String prompt) {

        ChatMessage chat = new ChatMessage(PromptRole.SYSTEM, prompt);
        String promptText = this.prompt.prompt(List.of(chat), true);
        generateText(context, vocab, sampler, promptText, null);
        messages.add(chat);
        this.systemPrompt = promptText;

        llama_memory_i memoryI = LLamaCore.llama_get_memory(context);
        systemPromptPos = LLamaCore.llama_memory_seq_pos_max(memoryI,0);
        llamaPosOffset = systemPromptPos;

    }

    private String generateText(llama_context context, llama_vocab vocab, llama_sampler sampler, String prompt, PrintStream stream) {

        StringBuilder output = new StringBuilder();

        llama_memory_i memoryI = LLamaCore.llama_get_memory(context);
        boolean isFirst = LLamaCore.llama_memory_seq_pos_max(memoryI,0) == 0;
        byte[] promptBytes = prompt.getBytes(StandardCharsets.UTF_8);

        BytePointer promptBuf = new BytePointer(Pointer.malloc(promptBytes.length));
        Pointer.memset(promptBuf, 0, prompt.getBytes().length);
        promptBuf.put(promptBytes);

        // 获取token化后会出现多少个token
        int n_tokens = -LLamaCore.llama_tokenize(
                vocab,
                promptBuf,
                promptBytes.length,
                (IntPointer) null,
                0,
                isFirst,
                true
        );

        // 分配内存，用于存放token化后的结果
        IntPointer tokens = new IntPointer(Pointer.malloc(
                (long)(n_tokens) * Pointer.sizeof(IntPointer.class)
        ));
        // 清空内存，防止有残留数据影响。
        Pointer.memset(tokens, 0, (long) n_tokens * Pointer.sizeof(IntPointer.class));

        // 将用户的文本进行分词，转换为token
        if(LLamaCore.llama_tokenize(
                vocab,
                promptBuf,
                promptBytes.length,
                tokens,
                n_tokens,
                isFirst,
                true
        ) < 0) {
            // 失败了，直接返回空字符串，并且释放资源
            tokens.close();
            promptBuf.close();
            return "";
        }

        // 创建并且填充一个Batch对象，用于后续的解算和生成。
        llama_batch llamaBatch = createBatch(tokens, n_tokens,0);
        // 累加context中的Offset，这个Offset必须是连续的，否则会导致异常。
        llamaPosOffset = llamaPosOffset + n_tokens - 1;

        // 创建一个新的token，用于存放生成的文本
        IntPointer newTokenId = new IntPointer(Pointer.malloc(Pointer.sizeof(IntPointer.class)));
        // 清空内存。
        newTokenId.put(0);

        ChatException exception = null;

        // 未完成的UTF8数据，在下次生成的时候需要拼接在开头。
        byte[] inCompleteUtf8Data = null;

        while (true) {
            int n_ctx = LLamaCore.llama_n_ctx(context);
            int n_ctx_used = LLamaCore.llama_memory_seq_pos_max(memoryI,0) + 1;
            if(n_ctx_used + llamaBatch.n_tokens() > n_ctx) {

                int n_discard = parameter.getDiscard();
                int posAfterRemove = systemPromptPos + n_discard;

                LLamaCore.llama_memory_seq_rm(memoryI,0, systemPromptPos, posAfterRemove);
                LLamaCore.llama_memory_seq_add(memoryI,0, posAfterRemove, llamaPosOffset, -n_discard);
                llamaPosOffset = llamaPosOffset - n_discard;

                IntPointer batchTokens = llamaBatch.token();
                int nTokens = llamaBatch.n_tokens();
                fillBatch(llamaBatch, batchTokens, nTokens, 0);

            }

            // 通过Batch解算token。
            if(LLamaCore.llama_decode(context, llamaBatch) != 0) {
                exception = new ChatException("Decode failed!");
                break;
            }

            // 采样并生成新的token。
            int newToken = LLamaCore.llama_sampler_sample(sampler, context, -1);
            newTokenId.put(0, newToken);

            if (LLamaCore.llama_token_is_eog(vocab, newToken)) {
                // EOG是生成结束的意思，遇到EOG字符就应该结束本次生成了。
                break;
            }

            // 分配文本片段的内存。
            BytePointer pieceBuf = new BytePointer(Pointer.malloc(256));
            // 清空内存。
            Pointer.memset(pieceBuf, 0, pieceBuf.sizeof());

            // 将token转换为文本片段。
            int n = LLamaCore.llama_token_to_piece(
                    vocab,
                    newToken,
                    pieceBuf,
                    256,
                    0,
                    true
            );
            if (n < 0) {
                // 失败了，直接返回空字符串，并且释放资源
                exception = new ChatException("Failed to convert token to piece");
                pieceBuf.close();
                break;
            }

            // 共生成了n个字符
            pieceBuf.capacity(n);
            // 准备Buffer并处理UTF8编码问题。
            byte[] data = new byte[n];
            if(inCompleteUtf8Data != null) {
                // 有未处理完毕的UTF8，拼接在本次生成的数据的最前面。
                int newLength = n + inCompleteUtf8Data.length;
                // 重新分配内存，用于存放完整的UTF8编码的文本片段。
                data = new byte[newLength];
                System.arraycopy(inCompleteUtf8Data, 0, data, 0, inCompleteUtf8Data.length);
                // 可以读取数据了。
                pieceBuf.get(data, inCompleteUtf8Data.length, n);
            } else {
                // 直接读取数据，这就是本次生成的文本。
                pieceBuf.get(data);
            }

            // 检查是否有无效的UTF8编码，如果有的话，就将它暂存到inCompleteUtf8Data中。
            int invalidPos = getInvalidBytesPos(data);
            if (invalidPos >= 0) {
                // 有无效的UTF8编码，暂存到inCompleteUtf8Data中。
                inCompleteUtf8Data = new byte[data.length - invalidPos];
                System.arraycopy(data, invalidPos, inCompleteUtf8Data, 0, data.length - invalidPos);
            } else {
                // 没有无效的UTF8编码，直接将数据写入输出。
                inCompleteUtf8Data = null;
            }

            // 开始生成文本片段。
            String piece = null;
            if (invalidPos > 0) {
                // 有无效的UTF8编码，只生成到invalidPos位置。
                piece = new String(data, 0, invalidPos, StandardCharsets.UTF_8);
            } else if (invalidPos != 0) {
                // 没有无效的UTF8编码，生成全部文本。
                piece = new String(data, StandardCharsets.UTF_8);
            }
            // 除以上情况外，当invalidPos为0时，piece将为null，这代表全部的数据都是未完成的UTF8编码，此时piece将为null。

            if (piece != null) {
                // 有可用的文本，写入数据。
                output.append(piece);
                if (stream != null) {
                    stream.print(piece);
                }
            }

            // 一次采样只生成一个token，所以需要将Offset加1。
            llamaPosOffset = llamaPosOffset + 1;

            //释放资源，准备下一个token的生成。
            pieceBuf.close();
            resetBatch(llamaBatch);
            fillBatch(llamaBatch, newTokenId, 1,0);

        }

        // 此时最后一个token生成完毕，但是Offset的值应该比最后一个token的Offset相等，
        // 所以需要将Offset加1，以表示下一个token的位置。
        llamaPosOffset = llamaPosOffset + 1;

        tokens.close();
        promptBuf.close();
        newTokenId.close();
        LLamaCore.llama_batch_free(llamaBatch);
        if (stream != null) {
            stream.append("\n");
        }

        if (exception != null) {
            throw exception;
        }

        return output.toString();
    }

    /**
     * 创建一个新的Batch，用于解码。
     * @param tokens 一个包含TokenId的BytePointer对象。
     * @param n_tokens Token的数量。
     * @param seq_id 序列ID，用于区分不同的输入（默认为0）
     * @return 一个初始化的Batch对象。
     */
    private llama_batch createBatch(IntPointer tokens, int n_tokens, int seq_id) {

        llama_batch llamaBatch = LLamaCore.llama_batch_init(parameter.getBatchSize(),0,1);
        return fillBatch(llamaBatch, tokens, n_tokens, seq_id);

    }

    /**
     * 重置Batch，将所有TokenId设置为0。
     * @param batch 要重置的Batch对象。
     * @return 重置后的Batch对象。
     */
    private llama_batch resetBatch(llama_batch batch) {
        batch.n_tokens(0);
        return batch;
    }

    /**
     * 填充Batch对象，用于解码。
     * @param llamaBatch Batch对象
     * @param tokens 一个包含TokenId的BytePointer对象。
     * @param n_tokens Token的数量。
     * @param seq_id 序列ID，用于区分不同的输入（默认为0）
     * @return 填充后的Batch对象。
     */
    private llama_batch fillBatch(llama_batch llamaBatch, IntPointer tokens, int n_tokens, int seq_id) {
        for (int i = 0; i < n_tokens; i++) {

            // 在Batch中填入TokenId
            llamaBatch.token().put(i,tokens.get(i));
            // 在Batch中填入Token的位置
            llamaBatch.pos().put(i,llamaPosOffset + i);
            // 在Batch中填入序列ID
            llamaBatch.seq_id(i).put(0,seq_id);
            // 在Batch中填入是否为最后一个Token的标志位
            llamaBatch.logits().put(i, i == n_tokens - 1 ? (byte) 1: (byte) 0);
            // 在Batch填入该Token属于几个序列（默认为1）
            llamaBatch.n_seq_id().put(i,1);

        }
        llamaBatch.n_tokens(n_tokens);
        return llamaBatch;
    }



    /**
     * 检测字节数组中是否存在无效的UTF-8编码，
     * AI模型的输出中可能存在不完整的UTF8字节序列，
     * 遇到这样的字节序列，应该暂存它，与下一次输出的字节序列拼接在一起，
     * 本方法将会返回第一个无效的UTF8子节的位置。
     * @param bytes
     * @return
     */
    public int getInvalidBytesPos(byte[] bytes) {

        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
        decoder.onMalformedInput(CodingErrorAction.REPORT);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        try {
            decoder.decode(byteBuffer);
            return -1;
        } catch (CharacterCodingException e) {
            return byteBuffer.position();
        }
    }

    /**
     * 卸载模型并且释放资源。
     */
    public synchronized void unload() {
        if (this.sampler != null && !this.sampler.isNull()) {
            LLamaCore.llama_sampler_free(this.sampler);
            this.sampler = null;
        }
        if (this.context != null && !this.context.isNull()) {
            LLamaCore.llama_free(this.context);
            this.context = null;
        }
        if (this.model != null && !this.model.isNull()) {
            LLamaCore.llama_free_model(this.model);
            this.model = null;
        }
    }

    public ChatPrompt getPrompt() {
        return prompt;
    }

    @Override
    public synchronized void close(){
        unload();
    }

    public static void setLogMonitor(BiConsumer<Integer,String> callback) {
        ggmlLogMonitor = callback;
        if (callback != null) {
            LLamaCore.llama_log_set(logCallback, null);
        }
    }

}
