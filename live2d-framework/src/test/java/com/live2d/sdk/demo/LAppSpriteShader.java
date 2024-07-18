package com.live2d.sdk.demo;

import com.jogamp.opengl.*;
import com.live2d.sdk.cubism.framework.utils.CubismDebug;


/**
 * スプライト用のシェーダー設定を保持するクラス
 */
public class LAppSpriteShader implements AutoCloseable {
    
    private GL2 gles2;
    
    /**
     * コンストラクタ
     */
    public LAppSpriteShader(GL2 gles2) {
        this.gles2 = gles2;
        programId = createShader();
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
    private int createShader() {
        // シェーダーのパスの作成
        String vertShaderFile = LAppDefine.ResourcePath.SHADER_ROOT.getPath();
        vertShaderFile += ("/" + LAppDefine.ResourcePath.VERT_SHADER.getPath());

        String fragShaderFile = LAppDefine.ResourcePath.SHADER_ROOT.getPath();
        fragShaderFile += ("/" + LAppDefine.ResourcePath.FRAG_SHADER.getPath());

        // シェーダーのコンパイル
        int vertexShaderId = compileShader(vertShaderFile, GLES2.GL_VERTEX_SHADER);
        int fragmentShaderId = compileShader(fragShaderFile, GLES2.GL_FRAGMENT_SHADER);

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
    private int compileShader(String fileName, int shaderType) {
        // ファイル読み込み
        byte[] shaderBuffer = LAppPal.loadFileAsBytes(fileName);

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

