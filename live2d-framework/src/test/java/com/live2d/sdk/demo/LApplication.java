package com.live2d.sdk.demo;

import com.jogamp.nativewindow.WindowClosingProtocol;
import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import org.bytedeco.javacpp.Loader;
import org.swdc.live2d.core.Live2dCore;

import javax.swing.*;


public class LApplication {

    static class GLListener implements GLEventListener {

        private GLAutoDrawable drawable;

        public GLListener(GLAutoDrawable drawable) {
            this.drawable = drawable;
        }

        @Override
        public void init(GLAutoDrawable drawable) {

            LAppDelegate delegate = LAppDelegate.getInstance(this.drawable);
            delegate.onStart();
            delegate.onSurfaceCreated();
            delegate.onSurfaceChanged();
            delegate.run();

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
            drawable.invoke(false, new GLRunnable() {
                @Override
                public boolean run(GLAutoDrawable drawable) {
                    // OpenGL的渲染操作务必在OpenGL的线程完成
                    // 例如这种切换模型的处理，如果不在OpenGL线程中执行，它将会无法正确的载入贴图
                    // 从而导致渲染的异常（但它不会报错）。
                    LAppLive2DManager manager = LAppLive2DManager.getInstance();
                    manager.nextScene();
                    return true;
                }
            });

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

        /*JFrame frame = new JFrame();
        frame.setSize(600,1000);

        GLJPanel canvas = new GLJPanel();
        GLListener listener = new GLListener(canvas);

        FPSAnimator animator = new FPSAnimator(canvas,30);
        animator.start();

        canvas.setAnimator(animator);
        canvas.addGLEventListener(listener);
        canvas.setRequestedGLCapabilities(new GLCapabilities(GLProfile.getDefault()));
        canvas.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                listener.mouseDown(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                listener.mouseUp(e.getX(), e.getY());
            }

            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                listener.mouseMove(e.getX(), e.getY());
            }
        });

        frame.add(canvas);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);*/

        GLWindow window = GLWindow.create(new GLCapabilities(GLProfile.getDefault()));
        GLListener listener = new GLListener(window);
        FPSAnimator animator = new FPSAnimator(window,60);
        window.setAnimator(animator);
        window.addGLEventListener(listener);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyed(WindowEvent e) {
                System.exit(0);
            }
        });
        window.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                window.invoke(false, new GLRunnable() {
                    @Override
                    public boolean run(GLAutoDrawable drawable) {
                        // OpenGL的渲染操作务必在OpenGL的线程完成
                        // 例如这种切换模型的处理，如果不在OpenGL线程中执行，它将会无法正确的载入贴图
                        // 从而导致渲染的异常（但它不会报错）。
                        animator.pause();
                        LAppLive2DManager manager = LAppLive2DManager.getInstance();
                        manager.nextScene();
                        animator.resume();
                        return true;
                    }
                });
            }
        });
        window.setDefaultCloseOperation(WindowClosingProtocol.WindowClosingMode.DISPOSE_ON_CLOSE);
        window.setSize(600,840);
        animator.start();
        window.setVisible(true);
    }

}
