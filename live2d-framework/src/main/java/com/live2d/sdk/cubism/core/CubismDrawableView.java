package com.live2d.sdk.cubism.core;

public class CubismDrawableView {
    private final int index;

    private final CubismDrawables drawables;

    CubismDrawableView(int index, CubismDrawables drawables) {
        assert index >= 0;
        assert drawables != null;
        this.index = index;
        this.drawables = drawables;
    }

    public int getIndex() {
        return this.index;
    }

    public String getId() {
        return this.drawables.getIds()[this.index];
    }

    public byte getConstantFlag() {
        return this.drawables.getConstantFlags()[this.index];
    }

    public byte getDynamicFlag() {
        return this.drawables.getDynamicFlags()[this.index];
    }

    public int getTextureIndex() {
        return this.drawables.getTextureIndices()[this.index];
    }

    public int getDrawOrder() {
        return this.drawables.getDrawOrders()[this.index];
    }

    public int getRenderOrder() {
        return this.drawables.getRenderOrders()[this.index];
    }

    public float getOpacity() {
        return this.drawables.getOpacities()[this.index];
    }

    public int[] getMasks() {
        return this.drawables.getMasks()[this.index];
    }

    public int getVertexCount() {
        return this.drawables.getVertexCounts()[this.index];
    }

    public float[] getVertexPositions() {
        return this.drawables.getVertexPositions()[this.index];
    }

    public float[] getVertexUvs() {
        return this.drawables.getVertexUvs()[this.index];
    }

    public short[] getIndices() {
        return this.drawables.getIndices()[this.index];
    }

    public float[] getMultiplyColors() {
        return this.drawables.getMultiplyColors()[this.index];
    }

    public float[] getScreenColors() {
        return this.drawables.getScreenColors()[this.index];
    }

    public int getParentPartIndex() {
        return this.drawables.getParentPartIndices()[this.index];
    }

    public CubismDrawables getDrawables() {
        return this.drawables;
    }
}
