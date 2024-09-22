package org.swdc.dear.widgets;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.swdc.dear.DearComponent;
import org.swdc.dear.DearSizeBox;
import org.swdc.imgui.core.ImGUICore;
import org.swdc.imgui.core.imgui.ImGuiInputTextCallback;
import org.swdc.imgui.core.imgui.ImGuiInputTextCallbackData;
import org.swdc.imgui.core.imgui.ImVec2;

public class DearField extends DearComponent {

    private BytePointer buffer;


    private ImGuiInputTextCallback callback;

    private float height = -1;

    private BytePointer label = new BytePointer(" ");

    public DearField() {
        this.buffer = new BytePointer(Pointer.malloc(1024 * 1024 * 4));
        this.callback = new ImGuiInputTextCallback() {
            @Override
            public int call(ImGuiInputTextCallbackData data) {
                return super.call(data);
            }
        };
    }


    @Override
    public void update() {
        ImGUICore.ImGui_SetNextItemWidth(getInnerWidth());
        ImGUICore.ImGui_InputTextEx(label,buffer,1024 * 1024 * 4,0,callback,null);
    }

    @Override
    public float getHeight() {
        if (height == -1) {
            ImVec2 size = ImGUICore.ImGui_CalcTextSize("");
            height = size.y() ;
            size.close();
        }
        float height = this.height + getPaddings().top() + getPaddings().bottom();
        return height + this.height / 2f;
    }

    @Override
    public void setHeight(float height) {
        height = height - (this.height + this.height / 2f);
        if (height > 0) {
            float padding = height / 2f;
            DearSizeBox sizeBox = getPaddings();
            sizeBox.top(padding);
            sizeBox.bottom(padding);
        }
    }
}
