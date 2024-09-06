package org.swdc.imgui.conf;


import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.Info;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

@Properties(value = {
        @Platform(
                value = "windows-x86_64",
                includepath = { "platforms/DearImGUI", "platforms/DearImGUI/libs/glfw/include/GLFW" },
                include = { "imgui_impl_glfw.h", "imgui_impl_opengl3.h","glfw3.h" },
                linkpath = { "platforms/DearImGUI/binary/windows-x86_64", "platforms/DearImGUI/libs/glfw/lib-vc2010-64" },
                link = {"DearImGUI","glfw3dll"}
        ),
        @Platform(
                compiler = "cpp11",
                value = "macosx-x86_64",
                includepath = { "platforms/DearImGUI", "platforms/DearImGUI/libs/glfw/include/GLFW" },
                include = { "imgui_impl_glfw.h", "imgui_impl_opengl3.h","glfw3.h" },
                linkpath = { "platforms/DearImGUI/binary/macos-x64", "platforms/DearImGUI/libs/glfw/lib-macos-universal" },
                link = {"DearImGUI","glfw.3"}
        )
},
        target = "org.swdc.imgui.core.glfw",
        global = "org.swdc.imgui.core.ImGUIGLFW"
)
public class ImGuiGLFWConfigure implements InfoMapper {
    @Override
    public void map(InfoMap infoMap) {

        infoMap.put(new Info("GLFWAPI").cppText("#define GLFWAPI"));
        infoMap.put(new Info("CALLBACK").cppText("#define CALLBACK"));
        infoMap.put(new Info("WINGDIAPI").cppText("#define WINGDIAPI"));
        infoMap.put(new Info("VkInstance").skip());

        infoMap.put(new Info("IMGUI_IMPL_API").cppText("#define IMGUI_IMPL_API"));
        infoMap.put(new Info("ImGui_ImplGlfw_InstallEmscriptenCallbacks").skip());
        infoMap.put(new Info("ImGui_ImplOpenGL3_RenderDrawData")
                .javaText("public static native void ImGui_ImplOpenGL3_RenderDrawData(org.swdc.imgui.core.imgui.ImDrawData draw_data);")
                .translate()
        );
        infoMap.put(new Info("GLFWwindow*", "GLFWmonitor*").cppTypes("void*").translate());

    }
}
