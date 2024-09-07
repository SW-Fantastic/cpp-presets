package org.swdc.imgui.conf;

import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.Info;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

@Properties(value = {
        @Platform(
                value = "windows-x86_64",
                includepath = "C:/Program Files (x86)/Windows Kits/10/Include/10.0.22621.0/um/gl",
                include = { "gl.h" },
                exclude = { "winapifamily.h" },
                link =  { "opengl32" }
        )
},
        target = "org.swdc.imgui.core.opengl",
        global = "org.swdc.imgui.core.ImGUIGL"
)
public class ImGuiOpenGLConfigure implements InfoMapper {
    @Override
    public void map(InfoMap infoMap) {
        infoMap.put(new Info("WINGDIAPI").cppText("#define WINGDIAPI"));
        infoMap.put(new Info(
                "PFNGLARRAYELEMENTEXTPROC",
                "PFNGLDRAWARRAYSEXTPROC",
                "PFNGLVERTEXPOINTEREXTPROC",
                "PFNGLNORMALPOINTEREXTPROC",
                "PFNGLCOLORPOINTEREXTPROC",
                "PFNGLINDEXPOINTEREXTPROC",
                "PFNGLTEXCOORDPOINTEREXTPROC",
                "PFNGLEDGEFLAGPOINTEREXTPROC",
                "PFNGLGETPOINTERVEXTPROC",
                "PFNGLARRAYELEMENTARRAYEXTPROC",
                "PFNGLDRAWRANGEELEMENTSWINPROC",
                "PFNGLADDSWAPHINTRECTWINPROC",
                "PFNGLCOLORTABLEEXTPROC",
                "PFNGLCOLORSUBTABLEEXTPROC",
                "PFNGLGETCOLORTABLEEXTPROC",
                "PFNGLGETCOLORTABLEPARAMETERIVEXTPROC",
                "PFNGLGETCOLORTABLEPARAMETERFVEXTPROC",
                "GL_WIN_swap_hint",
                "GL_WIN_draw_range_elements",
                "GL_MAX_ELEMENTS_VERTICES_WIN",
                "GL_MAX_ELEMENTS_INDICES_WIN",
                "GL_PHONG_WIN",
                "GL_PHONG_HINT_WIN",
                "GL_FOG_SPECULAR_TEXTURE_WIN",
                "APIENTRY"
        ).skip());

    }
}
