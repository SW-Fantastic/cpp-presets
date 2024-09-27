package org.swdc.live2d.conf;


import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.Info;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

@Properties(value = {
        @Platform(
                value = "windows-x86_64",
                includepath = { "platforms/Live2d/include", "platforms/Live2d/ext" },
                include = { "Live2DCubismCore.h","csmBasicType.h" ,"csmAllocator.h", },
                linkpath = "platforms/Live2d/dll/windows/x86_64",
                link = "Live2DCubismCore"
        ),
        @Platform(
                value = "macosx-x86_64",
                includepath = { "platforms/Live2d/include", "platforms/Live2d/ext" },
                include = { "Live2DCubismCore.h","csmBasicType.h" ,"csmAllocator.h", },
                linkpath = "platforms/Live2d/dll/macos",
                link = "Live2DCubismCore"
        ),
        @Platform(
                value = "linux-x86_64",
                includepath = { "platforms/Live2d/include", "platforms/Live2d/ext" },
                include = { "Live2DCubismCore.h","csmBasicType.h" ,"csmAllocator.h", },
                linkpath = "platforms/Live2d/dll/linux/x86_64",
                link = "Live2DCubismCore"
        ),
},
        target = "org.swdc.live2d.core.Live2dCore",
        global = "org.swdc.live2d.core.Live2dCore"
)
public class Live2dCoreConfigure implements InfoMapper {
        @Override
        public void map(InfoMap infoMap) {
                infoMap.put(new Info("csmCallingConvention").cppText("#define csmCallingConvention"));
        }
}
