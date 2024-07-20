module swdc.live2d.framework {

    requires java.desktop;

    requires transitive jogl.all;
    requires transitive jogl.all.main;

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
    exports com.live2d.sdk.cubism.framework.rendering;
    exports com.live2d.sdk.cubism.framework.rendering.jogl;
    exports com.live2d.sdk.cubism.framework.utils;
    exports com.live2d.sdk.cubism.framework.utils.jsonparser;

}