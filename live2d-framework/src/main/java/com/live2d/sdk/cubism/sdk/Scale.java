package com.live2d.sdk.cubism.sdk;

/**
 * Scaling rate.
 */
public enum Scale {
    /**
     * Default scaling rate
     */
    DEFAULT(1.0f),
    /**
     * Maximum scaling rate
     */
    MAX(2.0f),
    /**
     * Minimum scaling rate
     */
    MIN(0.8f);

    private final float value;

    Scale(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }
}