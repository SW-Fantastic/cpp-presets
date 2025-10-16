package com.live2d.sdk.cubism.sdk.lwjgl;

import com.live2d.sdk.cubism.framework.CubismFramework;
import com.live2d.sdk.cubism.sdk.Live2dConfigure;
import com.live2d.sdk.cubism.sdk.Live2dDelegate;
import com.live2d.sdk.cubism.sdk.Live2dModelPostProcessor;
import com.live2d.sdk.cubism.sdk.Live2dUtils;
import org.lwjgl.opengl.GL30;

import java.io.File;

/**
 * Live2d的OpenGL渲染代理。
 * 管理各类用于Live2d的OpenGL渲染组件
 */
public class Live2dLWGLDelegate extends Live2dDelegate {


    private Live2dLWGLTextureManager textureManager;

    private Live2dLWGLManager manager;

    private Live2dLWGLView view;

    private Live2dModelPostProcessor<Live2dLWGLModel> modelPostProcessor;

    private boolean disposed = true;

    private int viewWidth = 600;

    private int viewHeight = 800;

    public Live2dLWGLDelegate(Live2dConfigure configure, File modelRootDir) {

        super(configure, modelRootDir);

    }

    public void setModelPostProcessor(Live2dModelPostProcessor<Live2dLWGLModel> modelPostProcessor) {
        this.modelPostProcessor = modelPostProcessor;
    }

    public Live2dLWGLTextureManager getTextureManager() {
        return textureManager;
    }

    public Live2dLWGLView getView() {
        return view;
    }

    public Live2dLWGLManager getManager() {
        return manager;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public void setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
    }

    public synchronized void initialize(int viewWidth, int viewHeight) {

        if (!disposed) {
            throw new IllegalStateException("Already initialized.");
        }

        this.disposed = false;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;

        // テクスチャサンプリング設定
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);

        // 透過設定
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_ONE, GL30.GL_ONE_MINUS_SRC_ALPHA);

        // Initialize Cubism SDK framework
        CubismFramework.initialize();
        // 描画範囲指定
        GL30.glViewport(0, 0, getViewWidth(), getViewHeight());
        this.textureManager = new Live2dLWGLTextureManager(
                getConfigure(), getAssets()
        );
        this.view = new Live2dLWGLView(this);
        this.manager = new Live2dLWGLManager(this, modelPostProcessor);

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
        // 画面初期化
        float[] clearColor = view.getClearColor();
        GL30.glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        GL30.glClearDepth(1.0f);

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
    }



}
