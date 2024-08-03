package org.swdc.pdfium.conf;

import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.Info;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

@Properties(value = {
        @Platform(
                value = "windows-x86_64",
                includepath = { "platforms/Pdfium/include" },
                include = "fpdfview.h",
                linkpath = "platforms/Pdfium/dll/windows/x86_64",
                link = "pdfium.dll",
                preload = "pdfium"
        ),
        @Platform(
                value = "macosx-x86_64",
                includepath = { "platforms/Pdfium/include" },
                include = "fpdfview.h",
                linkpath = "platforms/Pdfium/dll/macosx",
                link = "pdfium",
                preload = "pdfium"
        )
},
        target = "org.swdc.pdfium.core.view",
        global = "org.swdc.pdfium.core.PdfiumView"
)
public class PdfiumViewConfigure implements InfoMapper {
        @Override
        public void map(InfoMap infoMap) {

                translateType(infoMap,"fpdf_annotation_t__","FPDF_ANNOTATION");
                translateType(infoMap,"fpdf_attachment_t__","FPDF_ATTACHMENT");
                translateType(infoMap,"fpdf_avail_t__","FPDF_AVAIL");
                translateType(infoMap,"fpdf_bitmap_t__","FPDF_BITMAP");
                translateType(infoMap,"fpdf_bookmark_t__","FPDF_BOOKMARK");
                translateType(infoMap,"fpdf_clippath_t__","FPDF_CLIPPATH");
                translateType(infoMap,"fpdf_dest_t__","FPDF_DEST");
                translateType(infoMap,"fpdf_document_t__","FPDF_DOCUMENT");
                translateType(infoMap,"fpdf_font_t__","FPDF_FONT");
                translateType(infoMap,"fpdf_form_handle_t__","FPDF_FORMHANDLE");
                translateType(infoMap,"fpdf_javascript_action_t","FPDF_JAVASCRIPT_ACTION");
                translateType(infoMap,"fpdf_link_t__","FPDF_LINK");
                translateType(infoMap,"fpdf_page_t__","FPDF_PAGE");
                translateType(infoMap,"fpdf_pagelink_t__","FPDF_PAGELINK");
                translateType(infoMap,"fpdf_pageobject_t__","FPDF_PAGEOBJECT");
                translateType(infoMap,"fpdf_pageobjectmark_t__","FPDF_PAGEOBJECTMARK");
                translateType(infoMap,"fpdf_schhandle_t__","FPDF_SCHHANDLE");
                translateType(infoMap,"fpdf_structelement_t__","FPDF_STRUCTELEMENT");
                translateType(infoMap,"fpdf_structtree_t__","FPDF_STRUCTTREE");
                translateType(infoMap,"fpdf_textpage_t__","FPDF_TEXTPAGE");
                translateType(infoMap,"fpdf_widget_t__","FPDF_WIDGET");
                translateType(infoMap,"fpdf_xobject_t__","FPDF_XOBJECT");

                translateType(infoMap,"fpdf_action_t__","FPDF_ACTION");

                translateConstType(infoMap,"fpdf_pagerange_t__","FPDF_PAGERANGE");
                translateConstType(infoMap,"fpdf_pathsegment_t","FPDF_PATHSEGMENT");
                translateConstType(infoMap,"fpdf_signature_t__","FPDF_SIGNATURE");
                translateConstType(infoMap,"fpdf_structelement_attr_t__","FPDF_STRUCTELEMENT_ATTR");
                translateConstType(infoMap,"fpdf_glyphpath_t__","FPDF_GLYPHPATH");

                infoMap.put(new Info("FPDF_STRING").cppTypes("const char*").translate());
                infoMap.put(new Info("FPDF_WCHAR").cppTypes("unsigned short").translate());
                infoMap.put(new Info("FPDF_BYTESTRING").cppTypes("const char*").translate());
                infoMap.put(new Info("FPDF_WIDESTRING").cppTypes("unsigned short*").translate());
                infoMap.put(new Info("FPDF_EXPORT").cppText("#define FPDF_EXPORT\n"));
                infoMap.put(new Info("FPDF_CALLCONV").cppText("#define FPDF_CALLCONV\n"));

                infoMap.put(new Info(
                        "HDC",
                        "FPDF_BStr_Clear",
                        "FPDF_BStr_Set",
                        "FPDF_BStr_Init",
                        "FPDF_GetRecommendedV8Flags",
                        "FPDF_RenderPageSkia",
                        "FPDF_SetPrintMode",
                        "FPDF_GetArrayBufferAllocatorSharedInstance"
                ).skip());

        }


        private void translateType(InfoMap map, String oldName, String newName) {
                map.put(new Info(newName).cppTypes(oldName + "*").translate());
        }

        private void translateConstType(InfoMap map, String oldName, String newName) {
                map.put(new Info(newName).cppTypes("const struct " + oldName + " *").translate());
        }

}
