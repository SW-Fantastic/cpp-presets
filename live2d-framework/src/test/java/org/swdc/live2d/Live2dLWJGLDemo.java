package org.swdc.live2d;

import com.live2d.sdk.cubism.framework.CubismFramework;
import com.live2d.sdk.cubism.sdk.Live2dConfigure;
import com.live2d.sdk.cubism.sdk.lwjgl.Live2dLWGLDelegate;
import com.live2d.sdk.cubism.sdk.lwjgl.Live2dLWGLManager;
import org.bytedeco.javacpp.Loader;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.swdc.live2d.core.Live2dCore;

import java.io.File;

public class Live2dLWJGLDemo {

    public static void main(String[] args) {

        // 初始化Live2D Framework
        CubismFramework.cleanUp();
        CubismFramework.startUp(new CubismFramework.Option());

        // 创建Live2D LWJGL渲染代理
        Live2dConfigure configure = new Live2dConfigure();
        configure.setDebugLog(true);
        Live2dLWGLDelegate delegate = new Live2dLWGLDelegate(
                configure,new File("live2d-framework/assets/")
        );

        // 初始化GLFW和OpenGL
        GLFW.glfwInit();
        long pointer = GLFW.glfwCreateWindow(800, 1000, "Live2D Demo",0,0);
        if (pointer <= 0) {
            System.err.println("Failed to create window");
            System.exit(0);
        }

        GLFW.glfwShowWindow(pointer);
        GLFW.glfwMakeContextCurrent(pointer);
        GL.createCapabilities();

        // 初始化Live2d渲染代理
        delegate.initialize(800,1000);
        delegate.setRenderingTargetClearColor(255,255,255);

        GLFW.glfwSetMouseButtonCallback(pointer,(window, button, action, mods) -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && action == GLFW.GLFW_PRESS) {
                delegate.setRenderingTargetClearColor(255,255,255);
                Live2dLWGLManager manager = delegate.getManager();
                manager.nextScene();
            }
        });

        long currentTime = System.currentTimeMillis();
        long lastTime = currentTime;

        while (!GLFW.glfwWindowShouldClose(pointer)) {
            lastTime = System.currentTimeMillis();
            if ((lastTime - currentTime) / 1000.0f > 0.03) {
                // 更新画面
                delegate.updateView();
                GLFW.glfwSwapBuffers(pointer);
                currentTime = lastTime;
            }
            GLFW.glfwPollEvents();
        }
        // 清理资源
        GLFW.glfwDestroyWindow(pointer);
        GLFW.glfwTerminate();
    }

}
