package com.live2d.sdk.cubism.sdk;

/**
 * [Head] tag for hit detection.
 * (Match with external definition file(json))
 */
public enum HitAreaName {

    HEAD("Head"),
    BODY("Body");

    private final String id;

    HitAreaName(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
