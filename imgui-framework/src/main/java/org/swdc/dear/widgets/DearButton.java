package org.swdc.dear.widgets;

import org.swdc.dear.DearColor;
import org.swdc.dear.DearComponent;
import org.swdc.dear.DearSizeBox;
import org.swdc.dear.listeners.MouseEventListener;
import org.swdc.imgui.core.ImGUICore;
import org.swdc.imgui.core.imgui.ImDrawList;
import org.swdc.imgui.core.imgui.ImGuiStyle;
import org.swdc.imgui.core.imgui.ImVec2;

import java.io.IOException;

public class DearButton extends DearComponent {

    private String text;

    private MouseEventListener clickEventListener;

    private DearColor background;

    private DearColor activeColor;

    private DearColor textColor;

    private DearColor hoverColor;

    private DearColor textHoverColor;

    @Override
    protected void update() {

        int rollback = 0;
        if (background != null) {
            ImGUICore.ImGui_PushStyleColorImVec4(ImGUICore.ImGuiCol_Button,background.getColor());
            rollback ++;
        }
        if (textColor != null) {
            ImGUICore.ImGui_PushStyleColorImVec4(ImGUICore.ImGuiCol_Text,textColor.getColor());
            rollback++;
        }
        if (hovering && textHoverColor != null) {
            ImGUICore.ImGui_PushStyleColorImVec4(ImGUICore.ImGuiCol_Text,textHoverColor.getColor());
            rollback++;
        }
        if (activeColor != null) {
            ImGUICore.ImGui_PushStyleColorImVec4(ImGUICore.ImGuiCol_ButtonActive,activeColor.getColor());
            rollback++;
        }

        if (hoverColor != null) {
            ImGUICore.ImGui_PushStyleColorImVec4(ImGUICore.ImGuiCol_ButtonHovered,hoverColor.getColor());
            rollback++;
        }


        if(ImGUICore.ImGui_ButtonEx(text,getInnerSize())) {
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
        if(this.background != null) {
            this.background.close();
        }
        this.background = background;
    }

    public DearColor getBackground() {
        return background;
    }

    public void setTextColor(DearColor textColor) {
        if (this.textColor != null) {
            this.textColor.close();
        }
        this.textColor = textColor;
    }

    public DearColor getTextColor() {
        return textColor;
    }

    public void setHoverColor(DearColor hoverColor) {
        if (this.hoverColor != null) {
            this.hoverColor.close();
        }
        this.hoverColor = hoverColor;
    }

    public DearColor getHoverColor() {
        return hoverColor;
    }

    public DearColor getTextHoverColor() {
        return textHoverColor;
    }

    public void setTextHoverColor(DearColor textHoverColor) {
        if (this.textHoverColor != null) {
            this.textHoverColor.close();
        }
        this.textHoverColor = textHoverColor;
    }

    public void setActiveColor(DearColor activeColor) {
        if (this.activeColor != null) {
            this.activeColor.close();
        }
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

        if (hoverColor != null) {
            hoverColor.close();
            hoverColor = null;
        }

        if (textHoverColor != null) {
            textHoverColor.close();
            textHoverColor = null;
        }

    }
}
