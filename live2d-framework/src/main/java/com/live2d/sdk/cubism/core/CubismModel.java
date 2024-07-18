package com.live2d.sdk.cubism.core;

import org.bytedeco.javacpp.Pointer;
import org.swdc.live2d.core.Live2dCore;

import java.io.Closeable;

public class CubismModel implements Closeable {

    private CubismParameterView[] parameterViews;

    private CubismPartView[] partViews;

    private CubismDrawableView[] drawableViews;

    private CubismCanvasInfo canvasInfo;

    private CubismParameters parameters;

    private CubismParts parts;

    private CubismDrawables drawables;

    private CubismMoc moc;

    private Live2dCore.csmModel nativeModelHandle;

    private Pointer buffer;

    public void update() {

        throwIfAlreadyReleased();
        parameters.update();
        drawables.update();
        parts.update();

        Live2dCore.csmUpdateModel(nativeModelHandle);
        Live2dCore.csmResetDrawableDynamicFlags(nativeModelHandle);

        parameters.reloadFromModel();
        drawables.reloadFromModal();
        parts.reloadFromModel();

        /*Live2DCubismCoreJNI.syncToNativeModel(this);
        Live2DCubismCoreJNI.updateModel(this.nativeModelHandle);
        Live2DCubismCoreJNI.syncFromNativeModel(this);*/
    }

    public void resetDrawableDynamicFlags() {
        throwIfAlreadyReleased();
        Live2dCore.csmResetDrawableDynamicFlags(this.nativeModelHandle);
        //Live2DCubismCoreJNI.resetDrawableDynamicFlags(this.nativeModelHandle);
    }

    public CubismCanvasInfo getCanvasInfo() {
        return this.canvasInfo;
    }

    public CubismMoc getMoc() {
        return this.moc;
    }

    public Live2dCore.csmModel getNativeHandle() {
        return this.nativeModelHandle;
    }

    public void close() {
        if (this.nativeModelHandle == null || this.nativeModelHandle.isNull()) {
            return;
        }
        if (this.buffer != null && !this.buffer.isNull()) {
            Live2dCore.csmDeallocateAligned(buffer);
        }
        this.moc.deleteAssociation(this);
    }

    public CubismParameterView[] getParameterViews() {
        return this.parameterViews;
    }

    public CubismPartView[] getPartViews() {
        return this.partViews;
    }

    public CubismDrawableView[] getDrawableViews() {
        return this.drawableViews;
    }

    public CubismParameterView findParameterView(String id) {
        if (id == null)
            return null;
        int count = this.parameters.getCount();
        for (int i = 0; i < count; i++) {
            if (this.parameters.getIds()[i].equals(id))
                return this.parameterViews[i];
        }
        return null;
    }

    public CubismPartView findPartView(String id) {
        if (id == null)
            return null;
        int count = this.parts.getCount();
        for (int i = 0; i < count; i++) {
            if (this.parts.getIds()[i].equals(id))
                return this.partViews[i];
        }
        return null;
    }

    public CubismDrawableView findDrawableView(String id) {
        if (id == null)
            return null;
        for (int i = 0; i < this.drawables.getCount(); i++) {
            if (this.drawables.getIds()[i].equals(id))
                return this.drawableViews[i];
        }
        return null;
    }

    public CubismParameters getParameters() {
        return this.parameters;
    }

    public CubismParts getParts() {
        return this.parts;
    }

    public CubismDrawables getDrawables() {
        return this.drawables;
    }

    static CubismModel instantiateModel(CubismMoc moc) {
        if (moc == null) {
            throw new IllegalArgumentException("moc is null");
        }
        if (moc.getNativeHandle() == null || moc.getNativeHandle().isNull()) {
            throw new IllegalArgumentException("moc is already released.");
        }

        Live2dCore.csmMoc nativeMoc = moc.getNativeHandle();
        /*Live2dCore.csmModel nativeModal = Live2dCore.csmAllocModel(nativeMoc);*/

        int modelSize = Live2dCore.csmGetSizeofModel(nativeMoc);
        int align = Live2dCore.csmAlignofModel;
        Pointer buffer = Live2dCore.csmAllocateAligned(modelSize,align);
        if (buffer == null || buffer.isNull()) {
            throw new IllegalStateException("Allocate memory failed!");
        }
        Live2dCore.csmModel nativeModal = Live2dCore.csmInitializeModelInPlace(nativeMoc,buffer,modelSize);
        if (nativeModal == null || nativeModal.isNull()) {
            throw new IllegalStateException("Instantiate model is failed.");
        }
        CubismModel model = new CubismModel();
        model.nativeModelHandle = nativeModal;
        model.moc = moc;
        model.buffer = buffer;
        model.initialize();
        return model;
    }

    private void initialize() {

        // 依次初始化各个对象。
        Live2dCore.csmVector2 tmpSizeInPixels = new Live2dCore.csmVector2();
        Live2dCore.csmVector2 tmpOriginInPixels = new Live2dCore.csmVector2();
        float[] tmpPixelsPerUnit = new float[1];

        Live2dCore.csmReadCanvasInfo(nativeModelHandle,tmpSizeInPixels,tmpOriginInPixels,tmpPixelsPerUnit);

        this.canvasInfo = new CubismCanvasInfo(new float[]{
                tmpSizeInPixels.X(),
                tmpOriginInPixels.Y()
        }, new float[] {
                tmpOriginInPixels.Y(),
                tmpOriginInPixels.Y()
        },tmpPixelsPerUnit[0]);

        this.parameters = new CubismParameters(this.nativeModelHandle);
        this.parts = new CubismParts(this.nativeModelHandle);
        this.drawables = new CubismDrawables(this.nativeModelHandle);

        this.parameterViews = new CubismParameterView[this.parameters.getCount()];
        int i;
        for (i = 0; i < this.parameters.getCount(); i++) {
            this.parameterViews[i] = new CubismParameterView(i, this.parameters);
        }
        this.partViews = new CubismPartView[this.parts.getCount()];
        for (i = 0; i < this.parts.getCount(); i++) {
            this.partViews[i] = new CubismPartView(i, this.parts);
        }
        this.drawableViews = new CubismDrawableView[this.drawables.getCount()];
        for (i = 0; i < this.drawables.getCount(); i++) {
            this.drawableViews[i] = new CubismDrawableView(i, this.drawables);
        }

    }

    private void throwIfAlreadyReleased() {
        if (this.nativeModelHandle == null || this.nativeModelHandle.isNull())
            throw new IllegalStateException("This Model is Already Released.");
    }
}
