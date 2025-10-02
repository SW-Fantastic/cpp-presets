package com.live2d.sdk.cubism.sdk;


import java.io.File;

/**
 * Live2d的渲染代理，
 * 不同的渲染方式有不同的渲染代理。
 */
public abstract class Live2dDelegate {

    private Live2dAssets assets;

    private Live2dConfigure configure;


    public Live2dDelegate(Live2dConfigure configure,File modelRootDir) {
        this.assets = new Live2dAssets(modelRootDir);
        this.configure = configure;
    }

    public Live2dAssets getAssets() {
        return assets;
    }


    public Live2dConfigure getConfigure() {
        return configure;
    }

}
