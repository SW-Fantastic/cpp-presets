package org.swdc.pdfium;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.swdc.pdfium.core.edit.FPDF_FILEWRITE;

import java.io.OutputStream;

public class PDFWriter extends FPDF_FILEWRITE {

    public PDFWriter(OutputStream outputStream) {
        this.WriteBlock(new WriteBlock_FPDF_FILEWRITE_Pointer_long() {

            private byte[] buffer = null;

            @Override
            public int call(FPDF_FILEWRITE pThis, Pointer pData, long size) {

                if (buffer == null || buffer.length < size) {
                    buffer = new byte[(int)size];
                }
                BytePointer pointer = new BytePointer(pData);
                pointer.get(buffer, 0, (int) size);
                try {
                    outputStream.write(buffer);
                    return 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

    }


}
