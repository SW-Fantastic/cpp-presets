package org.swdc.pdfium.page;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.swdc.pdfium.PDFBitmap;
import org.swdc.pdfium.PDFPage;
import org.swdc.pdfium.PDFPageObject;
import org.swdc.pdfium.core.PdfiumEdit;
import org.swdc.pdfium.core.view.fpdf_bitmap_t__;
import org.swdc.pdfium.core.view.fpdf_pageobject_t__;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class PDFImageObject extends PDFPageObject {

    /**
     * 写入PDF和渲染页面的时候，
     * 会反复读取图片的像素数据，所以，
     * 这个Buffer需要在本地内存申请，并再
     * 本对象关闭前一直保留。
     */
    private BytePointer buffer;

    public PDFImageObject(fpdf_pageobject_t__ obj, PDFPage page) {
        super(obj, page);
    }

    public PDFBitmap getBitmap() {

        valid();
        fpdf_bitmap_t__ bitmap = PdfiumEdit.FPDFImageObj_GetBitmap(obj);
        if (bitmap != null && !bitmap.isNull()) {
            return new PDFBitmap(bitmap);
        }
        return null;

    }

    public boolean loadImage(File file) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file)){
          return loadImage(inputStream);
        }
    }

    public boolean loadImage(InputStream inputStream) throws IOException {

        valid();
        BufferedImage image = ImageIO.read(inputStream);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image,"jpg" , os);

        byte[] data = os.toByteArray();
        if (this.buffer != null && !this.buffer.isNull()) {
            Pointer.realloc(
                    buffer,
                    data.length
            );
        } else {
            this.buffer = new BytePointer(
                    Pointer.malloc(data.length)
            );
        }

        buffer.put(data);

        return PdfiumEdit.FPDFObj_EXT_LoadJpegData(buffer,data.length,null,obj);
    }

    @Override
    public void close() {
        super.close();
        if (buffer != null && !buffer.isNull()) {
            this.buffer.close();
            this.buffer = null;
        }

    }
}
