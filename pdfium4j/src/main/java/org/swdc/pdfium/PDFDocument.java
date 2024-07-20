package org.swdc.pdfium;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.swdc.pdfium.core.PdfiumDocument;
import org.swdc.pdfium.core.PdfiumEdit;
import org.swdc.pdfium.core.PdfiumView;
import org.swdc.pdfium.core.view.fpdf_document_t__;

import java.io.Closeable;
import java.io.File;
import java.nio.charset.StandardCharsets;

public class PDFDocument implements Closeable {

    private fpdf_document_t__ document;

    public PDFDocument(File file) {

        if (!Pdfium.isInitialized()) {
            Pdfium.doInitialize();
        }

        document = PdfiumView.FPDF_LoadDocument(
                file.getAbsolutePath(),
                null
        );
        if (document == null || document.isNull()) {
            throw new RuntimeException("failed to load document.");
        }
    }

    public PDFDocument() {
        document = PdfiumEdit.FPDF_CreateNewDocument();
        if (document == null || document.isNull()) {
            throw new RuntimeException("failed to create document.");
        }
    }

    private void valid() {
        if (document == null || document.isNull()) {
            throw new RuntimeException("document is closed!");
        }
    }

    public int getPageCount()  {
        valid();
        return PdfiumView.FPDF_GetPageCount(document);
    }

    public String getTitle() {
        return getMetadata(PDFMetaType.Title);
    }

    public String getAuthor()  {
        return getMetadata(PDFMetaType.Author);
    }

    public String getKeywords() {
        return getMetadata(PDFMetaType.Keywords);
    }

    public String getCreator() {
        return getMetadata(PDFMetaType.Creator);
    }

    public String getSubject() {
        return getMetadata(PDFMetaType.Subject);
    }

    public String getCreationDate() {
        return getMetadata(PDFMetaType.CreationDate);
    }

    public String getModifyDate() {
        return getMetadata(PDFMetaType.ModDate);
    }

    public String getMetadata(PDFMetaType type) {
        valid();

        long size = PdfiumDocument.FPDF_GetMetaText(document,type.name(),null,0);
        if (size == 2) {
            return "";
        }

        byte[] data = new byte[(int)size - 16];
        BytePointer buf = new BytePointer(Pointer.malloc(size));
        PdfiumDocument.FPDF_GetMetaText(
                document,
                type.name(),
                buf,
                size * 2
        );

        buf.get(data);
        buf.close();

        return new String(data,StandardCharsets.UTF_16LE);
    }

    public PDFPage getPage(int index) {

        valid();
        if (index > getPageCount() || index < 0) {
            return null;
        }

        return new PDFPage(this.document,index);
    }

    @Override
    public void close() {

        if (document != null && !document.isNull()) {
            PdfiumView.FPDF_CloseDocument(document);
            document = null;
        }

    }
}
