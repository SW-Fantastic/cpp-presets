package com.live2d.sdk.demo;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLES2;
import com.live2d.sdk.cubism.framework.CubismFramework;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

// テクスチャの管理を行うクラス
public class LAppTextureManager {


    // 画像情報データクラス
    public static class TextureInfo {
        public int id;  // テクスチャID
        public int width;   // 横幅
        public int height;  // 高さ
        public String filePath; // ファイル名
    }

    private GL2 gles2;

    public LAppTextureManager(GL2 gles2) {
        this.gles2 = gles2;
    }

    // 画像読み込み
    // imageFileOffset: glGenTexturesで作成したテクスチャの保存場所
    public TextureInfo createTextureFromPngFile(String filePath) {
        // search loaded texture already
        for (TextureInfo textureInfo : textures) {
            if (textureInfo.filePath.equals(filePath)) {
                return textureInfo;
            }
        }

        // assetsフォルダの画像からビットマップを作成する
        AssetManager assetManager = LAppDelegate.getInstance().getAssets();
        BufferedImage bitmap = null;
        try {
            bitmap = ImageIO.read(assetManager.open(filePath));
            //stream = assetManager.open(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // decodeStreamは乗算済みアルファとして画像を読み込むようである
        //Bitmap bitmap = BitmapFactory.decodeStream(stream);

        // Texture0をアクティブにする
        gles2.glActiveTexture(GLES2.GL_TEXTURE0);

        // OpenGLにテクスチャを生成
        int[] textureId = new int[1];
        gles2.glGenTextures(1, textureId, 0);
        gles2.glBindTexture(GLES2.GL_TEXTURE_2D, textureId[0]);

        ByteBuffer buffer = bufferedAsTexture(bitmap);
        // メモリ上の2D画像をテクスチャに割り当てる
        gles2.glTexImage2D(GLES2.GL_TEXTURE_2D, 0, GLES2.GL_RGBA8, bitmap.getWidth(), bitmap.getHeight(),
                0, GLES2.GL_RGBA, GLES2.GL_UNSIGNED_BYTE, buffer);

        // ミップマップを生成する
        gles2.glGenerateMipmap(GLES2.GL_TEXTURE_2D);
        // 縮小時の補間設定
        gles2.glTexParameteri(GLES2.GL_TEXTURE_2D, GLES2.GL_TEXTURE_MIN_FILTER, GLES2.GL_LINEAR_MIPMAP_LINEAR);
        // 拡大時の補間設定
        gles2.glTexParameteri(GLES2.GL_TEXTURE_2D, GLES2.GL_TEXTURE_MAG_FILTER, GLES2.GL_LINEAR);


        TextureInfo textureInfo = new TextureInfo();
        textureInfo.filePath = filePath;
        textureInfo.width = bitmap.getWidth();
        textureInfo.height = bitmap.getHeight();
        textureInfo.id = textureId[0];

        textures.add(textureInfo);

        // bitmap解放
        //bitmap.recycle();
        bitmap = null;

        if (LAppDefine.DEBUG_LOG_ENABLE) {
            CubismFramework.coreLogFunction("Create texture: " + filePath);
        }

        return textureInfo;
    }

    public static ByteBuffer bufferedAsTexture(BufferedImage image) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        ByteBuffer buffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * 4);

        for(int h = 0; h < image.getHeight(); h++) {
            for(int w = 0; w < image.getWidth(); w++) {
                int pixel = pixels[h * image.getWidth() + w];

                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }

        buffer.flip();
        return buffer;
    }

    private final List<TextureInfo> textures = new ArrayList<>();        // 画像情報のリスト
}
