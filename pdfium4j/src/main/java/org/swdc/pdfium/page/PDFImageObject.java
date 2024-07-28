package org.swdc.pdfium.page;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.swdc.pdfium.PDFPage;
import org.swdc.pdfium.PDFPageObject;
import org.swdc.pdfium.core.PdfiumEdit;
import org.swdc.pdfium.core.view.FPDF_FILEACCESS;
import org.swdc.pdfium.core.view.fpdf_pageobject_t__;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class PDFImageObject extends PDFPageObject {

    public PDFImageObject(fpdf_pageobject_t__ obj, PDFPage page) {
        super(obj, page);
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

        FPDF_FILEACCESS fileaccess = new FPDF_FILEACCESS();
        fileaccess.m_FileLen(data.length);
        fileaccess.m_GetBlock(new FPDF_FILEACCESS.M_GetBlock_Pointer_long_BytePointer_long() {
            @Override
            public int call(Pointer param, long _position, BytePointer pBuf, long size) {
                if (_position + size < _position || _position + size > data.length){
                    return 0;
                }
                pBuf.put(data,(int)_position,(int)size);
                return (int)size;
            }
        });

        int state = PdfiumEdit.FPDFImageObj_LoadJpegFile(
                getOwner() == null ? null : getOwnerObj(), 0, obj, fileaccess
        );

        return state == 1;
    }

}
