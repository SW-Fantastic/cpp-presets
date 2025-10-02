package com.live2d.sdk.cubism.sdk;

/**
 * Motion priority
 */
public enum Priority {
    NONE(0),
    IDLE(1),
    NORMAL(2),
    FORCE(3);

    private final int priority;

    Priority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}