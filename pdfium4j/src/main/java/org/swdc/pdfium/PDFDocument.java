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

    public PDFDocument() {

        if (!Pdfium.isInitialized()) {
            Pdfium.doInitialize();
        }

        document = PdfiumEdit.FPDF_CreateNewDocument();
        if (document == null || document.isNull()) {
            throw new RuntimeException("failed to create document.");
        }
    }

    private void valid() {
        if (document == null || document.isNull()) {
            throw new RuntimeException("document is closed!");
        }
    }

    public int getPageCount()  {
        valid();
        return PdfiumView.FPDF_GetPageCount(document);
    }

    public String getTitle() {
        return getMetadata(PDFMetaType.Title);
    }

    public String getAuthor()  {
        return getMetadata(PDFMetaType.Author);
    }

    public String getKeywords() {
        return getMetadata(PDFMetaType.Keywords);
    }

    public String getCreator() {
        return getMetadata(PDFMetaType.Creator);
    }

    public String getSubject() {
        return getMetadata(PDFMetaType.Subject);
    }

    public String getCreationDate() {
        return getMetadata(PDFMetaType.CreationDate);
    }

    public String getModifyDate() {
        return getMetadata(PDFMetaType.ModDate);
    }

    public String getMetadata(PDFMetaType type) {
        valid();

        long size = PdfiumDocument.FPDF_GetMetaText(document,type.name(),null,0);
        if (size == 2) {
            return "";
        }

        byte[] data = new byte[(int)size - 16];
        BytePointer buf = new BytePointer(Pointer.malloc(size));
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

    public PDFPage createPage(int pageIndex,double width, double height) {

        valid();

        PDFPage page = new PDFPage(this,pageIndex,(int)width,(int)height);
        movePageReference(pageIndex,pageIndex + 1);
        loadedPages.put(pageIndex,page);

        return page;

    }

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

    void removeFont(PDFFont font) {
        loadedFonts.remove(font.getKey());
    }

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
