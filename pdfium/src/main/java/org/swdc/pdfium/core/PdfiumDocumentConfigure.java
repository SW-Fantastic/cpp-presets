package org.swdc.pdfium.core;

import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

@Properties(value = {
        @Platform(
                value = "windows-x86_64",
                includepath = { "platforms/Pdfium/include" },
                include = "fpdf_doc.h",
                linkpath = "platforms/Pdfium/dll/windows/x86_64",
                link = "pdfium.dll"
        )
},
        inherit = PdfiumViewConfigure.class,
        target = "org.swdc.pdfium.core.PdfiumDocument",
        global = "org.swdc.pdfium.core.PdfiumDocument"
)
public class PdfiumDocumentConfigure implements InfoMapper {
    @Override
    public void map(InfoMap infoMap) {

    }
}