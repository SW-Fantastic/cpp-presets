package org.swdc.pdfium;

import org.swdc.pdfium.core.PdfiumEdit;

public enum PDFPageObjectType {

    Unknown(PdfiumEdit.FPDF_PAGEOBJ_UNKNOWN),
    Text(PdfiumEdit.FPDF_PAGEOBJ_TEXT),
    Image(PdfiumEdit.FPDF_PAGEOBJ_IMAGE),
    Path(PdfiumEdit.FPDF_PAGEOBJ_PATH),
    Form(PdfiumEdit.FPDF_PAGEOBJ_FORM)

    ;

    private int type;

    PDFPageObjectType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }


    public static PDFPageObjectType of(int type) {
        for (PDFPageObjectType item: values()) {
            if (item.getType() == type) {
                return item;
            }
        }
        return Unknown;
    }


}
