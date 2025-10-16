package com.live2d.sdk.cubism.sdk.jogl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLRunnable;
import com.live2d.sdk.cubism.framework.CubismFramework;
import com.live2d.sdk.cubism.sdk.Live2dConfigure;
import com.live2d.sdk.cubism.sdk.Live2dDelegate;
import com.live2d.sdk.cubism.sdk.Live2dModelPostProcessor;
import com.live2d.sdk.cubism.sdk.Live2dUtils;

import java.io.File;

/**
 * Live2d的OpenGL渲染代理。
 * 管理各类用于Live2d的OpenGL渲染组件
 */
public class Live2dJOGLDelegate extends Live2dDelegate {

    private GLAutoDrawable gl2;

    private Live2dJOGLTextureManager textureManager;

    private Live2dJOGLManager manager;

    private Live2dJOGLView view;

    private Live2dModelPostProcessor<Live2dJOGLModel> modelPostProcessor;

    private boolean disposed = true;

    public Live2dJOGLDelegate(Live2dConfigure configure, File modelRootDir) {

        super(configure, modelRootDir);

    }

    public void setModelPostProcessor(Live2dModelPostProcessor<Live2dJOGLModel> modelPostProcessor) {
        this.modelPostProcessor = modelPostProcessor;
    }

    public GL2 getGl2() {
        return gl2.getGL().getGL2();
    }

    public int getViewWidth() {
        return gl2.getSurfaceWidth();
    }

    public int getViewHeight() {
        return gl2.getSurfaceHeight();
    }

    public Live2dJOGLTextureManager getTextureManager() {
        return textureManager;
    }

    public Live2dJOGLView getView() {
        return view;
    }

    public Live2dJOGLManager getManager() {
        return manager;
    }

    public synchronized void initialize(GLAutoDrawable glDrawable) {

        if (!disposed) {
            throw new IllegalStateException("Already initialized.");
        }

        this.disposed = false;
        this.gl2 = glDrawable;
        GL2 gl2 = glDrawable.getGL().getGL2();

        // テクスチャサンプリング設定
        gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);

        // 透過設定
        gl2.glEnable(GL2.GL_BLEND);
        gl2.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);

        // Initialize Cubism SDK framework
        CubismFramework.initialize();
        // 描画範囲指定
        gl2.glViewport(0, 0, getViewWidth(), getViewHeight());
        this.textureManager = new Live2dJOGLTextureManager(
                getConfigure(), getAssets(), gl2.getGL().getGL2()
        );
        this.view = new Live2dJOGLView(this);
        this.manager = new Live2dJOGLManager(this, modelPostProcessor);

        // AppViewの初期化
        view.initialize();
        view.initializeSprite();

    }

    public void setRenderingTargetClearColor(float r, float g, float b, float a) {
        view.setRenderingTargetClearColor(r, g, b, a);
    }

    public void setRenderingTargetClearColor(float r, float g, float b) {
        view.setRenderingTargetClearColor(r, g, b, 1.0f);
    }

    public void updateView() {

        if (disposed) {
            throw new IllegalStateException("Already disposed.");
        }

        // 時間更新
        Live2dUtils.updateTime();
        GL2 gl2 = getGl2();
        // 画面初期化
        float[] clearColor = view.getClearColor();
        gl2.glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
        gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl2.glClearDepthf(1.0f);

        if (view != null) {
            view.render();
        }

    }

    public synchronized void dispose() {

        if (disposed) {
            return;
        }

        this.disposed = true;
        if (view != null) {
            view.close();
        }
        manager = null;
        textureManager.clearTextures();
        textureManager = null;
        gl2 = null;
    }

    public void invoke(GLRunnable runnable) {
        if (disposed) {
            throw new IllegalStateException("Already disposed.");
        }
        gl2.invoke(true,runnable);
    }

    public void invokeLater(GLRunnable runnable) {
        if (disposed) {
            throw new IllegalStateException("Already disposed.");
        }
        gl2.invoke(false,runnable);
    }

}
