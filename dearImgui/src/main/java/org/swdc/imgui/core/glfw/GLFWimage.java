// Targeted by JavaCPP version 1.5.10: DO NOT EDIT THIS FILE

package org.swdc.imgui.core.glfw;

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import static org.swdc.imgui.core.ImGUIGLFW.*;


/** \brief Image data.
 *
 *  @see \ref cursor_custom
 *
 *  @since Added in version 2.1.
 *  \glfw3 Removed format and bytes-per-pixel members.
 */
@Properties(inherit = org.swdc.imgui.conf.ImGuiGLFWConfigure.class)
public class GLFWimage extends Pointer {
    static { Loader.load(); }
    /** Default native constructor. */
    public GLFWimage() { super((Pointer)null); allocate(); }
    /** Native array allocator. Access with {@link Pointer#position(long)}. */
    public GLFWimage(long size) { super((Pointer)null); allocateArray(size); }
    /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
    public GLFWimage(Pointer p) { super(p); }
    private native void allocate();
    private native void allocateArray(long size);
    @Override public GLFWimage position(long position) {
        return (GLFWimage)super.position(position);
    }
    @Override public GLFWimage getPointer(long i) {
        return new GLFWimage((Pointer)this).offsetAddress(i);
    }

    /** The width, in pixels, of this image.
     */
    public native int width(); public native GLFWimage width(int setter);
    /** The height, in pixels, of this image.
     */
    public native int height(); public native GLFWimage height(int setter);
    /** The pixel data of this image, arranged left-to-right, top-to-bottom.
     */
    public native @Cast("unsigned char*") BytePointer pixels(); public native GLFWimage pixels(BytePointer setter);
}
