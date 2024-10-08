// Targeted by JavaCPP version 1.5.10: DO NOT EDIT THIS FILE

package org.swdc.imgui.core.imgui;

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import static org.swdc.imgui.core.ImGUICore.*;


// Callback and functions types
@Properties(inherit = org.swdc.imgui.conf.ImGuiCoreConfigure.class)
public class ImGuiInputTextCallback extends FunctionPointer {
    static { Loader.load(); }
    /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
    public    ImGuiInputTextCallback(Pointer p) { super(p); }
    protected ImGuiInputTextCallback() { allocate(); }
    private native void allocate();
    public native int call(ImGuiInputTextCallbackData data);
}
