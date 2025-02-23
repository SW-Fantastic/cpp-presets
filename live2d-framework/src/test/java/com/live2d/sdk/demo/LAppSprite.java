package com.live2d.sdk.demo;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLES2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class LAppSprite {
    
    private GL2 gles2;
    
    public LAppSprite(
            float x,
            float y,
            float width,
            float height,
            int textureId,
            int programId,
            GL2 gles2
    ) {
        this.gles2 = gles2;
        
        rect.left = x - width * 0.5f;
        rect.right = x + width * 0.5f;
        rect.up = y + height * 0.5f;
        rect.down = y - height * 0.5f;

        this.textureId = textureId;

        // 何番目のattribute変数か
        positionLocation = gles2.glGetAttribLocation(programId, "position");
        uvLocation = gles2.glGetAttribLocation(programId, "uv");
        textureLocation = gles2.glGetUniformLocation(programId, "texture");
        colorLocation = gles2.glGetUniformLocation(programId, "baseColor");

        spriteColor[0] = 1.0f;
        spriteColor[1] = 1.0f;
        spriteColor[2] = 1.0f;
        spriteColor[3] = 1.0f;
    }

    public void render() {
        // Set the camera position (View matrix)
        uvVertex[0] = 1.0f;
        uvVertex[1] = 0.0f;
        uvVertex[2] = 0.0f;
        uvVertex[3] = 0.0f;
        uvVertex[4] = 0.0f;
        uvVertex[5] = 1.0f;
        uvVertex[6] = 1.0f;
        uvVertex[7] = 1.0f;


        gles2.glEnableVertexAttribArray(positionLocation);
        gles2.glEnableVertexAttribArray(uvLocation);

        gles2.glUniform1i(textureLocation, 0);

        // 頂点データ
        positionVertex[0] = (rect.right - maxWidth * 0.5f) / (maxWidth * 0.5f);
        positionVertex[1] = (rect.up - maxHeight * 0.5f) / (maxHeight * 0.5f);
        positionVertex[2] = (rect.left - maxWidth * 0.5f) / (maxWidth * 0.5f);
        positionVertex[3] = (rect.up - maxHeight * 0.5f) / (maxHeight * 0.5f);
        positionVertex[4] = (rect.left - maxWidth * 0.5f) / (maxWidth * 0.5f);
        positionVertex[5] = (rect.down - maxHeight * 0.5f) / (maxHeight * 0.5f);
        positionVertex[6] = (rect.right - maxWidth * 0.5f) / (maxWidth * 0.5f);
        positionVertex[7] = (rect.down - maxHeight * 0.5f) / (maxHeight * 0.5f);

        if (posVertexFloatBuffer == null) {
            ByteBuffer posVertexByteBuffer = ByteBuffer.allocateDirect(positionVertex.length * 4);
            posVertexByteBuffer.order(ByteOrder.nativeOrder());
            posVertexFloatBuffer = posVertexByteBuffer.asFloatBuffer();
        }
        if (uvVertexFloatBuffer == null) {
            ByteBuffer uvVertexByteBuffer = ByteBuffer.allocateDirect(uvVertex.length * 4);
            uvVertexByteBuffer.order(ByteOrder.nativeOrder());
            uvVertexFloatBuffer = uvVertexByteBuffer.asFloatBuffer();
        }
        posVertexFloatBuffer.put(positionVertex).position(0);
        uvVertexFloatBuffer.put(uvVertex).position(0);

        gles2.glVertexAttribPointer(positionLocation, 2, GLES2.GL_FLOAT, false, 0, posVertexFloatBuffer);
        gles2.glVertexAttribPointer(uvLocation, 2, GLES2.GL_FLOAT, false, 0, uvVertexFloatBuffer);

        gles2.glUniform4f(colorLocation, spriteColor[0], spriteColor[1], spriteColor[2], spriteColor[3]);

        gles2.glBindTexture(GLES2.GL_TEXTURE_2D, textureId);
        gles2.glDrawArrays(GLES2.GL_TRIANGLE_FAN, 0, 4);
    }

    private final float[] uvVertex = new float[8];
    private final float[] positionVertex = new float[8];

    private FloatBuffer posVertexFloatBuffer;
    private FloatBuffer uvVertexFloatBuffer;

    /**
     * テクスチャIDを指定して描画する
     *
     * @param textureId テクスチャID
     * @param uvVertex uv頂点座標
     */
    public void renderImmediate(int textureId, final float[] uvVertex) {
        // attribute属性を有効にする
        gles2.glEnableVertexAttribArray(positionLocation);
        gles2.glEnableVertexAttribArray(uvLocation);

        // uniform属性の登録
        gles2.glUniform1i(textureLocation, 0);

        // 頂点データ
        float[] positionVertex = {
                (rect.right - maxWidth * 0.5f) / (maxWidth * 0.5f), (rect.up - maxHeight * 0.5f) / (maxHeight * 0.5f),
                (rect.left - maxWidth * 0.5f) / (maxWidth * 0.5f), (rect.up - maxHeight * 0.5f) / (maxHeight * 0.5f),
                (rect.left - maxWidth * 0.5f) / (maxWidth * 0.5f), (rect.down - maxHeight * 0.5f) / (maxHeight * 0.5f),
                (rect.right - maxWidth * 0.5f) / (maxWidth * 0.5f), (rect.down - maxHeight * 0.5f) / (maxHeight * 0.5f)
        };

        // attribute属性を登録
        {
            ByteBuffer bb = ByteBuffer.allocateDirect(positionVertex.length * 4);
            bb.order(ByteOrder.nativeOrder());
            FloatBuffer buffer = bb.asFloatBuffer();
            buffer.put(positionVertex);
            buffer.position(0);

            gles2.glVertexAttribPointer(positionLocation, 2, GLES2.GL_FLOAT, false, 0, buffer);
        }
        {
            ByteBuffer bb = ByteBuffer.allocateDirect(uvVertex.length * 4);
            bb.order(ByteOrder.nativeOrder());
            FloatBuffer buffer = bb.asFloatBuffer();
            buffer.put(uvVertex);
            buffer.position(0);

            gles2.glVertexAttribPointer(uvLocation, 2, GLES2.GL_FLOAT, false, 0, buffer);
        }

        gles2.glUniform4f(colorLocation, spriteColor[0], spriteColor[1], spriteColor[2], spriteColor[3]);

        // モデルの描画
        gles2.glBindTexture(GLES2.GL_TEXTURE_2D, textureId);
        gles2.glDrawArrays(GLES2.GL_TRIANGLE_FAN, 0, 4);
    }

    // リサイズする
    public void resize(float x, float y, float width, float height) {
        rect.left = x - width * 0.5f;
        rect.right = x + width * 0.5f;
        rect.up = y + height * 0.5f;
        rect.down = y - height * 0.5f;
    }

    /**
     * 画像との当たり判定を行う
     *
     * @param pointX タッチした点のx座標
     * @param pointY タッチした点のy座標
     * @return 当たっていればtrue
     */
    public boolean isHit(float pointX, float pointY) {
        // y座標は変換する必要あり
        float y = maxHeight - pointY;

        return (pointX >= rect.left && pointX <= rect.right && y <= rect.up && y >= rect.down);
    }

    public void setColor(float r, float g, float b, float a) {
        spriteColor[0] = r;
        spriteColor[1] = g;
        spriteColor[2] = b;
        spriteColor[3] = a;
    }

    /**
     * ウィンドウサイズを設定する。
     *
     * @param width 横幅
     * @param height 高さ
     */
    public void setWindowSize(int width, int height) {
        maxWidth = width;
        maxHeight = height;
    }

    /**
     * Rectクラス
     */
    private static class Rect {
        /**
         * 左辺
         */
        public float left;
        /**
         * 右辺
         */
        public float right;
        /**
         * 上辺
         */
        public float up;
        /**
         * 下辺
         */
        public float down;
    }


    private final Rect rect = new Rect();
    private final int textureId;

    private final int positionLocation;  // 位置アトリビュート
    private final int uvLocation; // UVアトリビュート
    private final int textureLocation;   // テクスチャアトリビュート
    private final int colorLocation;     // カラーアトリビュート
    private final float[] spriteColor = new float[4];   // 表示カラー

    private int maxWidth;   // ウィンドウ幅
    private int maxHeight;  // ウィンドウ高さ
}
