// Targeted by JavaCPP version 1.5.10: DO NOT EDIT THIS FILE

package org.swdc.imgui.core.imgui;

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import static org.swdc.imgui.core.ImGUICore.*;


// Sorting specifications for a table (often handling sort specs for a single column, occasionally more)
// Obtained by calling TableGetSortSpecs().
// When 'SpecsDirty == true' you can sort your data. It will be true with sorting specs have changed since last call, or the first time.
// Make sure to set 'SpecsDirty = false' after sorting, else you may wastefully sort your data every frame!
@Properties(inherit = org.swdc.imgui.conf.ImGuiCoreConfigure.class)
public class ImGuiTableSortSpecs extends Pointer {
    static { Loader.load(); }
    /** Default native constructor. */
    public ImGuiTableSortSpecs() { super((Pointer)null); allocate(); }
    /** Native array allocator. Access with {@link Pointer#position(long)}. */
    public ImGuiTableSortSpecs(long size) { super((Pointer)null); allocateArray(size); }
    /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
    public ImGuiTableSortSpecs(Pointer p) { super(p); }
    private native void allocate();
    private native void allocateArray(long size);
    @Override public ImGuiTableSortSpecs position(long position) {
        return (ImGuiTableSortSpecs)super.position(position);
    }
    @Override public ImGuiTableSortSpecs getPointer(long i) {
        return new ImGuiTableSortSpecs((Pointer)this).offsetAddress(i);
    }

    public native @Const ImGuiTableColumnSortSpecs Specs(); public native ImGuiTableSortSpecs Specs(ImGuiTableColumnSortSpecs setter);       // Pointer to sort spec array.
    public native int SpecsCount(); public native ImGuiTableSortSpecs SpecsCount(int setter);  // Sort spec count. Most often 1. May be > 1 when ImGuiTableFlags_SortMulti is enabled. May be == 0 when ImGuiTableFlags_SortTristate is enabled.
    public native @Cast("bool") boolean SpecsDirty(); public native ImGuiTableSortSpecs SpecsDirty(boolean setter);  // Set to true when specs have changed since last time! Use this to sort again, then clear the flag.
}