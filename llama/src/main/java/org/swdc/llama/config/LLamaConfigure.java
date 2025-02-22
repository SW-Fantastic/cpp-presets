package org.swdc.llama.config;

import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.Info;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

@Properties(value = {
        @Platform(
                value = "windows-x86_64",
                includepath = { "platforms/llamaCpp/include" },
                include = {"llama.h"},
                linkpath = "platforms/llamaCpp/dll/windows/x86_64",
                link = "llama"
        )
},
        inherit = GGMLConfigure.class,
        target = "org.swdc.llama.core",
        global = "LLamaCore"
)
public class LLamaConfigure implements InfoMapper {

    @Override
    public void map(InfoMap infoMap) {

        infoMap.put(new Info("LLAMA_API").cppText("#define LLAMA_API"));
        infoMap.put(new Info(

                "llama_vocab_type",
                "llama_vocab_pre_type",
                "llama_rope_type",
                "llama_token_type",
                "llama_token_attr",
                "llama_ftype",
                "llama_rope_scaling_type",
                "llama_pooling_type",
                "llama_attention_type",
                "llama_split_mode"
        ).skip());

    }

}
