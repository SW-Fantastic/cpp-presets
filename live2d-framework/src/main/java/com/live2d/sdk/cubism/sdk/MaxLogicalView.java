package com.live2d.sdk.cubism.sdk;

/**
 * Maximum logical view coordinate system.
 */
public enum MaxLogicalView {
    /**
     * Maximum left end
     */
    LEFT(-2.0f),
    /**
     * Maximum right end
     */
    RIGHT(2.0f),
    /**
     * Maximum bottom end
     */
    BOTTOM(-2.0f),
    /**
     * Maximum top end
     */
    TOP(2.0f);

    private final float value;

    MaxLogicalView(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }
}