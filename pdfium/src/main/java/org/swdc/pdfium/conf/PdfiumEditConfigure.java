package org.swdc.pdfium.conf;

import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.Info;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

@Properties(value = {
        @Platform(
                value = "windows-x86_64",
                includepath = { "platforms/Pdfium/include","platforms/Pdfium/ext" },
                include = {
                        "fpdf_edit.h",
                        "fpdf_save.h",
                        "ext_fpdf_save.h",
                        "ext_fpdf_edit.h"
                },
                linkpath = "platforms/Pdfium/dll/windows/x86_64",
                link = "pdfium.dll"
        ),
        @Platform(
                value = "macosx-x86_64",
                includepath = { "platforms/Pdfium/include","platforms/Pdfium/ext" },
                include = {
                        "fpdf_edit.h",
                        "fpdf_save.h",
                        "ext_fpdf_save.h",
                        "ext_fpdf_edit.h"
                },
                linkpath = "platforms/Pdfium/dll/macosx",
                link = "pdfium"
        ),
        @Platform(
                value = "linux-x86_64",
                includepath = { "platforms/Pdfium/include","platforms/Pdfium/ext" },
                include = {
                        "fpdf_edit.h",
                        "fpdf_save.h",
                        "ext_fpdf_save.h",
                        "ext_fpdf_edit.h"
                },
                linkpath = "platforms/Pdfium/dll/linux",
                link = "pdfium"
        )
},
        inherit = PdfiumViewConfigure.class,
        target = "org.swdc.pdfium.core.edit",
        global = "org.swdc.pdfium.core.PdfiumEdit"
)
public class PdfiumEditConfigure implements InfoMapper {
    @Override
    public void map(InfoMap infoMap) {

        infoMap.put(new Info("FPDF_FILEWRITE_").cppTypes("FPDF_FILEWRITE").translate());
        // do not add ext private methods.
        infoMap.put(new Info(
                "priv_fpdf_write_data",
                "priv_fpdf_readJpegBytes",
                "FWriter_",
                "PDFWriter"
        ).skip());

    }
}
