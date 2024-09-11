package org.swdc.dear;

import org.swdc.dear.listeners.MouseEventListener;
import org.swdc.imgui.core.ImGUICore;
import org.swdc.imgui.core.imgui.ImVec2;
import org.swdc.imgui.core.imgui.ImVec4;

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

        refreshPos();
        ImGUICore.ImGui_SetCursorPos(pos);
        refreshPos();
        ImGUICore.ImGui_BeginChild(compId,size,ImGUICore.ImGuiChildFlags_None,ImGUICore.ImGuiWindowFlags_None);
        update();

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
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
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

    private void refreshPos() {
        pos.x(getX());
        pos.y(getY());
        size.x(getWidth());
        size.y(getHeight());
    }

    protected ImVec2 getSize() {
        refreshPos();
        return size;
    }

    protected ImVec2 getPos() {
        refreshPos();
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
