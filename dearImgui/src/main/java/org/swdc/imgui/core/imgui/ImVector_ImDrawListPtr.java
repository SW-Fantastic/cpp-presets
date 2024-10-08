// Targeted by JavaCPP version 1.5.10: DO NOT EDIT THIS FILE

package org.swdc.imgui.core.imgui;

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import static org.swdc.imgui.core.ImGUICore.*;


// Instantiation of ImVector<ImDrawList*>

@Properties(inherit = org.swdc.imgui.conf.ImGuiCoreConfigure.class)
public class ImVector_ImDrawListPtr extends Pointer {
    static { Loader.load(); }
    /** Default native constructor. */
    public ImVector_ImDrawListPtr() { super((Pointer)null); allocate(); }
    /** Native array allocator. Access with {@link Pointer#position(long)}. */
    public ImVector_ImDrawListPtr(long size) { super((Pointer)null); allocateArray(size); }
    /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
    public ImVector_ImDrawListPtr(Pointer p) { super(p); }
    private native void allocate();
    private native void allocateArray(long size);
    @Override public ImVector_ImDrawListPtr position(long position) {
        return (ImVector_ImDrawListPtr)super.position(position);
    }
    @Override public ImVector_ImDrawListPtr getPointer(long i) {
        return new ImVector_ImDrawListPtr((Pointer)this).offsetAddress(i);
    }

    public native int Size(); public native ImVector_ImDrawListPtr Size(int setter);
    public native int Capacity(); public native ImVector_ImDrawListPtr Capacity(int setter);
    public native ImDrawList Data(int i); public native ImVector_ImDrawListPtr Data(int i, ImDrawList setter);
    public native @Cast("ImDrawList**") PointerPointer Data(); public native ImVector_ImDrawListPtr Data(PointerPointer setter);
}
