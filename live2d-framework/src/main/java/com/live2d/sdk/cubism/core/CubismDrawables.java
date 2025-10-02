package com.live2d.sdk.cubism.core;

import org.bytedeco.javacpp.*;
import org.swdc.live2d.core.Live2dCore;

import java.nio.charset.StandardCharsets;

public class CubismDrawables {
    private static final int COLOR_UNIT = 4;

    private int count = -1;

    private String[] ids;

    private byte[] constantFlags;

    private byte[] dynamicFlags;

    private int[] textureIndices;

    private int[] drawOrders;

    private int[] renderOrders;

    private float[] opacities;

    private int[] maskCounts;

    private int[][] masks;

    private int[] vertexCounts;

    private float[][] vertexPositions;

    private float[][] vertexUvs;

    private int[] indexCounts;

    private short[][] indices;

    private float[][] multiplyColors;

    private float[][] screenColors;

    private int[] parentPartIndices;

    private Live2dCore.csmModel model;

    public CubismDrawables(int count) {
        assert count >= 0;
        this.count = count;
        this.ids = new String[count];
        this.constantFlags = new byte[count];
        this.dynamicFlags = new byte[count];
        this.textureIndices = new int[count];
        this.drawOrders = new int[count];
        this.renderOrders = new int[count];
        this.opacities = new float[count];
        this.maskCounts = new int[count];
        this.masks = new int[count][];
        int i;
        for (i = 0; i < count; i++)
            this.masks[i] = new int[0];
        this.vertexCounts = new int[count];
        this.vertexPositions = new float[count][];
        this.vertexUvs = new float[count][];
        for (i = 0; i < count; i++) {
            this.vertexPositions[i] = new float[0];
            this.vertexUvs[i] = new float[0];
        }
        this.indexCounts = new int[count];
        this.indices = new short[count][];
        for (i = 0; i < count; i++)
            this.indices[i] = new short[0];
        this.multiplyColors = new float[count][];
        this.screenColors = new float[count][];
        for (i = 0; i < count; i++) {
            this.multiplyColors[i] = new float[0];
            this.screenColors[i] = new float[0];
        }
        this.parentPartIndices = new int[count];
    }

    public CubismDrawables(Live2dCore.csmModel model) {
        this.model = model;
        reloadFromModal();
    }

    public void update() {

        int counts = getCount();

        BytePointer constantFlagsPointer = Live2dCore.csmGetDrawableConstantFlags(model);
        BytePointer dynamicFlagsPointer = Live2dCore.csmGetDrawableDynamicFlags(model);
        IntPointer textureIndPointer = Live2dCore.csmGetDrawableTextureIndices(model);
        IntPointer orderPointer = Live2dCore.csmGetDrawableDrawOrders(model);
        IntPointer renderPointer = Live2dCore.csmGetDrawableRenderOrders(model);

        FloatPointer opacityPointer = Live2dCore.csmGetDrawableOpacities(model);
        IntPointer maskCountPointer = Live2dCore.csmGetDrawableMaskCounts(model);
        PointerPointer masksPointer = Live2dCore.csmGetDrawableMasks(model);

        IntPointer vertexCountsPointer = Live2dCore.csmGetDrawableVertexCounts(model);
        PointerPointer vertexPosPointer = Live2dCore.csmGetDrawableVertexPositions(model);
        PointerPointer vertexUvPointer = Live2dCore.csmGetDrawableVertexUvs(model);

        IntPointer indexCountPointer = Live2dCore.csmGetDrawableIndexCounts(model);
        PointerPointer indicesPointer = Live2dCore.csmGetDrawableIndices(model);

        IntPointer parentPartIndicesPointer = Live2dCore.csmGetDrawableParentPartIndices(model);

        Live2dCore.csmVector4 screenColor = Live2dCore.csmGetDrawableScreenColors(model);
        Live2dCore.csmVector4 multiplyColor = Live2dCore.csmGetDrawableMultiplyColors(model);

        constantFlagsPointer.put(constantFlags,0,counts);
        dynamicFlagsPointer.put(dynamicFlags,0,counts);
        textureIndPointer.put(textureIndices,0,counts);
        orderPointer.put(drawOrders,0,counts);
        renderPointer.put(renderOrders,0,counts);
        opacityPointer.put(opacities,0,counts);
        maskCountPointer.put(maskCounts,0,counts);
        vertexCountsPointer.put(vertexCounts,0,counts);
        indexCountPointer.put(indexCounts,0,counts);
        parentPartIndicesPointer.put(parentPartIndices,0,counts);

        for (int idx = 0; idx < counts; idx ++) {

            IntPointer theMaskIdxPointer = new IntPointer(
                    masksPointer.get(idx)
            );

            theMaskIdxPointer.put(masks[idx],0,maskCounts[idx]);

            FloatPointer vertexPos = new FloatPointer(vertexPosPointer.get(idx));
            FloatPointer vertexUv = new FloatPointer(vertexUvPointer.get(idx));

            vertexPos.put(vertexPositions[idx],0,vertexCounts[idx] * 2);
            vertexUv.put(vertexUvs[idx],0,vertexCounts[idx] * 2);

            ShortPointer indices = new ShortPointer(indicesPointer.get(idx));
            indices.put(this.indices[idx],0,indexCounts[idx]);

            Live2dCore.csmVector4 currMultipleColor = multiplyColor.getPointer(idx);
            currMultipleColor.X(multiplyColors[idx][0]);
            currMultipleColor.Y(multiplyColors[idx][1]);
            currMultipleColor.Z(multiplyColors[idx][2]);
            currMultipleColor.W(multiplyColors[idx][3]);

            Live2dCore.csmVector4 currScreenColor = screenColor.getPointer(idx);
            currScreenColor.X(screenColors[idx][0]);
            currScreenColor.Y(screenColors[idx][1]);
            currScreenColor.Z(screenColors[idx][2]);
            currScreenColor.W(screenColors[idx][3]);

        }
    }


    public void reloadFromModal() {

        int counts = getCount();
        PointerPointer idsPointer = Live2dCore.csmGetDrawableIds(model);
        BytePointer constantFlagsPointer = Live2dCore.csmGetDrawableConstantFlags(model);
        BytePointer dynamicFlagsPointer = Live2dCore.csmGetDrawableDynamicFlags(model);
        IntPointer textureIndPointer = Live2dCore.csmGetDrawableTextureIndices(model);
        IntPointer orderPointer = Live2dCore.csmGetDrawableDrawOrders(model);
        IntPointer renderPointer = Live2dCore.csmGetDrawableRenderOrders(model);

        FloatPointer opacityPointer = Live2dCore.csmGetDrawableOpacities(model);
        IntPointer maskCountPointer = Live2dCore.csmGetDrawableMaskCounts(model);
        PointerPointer masksPointer = Live2dCore.csmGetDrawableMasks(model);

        IntPointer vertexCountsPointer = Live2dCore.csmGetDrawableVertexCounts(model);
        PointerPointer vertexPosPointer = Live2dCore.csmGetDrawableVertexPositions(model);
        PointerPointer vertexUvPointer = Live2dCore.csmGetDrawableVertexUvs(model);

        IntPointer indexCountPointer = Live2dCore.csmGetDrawableIndexCounts(model);
        PointerPointer indicesPointer = Live2dCore.csmGetDrawableIndices(model);

        IntPointer parentPartIndicesPointer = Live2dCore.csmGetDrawableParentPartIndices(model);

        Live2dCore.csmVector4 screenColor = Live2dCore.csmGetDrawableScreenColors(model);
        Live2dCore.csmVector4 multiplyColor = Live2dCore.csmGetDrawableMultiplyColors(model);

        constantFlagsPointer.get(constantFlags,0,counts);
        dynamicFlagsPointer.get(dynamicFlags,0,counts);
        textureIndPointer.get(textureIndices,0,counts);
        orderPointer.get(drawOrders,0,counts);
        renderPointer.get(renderOrders,0,counts);
        opacityPointer.get(opacities,0,counts);
        maskCountPointer.get(maskCounts,0,counts);
        vertexCountsPointer.get(vertexCounts,0,counts);
        indexCountPointer.get(indexCounts,0,counts);
        parentPartIndicesPointer.get(parentPartIndices,0,counts);

        for (int idx = 0; idx < counts; idx ++) {

            ids[idx] = idsPointer.getString(idx, StandardCharsets.UTF_8);

            if (masks[idx].length != maskCounts[idx]) {
                masks[idx] = new int[maskCounts[idx]];
            }

            IntPointer theMaskIdxPointer = new IntPointer(
                    masksPointer.get(idx)
            );
            theMaskIdxPointer.get(masks[idx],0,maskCounts[idx]);

            FloatPointer vertexPos = new FloatPointer(vertexPosPointer.get(idx));
            if (vertexPositions[idx].length != vertexCounts[idx]) {
                vertexPositions[idx] = new float[vertexCounts[idx] * 2];
            }

            FloatPointer vertexUv = new FloatPointer(vertexUvPointer.get(idx));
            if (vertexUvs[idx].length != vertexCounts[idx]) {
                vertexUvs[idx] = new float[vertexCounts[idx] * 2];
            }

            vertexPos.get(vertexPositions[idx],0,vertexCounts[idx] * 2);
            vertexUv.get(vertexUvs[idx],0,vertexCounts[idx] * 2);

            if (indices[idx].length != indexCounts[idx]) {
                this.indices[idx] = new short[indexCounts[idx]];
            }
            ShortPointer indices = new ShortPointer(indicesPointer.get(idx));
            indices.get(this.indices[idx],0,indexCounts[idx]);

            Live2dCore.csmVector4 currMultipleColor = multiplyColor.getPointer(idx);
            multiplyColors[idx] = new float[]{
                    currMultipleColor.X(),currMultipleColor.Y(),currMultipleColor.Z(),currMultipleColor.W()
            };

            Live2dCore.csmVector4 currScreenColor = screenColor.getPointer(idx);
            screenColors[idx]=  new float[] {
                    currScreenColor.X(), currScreenColor.Y(),currScreenColor.Z(), currScreenColor.W()
            };

        }

    }

    public int getCount() {
        if (count < 0) {
            count = Live2dCore.csmGetDrawableCount(model);
            this.ids = new String[count];
            this.constantFlags = new byte[count];
            this.dynamicFlags = new byte[count];
            this.textureIndices = new int[count];
            this.drawOrders = new int[count];
            this.renderOrders = new int[count];
            this.opacities = new float[count];
            this.maskCounts = new int[count];
            this.masks = new int[count][];
            int i;
            for (i = 0; i < count; i++) {
                this.masks[i] = new int[0];
            }
            this.vertexCounts = new int[count];
            this.vertexPositions = new float[count][];
            this.vertexUvs = new float[count][];
            for (i = 0; i < count; i++) {
                this.vertexPositions[i] = new float[0];
                this.vertexUvs[i] = new float[0];
            }
            this.indexCounts = new int[count];
            this.indices = new short[count][];
            for (i = 0; i < count; i++) {
                this.indices[i] = new short[0];
            }
            this.multiplyColors = new float[count][];
            this.screenColors = new float[count][];
            for (i = 0; i < count; i++) {
                this.multiplyColors[i] = new float[0];
                this.screenColors[i] = new float[0];
            }
            this.parentPartIndices = new int[count];
        }
        return this.count;
    }

    public String[] getIds() {
        return this.ids;
    }

    public byte[] getConstantFlags() {
        return this.constantFlags;
    }

    public byte[] getDynamicFlags() {
        return this.dynamicFlags;
    }

    public int[] getTextureIndices() {
        return this.textureIndices;
    }

    public int[] getDrawOrders() {
        return this.drawOrders;
    }

    public int[] getRenderOrders() {
        return this.renderOrders;
    }

    public float[] getOpacities() {
        return this.opacities;
    }

    public int[] getMaskCounts() {
        return this.maskCounts;
    }

    public int[][] getMasks() {
        return this.masks;
    }

    public int[] getVertexCounts() {
        return this.vertexCounts;
    }

    public float[][] getVertexPositions() {
        return this.vertexPositions;
    }

    public float[][] getVertexUvs() {
        return this.vertexUvs;
    }

    public int[] getIndexCounts() {
        return this.indexCounts;
    }

    public short[][] getIndices() {
        return this.indices;
    }

    public float[][] getMultiplyColors() {
        return this.multiplyColors;
    }

    public float[][] getScreenColors() {
        return this.screenColors;
    }

    public int[] getParentPartIndices() {
        return this.parentPartIndices;
    }
}
