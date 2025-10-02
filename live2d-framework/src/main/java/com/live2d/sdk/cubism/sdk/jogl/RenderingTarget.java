package com.live2d.sdk.cubism.sdk.jogl;

public enum RenderingTarget {
    NONE,   // デフォルトのフレームバッファにレンダリング
    MODEL_FRAME_BUFFER,     // LAppModelForSmallDemoが各自持つフレームバッファにレンダリング
    VIEW_FRAME_BUFFER  // LAppViewForSmallDemoが持つフレームバッファにレンダリング
}