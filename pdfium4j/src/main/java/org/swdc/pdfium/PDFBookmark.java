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

    /**
     * PDFBookmark 构造函数。
     *
     * @param document PDF文档对象，该书签所属的PDF文档
     * @param bookmark Pdfium库中的书签对象
     */
    PDFBookmark(PDFDocument document, fpdf_bookmark_t__ bookmark) {
        this.document = document;
        this.bookmark = bookmark;
    }


    /**
     * 获取书签的标题。
     *
     * @return 返回书签的标题字符串，如果无法获取标题则返回空字符串。
     * @throws RuntimeException 如果书签对象无效，则抛出运行时异常。
     */
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

    /**
     * 获取当前书签的所有子书签。
     *
     * @return 返回包含所有子书签的列表，如果当前书签没有子书签，则返回一个空列表。
     * @throws RuntimeException 如果书签对象无效，则抛出运行时异常。
     */
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

    /**
     * 获取书签指向的页面。
     *
     * 如果书签指向一个具体的页面，则返回该页面的PDFPage对象；否则返回null。
     *
     * @return 返回书签指向的PDFPage对象，如果不指向任何页面则返回null。
     * @throws RuntimeException 如果书签对象无效，则抛出运行时异常。
     */
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

    /**
     * 验证书签对象是否有效。
     *
     * 如果书签对象为空或已被关闭，则抛出运行时异常，提示书签已关闭。
     *
     * @throws RuntimeException 如果书签对象无效，则抛出此异常
     */
    private void valid() {
        if (bookmark == null || bookmark.isNull()) {
            throw new RuntimeException("bookmark has closed");
        }
    }

    /**
     * 关闭书签对象，释放相关资源。
     *
     * 如果书签对象不为空且未关闭，则关闭书签对象并释放相关资源，
     * 同时将内部书签对象指针置为空。
     */
    @Override
    public void close() {
        if (bookmark != null && !bookmark.isNull()) {
            bookmark.close();
            bookmark = null;
        }
    }
}
