package org.swdc.llm.prompts;

import org.swdc.llm.ChatMessage;
import org.swdc.llm.ChatPrompt;

public interface GLMPrompts {


    ChatPrompt ChatGLMV3 = (messages, addAss) -> {
        StringBuilder result = new StringBuilder();
        for (ChatMessage message : messages) {
            PromptRole role = message.getRole();
            String content = message.getContent();

            result.append("[gMASK]sop");

            if (role == PromptRole.USER) {
                result.append(String.format("<|user|>\n%s", content));
                if (addAss) {
                    result.append("<|assistant|>");
                }
            } else if (role == PromptRole.ASSISTANT) {
                result.append(String.format("<|assistant|>\n%s", content));
            } else if (role == PromptRole.SYSTEM) {
                result.append(String.format("<|system|>\n%s", content));
            }
        }
        return result.toString();
    };

    ChatPrompt ChatGLMV4 = (messages, addAss) -> {
        StringBuilder result = new StringBuilder();
        for (ChatMessage message : messages) {
            PromptRole role = message.getRole();
            String content = message.getContent();

            result.append("[gMASK]<sop>");

            if (role == PromptRole.USER) {
                result.append(String.format("<|user|>\n%s", content));
                if (addAss) {
                    result.append("<|assistant|>");
                }
            } else if (role == PromptRole.ASSISTANT) {
                result.append(String.format("<|assistant|>\n%s", content));
            } else if (role == PromptRole.SYSTEM) {
                result.append(String.format("<|system|>\n%s", content));
            }
        }
        return result.toString();
    };

}
