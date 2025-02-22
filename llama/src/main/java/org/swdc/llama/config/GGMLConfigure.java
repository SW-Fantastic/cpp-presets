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
                include = {"ggml-alloc.h", "ggml.h", "ggml-backend.h","ggml-cpu.h"},
                linkpath = "platforms/llamaCpp/dll/windows/x86_64",
                link = {"ggml-base", "ggml-cpu","ggml"}
        )
},
        target = "org.swdc.llama.core.ggml",
        global = "GGML"
)
public class GGMLConfigure implements InfoMapper {
    @Override
    public void map(InfoMap infoMap) {
        infoMap.put(new Info(
                "GGML_NORETURN",
                "GGML_RESTRICT",
                "GGML_TENSOR_UNARY_OP_LOCALS",
                "GGML_TENSOR_BINARY_OP_LOCALS",
                "GGML_TENSOR_BINARY_OP_LOCALS01",
                "ggml_backend_graph_copy",
                "ggml_backend_dev_type",
                "ggml_backend_dev_caps",
                "ggml_backend_dev_props",

                "ggml_graph_export",
                "ggml_graph_import",
                "ggml_threadpool_get_n_threads"
        ).skip());

        infoMap.put(new Info("GGML_API").cppText("#define GGML_API"));
        infoMap.put(new Info("GGML_BACKEND_API").cppText("#define GGML_BACKEND_API"));
        infoMap.put(new Info("GGML_RESTRICT").cppText("#define GGML_RESTRICT "));

        infoMap.put(new Info("ggml_gallocr_t").cppTypes("ggml_gallocr*"));
        infoMap.put(new Info("ggml_backend_buffer_t").cppTypes("ggml_backend_buffer*"));
        infoMap.put(new Info("ggml_backend_t").cppTypes("ggml_backend*"));
        infoMap.put(new Info("ggml_backend_type_t").cppTypes("ggml_backend_type*"));
        infoMap.put(new Info("ggml_backend_buffer_type_t").cppTypes("ggml_backend_buffer_type*"));
        infoMap.put(new Info("ggml_backend_sched_t").cppTypes("ggml_backend_sched*"));
        infoMap.put(new Info("ggml_backend_reg_t").cppTypes("ggml_backend_reg*"));
        infoMap.put(new Info("ggml_backend_dev_t").cppTypes("ggml_backend_device*"));
        infoMap.put(new Info("ggml_backend_event_t").cppTypes("ggml_backend_event*"));
        infoMap.put(new Info("ggml_threadpool_t").cppTypes("ggml_threadpool*"));


    }
}
