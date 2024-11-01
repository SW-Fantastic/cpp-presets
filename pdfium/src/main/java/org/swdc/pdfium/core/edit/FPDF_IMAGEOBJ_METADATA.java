// Targeted by JavaCPP version 1.5.10: DO NOT EDIT THIS FILE

package org.swdc.pdfium.core.edit;

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import org.swdc.pdfium.core.view.*;
import static org.swdc.pdfium.core.PdfiumView.*;

import static org.swdc.pdfium.core.PdfiumEdit.*;


@Properties(inherit = org.swdc.pdfium.conf.PdfiumEditConfigure.class)
public class FPDF_IMAGEOBJ_METADATA extends Pointer {
    static { Loader.load(); }
    /** Default native constructor. */
    public FPDF_IMAGEOBJ_METADATA() { super((Pointer)null); allocate(); }
    /** Native array allocator. Access with {@link Pointer#position(long)}. */
    public FPDF_IMAGEOBJ_METADATA(long size) { super((Pointer)null); allocateArray(size); }
    /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
    public FPDF_IMAGEOBJ_METADATA(Pointer p) { super(p); }
    private native void allocate();
    private native void allocateArray(long size);
    @Override public FPDF_IMAGEOBJ_METADATA position(long position) {
        return (FPDF_IMAGEOBJ_METADATA)super.position(position);
    }
    @Override public FPDF_IMAGEOBJ_METADATA getPointer(long i) {
        return new FPDF_IMAGEOBJ_METADATA((Pointer)this).offsetAddress(i);
    }

  // The image width in pixels.
  public native @Cast("unsigned int") int width(); public native FPDF_IMAGEOBJ_METADATA width(int setter);
  // The image height in pixels.
  public native @Cast("unsigned int") int height(); public native FPDF_IMAGEOBJ_METADATA height(int setter);
  // The image's horizontal pixel-per-inch.
  public native float horizontal_dpi(); public native FPDF_IMAGEOBJ_METADATA horizontal_dpi(float setter);
  // The image's vertical pixel-per-inch.
  public native float vertical_dpi(); public native FPDF_IMAGEOBJ_METADATA vertical_dpi(float setter);
  // The number of bits used to represent each pixel.
  public native @Cast("unsigned int") int bits_per_pixel(); public native FPDF_IMAGEOBJ_METADATA bits_per_pixel(int setter);
  // The image's colorspace. See above for the list of FPDF_COLORSPACE_*.
  public native int colorspace(); public native FPDF_IMAGEOBJ_METADATA colorspace(int setter);
  // The image's marked content ID. Useful for pairing with associated alt-text.
  // A value of -1 indicates no ID.
  public native int marked_content_id(); public native FPDF_IMAGEOBJ_METADATA marked_content_id(int setter);
}
