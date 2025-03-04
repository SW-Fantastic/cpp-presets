package org.swdc.llm;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Pointer;
import org.swdc.llama.core.*;
import org.swdc.llm.exceptions.ChatException;
import org.swdc.llm.prompts.PromptRole;
import org.swdc.llm.prompts.Prompts;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LLModal implements Closeable {

    private llama_model model;

    private llama_context context;

    private llama_vocab vocab;

    private llama_sampler sampler;

    private File modelFile;

    private LLMParameter parameter;

    private List<ChatMessage> messages;

    private ChatPrompt prompt;

    /**
     * LLModal类的构造函数。
     *
     * 用于创建LLModal对象，初始化其参数和模型文件。
     *
     * @param parameter 参数对象，包含模型加载所需的配置信息。
     * @param modelFile 模型文件，包含模型数据的文件对象。
     */
    public LLModal(LLMParameter parameter, File modelFile) {
        this.parameter = parameter;
        this.modelFile = modelFile;
    }

    /**
     * 加载模型
     *
     * @return 如果模型加载成功，返回true；否则返回false
     */
    public synchronized boolean load() {
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


    public String chat(String prompt) {
        return this.chat(prompt, System.out);
    }

    /**
     * 与模型进行聊天
     *
     * @param prompt 用户输入的聊天内容
     * @param stream 输出流，用于将聊天内容输出到控制台或其他输出设备
     * @return 模型生成的回复内容
     * @throws ChatException 如果加载模型失败，或者生成出现问题会抛出 ChatException 异常
     */
    public String chat(String prompt, PrintStream stream) {

        if(this.model == null || this.context == null) {
            if(!load()) {
                throw new ChatException("Failed to load model.");
            }
        }

        ChatMessage chat = new ChatMessage(PromptRole.USER, prompt);
        messages.add(chat);

        String promptText = this.prompt.prompt(List.of(chat), true);
        String result = generateText(context, vocab, sampler, promptText, stream);

        ChatMessage message = new ChatMessage(PromptRole.ASSISTANT, result);
        messages.add(message);

        return result;

    }

    private String generateText(llama_context context, llama_vocab vocab, llama_sampler sampler, String prompt, PrintStream stream) {

        StringBuilder output = new StringBuilder();
        boolean isFirst = LLamaCore.llama_get_kv_cache_used_cells(context) == 0;
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
                isFirst,
                true
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
                isFirst,
                true
        ) < 0) {
            tokens.close();
            promptBuf.close();
            return "";
        }

        llama_batch llamaBatch = LLamaCore.llama_batch_get_one(tokens, n_tokens);
        IntPointer newTokenId = new IntPointer(Pointer.malloc(Pointer.sizeof(IntPointer.class)));
        newTokenId.put(0);

        ChatException exception = null;

        while (true) {
            int n_ctx = LLamaCore.llama_n_ctx(context);
            int n_ctx_used = LLamaCore.llama_get_kv_cache_used_cells(context);
            if(n_ctx_used + llamaBatch.n_tokens() > n_ctx) {
                exception = new ChatException("Context overflow!");
                break;
            }

            if(LLamaCore.llama_decode(context, llamaBatch) != 0) {
                exception = new ChatException("Decode failed!");
                break;
            }

            int newToken = LLamaCore.llama_sampler_sample(sampler, context, -1);
            newTokenId.put(0, newToken);
            if (LLamaCore.llama_token_is_eog(vocab, newToken)) {
                break;
            }

            BytePointer pieceBuf = new BytePointer(Pointer.malloc(256));
            Pointer.memset(pieceBuf, 0, pieceBuf.sizeof());

            int n = LLamaCore.llama_token_to_piece(
                    vocab,
                    newToken,
                    pieceBuf,
                    256,
                    0,
                    true
            );
            if (n < 0) {
                exception = new ChatException("Failed to convert token to piece");
                pieceBuf.close();
                break;
            }

            pieceBuf.capacity(n);
            String piece = pieceBuf.getString(StandardCharsets.UTF_8);
            output.append(piece);
            if (stream != null) {
                stream.print(piece);
            }

            pieceBuf.close();

            llamaBatch = LLamaCore.llama_batch_get_one(newTokenId, 1);
        }

        tokens.close();
        promptBuf.close();
        newTokenId.close();
        if (stream != null) {
            stream.println();
        }

        if (exception != null) {
            throw exception;
        }

        return output.toString();
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


    @Override
    public void close() throws IOException {
        unload();
    }
}
