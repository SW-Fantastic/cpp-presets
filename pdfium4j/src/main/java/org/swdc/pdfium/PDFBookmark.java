package org.swdc.pdfium;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.swdc.pdfium.core.PdfiumDocument;
import org.swdc.pdfium.core.view.fpdf_bookmark_t__;
import org.swdc.pdfium.core.view.fpdf_dest_t__;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PDFBookmark implements AutoCloseable {

    private PDFDocument document;

    private fpdf_bookmark_t__ bookmark;

    PDFBookmark(PDFDocument document, fpdf_bookmark_t__ bookmark) {
        this.document = document;
        this.bookmark = bookmark;
    }


    public String getTitle() {

        valid();
        long length = PdfiumDocument.FPDFBookmark_GetTitle(bookmark,null,0);
        if (length == 2) {
            return "";
        }

        byte[] data = new byte[(int)length - 16];
        BytePointer buf = new BytePointer(Pointer.malloc(length));
        PdfiumDocument.FPDFBookmark_GetTitle(bookmark,buf,length);
        buf.get(data);
        buf.close();

        return new String(data, StandardCharsets.UTF_16LE);
    }

    public List<PDFBookmark> getChildren() {

        valid();

        List<PDFBookmark> result = new ArrayList<>();
        fpdf_bookmark_t__ mark = PdfiumDocument.FPDFBookmark_GetFirstChild(
                document.getDocument(),
                bookmark
        );
        if (mark == null || mark.isNull()) {
            return Collections.emptyList();
        }

        PDFBookmark next = new PDFBookmark(document,mark);
        result.add(next);

        boolean hasNext = true;
        while (hasNext) {

            mark = PdfiumDocument.FPDFBookmark_GetNextSibling(document.getDocument(),mark);
            hasNext = mark != null && !mark.isNull();
            if (hasNext) {
                next = new PDFBookmark(document,mark);
                result.add(next);
            }

        }

        return result;

    }

    public PDFPage getPage() {

        valid();
        fpdf_dest_t__ dest = PdfiumDocument.FPDFBookmark_GetDest(
                document.getDocument(),bookmark
        );

        if (dest == null || dest.isNull()) {
            return null;
        }

        int pageIndex = PdfiumDocument.FPDFDest_GetDestPageIndex(
                document.getDocument(),dest
        );

        if (pageIndex >= 0) {
            return document.getPage(pageIndex);
        }

        return null;
    }

    private void valid() {
        if (bookmark == null || bookmark.isNull()) {
            throw new RuntimeException("bookmark has closed");
        }
    }

    @Override
    public void close() {
        if (bookmark != null && !bookmark.isNull()) {
            bookmark.close();
            bookmark = null;
        }
    }
}
