package com.live2d.sdk.cubism.sdk.lwjgl;

import com.jogamp.opengl.GLES2;
import com.live2d.sdk.cubism.framework.utils.CubismDebug;
import com.live2d.sdk.cubism.sdk.Live2dAssets;
import org.lwjgl.opengl.GL30;

import java.io.File;
import java.nio.ByteBuffer;


/**
 * スプライト用のシェーダー設定を保持するクラス
 */
public class Live2dLWGLSpriteShader implements AutoCloseable {

    private Live2dLWGLDelegate delegate;

    /**
     * コンストラクタ
     */
    public Live2dLWGLSpriteShader(Live2dLWGLDelegate delegate) {
        programId = createShader(delegate);
    }

    @Override
    public void close() {
        GL30.glDeleteShader(programId);
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
    private int createShader(Live2dLWGLDelegate delegate) {
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
        int programId = GL30.glCreateProgram();

        // Programのシェーダーを設定
        GL30.glAttachShader(programId, vertexShaderId);
        GL30.glAttachShader(programId, fragmentShaderId);

        GL30.glLinkProgram(programId);
        GL30.glUseProgram(programId);

        // 不要になったシェーダーオブジェクトの削除
        GL30.glDeleteShader(vertexShaderId);
        GL30.glDeleteShader(fragmentShaderId);

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
        GL30.glGetShaderiv(shaderId, GLES2.GL_INFO_LOG_LENGTH, logLength);

        if (logLength[0] > 0) {

            byte[] logBuf = new byte[logLength[0]];
            ByteBuffer logBuffer = ByteBuffer.allocate(logLength[0]);
            GL30.glGetShaderInfoLog(shaderId,logLength,logBuffer);
            logBuffer.get(logBuf);
            CubismDebug.cubismLogError("Shader compile log: %s", new String(logBuf));

        }

        int[] status = new int[1];
        GL30.glGetShaderiv(shaderId, GL30.GL_COMPILE_STATUS, status);

        if (status[0] == GLES2.GL_FALSE) {
            GL30.glDeleteShader(shaderId);
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
    private int compileShader(Live2dLWGLDelegate delegate, String fileName, int shaderType) {
        // ファイル読み込み
        byte[] shaderBuffer = delegate.getAssets().loadResource(fileName);
        // コンパイル
        int shaderId = GL30.glCreateShader(shaderType);
        String shaderStr = new String(shaderBuffer);
        GL30.glShaderSource(shaderId,new String[] { shaderStr });
        GL30.glCompileShader(shaderId);

        if (!checkShader(shaderId)) {
            return 0;
        }

        return shaderId;
    }

    private final int programId; // シェーダーID
}

