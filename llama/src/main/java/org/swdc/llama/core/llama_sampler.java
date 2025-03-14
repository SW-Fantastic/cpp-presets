// Targeted by JavaCPP version 1.5.10: DO NOT EDIT THIS FILE

package org.swdc.llama.core;

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import org.swdc.llama.core.ggml.*;
import static org.swdc.llama.core.ggml.GGML.*;

import static org.swdc.llama.core.LLamaCore.*;


    @Properties(inherit = org.swdc.llama.config.LLamaConfigure.class)
public class llama_sampler extends Pointer {
        static { Loader.load(); }
        /** Default native constructor. */
        public llama_sampler() { super((Pointer)null); allocate(); }
        /** Native array allocator. Access with {@link Pointer#position(long)}. */
        public llama_sampler(long size) { super((Pointer)null); allocateArray(size); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public llama_sampler(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(long size);
        @Override public llama_sampler position(long position) {
            return (llama_sampler)super.position(position);
        }
        @Override public llama_sampler getPointer(long i) {
            return new llama_sampler((Pointer)this).offsetAddress(i);
        }
    
        public native @Const llama_sampler_i iface(); public native llama_sampler iface(llama_sampler_i setter);
        public native llama_sampler_context_t ctx(); public native llama_sampler ctx(llama_sampler_context_t setter);
    }
