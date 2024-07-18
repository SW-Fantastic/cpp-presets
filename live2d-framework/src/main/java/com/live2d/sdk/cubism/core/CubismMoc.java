package com.live2d.sdk.cubism.core;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.swdc.live2d.core.Live2dCore;

import java.io.Closeable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CubismMoc implements Closeable {

    private List<CubismModel> models;

    private Live2dCore.csmMoc nativeMocHandle;

    private BytePointer buffer;

    public static CubismMoc instantiate(byte[] mocBinary) throws ParseException {

        /*Pointer pointer = Live2dCore.csmAllocateAligned(mocBinary.length,Live2dCore.csmAlignofMoc);
        BytePointer buffer = new BytePointer(pointer);
        buffer.put(mocBinary);

        boolean hasConsistency = Live2dCore.csmHasMocConsistency(
                buffer,mocBinary.length
        ) > 0;

        if (!hasConsistency) {
            throw new ParseException("moc data is Invalid.", 0);
        }
        Live2dCore.csmMoc nativeMoc = Live2dCore.csmReviveMocInPlace(buffer,mocBinary.length);
        if (nativeMoc == null || nativeMoc.isNull()) {
            throw new ParseException("moc data is Invalid.", 0);
        }*/

        Live2dCore.csmMoc nativeMoc = Live2dCore.csmAllocMoc(
                mocBinary,
                mocBinary.length,
                false
        );

        CubismMoc moc = new CubismMoc();
        //moc.buffer = buffer;
        moc.nativeMocHandle = nativeMoc;
        return moc;
    }

    public void close() {
        if (this.nativeMocHandle == null || this.nativeMocHandle.isNull()) {
            return;
        }
        if (!this.models.isEmpty()) {
            throw new IllegalStateException("Instantiated models are not destroyed yet!!");
        }
        this.nativeMocHandle.close();
        if (this.buffer != null && !this.buffer.isNull()) {
            this.buffer.close();
        }
        this.nativeMocHandle = null;
        this.buffer = null;
    }

    public CubismModel instantiateModel() {
        throwIfAlreadyReleased();
        CubismModel model = CubismModel.instantiateModel(this);
        if (model == null)
            return null;
        this.models.add(model);
        return model;
    }

    public List<CubismModel> getModels() {
        return this.models;
    }

    public Live2dCore.csmMoc getNativeHandle() {
        return this.nativeMocHandle;
    }

    void deleteAssociation(CubismModel model) {
        this.models.remove(model);
    }

    private CubismMoc() {
        this.models = new ArrayList<>();
    }

    private void throwIfAlreadyReleased() {
        if (this.nativeMocHandle == null || this.nativeMocHandle.isNull())
            throw new IllegalStateException("This Model is Already Released.");
    }
}
