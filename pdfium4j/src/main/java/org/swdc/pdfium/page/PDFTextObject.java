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
                Pointer.malloc(data.length + 2)
        );
        Pointer.memset(pointer, 0, data.length);
        for (int i = 0; i < data.length; i ++) {
            pointer.put(i,data[i]);
        }
        // 添加结束符
        pointer.putUnsigned((short)0);
        pointer.putUnsigned((short)0);

        int rst = PdfiumEdit.FPDFText_SetText(obj,new ShortPointer(pointer));
        pointer.close();

        return rst == 1;
    }

    public boolean setTextColor(PDFColor color) {
        return setTextColor(
                color.getR(),
                color.getG(),
                color.getB(),
                color.getA()
        );
    }

    public boolean setTextColor(int r, int g, int b, int a) {

        valid();
        return PdfiumEdit.FPDFPageObj_SetFillColor(obj,r,g,b,a) == 1;

    }

    public PDFColor getTextColor() {

        int[] color = getTextColorRGBA();
        if (color == null) {
            return null;
        }
        return new PDFColor(color);

    }

    public int[] getTextColorRGBA() {

        valid();
        int[] r = new int[1];
        int[] g = new int[1];
        int[] b = new int[1];
        int[] a = new int[1];
        int rst = PdfiumEdit.FPDFPageObj_GetFillColor(obj,r,g,b,a);
        if (rst == 1) {
            return new int[] {
                    r[0],g[0],b[0],a[0]
            };
        }
        return null;
    }

}
