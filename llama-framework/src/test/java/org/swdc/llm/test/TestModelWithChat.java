package org.swdc.llm.test;

import org.swdc.llama.core.*;
import org.swdc.llm.*;
import org.swdc.llm.prompts.DeepSeekPrompts;

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

        // 初始化模型参数
        llama_model_params params = LLamaCore.llama_model_default_params();
        // 启用mmap内存交换
        params.use_mmap(true);
        // 设置GPU层数为0，即不使用GPU加速
        params.n_gpu_layers(0);

        //File modelFile = new File("D:\\SDK\\LLM-Models\\ChatGLM.cpp\\glm-4-9b-chat-Q2_K.gguf");
        //File modelFile = new File("D:\\SDK\\LLM-Models\\RwKV\\rwkv-6-world-7b-Q4_0.gguf")；
        //File modelFile = new File(("D:\\SDK\\LLM-Models\\Phi\\Phi-3-mini-4k-instruct-q4.gguf"));
        //File modelFile = new File("D:\\SDK\\LLM-Models\\LLAMA.cpp\\ggml-model-q2_k.gguf");

        File modelFile = new File("D:\\SDK\\LLM-Models\\Deepseek\\DeepSeek-R1-Distill-Llama-8B-Q2_K.gguf");

        LLMParameter parameter = new LLMParameter();
        parameter.setGpuLayers(0);
        parameter.setMinP(0.05f);
        parameter.setMinKeepP(1L);
        parameter.setTemp(0.8f);
        parameter.setPrompt(DeepSeekPrompts.DeepSeekV3);
        parameter.setSeeds(LLamaCore.LLAMA_DEFAULT_SEED);

        LLModal modal = new LLModal(parameter, modelFile);
        modal.load();

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
