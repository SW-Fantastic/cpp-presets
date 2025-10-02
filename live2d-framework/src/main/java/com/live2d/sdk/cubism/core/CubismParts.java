package com.live2d.sdk.cubism.core;

import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.PointerPointer;
import org.swdc.live2d.core.Live2dCore;

import java.nio.charset.StandardCharsets;

public class CubismParts {
    private int count = -1;

    private String[] ids;

    private int[] parentPartIndices;

    private float[] opacities;

    private Live2dCore.csmModel model;

    CubismParts(int count) {
        assert count >= 0;
        this.count = count;
        this.ids = new String[count];
        this.opacities = new float[count];
        this.parentPartIndices = new int[count];
    }

    CubismParts(Live2dCore.csmModel model) {
        this.model = model;
        reloadFromModel();
    }

    public void update() {

        int counts = getCount();

        FloatPointer opacityPointer = Live2dCore.csmGetPartOpacities(model);
        IntPointer parentPartIndPointer = Live2dCore.csmGetPartParentPartIndices(model);
        opacityPointer.put(opacities,0,counts);
        parentPartIndPointer.put(parentPartIndices,0,counts);

    }

    void reloadFromModel() {

        int counts = getCount();

        PointerPointer idsPointer = Live2dCore.csmGetPartIds(model);
        FloatPointer opacityPointer = Live2dCore.csmGetPartOpacities(model);
        IntPointer parentPartIndPointer = Live2dCore.csmGetPartParentPartIndices(model);

        opacityPointer.get(opacities,0,counts);
        parentPartIndPointer.get(parentPartIndices,0,counts);

        for (int idx = 0; idx < counts; idx ++) {

            ids[idx] = idsPointer.getString(idx, StandardCharsets.UTF_8);

        }

    }

    public int getCount() {
        if (count < 0) {
            count = Live2dCore.csmGetPartCount(model);
            this.ids = new String[count];
            this.opacities = new float[count];
            this.parentPartIndices = new int[count];
        }
        return this.count;
    }

    public String[] getIds() {
        return this.ids;
    }

    public float[] getOpacities() {
        return this.opacities;
    }

    public int[] getParentPartIndices() {
        return this.parentPartIndices;
    }
}
