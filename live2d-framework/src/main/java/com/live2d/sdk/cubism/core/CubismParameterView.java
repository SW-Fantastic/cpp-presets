package com.live2d.sdk.cubism.core;

public class CubismParameterView {
    private final int index;

    private final CubismParameters parameters;

    CubismParameterView(int index, CubismParameters parameters) {
        assert index >= 0;
        assert parameters != null;
        this.index = index;
        this.parameters = parameters;
    }

    public int getIndex() {
        return this.index;
    }

    public String getId() {
        return this.parameters.getIds()[this.index];
    }

    public CubismParameters.ParameterType getType() {
        return this.parameters.getTypes()[this.index];
    }

    public float getMinimumValue() {
        return this.parameters.getMinimumValues()[this.index];
    }

    public float getMaximumValue() {
        return this.parameters.getMaximumValues()[this.index];
    }

    public float getDefaultValue() {
        return this.parameters.getDefaultValues()[this.index];
    }

    public float getValue() {
        return this.parameters.getValues()[this.index];
    }

    public void setValue(float value) {
        this.parameters.getValues()[this.index] = value;
    }

    public int getKeyCount() {
        return this.parameters.getKeyCounts()[this.index];
    }

    public float[] getKeyValue() {
        return this.parameters.getKeyValues()[this.index];
    }

    public CubismParameters getParameters() {
        return this.parameters;
    }
}
