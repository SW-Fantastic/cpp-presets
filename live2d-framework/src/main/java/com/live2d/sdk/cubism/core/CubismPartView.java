package com.live2d.sdk.cubism.core;

public class CubismPartView {
    private final int index;

    private final CubismParts parts;

    CubismPartView(int index, CubismParts parts) {
        assert index >= 0;
        assert parts != null;
        this.index = index;
        this.parts = parts;
    }

    public int getIndex() {
        return this.index;
    }

    public String getId() {
        return this.parts.getIds()[this.index];
    }

    public float getOpacity() {
        return this.parts.getOpacities()[this.index];
    }

    public void setOpacity(float opacity) {
        this.parts.getOpacities()[this.index] = opacity;
    }

    public int getParentPartIndex() {
        return this.parts.getParentPartIndices()[this.index];
    }

    public CubismParts getParts() {
        return this.parts;
    }
}
