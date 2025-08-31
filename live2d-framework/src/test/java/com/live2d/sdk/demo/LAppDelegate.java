package com.live2d.sdk.demo;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.live2d.sdk.cubism.framework.CubismFramework;

public class LAppDelegate {

    private GL2 gl2;

    private AssetManager assets = new AssetManager();

    public AssetManager getAssets() {
        return assets;
    }

    public static LAppDelegate getInstance() {
        return s_instance;
    }

    public static LAppDelegate getInstance(GLAutoDrawable gl2) {
        if (s_instance == null) {
            s_instance = new LAppDelegate(gl2);
        }
        return s_instance;
    }

    /**
     * クラスのインスタンス（シングルトン）を解放する。
     */
    public static void releaseInstance() {
        if (s_instance != null) {
            s_instance = null;
        }
    }

    /**
     * アプリケーションを非アクティブにする
     */
    public void deactivateApp() {
        isActive = false;
    }

    public void onStart() {
        textureManager = new LAppTextureManager(gl2);
        view = new LAppView(gl2);
        LAppPal.updateTime();
    }

    public void onPause() {
        currentModel = LAppLive2DManager.getInstance().getCurrentModel();
    }

    public void onStop() {
        if (view != null) {
            view.close();
        }
        textureManager = null;

        LAppLive2DManager.releaseInstance();
        CubismFramework.dispose();
    }

    public void onDestroy() {
        releaseInstance();
    }

    public void onSurfaceCreated() {
        // テクスチャサンプリング設定
        gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);

        // 透過設定
        gl2.glEnable(GL2.GL_BLEND);
        gl2.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);

        // Initialize Cubism SDK framework
        CubismFramework.initialize();
    }

    public void onSurfaceChanged() {
        // 描画範囲指定
        gl2.glViewport(0, 0, getWindowWidth(), getWindowHeight());

        // AppViewの初期化
        view.initialize();
        view.initializeSprite();

        LAppLive2DManager.createInstance(gl2);
        // load models
        if (LAppLive2DManager.getInstance().getCurrentModel() != currentModel) {
            LAppLive2DManager.getInstance().changeScene(currentModel);
        }

        isActive = true;
    }

    public void run() {
        // 時間更新
        LAppPal.updateTime();

        // 画面初期化
        gl2.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl2.glClearDepthf(1.0f);

        if (view != null) {
            view.render();
        }

        // アプリケーションを非アクティブにする
        if (!isActive) {
            System.exit(0);
        }
    }


    public void onTouchBegan(float x, float y) {
        mouseX = x;
        mouseY = y;

        if (view != null) {
            isCaptured = true;
            view.onTouchesBegan(mouseX, mouseY);
        }
    }

    public void onTouchEnd(float x, float y) {
        mouseX = x;
        mouseY = y;

        if (view != null) {
            isCaptured = false;
            view.onTouchesEnded(mouseX, mouseY);
        }
    }

    public void onTouchMoved(float x, float y) {
        mouseX = x;
        mouseY = y;

        if (isCaptured && view != null) {
            view.onTouchesMoved(mouseX, mouseY);
        }
    }

    // getter, setter群
    public GLAutoDrawable getActivity() {
        return activity;
    }

    public LAppTextureManager getTextureManager() {
        return textureManager;
    }

    public LAppView getView() {
        return view;
    }

    public int getWindowWidth() {
        return activity.getSurfaceWidth() ;
    }

    public int getWindowHeight() {
        return activity.getSurfaceHeight() ;
    }

    private static LAppDelegate s_instance;

    private LAppDelegate(GLAutoDrawable gl2) {
        activity = gl2;
        this.gl2 = gl2.getGL().getGL2();
        currentModel = 0;

        // Set up Cubism SDK framework.
        cubismOption.logFunction = new LAppPal.PrintLogFunction();
        cubismOption.loggingLevel = LAppDefine.cubismLoggingLevel;

        CubismFramework.cleanUp();
        CubismFramework.startUp(cubismOption);
    }

    private GLAutoDrawable activity;

    private final CubismFramework.Option cubismOption = new CubismFramework.Option();

    private LAppTextureManager textureManager;
    private LAppView view;

    private boolean isActive = true;

    /**
     * モデルシーンインデックス
     */
    private int currentModel;

    /**
     * クリックしているか
     */
    private boolean isCaptured;
    /**
     * マウスのX座標
     */
    private float mouseX;
    /**
     * マウスのY座標
     */
    private float mouseY;
}
