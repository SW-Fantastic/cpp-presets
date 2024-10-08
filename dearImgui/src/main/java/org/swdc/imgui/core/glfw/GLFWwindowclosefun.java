// Targeted by JavaCPP version 1.5.10: DO NOT EDIT THIS FILE

package org.swdc.imgui.core.glfw;

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import static org.swdc.imgui.core.ImGUIGLFW.*;


/** \brief The function signature for window close callbacks.
 *
 *  This is the function signature for window close callback functions.
 *
 *  @param window [in] The window that the user attempted to close.
 *
 *  @see \ref window_close
 *  @see glfwSetWindowCloseCallback
 *
 *  @since Added in version 2.5.
 *  \glfw3 Added window handle parameter.
 *
 *  \ingroup window
 */
@Properties(inherit = org.swdc.imgui.conf.ImGuiGLFWConfigure.class)
public class GLFWwindowclosefun extends FunctionPointer {
    static { Loader.load(); }
    /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
    public    GLFWwindowclosefun(Pointer p) { super(p); }
    protected GLFWwindowclosefun() { allocate(); }
    private native void allocate();
    public native void call(GLFWwindow arg0);
}
