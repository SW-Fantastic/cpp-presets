package org.swdc.dear.layout;

import org.swdc.dear.DearComponent;
import org.swdc.dear.DearWindow;
import org.swdc.imgui.core.ImGUICore;
import org.swdc.imgui.core.imgui.ImGuiStyle;
import org.swdc.imgui.core.imgui.ImVec2;

import java.util.ArrayList;
import java.util.List;

/**
 * VBox，纵向布局
 */
public class DearVBox extends DearComponent {

    private float spacing;

    private Justify justify = Justify.START;

    private Alignment alignment = Alignment.BEGIN;

    private List<DearComponent> children = new ArrayList<>();

    public void addChild(DearComponent component) {
        if (component == null || children.contains(component)) {
            return;
        }
        children.add(component);
        component.setParent(this);
        refreshSize();
    }

    private void refreshSize() {

        if (getWidth() == 0) {
            float max = 0;
            for (DearComponent component : children) {
                if (component.getWidth() > max) {
                    max = component.getWidth();
                }
            }
            setWidth(max);
        } else {
            for (DearComponent comp : children) {
                comp.setWidth(getWidth());
            }
        }


        DearComponent parent = getParent();
        DearWindow owner = getOwner();
        if (parent!= null) {
            setHeight(parent.getHeight());
        } else if (owner != null) {
            setHeight(owner.getHeight());
        }

    }

    @Override
    protected void update() {

        refreshSize();

        if (justify == Justify.START) {

            float offset = 0;
            for (int index = 0; index < children.size(); index ++) {

                DearComponent comp = children.get(index);
                if (alignment == Alignment.BEGIN) {
                    comp.setX(0);
                } else if (alignment == Alignment.CENTER) {
                    comp.setX(comp.getWidth() / 2f);
                } else if (alignment == Alignment.END) {
                    comp.setX(getWidth() - comp.getWidth());
                }
                comp.setY(offset);
                offset = offset + comp.getHeight() + spacing;
                comp.doUpdate();

            }

        }

    }

    public void setSpacing(float spacing) {
        this.spacing = spacing;
    }

    public float getSpacing() {
        return spacing;
    }

    public void setJustify(Justify justify) {
        this.justify = justify;
    }

    public Justify getJustify() {
        return justify;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    public Alignment getAlignment() {
        return alignment;
    }


}
