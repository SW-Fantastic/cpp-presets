package org.swdc.dear;

import org.bytedeco.javacpp.BoolPointer;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.swdc.dear.widgets.DearTitleBar;
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

    private DearTitleBar titleBar;

    private ImVec2 absolutePos;

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
        this.titleBar = new DearTitleBar();
        this.titleBar.setCloseRequestListener(() -> {
            this.closeFlag.put(false);
        });

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

            setWidth(sizeVec.x());
            setHeight(sizeVec.y());

            titleBar.setX(0);
            titleBar.setY(0);
            titleBar.setWidth(sizeVec.x());
            titleBar.setText(title.getString());
            titleBar.setComponentBackgroundColor(new DearColor("#FFF"));
            titleBar.doUpdate();

            if (absolutePos != null && absolutePos.isNull()) {
                absolutePos.close();
            }
            absolutePos = ImGUICore.ImGui_GetCursorScreenPos();

            content.setY(titleBar.getHeight());
            content.setWidth(sizeVec.x());
            content.setHeight(sizeVec.y() - titleBar.getHeight());
            content.doUpdate();

            sizeVec.close();
            viewport.close();
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

    public ImVec2 getAbsolutePos() {
        return absolutePos;
    }
}
