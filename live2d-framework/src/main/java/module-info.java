module swdc.live2d.framework {

    requires java.desktop;

    requires static jogl.all;
    requires static jogl.all.main;
    requires static org.lwjgl;
    requires static org.lwjgl.natives;
    requires static org.lwjgl.glfw;
    requires static org.lwjgl.glfw.natives;
    requires static org.lwjgl.opengl;
    requires static org.lwjgl.opengl.natives;

    requires swdc.presets.live2d;

    exports com.live2d.sdk.cubism.core;
    exports com.live2d.sdk.cubism.framework.type;
    exports com.live2d.sdk.cubism.framework.model;
    exports com.live2d.sdk.cubism.framework.id;
    exports com.live2d.sdk.cubism.framework.effect;
    exports com.live2d.sdk.cubism.framework.math;
    exports com.live2d.sdk.cubism.framework.exception;
    exports com.live2d.sdk.cubism.framework.motion;
    exports com.live2d.sdk.cubism.framework.physics;
    exports com.live2d.sdk.cubism.framework.utils;
    exports com.live2d.sdk.cubism.framework.utils.jsonparser;

    exports com.live2d.sdk.cubism.framework.rendering;
    exports com.live2d.sdk.cubism.framework.rendering.opengl;
    exports com.live2d.sdk.cubism.framework.rendering.jogl;
    exports com.live2d.sdk.cubism.framework.rendering.lwjgl;

    exports com.live2d.sdk.cubism.sdk;
    exports com.live2d.sdk.cubism.sdk.jogl;
    exports com.live2d.sdk.cubism.sdk.lwjgl;

}