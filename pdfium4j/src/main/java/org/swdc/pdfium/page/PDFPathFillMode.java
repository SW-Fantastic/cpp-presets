package org.swdc.pdfium.page;

import org.swdc.pdfium.core.PdfiumEdit;

public enum PDFPathFillMode {


    None(PdfiumEdit.FPDF_FILLMODE_NONE),
    Winding(PdfiumEdit.FPDF_FILLMODE_WINDING),
    Alternate(PdfiumEdit.FPDF_FILLMODE_ALTERNATE);

    private int mode;
    PDFPathFillMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

    public static PDFPathFillMode of(int mode) {
        for (PDFPathFillMode item : values()) {
            if (item.getMode() == mode) {
                return item;
            }
        }
        return null;
    }
}
