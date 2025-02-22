package com.live2d.sdk.demo;

import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import org.bytedeco.javacpp.Loader;
import org.swdc.live2d.core.Live2dCore;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


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

        public void mouseDown(float x, float y) {
            LAppDelegate delegate = LAppDelegate.getInstance(this.drawable);
            delegate.onTouchBegan(x,y);
        }

        public void mouseMove(float x, float y) {
            LAppDelegate delegate = LAppDelegate.getInstance(this.drawable);
            delegate.onTouchMoved(x,y);
        }

        public void mouseUp(float x, float y) {
            LAppDelegate delegate = LAppDelegate.getInstance(this.drawable);
            delegate.onTouchEnd(x,y);
        }

    }

    public static void main(String[] args) throws InterruptedException {

        Loader.load(Live2dCore.class);

        JFrame frame = new JFrame();
        frame.setSize(600,1000);

        GLJPanel canvas = new GLJPanel();
        GLListener listener = new GLListener(canvas);
        canvas.addGLEventListener(listener);
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                listener.mouseDown(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                listener.mouseUp(e.getX(), e.getY());
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                listener.mouseMove(e.getX(), e.getY());
            }
        });
        frame.add(canvas);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

}
