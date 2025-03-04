package org.swdc.llama.config;

import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

@Properties(value = {
        @Platform(
                value = "windows-x86_64",
                includepath = { "platforms/llamaCpp/ext" },
                include = {"llama_std_support.h"},
                linkpath = "platforms/llamaCpp/dll/windows/x86_64",
                link = {"llama", "ggml-base"}
        )
},
        target = "org.swdc.llama.core.LLamaExt",
        global = "org.swdc.llama.core.LLamaExt"
)
public class LLamaExtConfigure implements InfoMapper {
    @Override
    public void map(InfoMap infoMap) {

    }
}
