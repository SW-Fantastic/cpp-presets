package com.live2d.sdk.cubism.sdk;

/**
 * Logical view coordinate system.
 */
public enum LogicalView {
    /**
     * Left end
     */
    LEFT(-1.0f),
    /**
     * Right end
     */
    RIGHT(1.0f),
    /**
     * Bottom end
     */
    BOTTOM(-1.0f),
    /**
     * Top end
     */
    TOP(1.0f);

    private final float value;

    LogicalView(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }
}