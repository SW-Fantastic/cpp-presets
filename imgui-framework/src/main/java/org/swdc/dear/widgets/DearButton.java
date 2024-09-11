package org.swdc.dear.widgets;

import org.swdc.dear.DearColor;
import org.swdc.dear.DearComponent;
import org.swdc.dear.listeners.MouseEventListener;
import org.swdc.imgui.core.ImGUICore;
import org.swdc.imgui.core.imgui.ImGuiStyle;
import org.swdc.imgui.core.imgui.ImVec2;

import java.io.IOException;

public class DearButton extends DearComponent {

    private String text;

    private MouseEventListener clickEventListener;

    private DearColor background;

    private DearColor activeColor;

    private DearColor textColor;

    @Override
    protected void update() {

        ImGuiStyle style = ImGUICore.ImGui_GetStyle();
        ImVec2 padding = style.WindowPadding();

        ImVec2 size = getSize();
        size.x(size.x() - padding.x() * 2);
        size.y(size.y() - padding.y() * 2);

        int rollback = 0;
        if (background != null) {
            ImGUICore.ImGui_PushStyleColorImVec4(ImGUICore.ImGuiCol_Button,background.getColor());
            rollback ++;
        }
        if (textColor != null) {
            ImGUICore.ImGui_PushStyleColorImVec4(ImGUICore.ImGuiCol_Text,textColor.getColor());
            rollback++;
        }
        if (activeColor != null) {
            ImGUICore.ImGui_PushStyleColorImVec4(ImGUICore.ImGuiCol_ButtonActive,activeColor.getColor());
            rollback++;
        }

        if(ImGUICore.ImGui_ButtonEx(text,size)) {
            preformAsyncListener(clickEventListener);
        }

        ImGUICore.ImGui_PopStyleColorEx(rollback);
    }

    public void setClickEventListener(MouseEventListener clickEventListener) {
        this.clickEventListener = clickEventListener;
    }

    public MouseEventListener getClickEventListener() {
        return clickEventListener;
    }


    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setBackground(DearColor background) {
        this.background = background;
    }

    public DearColor getBackground() {
        return background;
    }

    public void setTextColor(DearColor textColor) {
        this.textColor = textColor;
    }

    public DearColor getTextColor() {
        return textColor;
    }

    public void setActiveColor(DearColor activeColor) {
        this.activeColor = activeColor;
    }

    public DearColor getActiveColor() {
        return activeColor;
    }

    @Override
    public void close() throws IOException {

        super.close();
        if (activeColor != null) {
            activeColor.close();
            activeColor = null;
        }
        if (textColor != null) {
            textColor.close();
            textColor = null;
        }
        if (background != null) {
            background.close();
            background = null;
        }

    }
}
