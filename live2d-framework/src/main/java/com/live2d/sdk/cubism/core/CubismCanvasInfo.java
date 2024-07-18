package com.live2d.sdk.cubism.core;

public class CubismCanvasInfo {
    CubismCanvasInfo(float[] sizeInPixels, float[] originInPixels, float pixelsPerUnit) {
        assert sizeInPixels != null;
        assert originInPixels != null;
        assert sizeInPixels.length == 2;
        assert originInPixels.length == 2;
        this.sizeInPixels[0] = sizeInPixels[0];
        this.sizeInPixels[1] = sizeInPixels[1];
        this.originInPixels[0] = originInPixels[0];
        this.originInPixels[1] = originInPixels[1];
        this.pixelsPerUnit = pixelsPerUnit;
    }

    public float[] getSizeInPixels() {
        return this.sizeInPixels;
    }

    public float[] getOriginInPixels() {
        return this.originInPixels;
    }

    public float getPixelsPerUnit() {
        return this.pixelsPerUnit;
    }

    private final float[] sizeInPixels = new float[2];

    private final float[] originInPixels = new float[2];

    private float pixelsPerUnit = 0.0F;
}
