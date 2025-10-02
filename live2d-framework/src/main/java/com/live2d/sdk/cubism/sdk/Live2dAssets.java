package com.live2d.sdk.cubism.sdk;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class Live2dAssets {

    private File modelRootDir;

    /**
     * 创建一个AssetsManager
     * @param modelRootDir 模型根目录，包含多个模型的目录。
     */
    public Live2dAssets(File modelRootDir) {
        this.modelRootDir = modelRootDir;
    }

    public File getResource(String path) {
        return modelRootDir.toPath().resolve(path).toFile();
    }

    public byte[] loadResource(String path) {
        File file = getResource(path);
        if (file == null || !file.exists() || file.isDirectory()) {
            return new byte[0];
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String[] list(String filePath) throws IOException {
        File theFile = getResource(filePath);
        if (theFile.isDirectory()) {
            return Files.list(theFile.toPath())
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toList()).toArray(new String[0]);
        } else {
            return new String[] { theFile.getName() };
        }

    }

}
