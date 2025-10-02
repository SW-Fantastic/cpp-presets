package com.live2d.sdk.cubism.sdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Live2dUtils {

    private static double s_currentFrame;
    private static double _lastNanoTime;
    private static double _deltaNanoTime;

    private static long getSystemNanoTime() {
        return System.nanoTime();
    }

    // デルタタイム(前回フレームとの差分)を取得する
    public static float getDeltaTime() {
        // ナノ秒を秒に変換
        return (float) (_deltaNanoTime / 1000000000.0f);
    }

    /**
     * Logging function
     *
     * @param message log message
     */
    public static void printLog(String message) {
        System.err.println("[LOG] : " + message);
    }

    public static byte[] loadFileAsBytes(Live2dAssets assets, String path) {
        try {
            return assets.loadResource(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateTime() {
        s_currentFrame = getSystemNanoTime();
        _deltaNanoTime = s_currentFrame - _lastNanoTime;
        _lastNanoTime = s_currentFrame;
    }
}
