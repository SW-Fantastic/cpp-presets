// Targeted by JavaCPP version 1.5.10: DO NOT EDIT THIS FILE

package org.swdc.pdfium.core.view;

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import static org.swdc.pdfium.core.PdfiumView.*;


// 2D Point. Coordinate system agnostic.
@Properties(inherit = org.swdc.pdfium.conf.PdfiumViewConfigure.class)
public class FS_POINTF extends Pointer {
    static { Loader.load(); }
    /** Default native constructor. */
    public FS_POINTF() { super((Pointer)null); allocate(); }
    /** Native array allocator. Access with {@link Pointer#position(long)}. */
    public FS_POINTF(long size) { super((Pointer)null); allocateArray(size); }
    /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
    public FS_POINTF(Pointer p) { super(p); }
    private native void allocate();
    private native void allocateArray(long size);
    @Override public FS_POINTF position(long position) {
        return (FS_POINTF)super.position(position);
    }
    @Override public FS_POINTF getPointer(long i) {
        return new FS_POINTF((Pointer)this).offsetAddress(i);
    }

  public native float x(); public native FS_POINTF x(float setter);
  public native float y(); public native FS_POINTF y(float setter);
}