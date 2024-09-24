package org.swdc.dear.widgets;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.swdc.dear.DearColor;
import org.swdc.dear.DearComponent;
import org.swdc.imgui.core.ImGUICore;
import org.swdc.imgui.core.imgui.ImGuiInputTextCallback;
import org.swdc.imgui.core.imgui.ImGuiInputTextCallbackData;

public class DearTextArea extends DearComponent {

    private BytePointer buffer;


    private ImGuiInputTextCallback callback;

    private DearColor textColor;

    private BytePointer label = new BytePointer(" ");

    public DearTextArea() {
        this.buffer = new BytePointer(Pointer.malloc(1024 * 1024 * 4));
        this.callback = new ImGuiInputTextCallback() {
            @Override
            public int call(ImGuiInputTextCallbackData data) {
                return super.call(data);
            }
        };
    }
    @Override
    protected void update() {
        int rollback = 0;
        if (textColor != null) {
            ImGUICore.ImGui_PushStyleColorImVec4(ImGUICore.ImGuiCol_Text,textColor.getColor());
            rollback ++;
        }
        ImGUICore.ImGui_InputTextMultilineEx(label,buffer, 1024 * 1024 * 4,getInnerSize(),0,callback,null);
        ImGUICore.ImGui_PopStyleColorEx(rollback);
    }



}
