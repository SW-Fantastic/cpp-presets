package org.swdc.dear.widgets;

import org.swdc.dear.DearColor;
import org.swdc.dear.DearComponent;
import org.swdc.imgui.core.ImGUICore;
import org.swdc.imgui.core.imgui.ImVec2;

import java.io.IOException;

public class DearLabel extends DearComponent {

    private String text;

    private DearColor textColor;

    @Override
    protected void update() {

        ImVec2 size = ImGUICore.ImGui_CalcTextSize(text);

        if (size.y() > getHeight()) {
            ImGUICore.ImGui_SetCursorPosX(0);
        } else {
            ImGUICore.ImGui_SetCursorPosY((getHeight() - size.y()) / 2f);
        }

        int rollback = 0;
        if (textColor != null) {
            ImGUICore.ImGui_PushStyleColorImVec4(ImGUICore.ImGuiCol_Text,textColor.getColor());
            rollback ++;
        }

        ImGUICore.ImGui_Text(text);
        ImGUICore.ImGui_PopStyleColorEx(rollback);

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public DearColor getTextColor() {
        return textColor;
    }

    public void setTextColor(DearColor textColor) {
        this.textColor = textColor;
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (textColor != null) {
            textColor.close();
            textColor = null;
        }
    }
}
