package org.swdc.pdfium;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.swdc.pdfium.core.PdfiumDocument;
import org.swdc.pdfium.core.PdfiumEdit;
import org.swdc.pdfium.core.PdfiumText;
import org.swdc.pdfium.core.PdfiumView;
import org.swdc.pdfium.core.view.fpdf_font_t__;
import org.swdc.pdfium.core.view.fpdf_page_t__;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

/**
 * Pdfium的Pdf页面。
 */
public class PDFPage implements Closeable {

    private PDFDocument document;

    private fpdf_page_t__ page;

    private int index;

    /**
     * 读取现有的Pdf页面
     * @param document 所属的PDF文档
     * @param index 页面序号
     */
    PDFPage(PDFDocument document, int index) {

        if (!Pdfium.isInitialized()) {
            Pdfium.doInitialize();
        }

        if (document.getDocument() == null || document.getDocument().isNull()) {
            throw new RuntimeException("failed to load pdf page caused by document has closed.");
        }

        this.document = document;
        this.index = index;
        this.page = PdfiumView.FPDF_LoadPage(document.getDocument(),index);
        if (page == null || page.isNull()) {
            throw new RuntimeException("failed to load pdf page.");
        }
    }

    /**
     * 创建新的PDF页面。
     *
     * @param document 所属的PDF文档
     * @param index 页面序号
     * @param width 宽度
     * @param height 高度
     */
    PDFPage(PDFDocument document,int index, int width, int height) {

        if (!Pdfium.isInitialized()) {
            Pdfium.doInitialize();
        }

        if (document == null || document.getDocument() == null || document.getDocument().isNull()) {
            throw new RuntimeException("failed to create pdf page caused by document has closed");
        }

        this.document = document;
        this.index = index;

        this.page = PdfiumEdit.FPDFPage_New(
                document.getDocument(),
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

    /**
     * 仅供内部使用，调整页面的index，不会实际改变页面在PDF的位置，
     *
     * @param index 新的index
     */
    void setIndexRef(int index) {
        this.index = index;
    }

    /**
     * 修改页面的序号，将会移动页面。
     * @param newIndex 新的序号
     * @return 是否成功。
     */
    public boolean setIndex(int newIndex) {
        int state = PdfiumEdit.FPDF_MovePages(
                document.getDocument(),
                new int[] { this.index },
                1,
                newIndex
        );
        if (state == 1) {
            document.movePageReference(this.index,newIndex);
            return true;
        }
        return false;
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

        long size = PdfiumDocument.FPDF_GetPageLabel(
                document.getDocument(),
                index,
                null,
                0
        );
        if (size == 2) {
            return "";
        }

        byte[] data = new byte[(int)size - 16];
        BytePointer buf = new BytePointer(Pointer.malloc(size));
        PdfiumDocument.FPDF_GetPageLabel(
                document.getDocument(),
                index,
                buf,
                size * 2
        );

        buf.get(data);
        buf.close();

        return new String(data, StandardCharsets.UTF_16LE);
    }

    /**
     * 渲染PDF为图像
     * @param scale 缩放，影响页面渲染的质量
     * @param rotate 旋转
     * @return 渲染后的图片对象。
     */
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
            document.removePage(this.index);
            page = null;
        }
    }
}
