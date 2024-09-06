package org.swdc.dear.imgui;


import org.bytedeco.javacpp.BoolPointer;
import org.bytedeco.javacpp.BytePointer;
import org.swdc.imgui.core.ImGUICore;
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

        GLFWwindow window = ImGUIGLFW.glfwCreateWindow(1000,800,"Demo",null,null);
        ImGUIGLFW.glfwMakeContextCurrent(window);
        ImGUIGLFW.glfwSwapInterval(1);

        ImGUICore.ImGui_CreateContext(null);
        ImGuiIO imGuiIO = ImGUICore.ImGui_GetIO();
        imGuiIO.ConfigFlags(imGuiIO.ConfigFlags() | ImGUICore.ImGuiConfigFlags_NavEnableGamepad | ImGUICore.ImGuiConfigFlags_NavEnableKeyboard);
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
                ImGUIGLFW.glfwShowWindow(window);
            }
            ImGUIGLFW.glfwPollEvents();
            //ImGUIGLFW.ImGui_ImplOpenGL3_NewFrame();
            //ImGUIGLFW.ImGui_ImplGlfw_NewFrame();
        }
    }

}
