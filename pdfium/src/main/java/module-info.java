module swdc.presets.pdfium {

    requires transitive org.bytedeco.javacpp;

    exports org.swdc.pdfium.core.view;
    exports org.swdc.pdfium.core.edit;
    exports org.swdc.pdfium.core;

    opens org.swdc.pdfium.core to org.bytedeco.javacpp;
}