package org.swdc.pdfium.page;

public class PDFColor {

    private int r;

    private int g;

    private int b;

    private int a;

    /**
     * Construct PDFColor with color value
     * 通过颜色值构建PDF颜色对象。
     *
     * @param r red 红色
     * @param g green 绿色
     * @param b blue 蓝色
     * @param a alpha 透明度
     */
    public PDFColor(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    /**
     * Construct PDFColor object with color string
     * 通过颜色字符串构建PDF颜色对象。
     *
     * @param colorStr rgba(red,green,blue,alpha) or rgb(red,green,blue) or hex string like #FFF.
     *                 使用rgb(红,绿,蓝)，rgba(红,绿,蓝,透明度(取值范围为0 - 1.0))或者以#开头的十六进制颜色字符串。
     */
    public PDFColor(String colorStr) {
        colorStr = colorStr.toLowerCase();
        if (colorStr.startsWith("#")) {
            colorStr = colorStr.substring(1);
            if (colorStr.length() == 3) {
                r = Integer.parseInt(colorStr.substring(0,1).repeat(2),16);
                g = Integer.parseInt(colorStr.substring(1,2).repeat(2),16);
                b = Integer.parseInt(colorStr.substring(2).repeat(2),16);
                a = 255;
            } else if (colorStr.length() == 6) {
                r = Integer.parseInt(colorStr.substring(0, 2), 16);
                g = Integer.parseInt(colorStr.substring(2, 4), 16);
                b = Integer.parseInt(colorStr.substring(4, 6), 16);
                a = 255;
            } else if (colorStr.length() == 8) {
                r = Integer.parseInt(colorStr.substring(0, 2), 16);
                g = Integer.parseInt(colorStr.substring(2, 4), 16);
                b = Integer.parseInt(colorStr.substring(4, 6), 16);
                a = Integer.parseInt(colorStr.substring(6, 8), 16);
            } else {
                throw new RuntimeException("unsupported color string format: " + colorStr);
            }
        } else if (colorStr.startsWith("rgb")) {
            if (colorStr.startsWith("rgb(")) {
                colorStr = colorStr.replace("rgb(", "")
                        .replace(")", "");
                String[] rgb = colorStr.split(",");
                r = Integer.parseInt(rgb[0]);
                g = Integer.parseInt(rgb[1]);
                b = Integer.parseInt(rgb[2]);
                a = 255;
            } else if (colorStr.startsWith("rgba(")) {

                colorStr = colorStr.replace("rgba(","")
                        .replace(")","");
                String[] rgba = colorStr.split(",");
                String a = rgba[3];
                if (a.indexOf('.') > 0) {
                    double alpha = Double.parseDouble(a);
                    int intAlpha = (int)(alpha * 255);
                    a = "" + intAlpha;
                }

                this.r = Integer.parseInt(rgba[0]);
                this.g = Integer.parseInt(rgba[1]);
                this.b = Integer.parseInt(rgba[2]);
                this.a = Integer.parseInt(a);

            } else {
                throw new RuntimeException("unsupported color string format: " + colorStr);
            }
        } else {
            throw new RuntimeException("unsupported color string format: " + colorStr);
        }
    }

    /**
     * Construct color object with color Array
     * 通过颜色数组构建颜色对象，内部使用，不对外公开。
     *
     * @param arr color array，length must be 4，arr[0] - arr[3] is red,green,blue,alpha
     *            颜色数组，长度必须为4，从0-4分别为红，绿，蓝，透明度。
     */
    PDFColor(int[] arr) {
        this.r = arr[0];
        this.g = arr[1];
        this.b = arr[2];
        this.a = arr[3];
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getG() {
        return g;
    }

    public int getR() {
        return r;
    }
}
