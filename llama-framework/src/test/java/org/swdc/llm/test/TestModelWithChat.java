package org.swdc.llm.test;

import org.swdc.llama.core.*;
import org.swdc.llm.*;
import org.swdc.llm.prompts.DeepSeekPrompts;
import org.swdc.llm.prompts.LLamaPrompts;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * 本类用于测试LLama模型的加载和基本使用。
 *
 */
public class TestModelWithChat {

    public static void main(String[] args) throws IOException {

        /* Embeddings embeddings = new Embeddings(
                new File("D:\\SDK\\LLM-Models\\snowflake-arctic-embed-s-q8_0.gguf")
        ); */


        //File modelFile = new File("D:\\SDK\\LLM-Models\\ChatGLM.cpp\\glm-4-9b-chat-Q2_K.gguf");
        //File modelFile = new File("D:\\SDK\\LLM-Models\\RwKV\\rwkv-6-world-7b-Q4_0.gguf")；
        //File modelFile = new File(("D:\\SDK\\LLM-Models\\Phi\\Phi-3-mini-4k-instruct-q4.gguf"));
        //File modelFile = new File("D:\\SDK\\LLM-Models\\LLAMA.cpp\\ggml-model-q2_k.gguf");

        //File modelFile = new File("D:\\SDK\\LLM-Models\\LLAMA3\\Llama3-8B-Chinese-Chat.Q4_K_S.gguf");
        //File modelFile = new File("D:\\SDK\\LLM-Models\\Deepseek\\DeepSeek-R1-Distill-Llama-8B-Q4_K_S.gguf");
        //File modelFile = new File("D:\\SDK\\LLM-Models\\Deepseek\\DeepSeek-R1-Distill-Llama-8B-Q2_K.gguf");
        File modelFile = new File("D:\\SDK\\LLM-Models\\Deepseek\\DeepSeek-R1-0528-Qwen3-8B-Q4_K_S.gguf");

        LLMParameter parameter = new LLMParameter();
        parameter.setGpuLayers(0);
        parameter.setMinP(0.05f);
        parameter.setMinKeepP(1L);
        parameter.setTemp(0.8f);
        //parameter.setPrompt(DeepSeekPrompts.DeepSeekV3);
        parameter.setSeeds(LLamaCore.LLAMA_DEFAULT_SEED);
        parameter.setMemorySwap(false);
        parameter.setContextSize(1024 * 4);

        LLModel modal = new LLModel(parameter, modelFile);
        modal.load();

        System.out.println("------------------------LLM is ready--------------------------------\r\n");

        while (true) {

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            if (input.isBlank()) {
                continue;
            }

            modal.chat(input,System.out);

        }

        // 释放资源
        modal.close();
    }


}
