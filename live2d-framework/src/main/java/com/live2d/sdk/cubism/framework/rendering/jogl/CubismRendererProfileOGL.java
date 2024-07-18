/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.rendering.jogl;

import com.jogamp.opengl.GL2;

import static com.jogamp.opengl.GL2.*;

/**
 * Class that saves and restores the OpenGL ES 2.0 state just before drawing the Cubism model.
 */
class CubismRendererProfileOGL {

    private GL2 GL2;

    CubismRendererProfileOGL(GL2 gl2) {
        this.GL2 = gl2;
    }

    /**
     * Save OpenGL ES 2.0 state.
     */
    public void save() {
        //-- push state --
        GL2.glGetIntegerv(GL_ARRAY_BUFFER_BINDING, lastArrayBufferBinding, 0);
        GL2.glGetIntegerv(GL_ELEMENT_ARRAY_BUFFER_BINDING, lastElementArrayBufferBinding, 0);
        GL2.glGetIntegerv(GL_CURRENT_PROGRAM, lastProgram, 0);

        GL2.glGetIntegerv(GL_ACTIVE_TEXTURE, lastActiveTexture, 0);

        // Activate Texture Unit1 (It is the target to be set thereafter)
        GL2.glActiveTexture(GL_TEXTURE1);
        GL2.glGetIntegerv(GL_TEXTURE_BINDING_2D, lastTexture1Binding2D, 0);

        // Activate Texture Unit0 (It is the target to be set thereafter)
        GL2.glActiveTexture(GL_TEXTURE0);
        GL2.glGetIntegerv(GL_TEXTURE_BINDING_2D, lastTexture0Binding2D, 0);

        GL2.glGetVertexAttribiv(0, GL_VERTEX_ATTRIB_ARRAY_ENABLED, lastVertexAttribArrayEnabled[0], 0);
        GL2.glGetVertexAttribiv(1, GL_VERTEX_ATTRIB_ARRAY_ENABLED, lastVertexAttribArrayEnabled[1], 0);
        GL2.glGetVertexAttribiv(2, GL_VERTEX_ATTRIB_ARRAY_ENABLED, lastVertexAttribArrayEnabled[2], 0);
        GL2.glGetVertexAttribiv(3, GL_VERTEX_ATTRIB_ARRAY_ENABLED, lastVertexAttribArrayEnabled[3], 0);

        lastScissorTest = GL2.glIsEnabled(GL_SCISSOR_TEST);
        lastStencilTest = GL2.glIsEnabled(GL_STENCIL_TEST);
        lastDepthTest = GL2.glIsEnabled(GL_DEPTH_TEST);
        lastCullFace = GL2.glIsEnabled(GL_CULL_FACE);
        lastBlend = GL2.glIsEnabled(GL_BLEND);

        GL2.glGetIntegerv(GL_FRONT_FACE, lastFrontFace, 0);

        byte[] lastColorMsk = new byte[4];
        GL2.glGetBooleanv(GL_COLOR_WRITEMASK, lastColorMsk, 0);
        for (int i = 0; i < lastColorMsk.length ; i++) {
            lastColorMask[i] = lastColorMsk[i] == GL2.GL_TRUE;
        }

        // backup blending
        GL2.glGetIntegerv(GL_BLEND_SRC_RGB, lastBlendingSrcRGB, 0);
        GL2.glGetIntegerv(GL_BLEND_DST_RGB, lastBlendingDstRGB, 0);
        GL2.glGetIntegerv(GL_BLEND_SRC_ALPHA, lastBlendingSrcAlpha, 0);
        GL2.glGetIntegerv(GL_BLEND_DST_ALPHA, lastBlendingDstAlpha, 0);

        // Save the FBO and viewport just before drawing the model.
        GL2.glGetIntegerv(GL_FRAMEBUFFER_BINDING, lastFBO, 0);
        GL2.glGetIntegerv(GL_VIEWPORT, lastViewport, 0);
    }

    /**
     * Restore OpenGL ES 2.0 state which is saved.
     */
    public void restore() {
        GL2.glUseProgram(lastProgram[0]);

        setGlEnableVertexAttribArray(0, lastVertexAttribArrayEnabled[0][0]);
        setGlEnableVertexAttribArray(1, lastVertexAttribArrayEnabled[1][0]);
        setGlEnableVertexAttribArray(2, lastVertexAttribArrayEnabled[2][0]);
        setGlEnableVertexAttribArray(3, lastVertexAttribArrayEnabled[3][0]);

        setGlEnable(GL_SCISSOR_TEST, lastScissorTest);
        setGlEnable(GL_STENCIL_TEST, lastStencilTest);
        setGlEnable(GL_DEPTH_TEST, lastDepthTest);
        setGlEnable(GL_CULL_FACE, lastCullFace);
        setGlEnable(GL_BLEND, lastBlend);

        GL2.glFrontFace(lastFrontFace[0]);

        GL2.glColorMask(
            lastColorMask[0],
            lastColorMask[1],
            lastColorMask[2],
            lastColorMask[3]
        );

        // If the buffer was bound before, it needs to be destroyed.
        GL2.glBindBuffer(GL_ARRAY_BUFFER, lastArrayBufferBinding[0]);
        GL2.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, lastElementArrayBufferBinding[0]);

        // Restore Texture Unit1.
        GL2.glActiveTexture(GL_TEXTURE1);
        GL2.glBindTexture(GL_TEXTURE_2D, lastTexture1Binding2D[0]);

        // Restore Texture Unit0.
        GL2.glActiveTexture(GL_TEXTURE0);
        GL2.glBindTexture(GL_TEXTURE_2D, lastTexture0Binding2D[0]);

        GL2.glActiveTexture(lastActiveTexture[0]);

        // restore blending
        GL2.glBlendFuncSeparate(
            lastBlendingSrcRGB[0],
            lastBlendingDstRGB[0],
            lastBlendingSrcAlpha[0],
            lastBlendingDstAlpha[0]
        );
    }

    /**
     * FBO just before model drawing
     */
    public final int[] lastFBO = new int[1];
    /**
     * Viewport just before drawing the model
     */
    public final int[] lastViewport = new int[4];

    /**
     * Set enable/disable of OpenGL ES 2.0 features.
     *
     * @param index index of function to enable/disable
     * @param enabled If true, enable it.
     */
    private void setGlEnable(int index, boolean enabled) {
        if (enabled) {
            GL2.glEnable(index);
        } else {
            GL2.glDisable(index);
        }
    }

    /**
     * Set enable/disable for Vertex Attribute Array feature in OpenGL ES 2.0.
     *
     * @param index index of function to enable/disable
     * @param isEnabled If true, enable it.
     */
    private void setGlEnableVertexAttribArray(int index, int isEnabled) {
        // It true
        if (isEnabled != 0) {
            GL2.glEnableVertexAttribArray(index);
        }
        // If false
        else {
            GL2.glDisableVertexAttribArray(index);
        }
    }

    /**
     * Vertex buffer just before drawing the model
     */
    private final int[] lastArrayBufferBinding = new int[1];
    /**
     * Element buffer just before drawing the model
     */
    private final int[] lastElementArrayBufferBinding = new int[1];
    /**
     * Shader program buffer just before drawing the model
     */
    private final int[] lastProgram = new int[1];
    /**
     * The active texture just before drawing the model
     */
    private final int[] lastActiveTexture = new int[1];
    /**
     * Texture unit0 just before model drawing
     */
    private final int[] lastTexture0Binding2D = new int[1];
    /**
     * Texture unit1 just before model drawing
     */
    private final int[] lastTexture1Binding2D = new int[1];
    /**
     * GL_VERTEX_ATTRIB_ARRAY_ENABLED parameter just before model drawing
     */
    private final int[][] lastVertexAttribArrayEnabled = new int[4][1];
    /**
     * GL_SCISSOR_TEST parameter just before drawing the model
     */
    private boolean lastScissorTest;
    /**
     * GL_BLEND parameter just before model drawing
     */
    private boolean lastBlend;
    /**
     * GL_STENCIL_TEST parameter just before drawing the model
     */
    private boolean lastStencilTest;
    /**
     * GL_DEPTH_TEST parameter just before drawing the model
     */
    private boolean lastDepthTest;
    /**
     * GL_CULL_FACE parameter just before drawing the model
     */
    private boolean lastCullFace;
    /**
     * GL_FRONT_FACE parameter just before model drawing
     */
    private final int[] lastFrontFace = new int[1];
    /**
     * GL_COLOR_WRITEMASK parameter just before model drawing
     */
    private final boolean[] lastColorMask = new boolean[4];
    /**
     * GL_BLEND_SRC_RGB parameter just before model drawing
     */
    private final int[] lastBlendingSrcRGB = new int[1];
    /**
     * GL_BLEND_DST_RGB parameter just before model drawing
     */
    private final int[] lastBlendingDstRGB = new int[1];
    /**
     * GL_BLEND_SRC_ALPHA parameter just before model drawing
     */
    private final int[] lastBlendingSrcAlpha = new int[1];
    /**
     * GL_BLEND_DST_ALPHA parameter just before model drawing
     */
    private final int[] lastBlendingDstAlpha = new int[1];
}
