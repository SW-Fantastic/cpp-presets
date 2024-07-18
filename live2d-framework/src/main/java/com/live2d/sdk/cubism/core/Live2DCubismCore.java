package com.live2d.sdk.cubism.core;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Loader;
import org.swdc.live2d.core.Live2dCore;

public class Live2DCubismCore {
    private static ICubismLogger logger;

    private static CubismCoreVersion version;

    public static class MocVersion {
        public static final int UNKNOWN = 0;

        public static final int V30 = 1;

        public static final int V33 = 2;

        public static final int V40 = 3;

        public static final int V42 = 4;

        public static final int V50 = 5;
    }

    public static CubismCoreVersion getVersion() {
        if (Live2DCubismCore.version == null) {
            int version = Live2dCore.csmGetVersion();
            Live2DCubismCore.version = new CubismCoreVersion(version);
        }
        return Live2DCubismCore.version;
    }

    public static int getLatestMocVersion() {
        if (latestMocVersion < 0)
            latestMocVersion = Live2dCore.csmGetLatestMocVersion();
        return latestMocVersion;
    }

    public static int getMocVersion(byte[] mocBinary) {
        return Live2dCore.csmGetMocVersion(new BytePointer(mocBinary),mocBinary.length);
    }

    public static boolean hasMocConsistency(byte[] mocBinary) {
        BytePointer buf = new BytePointer(
                Live2dCore.csmAllocateAligned(mocBinary.length,Live2dCore.csmAlignofMoc)
        );
        buf.put(mocBinary);
        int isValid = Live2dCore.csmHasMocConsistency(buf, mocBinary.length);
        buf.close();

        return (isValid != 0);
    }

    public static ICubismLogger getLogger() {
        return logger;
    }

    public static void setLogger(ICubismLogger logger) {
        Live2DCubismCore.logger = logger;
    }

    private static int latestMocVersion = -1;
}
