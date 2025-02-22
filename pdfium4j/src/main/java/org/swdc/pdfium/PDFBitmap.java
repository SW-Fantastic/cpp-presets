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

    /**
     * 创建一个新的PDF位图对象。
     *
     * @param width 位图的宽度
     * @param height 位图的高度
     * @param hasAlpha 是否包含Alpha通道（透明度）
     * @throws RuntimeException 如果无法创建位图，则抛出运行时异常
     */
    public PDFBitmap(int width, int height, boolean hasAlpha) {

        if (!Pdfium.isInitialized()) {
            Pdfium.doInitialize();
        }

        bitmap = PdfiumView.FPDFBitmap_Create(width,height,hasAlpha ? 1 : 0);
        if (bitmap == null) {
            throw new RuntimeException("failed to create bitmap");
        }

    }

    /**
     * PDFBitmap 构造函数。
     *
     * @param bitmap Pdfium库中的位图对象
     */
    public PDFBitmap(fpdf_bitmap_t__ bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * 获取位图的宽度。
     *
     * @return 返回位图的宽度（以像素为单位）。
     * @throws RuntimeException 如果位图对象无效，则抛出运行时异常。
     */
    public int getWidth() {
        valid();
        return PdfiumView.FPDFBitmap_GetWidth(bitmap);
    }

    /**
     * 获取位图的高度。
     *
     * @return 返回位图的高度（以像素为单位）。
     * @throws RuntimeException 如果位图对象无效，则抛出运行时异常。
     */
    public int getHeight() {
        valid();
        return PdfiumView.FPDFBitmap_GetHeight(bitmap);
    }

    /**
     * 在位图上绘制一个填充的矩形。
     *
     * @param left  矩形的左上角x坐标
     * @param top   矩形的左上角y坐标
     * @param width 矩形的宽度
     * @param height 矩形的高度
     * @param hex   填充颜色的十六进制表示
     * @throws RuntimeException 如果位图对象无效，则抛出运行时异常
     */
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

    /**
     * 获取位图的步长（stride）。
     *
     * 步长是指位图每行像素所占用的字节数，通常等于位图宽度乘以每个像素所占的字节数（对于RGB位图通常是3，对于RGBA位图通常是4）。
     *
     * @return 返回位图的步长（以字节为单位）。
     * @throws RuntimeException 如果位图对象无效，则抛出运行时异常。
     */
    public int getStride()  {
        valid();
        return PdfiumView.FPDFBitmap_GetStride(bitmap);
    }


    /**
     * 获取位图的像素数据缓冲区。
     *
     * @return 返回包含位图像素数据的字节数组，如果位图对象无效或无法获取缓冲区，则返回null。
     * @throws RuntimeException 如果位图对象无效，则抛出运行时异常。
     */
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

    /**
     * 将PDF位图转换为BufferedImage对象。
     *
     * @return 返回转换后的BufferedImage对象。
     * @throws IOException 如果在转换过程中发生I/O错误，则抛出此异常。
     * @throws RuntimeException 如果位图对象无效，则抛出运行时异常。
     */
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

    /**
     * 验证位图对象是否有效。
     *
     * 如果位图对象为空或已被关闭，则抛出运行时异常，提示位图已关闭。
     *
     * @throws RuntimeException 如果位图对象无效，则抛出此异常
     */
    private void valid() {
        if (bitmap == null || bitmap.isNull()) {
            throw new RuntimeException("bitmap is closed!");
        }
    }

    /**
     * 关闭位图对象并释放相关资源。
     *
     * @throws IOException 如果在关闭位图时发生I/O错误，则抛出此异常。
     */
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
