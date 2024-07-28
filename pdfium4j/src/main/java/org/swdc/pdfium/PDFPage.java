package org.swdc.pdfium;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.swdc.pdfium.core.PdfiumDocument;
import org.swdc.pdfium.core.PdfiumEdit;
import org.swdc.pdfium.core.PdfiumView;
import org.swdc.pdfium.core.view.fpdf_page_t__;
import org.swdc.pdfium.core.view.fpdf_pageobject_t__;
import org.swdc.pdfium.page.PDFImageObject;
import org.swdc.pdfium.page.PDFTextObject;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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

    /**
     * 获取Page的序号
     * @return Page序号（从0开始）
     */
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


    public PDFFont loadFont(File font) throws IOException {
        return document.loadFont(font);
    }

    public int getObjectCount() {
        valid();
        return PdfiumEdit.FPDFPage_CountObjects(page);
    }

    public <T extends PDFPageObject> T getObject(int index) {

        if (index < 0 || index > getObjectCount()) {
            return null;
        }

        fpdf_pageobject_t__ obj = PdfiumEdit.FPDFPage_GetObject(page,index);
        if (obj == null || obj.isNull()) {
            return null;
        }

        int type = PdfiumEdit.FPDFPageObj_GetType(obj);
        PDFPageObjectType objType = PDFPageObjectType.of(type);

        switch (objType) {
            case Text : {
                return (T) new PDFTextObject(obj,this);
            }
            default: {
                return (T)new PDFPageObject(obj,this);
            }
        }

    }

    public PDFTextObject createText(PDFFont font, float fontSize) {

        valid();
        fpdf_pageobject_t__ obj = PdfiumEdit.FPDFPageObj_CreateTextObj(
                document.getDocument(),
                font.getFont(),
                fontSize
        );
        if (obj == null || obj.isNull()) {
            return null;
        }

        return new PDFTextObject(obj,null);
    }

    public boolean generateContent() {

        valid();
        return PdfiumEdit.FPDFPage_GenerateContent(page) == 1;

    }

    public PDFImageObject createImage() {

        valid();
        fpdf_pageobject_t__ obj = PdfiumEdit.FPDFPageObj_NewImageObj(
                document.getDocument()
        );

        if (obj == null || obj.isNull()) {
            return null;
        }

        return new PDFImageObject(obj,null);
    }

    public void addObject(PDFPageObject object) {

        valid();
        PdfiumEdit.FPDFPage_InsertObject(page,object.getObj());
        object.setOwner(this);

    }


    public boolean removeObject(PDFPageObject object) {

        valid();
        boolean result = PdfiumEdit.FPDFPage_RemoveObject(
                page,
                object.getObj()
        ) == 1;

        if (result) {
            object.close();
        }

        return result;

    }


    fpdf_page_t__ getPage() {
        valid();
        return page;
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
