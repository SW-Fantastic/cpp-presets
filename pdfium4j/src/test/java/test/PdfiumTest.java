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
        // TODO 未知原因的崩溃，generateContent和ImageIO冲突了。

        //document.write(new File("testout.pdf"));

        OutputStream os = Files.newOutputStream(Paths.get("./pdfium4j/test.png"));
        PDFBitmap image = pdfPage.renderPage(4, PDFPageRotate.NO_ROTATE);
        BufferedImage image1 = image.createBufferedImage();
        ImageIO.write(image1,"png" ,os );
        document.write(new File("testout.pdf"));

        // ImageIO的write和Pdfium的write存在未知冲突，一旦调用ImageIO的write后，
        // 则不能调用Pdfium的Write，否则将会导致崩溃。
        // 如果非要这样做，首先需要关闭Pdfium的Document，再次打开后就能正常Write。
        //ImageIO.write(image1,"png" ,os );
        os.close();

    }

}
