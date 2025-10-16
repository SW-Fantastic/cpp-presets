
module org.swdc.llamacpp {

    requires transitive org.bytedeco.javacpp;
    exports org.swdc.llama.core;
    exports org.swdc.llama.core.ggml;
    exports org.swdc.llama.core.ext;

}