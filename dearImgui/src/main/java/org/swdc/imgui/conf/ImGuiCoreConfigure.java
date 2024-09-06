package org.swdc.imgui.conf;

import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.Info;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

@Properties(value = {
        @Platform(
                value = "windows-x86_64",
                includepath = { "platforms/DearImGUI" },
                include = { "cimgui.h" },
                linkpath = "platforms/DearImGUI/binary/windows-x86_64",
                link = "DearImGUI"
        ),
        @Platform(
                compiler = "cpp11",
                value = "macosx-x86_64",
                includepath = { "platforms/DearImGUI" },
                include = { "cimgui.h" },
                linkpath = "platforms/DearImGUI/binary/macos-x64",
                link = "DearImGUI"
        )
},
        target = "org.swdc.imgui.core.imgui",
        global = "org.swdc.imgui.core.ImGUICore"
)
public class ImGuiCoreConfigure implements InfoMapper {

    @Override
    public void map(InfoMap infoMap) {

        infoMap.put(new Info("CIMGUI_API").cppText("#define CIMGUI_API"));
        infoMap.put(new Info("CIMGUI_IMPL_API").cppText("#define CIMGUI_IMPL_API"));

        infoMap.put(new Info(
                "ImDrawCallback_ResetRenderState",
                "ImStr",
                "ImVector",
                "IM_MSVC_RUNTIME_CHECKS_OFF",
                "IM_MSVC_RUNTIME_CHECKS_RESTORE"
        ).skip());
    }

}
