package org.swdc.dear;

import org.bytedeco.javacpp.Pointer;
import org.swdc.imgui.core.imgui.ImFontConfig;

public class DearUtils {

    /**
     * JavaCPP创建的本地对象不会执行对应的构造函数
     * 本方法用于创建一个ImFontConfig并且等效的执行对应的构造函数。
     *
     * @param withMemory 该Config将使用的数据来自内存还是文件，如果来自内存请设置为true
     * @param merge 是否使用merge模式，将本config将要设置的字体合并入其他字体中。
     * @return config对象。
     */
    public static ImFontConfig createFontConfig(boolean withMemory, boolean merge) {
        ImFontConfig config = new ImFontConfig(1);
        Pointer.memset(config,0,Pointer.sizeof(ImFontConfig.class));
        config.FontDataOwnedByAtlas(!withMemory);
        config.OversampleH(2);
        config.OversampleV(1);
        config.GlyphMaxAdvanceX(Float.MAX_VALUE);
        config.RasterizerMultiply(1);
        config.RasterizerDensity(1);
        config.EllipsisChar(-1);
        config.MergeMode(merge);
        return config;
    }

}
