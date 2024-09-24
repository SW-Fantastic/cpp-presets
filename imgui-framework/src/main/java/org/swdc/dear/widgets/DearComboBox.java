package org.swdc.dear.widgets;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.swdc.dear.DearComponent;
import org.swdc.dear.DearSizeBox;
import org.swdc.imgui.core.ImGUICore;
import org.swdc.imgui.core.imgui.ImGuiStyle;
import org.swdc.imgui.core.imgui.ImVec2;

public class DearComboBox extends DearComponent {

    private IntPointer current;

    private BytePointer label = new BytePointer(" ");

    private ImVec2 insets = null;

    private ImVec2 cellInsets = null;

    private boolean reshaped;

    private BytePointer items = new BytePointer("-\0aaa\0bbb\0eeee\0");

    public DearComboBox() {
        current = new IntPointer(1);
        current.put(0);

        insets = new ImVec2(1);
        insets.x(8);
        insets.y(0);

        cellInsets = new ImVec2(1);
        cellInsets.x(8);
        cellInsets.y(0);
    }

    @Override
    protected void update() {

        int styleRollback = 0;
        if (insets.y() > 0) {
            ImGUICore.ImGui_PushStyleVarImVec2(ImGUICore.ImGuiStyleVar_FramePadding,insets);
            styleRollback ++;
        }

        if (cellInsets.y() > 0) {
            ImGUICore.ImGui_PushStyleVarImVec2(ImGUICore.ImGuiChildFlags_AlwaysUseWindowPadding,cellInsets);
            styleRollback ++;
            ImGUICore.ImGui_PushStyleVarImVec2(ImGUICore.ImGuiStyleVar_ItemSpacing,cellInsets);
            styleRollback ++;
        }

        ImGUICore.ImGui_SetNextItemWidth(getInnerWidth());
        ImGUICore.ImGui_Combo(label,current,items);
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

        ImGUICore.ImGui_PopStyleVarEx(styleRollback);
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        reshaped = false;
    }

    public void setItemPadding(float val) {
        cellInsets.y(val);
    }

    public float getItemPadding(){
        return cellInsets.y();
    }

}
