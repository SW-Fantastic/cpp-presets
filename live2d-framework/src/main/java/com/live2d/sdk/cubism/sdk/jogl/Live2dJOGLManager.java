package com.live2d.sdk.cubism.sdk.jogl;

import com.live2d.sdk.cubism.framework.math.CubismMatrix44;
import com.live2d.sdk.cubism.framework.motion.ACubismMotion;
import com.live2d.sdk.cubism.framework.motion.IBeganMotionCallback;
import com.live2d.sdk.cubism.framework.motion.IFinishedMotionCallback;
import com.live2d.sdk.cubism.sdk.*;

import java.io.IOException;
import java.util.*;

public class Live2dJOGLManager {

    private final List<Live2dJOGLModel> models = new ArrayList<>();

    /**
     * 表示するシーンのインデックス値
     */
    private int currentModel;

    /**
     * モデルディレクトリ名
     */
    private final List<String> modelDir = new ArrayList<>();

    // onUpdateメソッドで使用されるキャッシュ変数
    private CubismMatrix44 viewMatrix = CubismMatrix44.create();
    private CubismMatrix44 projection = CubismMatrix44.create();

    private Live2dJOGLDelegate delegate;

    public Live2dJOGLManager(Live2dJOGLDelegate delegate) {
        this.delegate = delegate;
        setUpModel();
        changeScene(0);
    }


    /**
     * 現在のシーンで保持している全てのモデルを解放する
     */
    public void releaseAllModel() {
        for (Live2dJOGLModel model : models) {
            model.deleteModel();
        }
        models.clear();
    }

    /**
     * assets フォルダにあるモデルフォルダ名をセットする
     */
    public void setUpModel() {
        // assetsフォルダの中にあるフォルダ名を全てクロールし、モデルが存在するフォルダを定義する。
        // フォルダはあるが同名の.model3.jsonが見つからなかった場合はリストに含めない。
        modelDir.clear();
        Live2dAssets assets = delegate.getAssets();
        try {
            String[] root = assets.list("");
            for (String subdir: root) {
                String[] files = assets.list(subdir);
                String target = subdir + ".model3.json";
                // フォルダと同名の.model3.jsonがあるか探索する
                for (String file : files) {
                    if (file.equals(target)) {
                        modelDir.add(subdir);
                        break;
                    }
                }
            }
            Collections.sort(modelDir);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    // モデル更新処理及び描画処理を行う
    public void onUpdate() {
        int width = delegate.getViewWidth();
        int height = delegate.getViewHeight();

        for (int i = 0; i < models.size(); i++) {
            Live2dJOGLModel model = models.get(i);

            if (model.getModel() == null) {
                Live2dUtils.printLog("Failed to model.getModel().");
                continue;
            }

            projection.loadIdentity();

            if (model.getModel().getCanvasWidth() > 1.0f && width < height) {
                // 横に長いモデルを縦長ウィンドウに表示する際モデルの横サイズでscaleを算出する
                model.getModelMatrix().setWidth(2.0f);
                projection.scale(1.0f, (float) width / (float) height);
            } else {
                projection.scale(((float) height / (float) width) / 2f , 0.5f);
            }

            // 必要があればここで乗算する
            if (viewMatrix != null) {
                viewMatrix.multiplyByMatrix(projection);
            }

            // モデル1体描画前コール
            delegate.getView().preModelDraw(model);
            model.update();
            model.draw(projection);     // 参照渡しなのでprojectionは変質する
            // モデル1体描画後コール
            delegate.getView().postModelDraw(model);
        }
    }

    /**
     * 画面をドラッグした時の処理
     *
     * @param x 画面のx座標
     * @param y 画面のy座標
     */
    public void onDrag(float x, float y) {
        for (int i = 0; i < models.size(); i++) {
            Live2dJOGLModel model = getModel(i);
            model.setDragging(x, y);
        }
    }

    /**
     * 画面をタップした時の処理
     *
     * @param x 画面のx座標
     * @param y 画面のy座標
     */
    public void onTap(float x, float y) {
        Live2dConfigure configure = delegate.getConfigure();
        if (configure.isDebugLog()) {
            Live2dUtils.printLog("tap point: {" + x + ", y: " + y);
        }

        for (int i = 0; i < models.size(); i++) {
            Live2dJOGLModel model = models.get(i);

            // 頭をタップした場合表情をランダムで再生する
            if (model.hitTest(HitAreaName.HEAD.getId(), x, y)) {
                if (configure.isDebugLog()) {
                    Live2dUtils.printLog("hit area: " + HitAreaName.HEAD.getId());
                }
                model.setRandomExpression();
            }
            // 体をタップした場合ランダムモーションを開始する
            else if (model.hitTest(HitAreaName.BODY.getId(), x, y)) {
                if (configure.isDebugLog()) {
                    Live2dUtils.printLog("hit area: " + HitAreaName.HEAD.getId());
                }

                model.startRandomMotion(MotionGroup.TAP_BODY.getId(), Priority.NORMAL.getPriority(), finishedMotion,beganMotion);
            }
        }
    }

    /**
     * 次のシーンに切り替える
     * サンプルアプリケーションではモデルセットの切り替えを行う
     */
    public void nextScene() {
        final int number = (currentModel + 1) % modelDir.size();
        changeScene(number);
    }

    /**
     * シーンを切り替える
     *
     * @param index 切り替えるシーンインデックス
     */
    public void changeScene(int index) {
        Live2dConfigure configure = delegate.getConfigure();
        currentModel = index;
        if (configure.isDebugLog()) {
            Live2dUtils.printLog("model index: " + currentModel);
        }

        String modelDirName = modelDir.get(index);

        Live2dAssets assets = delegate.getAssets();
        String modelPath = assets.getResource(modelDirName).getAbsolutePath();
        String modelJsonName = modelDirName + ".model3.json";

        releaseAllModel();

        models.add(new Live2dJOGLModel(delegate));
        models.get(0).loadAssets(modelPath, modelJsonName);

        /*
         * モデル半透明表示を行うサンプルを提示する。
         * ここでUSE_RENDER_TARGET、USE_MODEL_RENDER_TARGETが定義されている場合
         * 別のレンダリングターゲットにモデルを描画し、描画結果をテクスチャとして別のスプライトに張り付ける。
         */
        RenderingTarget useRenderingTarget;
        if (configure.isUseRenderTarget()) {
            // LAppViewの持つターゲットに描画を行う場合こちらを選択
            useRenderingTarget = RenderingTarget.VIEW_FRAME_BUFFER;
        } else if (configure.isUserModelRenderTarget()) {
            // 各Live2dJOGLModelの持つターゲットに描画を行う場合こちらを選択
            useRenderingTarget = RenderingTarget.MODEL_FRAME_BUFFER;
        } else {
            // デフォルトのメインフレームバッファへレンダリングする(通常)
            useRenderingTarget = RenderingTarget.NONE;
        }

        if (configure.isUseRenderTarget()|| configure.isUserModelRenderTarget()) {
            // モデル個別にαを付けるサンプルとして、もう1体モデルを作成し少し位置をずらす。
            models.add(new Live2dJOGLModel(delegate));
            models.get(1).loadAssets(modelPath, modelJsonName);
            models.get(1).getModelMatrix().translateX(0.2f);
        }

        // レンダリングターゲットを切り替える
        delegate.getView().switchRenderingTarget(useRenderingTarget);

        // 別レンダリング先を選択した際の背景クリア色
        float[] clearColor = {0.0f, 0.0f, 0.0f};
        delegate.getView().setRenderingTargetClearColor(clearColor[0], clearColor[1], clearColor[2]);
    }

    /**
     * 現在のシーンで保持しているモデルを返す
     *
     * @param number モデルリストのインデックス値
     * @return モデルのインスタンスを返す。インデックス値が範囲外の場合はnullを返す
     */
    public Live2dJOGLModel getModel(int number) {
        if (number < models.size()) {
            return models.get(number);
        }
        return null;
    }

    /**
     * シーンインデックスを返す
     *
     * @return シーンインデックス
     */
    public int getCurrentModel() {
        return currentModel;
    }

    /**
     * Return the number of models in this LAppLive2DManager instance has.
     *
     * @return number fo models in this LAppLive2DManager instance has. If models list is null, return 0.
     */
    public int getModelNum() {
        if (models == null) {
            return 0;
        }
        return models.size();
    }

    /**
     * モーション再生時に実行されるコールバック関数
     */
    private static class BeganMotion implements IBeganMotionCallback {
        @Override
        public void execute(ACubismMotion motion) {
            Live2dUtils.printLog("Motion Began: " + motion);
        }
    }

    private static final BeganMotion beganMotion = new BeganMotion();

    /**
     * モーション終了時に実行されるコールバック関数
     */
    private static class FinishedMotion implements IFinishedMotionCallback {
        @Override
        public void execute(ACubismMotion motion) {
            Live2dUtils.printLog("Motion Finished: " + motion);
        }
    }

    private static final FinishedMotion finishedMotion = new FinishedMotion();





}
