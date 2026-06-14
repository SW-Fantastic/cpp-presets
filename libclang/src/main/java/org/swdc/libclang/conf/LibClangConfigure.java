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
                include = { "Index.h" },
                linkpath = "platforms/libclang/dll/windows/x86_64",
                link = "libclang"
        ),
        @Platform(
                value = "linux-x86_64",
                includepath = { "platforms/libclang/include" },
                include = { "Index.h" },
                linkpath = "platforms/libclang/dll/linux/x86_64",
                link = "clang"
        )
},
        target = "org.swdc.libclang.core",
        global = "org.swdc.libclang.core.LibClang",
        inherit = LibClangIOConfigure.class
)
public class LibClangConfigure implements InfoMapper {

    @Override
    public void map(InfoMap infoMap) {

        infoMap.put(new Info("CINDEX_VERSION_ENCODE", "CINDEX_VERSION_STRINGIZE", "CINDEX_VERSION", "CINDEX_VERSION_STRING").skip());
        infoMap.put(new Info("CXTranslationUnit").cppTypes("CXTranslationUnitImpl*").translate());
        infoMap.put(new Info("CXTargetInfo").cppTypes("CXTargetInfoImpl*").translate());
        infoMap.put(new Info("CXCursorSet").cppTypes("CXCursorSetImpl*").translate());
        infoMap.put(new Info("CINDEX_LINKAGE").cppText("#define CINDEX_LINKAGE").translate());
        infoMap.put(new Info("LLVM_CLANG_C_EXTERN_C_BEGIN").cppText("#define LLVM_CLANG_C_EXTERN_C_BEGIN").translate());
        infoMap.put(new Info("LLVM_CLANG_C_EXTERN_C_END").cppText("#define LLVM_CLANG_C_EXTERN_C_END").translate());

        infoMap.put(new Info("clang_visitChildrenWithBlock",
                "clang_findIncludesInFileWithBlock",
                "clang_findReferencesInFileWithBlock").skip());
    }

}
