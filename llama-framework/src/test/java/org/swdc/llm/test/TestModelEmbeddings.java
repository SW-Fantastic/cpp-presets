package org.swdc.llm.test;

import org.swdc.llm.LLMEmbeddingModel;
import org.swdc.llm.LLMParameter;

import java.io.File;
import java.util.Arrays;

public class TestModelEmbeddings {

    public static void main(String[] args) {
       String text =  "嗯，今天我想了解一下GitHub是什么。听说过很多人都用这个东西，但具体是什么，我还不太清楚。首先，我知道GitHub是一个平台，对吧？好像是和代码相关的，可能用于托管开源项目？开源项目的英文是open source，那是不是和开源软件有关？\n" +
                "\n" +
                "GitHub是一个广泛使用的代码托管平台，主要用于托管开源项目。它支持多种功能，包括：\n" +
                "\n" +
                "1. **代码托管**：用户可以将各类代码托管，方便开发者管理和分享项目。\n" +
                "2. **版本控制**：支持Git操作，用户可以进行分支、合并等版本控制管理。\n" +
                "3. **团队协作**：提供分支管理和Pull Request功能，支持团队协作，避免代码冲突。\n" +
                "4. **问题管理**：提供问题管理功能，方便团队在项目中讨论和解决问题。\n" +
                "5. **开源项目管理**：用户可以发布开源项目，其他人可以克隆并贡献代码，参与项目开发。\n" +
                "6. **访问权限管理**：用户可以设置访问权限，限制团队成员访问特定功能或代码。\n" +
                "\n" +
                "通过使用GitHub，开发者和团队可以更有效地管理开源项目，促进高效协作和项目进展。";


        LLMParameter parameter = new LLMParameter();
        parameter.setContextSize(1024 * 4);
        parameter.setBatchSize(1024);

        LLMEmbeddingModel embeddings = new LLMEmbeddingModel(
                parameter,
                new File("D:\\SDK\\LLM-Models\\snowflake-arctic-embed-s-q8_0.gguf")
        );
        if(!embeddings.load(null)) {
            System.err.println("加载模型失败");
            return;
        }
        float[] embedding = embeddings.embedding(text);
        System.err.println(Arrays.toString(embedding));
    }

}
