// Targeted by JavaCPP version 1.5.10: DO NOT EDIT THIS FILE

package org.swdc.imgui.core.imgui;

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import static org.swdc.imgui.core.ImGUICore.*;


// Helper: Key->Value storage
// Typically you don't have to worry about this since a storage is held within each Window.
// We use it to e.g. store collapse state for a tree (Int 0/1)
// This is optimized for efficient lookup (dichotomy into a contiguous buffer) and rare insertion (typically tied to user interactions aka max once a frame)
// You can use it as custom user storage for temporary values. Declare your own storage if, for example:
// - You want to manipulate the open/close state of a particular sub-tree in your interface (tree node uses Int 0/1 to store their state).
// - You want to store custom debug data easily without adding or editing structures in your code (probably not efficient, but convenient)
// Types are NOT stored, so it is up to you to make sure your Key don't collide with different types.
@Properties(inherit = org.swdc.imgui.conf.ImGuiCoreConfigure.class)
public class ImGuiStorage extends Pointer {
    static { Loader.load(); }
    /** Default native constructor. */
    public ImGuiStorage() { super((Pointer)null); allocate(); }
    /** Native array allocator. Access with {@link Pointer#position(long)}. */
    public ImGuiStorage(long size) { super((Pointer)null); allocateArray(size); }
    /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
    public ImGuiStorage(Pointer p) { super(p); }
    private native void allocate();
    private native void allocateArray(long size);
    @Override public ImGuiStorage position(long position) {
        return (ImGuiStorage)super.position(position);
    }
    @Override public ImGuiStorage getPointer(long i) {
        return new ImGuiStorage((Pointer)this).offsetAddress(i);
    }

    // [Internal]
    public native @ByRef ImVector_ImGuiStoragePair Data(); public native ImGuiStorage Data(ImVector_ImGuiStoragePair setter);
}