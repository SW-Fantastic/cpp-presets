package com.live2d.sdk.cubism.sdk.jogl;

import com.jogamp.opengl.GL2;
import com.live2d.sdk.cubism.framework.math.CubismMatrix44;
import com.live2d.sdk.cubism.framework.math.CubismViewMatrix;
import com.live2d.sdk.cubism.framework.rendering.jogl.CubismOffscreenSurfaceOGL;
import com.live2d.sdk.cubism.sdk.*;

public class Live2dJOGLView implements AutoCloseable {

    private GL2 gles2;

    private Live2dJOGLDelegate delegate;

    private Live2dJOGLSprite renderingSprite;

    private Live2dSpriteShader spriteShader;

    public Live2dJOGLView(Live2dJOGLDelegate delegate) {
        clearColor[0] = 1.0f;
        clearColor[1] = 1.0f;
        clearColor[2] = 1.0f;
        clearColor[3] = 0.0f;
        this.delegate = delegate;
        this.gles2 = delegate.getGl2();
        renderingBuffer = new CubismOffscreenSurfaceOGL(gles2);
        this.initialize();
    }

    @Override
    public void close() {
        spriteShader.close();
    }

    // ビューを初期化する
    public void initialize() {
        int width = delegate.getViewWidth();
        int height = delegate.getViewHeight();

        float ratio = (float) width / (float) height;
        float left = -ratio;
        float right = ratio;
        float bottom = LogicalView.LEFT.getValue();
        float top = LogicalView.RIGHT.getValue();

        // デバイスに対応する画面範囲。Xの左端、Xの右端、Yの下端、Yの上端
        viewMatrix.setScreenRect(left, right, bottom, top);
        viewMatrix.scale(Scale.DEFAULT.getValue(), Scale.DEFAULT.getValue());

        // 単位行列に初期化
        deviceToScreen.loadIdentity();

        if (width > height) {
            float screenW = Math.abs(right - left);
            deviceToScreen.scaleRelative(screenW / width, -screenW / width);
        } else {
            float screenH = Math.abs(top - bottom);
            deviceToScreen.scaleRelative(screenH / height, -screenH / height);
        }
        deviceToScreen.translateRelative(-width * 0.5f, -height * 0.5f);

        // 表示範囲の設定
        viewMatrix.setMaxScale(Scale.MAX.getValue());   // 限界拡大率
        viewMatrix.setMinScale(Scale.MIN.getValue());   // 限界縮小率

        // 表示できる最大範囲
        viewMatrix.setMaxScreenRect(
                MaxLogicalView.LEFT.getValue(),
                MaxLogicalView.RIGHT.getValue(),
                MaxLogicalView.BOTTOM.getValue(),
                MaxLogicalView.TOP.getValue()
        );

        spriteShader = new Live2dSpriteShader(this.delegate);

    }

    // 画像を初期化する
    public void initializeSprite() {

        int windowWidth = delegate.getViewWidth();
        int windowHeight = delegate.getViewHeight();

        // x,yは画像の中心座標
        float x = windowWidth * 0.5f;
        float y = windowHeight * 0.5f;
        int programId = spriteShader.getShaderId();

        if (renderingSprite == null) {
            renderingSprite = new Live2dJOGLSprite(x, y, windowWidth, windowHeight, 0, programId,gles2);
        } else {
            renderingSprite.resize(x, y, windowWidth, windowHeight);
        }
    }

    // 描画する
    public void render() {

        // 画面サイズを取得する。
        int maxWidth = delegate.getViewWidth();
        int maxHeight = delegate.getViewHeight();

        // モデルの描画
        Live2dJOGLManager live2dManager = delegate.getManager();
        live2dManager.onUpdate();

        // 各モデルが持つ描画ターゲットをテクスチャとする場合
        if (renderingTarget == RenderingTarget.MODEL_FRAME_BUFFER && renderingSprite != null) {
            final float[] uvVertex = {
                    1.0f, 1.0f,
                    0.0f, 1.0f,
                    0.0f, 0.0f,
                    1.0f, 0.0f
            };

            for (int i = 0; i < live2dManager.getModelNum(); i++) {
                Live2dJOGLModel model = live2dManager.getModel(i);
                float alpha = i < 1 ? 1.0f : model.getOpacity();    // 片方のみ不透明度を取得できるようにする。

                renderingSprite.setColor(1.0f, 1.0f, 1.0f, alpha);

                if (model != null) {
                    renderingSprite.setWindowSize(maxWidth, maxHeight);
                    renderingSprite.renderImmediate(model.getRenderingBuffer().getColorBuffer()[0], uvVertex);
                }
            }
        }
    }

    /**
     * モデル1体を描画する直前にコールされる
     *
     * @param refModel モデルデータ
     */
    public void preModelDraw(Live2dJOGLModel refModel) {
        // 別のレンダリングターゲットへ向けて描画する場合の使用するオフスクリーンサーフェス
        CubismOffscreenSurfaceOGL useTarget;

        // 別のレンダリングターゲットへ向けて描画する場合
        if (renderingTarget != RenderingTarget.NONE) {

            // 使用するターゲット
            useTarget = (renderingTarget == RenderingTarget.VIEW_FRAME_BUFFER)
                    ? renderingBuffer
                    : refModel.getRenderingBuffer();

            // 描画ターゲット内部未作成の場合はここで作成
            if (!useTarget.isValid()) {
                int width = delegate.getViewWidth();
                int height = delegate.getViewHeight();

                // モデル描画キャンバス
                useTarget.createOffscreenSurface((int) width, (int) height, null);
            }
            // レンダリング開始
            useTarget.beginDraw(null);
            useTarget.clear(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);   // 背景クリアカラー
        }
    }

    /**
     * モデル1体を描画した直後にコールされる
     *
     * @param refModel モデルデータ
     */
    public void postModelDraw(Live2dJOGLModel refModel) {
        CubismOffscreenSurfaceOGL useTarget = null;

        // 別のレンダリングターゲットへ向けて描画する場合
        if (renderingTarget != RenderingTarget.NONE) {
            // 使用するターゲット
            useTarget = (renderingTarget == RenderingTarget.VIEW_FRAME_BUFFER)
                    ? renderingBuffer
                    : refModel.getRenderingBuffer();

            // レンダリング終了
            useTarget.endDraw();

            // LAppViewの持つフレームバッファを使うなら、スプライトへの描画はこことなる
            if (renderingTarget == RenderingTarget.VIEW_FRAME_BUFFER && renderingSprite != null) {
                final float[] uvVertex = {
                        1.0f, 1.0f,
                        0.0f, 1.0f,
                        0.0f, 0.0f,
                        1.0f, 0.0f
                };
                renderingSprite.setColor(1.0f, 1.0f, 1.0f, getSpriteAlpha(0));

                // 画面サイズを取得する。
                int maxWidth = delegate.getViewWidth();
                int maxHeight = delegate.getViewHeight();

                renderingSprite.setWindowSize(maxWidth, maxHeight);
                renderingSprite.renderImmediate(useTarget.getColorBuffer()[0], uvVertex);
            }
        }
    }

    /**
     * レンダリング先を切り替える
     *
     * @param targetType レンダリング先
     */
    public void switchRenderingTarget(RenderingTarget targetType) {
        renderingTarget = targetType;
    }

    /**
     * タッチされたときに呼ばれる
     *
     * @param pointX スクリーンX座標
     * @param pointY スクリーンY座標
     */
    public void onTouchesBegan(float pointX, float pointY) {
        touchManager.touchesBegan(pointX, pointY);
    }

    /**
     * タッチしているときにポインターが動いたら呼ばれる
     *
     * @param pointX スクリーンX座標
     * @param pointY スクリーンY座標
     */
    public void onTouchesMoved(float pointX, float pointY) {
        float viewX = transformViewX(touchManager.getLastX());
        float viewY = transformViewY(touchManager.getLastY());

        touchManager.touchesMoved(pointX, pointY);

        Live2dJOGLManager manager = delegate.getManager();
        manager.onDrag(viewX, viewY);
    }

    /**
     * タッチが終了したら呼ばれる
     *
     * @param pointX スクリーンX座標
     * @param pointY スクリーンY座標
     */
    public void onTouchesEnded(float pointX, float pointY) {
        // タッチ終了
        Live2dConfigure configure = delegate.getConfigure();
        Live2dJOGLManager live2DManager = delegate.getManager();
        live2DManager.onDrag(0.0f, 0.0f);

        // シングルタップ
        // 論理座標変換した座標を取得
        float x = deviceToScreen.transformX(touchManager.getLastX());
        // 論理座標変換した座標を取得
        float y = deviceToScreen.transformY(touchManager.getLastY());

        if (configure.isDebugTouchLog()) {
            Live2dUtils.printLog("Touches ended x: " + x + ", y:" + y);
        }

        live2DManager.onTap(x, y);
    }

    /**
     * X座標をView座標に変換する
     *
     * @param deviceX デバイスX座標
     * @return ViewX座標
     */
    public float transformViewX(float deviceX) {
        // 論理座標変換した座標を取得
        float screenX = deviceToScreen.transformX(deviceX);
        // 拡大、縮小、移動後の値
        return viewMatrix.invertTransformX(screenX);
    }

    /**
     * Y座標をView座標に変換する
     *
     * @param deviceY デバイスY座標
     * @return ViewY座標
     */
    public float transformViewY(float deviceY) {
        // 論理座標変換した座標を取得
        float screenY = deviceToScreen.transformY(deviceY);
        // 拡大、縮小、移動後の値
        return viewMatrix.invertTransformX(screenY);
    }

    /**
     * X座標をScreen座標に変換する
     *
     * @param deviceX デバイスX座標
     * @return ScreenX座標
     */
    public float transformScreenX(float deviceX) {
        return deviceToScreen.transformX(deviceX);
    }

    /**
     * Y座標をScreen座標に変換する
     *
     * @param deviceY デバイスY座標
     * @return ScreenY座標
     */
    public float transformScreenY(float deviceY) {
        return deviceToScreen.transformX(deviceY);
    }

    /**
     * レンダリング先をデフォルト以外に切り替えた際の背景クリア色設定
     *
     * @param r 赤(0.0~1.0)
     * @param g 緑(0.0~1.0)
     * @param b 青(0.0~1.0)
     */
    public void setRenderingTargetClearColor(float r, float g, float b) {
        clearColor[0] = r;
        clearColor[1] = g;
        clearColor[2] = b;
    }

    /**
     * 別レンダリングターゲットにモデルを描画するサンプルで描画時のαを決定する
     *
     * @param assign
     * @return
     */
    public float getSpriteAlpha(int assign) {
        // assignの数値に応じて適当な差をつける
        float alpha = 0.25f + (float) assign * 0.5f;

        // サンプルとしてαに適当な差をつける
        if (alpha > 1.0f) {
            alpha = 1.0f;
        }
        if (alpha < 0.1f) {
            alpha = 0.1f;
        }
        return alpha;
    }

    /**
     * Return rendering target enum instance.
     *
     * @return rendering target
     */
    public RenderingTarget getRenderingTarget() {
        return renderingTarget;
    }

    private final CubismMatrix44 deviceToScreen = CubismMatrix44.create(); // デバイス座標からスクリーン座標に変換するための行列
    private final CubismViewMatrix viewMatrix = new CubismViewMatrix();   // 画面表示の拡縮や移動の変換を行う行列
    private int windowWidth;
    private int windowHeight;

    /**
     * レンダリング先の選択肢
     */
    private RenderingTarget renderingTarget = RenderingTarget.NONE;
    /**
     * レンダリングターゲットのクリアカラー
     */
    private final float[] clearColor = new float[4];

    private CubismOffscreenSurfaceOGL renderingBuffer ;


    /**
     * モデルの切り替えフラグ
     */
    private boolean isChangedModel;

    private final TouchManager touchManager = new TouchManager();


}
