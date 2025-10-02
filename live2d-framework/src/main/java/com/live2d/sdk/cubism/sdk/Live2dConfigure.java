package com.live2d.sdk.cubism.sdk;

import com.live2d.sdk.cubism.framework.CubismFrameworkConfig;

public class Live2dConfigure {



    /**
     * Path of image materials.
     */
    public enum ResourcePath {
        /**
         * Relative path of the material directory
         */
        ROOT(""),
        /**
         * Relative path of shader directory
         */
        SHADER_ROOT("Shaders"),
        /**
         * Background image file
         */
        BACK_IMAGE("back_class_normal.png"),
        /**
         * Gear image file
         */
        GEAR_IMAGE("icon_gear.png"),
        /**
         * Power button image file
         */
        POWER_IMAGE("close.png"),
        /**
         * Vertex shader file
         */
        VERT_SHADER("VertSprite.vert"),
        /**
         * Fragment shader file
         */
        FRAG_SHADER("FragSprite.frag");

        private final String path;

        ResourcePath(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }




    /**
     * MOC3の整合性を検証するかどうか。有効ならtrue。
     */
    public boolean mocConsistencyValidation = true;

    /**
     * motion3.jsonの整合性を検証するかどうか。有効ならtrue。
     */
    public boolean motionConsistencyValidation = true;

    /**
     * Enable/Disable debug logging.
     */
    public boolean debugLog = true;
    /**
     * Enable/Disable debug logging for processing tapping information.
     */
    public boolean debugTouchLog = true;
    /**
     * Setting the level of the log output from the Framework.
     */
    public CubismFrameworkConfig.LogLevel cubismLoggingLevel = CubismFrameworkConfig.LogLevel.VERBOSE;

    /**
     * Enable/Disable premultiplied alpha.
     * 由于Android的BitmapFactory.decodeStream方法使用Premultiplied Alpha方法生成图像，
     * 因此需要将范例LAppDefine中定义的PREMULTIPLIED_ALPHA_ENABLE设置为true。
     */
    public boolean preMultipliedAlpha = false;

    /**
     * Flag whether to draw to the target held by LAppView. (If both USE_RENDER_TARGET and USE_MODEL_RENDER_TARGET are true, this variable is given priority over USE_MODEL_RENDER_TARGET.)
     */
    public boolean useRenderTarget = false;
    /**
     * Flag whether to draw to the target that each LAppModel has.
     */
    public boolean userModelRenderTarget = false;

    public void setCubismLoggingLevel(CubismFrameworkConfig.LogLevel cubismLoggingLevel) {
        this.cubismLoggingLevel = cubismLoggingLevel;
    }

    public void setDebugLog(boolean debugLog) {
        this.debugLog = debugLog;
    }

    public void setDebugTouchLog(boolean debugTouchLog) {
        this.debugTouchLog = debugTouchLog;
    }

    public void setMocConsistencyValidation(boolean mocConsistencyValidation) {
        this.mocConsistencyValidation = mocConsistencyValidation;
    }

    public void setMotionConsistencyValidation(boolean motionConsistencyValidation) {
        this.motionConsistencyValidation = motionConsistencyValidation;
    }

    public void setPreMultipliedAlpha(boolean preMultipliedAlpha) {
        this.preMultipliedAlpha = preMultipliedAlpha;
    }

    public void setUseRenderTarget(boolean useRenderTarget) {
        this.useRenderTarget = useRenderTarget;
    }

    public void setUserModelRenderTarget(boolean userModelRenderTarget) {
        this.userModelRenderTarget = userModelRenderTarget;
    }

    public boolean isDebugLog() {
        return debugLog;
    }

    public boolean isDebugTouchLog() {
        return debugTouchLog;
    }

    public boolean isMocConsistencyValidation() {
        return mocConsistencyValidation;
    }

    public boolean isMotionConsistencyValidation() {
        return motionConsistencyValidation;
    }

    public boolean isPreMultipliedAlpha() {
        return preMultipliedAlpha;
    }

    public boolean isUseRenderTarget() {
        return useRenderTarget;
    }

    public boolean isUserModelRenderTarget() {
        return userModelRenderTarget;
    }

    public CubismFrameworkConfig.LogLevel getCubismLoggingLevel() {
        return cubismLoggingLevel;
    }

}
