package org.swdc.dear;

import org.swdc.dear.listeners.MouseEventListener;
import org.swdc.imgui.core.ImGUICore;
import org.swdc.imgui.core.imgui.ImGuiStyle;
import org.swdc.imgui.core.imgui.ImVec2;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DearComponent implements Closeable {

    private static Map<Class, Long> componentId = new HashMap<>();

    private float x;

    private float y;

    private float width;

    private float height;

    private DearComponent parent;

    private DearWindow owner;

    private String compId;

    private ImVec2 pos = new ImVec2(1);

    private ImVec2 size = new ImVec2(1);

    private MouseEventListener hoverEventListener;

    private MouseEventListener activeEventListener;

    private MouseEventListener deActiveEventListener;

    private DearColor componentBackgroundColor;

    private DearSizeBox sizeBox = new DearSizeBox();

    protected static synchronized String createId(Class compType) {
        Long val = componentId.get(compType);
        if (val == null) {
            componentId.put(compType,0L);
            return compType.getSimpleName() + "_0";
        }
        componentId.put(compType, val + 1);
        return compType.getSimpleName() + "_" + val + 1;
    }


    public DearComponent() {
        this((DearComponent) null);
    }

    public DearComponent(DearWindow window) {
        this((DearComponent) null);
        this.owner = window;
    }

    public DearComponent(DearComponent component) {

        this.parent = component;

        pos.x(0);
        pos.y(0);

        size.x(0);
        size.y(0);

    }

    protected void update() {

    }

    public void setOwner(DearWindow window) {
        this.owner = window;
        this.parent = null;
    }

    public void setParent(DearComponent component) {
        this.parent = component;
        this.owner = parent.getOwner();
    }

    public void doUpdate() {

        if (this.compId == null) {
            compId = createId(this.getClass());
        }

        ImGuiStyle style = ImGUICore.ImGui_GetStyle();
        style.WindowPadding().x(0);
        style.WindowPadding().y(0);

        refreshPos(false);
        ImGUICore.ImGui_SetCursorPos(pos);
        refreshPos(false);
        ImGUICore.ImGui_BeginChild(compId,size,ImGUICore.ImGuiChildFlags_Borders,ImGUICore.ImGuiWindowFlags_None);
        int rollback = 0;
        if (componentBackgroundColor != null) {
            ImGUICore.ImGui_PushStyleColorImVec4(ImGUICore.ImGuiCol_ChildBg,componentBackgroundColor.getColor());
            rollback ++;
        }

        refreshPos(true);
        ImGUICore.ImGui_SetCursorPos(pos);
        refreshPos(true);
        ImGUICore.ImGui_BeginChild(compId,size,ImGUICore.ImGuiChildFlags_Borders,ImGUICore.ImGuiWindowFlags_None);

        update();

        ImGUICore.ImGui_EndChild();

        ImGUICore.ImGui_PopStyleColorEx(rollback);

        if (ImGUICore.ImGui_IsItemHovered(0)) {
            preformAsyncListener(hoverEventListener);
        }

        if (ImGUICore.ImGui_IsItemHovered(0)) {
            preformAsyncListener(hoverEventListener);
        }

        if (ImGUICore.ImGui_IsItemActive()) {
            preformAsyncListener(activeEventListener);
        }

        if (ImGUICore.ImGui_IsItemDeactivated()) {
            preformAsyncListener(deActiveEventListener);
        }

        ImGUICore.ImGui_EndChild();

    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width + sizeBox.paddingRight() + sizeBox.paddingLeft();
    }

    public void setWidth(float width) {
        this.width = width - sizeBox.paddingLeft() - sizeBox.paddingRight();
    }

    public float getHeight() {
        return height + sizeBox.paddingTop() + sizeBox.paddingBottom();
    }

    public void setHeight(float height) {
        this.height = height - sizeBox.paddingTop() - sizeBox.paddingBottom();
    }

    public String getCompId() {
        return compId;
    }

    public void setCompId(String compId) {
        this.compId = compId;
    }

    public DearComponent getParent() {
        return parent;
    }

    public DearWindow getOwner() {
        if (owner != null) {
            return owner;
        } else if (parent != null) {
            return parent.getOwner();
        }
        return null;
    }

    private void refreshPos(boolean insets) {
        if (insets) {
            pos.x(sizeBox.paddingLeft());
            pos.y(sizeBox.paddingTop());
            size.x(getWidth() - sizeBox.paddingLeft() - sizeBox.paddingRight());
            size.y(getHeight() - sizeBox.paddingTop() - sizeBox.paddingBottom());
        } else {
            pos.x(getX());
            pos.y(getY());
            size.x(getWidth());
            size.y(getHeight());
        }

    }

    protected ImVec2 getSize() {
        refreshPos(false);
        return size;
    }

    protected ImVec2 getInnerSize() {
        refreshPos(true);
        return size;
    }

    protected ImVec2 getPos() {
        refreshPos(false);
        return pos;
    }

    protected void preformAsyncListener(MouseEventListener listener) {
        if (listener == null) {
            return;
        }
        Thread.ofVirtual().start(listener::accept);
    }

    public MouseEventListener getHoverEventListener() {
        return hoverEventListener;
    }

    public void setHoverEventListener(MouseEventListener hoverEventListener) {
        this.hoverEventListener = hoverEventListener;
    }

    public MouseEventListener getActiveEventListener() {
        return activeEventListener;
    }

    public void setActiveEventListener(MouseEventListener activeEventListener) {
        this.activeEventListener = activeEventListener;
    }

    public MouseEventListener getDeActiveEventListener() {
        return deActiveEventListener;
    }

    public void setDeActiveEventListener(MouseEventListener deActiveEventListener) {
        this.deActiveEventListener = deActiveEventListener;
    }

    public void setComponentBackgroundColor(DearColor componentBackgroundColor) {
        this.componentBackgroundColor = componentBackgroundColor;
    }

    public DearColor getComponentBackgroundColor() {
        return componentBackgroundColor;
    }

    public DearSizeBox getSizeBox() {
        return sizeBox;
    }

    @Override
    public void close() throws IOException {

        if (pos != null && !pos.isNull()) {
            pos.close();
            pos = null;
        }

        if (size != null && !size.isNull()) {
            size.close();
            size = null;
        }

    }
}
