package test;


import org.swdc.pdfium.*;
import org.swdc.pdfium.page.PDFImageObject;
import org.swdc.pdfium.page.PDFTextObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PdfiumTest {

    public static void main(String[] args) throws IOException, InterruptedException {

        PDFDocument document = new PDFDocument(new File("./pdfium4j/demo2.pdf"));
        for (PDFMetaType type: PDFMetaType.values()) {
            System.err.println("type name : " + type.name() + " type value : " + document.getMetadata(type));
        }

        PDFPage pdfPage = document.getPage(1);
        System.err.println("page w: " + pdfPage.getWidth());
        System.err.println("page h: " + pdfPage.getHeight());

       PDFFont font = document.loadFont(new File("./pdfium4j/font.ttf").getAbsoluteFile());
        PDFTextObject txt = pdfPage.createText(font,12);
        txt.setText("测试文本 HelloWorld");
        txt.setBounds(2.4f,2,0,0);
        pdfPage.addObject(txt);

        System.err.println(txt.getText());

        PDFImageObject img = pdfPage.createImage();
        img.loadImage(new File("./pdfium4j/test_3.png").getAbsoluteFile());
        img.setBounds(120, 160, 80, 80);
        pdfPage.addObject(img);
        pdfPage.generateContent();
        pdfPage.close();

        pdfPage = document.getPage(1);

        OutputStream os = Files.newOutputStream(Paths.get("./pdfium4j/test.png"));
        PDFBitmap image = pdfPage.renderPage(4, PDFPageRotate.NO_ROTATE);
        BufferedImage image1 = image.createBufferedImage();
        //ImageIO.write(image1,"png" ,os );

        //pdfPage.close();

        OutputStream bos = new FileOutputStream("testout.pdf");
        document.write(bos);
        document.close();
        //os.close();
        //image.close();





    }

}
