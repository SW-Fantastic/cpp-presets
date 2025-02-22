package org.swdc.pdfium;

import org.swdc.pdfium.core.PdfiumView;

public class Pdfium {

    private static boolean initialized = false;

    /**
     * 初始化Pdfium库。
     *
     * 如果库已经初始化，则不会重复初始化。
     * 该方法使用双重检查锁定模式（Double-Checked Locking）来确保线程安全。
     */
    public static void doInitialize() {
        if (!initialized) {
            synchronized (Pdfium.class) {
                if (!initialized) {
                    PdfiumView.FPDF_InitLibrary();
                    initialized = true;
                }
            }
        }
    }

    /**
     * 检查Pdfium库是否已经初始化。
     *
     * @return 如果Pdfium库已经初始化，则返回true；否则返回false。
     */
    public static boolean isInitialized() {
        synchronized (Pdfium.class) {
            return initialized;
        }
    }

}
