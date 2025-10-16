package com.live2d.sdk.cubism.sdk.lwjgl;

import com.live2d.sdk.cubism.framework.CubismFramework;
import com.live2d.sdk.cubism.sdk.Live2dAssets;
import com.live2d.sdk.cubism.sdk.Live2dConfigure;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Live2dLWGLTextureManager {

    // 画像情報データクラス
    public static class TextureInfo {
        public int id;  // テクスチャID
        public int width;   // 横幅
        public int height;  // 高さ
        public String filePath; // ファイル名
    }


    private final List<TextureInfo> textures = new ArrayList<>();        // 画像情報のリスト


    private Live2dAssets assets;

    private Live2dConfigure configure;

    public Live2dLWGLTextureManager(Live2dConfigure configure, Live2dAssets assets) {
        this.assets = assets;
        this.configure = configure;
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
        BufferedImage bitmap = null;
        try {
            bitmap = ImageIO.read(assets.getResource(filePath));
            //stream = assetManager.open(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // decodeStreamは乗算済みアルファとして画像を読み込むようである
        //Bitmap bitmap = BitmapFactory.decodeStream(stream);

        // Texture0をアクティブにする
        GL30.glActiveTexture(GL30.GL_TEXTURE0);

        // OpenGLにテクスチャを生成
        int[] textureId = new int[1];
        GL30.glGenTextures(textureId);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, textureId[0]);

        ByteBuffer buffer = bufferedAsTexture(bitmap);
        // メモリ上の2D画像をテクスチャに割り当てる
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA8, bitmap.getWidth(), bitmap.getHeight(),
                0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, buffer);

        // ミップマップを生成する
        GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D);
        // 縮小時の補間設定
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR_MIPMAP_LINEAR);
        // 拡大時の補間設定
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);


        TextureInfo textureInfo = new TextureInfo();
        textureInfo.filePath = filePath;
        textureInfo.width = bitmap.getWidth();
        textureInfo.height = bitmap.getHeight();
        textureInfo.id = textureId[0];

        textures.add(textureInfo);

        // bitmap解放
        //bitmap.recycle();
        bitmap = null;

        if (configure.isDebugLog()) {
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

    public void clearTextures() {
        for (TextureInfo texture : textures) {
            GL30.glDeleteTextures(texture.id);
        }
        textures.clear();
    }

}
