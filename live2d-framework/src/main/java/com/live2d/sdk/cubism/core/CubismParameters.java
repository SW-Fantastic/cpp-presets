package com.live2d.sdk.cubism.core;

import org.bytedeco.javacpp.*;
import org.swdc.live2d.core.Live2dCore;

import java.util.Arrays;

public class CubismParameters {

    private int count = -1;

    private String[] ids;

    private ParameterType[] types;

    private float[] minimumValues;

    private float[] maximumValues;

    private float[] defaultValues;

    private int[] keyCounts;

    private float[][] keyValues;

    private boolean[] repeats;

    private float[] values;

    private Live2dCore.csmModel model;

    public enum ParameterType {
        NORMAL(0),
        BLEND_SHAPE(1);

        private final int type;

        ParameterType(int type) {
            this.type = type;
        }

        public static ParameterType toType(int parameterType) {
            for (ParameterType value : values()) {
                if (parameterType == value.getNumber())
                    return value;
            }
            throw new IllegalArgumentException(String.format("Invalid number that does not exist in the ParameterType: %d", new Object[] { Integer.valueOf(parameterType) }));
        }

        public int getNumber() {
            return this.type;
        }
    }

    public CubismParameters(int count) {
        assert count >= 0;
        this.count = count;
        this.ids = new String[count];
        this.types = new ParameterType[count];
        this.minimumValues = new float[count];
        this.maximumValues = new float[count];
        this.defaultValues = new float[count];
        this.values = new float[count];
        this.keyCounts = new int[count];
        this.keyValues = new float[count][];
        this.repeats = new boolean[count];
        for (int i = 0; i < count; i++) {
            this.keyValues[i] = new float[0];
        }

    }

    public CubismParameters(Live2dCore.csmModel model) {
        this.model = model;
        reloadFromModel();
    }

    public int getCount() {
        if (count < 0) {
            count = Live2dCore.csmGetParameterCount(model);
            this.ids = new String[count];
            this.types = new ParameterType[count];
            this.minimumValues = new float[count];
            this.maximumValues = new float[count];
            this.defaultValues = new float[count];
            this.values = new float[count];
            this.keyCounts = new int[count];
            this.keyValues = new float[count][];
            this.repeats = new boolean[count];
        }
        return this.count;
    }

    public String[] getIds() {
        return ids;
    }

    public ParameterType[] getTypes() {
        return types;
    }

    public float[] getMinimumValues() {
        return this.minimumValues;
    }

    public float[] getMaximumValues() {
        return this.maximumValues;
    }

    public float[] getDefaultValues() {
        return this.defaultValues;
    }

    public float[] getValues() {
        return this.values;
    }

    public int[] getKeyCounts() {
        return this.keyCounts;
    }

    public float[][] getKeyValues() {
        return this.keyValues;
    }

    public boolean[] getParameterRepeats() {
        return repeats;
    }

    public void getParameterRepeats(boolean[] repeats) {
        this.repeats = repeats;
    }

    public void update() {

        int count = getCount();

        FloatPointer valueParam = Live2dCore.csmGetParameterValues(model);
        FloatPointer defaultValue = Live2dCore.csmGetParameterDefaultValues(model);
        FloatPointer maximumValue = Live2dCore.csmGetParameterMaximumValues(model);
        FloatPointer minimumValue = Live2dCore.csmGetParameterMinimumValues(model);
        IntPointer typeParam = Live2dCore.csmGetParameterTypes(model);
        IntPointer keyCount = Live2dCore.csmGetParameterKeyCounts(model);
        PointerPointer kvs = Live2dCore.csmGetParameterKeyValues(model);
        IntPointer repeat = Live2dCore.csmGetParameterRepeats(model);

        valueParam.put(values,0,count);
        defaultValue.put(defaultValues,0,count);
        maximumValue.put(maximumValues,0,count);
        minimumValue.put(minimumValues,0,count);
        typeParam.put(Arrays
                .stream(types)
                .mapToInt(ParameterType::getNumber)
                .toArray(),0,count
        );
        keyCount.put(keyCounts,0,count);

        for (int idx = 0; idx < count; idx ++) {

            repeat.put(idx,repeats[idx] ? 1: 0);
            FloatPointer kv = new FloatPointer(kvs.get(idx));
            kv.put(keyValues[idx],0,keyCounts[idx]);

        }

    }

    public void reloadFromModel() {

        int count = getCount();

        PointerPointer pp = Live2dCore.csmGetParameterIds(model);
        FloatPointer valueParam = Live2dCore.csmGetParameterValues(model);
        FloatPointer defaultValue = Live2dCore.csmGetParameterDefaultValues(model);
        FloatPointer maximumValue = Live2dCore.csmGetParameterMaximumValues(model);
        FloatPointer minimumValue = Live2dCore.csmGetParameterMinimumValues(model);
        IntPointer typeParam = Live2dCore.csmGetParameterTypes(model);
        IntPointer keyCount = Live2dCore.csmGetParameterKeyCounts(model);
        PointerPointer kvs = Live2dCore.csmGetParameterKeyValues(model);
        IntPointer repeat = Live2dCore.csmGetParameterRepeats(model);

        valueParam.get(values,0,count);
        defaultValue.get(defaultValues,0,count);
        maximumValue.get(maximumValues,0,count);
        minimumValue.get(minimumValues,0,count);
        keyCount.get(keyCounts,0,count);

        for (int idx = 0; idx < count; idx ++) {

            ids[idx] = pp.getString(idx);
            types[idx] = ParameterType.toType(typeParam.get(idx));

            keyValues[idx] = new float[keyCounts[idx]];
            repeats[idx] = repeat.get(idx) > 0;

            FloatPointer kv = new FloatPointer(kvs.get(idx));
            kv.get(keyValues[idx],0,keyCounts[idx]);

        }

    }


}
