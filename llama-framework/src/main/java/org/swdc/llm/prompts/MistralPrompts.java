package org.swdc.llm.prompts;

import org.swdc.llm.ChatMessage;
import org.swdc.llm.ChatPrompt;

public interface MistralPrompts {

    ChatPrompt MistralV7 = (list, addAss) -> {
        StringBuilder result = new StringBuilder();
        for (ChatMessage message  : list) {
            PromptRole role = message.getRole();
            String text = message.getContent();
            if (role.equals(PromptRole.SYSTEM)) {
                text = String.format("[SYSTEM_PROMPT] %s [/SYSTEM_PROMPT]",text);
            } else if (role.equals(PromptRole.USER)) {
                text = String.format("[INST] %s [/INST]",text);
            } else if (role.equals(PromptRole.ASSISTANT)) {
                text = String.format(" %s</s>",text);
            }
            result.append(text);
        }
        return result.toString();
    };

    ChatPrompt MistralV1 = factory(true, false,false);

    ChatPrompt MistralV3Tekken = factory(false,true,false);

    ChatPrompt MistralV3 = factory(false,false,true);


    static boolean leadingSpace(String template) {
        return template.contains(" [INST]");
    }

    static boolean trailingSpace(String template) {
        return template.contains("\"[INST]\"");
    }

    static ChatPrompt factory(boolean leadingSpace, boolean trailingSpace, boolean trimAss) {
        return (list,addAss) -> {
            StringBuilder result = new StringBuilder();
            boolean insideTurn = false;
            for (ChatMessage message : list) {

                PromptRole role = message.getRole();
                String text = message.getContent();

                if (!insideTurn) {
                    result.append("[INST]");
                    if (trailingSpace) {
                        result.append(" ");
                    }
                    insideTurn = true;
                }

                if (role == PromptRole.SYSTEM) {
                    result.append(text).append("\n\n");
                } else if (role == PromptRole.USER) {
                    result.append(text);
                    if(leadingSpace) {
                        result.append(" ");
                    }
                    result.append("[/INST]");
                } else if (role == PromptRole.ASSISTANT) {
                    if (trimAss) {
                        text = text.trim();
                    }
                    if (trailingSpace) {
                        result.append(" ");
                    }
                    result.append(text).append("</s>");
                    insideTurn = false;
                }
            }
            return result.toString();
        };
    }
}
