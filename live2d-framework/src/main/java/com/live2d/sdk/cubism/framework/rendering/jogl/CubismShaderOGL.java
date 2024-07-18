/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.rendering.jogl;

import com.jogamp.opengl.GL2;
import com.live2d.sdk.cubism.framework.math.CubismMatrix44;
import com.live2d.sdk.cubism.framework.model.CubismModel;
import com.live2d.sdk.cubism.framework.rendering.CubismRenderer;
import com.live2d.sdk.cubism.framework.type.csmRectF;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.jogamp.opengl.GL2.*;
import static com.live2d.sdk.cubism.framework.rendering.jogl.CubismShaderPrograms.*;
import static com.live2d.sdk.cubism.framework.utils.CubismDebug.cubismLogError;

/**
 * This class manage a shader program for Android(OpenGL ES 2.0). This is sinGL2.gleton.
 */
class CubismShaderOGL {
    
    private GL2 GL2;

    CubismShaderOGL(GL2 gl2) {
        this.GL2 = gl2;
    }
    
    /**
     * Tegra processor support. Enable/Disable drawing by extension method.
     *
     * @param extMode   Whether to draw using the extended method.
     * @param extPAMode Enables/disables the PA setting for the extension method.
     */
    public static void setExtShaderMode(boolean extMode, boolean extPAMode) {
        CubismShaderOGL.EXT_MODE = extMode;
        CubismShaderOGL.EXT_PA_MODE = extPAMode;
    }

    /**
     * Get this sinGL2.gleton instance.
     *
     * @return sinGL2.gleton instance
     */
    public static CubismShaderOGL getInstance(GL2 gl2) {
        if (s_instance == null) {
            s_instance = new CubismShaderOGL(gl2);
        }

        return s_instance;
    }

    /**
     * Delete this sinGL2.gleton instance.
     */
    public static void deleteInstance() {
        s_instance = null;
    }

    /**
     * Setup shader program.
     *
     * @param renderer renderer instance
     * @param model    rendered model
     * @param index    target artmesh index
     */
    public void setupShaderProgramForDraw(
        CubismRendererOGL renderer,
        CubismModel model,
        int index
    ) {
        if (shaderSets.isEmpty()) {
            generateShaders();
        }

        // Blending
        int srcColor;
        int dstColor;
        int srcAlpha;
        int dstAlpha;

        // shaderSets用のオフセット計算
        final boolean isMasked = renderer.getClippingContextBufferForDraw() != null;  // この描画オブジェクトはマスク対象か？
        final boolean isInvertedMask = model.getDrawableInvertedMask(index);
        final boolean isPremultipliedAlpha = renderer.isPremultipliedAlpha();

        final int offset = (isMasked ? (isInvertedMask ? 2 : 1) : 0) + (isPremultipliedAlpha ? 3 : 0);

        // シェーダーセット
        CubismShaderSet shaderSet;
        switch (model.getDrawableBlendMode(index)) {
            case NORMAL:
            default:
                shaderSet = this.shaderSets.get(ShaderNames.NORMAL.getId() + offset);
                srcColor = GL_ONE;
                dstColor = GL_ONE_MINUS_SRC_ALPHA;
                srcAlpha = GL_ONE;
                dstAlpha = GL_ONE_MINUS_SRC_ALPHA;
                break;
            case ADDITIVE:
                shaderSet = shaderSets.get(ShaderNames.ADD.getId() + offset);
                srcColor = GL_ONE;
                dstColor = GL_ONE;
                srcAlpha = GL_ZERO;
                dstAlpha = GL_ONE;
                break;
            case MULTIPLICATIVE:
                shaderSet = shaderSets.get(ShaderNames.MULT.getId() + offset);
                srcColor = GL_DST_COLOR;
                dstColor = GL_ONE_MINUS_SRC_ALPHA;
                srcAlpha = GL_ZERO;
                dstAlpha = GL_ONE;
                break;
        }

        GL2.glUseProgram(shaderSet.shaderProgram);

        // キャッシュされたバッファを取得し、実際のデータを格納する。
        CubismDrawableInfoCachesHolder drawableInfoCachesHolder = renderer.getDrawableInfoCachesHolder();
        // vertex array
        FloatBuffer vertexArrayBuffer = drawableInfoCachesHolder.setUpVertexArray(
            index,
            model.getDrawableVertices(index)
        );
        // uv array
        FloatBuffer uvArrayBuffer = drawableInfoCachesHolder.setUpUvArray(
            index,
            model.getDrawableVertexUvs(index)
        );

        // setting of vertex array
        GL2.glEnableVertexAttribArray(shaderSet.attributePositionLocation);
        GL2.glVertexAttribPointer(
            shaderSet.attributePositionLocation,
            2,
            GL_FLOAT,
            false,
            Float.SIZE / Byte.SIZE * 2,
            vertexArrayBuffer
        );

        // setting of texture vertex
        GL2.glEnableVertexAttribArray(shaderSet.attributeTexCoordLocation);
        GL2.glVertexAttribPointer(
            shaderSet.attributeTexCoordLocation,
            2,
            GL_FLOAT,
            false,
            Float.SIZE / Byte.SIZE * 2,
            uvArrayBuffer
        );

        if (isMasked) {
            GL2.glActiveTexture(GL_TEXTURE1);

            // OffscreenSurfaceに描かれたテクスチャ
            int tex = renderer.getMaskBuffer(renderer.getClippingContextBufferForDraw().bufferIndex).getColorBuffer()[0];
            GL2.glBindTexture(GL_TEXTURE_2D, tex);
            GL2.glUniform1i(shaderSet.samplerTexture1Location, 1);

            // set up a matrix to convert View-coordinates to ClippingContext coordinates
            GL2.glUniformMatrix4fv(
                shaderSet.uniformClipMatrixLocation,
                1,
                false,
                renderer.getClippingContextBufferForDraw().matrixForDraw.getArray(),
                0
            );

            // Set used color channel.
            final int channelIndex = renderer.getClippingContextBufferForDraw().layoutChannelIndex;
            CubismRenderer.CubismTextureColor colorChannel = renderer
                .getClippingContextBufferForDraw()
                .getClippingManager()
                .getChannelFlagAsColor(channelIndex);
            GL2.glUniform4f(
                shaderSet.uniformChannelFlagLocation,
                colorChannel.r,
                colorChannel.g,
                colorChannel.b,
                colorChannel.a
            );
        }

        // texture setting
        int textureId = renderer.getBoundTextureId(
            model.getDrawableTextureIndex(index)
        );
        GL2.glActiveTexture(GL_TEXTURE0);
        GL2.glBindTexture(GL_TEXTURE_2D, textureId);
        GL2.glUniform1i(shaderSet.samplerTexture0Location, 0);

        // coordinate transformation
        CubismMatrix44 matrix44 = renderer.getMvpMatrix();
        GL2.glUniformMatrix4fv(
            shaderSet.uniformMatrixLocation,
            1,
            false,
            matrix44.getArray(),
            0
        );

        // ベース色の取得
        CubismRenderer.CubismTextureColor baseColor = renderer.getModelColorWithOpacity(
            model.getDrawableOpacity(index)
        );
        CubismRenderer.CubismTextureColor multiplyColor = model.getMultiplyColor(index);
        CubismRenderer.CubismTextureColor screenColor = model.getScreenColor(index);
        GL2.glUniform4f(
            shaderSet.uniformBaseColorLocation,
            baseColor.r,
            baseColor.g,
            baseColor.b,
            baseColor.a
        );
        GL2.glUniform4f(
            shaderSet.uniformMultiplyColorLocation,
            multiplyColor.r,
            multiplyColor.g,
            multiplyColor.b,
            multiplyColor.a
        );
        GL2.glUniform4f(
            shaderSet.uniformScreenColorLocation,
            screenColor.r,
            screenColor.g,
            screenColor.b,
            screenColor.a
        );

        GL2.glBlendFuncSeparate(srcColor, dstColor, srcAlpha, dstAlpha);
    }

    public void setupShaderProgramForMask(
        CubismRendererOGL renderer,
        CubismModel model,
        int index
    ) {
        if (shaderSets.isEmpty()) {
            generateShaders();
        }

        // Blending
        int srcColor;
        int dstColor;
        int srcAlpha;
        int dstAlpha;

        CubismShaderSet shaderSet = shaderSets.get(ShaderNames.SETUP_MASK.id);
        GL2.glUseProgram(shaderSet.shaderProgram);

        // texture setting
        int textureId = renderer.getBoundTextureId(model.getDrawableTextureIndex(index));
        GL2.glActiveTexture(GL_TEXTURE0);
        GL2.glBindTexture(GL_TEXTURE_2D, textureId);
        GL2.glUniform1i(shaderSet.samplerTexture0Location, 0);

        // キャッシュされたバッファを取得し、実際のデータを格納する。
        CubismDrawableInfoCachesHolder drawableInfoCachesHolder = renderer.getDrawableInfoCachesHolder();
        // vertex array
        FloatBuffer vertexArrayBuffer = drawableInfoCachesHolder.setUpVertexArray(
            index,
            model.getDrawableVertices(index)
        );
        // uv array
        FloatBuffer uvArrayBuffer = drawableInfoCachesHolder.setUpUvArray(
            index,
            model.getDrawableVertexUvs(index)
        );

        // setting of vertex array
        GL2.glEnableVertexAttribArray(shaderSet.attributePositionLocation);
        GL2.glVertexAttribPointer(
            shaderSet.attributePositionLocation,
            2,
            GL_FLOAT,
            false,
            Float.SIZE / Byte.SIZE * 2,
            vertexArrayBuffer
        );

        // setting of texture vertex
        GL2.glEnableVertexAttribArray(shaderSet.attributeTexCoordLocation);
        GL2.glVertexAttribPointer(
            shaderSet.attributeTexCoordLocation,
            2,
            GL_FLOAT,
            false,
            Float.SIZE / Byte.SIZE * 2,
            uvArrayBuffer
        );

        // channels
        final int channelIndex = renderer.getClippingContextBufferForMask().layoutChannelIndex;
        CubismRenderer.CubismTextureColor colorChannel = renderer
            .getClippingContextBufferForMask()
            .getClippingManager()
            .getChannelFlagAsColor(channelIndex);

        GL2.glUniform4f(
            shaderSet.uniformChannelFlagLocation,
            colorChannel.r,
            colorChannel.g,
            colorChannel.b,
            colorChannel.a
        );

        GL2.glUniformMatrix4fv(
            shaderSet.uniformClipMatrixLocation,
            1,
            false,
            renderer.getClippingContextBufferForMask().matrixForMask.getArray(),
            0
        );

        csmRectF rect = renderer.getClippingContextBufferForMask().layoutBounds;

        GL2.glUniform4f(
            shaderSet.uniformBaseColorLocation,
            rect.getX() * 2.0f - 1.0f,
            rect.getY() * 2.0f - 1.0f,
            rect.getRight() * 2.0f - 1.0f,
            rect.getBottom() * 2.0f - 1.0f
        );

        CubismRenderer.CubismTextureColor multiplyColor = model.getMultiplyColor(index);
        CubismRenderer.CubismTextureColor screenColor = model.getScreenColor(index);
        GL2.glUniform4f(
            shaderSet.uniformMultiplyColorLocation,
            multiplyColor.r,
            multiplyColor.g,
            multiplyColor.b,
            multiplyColor.a
        );
        GL2.glUniform4f(
            shaderSet.uniformScreenColorLocation,
            screenColor.r,
            screenColor.g,
            screenColor.b,
            screenColor.a
        );

        srcColor = GL_ZERO;
        dstColor = GL_ONE_MINUS_SRC_COLOR;
        srcAlpha = GL_ZERO;
        dstAlpha = GL_ONE_MINUS_SRC_ALPHA;

        GL2.glBlendFuncSeparate(srcColor, dstColor, srcAlpha, dstAlpha);
    }

    /**
     * SinGL2.gleton instance.
     */
    private static CubismShaderOGL s_instance;

    /**
     * Tegra support. Drawing with Extended Method.
     */
    private static boolean EXT_MODE;
    /**
     * Variable for setting the PA of the extension method.
     */
    private static boolean EXT_PA_MODE;

    /**
     * Shader names
     */
    private enum ShaderNames {
        // Setup Mask
        SETUP_MASK(0),

        // Normal
        NORMAL(1),
        NORMAL_MASKED(2),
        NORMAL_MASKED_INVERTED(3),
        NORMAL_PREMULTIPLIED_ALPHA(4),
        NORMAL_MASKED_PREMULTIPLIED_ALPHA(5),
        NORMAL_MASKED_INVERTED_PREMULTIPLIED_ALPHA(6),

        // Add
        ADD(7),
        ADD_MASKED(8),
        ADD_MASKED_INVERTED(9),
        ADD_PREMULTIPLIED_ALPHA(10),
        ADD_MASKED_PREMULTIPLIED_ALPHA(11),
        ADD_MASKED_PREMULTIPLIED_ALPHA_INVERTED(12),

        // Multi
        MULT(13),
        MULT_MASKED(14),
        MULT_MASKED_INVERTED(15),
        MULT_PREMULTIPLIED_ALPHA(16),
        MULT_MASKED_PREMULTIPLIED_ALPHA(17),
        MULT_MASKED_PREMULTIPLIED_ALPHA_INVERTED(18);

        private final int id;

        ShaderNames(int id) {
            this.id = id;
        }

        private int getId() {
            return id;
        }
    }

    /**
     * Data class that holds the addresses of shader programs and shader variables
     */
    private static class CubismShaderSet {
        /**
         * address of shader program.
         */
        int shaderProgram;
        /**
         * Address of the variable to be passed to the shader program (Position)
         */
        int attributePositionLocation;
        /**
         * Address of the variable to be passed to the shader program (TexCoord)
         */
        int attributeTexCoordLocation;
        /**
         * Address of the variable to be passed to the shader program (Matrix)
         */
        int uniformMatrixLocation;
        /**
         * Address of the variable to be passed to the shader program (ClipMatrix)
         */
        int uniformClipMatrixLocation;
        /**
         * Address of the variable to be passed to the shader program (Texture0)
         */
        int samplerTexture0Location;
        /**
         * Address of the variable to be passed to the shader program (Texture1)
         */
        int samplerTexture1Location;
        /**
         * Address of the variable to be passed to the shader program (BaseColor)
         */
        int uniformBaseColorLocation;
        /**
         * Address of the variable to be passed to the shader program (MultiplyColor)
         */
        int uniformMultiplyColorLocation;
        /**
         * Address of the variable to be passed to the shader program (ScreenColor)
         */
        int uniformScreenColorLocation;
        /**
         * Address of the variable to be passed to the shader program (ChannelFlag)
         */
        int uniformChannelFlagLocation;
    }

    /**
     * Release shader programs.
     */
    private void releaseShaderProgram() {
        for (CubismShaderSet shaderSet : shaderSets) {
            GL2.glDeleteProgram(shaderSet.shaderProgram);
            shaderSet.shaderProgram = 0;
        }
        shaderSets.clear();
    }

    /**
     * Initialize and generate shader programs.
     */
    private void generateShaders() {
        for (int i = 0; i < SHADER_COUNT; i++) {
            shaderSets.add(new CubismShaderSet());
        }

        if (EXT_MODE) {
            shaderSets.get(0).shaderProgram = loadShaderProgram(VERT_SHADER_SRC_SETUP_MASK, FRAG_SHADER_SRC_SETUP_MASK_TEGRA);
            shaderSets.get(1).shaderProgram = loadShaderProgram(VERT_SHADER_SRC, FRAG_SHADER_SRC_TEGRA);
            shaderSets.get(2).shaderProgram = loadShaderProgram(VERT_SHADER_SRC_MASKED, FRAG_SHADER_SRC_MASK_TEGRA);
            shaderSets.get(3).shaderProgram = loadShaderProgram(VERT_SHADER_SRC_MASKED, FRAG_SHADER_SRC_MASK_INVERTED_TEGRA);
            shaderSets.get(4).shaderProgram = loadShaderProgram(VERT_SHADER_SRC, FRAG_SHADER_SRC_PREMULTIPLIED_ALPHA_TEGRA);
            shaderSets.get(5).shaderProgram = loadShaderProgram(VERT_SHADER_SRC_MASKED, FRAG_SHADER_SRC_MASK_PREMULTIPLIED_ALPHA_TEGRA);
            shaderSets.get(6).shaderProgram = loadShaderProgram(VERT_SHADER_SRC_MASKED, FRAG_SHADER_SRC_MASK_INVERTED_PREMULTIPLIED_ALPHA_TEGRA);
        } else {
            shaderSets.get(0).shaderProgram = loadShaderProgram(VERT_SHADER_SRC_SETUP_MASK, FRAG_SHADER_SRC_SETUP_MASK);
            shaderSets.get(1).shaderProgram = loadShaderProgram(VERT_SHADER_SRC, FRAG_SHADER_SRC);
            shaderSets.get(2).shaderProgram = loadShaderProgram(VERT_SHADER_SRC_MASKED, FRAG_SHADER_SRC_MASK);
            shaderSets.get(3).shaderProgram = loadShaderProgram(VERT_SHADER_SRC_MASKED, FRAG_SHADER_SRC_MASK_INVERTED);
            shaderSets.get(4).shaderProgram = loadShaderProgram(VERT_SHADER_SRC, FRAG_SHADER_SRC_PREMULTIPLIED_ALPHA);
            shaderSets.get(5).shaderProgram = loadShaderProgram(VERT_SHADER_SRC_MASKED, FRAG_SHADER_SRC_MASK_PREMULTIPLIED_ALPHA);
            shaderSets.get(6).shaderProgram = loadShaderProgram(VERT_SHADER_SRC_MASKED, FRAG_SHADER_SRC_MASK_INVERTED_PREMULTIPLIED_ALPHA);
        }

        // 加算も通常と同じシェーダーを利用する
        shaderSets.get(7).shaderProgram = shaderSets.get(1).shaderProgram;
        shaderSets.get(8).shaderProgram = shaderSets.get(2).shaderProgram;
        shaderSets.get(9).shaderProgram = shaderSets.get(3).shaderProgram;
        shaderSets.get(10).shaderProgram = shaderSets.get(4).shaderProgram;
        shaderSets.get(11).shaderProgram = shaderSets.get(5).shaderProgram;
        shaderSets.get(12).shaderProgram = shaderSets.get(6).shaderProgram;

        // 乗算も通常と同じシェーダーを利用する
        shaderSets.get(13).shaderProgram = shaderSets.get(1).shaderProgram;
        shaderSets.get(14).shaderProgram = shaderSets.get(2).shaderProgram;
        shaderSets.get(15).shaderProgram = shaderSets.get(3).shaderProgram;
        shaderSets.get(16).shaderProgram = shaderSets.get(4).shaderProgram;
        shaderSets.get(17).shaderProgram = shaderSets.get(5).shaderProgram;
        shaderSets.get(18).shaderProgram = shaderSets.get(6).shaderProgram;

        // Setup mask
        shaderSets.get(0).attributePositionLocation = GL2.glGetAttribLocation(shaderSets.get(0).shaderProgram, "a_position");
        shaderSets.get(0).attributeTexCoordLocation = GL2.glGetAttribLocation(shaderSets.get(0).shaderProgram, "a_texCoord");
        shaderSets.get(0).samplerTexture0Location = GL2.glGetUniformLocation(shaderSets.get(0).shaderProgram, "s_texture0");
        shaderSets.get(0).uniformClipMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(0).shaderProgram, "u_clipMatrix");
        shaderSets.get(0).uniformChannelFlagLocation = GL2.glGetUniformLocation(shaderSets.get(0).shaderProgram, "u_channelFlag");
        shaderSets.get(0).uniformBaseColorLocation = GL2.glGetUniformLocation(shaderSets.get(0).shaderProgram, "u_baseColor");
        shaderSets.get(0).uniformMultiplyColorLocation = GL2.glGetUniformLocation(shaderSets.get(0).shaderProgram, "u_multiplyColor");
        shaderSets.get(0).uniformScreenColorLocation = GL2.glGetUniformLocation(shaderSets.get(0).shaderProgram, "u_screenColor");

        // 通常
        shaderSets.get(1).attributePositionLocation = GL2.glGetAttribLocation(shaderSets.get(1).shaderProgram, "a_position");
        shaderSets.get(1).attributeTexCoordLocation = GL2.glGetAttribLocation(shaderSets.get(1).shaderProgram, "a_texCoord");
        shaderSets.get(1).samplerTexture0Location = GL2.glGetUniformLocation(shaderSets.get(1).shaderProgram, "s_texture0");
        shaderSets.get(1).uniformMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(1).shaderProgram, "u_matrix");
        shaderSets.get(1).uniformBaseColorLocation = GL2.glGetUniformLocation(shaderSets.get(1).shaderProgram, "u_baseColor");
        shaderSets.get(1).uniformMultiplyColorLocation = GL2.glGetUniformLocation(shaderSets.get(1).shaderProgram, "u_multiplyColor");
        shaderSets.get(1).uniformScreenColorLocation = GL2.glGetUniformLocation(shaderSets.get(1).shaderProgram, "u_screenColor");

        // 通常（クリッピング）
        shaderSets.get(2).attributePositionLocation = GL2.glGetAttribLocation(shaderSets.get(2).shaderProgram, "a_position");
        shaderSets.get(2).attributeTexCoordLocation = GL2.glGetAttribLocation(shaderSets.get(2).shaderProgram, "a_texCoord");
        shaderSets.get(2).samplerTexture0Location = GL2.glGetUniformLocation(shaderSets.get(2).shaderProgram, "s_texture0");
        shaderSets.get(2).samplerTexture1Location = GL2.glGetUniformLocation(shaderSets.get(2).shaderProgram, "s_texture1");
        shaderSets.get(2).uniformMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(2).shaderProgram, "u_matrix");
        shaderSets.get(2).uniformClipMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(2).shaderProgram, "u_clipMatrix");
        shaderSets.get(2).uniformChannelFlagLocation = GL2.glGetUniformLocation(shaderSets.get(2).shaderProgram, "u_channelFlag");
        shaderSets.get(2).uniformBaseColorLocation = GL2.glGetUniformLocation(shaderSets.get(2).shaderProgram, "u_baseColor");
        shaderSets.get(2).uniformMultiplyColorLocation = GL2.glGetUniformLocation(shaderSets.get(2).shaderProgram, "u_multiplyColor");
        shaderSets.get(2).uniformScreenColorLocation = GL2.glGetUniformLocation(shaderSets.get(2).shaderProgram, "u_screenColor");

        // 通常（クリッピング・反転）
        shaderSets.get(3).attributePositionLocation = GL2.glGetAttribLocation(shaderSets.get(3).shaderProgram, "a_position");
        shaderSets.get(3).attributeTexCoordLocation = GL2.glGetAttribLocation(shaderSets.get(3).shaderProgram, "a_texCoord");
        shaderSets.get(3).samplerTexture0Location = GL2.glGetUniformLocation(shaderSets.get(3).shaderProgram, "s_texture0");
        shaderSets.get(3).samplerTexture1Location = GL2.glGetUniformLocation(shaderSets.get(3).shaderProgram, "s_texture1");
        shaderSets.get(3).uniformMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(3).shaderProgram, "u_matrix");
        shaderSets.get(3).uniformClipMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(3).shaderProgram, "u_clipMatrix");
        shaderSets.get(3).uniformChannelFlagLocation = GL2.glGetUniformLocation(shaderSets.get(3).shaderProgram, "u_channelFlag");
        shaderSets.get(3).uniformBaseColorLocation = GL2.glGetUniformLocation(shaderSets.get(3).shaderProgram, "u_baseColor");
        shaderSets.get(3).uniformMultiplyColorLocation = GL2.glGetUniformLocation(shaderSets.get(3).shaderProgram, "u_multiplyColor");
        shaderSets.get(3).uniformScreenColorLocation = GL2.glGetUniformLocation(shaderSets.get(3).shaderProgram, "u_screenColor");

        // 通常（PremultipliedAlpha）
        shaderSets.get(4).attributePositionLocation = GL2.glGetAttribLocation(shaderSets.get(4).shaderProgram, "a_position");
        shaderSets.get(4).attributeTexCoordLocation = GL2.glGetAttribLocation(shaderSets.get(4).shaderProgram, "a_texCoord");
        shaderSets.get(4).samplerTexture0Location = GL2.glGetUniformLocation(shaderSets.get(4).shaderProgram, "s_texture0");
        shaderSets.get(4).uniformMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(4).shaderProgram, "u_matrix");
        shaderSets.get(4).uniformBaseColorLocation = GL2.glGetUniformLocation(shaderSets.get(4).shaderProgram, "u_baseColor");
        shaderSets.get(4).uniformMultiplyColorLocation = GL2.glGetUniformLocation(shaderSets.get(4).shaderProgram, "u_multiplyColor");
        shaderSets.get(4).uniformScreenColorLocation = GL2.glGetUniformLocation(shaderSets.get(4).shaderProgram, "u_screenColor");

        // 通常（クリッピング、PremultipliedAlpha）
        shaderSets.get(5).attributePositionLocation = GL2.glGetAttribLocation(shaderSets.get(5).shaderProgram, "a_position");
        shaderSets.get(5).attributeTexCoordLocation = GL2.glGetAttribLocation(shaderSets.get(5).shaderProgram, "a_texCoord");
        shaderSets.get(5).samplerTexture0Location = GL2.glGetUniformLocation(shaderSets.get(5).shaderProgram, "s_texture0");
        shaderSets.get(5).samplerTexture1Location = GL2.glGetUniformLocation(shaderSets.get(5).shaderProgram, "s_texture1");
        shaderSets.get(5).uniformMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(5).shaderProgram, "u_matrix");
        shaderSets.get(5).uniformClipMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(5).shaderProgram, "u_clipMatrix");
        shaderSets.get(5).uniformChannelFlagLocation = GL2.glGetUniformLocation(shaderSets.get(5).shaderProgram, "u_channelFlag");
        shaderSets.get(5).uniformBaseColorLocation = GL2.glGetUniformLocation(shaderSets.get(5).shaderProgram, "u_baseColor");
        shaderSets.get(5).uniformMultiplyColorLocation = GL2.glGetUniformLocation(shaderSets.get(5).shaderProgram, "u_multiplyColor");
        shaderSets.get(5).uniformScreenColorLocation = GL2.glGetUniformLocation(shaderSets.get(5).shaderProgram, "u_screenColor");

        // 通常（クリッピング・反転、PremultipliedAlpha）
        shaderSets.get(6).attributePositionLocation = GL2.glGetAttribLocation(shaderSets.get(6).shaderProgram, "a_position");
        shaderSets.get(6).attributeTexCoordLocation = GL2.glGetAttribLocation(shaderSets.get(6).shaderProgram, "a_texCoord");
        shaderSets.get(6).samplerTexture0Location = GL2.glGetUniformLocation(shaderSets.get(6).shaderProgram, "s_texture0");
        shaderSets.get(6).samplerTexture1Location = GL2.glGetUniformLocation(shaderSets.get(6).shaderProgram, "s_texture1");
        shaderSets.get(6).uniformMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(6).shaderProgram, "u_matrix");
        shaderSets.get(6).uniformClipMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(6).shaderProgram, "u_clipMatrix");
        shaderSets.get(6).uniformChannelFlagLocation = GL2.glGetUniformLocation(shaderSets.get(6).shaderProgram, "u_channelFlag");
        shaderSets.get(6).uniformBaseColorLocation = GL2.glGetUniformLocation(shaderSets.get(6).shaderProgram, "u_baseColor");
        shaderSets.get(6).uniformMultiplyColorLocation = GL2.glGetUniformLocation(shaderSets.get(6).shaderProgram, "u_multiplyColor");
        shaderSets.get(6).uniformScreenColorLocation = GL2.glGetUniformLocation(shaderSets.get(6).shaderProgram, "u_screenColor");

        // 加算
        shaderSets.get(7).attributePositionLocation = GL2.glGetAttribLocation(shaderSets.get(7).shaderProgram, "a_position");
        shaderSets.get(7).attributeTexCoordLocation = GL2.glGetAttribLocation(shaderSets.get(7).shaderProgram, "a_texCoord");
        shaderSets.get(7).samplerTexture0Location = GL2.glGetUniformLocation(shaderSets.get(7).shaderProgram, "s_texture0");
        shaderSets.get(7).uniformMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(7).shaderProgram, "u_matrix");
        shaderSets.get(7).uniformBaseColorLocation = GL2.glGetUniformLocation(shaderSets.get(7).shaderProgram, "u_baseColor");
        shaderSets.get(7).uniformMultiplyColorLocation = GL2.glGetUniformLocation(shaderSets.get(7).shaderProgram, "u_multiplyColor");
        shaderSets.get(7).uniformScreenColorLocation = GL2.glGetUniformLocation(shaderSets.get(7).shaderProgram, "u_screenColor");

        // 加算（クリッピング）
        shaderSets.get(8).attributePositionLocation = GL2.glGetAttribLocation(shaderSets.get(8).shaderProgram, "a_position");
        shaderSets.get(8).attributeTexCoordLocation = GL2.glGetAttribLocation(shaderSets.get(8).shaderProgram, "a_texCoord");
        shaderSets.get(8).samplerTexture0Location = GL2.glGetUniformLocation(shaderSets.get(8).shaderProgram, "s_texture0");
        shaderSets.get(8).samplerTexture1Location = GL2.glGetUniformLocation(shaderSets.get(8).shaderProgram, "s_texture1");
        shaderSets.get(8).uniformMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(8).shaderProgram, "u_matrix");
        shaderSets.get(8).uniformClipMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(8).shaderProgram, "u_clipMatrix");
        shaderSets.get(8).uniformChannelFlagLocation = GL2.glGetUniformLocation(shaderSets.get(8).shaderProgram, "u_channelFlag");
        shaderSets.get(8).uniformBaseColorLocation = GL2.glGetUniformLocation(shaderSets.get(8).shaderProgram, "u_baseColor");
        shaderSets.get(8).uniformMultiplyColorLocation = GL2.glGetUniformLocation(shaderSets.get(8).shaderProgram, "u_multiplyColor");
        shaderSets.get(8).uniformScreenColorLocation = GL2.glGetUniformLocation(shaderSets.get(8).shaderProgram, "u_screenColor");

        // 加算（クリッピング・反転）
        shaderSets.get(9).attributePositionLocation = GL2.glGetAttribLocation(shaderSets.get(9).shaderProgram, "a_position");
        shaderSets.get(9).attributeTexCoordLocation = GL2.glGetAttribLocation(shaderSets.get(9).shaderProgram, "a_texCoord");
        shaderSets.get(9).samplerTexture0Location = GL2.glGetUniformLocation(shaderSets.get(9).shaderProgram, "s_texture0");
        shaderSets.get(9).samplerTexture1Location = GL2.glGetUniformLocation(shaderSets.get(9).shaderProgram, "s_texture1");
        shaderSets.get(9).uniformMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(9).shaderProgram, "u_matrix");
        shaderSets.get(9).uniformClipMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(9).shaderProgram, "u_clipMatrix");
        shaderSets.get(9).uniformChannelFlagLocation = GL2.glGetUniformLocation(shaderSets.get(9).shaderProgram, "u_channelFlag");
        shaderSets.get(9).uniformBaseColorLocation = GL2.glGetUniformLocation(shaderSets.get(9).shaderProgram, "u_baseColor");
        shaderSets.get(9).uniformMultiplyColorLocation = GL2.glGetUniformLocation(shaderSets.get(9).shaderProgram, "u_multiplyColor");
        shaderSets.get(9).uniformScreenColorLocation = GL2.glGetUniformLocation(shaderSets.get(9).shaderProgram, "u_screenColor");

        // 加算（PremultipliedAlpha）
        shaderSets.get(10).attributePositionLocation = GL2.glGetAttribLocation(shaderSets.get(10).shaderProgram, "a_position");
        shaderSets.get(10).attributeTexCoordLocation = GL2.glGetAttribLocation(shaderSets.get(10).shaderProgram, "a_texCoord");
        shaderSets.get(10).samplerTexture0Location = GL2.glGetUniformLocation(shaderSets.get(10).shaderProgram, "s_texture0");
        shaderSets.get(10).uniformMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(10).shaderProgram, "u_matrix");
        shaderSets.get(10).uniformBaseColorLocation = GL2.glGetUniformLocation(shaderSets.get(10).shaderProgram, "u_baseColor");
        shaderSets.get(10).uniformMultiplyColorLocation = GL2.glGetUniformLocation(shaderSets.get(10).shaderProgram, "u_multiplyColor");
        shaderSets.get(10).uniformScreenColorLocation = GL2.glGetUniformLocation(shaderSets.get(10).shaderProgram, "u_screenColor");

        // 加算（クリッピング、PremultipliedAlpha）
        shaderSets.get(11).attributePositionLocation = GL2.glGetAttribLocation(shaderSets.get(11).shaderProgram, "a_position");
        shaderSets.get(11).attributeTexCoordLocation = GL2.glGetAttribLocation(shaderSets.get(11).shaderProgram, "a_texCoord");
        shaderSets.get(11).samplerTexture0Location = GL2.glGetUniformLocation(shaderSets.get(11).shaderProgram, "s_texture0");
        shaderSets.get(11).samplerTexture1Location = GL2.glGetUniformLocation(shaderSets.get(11).shaderProgram, "s_texture1");
        shaderSets.get(11).uniformMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(11).shaderProgram, "u_matrix");
        shaderSets.get(11).uniformClipMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(11).shaderProgram, "u_clipMatrix");
        shaderSets.get(11).uniformChannelFlagLocation = GL2.glGetUniformLocation(shaderSets.get(11).shaderProgram, "u_channelFlag");
        shaderSets.get(11).uniformBaseColorLocation = GL2.glGetUniformLocation(shaderSets.get(11).shaderProgram, "u_baseColor");
        shaderSets.get(11).uniformMultiplyColorLocation = GL2.glGetUniformLocation(shaderSets.get(11).shaderProgram, "u_multiplyColor");
        shaderSets.get(11).uniformScreenColorLocation = GL2.glGetUniformLocation(shaderSets.get(11).shaderProgram, "u_screenColor");

        // 加算（クリッピング・反転、PremultipliedAlpha）
        shaderSets.get(12).attributePositionLocation = GL2.glGetAttribLocation(shaderSets.get(12).shaderProgram, "a_position");
        shaderSets.get(12).attributeTexCoordLocation = GL2.glGetAttribLocation(shaderSets.get(12).shaderProgram, "a_texCoord");
        shaderSets.get(12).samplerTexture0Location = GL2.glGetUniformLocation(shaderSets.get(12).shaderProgram, "s_texture0");
        shaderSets.get(12).samplerTexture1Location = GL2.glGetUniformLocation(shaderSets.get(12).shaderProgram, "s_texture1");
        shaderSets.get(12).uniformMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(12).shaderProgram, "u_matrix");
        shaderSets.get(12).uniformClipMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(12).shaderProgram, "u_clipMatrix");
        shaderSets.get(12).uniformChannelFlagLocation = GL2.glGetUniformLocation(shaderSets.get(12).shaderProgram, "u_channelFlag");
        shaderSets.get(12).uniformBaseColorLocation = GL2.glGetUniformLocation(shaderSets.get(12).shaderProgram, "u_baseColor");
        shaderSets.get(12).uniformMultiplyColorLocation = GL2.glGetUniformLocation(shaderSets.get(12).shaderProgram, "u_multiplyColor");
        shaderSets.get(12).uniformScreenColorLocation = GL2.glGetUniformLocation(shaderSets.get(12).shaderProgram, "u_screenColor");

        // 乗算
        shaderSets.get(13).attributePositionLocation = GL2.glGetAttribLocation(shaderSets.get(13).shaderProgram, "a_position");
        shaderSets.get(13).attributeTexCoordLocation = GL2.glGetAttribLocation(shaderSets.get(13).shaderProgram, "a_texCoord");
        shaderSets.get(13).samplerTexture0Location = GL2.glGetUniformLocation(shaderSets.get(13).shaderProgram, "s_texture0");
        shaderSets.get(13).uniformMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(13).shaderProgram, "u_matrix");
        shaderSets.get(13).uniformBaseColorLocation = GL2.glGetUniformLocation(shaderSets.get(13).shaderProgram, "u_baseColor");
        shaderSets.get(13).uniformMultiplyColorLocation = GL2.glGetUniformLocation(shaderSets.get(13).shaderProgram, "u_multiplyColor");
        shaderSets.get(13).uniformScreenColorLocation = GL2.glGetUniformLocation(shaderSets.get(13).shaderProgram, "u_screenColor");

        // 乗算（クリッピング）
        shaderSets.get(14).attributePositionLocation = GL2.glGetAttribLocation(shaderSets.get(14).shaderProgram, "a_position");
        shaderSets.get(14).attributeTexCoordLocation = GL2.glGetAttribLocation(shaderSets.get(14).shaderProgram, "a_texCoord");
        shaderSets.get(14).samplerTexture0Location = GL2.glGetUniformLocation(shaderSets.get(14).shaderProgram, "s_texture0");
        shaderSets.get(14).samplerTexture1Location = GL2.glGetUniformLocation(shaderSets.get(14).shaderProgram, "s_texture1");
        shaderSets.get(14).uniformMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(14).shaderProgram, "u_matrix");
        shaderSets.get(14).uniformClipMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(14).shaderProgram, "u_clipMatrix");
        shaderSets.get(14).uniformChannelFlagLocation = GL2.glGetUniformLocation(shaderSets.get(14).shaderProgram, "u_channelFlag");
        shaderSets.get(14).uniformBaseColorLocation = GL2.glGetUniformLocation(shaderSets.get(14).shaderProgram, "u_baseColor");
        shaderSets.get(14).uniformMultiplyColorLocation = GL2.glGetUniformLocation(shaderSets.get(14).shaderProgram, "u_multiplyColor");
        shaderSets.get(14).uniformScreenColorLocation = GL2.glGetUniformLocation(shaderSets.get(14).shaderProgram, "u_screenColor");

        // 乗算（クリッピング・反転）
        shaderSets.get(15).attributePositionLocation = GL2.glGetAttribLocation(shaderSets.get(15).shaderProgram, "a_position");
        shaderSets.get(15).attributeTexCoordLocation = GL2.glGetAttribLocation(shaderSets.get(15).shaderProgram, "a_texCoord");
        shaderSets.get(15).samplerTexture0Location = GL2.glGetUniformLocation(shaderSets.get(15).shaderProgram, "s_texture0");
        shaderSets.get(15).samplerTexture1Location = GL2.glGetUniformLocation(shaderSets.get(15).shaderProgram, "s_texture1");
        shaderSets.get(15).uniformMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(15).shaderProgram, "u_matrix");
        shaderSets.get(15).uniformClipMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(15).shaderProgram, "u_clipMatrix");
        shaderSets.get(15).uniformChannelFlagLocation = GL2.glGetUniformLocation(shaderSets.get(15).shaderProgram, "u_channelFlag");
        shaderSets.get(15).uniformBaseColorLocation = GL2.glGetUniformLocation(shaderSets.get(15).shaderProgram, "u_baseColor");
        shaderSets.get(15).uniformMultiplyColorLocation = GL2.glGetUniformLocation(shaderSets.get(15).shaderProgram, "u_multiplyColor");
        shaderSets.get(15).uniformScreenColorLocation = GL2.glGetUniformLocation(shaderSets.get(15).shaderProgram, "u_screenColor");

        // 乗算（PremultipliedAlpha）
        shaderSets.get(16).attributePositionLocation = GL2.glGetAttribLocation(shaderSets.get(16).shaderProgram, "a_position");
        shaderSets.get(16).attributeTexCoordLocation = GL2.glGetAttribLocation(shaderSets.get(16).shaderProgram, "a_texCoord");
        shaderSets.get(16).samplerTexture0Location = GL2.glGetUniformLocation(shaderSets.get(16).shaderProgram, "s_texture0");
        shaderSets.get(16).uniformMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(16).shaderProgram, "u_matrix");
        shaderSets.get(16).uniformBaseColorLocation = GL2.glGetUniformLocation(shaderSets.get(16).shaderProgram, "u_baseColor");
        shaderSets.get(16).uniformMultiplyColorLocation = GL2.glGetUniformLocation(shaderSets.get(16).shaderProgram, "u_multiplyColor");
        shaderSets.get(16).uniformScreenColorLocation = GL2.glGetUniformLocation(shaderSets.get(16).shaderProgram, "u_screenColor");

        // 乗算（クリッピング、PremultipliedAlpha）
        shaderSets.get(17).attributePositionLocation = GL2.glGetAttribLocation(shaderSets.get(17).shaderProgram, "a_position");
        shaderSets.get(17).attributeTexCoordLocation = GL2.glGetAttribLocation(shaderSets.get(17).shaderProgram, "a_texCoord");
        shaderSets.get(17).samplerTexture0Location = GL2.glGetUniformLocation(shaderSets.get(17).shaderProgram, "s_texture0");
        shaderSets.get(17).samplerTexture1Location = GL2.glGetUniformLocation(shaderSets.get(17).shaderProgram, "s_texture1");
        shaderSets.get(17).uniformMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(17).shaderProgram, "u_matrix");
        shaderSets.get(17).uniformClipMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(17).shaderProgram, "u_clipMatrix");
        shaderSets.get(17).uniformChannelFlagLocation = GL2.glGetUniformLocation(shaderSets.get(17).shaderProgram, "u_channelFlag");
        shaderSets.get(17).uniformBaseColorLocation = GL2.glGetUniformLocation(shaderSets.get(17).shaderProgram, "u_baseColor");
        shaderSets.get(17).uniformMultiplyColorLocation = GL2.glGetUniformLocation(shaderSets.get(17).shaderProgram, "u_multiplyColor");
        shaderSets.get(17).uniformScreenColorLocation = GL2.glGetUniformLocation(shaderSets.get(17).shaderProgram, "u_screenColor");

        // 乗算（クリッピング・反転、PremultipliedAlpha）
        shaderSets.get(18).attributePositionLocation = GL2.glGetAttribLocation(shaderSets.get(18).shaderProgram, "a_position");
        shaderSets.get(18).attributeTexCoordLocation = GL2.glGetAttribLocation(shaderSets.get(18).shaderProgram, "a_texCoord");
        shaderSets.get(18).samplerTexture0Location = GL2.glGetUniformLocation(shaderSets.get(18).shaderProgram, "s_texture0");
        shaderSets.get(18).samplerTexture1Location = GL2.glGetUniformLocation(shaderSets.get(18).shaderProgram, "s_texture1");
        shaderSets.get(18).uniformMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(18).shaderProgram, "u_matrix");
        shaderSets.get(18).uniformClipMatrixLocation = GL2.glGetUniformLocation(shaderSets.get(18).shaderProgram, "u_clipMatrix");
        shaderSets.get(18).uniformChannelFlagLocation = GL2.glGetUniformLocation(shaderSets.get(18).shaderProgram, "u_channelFlag");
        shaderSets.get(18).uniformBaseColorLocation = GL2.glGetUniformLocation(shaderSets.get(18).shaderProgram, "u_baseColor");
        shaderSets.get(18).uniformMultiplyColorLocation = GL2.glGetUniformLocation(shaderSets.get(18).shaderProgram, "u_multiplyColor");
        shaderSets.get(18).uniformScreenColorLocation = GL2.glGetUniformLocation(shaderSets.get(18).shaderProgram, "u_screenColor");
    }

    private void setAttribLocation(final int shaderIndex) {
        CubismShaderSet shader = shaderSets.get(shaderIndex);

        shader.attributePositionLocation = GL2.glGetAttribLocation(shader.shaderProgram, "a_position");
        shader.attributeTexCoordLocation = GL2.glGetAttribLocation(shader.shaderProgram, "a_texCoord");
        shader.samplerTexture0Location = GL2.glGetUniformLocation(shader.shaderProgram, "s_texture0");
        shader.uniformMatrixLocation = GL2.glGetUniformLocation(shader.shaderProgram, "u_matrix");
        shader.uniformBaseColorLocation = GL2.glGetUniformLocation(shader.shaderProgram, "u_baseColor");
    }

    private void setAttribLocationClipping(final int shaderIndex) {
        CubismShaderSet shader = shaderSets.get(shaderIndex);

        shader.attributePositionLocation = GL2.glGetAttribLocation(shader.shaderProgram, "a_position");
        shader.attributeTexCoordLocation = GL2.glGetAttribLocation(shader.shaderProgram, "a_texCoord");
        shader.samplerTexture0Location = GL2.glGetUniformLocation(shader.shaderProgram, "s_texture0");
        shader.samplerTexture1Location = GL2.glGetUniformLocation(shader.shaderProgram, "s_texture1");
        shader.uniformMatrixLocation = GL2.glGetUniformLocation(shader.shaderProgram, "u_matrix");
        shader.uniformClipMatrixLocation = GL2.glGetUniformLocation(shader.shaderProgram, "u_clipMatrix");
        shader.uniformChannelFlagLocation = GL2.glGetUniformLocation(shader.shaderProgram, "u_channelFlag");
        shader.uniformBaseColorLocation = GL2.glGetUniformLocation(shader.shaderProgram, "u_baseColor");
    }

    /**
     * Load shader program.
     *
     * @param vertShaderSrc source of vertex shader
     * @param fragShaderSrc source of fragment shader
     * @return reference value to the shader program
     */
    private int loadShaderProgram(final String vertShaderSrc, final String fragShaderSrc) {
        int[] vertShader = new int[1];
        int[] fragShader = new int[1];

        // Create shader program.
        int shaderProgram = GL2.glCreateProgram();

        if (!compileShaderSource(vertShader, GL_VERTEX_SHADER, vertShaderSrc)) {
            cubismLogError("Vertex shader compile error!");
            return 0;
        }

        // Create and compile fragment shader.
        if (!compileShaderSource(fragShader, GL_FRAGMENT_SHADER, fragShaderSrc)) {
            cubismLogError("Fragment shader compile error!");
            return 0;
        }

        // Attach vertex shader to program.
        GL2.glAttachShader(shaderProgram, vertShader[0]);
        // Attach fragment shader to program.
        GL2.glAttachShader(shaderProgram, fragShader[0]);

        // Link program.
        if (!linkProgram(shaderProgram)) {
            cubismLogError("Failed to link program: " + shaderProgram);

            GL2.glDeleteShader(vertShader[0]);
            GL2.glDeleteShader(fragShader[0]);
            GL2.glDeleteProgram(shaderProgram);

            return 0;
        }

        // Release vertex and fragment shaders.
        GL2.glDetachShader(shaderProgram, vertShader[0]);
        GL2.glDeleteShader(vertShader[0]);

        GL2.glDetachShader(shaderProgram, fragShader[0]);
        GL2.glDeleteShader(fragShader[0]);

        return shaderProgram;
    }

    /**
     * Compile shader program.
     *
     * @param shader       reference value to compiled shader program
     * @param shaderType   shader type(Vertex/Fragment)
     * @param shaderSource source of shader program
     * @return If compilling succeeds, return true
     */
    private boolean compileShaderSource(int[] shader, int shaderType, final String shaderSource) {
        if (shader == null || shader.length == 0) {
            return false;
        }

        shader[0] = GL2.glCreateShader(shaderType);

        GL2.glShaderSource(shader[0],1, new String[] { shaderSource }, null);
        GL2.glCompileShader(shader[0]);

        int[] logLength = new int[1];
        GL2.glGetShaderiv(shader[0], GL_INFO_LOG_LENGTH, IntBuffer.wrap(logLength));
        if (logLength[0] > 0) {

            byte[] msg = new byte[logLength[0]];
            GL2.glGetShaderInfoLog(shader[0],logLength[0], logLength,0,msg,0);
            cubismLogError("Shader compile log: " + new String(msg, StandardCharsets.UTF_8));
        }

        int[] status = new int[1];
        GL2.glGetShaderiv(shader[0], GL_COMPILE_STATUS, IntBuffer.wrap(status));
        if (status[0] == GL_FALSE) {
            GL2.glDeleteShader(shader[0]);
            return false;
        }
        return true;
    }

    /**
     * Link shader program.
     *
     * @param shaderProgram reference value to a shader program to link
     * @return If linking succeeds, return true
     */
    private boolean linkProgram(int shaderProgram) {
        GL2.glLinkProgram(shaderProgram);

        int[] logLength = new int[1];
        GL2.glGetProgramiv(shaderProgram, GL_INFO_LOG_LENGTH, IntBuffer.wrap(logLength));
        if (logLength[0] > 0) {
            byte[] msg = new byte[logLength[0]];
            GL2.glGetProgramInfoLog(shaderProgram,logLength[0], logLength,0,msg,0);
            cubismLogError("Program link log: " + new String(msg));
        }

        int[] status = new int[1];
        GL2.glGetProgramiv(shaderProgram, GL_LINK_STATUS, IntBuffer.wrap(status));
        return status[0] != GL_FALSE;
    }

    /**
     * Validate shader program.
     *
     * @param shaderProgram reference value to shader program to be validated
     * @return If there is no problem, return true
     */
    private boolean validateProgram(int shaderProgram) {
        GL2.glValidateProgram(shaderProgram);

        int[] logLength = new int[1];
        GL2.glGetProgramiv(shaderProgram, GL_INFO_LOG_LENGTH, IntBuffer.wrap(logLength));
        if (logLength[0] > 0) {
            byte[] msg = new byte[logLength[0]];
            GL2.glGetProgramInfoLog(shaderProgram,logLength[0], logLength,0,msg,0);
            cubismLogError("Validate program log: " + new String(msg));
        }

        int[] status = new int[1];
        GL2.glGetProgramiv(shaderProgram, GL_VALIDATE_STATUS, IntBuffer.wrap(status));
        return status[0] != GL_FALSE;
    }

    /**
     * Variable that holds the loaded shader program.
     */
    private final List<CubismShaderSet> shaderSets = new ArrayList<CubismShaderSet>();
}
