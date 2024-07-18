package com.live2d.sdk.cubism.core;

public class CubismDrawableFlag {
    public static class ConstantFlag {
        public static final byte BLEND_ADDITIVE = 1;

        public static final byte BLEND_MULTIPLICATIVE = 2;

        public static final byte IS_DOUBLE_SIDED = 4;

        public static final byte IS_INVERTED_MASK = 8;
    }

    public static class DynamicFlag {
        public static final byte IS_VISIBLE = 1;

        public static final byte VISIBILITY_DID_CHANGE = 2;

        public static final byte OPACITY_DID_CHANGE = 4;

        public static final byte DRAW_ORDER_DID_CHANGE = 8;

        public static final byte RENDER_ORDER_DID_CHANGE = 16;

        public static final byte VERTEX_POSITIONS_DID_CHANGE = 32;

        public static final byte BLEND_COLOR_DID_CHANGE = 64;
    }
}
