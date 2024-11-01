// Targeted by JavaCPP version 1.5.10: DO NOT EDIT THIS FILE

package org.swdc.mariadb.core.global;

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import static org.swdc.mariadb.core.MyGlobal.*;



@Properties(inherit = org.swdc.mariadb.conf.MyGlobalConfigure.class)
public class MEM_ROOT extends Pointer {
    static { Loader.load(); }
    /** Default native constructor. */
    public MEM_ROOT() { super((Pointer)null); allocate(); }
    /** Native array allocator. Access with {@link Pointer#position(long)}. */
    public MEM_ROOT(long size) { super((Pointer)null); allocateArray(size); }
    /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
    public MEM_ROOT(Pointer p) { super(p); }
    private native void allocate();
    private native void allocateArray(long size);
    @Override public MEM_ROOT position(long position) {
        return (MEM_ROOT)super.position(position);
    }
    @Override public MEM_ROOT getPointer(long i) {
        return new MEM_ROOT((Pointer)this).offsetAddress(i);
    }
                  /* blocks with free memory in it */                  /* blocks almost without free memory */             /* preallocated block */
  /* if block have less memory it will be put in 'used' list */
  public native @Cast("size_t") long min_malloc(); public native MEM_ROOT min_malloc(long setter);
  public native @Cast("size_t") long block_size(); public native MEM_ROOT block_size(long setter);               /* initial block size */
  public native @Cast("unsigned int") int block_num(); public native MEM_ROOT block_num(int setter);          /* allocated blocks counter */
  /* 
     first free block in queue test counter (if it exceed 
     MAX_BLOCK_USAGE_BEFORE_DROP block will be dropped in 'used' list)
  */
  public native @Cast("unsigned short") short first_block_usage(); public native MEM_ROOT first_block_usage(short setter);
  public native @Cast("unsigned short") short flags(); public native MEM_ROOT flags(short setter);

  public static class Error_handler extends FunctionPointer {
      static { Loader.load(); }
      /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
      public    Error_handler(Pointer p) { super(p); }
      protected Error_handler() { allocate(); }
      private native void allocate();
      public native void call();
  }
  public native Error_handler error_handler(); public native MEM_ROOT error_handler(Error_handler setter);

  public native @Cast("unsigned int") int psi_key(); public native MEM_ROOT psi_key(int setter);
}
