package org.swdc.dear;

import org.bytedeco.javacpp.BoolPointer;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.swdc.imgui.core.ImGUICore;
import org.swdc.imgui.core.imgui.ImGuiStyle;
import org.swdc.imgui.core.imgui.ImGuiViewport;
import org.swdc.imgui.core.imgui.ImVec2;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class DearWindow {

    private ImVec2 sizes;

    private BoolPointer closeFlag;

    private BytePointer title;

    private DearApplication application;

    private DearComponent content;

    public DearWindow(DearApplication application, String text) {

        this.application = application;

        this.title = new BytePointer(Pointer.malloc(
                text.getBytes(StandardCharsets.UTF_8).length
        ));
        this.title.putString(text);

        this.closeFlag = new BoolPointer(1);
        this.closeFlag.put(true);

        this.sizes = new ImVec2(1);
        this.sizes.x(800);
        this.sizes.y(600);

        application.regWindow(this);

    }

    void doRender() {

        if (!closeFlag.get()) {
            return;
        }

        ImGUICore.ImGui_SetNextWindowSize(sizes,ImGUICore.ImGuiCond_Once);
        ImGUICore.ImGui_Begin(title, closeFlag, ImGUICore.ImGuiWindowFlags_NoCollapse | ImGUICore.ImGuiWindowFlags_NoDocking | ImGUICore.ImGuiWindowFlags_NoTitleBar);

        if (content != null) {


            ImGuiViewport viewport = ImGUICore.ImGui_GetWindowViewport();
            ImVec2 sizeVec = viewport.Size();

            ImGuiStyle style = ImGUICore.ImGui_GetStyle();
            ImVec2 padding = style.WindowPadding();

            setWidth(sizeVec.x());
            setHeight(sizeVec.y() - padding.y());

            content.setY(0);
            content.setWidth(sizeVec.x());
            content.setHeight(sizeVec.y() - padding.y());
            content.doUpdate();

        }

        ImGUICore.ImGui_End();

    }

    public void setContent(DearComponent content) {
        this.content = content;
        this.content.setOwner(this);
    }

    public DearComponent getContent() {
        return content;
    }

    public void show() {
        closeFlag.put(true);
    }

    public void close() {
        closeFlag.put(false);
    }

    public boolean visible() {
        return closeFlag.get();
    }

    public float getWidth() {
        return sizes.x();
    }

    public float getHeight() {
        return sizes.y();
    }

    public void setWidth(float width) {
        sizes.x(width);
    }

    public void setHeight(float height) {
        sizes.y(height);
    }

}
