package org.swdc.pdfium;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.swdc.pdfium.core.PdfiumEdit;
import org.swdc.pdfium.core.view.fpdf_font_t__;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;

public class PDFFont implements Closeable {

    private fpdf_font_t__ font;

    private String key;

    private PDFDocument document;

    PDFFont(String key, PDFDocument document, fpdf_font_t__ font) {
        this.font = font;
        this.document = document;
        this.key = key;
    }

    public String getFontName() {

        long size = PdfiumEdit.FPDFFont_GetFontName(font,(byte[]) null,0);
        if (size == 2) {
            return "";
        }

        byte[] data = new byte[(int)size - 16];
        BytePointer buf = new BytePointer(Pointer.malloc(size));
        PdfiumEdit.FPDFFont_GetFontName(
                font,
                buf,
                size
        );

        buf.get(data);
        buf.close();

        return new String(data, StandardCharsets.UTF_16LE);

    }


    public boolean isEmbedded() {

        valid();
        return PdfiumEdit.FPDFFont_GetIsEmbedded(font) == 1;

    }

    private void valid() {
        if (font == null || font.isNull()) {
            throw new RuntimeException("this font has closed");
        }
    }

    public String getKey() {
        return key;
    }

    @Override
    public void close() {

        if (font != null && !font.isNull()) {
            document.removeFont(this);
            PdfiumEdit.FPDFFont_Close(font);
            font = null;
            key = null;
        }

    }
}
