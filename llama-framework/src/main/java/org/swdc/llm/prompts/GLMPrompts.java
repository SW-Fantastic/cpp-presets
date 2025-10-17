package org.swdc.llm.prompts;

import org.swdc.llm.ChatMessage;
import org.swdc.llm.ChatPrompt;

public interface GLMPrompts {


    ChatPrompt ChatGLMV3 = (messages, addAss) -> {
        StringBuilder result = new StringBuilder();
        result.append("[gMASK]sop");

        for (ChatMessage message : messages) {
            PromptRole role = message.getRole();
            String roleStr = "";
            if (role == PromptRole.USER) {
                roleStr = "user";
            } else if (role == PromptRole.ASSISTANT) {
                roleStr = "assistant";
            } else if (role == PromptRole.SYSTEM) {
                roleStr = "system";
            }
            String template = "<|" + roleStr + ">\n" + message.getContent();
            result.append(template);
        }
        if (addAss) {
            result.append("<|assistant|>");
        }
        return result.toString();
    };

    ChatPrompt ChatGLMV4 = (messages, addAss) -> {
        StringBuilder result = new StringBuilder();
        result.append("[gMASK]<sop>");

        for (ChatMessage message : messages) {
            PromptRole role = message.getRole();
            String content = message.getContent();
            if (role == PromptRole.USER) {
                result.append(String.format("<|user|>\n%s", content));
            } else if (role == PromptRole.ASSISTANT) {
                result.append(String.format("<|assistant|>\n%s", content));
            } else if (role == PromptRole.SYSTEM) {
                result.append(String.format("<|system|>\n%s", content));
            }
        }
        if (addAss) {
            result.append("<|assistant|>");
        }
        return result.toString();
    };

}
