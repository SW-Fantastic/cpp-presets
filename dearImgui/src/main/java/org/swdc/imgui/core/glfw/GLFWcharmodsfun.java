// Targeted by JavaCPP version 1.5.10: DO NOT EDIT THIS FILE

package org.swdc.imgui.core.glfw;

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import static org.swdc.imgui.core.ImGUIGLFW.*;


/** \brief The function signature for Unicode character with modifiers
 *  callbacks.
 *
 *  This is the function signature for Unicode character with modifiers callback
 *  functions.  It is called for each input character, regardless of what
 *  modifier keys are held down.
 *
 *  @param window [in] The window that received the event.
 *  @param codepoint [in] The Unicode code point of the character.
 *  @param mods [in] Bit field describing which [modifier keys](\ref mods) were
 *  held down.
 *
 *  @see \ref input_char
 *  @see glfwSetCharModsCallback
 *
 *  @since Added in version 3.1.
 *
 *  \ingroup input
 */
@Properties(inherit = org.swdc.imgui.conf.ImGuiGLFWConfigure.class)
public class GLFWcharmodsfun extends FunctionPointer {
    static { Loader.load(); }
    /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
    public    GLFWcharmodsfun(Pointer p) { super(p); }
    protected GLFWcharmodsfun() { allocate(); }
    private native void allocate();
    public native void call(GLFWwindow arg0,@Cast("unsigned int") int arg1,int arg2);
}
