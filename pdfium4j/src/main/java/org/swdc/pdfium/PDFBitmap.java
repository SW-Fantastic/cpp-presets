package org.swdc.pdfium;


import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.swdc.pdfium.core.PdfiumEdit;
import org.swdc.pdfium.core.PdfiumView;
import org.swdc.pdfium.core.view.fpdf_bitmap_t__;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.*;
import java.nio.ByteBuffer;

public class PDFBitmap implements Closeable {

    private fpdf_bitmap_t__ bitmap;

    public PDFBitmap(int width, int height, boolean hasAlpha) {

        if (!Pdfium.isInitialized()) {
            Pdfium.doInitialize();
        }

        bitmap = PdfiumView.FPDFBitmap_Create(width,height,hasAlpha ? 1 : 0);
        if (bitmap == null) {
            throw new RuntimeException("failed to create bitmap");
        }

    }

    public int getWidth() {
        valid();
        return PdfiumView.FPDFBitmap_GetWidth(bitmap);
    }

    public int getHeight() {
        valid();
        return PdfiumView.FPDFBitmap_GetHeight(bitmap);
    }

    public void fillRect(int left, int top, int width, int height, String hex) {
        valid();
        PdfiumView.FPDFBitmap_FillRect(
                bitmap,
                left,
                top,
                width,
                height,
                Long.parseUnsignedLong(hex, 16)
        );
    }

    public int getStride()  {
        valid();
        return PdfiumView.FPDFBitmap_GetStride(bitmap);
    }


    public byte[] getBuffer() {

        valid();
        Pointer buffer = PdfiumView.FPDFBitmap_GetBuffer(bitmap);

        if (buffer != null) {

            int theHeight = PdfiumView.FPDFBitmap_GetHeight(bitmap);
            int stride = PdfiumView.FPDFBitmap_GetStride(bitmap);
            int bufSize = stride * theHeight;
            byte[] data = new byte[bufSize];

            BytePointer bufPointer = new BytePointer(buffer);
            bufPointer.get(data);
            bufPointer.close();
            return data;
        }
        return null;
    }

    public BufferedImage createBufferedImage() throws IOException {

        byte[] data = getBuffer();

        int width = getWidth();
        int height = getHeight();

        DataBuffer dataBuffer = new DataBufferByte(data,width * height * 4);
        ColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[] {8,8,8,8}, true, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);

        WritableRaster raster = Raster.createInterleavedRaster(
                dataBuffer,
                width,
                height,
                width * 4,
                4,
                new int[] {2, 1, 0, 3},  // 这个指的是颜色通道的顺序。
                null
        );

        return new BufferedImage(colorModel, raster, false, null);
    }

    private void valid() {
        if (bitmap == null || bitmap.isNull()) {
            throw new RuntimeException("bitmap is closed!");
        }
    }

    @Override
    public void close() throws IOException {
        if (bitmap != null && !bitmap.isNull()) {
            PdfiumView.FPDFBitmap_Destroy(bitmap);
            bitmap = null;
        }
    }

    fpdf_bitmap_t__ getBitmap() {
        return bitmap;
    }
}
