package org.swdc.dear.widgets;

import org.bytedeco.javacpp.BoolPointer;
import org.bytedeco.javacpp.BytePointer;
import org.swdc.dear.DearComponent;
import org.swdc.dear.DearSizeBox;
import org.swdc.dear.listeners.ChangedListener;
import org.swdc.dear.listeners.MouseEventListener;
import org.swdc.imgui.core.ImGUICore;
import org.swdc.imgui.core.imgui.ImGuiStyle;
import org.swdc.imgui.core.imgui.ImVec2;

public class DearCheckBox extends DearComponent {

    private BoolPointer checked = null;

    private BytePointer text;

    private float size;

    private ChangedListener<Boolean> stateChanged;

    private MouseEventListener clickListener;

    public DearCheckBox(String label) {

        this.text = new BytePointer(label);
        this.checked = new BoolPointer(1);

    }

    @Override
    protected void update() {
        boolean val = checked.get();
        if(ImGUICore.ImGui_Checkbox(text,checked)) {
            if (stateChanged != null && val != checked.get()) {
                preformAsyncListener(stateChanged,checked.get());
            }
            preformAsyncListener(clickListener);
        }
    }

    public void setBoxSize(float size) {
        this.size = size;
    }

    public float getBoxSize() {
        return size;
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

    public ChangedListener<Boolean> getStateChanged() {
        return stateChanged;
    }

    public void setStateChanged(ChangedListener<Boolean> stateChanged) {
        this.stateChanged = stateChanged;
    }

    public void setClickListener(MouseEventListener clickListener) {
        this.clickListener = clickListener;
    }

    public MouseEventListener getClickListener() {
        return clickListener;
    }
}
