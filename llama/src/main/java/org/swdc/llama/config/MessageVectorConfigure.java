package org.swdc.llama.config;

import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

@Properties(value = {
        @Platform(
                value = "windows-x86_64",
                includepath = { "platforms/llamaCpp/ext" },
                include = {"llama_std_support.h"}
        )
},
        target = "org.swdc.llama.core.MessageVector",
        global = "org.swdc.llama.core.MessageVector"
)
public class MessageVectorConfigure implements InfoMapper {
    @Override
    public void map(InfoMap infoMap) {

    }
}
