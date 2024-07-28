module swdc.pdfium4j {

    requires transitive swdc.presets.pdfium;
    requires java.desktop;
    exports org.swdc.pdfium.page;
    exports org.swdc.pdfium;

}