package org.swdc.pdfium;

import org.swdc.pdfium.core.PdfiumEdit;
import org.swdc.pdfium.core.PdfiumText;
import org.swdc.pdfium.core.view.FS_MATRIX;
import org.swdc.pdfium.core.view.fpdf_page_t__;
import org.swdc.pdfium.core.view.fpdf_pageobject_t__;
import org.swdc.pdfium.core.view.fpdf_textpage_t__;

public class PDFPageObject implements AutoCloseable {

    protected fpdf_pageobject_t__ obj;

    private PDFPage owner;

    protected PDFPageObject(fpdf_pageobject_t__ obj, PDFPage page) {
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

    void setOwner(PDFPage page) {
        this.owner = page;
    }

    public boolean setBounds(float width, float height, float posX, float posY) {
        valid();
        if (width < 0 || height < 0 || posX < 0 || posY < 0) {
            return false;
        }

        FS_MATRIX matrix = new FS_MATRIX();
        matrix.a(width);
        matrix.b(0);
        matrix.c(0);
        matrix.d(height);
        matrix.e(posX);
        matrix.f(posY);

        return PdfiumEdit.FPDFPageObj_SetMatrix(obj,matrix) == 1;
    }


    protected fpdf_textpage_t__ getTextPage() {
        valid();
        return PdfiumText.FPDFText_LoadPage(owner.getPage());
    }

    fpdf_pageobject_t__ getObj() {
        return obj;
    }

    protected void valid() {
        if (obj == null || obj.isNull()) {
            throw new RuntimeException("object has closed");
        }
    }

    protected fpdf_page_t__ getOwnerObj() {
        valid();
        return owner.getPage();
    }

    @Override
    public void close() {

        if (obj != null && !obj.isNull()) {
            PdfiumEdit.FPDFPageObj_Destroy(obj);
            obj = null;
        }

    }
}
