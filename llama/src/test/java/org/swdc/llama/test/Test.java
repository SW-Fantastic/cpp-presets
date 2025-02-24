package org.swdc.llama.test;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Pointer;
import org.swdc.llama.core.*;
import org.swdc.llama.ext.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * 本类用于测试LLama模型的加载和基本使用。
 *
 */
public class Test {

    public static void main(String[] args) {
        // 初始化模型参数
        llama_model_params params = LLamaCore.llama_model_default_params();
        // 启用mmap内存交换
        params.use_mmap(true);
        // 设置GPU层数为0，即不使用GPU加速
        params.n_gpu_layers(0);

        //File modelFile = new File("D:\\SDK\\LLM-Models\\Deepseek\\DeepSeek-R1-Distill-Llama-8B-Q2_K.gguf");
        File modelFile = new File("D:\\SDK\\LLM-Models\\LLAMA.cpp\\ggml-model-q2_k.gguf");
        // 加载模型文件
        llama_model model = LLamaCore.llama_load_model_from_file(
                modelFile.getAbsolutePath(),
                params
        );

        // 加载词汇表
        llama_vocab vocab = LLamaCore.llama_model_get_vocab(model);

        // 初始化上下文参数
        llama_context_params llama_context_params = LLamaCore.llama_context_default_params();
        llama_context_params.n_ctx(2048);
        llama_context_params.n_batch(1024);
        llama_context_params.n_threads(4);

        // 初始化上下文
        llama_context context = LLamaCore.llama_init_from_model(model,llama_context_params);

        // 初始化LLama的采样器链
        llama_sampler_chain_params sampler_chain_params = LLamaCore.llama_sampler_chain_default_params();
        llama_sampler sampler = LLamaCore.llama_sampler_chain_init(sampler_chain_params);
        LLamaCore.llama_sampler_chain_add(sampler,LLamaCore.llama_sampler_init_min_p(0.05f,1));
        LLamaCore.llama_sampler_chain_add(sampler,LLamaCore.llama_sampler_init_temp(0.8f));
        LLamaCore.llama_sampler_chain_add(sampler,LLamaCore.llama_sampler_init_dist(LLamaCore.LLAMA_DEFAULT_SEED));

        List<ChatMessage> messages = new ArrayList<>();

        while (true) {

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            if (input.isBlank()) {
                continue;
            }

            String template = LLamaCore.llama_model_chat_template(model,(String) null);
            ChatPrompt promptTemplate = Prompts.getByModel(template);
            if (promptTemplate == null) {
                System.err.println("该模型未提供Chat Template，请根据模型的类型自行选择提示词工具。");
                continue;
            }

            //ChatPrompt promptTemplate = LLamaPrompts.LLAMA2;

            messages.add(new ChatMessage(PromptRole.USER, input));
            String prompt = promptTemplate.prompt(messages, true);

            StringBuilder result = new StringBuilder();
            generateText(context, vocab,sampler,prompt, result);

            ChatMessage message = new ChatMessage(PromptRole.ASSISTANT, result.toString());
            messages.add(message);

        }

        // 释放资源
        LLamaCore.llama_free(context);
        LLamaCore.llama_free_model(model);
    }

    public static void generateText(llama_context context, llama_vocab vocab,llama_sampler sampler, String prompt, StringBuilder output) {

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
            return;
        }

        llama_batch llamaBatch = LLamaCore.llama_batch_get_one(tokens, n_tokens);
        IntPointer newTokenId = new IntPointer(Pointer.malloc(Pointer.sizeof(IntPointer.class)));
        newTokenId.put(0);
        while (true) {
            int n_ctx = LLamaCore.llama_n_ctx(context);
            int n_ctx_used = LLamaCore.llama_get_kv_cache_used_cells(context);
            if(n_ctx_used + llamaBatch.n_tokens() > n_ctx) {
                System.err.println("Context overflow!");
                break;
            }

            if(LLamaCore.llama_decode(context, llamaBatch) != 0) {
                System.err.println("Decode failed!");
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
                System.err.println("Failed to convert token to piece");
                pieceBuf.close();
                break;
            }

            pieceBuf.capacity(n);
            String piece = pieceBuf.getString(StandardCharsets.UTF_8);
            output.append(piece);
            System.out.print(piece);

            pieceBuf.close();

            llamaBatch = LLamaCore.llama_batch_get_one(newTokenId, 1);
        }

        tokens.close();
        promptBuf.close();
        newTokenId.close();
        System.out.println();
    }

}
