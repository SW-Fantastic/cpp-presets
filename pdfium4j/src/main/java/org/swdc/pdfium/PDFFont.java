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

    /**
     * PDFFont构造函数。
     *
     * @param key        字体文件的路径或名称，作为唯一标识符
     * @param document   PDF文档对象，该字体所属的PDF文档
     * @param font       Pdfium库中的字体对象
     */
    PDFFont(String key, PDFDocument document, fpdf_font_t__ font) {
        this.font = font;
        this.document = document;
        this.key = key;
    }

    /**
     * 获取字体的名称。
     *
     * @return 返回字体的名称，如果无法获取则返回空字符串。
     */
    public String getFontName() {

        long size = PdfiumEdit.FPDFFont_GetFamilyName(font,(byte[]) null,0);
        if (size == 2) {
            return "";
        }

        byte[] data = new byte[(int)size - 16];
        BytePointer buf = new BytePointer(Pointer.malloc(size));
        PdfiumEdit.FPDFFont_GetFamilyName(
                font,
                buf,
                size
        );

        buf.get(data);
        buf.close();

        return new String(data, StandardCharsets.UTF_16LE);

    }


    /**
     * 判断字体是否被嵌入到PDF文档中。
     *
     * @return 如果字体被嵌入，则返回true；否则返回false。
     * @throws RuntimeException 如果文档对象无效，则抛出运行时异常
     */
    public boolean isEmbedded() {

        valid();
        return PdfiumEdit.FPDFFont_GetIsEmbedded(font) == 1;

    }

    /**
     * 验证字体对象是否有效。
     *
     * @throws RuntimeException 如果字体对象为空或已关闭，则抛出运行时异常，提示字体已关闭。
     */
    private void valid() {
        if (font == null || font.isNull()) {
            throw new RuntimeException("this font has closed");
        }
    }

    fpdf_font_t__ getFont() {
        return font;
    }

    public String getKey() {
        return key;
    }

    /**
     * 关闭字体对象，释放相关资源。
     *
     * 如果字体对象不为空且未关闭，则从文档对象中移除该字体，并关闭Pdfium库中的字体对象，
     * 同时将内部字体对象指针和唯一标识符置为空。
     */
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
