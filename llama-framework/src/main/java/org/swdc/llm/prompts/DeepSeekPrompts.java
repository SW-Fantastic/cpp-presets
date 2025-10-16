package org.swdc.llm.prompts;

import org.swdc.llm.ChatMessage;
import org.swdc.llm.ChatPrompt;

public interface DeepSeekPrompts {

    ChatPrompt DeepSeek = (list, addAss) -> {
        StringBuilder result = new StringBuilder();
        for (ChatMessage message : list) {
            String text = message.getContent();
            PromptRole role = message.getRole();
            if(role.equals(PromptRole.SYSTEM)) {

                text = String.format("%s\n\n", text);

            } else if(role.equals(PromptRole.USER)) {

                text = String.format("### Instruction:\n%s\n", text) + (addAss ? "### Assistant:\n" : "");

            } else if (role.equals(PromptRole.ASSISTANT)) {

                text = String.format("### Response:\n%s\n<|EOT|>\n", text);

            }
            result.append(text);
        }
        return result.toString();
    };

    ChatPrompt DeepSeekV2 = (list, addAss) -> {

        StringBuilder result = new StringBuilder();
        for (ChatMessage message : list) {

            PromptRole role = message.getRole();
            String text = message.getContent();

            if (role.equals(PromptRole.SYSTEM)) {

                text = String.format("%s\n\n", text);

            } else if (role.equals(PromptRole.USER)) {

                text = String.format("User: %s\n\n", text) + (addAss ? "Assistant:" : "");

            } else if (role.equals(PromptRole.ASSISTANT)) {

                text = String.format("Assistant: %s<｜end of sentence｜>", text);

            }
            result.append(text);
        }
        return result.toString();
    };

    ChatPrompt DeepSeekV3 = (list, addAss) -> {
        StringBuilder result = new StringBuilder();
        for (ChatMessage message : list) {
            String text = message.getContent();
            PromptRole role = message.getRole();
            if(role.equals(PromptRole.SYSTEM)) {

                text = String.format("%s\n\n", text);

            } else if(role.equals(PromptRole.USER)) {

                text = String.format("<｜User｜>%s", text);

            } else if (role.equals(PromptRole.ASSISTANT)) {

                text = String.format("<｜Assistant｜>%s<｜end of sentence｜>", text);

            }
            result.append(text);
        }
        if (addAss) {
            result.append("<｜Assistant｜>");
        }
        return result.toString();
    };



}
