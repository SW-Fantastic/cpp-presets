package com.live2d.sdk.cubism.sdk;

/**
 * Motion group
 */
public enum MotionGroup {
    /**
     * ID of the motion to be played at idling.
     */
    IDLE("Idle"),
    /**
     * ID of the motion to be played at tapping body.
     */
    TAP_BODY("TapBody");

    private final String id;

    MotionGroup(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}