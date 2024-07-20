package org.swdc.pdfium;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.swdc.pdfium.core.PdfiumDocument;
import org.swdc.pdfium.core.PdfiumEdit;
import org.swdc.pdfium.core.PdfiumView;
import org.swdc.pdfium.core.view.fpdf_document_t__;
import org.swdc.pdfium.core.view.fpdf_page_t__;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Pdfium的Pdf页面。
 */
public class PDFPage implements Closeable {

    private fpdf_document_t__ document;

    private fpdf_page_t__ page;

    private int index;

    PDFPage(fpdf_document_t__ document, int index) {

        if (!Pdfium.isInitialized()) {
            Pdfium.doInitialize();
        }

        if (document == null || document.isNull()) {
            throw new RuntimeException("failed to load pdf page caused by document has closed.");
        }

        this.document = document;
        this.index = index;
        this.page = PdfiumView.FPDF_LoadPage(document,index);
        if (page == null || page.isNull()) {
            throw new RuntimeException("failed to load pdf page.");
        }
    }

    PDFPage(fpdf_document_t__ document,int index, int width, int height) {

        if (!Pdfium.isInitialized()) {
            Pdfium.doInitialize();
        }

        if (document == null || document.isNull()) {
            throw new RuntimeException("failed to create pdf page caused by document has closed");
        }

        this.document = document;
        this.index = index;

        this.page = PdfiumEdit.FPDFPage_New(
                document,
                index,
                width,
                height
        );

        if (this.page == null || this.page.isNull()) {
            throw new RuntimeException("failed to create new page!");
        }

    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        int state = PdfiumEdit.FPDF_MovePages(
                document,
                new int[] { this.index },
                1,
                index
        );
        if (state == 1) {
            this.index = index;
        }
    }

    public double getHeight() {
        valid();
        return PdfiumView.FPDF_GetPageHeight(page);
    }

    public double getWidth() {
        valid();
        return PdfiumView.FPDF_GetPageWidth(page);
    }

    public float getHeightF() {
        valid();
        return PdfiumView.FPDF_GetPageHeightF(page);
    }

    public float getWidthF() {
        valid();
        return PdfiumView.FPDF_GetPageWidthF(page);
    }

    public PDFPageRotate getPageRotate() {
        valid();
        int val = PdfiumEdit.FPDFPage_GetRotation(page);
        return PDFPageRotate.of(val);
    }

    public void setPageRotate(PDFPageRotate rotate) {
        valid();
        if (rotate == null) {
            return;
        }
        PdfiumEdit.FPDFPage_SetRotation(page,rotate.getValue());
    }

    public String getLabel() {
        valid();

        long size = PdfiumDocument.FPDF_GetPageLabel(document,index,null,0);
        if (size == 2) {
            return "";
        }

        byte[] data = new byte[(int)size - 16];
        BytePointer buf = new BytePointer(Pointer.malloc(size));
        PdfiumDocument.FPDF_GetPageLabel(
                document,
                index,
                buf,
                size * 2
        );

        buf.get(data);
        buf.close();

        return new String(data, StandardCharsets.UTF_16LE);
    }

    public PDFBitmap renderPage(int scale, PDFPageRotate rotate) {

        valid();

        if (rotate == null) {
            rotate = PDFPageRotate.NO_ROTATE;
        }

        double width = getWidth() * scale;
        double height = getHeight() * scale;

        PDFBitmap bitmapImage = new PDFBitmap((int)width, (int)height, true);
        bitmapImage.fillRect(0, 0, (int)width, (int)height, "FFFFFFFF");

        PdfiumView.FPDF_RenderPageBitmap(
                bitmapImage.getBitmap(),
                page,
                0,
                0,
                (int)width,
                (int)height,
                rotate.getValue(),
                PdfiumView.FPDF_ANNOT
        );

        return bitmapImage;
    }

    private void valid () {
        if (page == null || page.isNull()) {
            throw new RuntimeException("page has closed.");
        }
    }

    @Override
    public void close() {
        if (page != null && !page.isNull()) {
            PdfiumView.FPDF_ClosePage(page);
            page = null;
        }
    }
}
