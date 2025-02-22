package org.swdc.pdfium;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.swdc.pdfium.core.PdfiumDocument;
import org.swdc.pdfium.core.PdfiumEdit;
import org.swdc.pdfium.core.PdfiumView;
import org.swdc.pdfium.core.view.fpdf_page_t__;
import org.swdc.pdfium.core.view.fpdf_pageobject_t__;
import org.swdc.pdfium.page.PDFImageObject;
import org.swdc.pdfium.page.PDFPathObject;
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


    /**
     * 获取PDF页面的高度。
     *
     * @return 返回PDF页面的高度（以点为单位）。
     * @throws RuntimeException 如果页面对象无效，则抛出运行时异常。
     */
    public double getHeight() {
        valid();
        return PdfiumView.FPDF_GetPageHeight(page);
    }

    /**
     * 获取PDF页面的宽度。
     *
     * @return 返回PDF页面的宽度（以点为单位）。
     * @throws RuntimeException 如果页面对象无效，则抛出运行时异常。
     */
    public double getWidth() {
        valid();
        return PdfiumView.FPDF_GetPageWidth(page);
    }

    /**
     * 获取PDF页面的高度（浮点型）。
     *
     * @return 返回PDF页面的高度（以浮点型表示，单位为点）。
     * @throws RuntimeException 如果页面对象无效，则抛出运行时异常。
     */
    public float getHeightF() {
        valid();
        return PdfiumView.FPDF_GetPageHeightF(page);
    }

    /**
     * 获取PDF页面的宽度（浮点型）。
     *
     * @return 返回PDF页面的宽度（以浮点型表示，单位为点）。
     * @throws RuntimeException 如果页面对象无效，则抛出运行时异常。
     */
    public float getWidthF() {
        valid();
        return PdfiumView.FPDF_GetPageWidthF(page);
    }

    /**
     * 获取PDF页面的旋转角度。
     *
     * @return 返回PDFPageRotate枚举值，表示页面的旋转角度。
     * @throws RuntimeException 如果页面对象无效，则抛出运行时异常。
     */
    public PDFPageRotate getPageRotate() {
        valid();
        int val = PdfiumEdit.FPDFPage_GetRotation(page);
        return PDFPageRotate.of(val);
    }

    /**
     * 设置PDF页面的旋转角度。
     *
     * @param rotate PDFPageRotate枚举值，表示要设置的旋转角度。
     * @throws RuntimeException 如果页面对象无效，则抛出运行时异常。
     */
    public void setPageRotate(PDFPageRotate rotate) {
        valid();
        if (rotate == null) {
            return;
        }
        PdfiumEdit.FPDFPage_SetRotation(page,rotate.getValue());
    }

    /**
     * 获取PDF页面标签。
     *
     * @return 返回页面的标签字符串，如果页面没有标签则返回空字符串。
     * @throws RuntimeException 如果页面对象无效，则抛出运行时异常。
     */
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


    /**
     * 加载字体文件并返回对应的PDFFont对象。
     *
     * @param font 要加载的字体文件
     * @return 返回加载的PDFFont对象
     * @throws IOException 如果在读取字体文件时发生I/O错误，则抛出此异常
     */
    public PDFFont loadFont(File font) throws IOException {
        return document.loadFont(font);
    }

    /**
     * 获取当前页面上的对象数量。
     *
     * @return 返回当前页面上的对象数量。
     * @throws RuntimeException 如果页面对象无效，则抛出运行时异常。
     */
    public int getObjectCount() {
        valid();
        return PdfiumEdit.FPDFPage_CountObjects(page);
    }

    /**
     * 获取指定索引处的页面对象。
     *
     * @param index 要获取的页面对象的索引
     * @param <T> 页面对象的类型，必须是PDFPageObject的子类
     * @return 返回指定索引处的页面对象，如果索引超出范围或对象不存在则返回null
     * @throws RuntimeException 如果页面对象无效，则抛出运行时异常
     */
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
            case Image: {
                return (T)new PDFImageObject(obj, this);
            }
            case Path: {
                return (T) new PDFPathObject(obj, this);
            }
            default: {
                return (T)new PDFPageObject(obj,this);
            }
        }

    }

    /**
     * 在PDF页面上创建一个文本对象。
     *
     * @param font    要使用的字体对象
     * @param fontSize 字体大小
     * @return 返回创建的PDFTextObject对象，如果创建失败则返回null
     * @throws RuntimeException 如果页面对象无效，则抛出运行时异常
     */
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

    /**
     * 生成PDF页面的内容。
     *
     * 此方法调用Pdfium库的FPDFPage_GenerateContent函数来生成页面的内容。
     *
     * @return 如果内容生成成功，则返回true；否则返回false。
     * @throws RuntimeException 如果页面对象无效，则抛出运行时异常。
     */
    public boolean generateContent() {

        valid();
        return PdfiumEdit.FPDFPage_GenerateContent(page) == 1;

    }

    /**
     * 在PDF页面上创建一个新的图像对象。
     *
     * @return 返回创建的PDFImageObject对象，如果创建失败则返回null。
     * @throws RuntimeException 如果页面对象无效，则抛出运行时异常。
     */
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

    /**
     * 向PDF页面添加对象。
     *
     * @param object 要添加到页面的对象
     * @throws RuntimeException 如果页面对象无效，则抛出运行时异常
     */
    public void addObject(PDFPageObject object) {

        valid();
        PdfiumEdit.FPDFPage_InsertObject(page,object.getObj());
        object.setOwner(this);

    }


    /**
     * 从PDF页面中移除对象。
     *
     * @param object 要从页面中移除的对象
     * @return 如果对象成功移除，则返回true；否则返回false
     * @throws RuntimeException 如果页面对象无效，则抛出运行时异常
     */
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

    /**
     * 验证页面对象是否有效。
     *
     * 如果页面对象为空或已被关闭，则抛出运行时异常，提示页面已关闭。
     *
     * @throws RuntimeException 如果页面对象无效，则抛出此异常
     */
    private void valid () {
        if (page == null || page.isNull()) {
            throw new RuntimeException("page has closed.");
        }
    }

    /**
     * 关闭PDF页面并释放相关资源。
     *
     * 如果页面对象不为空且未关闭，则关闭该页面并从文档中移除相应的页面引用，
     * 同时将内部页面对象指针置为空。
     */
    @Override
    public void close() {
        if (page != null && !page.isNull()) {
            PdfiumView.FPDF_ClosePage(page);
            document.removePage(this.index);
            page = null;
        }
    }
}
