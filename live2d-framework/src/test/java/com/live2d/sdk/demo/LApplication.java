package com.live2d.sdk.demo;

import com.jogamp.nativewindow.WindowClosingProtocol;
import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.FPSAnimator;
import org.bytedeco.javacpp.Loader;
import org.swdc.live2d.core.Live2dCore;


public class LApplication {

    static class GLListener implements GLEventListener {

        private GLAutoDrawable drawable;

        private FPSAnimator animator;

        public GLListener(GLAutoDrawable drawable) {
            this.drawable = drawable;
            animator = new FPSAnimator(this.drawable,60);
        }

        @Override
        public void init(GLAutoDrawable drawable) {
            LAppDelegate delegate = LAppDelegate.getInstance(this.drawable);
            delegate.onStart();
            delegate.onSurfaceCreated();
            delegate.onSurfaceChanged();
            delegate.run();
            animator.start();
        }

        @Override
        public void dispose(GLAutoDrawable drawable) {

        }

        @Override
        public void display(GLAutoDrawable drawable) {
            LAppDelegate.getInstance(this.drawable).run();
        }

        @Override
        public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
            LAppDelegate delegate = LAppDelegate.getInstance(this.drawable);
            delegate.onSurfaceChanged();
            delegate.run();
        }

    }

    public static void main(String[] args) throws InterruptedException {

        Loader.load(Live2dCore.class);

        /*JFrame frame = new JFrame();
        frame.setSize(600,1000);
        GLJPanel canvas = new GLJPanel();
        frame.add(canvas);
        canvas.addGLEventListener(new GLListener(canvas));

        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);*/

        GLWindow window = GLWindow.create(new GLCapabilities(null));
        window.addGLEventListener(new GLListener(window));
        window.setSize(800,1000);
        window.setVisible(true);

    }

}
