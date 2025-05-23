// Targeted by JavaCPP version 1.5.10: DO NOT EDIT THIS FILE

package org.swdc.llama.core;

import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import org.swdc.llama.core.ggml.*;
import static org.swdc.llama.core.ggml.GGML.*;

import static org.swdc.llama.core.LLamaCore.*;


    @Properties(inherit = org.swdc.llama.config.LLamaConfigure.class)
public class llama_progress_callback extends FunctionPointer {
        static { Loader.load(); }
        /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
        public    llama_progress_callback(Pointer p) { super(p); }
        protected llama_progress_callback() { allocate(); }
        private native void allocate();
        public native @Cast("bool") boolean call(float progress, Pointer user_data);
    }
