package org.swdc.llama.ext;

public interface LLamaPrompts {

    ChatPrompt LLAMA2 = factory(false, false, false);

    ChatPrompt LLAMA2_SYS_STRIP = factory(false, true, true);

    ChatPrompt LLAMA2_SYS_BOS = factory(true, true, false);

    ChatPrompt LLAMA2_SYS = factory(false, true, false);

    ChatPrompt LLAMA3 = (messages, addAss) -> {
        StringBuilder result = new StringBuilder();
        for (ChatMessage message : messages) {

            PromptRole role = message.getRole();
            String content = message.getContent();

            if (role.equals(PromptRole.SYSTEM)) {
                result.append(
                        String.format("<|start_header_id|>system<|end_header_id|>\n\n%s<|eot_id|>", content.trim())
                );
            } else if (role.equals(PromptRole.USER)) {
                result.append(
                        String.format("<|start_header_id|>user<|end_header_id|>\n\n%s<|eot_id|>", content.trim())
                );
                if (addAss) {
                    result.append("<|start_header_id|>assistant<|end_header_id|>\n\n");
                }
            } else if (role.equals(PromptRole.ASSISTANT)) {
                result.append(
                        String.format("<|start_header_id|>assistant<|end_header_id|>\n\n%s<|eot_id|>", content.trim())
                );
            }
        }
        return result.toString();
    };

    static boolean supportSystemMessage(String template) {
        return template.contains("<<SYS>>");
    }

    static boolean bosInsideHistory(String template) {
        return template.contains("bos_token + '[INST]");
    }

    static boolean needStripeMessage(String template) {
        return template.contains("content.strip()");
    }

    static ChatPrompt factory(boolean addBos, boolean supportSys, boolean needStripe) {
        return (list, addAss) -> {
            StringBuilder result = new StringBuilder("[INST]");
            boolean insideTurn = true;
            for (ChatMessage message : list) {

                PromptRole role = message.getRole();
                String content = message.getContent();

                if (!insideTurn) {
                    insideTurn = true;
                    if (addBos) {
                        result.append("<s>[INST] ");
                    } else {
                        result.append("[INST] ");
                    }
                }

                if (role.equals(PromptRole.SYSTEM)) {
                    if (supportSys) {
                        result.append(
                                String.format("<<SYS>>\n%s\n<</SYS>>\n\n", content)
                        );
                    } else {
                        result.append(content).append("\n");
                    }
                } else if (role.equals(PromptRole.USER)) {
                    result.append(content).append("[/INST]");
                } else {
                    result.append(content).append("</s>");
                    insideTurn = false;
                }

            }
            return result.toString();
        };
    }

}
