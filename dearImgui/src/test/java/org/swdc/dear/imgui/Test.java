package org.swdc.dear.imgui;


import org.bytedeco.javacpp.BoolPointer;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.swdc.imgui.core.ImGUICore;
import org.swdc.imgui.core.ImGUIGL;
import org.swdc.imgui.core.ImGUIGLFW;
import org.swdc.imgui.core.glfw.GLFWerrorfun;
import org.swdc.imgui.core.glfw.GLFWwindow;
import org.swdc.imgui.core.imgui.*;

public class Test {

    public static void main(String[] args) {

        ImGUIGLFW.glfwSetErrorCallback(new GLFWerrorfun() {
            @Override
            public void call(int arg0, BytePointer arg1) {
                System.err.println("Err  - " + arg1.getString());
            }
        });

        ImGUIGLFW.glfwInit();
        ImGUIGLFW.glfwDefaultWindowHints();
        ImGUIGLFW.glfwWindowHint(ImGUIGLFW.GLFW_VISIBLE,ImGUIGLFW.GLFW_FALSE);

        GLFWwindow window = ImGUIGLFW.glfwCreateWindow(1000,800,"Demo",null,null);
        ImGUIGLFW.glfwMakeContextCurrent(window);
        ImGUIGLFW.glfwSwapInterval(1);
        ImGUICore.ImGui_CreateContext(null);
        ImGuiIO imGuiIO = ImGUICore.ImGui_GetIO();
        imGuiIO.ConfigFlags(imGuiIO.ConfigFlags() | ImGUICore.ImGuiConfigFlags_NavEnableKeyboard | ImGUICore.ImGuiConfigFlags_DockingEnable | ImGUICore.ImGuiConfigFlags_ViewportsEnable);
        imGuiIO.ConfigViewportsNoAutoMerge(true);

        ImGUICore.ImGui_StyleColorsDark(null);

        ImGUIGLFW.ImGui_ImplGlfw_InitForOpenGL(window,true);
        ImGUIGLFW.ImGui_ImplOpenGL3_Init();

        ImVec4 clear_color = new ImVec4();
        clear_color.x(0.45f);
        clear_color.y(0.55f);
        clear_color.z(0.60f);
        clear_color.w(1.0f);

        BoolPointer pointer = new BoolPointer(1);
        pointer.put(false);
        boolean shown = false;
        while (ImGUIGLFW.glfwWindowShouldClose(window) == 0) {

            if (!shown) {
                //ImGUIGLFW.glfwHideWindow(window);
                shown = true;
            }

            ImGUIGLFW.glfwPollEvents();

            ImGUIGLFW.ImGui_ImplOpenGL3_NewFrame();
            ImGUIGLFW.ImGui_ImplGlfw_NewFrame();
            ImGUICore.ImGui_NewFrame();

            ImGUICore.ImGui_ShowDemoWindow((BoolPointer) null);
            ImGUICore.ImGui_Render();

            IntPointer pWidth = new IntPointer(1);
            IntPointer pHeight = new IntPointer(1);
            ImGUIGLFW.glfwGetFramebufferSize(window,pWidth,pHeight);
            ImGUIGL.glViewport(0,0,pWidth.get(),pHeight.get());
            ImGUIGL.glClearColor(clear_color.x() * clear_color.w(), clear_color.y() * clear_color.w(), clear_color.z() * clear_color.w(), clear_color.w());
            ImGUIGL.glClear(ImGUIGL.GL_COLOR_BUFFER_BIT);

            GLFWwindow cur = ImGUIGLFW.glfwGetCurrentContext();
            ImGUICore.ImGui_UpdatePlatformWindows();
            ImGUICore.ImGui_RenderPlatformWindowsDefault();
            ImGUIGLFW.glfwMakeContextCurrent(cur);

            ImGUIGLFW.ImGui_ImplOpenGL3_RenderDrawData(ImGUICore.ImGui_GetDrawData());
            ImGUIGLFW.glfwSwapBuffers(window);

        }


        ImGUIGLFW.ImGui_ImplOpenGL3_Shutdown();
        ImGUIGLFW.ImGui_ImplGlfw_Shutdown();
        ImGUICore.ImGui_DestroyContext(null);

        ImGUIGLFW.glfwDestroyWindow(window);
        ImGUIGLFW.glfwTerminate();
    }

}
