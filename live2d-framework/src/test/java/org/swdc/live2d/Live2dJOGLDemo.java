package org.swdc.live2d;

import com.jogamp.nativewindow.WindowClosingProtocol;
import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.FPSAnimator;
import com.live2d.sdk.cubism.framework.CubismFramework;
import com.live2d.sdk.cubism.sdk.Live2dConfigure;
import com.live2d.sdk.cubism.sdk.jogl.Live2dJOGLDelegate;
import com.live2d.sdk.demo.LAppDelegate;
import com.live2d.sdk.demo.LAppLive2DManager;
import org.bytedeco.javacpp.Loader;
import org.swdc.live2d.core.Live2dCore;

import java.io.File;

public class Live2dJOGLDemo {

    static class GLListener implements GLEventListener {

        private Live2dJOGLDelegate delegate;

        public GLListener(Live2dJOGLDelegate delegate) {
            this.delegate = delegate;
        }

        @Override
        public void init(GLAutoDrawable drawable) {
            delegate.initialize(drawable);
        }

        @Override
        public void dispose(GLAutoDrawable drawable) {
            delegate.dispose();
        }

        @Override
        public void display(GLAutoDrawable drawable) {
            delegate.updateView();
        }

        @Override
        public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
            delegate.updateView();
        }

    }

    public static void main(String[] args) {

        Loader.load(Live2dCore.class);
        CubismFramework.cleanUp();
        CubismFramework.startUp(new CubismFramework.Option());

        Live2dConfigure configure = new Live2dConfigure();
        configure.setDebugLog(true);
        Live2dJOGLDelegate delegate = new Live2dJOGLDelegate(
                configure,new File("live2d-framework/assets/")
        );


        GLWindow window = GLWindow.create(new GLCapabilities(GLProfile.getDefault()));

        FPSAnimator animator = new FPSAnimator(window,60);
        window.setAnimator(animator);
        window.addGLEventListener(new GLListener(delegate));
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyed(WindowEvent e) {
                System.exit(0);
            }
        });
        window.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                delegate.invoke(drawable -> {
                    animator.pause();
                    delegate.getManager().nextScene();
                    animator.resume();
                    return true;
                });
            }
        });
        window.setDefaultCloseOperation(WindowClosingProtocol.WindowClosingMode.DISPOSE_ON_CLOSE);
        window.setSize(600,840);
        animator.start();
        window.setVisible(true);


    }

}
