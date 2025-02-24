package org.swdc.llama.ext;

public class ChatMessage {

    private PromptRole role;

    private String content;

    public ChatMessage(PromptRole role, String content) {
        this.role = role;
        this.content = content;
    }

    public PromptRole getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }
}
