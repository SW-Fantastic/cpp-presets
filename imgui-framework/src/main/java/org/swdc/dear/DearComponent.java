package org.swdc.dear;

import org.swdc.dear.listeners.ChangedListener;
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

    private ImVec2 absolutePos = new ImVec2(1);

    private MouseEventListener hoverEventListener;

    private MouseEventListener activeEventListener;

    private MouseEventListener deActiveEventListener;

    private DearColor componentBackgroundColor;

    private DearSizeBox paddingSizeBox = new DearSizeBox();

    private DearColor borderColor;

    private boolean border;

    private float borderSize = 1f;

    protected boolean hovering = false;

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
        ImGUICore.ImGui_BeginChild(compId,size,ImGUICore.ImGuiChildFlags_None,ImGUICore.ImGuiWindowFlags_None);

        int rollback = 0;
        int styleRollback = 0;
        if (componentBackgroundColor != null) {
            ImGUICore.ImGui_PushStyleColorImVec4(ImGUICore.ImGuiCol_ChildBg,componentBackgroundColor.getColor());
            rollback ++;
        }


        if (borderColor != null && border) {
            ImGUICore.ImGui_PushStyleColorImVec4(ImGUICore.ImGuiCol_Border,borderColor.getColor());
            rollback ++;
            ImGUICore.ImGui_PushStyleVar(ImGUICore.ImGuiStyleVar_FrameBorderSize,borderSize);
            styleRollback ++;
        }

        refreshPos(true);
        ImGUICore.ImGui_SetCursorPos(pos);
        refreshPos(true);

        ImGUICore.ImGui_BeginChild(compId,size,ImGUICore.ImGuiChildFlags_None,ImGUICore.ImGuiWindowFlags_None);
        update();

        ImGUICore.ImGui_EndChild();
        ImGUICore.ImGui_PopStyleColorEx(rollback);
        ImGUICore.ImGui_PopStyleVarEx(styleRollback);

        if (ImGUICore.ImGui_IsItemHovered(0)) {
            hovering = true;
            preformAsyncListener(hoverEventListener);
        } else {
            hovering = false;
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
        return width + paddingSizeBox.right() + paddingSizeBox.left();
    }

    public float getInnerWidth() {
        return width;
    }

    public float getInnerHeight() {
        return height;
    }

    public void setWidth(float width) {
        this.width = width - paddingSizeBox.left() - paddingSizeBox.right();
    }

    public float getHeight() {
        return height + paddingSizeBox.top() + paddingSizeBox.bottom();
    }

    public void setHeight(float height) {
        this.height = height - paddingSizeBox.top() - paddingSizeBox.bottom();
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
            pos.x(paddingSizeBox.left());
            pos.y(paddingSizeBox.top());
            size.x(getWidth() - paddingSizeBox.left() - paddingSizeBox.right());
            size.y(getHeight() - paddingSizeBox.top() - paddingSizeBox.bottom());
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

    protected ImVec2 getInnerPos() {
        refreshPos(true);
        return pos;
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

    protected <T> void preformAsyncListener(ChangedListener<T> listener, T val) {
        if (listener == null){
            return;
        }
        Thread.ofVirtual().start(() -> listener.onChanged(val));
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
        if (this.componentBackgroundColor != null) {
            this.componentBackgroundColor.close();
        }
        this.componentBackgroundColor = componentBackgroundColor;
    }

    public DearColor getComponentBackgroundColor() {
        return componentBackgroundColor;
    }

    public DearSizeBox getPaddings() {
        return paddingSizeBox;
    }

    public void setPaddings(float padding) {
        paddingSizeBox.top(padding)
                .left(padding)
                .right(padding)
                .bottom(padding);
    }

    public ImVec2 getAbsolutePos() {

        float x = 0;
        float y = 0;
        if (getParent() != null) {
            x = parent.getAbsolutePos().x() + this.x + this.paddingSizeBox.left();
            y = parent.getAbsolutePos().y() + this.y + this.paddingSizeBox.top();
        } else {
            ImVec2 abs = getOwner().getAbsolutePos();
            x = abs.x() + this.x + this.paddingSizeBox.left();
            y = abs.y() + this.y + this.paddingSizeBox.top();
        }

        absolutePos.x(x);
        absolutePos.y(y);

        return absolutePos;
    }

    public DearColor getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(DearColor borderColor) {
        this.borderColor = borderColor;
    }

    public void setBorder(boolean border) {
        this.border = border;
    }

    public boolean isBorder() {
        return border;
    }

    public float getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(float borderSize) {
        this.borderSize = borderSize;
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
