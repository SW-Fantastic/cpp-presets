package com.live2d.sdk.demo;

import com.live2d.sdk.cubism.core.ICubismLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LAppPal {
    /**
     * Logging Function class to be registered in the CubismFramework's logging function.
     */
    public static class PrintLogFunction implements ICubismLogger {
        @Override
        public void print(String message) {
            System.err.println("[LOG]: " + message);
        }
    }

    // アプリケーションを中断状態にする。実行されるとonPause()イベントが発生する
    public static void moveTaskToBack() {
        //LAppDelegate.getInstance().getActivity().moveTaskToBack(true);
    }

    // デルタタイムの更新
    public static void updateTime() {
        s_currentFrame = getSystemNanoTime();
        _deltaNanoTime = s_currentFrame - _lastNanoTime;
        _lastNanoTime = s_currentFrame;
    }

    // ファイルをバイト列として読み込む
    public static byte[] loadFileAsBytes(final String path) {
        InputStream fileData = null;
        try {
            File target = LAppDelegate.getInstance().getAssets().open(path);
            FileInputStream fin = new FileInputStream(target);
            byte[] data = fin.readAllBytes();
            fin.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();

            if (LAppDefine.DEBUG_LOG_ENABLE) {
                printLog("File open error.");
            }

            return new byte[0];
        } finally {
            try {
                if (fileData != null) {
                    fileData.close();
                }
            } catch (IOException e) {
                e.printStackTrace();

                if (LAppDefine.DEBUG_LOG_ENABLE) {
                    printLog("File open error.");
                }
            }
        }
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

    private static long getSystemNanoTime() {
        return System.nanoTime();
    }

    private static double s_currentFrame;
    private static double _lastNanoTime;
    private static double _deltaNanoTime;

    private static final String TAG = "[APP]";

    private LAppPal() {}
}
