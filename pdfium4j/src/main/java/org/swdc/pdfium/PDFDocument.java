package org.swdc.pdfium;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.swdc.pdfium.core.PdfiumDocument;
import org.swdc.pdfium.core.PdfiumEdit;
import org.swdc.pdfium.core.PdfiumView;
import org.swdc.pdfium.core.edit.FPDF_FILEWRITE;
import org.swdc.pdfium.core.view.fpdf_bookmark_t__;
import org.swdc.pdfium.core.view.fpdf_document_t__;
import org.swdc.pdfium.core.view.fpdf_font_t__;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class PDFDocument implements Closeable {

    private fpdf_document_t__ document;

    private Map<Integer,PDFPage> loadedPages = new HashMap<>();
    private Map<String,PDFFont> loadedFonts = new HashMap<>();

    /**
     * 使用指定的文件创建PDF文档对象。
     *
     * @param file 要加载的PDF文件
     * @throws RuntimeException 如果无法加载PDF文档，则抛出运行时异常
     */
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

    /**
     * 无参构造函数，创建一个新的PDF文档对象。
     *
     * 如果Pdfium库尚未初始化，则先调用Pdfium.doInitialize()方法进行初始化。
     * 使用PdfiumEdit.FPDF_CreateNewDocument()方法创建一个新的PDF文档对象。
     * 如果创建失败（即document为null或document.isNull()返回true），则抛出运行时异常。
     *
     * @throws RuntimeException 如果创建PDF文档失败，则抛出此异常
     */
    public PDFDocument() {

        if (!Pdfium.isInitialized()) {
            Pdfium.doInitialize();
        }

        document = PdfiumEdit.FPDF_CreateNewDocument();
        if (document == null || document.isNull()) {
            throw new RuntimeException("failed to create document.");
        }
    }

    /**
     * 验证当前文档对象是否有效。
     *
     * 如果文档对象为空或者已经关闭（即document.isNull()返回true），则抛出运行时异常，提示"document is closed!"。
     *
     * @throws RuntimeException 如果文档对象无效，则抛出此异常
     */
    private void valid() {
        if (document == null || document.isNull()) {
            throw new RuntimeException("document is closed!");
        }
    }

    /**
     * 获取PDF文档的页数。
     *
     * @return 返回PDF文档的页数。
     * @throws RuntimeException 如果文档对象无效，则抛出运行时异常。
     */
    public int getPageCount()  {
        valid();
        return PdfiumView.FPDF_GetPageCount(document);
    }

    /**
     * 获取PDF文档的标题。
     *
     * @return 返回PDF文档的标题。
     */
    public String getTitle() {
        return getMetadata(PDFMetaType.Title);
    }

    /**
     * 获取PDF文档的作者信息。
     *
     * @return 返回PDF文档的作者信息。
     */
    public String getAuthor()  {
        return getMetadata(PDFMetaType.Author);
    }

    /**
     * 获取PDF文档的关键词。
     *
     * @return 返回PDF文档的关键词。
     */
    public String getKeywords() {
        return getMetadata(PDFMetaType.Keywords);
    }

    /**
     * 获取PDF文档的创建者信息。
     *
     * @return 返回PDF文档的创建者信息。
     */
    public String getCreator() {
        return getMetadata(PDFMetaType.Creator);
    }

    /**
     * 获取PDF文档的主题。
     *
     * @return 返回PDF文档的主题信息。
     */
    public String getSubject() {
        return getMetadata(PDFMetaType.Subject);
    }

    /**
     * 获取PDF文档的创建日期。
     *
     * @return 返回PDF文档的创建日期。
     */
    public String getCreationDate() {
        return getMetadata(PDFMetaType.CreationDate);
    }

    /**
     * 获取PDF文档的修改日期。
     *
     * @return 返回PDF文档的修改日期。
     */
    public String getModifyDate() {
        return getMetadata(PDFMetaType.ModDate);
    }

    /**
     * 获取PDF文档的元数据。
     *
     * @param type 元数据类型，例如标题、作者、关键词等
     * @return 返回指定类型的元数据字符串，如果不存在则返回空字符串
     * @throws RuntimeException 如果文档对象无效，则抛出运行时异常
     */
    public String getMetadata(PDFMetaType type) {
        valid();

        long size = PdfiumDocument.FPDF_GetMetaText(document,type.name(),null,0);
        if (size < 2) {
            return "";
        }

        byte[] data = new byte[(int)size - 2];
        BytePointer buf = new BytePointer(Pointer.malloc(size));
        Pointer.memset(buf, 0, data.length);
        PdfiumDocument.FPDF_GetMetaText(
                document,
                type.name(),
                buf,
                size
        );

        buf.get(data);
        buf.close();

        return new String(data,StandardCharsets.UTF_16LE);
    }

    /**
     * 获取指定索引的PDF页面对象。
     *
     * @param index 页面的索引值，从0开始
     * @return 返回指定索引的PDF页面对象，如果索引超出范围或页面未加载则返回null
     * @throws RuntimeException 如果文档对象无效，则抛出运行时异常
     */
    public PDFPage getPage(int index) {

        valid();
        if (index > getPageCount() || index < 0) {
            return null;
        } else if (loadedPages.containsKey(index)) {
            return loadedPages.get(index);
        }

        PDFPage page = new PDFPage(this,index);
        loadedPages.put(index,page);

        return page;
    }

    /**
     * 在PDF文档中创建一个新的页面。
     *
     * @param pageIndex 新页面的索引位置
     * @param width 新页面的宽度
     * @param height 新页面的高度
     * @return 返回新创建的PDF页面对象
     * @throws RuntimeException 如果文档对象无效，则抛出运行时异常
     */
    public PDFPage createPage(int pageIndex,double width, double height) {

        valid();

        PDFPage page = new PDFPage(this,pageIndex,(int)width,(int)height);
        movePageReference(pageIndex,pageIndex + 1);
        loadedPages.put(pageIndex,page);

        return page;

    }

    /**
     * 加载字体文件并返回对应的PDFFont对象。
     *
     * @param fontFile 要加载的字体文件
     * @return 返回加载的PDFFont对象，如果文件不存在或加载失败则返回null
     * @throws IOException 如果在读取字体文件时发生I/O错误，则抛出此异常
     * @throws RuntimeException 如果文档对象无效，则抛出运行时异常
     */
    public PDFFont loadFont(File fontFile) throws IOException {

        valid();

        if (loadedFonts.containsKey(fontFile.getAbsolutePath())) {
            return loadedFonts.get(fontFile.getAbsolutePath());
        }

        if(!fontFile.exists()) {
            return null;
        }
        String name = fontFile.getName().toLowerCase();
        byte[] data = Files.readAllBytes(fontFile.toPath());
        fpdf_font_t__ loaded = PdfiumEdit.FPDFText_LoadFont(
                document,
                data,
                data.length,
                name.toLowerCase().endsWith("ttf") ?
                        PdfiumEdit.FPDF_FONT_TRUETYPE :
                        PdfiumEdit.FPDF_FONT_TYPE1,
                1
        );

        if (loaded == null || loaded.isNull()) {
            return null;
        }

        PDFFont font = new PDFFont(fontFile.getAbsolutePath(),this,loaded);
        loadedFonts.put(fontFile.getAbsolutePath(),font);
        return font;

    }


    /**
     * 获取PDF文档中的所有书签。
     *
     * @return 返回包含所有书签的列表，如果文档中没有书签，则返回一个空列表。
     * @throws RuntimeException 如果文档对象无效，则抛出运行时异常
     */
    public List<PDFBookmark> getBookMark() {

        valid();

        fpdf_bookmark_t__ mark = PdfiumDocument.FPDFBookmark_GetFirstChild(document,null);
        if (mark == null || mark.isNull()) {
            return Collections.emptyList();
        }

        List<PDFBookmark> result = new ArrayList<>();
        PDFBookmark bookmark = new PDFBookmark(this,mark);
        result.add(bookmark);

        boolean hasNext = true;
        while (hasNext) {
            fpdf_bookmark_t__ item = PdfiumDocument.FPDFBookmark_GetNextSibling(document,mark);
            hasNext = (item != null && !item.isNull());
            if (hasNext) {
                bookmark = new PDFBookmark(this,item);
                result.add(bookmark);
            }
        }

        return result;
    }

    /**
     * 从PDF文档中删除指定的页面。
     *
     * @param page 要删除的页面对象
     * @throws RuntimeException 如果文档对象无效，则抛出运行时异常
     */
    public void removePage(PDFPage page) {

        valid();
        PdfiumEdit.FPDFPage_Delete(
                document,
                page.getIndex()
        );
        removePage(page.getIndex());
        page.close();

    }

    /**
     * 存储为新的PDF文件。
     *
     * 如果遇到了JVM崩溃，请确认你没有在编辑期间使用ImageIO的write，这会导致
     * 未知的冲突从而引发Java的崩溃。
     *
     * @param targetFile 目标文件
     * @return
     */
    public boolean write(File targetFile) {

        valid();
        return PdfiumEdit.FPDF_EXT_SaveAsCopy(
                document,
                targetFile.getAbsolutePath()
        );

    }

    fpdf_document_t__ getDocument() {
        return document;
    }

    void removePage(int index) {
        this.loadedPages.remove(index);
    }

    /**
     * 移动页面。
     *
     * 该方法用于在PDF文档中移动页面的引用位置。当页面的顺序发生变化时，需要更新所有页面的引用索引，以确保文档的正确性。
     *
     * @param src 源页面索引
     * @param dst 目标页面索引
     */
    void movePageReference(int src, int dst) {

        if (src == dst){
            return;
        }

        List<PDFPage> between = loadedPages.values()
                .stream()
                .filter(p -> p.getIndex() > src && p.getIndex() < dst)
                .collect(Collectors.toList());

        List<PDFPage> after = loadedPages.values()
                .stream()
                .filter(p -> p.getIndex() >= dst)
                .collect(Collectors.toList());

        for (PDFPage betweenItem : between) {
            betweenItem.setIndexRef(betweenItem.getIndex() - 1);
        }

        for (PDFPage afterItem : after) {
            afterItem.setIndexRef(afterItem.getIndex() + 1);
        }

        PDFPage srcPage = loadedPages.remove(src);
        if (srcPage != null) {
            srcPage.setIndexRef(dst);
        }

    }

    /**
     * 移除已加载的字体。
     *
     * @param font 要移除的字体对象
     */
    void removeFont(PDFFont font) {
        loadedFonts.remove(font.getKey());
    }

    /**
     * 关闭PDF文档，释放相关资源。
     *
     * 关闭所有已加载的页面和字体，并释放PDF文档对象占用的资源。
     */
    @Override
    public void close() {

        for (PDFPage page : loadedPages.values()) {
            page.close();
        }
        loadedPages.clear();

        for (PDFFont font : loadedFonts.values()) {
            font.close();
        }
        loadedFonts.clear();

        if (document != null && !document.isNull()) {
            PdfiumView.FPDF_CloseDocument(document);
            document = null;
        }

    }
}
