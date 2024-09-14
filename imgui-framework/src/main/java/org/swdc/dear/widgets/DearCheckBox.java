package org.swdc.dear.widgets;

import org.bytedeco.javacpp.BoolPointer;
import org.bytedeco.javacpp.BytePointer;
import org.swdc.dear.DearComponent;
import org.swdc.imgui.core.ImGUICore;
import org.swdc.imgui.core.imgui.ImGuiStyle;
import org.swdc.imgui.core.imgui.ImVec2;

public class DearCheckBox extends DearComponent {

    private BoolPointer checked = null;

    private BytePointer text;

    private float height;

    public DearCheckBox(String label) {

        this.text = new BytePointer(label);
        this.checked = new BoolPointer(1);

    }

    @Override
    protected void update() {
        ImGUICore.ImGui_Checkbox(text,checked);
    }

    @Override
    public float getHeight() {
        ImVec2 size = ImGUICore.ImGui_CalcTextSize(text);
        this.height = size.y();
        size.close();
        return height + height / 2f;
    }

    @Override
    public void setHeight(float height) {
        return;
    }

    public void setText(BytePointer text) {
        this.text = text;
    }

    public BytePointer getText() {
        return text;
    }

    public boolean isChecked() {
        return checked.get();
    }

    public void setChecked(boolean val) {
        checked.put(val);
    }
}
