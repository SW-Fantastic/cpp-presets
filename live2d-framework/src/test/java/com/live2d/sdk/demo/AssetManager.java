package com.live2d.sdk.demo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class AssetManager {

    private Path file = new File("live2d-framework/assets").getAbsoluteFile().toPath();

    public File open(String filePath) {
        return file.resolve(filePath).toFile();
    }

    public String[] list(String filePath) throws IOException {
        File theFile = open(filePath);
        if (theFile.isDirectory()) {
            return Files.list(theFile.toPath())
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toList()).toArray(new String[0]);
        } else {
            return new String[] { theFile.getName() };
        }

    }
}
