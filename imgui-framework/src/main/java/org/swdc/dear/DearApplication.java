package org.swdc.dear;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.swdc.imgui.core.ImGUICore;
import org.swdc.imgui.core.ImGUIGL;
import org.swdc.imgui.core.ImGUIGLFW;
import org.swdc.imgui.core.glfw.GLFWerrorfun;
import org.swdc.imgui.core.glfw.GLFWwindow;
import org.swdc.imgui.core.imgui.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用程序基础
 */
public class DearApplication {

    private List<DearWindow> windows = new ArrayList<>();

    private File defaultFont;

    void regWindow(DearWindow window) {
        if (!windows.contains(window)) {
            windows.add(window);
        }
    }

    public void setDefaultFont(File defaultFont) {
        if (!defaultFont.exists() || !defaultFont.canRead()) {
            throw new RuntimeException("Can not read the special file");
        }
        this.defaultFont = defaultFont;
    }

    public final void launch(String[] args) {

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
        imGuiIO.ConfigViewportsNoTaskBarIcon(false);
        ImGUICore.ImGui_StyleColorsLight(null);


        if (defaultFont != null) {
            try {

                // 计算高分辨率下的字号。
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice device = ge.getDefaultScreenDevice();
                GraphicsConfiguration configuration = device.getDefaultConfiguration();
                AffineTransform transform = configuration.getDefaultTransform();

                ImFontAtlas atlas = imGuiIO.Fonts();
                ImGUICore.ImFontAtlas_AddFontFromFileTTF(
                        atlas,
                        defaultFont.getAbsolutePath(),
                        (float) (14 * transform.getScaleX()), // 也就是14 * 屏幕的缩放率。
                        null,
                        ImGUICore.ImFontAtlas_GetGlyphRangesChineseFull(atlas)
                );

            } catch (Exception e) {
                ImGUIGLFW.glfwDestroyWindow(window);
                ImGUIGLFW.glfwTerminate();
                throw new RuntimeException("Can not load font.");
            }
        }

        ImGUIGLFW.ImGui_ImplGlfw_InitForOpenGL(window,true);
        ImGUIGLFW.ImGui_ImplOpenGL3_Init();

        ImVec4 clear_color = new ImVec4();
        clear_color.x(0.45f);
        clear_color.y(0.55f);
        clear_color.z(0.60f);
        clear_color.w(1.0f);

        boolean initialized = false;

        do {

            ImGUIGLFW.ImGui_ImplOpenGL3_NewFrame();
            ImGUIGLFW.ImGui_ImplGlfw_NewFrame();
            ImGUICore.ImGui_NewFrame();

            if (!initialized) {
                active();
                initialized = true;
            }

            ImGUIGLFW.glfwPollEvents();

            boolean isActive = false;
            for (DearWindow frames : windows) {
                frames.doRender();
                if (frames.visible()) {
                    isActive = true;
                }
            }

            if (!isActive) {
                ImGUIGLFW.glfwSetWindowShouldClose(window,ImGUIGLFW.GLFW_TRUE);
            }

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


        } while (ImGUIGLFW.glfwWindowShouldClose(window) == 0);

        ImGUIGLFW.ImGui_ImplOpenGL3_Shutdown();
        ImGUIGLFW.ImGui_ImplGlfw_Shutdown();
        ImGUICore.ImGui_DestroyContext(null);

        ImGUIGLFW.glfwDestroyWindow(window);
        ImGUIGLFW.glfwTerminate();

    }

    protected void active() {

    }


}
