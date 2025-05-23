// Targeted by JavaCPP version 1.5.10: DO NOT EDIT THIS FILE

package org.swdc.mariadb.core.com;

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import org.swdc.mariadb.core.global.*;
import static org.swdc.mariadb.core.MyGlobal.*;

import static org.swdc.mariadb.core.MyCom.*;


  /* This holds information about the result */

@Properties(inherit = org.swdc.mariadb.conf.MyComConfigure.class)
public class UDF_INIT extends Pointer {
    static { Loader.load(); }
    /** Default native constructor. */
    public UDF_INIT() { super((Pointer)null); allocate(); }
    /** Native array allocator. Access with {@link Pointer#position(long)}. */
    public UDF_INIT(long size) { super((Pointer)null); allocateArray(size); }
    /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
    public UDF_INIT(Pointer p) { super(p); }
    private native void allocate();
    private native void allocateArray(long size);
    @Override public UDF_INIT position(long position) {
        return (UDF_INIT)super.position(position);
    }
    @Override public UDF_INIT getPointer(long i) {
        return new UDF_INIT((Pointer)this).offsetAddress(i);
    }

  public native @Cast("char") byte maybe_null(); public native UDF_INIT maybe_null(byte setter);          /* 1 if function can return NULL */
  public native @Cast("unsigned int") int decimals(); public native UDF_INIT decimals(int setter);       /* for real functions */
  public native @Cast("unsigned long") long max_length(); public native UDF_INIT max_length(long setter);    /* For string functions */
  public native @Cast("char*") BytePointer ptr(); public native UDF_INIT ptr(BytePointer setter);                   /* free pointer for function data */
  public native @Cast("char") byte const_item(); public native UDF_INIT const_item(byte setter);          /* 1 if function always returns the same value */
  public native Pointer extension(); public native UDF_INIT extension(Pointer setter);
}
