package org.swdc.dear.layout;

import org.swdc.dear.DearComponent;
import org.swdc.dear.DearWindow;
import org.swdc.imgui.core.ImGUICore;
import org.swdc.imgui.core.imgui.ImGuiStyle;
import org.swdc.imgui.core.imgui.ImVec2;

import java.util.ArrayList;
import java.util.List;

/**
 * HBox，横向布局
 */
public class DearHBox extends DearComponent {

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

        if (getHeight() == 0) {
            float max = 0;
            for (DearComponent component : children) {
                if (component.getHeight() > max) {
                    max = component.getHeight();
                }
            }
            setHeight(max);
        } else {
            for (DearComponent comp : children) {
                comp.setHeight(getHeight());
            }
        }

        DearComponent parent = getParent();
        DearWindow owner = getOwner();
        if (parent!= null) {
            setWidth(parent.getWidth());
        } else if (owner != null) {
            setWidth(owner.getWidth());
        }

    }

    @Override
    protected void update() {

        refreshSize();

        ImGuiStyle style = ImGUICore.ImGui_GetStyle();

        if (justify == Justify.START) {
            float posOffset = 0;
            for (int idx = 0; idx < children.size(); idx ++) {

                DearComponent comp = children.get(idx);
                comp.setX(posOffset);
                if (alignment == Alignment.BEGIN) {
                    comp.setY(0);
                } else if (alignment == Alignment.CENTER) {
                    comp.setY((getHeight() - comp.getHeight()) / 2f);
                } else if (alignment == Alignment.END) {
                    comp.setY(getHeight() - comp.getHeight());
                }
                posOffset = posOffset + comp.getWidth() + spacing;
                comp.doUpdate();

            }
        } else if (justify == Justify.END) {

            float posOffset = 0;
            for (int idx = children.size() - 1; idx >= 0; idx --) {

                DearComponent comp = children.get(idx);
                posOffset = posOffset + comp.getWidth();
                comp.setX(getWidth() - posOffset);
                posOffset = posOffset + spacing;
                if (alignment == Alignment.BEGIN) {
                    comp.setY(0);
                } else if (alignment == Alignment.CENTER) {
                    comp.setY((getHeight() - comp.getHeight()) / 2f);
                } else if (alignment == Alignment.END) {
                    comp.setY(getHeight() - comp.getHeight());
                }
                comp.doUpdate();

            }

        } else if (justify == Justify.CENTER) {

            float specTotal = (children.size() - 1) * spacing;
            float totalWidth = getTotalWidth();

            float begin = (getWidth() - specTotal - totalWidth) / 2.0f;
            float posOffset = 0;
            for (int idx = 0; idx < children.size(); idx ++) {

                DearComponent comp = children.get(idx);
                comp.setX(posOffset + begin);
                if (alignment == Alignment.BEGIN) {
                    comp.setY(0);
                } else if (alignment == Alignment.CENTER) {
                    comp.setY((getHeight() - comp.getHeight()) / 2f);
                } else if (alignment == Alignment.END) {
                    comp.setY( getHeight() - comp.getHeight());
                }
                posOffset = posOffset + comp.getWidth() + spacing;
                comp.doUpdate();

            }

        } else if (justify == Justify.SPACE) {

            float space = getWidth() / children.size();
            float posOffset = 0;
            for (int idx = 0; idx < children.size(); idx ++) {

                DearComponent comp = children.get(idx);
                comp.setX(posOffset + (space - comp.getWidth()) / 2f);
                if (alignment == Alignment.BEGIN) {
                    comp.setY(0);
                } else if (alignment == Alignment.CENTER) {
                    comp.setY((getHeight() - comp.getHeight()) / 2f);
                } else if (alignment == Alignment.END) {
                    comp.setY(getHeight() - comp.getHeight());
                }
                posOffset = posOffset + space;
                comp.doUpdate();

            }

        } else if (justify == Justify.BETWEEN) {

            float totalWidth = getTotalWidth();
            float width = getWidth();

            float spacing = (width - totalWidth) / (children.size() - 1);
            float posOffset = 0;
            for (int idx = 0; idx < children.size(); idx ++) {

                DearComponent comp = children.get(idx);
                comp.setX( posOffset);
                if (alignment == Alignment.BEGIN) {
                    comp.setY(0);
                } else if (alignment == Alignment.CENTER) {
                    comp.setY( (getHeight() - comp.getHeight()) / 2f);
                } else if (alignment == Alignment.END) {
                    comp.setY( getHeight() - comp.getHeight());
                }
                posOffset = posOffset + comp.getWidth() + spacing;
                comp.doUpdate();

            }
        }

    }

    public Justify getJustify() {
        return justify;
    }

    public void setJustify(Justify justify) {
        this.justify = justify;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    private float getTotalWidth() {
        float totalWidth = 0;
        for (DearComponent comp: children) {
            totalWidth = totalWidth + comp.getWidth();
        }
        return totalWidth;
    }

    public void setSpacing(float spacing) {
        this.spacing = spacing;
    }

    public float getSpacing() {
        return spacing;
    }
}
