package org.swdc.pdfium.page;

import org.swdc.pdfium.PDFPage;
import org.swdc.pdfium.PDFPageObject;
import org.swdc.pdfium.core.PdfiumEdit;
import org.swdc.pdfium.core.view.fpdf_pageobject_t__;

public class PDFPathObject extends PDFPageObject {

    public PDFPathObject(fpdf_pageobject_t__ obj, PDFPage page) {
        super(obj, page);
    }

    public boolean setDrawMode(PathDrawMode mode) {
        if (mode == null || mode.getFillMode() == null) {
            return false;
        }

        valid();
        return PdfiumEdit.FPDFPath_SetDrawMode(
                obj,
                mode.getFillMode().getMode(),
                mode.isStork() ? 1 : 0
        ) == 1;
    }

    public PathDrawMode getDrawMode() {

        valid();
        int[] fillMode = new int[1];
        int[] stoke = new int[1];
        int result = PdfiumEdit.FPDFPath_GetDrawMode(obj,fillMode,stoke);
        if (result == 1) {
            return new PathDrawMode(
                    PDFPathFillMode.of(fillMode[0]),
                    stoke[0] == 1
            );
        }
        return null;

    }

    public boolean setFillColor(PDFColor color) {
        return setFillColor(
                color.getR(),
                color.getG(),
                color.getB(),
                color.getA()
        );
    }

    public boolean setFillColor(int r, int g, int b, int a) {

        valid();
        return PdfiumEdit.FPDFPageObj_SetFillColor(obj,r,g,b,a) == 1;

    }


    public PDFColor getFillColor() {
        int[] color = getFillColorRGBA();
        if (color == null) {
            return null;
        }
        return new PDFColor(color);
    }

    public int[] getFillColorRGBA() {

        valid();
        int[] r = new int[1];
        int[] g = new int[1];
        int[] b = new int[1];
        int[] a = new int[1];
        int rst = PdfiumEdit.FPDFPageObj_GetFillColor(obj,r,g,b,a);
        if (rst == 1) {
            return new int[] {
                    r[0],g[0],b[0],a[0]
            };
        }
        return null;
    }

    public boolean setStrokeColor(PDFColor color) {
        return setStrokeColor(
                color.getR(),
                color.getG(),
                color.getB(),
                color.getA()
        );
    }

    public boolean setStrokeColor(int r, int g, int b, int a) {
        valid();
        return PdfiumEdit.FPDFPageObj_SetStrokeColor(
                obj,r,g,b,a
        ) == 1;
    }

    /**
     * Get the stroke RGBA of a page object. Range of values: 0 - 255.
     * @return color object
     */
    public PDFColor getStrokeColor() {

        int[] color = getStrokeColorRGBA();
        if (color == null) {
            return null;
        }

        return new PDFColor(color);

    }

    /**
     * Get the stroke RGBA of a page object. Range of values: 0 - 255.
     * @return array with order of Red, Green, Blue , Alpha
     */
    public int [] getStrokeColorRGBA() {

        valid();
        int[] r = new int[1];
        int[] g = new int[1];
        int[] b = new int[1];
        int[] a = new int[1];
        int rst = PdfiumEdit.FPDFPageObj_GetStrokeColor(obj,r,g,b,a);
        if (rst == 1) {
            return new int[] {
                    r[0],g[0],b[0],a[0]
            };
        }

        return null;
    }

    /**
     * Set the stroke width of a page object.
     * @param width stroke width
     * @return true on success
     */
    public boolean setStrokeWidth(float width) {

        valid();
        return PdfiumEdit.FPDFPageObj_SetStrokeWidth(obj,width) == 1;

    }

    /**
     * Get the stroke width of a page object.
     * @return width
     */
    public float getStrokeWidth() {

        valid();
        float[] width = new float[1];
        int state = PdfiumEdit.FPDFPageObj_GetStrokeWidth(obj,width);
        if (state == 1) {
            return width[0];
        }

        return 0;

    }

    /**
     * Add a line between the current point and a new point in the path.
     * The path's current point is changed to (x, y).
     *
     * @param x the horizontal position of the new point.
     * @param y the vertical position of the new point.
     * @return true on success.
     */
    public boolean lineTo(float x, float y) {
        valid();
        return PdfiumEdit.FPDFPath_LineTo(obj,x,y) == 1;
    }

    /**
     * The path's current point is changed to (x, y).
     * Note that no line will be created between the previous current point and the
     * new one.
     *
     * @param x the horizontal position of the new point.
     * @param y the vertical position of the new point.
     * @return true on success.
     */
    public boolean moveTo(float x, float y) {
        valid();
        return PdfiumEdit.FPDFPath_MoveTo(obj,x,y) == 1;
    }

    public boolean bezierTo(float x1,float y1, float x2, float y2, float x3, float y3) {
        valid();
        return PdfiumEdit.FPDFPath_BezierTo(
                obj,x1,y1,x2,y2,x3,y3
        ) == 1;
    }


}
