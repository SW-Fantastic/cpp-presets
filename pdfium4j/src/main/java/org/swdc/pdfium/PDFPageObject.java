package org.swdc.pdfium;

import org.swdc.pdfium.core.PdfiumEdit;
import org.swdc.pdfium.core.view.fpdf_pageobject_t__;

public class PDFPageObject implements AutoCloseable {

    private fpdf_pageobject_t__ obj;

    private PDFPage owner;

    PDFPageObject(fpdf_pageobject_t__ obj, PDFPage page) {
        this.obj = obj;
        this.owner = page;
    }

    public PDFPageObjectType getType() {

        return PDFPageObjectType.of(
                PdfiumEdit.FPDFPageObj_GetType(obj)
        );
    }

    public PDFPage getOwner() {
        return owner;
    }

    private void valid() {
        if (obj == null || obj.isNull()) {
            throw new RuntimeException("object has closed");
        }
    }

    @Override
    public void close() {

        if (obj != null && !obj.isNull()) {
            PdfiumEdit.FPDFPageObj_Destroy(obj);
            obj = null;
        }

    }
}
