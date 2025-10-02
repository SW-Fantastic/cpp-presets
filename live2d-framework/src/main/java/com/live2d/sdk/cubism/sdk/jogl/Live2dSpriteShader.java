package com.live2d.sdk.cubism.sdk.jogl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLES2;
import com.live2d.sdk.cubism.framework.utils.CubismDebug;
import com.live2d.sdk.cubism.sdk.Live2dAssets;

import java.io.File;


/**
 * スプライト用のシェーダー設定を保持するクラス
 */
public class Live2dSpriteShader implements AutoCloseable {

    private GL2 gles2;

    private Live2dJOGLDelegate delegate;

    /**
     * コンストラクタ
     */
    public Live2dSpriteShader(Live2dJOGLDelegate delegate) {
        this.gles2 = delegate.getGl2();
        programId = createShader(delegate);
    }

    @Override
    public void close() {
        gles2.glDeleteShader(programId);
    }

    /**
     * シェーダーIDを取得する。
     *
     * @return シェーダーID
     */
    public int getShaderId() {
        return programId;
    }

    /**
     * シェーダーを作成する。
     *
     * @return シェーダーID。正常に作成できなかった場合は0を返す。
     */
    private int createShader(Live2dJOGLDelegate delegate) {
        Live2dAssets assets = delegate.getAssets();
        // シェーダーのパスの作成
        File fragShaderFile = assets.getResource("Shaders/FragSprite.frag");
        File vertShaderFile = assets.getResource("Shaders/VertSprite.vert");

        // シェーダーのコンパイル
        int vertexShaderId = compileShader(delegate,vertShaderFile.getAbsolutePath(), GLES2.GL_VERTEX_SHADER);
        int fragmentShaderId = compileShader(delegate,fragShaderFile.getAbsolutePath(), GLES2.GL_FRAGMENT_SHADER);

        if (vertexShaderId == 0 || fragmentShaderId == 0) {
            return 0;
        }

        // プログラムオブジェクトの作成
        int programId = gles2.glCreateProgram();

        // Programのシェーダーを設定
        gles2.glAttachShader(programId, vertexShaderId);
        gles2.glAttachShader(programId, fragmentShaderId);

        gles2.glLinkProgram(programId);
        gles2.glUseProgram(programId);

        // 不要になったシェーダーオブジェクトの削除
        gles2.glDeleteShader(vertexShaderId);
        gles2.glDeleteShader(fragmentShaderId);

        return programId;
    }

    /**
     * CreateShader内部関数。エラーチェックを行う。
     *
     * @param shaderId シェーダーID
     * @return エラーチェック結果。trueの場合、エラーなし。
     */
    private boolean checkShader(int shaderId) {
        int[] logLength = new int[1];
        gles2.glGetShaderiv(shaderId, GLES2.GL_INFO_LOG_LENGTH, logLength, 0);

        if (logLength[0] > 0) {
            byte[] logBuf = new byte[logLength[0]];
            gles2.glGetShaderInfoLog(shaderId,logBuf.length,logLength,0,logBuf,0);
            CubismDebug.cubismLogError("Shader compile log: %s", new String(logBuf));
        }

        int[] status = new int[1];
        gles2.glGetShaderiv(shaderId, GLES2.GL_COMPILE_STATUS, status, 0);

        if (status[0] == GLES2.GL_FALSE) {
            gles2.glDeleteShader(shaderId);
            return false;
        }

        return true;
    }


    /**
     * シェーダーをコンパイルする。
     * コンパイルに成功したら0を返す。
     *
     * @param fileName シェーダーファイル名
     * @param shaderType 作成するシェーダーの種類
     * @return シェーダーID。正常に作成できなかった場合は0を返す。
     */
    private int compileShader(Live2dJOGLDelegate delegate,String fileName, int shaderType) {
        // ファイル読み込み
        byte[] shaderBuffer = delegate.getAssets().loadResource(fileName);
        // コンパイル
        int shaderId = gles2.glCreateShader(shaderType);
        String shaderStr = new String(shaderBuffer);
        gles2.glShaderSource(shaderId,1, new String[] { shaderStr }, new int[] { shaderStr.length() }, 0);
        gles2.glCompileShader(shaderId);

        if (!checkShader(shaderId)) {
            return 0;
        }

        return shaderId;
    }

    private final int programId; // シェーダーID
}

