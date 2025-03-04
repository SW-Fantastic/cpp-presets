package org.swdc.llm.prompts;

import org.swdc.llm.ChatMessage;
import org.swdc.llm.ChatPrompt;

public interface ChatMLPrompts {


    ChatPrompt ChatML = (list, addAss) -> {
        StringBuilder result = new StringBuilder();
        for (ChatMessage message : list) {

            PromptRole role = message.getRole();
            String text = message.getContent();

            if (role.equals(PromptRole.USER)) {
                text = String.format("<|im_start|>user\n%s<|im_end|>\n",text) + (addAss ? "<|im_start|>assistant\n<|im_end|>\n" : "");
            } else if (role.equals(PromptRole.ASSISTANT)) {
                text = String.format("<|im_start|>assistant\n%s<|im_end|>\n", text);
            } else if (role.equals(PromptRole.SYSTEM)) {
                text = String.format("<|im_start|>system\n%s<|im_end|>\n", text);
            }

            result.append(text);
        }

        return result.toString();
    };

    ChatPrompt ChatML_SEP = (list, addAss) -> {
        StringBuilder result = new StringBuilder();
        for (ChatMessage message: list) {
            PromptRole role = message.getRole();
            String text = message.getContent();

            if (role.equals(PromptRole.USER)) {
                text = String.format("<|im_start|>user<|im_sep|>%s<|im_end|>", text) + (addAss ? "<|im_start|>assistant<|im_sep|>" : "");
            } else if (role.equals(PromptRole.ASSISTANT)) {
                text = String.format("<|im_start|>assistant<|im_sep|>%s<|im_end|>", text);
            } else if (role.equals(PromptRole.SYSTEM)) {
                text = String.format("<|im_start|>system<|im_sep|>%s<|im_end|>", text);
            }

            result.append(text);
        }

        return result.toString();
    };


    ChatPrompt PhiV3 = (list, addAss) -> {
        StringBuilder result = new StringBuilder();
        for (ChatMessage message: list) {
            PromptRole role = message.getRole();
            String text = message.getContent();
            if (role.equals(PromptRole.USER)) {
                text = String.format("<|user|>%s<|end|>\n", text);
                if (addAss) {
                    text += "<|assistant|>\n";
                }
            } else if (role.equals(PromptRole.ASSISTANT)) {
                text = String.format("<|assistant|>%s<|end|>\n", text);
            } else if (role.equals(PromptRole.SYSTEM)) {
                text = String.format("<|system|>%s<|end|>\n", text);
            }
            result.append(text);
        }
        return result.toString();
    };


}
