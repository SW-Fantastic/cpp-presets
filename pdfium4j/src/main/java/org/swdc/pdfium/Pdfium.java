package org.swdc.pdfium;

import org.swdc.pdfium.core.PdfiumView;

public class Pdfium {

    private static boolean initialized = false;

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

    public static boolean isInitialized() {
        synchronized (Pdfium.class) {
            return initialized;
        }
    }

}
