package org.swdc.pdfium.page;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.CharPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.ShortPointer;
import org.swdc.pdfium.PDFPage;
import org.swdc.pdfium.PDFPageObject;
import org.swdc.pdfium.core.PdfiumEdit;
import org.swdc.pdfium.core.view.fpdf_pageobject_t__;
import org.swdc.pdfium.core.view.fpdf_textpage_t__;

import java.nio.charset.StandardCharsets;

public class PDFTextObject extends PDFPageObject {

    public PDFTextObject(fpdf_pageobject_t__ obj, PDFPage page) {
        super(obj,page);
    }

    public String getText() {

        valid();

        fpdf_textpage_t__ textPage = getTextPage();
        long size = PdfiumEdit.FPDFTextObj_GetText(
                obj,textPage,(short[]) null,0
        );

        if (size <= 2) {
            return "";
        }

        short[] data = new short[(int) size];
        PdfiumEdit.FPDFTextObj_GetText(
                obj, textPage,data,data.length
        );

        BytePointer pointer = new BytePointer(
                Pointer.malloc(size)
        );

        Pointer.memcpy(pointer,new ShortPointer(data),size);
        byte[] textBuf = new byte[(int)size - 2];
        pointer.get(textBuf);
        pointer.close();

        return new String(textBuf, StandardCharsets.UTF_16LE);

    }

    public boolean setText(String text) {

        valid();

        byte[] data = text.getBytes(StandardCharsets.UTF_16LE);
        BytePointer pointer = new BytePointer(
                Pointer.malloc(data.length)
        );
        for (int i = 0; i < data.length; i ++) {
            pointer.put(i,data[i]);
        }

        int rst = PdfiumEdit.FPDFText_SetText(obj,new ShortPointer(pointer));
        pointer.close();

        return rst == 1;
    }

}
