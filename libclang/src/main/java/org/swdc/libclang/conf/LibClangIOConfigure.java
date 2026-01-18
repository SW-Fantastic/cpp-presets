package org.swdc.libclang.conf;

import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.Info;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

@Properties(value = {
        @Platform(
                value = "windows-x86_64",
                includepath = { "platforms/libclang/include" },
                include = { "CXFile.h", "CXErrorCode.h", "CXString.h", "CXDiagnostic.h", "CXSourceLocation.h"},
                linkpath = "platforms/libclang/dll/windows/x86_64",
                link = "libclang"
        )
},
        target = "org.swdc.libclang.core.io",
        global = "org.swdc.libclang.core.io.ClangIO"
)
public class LibClangIOConfigure implements InfoMapper {
    @Override
    public void map(InfoMap infoMap) {
        infoMap.put(new Info("CINDEX_DEPRECATED").skip());
        infoMap.put(new Info("CINDEX_LINKAGE").cppText("#define CINDEX_LINKAGE").translate());
        infoMap.put(new Info("LLVM_CLANG_C_EXTERN_C_BEGIN").cppText("#define LLVM_CLANG_C_EXTERN_C_BEGIN").translate());
        infoMap.put(new Info("LLVM_CLANG_C_EXTERN_C_END").cppText("#define LLVM_CLANG_C_EXTERN_C_END").translate());
    }
}
