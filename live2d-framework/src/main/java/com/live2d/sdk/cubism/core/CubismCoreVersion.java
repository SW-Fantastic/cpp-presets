package com.live2d.sdk.cubism.core;

public class CubismCoreVersion {
    private final int versionNumber;

    private final int major;

    private final int minor;

    private final int patch;

    CubismCoreVersion(int version) {
        this.versionNumber = version;
        this.major = version >>> 24 & 0xFF;
        this.minor = version >>> 16 & 0xFF;
        this.patch = version & 0xFF;
    }

    public int getMajor() {
        return this.major;
    }

    public int getMinor() {
        return this.minor;
    }

    public int getPatch() {
        return this.patch;
    }

    public int getVersionNumber() {
        return this.versionNumber;
    }

    public String toString() {
        return String.format("%02d.%02d.%04d (%d)", new Object[] { Integer.valueOf(this.major), Integer.valueOf(this.minor), Integer.valueOf(this.patch), Integer.valueOf(this.versionNumber) });
    }
}
