package org.swdc.llm.exceptions;

public class ChatException extends RuntimeException {

    public ChatException(Exception e) {
        super(e);
    }

    public ChatException(String message) {
        super(message);
    }

}
