// Targeted by JavaCPP version 1.5.10: DO NOT EDIT THIS FILE

package org.swdc.imgui.core.glfw;

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import static org.swdc.imgui.core.ImGUIGLFW.*;


/** \brief The function signature for cursor enter/leave callbacks.
 *
 *  This is the function signature for cursor enter/leave callback functions.
 *
 *  @param window [in] The window that received the event.
 *  @param entered [in] {@code GLFW_TRUE} if the cursor entered the window's client
 *  area, or {@code GLFW_FALSE} if it left it.
 *
 *  @see \ref cursor_enter
 *  @see glfwSetCursorEnterCallback
 *
 *  @since Added in version 3.0.
 *
 *  \ingroup input
 */
@Properties(inherit = org.swdc.imgui.conf.ImGuiGLFWConfigure.class)
public class GLFWcursorenterfun extends FunctionPointer {
    static { Loader.load(); }
    /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
    public    GLFWcursorenterfun(Pointer p) { super(p); }
    protected GLFWcursorenterfun() { allocate(); }
    private native void allocate();
    public native void call(GLFWwindow arg0,int arg1);
}
