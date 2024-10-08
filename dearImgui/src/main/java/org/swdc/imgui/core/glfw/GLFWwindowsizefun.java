// Targeted by JavaCPP version 1.5.10: DO NOT EDIT THIS FILE

package org.swdc.imgui.core.glfw;

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import static org.swdc.imgui.core.ImGUIGLFW.*;


/** \brief The function signature for window resize callbacks.
 *
 *  This is the function signature for window size callback functions.
 *
 *  @param window [in] The window that was resized.
 *  @param width [in] The new width, in screen coordinates, of the window.
 *  @param height [in] The new height, in screen coordinates, of the window.
 *
 *  @see \ref window_size
 *  @see glfwSetWindowSizeCallback
 *
 *  @since Added in version 1.0.
 *  \glfw3 Added window handle parameter.
 *
 *  \ingroup window
 */
@Properties(inherit = org.swdc.imgui.conf.ImGuiGLFWConfigure.class)
public class GLFWwindowsizefun extends FunctionPointer {
    static { Loader.load(); }
    /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
    public    GLFWwindowsizefun(Pointer p) { super(p); }
    protected GLFWwindowsizefun() { allocate(); }
    private native void allocate();
    public native void call(GLFWwindow arg0,int arg1,int arg2);
}
