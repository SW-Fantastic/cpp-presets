package org.swdc.llama.test;

import org.bytedeco.javacpp.Pointer;
import org.swdc.llama.core.*;

import java.io.File;

public class Test {

    public static void main(String[] args) {
        // 初始化模型参数
        llama_model_params params = LLamaCore.llama_model_default_params();
        // 启用mmap内存交换
        params.use_mmap(true);
        // 设置GPU层数为0，即不使用GPU加速
        params.n_gpu_layers(0);

        // 加载模型文件
        llama_model model = LLamaCore.llama_load_model_from_file(
                new File("D:\\SDK\\LLM-Models\\LLAMA.cpp\\ggml-model-q2_k.gguf").getAbsolutePath(),
                params
        );

        // 初始化上下文参数
        llama_context_params llama_context_params = LLamaCore.llama_context_default_params();
        llama_context_params.n_ctx(1024);
        llama_context_params.n_batch(512);
        llama_context_params.n_threads(4);

        // 初始化上下文
        llama_context context = LLamaCore.llama_init_from_model(model,llama_context_params);

        // TODO: 执行推理任务

        // 释放资源
        LLamaCore.llama_free(context);
        LLamaCore.llama_free_model(model); 
    }

}
