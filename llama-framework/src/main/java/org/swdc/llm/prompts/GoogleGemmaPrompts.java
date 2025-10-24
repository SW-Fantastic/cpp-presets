package org.swdc.llm.prompts;

import org.swdc.llm.ChatMessage;
import org.swdc.llm.ChatPrompt;

public interface GoogleGemmaPrompts {

    /**
     * 参照Llamacpp的apply_template方法得到的GoogleGemma的Chat Template。
     */
    ChatPrompt GoogleGemma = (messages, addAss) -> {
        StringBuilder result = new StringBuilder();
        String systemPrompt = null;
        for (ChatMessage message : messages) {
            String role = "";
            String text = message.getContent();
            if (message.getRole() == PromptRole.SYSTEM) {
                systemPrompt = text;
                continue;
            } else if (message.getRole() == PromptRole.USER) {
                role = "user";
            } else {
                role = "model";
            }
            String template = "<start_of_turn>" + role + "\n";
            if (systemPrompt != null && message.getRole() == PromptRole.USER) {
                template = template + systemPrompt.trim() + "\n\n";
                systemPrompt = null;
            }
            template = template + text.trim() + "<end_of_turn>\n";
            result.append(template);
        }
        if (addAss) {
            result.append("<start_of_turn>model\n");
        }
        return result.toString();
    };

}
