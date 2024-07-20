package test;


import org.swdc.pdfium.*;
import org.swdc.pdfium.core.PdfiumView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PdfiumTest {

    public static void main(String[] args) throws IOException {

        PDFDocument document = new PDFDocument(new File("./pdfium4j/demo2.pdf"));
        for (PDFMetaType type: PDFMetaType.values()) {
            System.err.println("type name : " + type.name() + " type value : " + document.getMetadata(type));
        }

        PDFPage pdfPage = document.getPage(1);
        System.err.println("page w: " + pdfPage.getWidth());
        System.err.println("page h: " + pdfPage.getHeight());

        OutputStream os = Files.newOutputStream(Paths.get("./pdfium4j/test.png"));
        PDFBitmap image = pdfPage.renderPage(4, PDFPageRotate.NO_ROTATE);
        BufferedImage image1 = image.createBufferedImage();
        ImageIO.write(image1,"png" ,os );
        //os.write(data);
        os.close();
        image.close();


        pdfPage.close();
        document.close();

    }

}
