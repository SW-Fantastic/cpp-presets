// Targeted by JavaCPP version 1.5.10: DO NOT EDIT THIS FILE

package org.swdc.imgui.core.imgui;

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import static org.swdc.imgui.core.ImGUICore.*;
           // Callback function for ImGui::SetNextWindowSizeConstraints()
@Properties(inherit = org.swdc.imgui.conf.ImGuiCoreConfigure.class)
public class ImGuiMemAllocFunc extends FunctionPointer {
    static { Loader.load(); }
    /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
    public    ImGuiMemAllocFunc(Pointer p) { super(p); }
    protected ImGuiMemAllocFunc() { allocate(); }
    private native void allocate();
    public native Pointer call(@Cast("size_t") long sz, Pointer user_data);
}