package org.swdc.dear.widgets;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.swdc.dear.DearColor;
import org.swdc.dear.DearComponent;
import org.swdc.dear.DearSizeBox;
import org.swdc.imgui.core.ImGUICore;
import org.swdc.imgui.core.imgui.ImGuiInputTextCallback;
import org.swdc.imgui.core.imgui.ImGuiInputTextCallbackData;
import org.swdc.imgui.core.imgui.ImVec2;

public class DearField extends DearComponent {

    private BytePointer buffer;

    private DearColor textColor;

    private ImGuiInputTextCallback callback;

    private float height = -1;

    private BytePointer label = new BytePointer(" ");

    private boolean reshaped = false;

    private ImVec2 insets = null;

    public DearField() {
        this.buffer = new BytePointer(Pointer.malloc(1024 * 1024 * 4));
        this.callback = new ImGuiInputTextCallback() {
            @Override
            public int call(ImGuiInputTextCallbackData data) {
                return super.call(data);
            }
        };
        this.insets = new ImVec2();
        this.insets.x(8);
        this.insets.y(0);
    }


    @Override
    public void update() {
        int rollback = 0;
        if (textColor != null) {
            ImGUICore.ImGui_PushStyleColorImVec4(ImGUICore.ImGuiCol_Text,textColor.getColor());
            rollback ++;
        }

        int styleRollback = 0;
        if (insets.y() > 0) {
            ImGUICore.ImGui_PushStyleVarImVec2(ImGUICore.ImGuiStyleVar_FramePadding,insets);
            styleRollback ++;
        }

        ImGUICore.ImGui_SetNextItemWidth(getInnerWidth());
        ImGUICore.ImGui_InputTextEx(label,buffer,1024 * 1024 * 4,0,callback,null);
        ImVec2 size = ImGUICore.ImGui_GetItemRectSize();
        if (!reshaped && size.y() != getInnerHeight()) {
            if (getInnerHeight() > size.y()) {
                insets.y((getInnerHeight() - size.y()) / 2);
            } else {
                insets.y(0);
                setHeight(size.y() + getPaddings().bottom() + getPaddings().top());
            }
            reshaped = true;
        }
        size.close();
        ImGUICore.ImGui_PopStyleColorEx(rollback);
        ImGUICore.ImGui_PopStyleVarEx(styleRollback);
    }

    public DearColor getTextColor() {
        return textColor;
    }

    public void setTextColor(DearColor textColor) {
        if (this.textColor != null) {
            textColor.close();
        }
        this.textColor = textColor;
    }
}
