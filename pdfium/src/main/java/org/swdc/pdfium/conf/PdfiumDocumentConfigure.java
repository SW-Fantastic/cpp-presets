package org.swdc.pdfium.conf;

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
        ),
        @Platform(
                value = "macosx-x86_64",
                includepath = { "platforms/Pdfium/include" },
                include = "fpdf_doc.h",
                linkpath = "platforms/Pdfium/dll/macosx",
                link = "pdfium"
        ),
        @Platform(
                value = "linux-x86_64",
                includepath = { "platforms/Pdfium/include" },
                include = "fpdf_doc.h",
                linkpath = "platforms/Pdfium/dll/linux",
                link = "pdfium"
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
