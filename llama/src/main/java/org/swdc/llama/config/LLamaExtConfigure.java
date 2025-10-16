package org.swdc.llama.config;

import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.Info;
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
        target = "org.swdc.llama.core.ext",
        global = "org.swdc.llama.core.LLamaExt",
        inherit = LLamaConfigure.class
)
public class LLamaExtConfigure implements InfoMapper {
    @Override
    public void map(InfoMap infoMap) {
        infoMap.put(new Info("std::vector<llama_chat_message>")
                .pointerTypes("ChatMessageVec")
                .define()
        );
        infoMap.put(new Info("std::vector<char>")
                .pointerTypes("CharVec")
                .define()
        );
    }
}
