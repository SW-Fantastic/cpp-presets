module llama.framework {

    requires org.swdc.llamacpp;
    requires org.bytedeco.javacpp;

    exports org.swdc.llm;
    exports org.swdc.llm.prompts;
    exports org.swdc.llm.exceptions;

}