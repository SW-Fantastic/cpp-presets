package org.swdc.pdfium;

public enum PDFPageRotate {

    NO_ROTATE(0),
    CLOCKWISE_90(1),
    HORIZONTAL_FLIP(2),
    COUNTER_CLOCKWISE_90(3);

    private int value;
    PDFPageRotate(int val) {
        this.value = val;
    }

    public int getValue() {
        return value;
    }

    public static PDFPageRotate of(int val) {
        for (PDFPageRotate v: values()) {
            if (v.value == val) {
                return v;
            }
        }
        return null;
    }
}
