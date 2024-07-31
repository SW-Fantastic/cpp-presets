package org.swdc.pdfium.page;

public class PathDrawMode {

    private PDFPathFillMode fillMode;

    private boolean stork;

    public PathDrawMode(PDFPathFillMode fillMode, boolean doStork) {
        this.fillMode = fillMode;
        this.stork = doStork;
    }

    public PDFPathFillMode getFillMode() {
        return fillMode;
    }

    public boolean isStork() {
        return stork;
    }

}
