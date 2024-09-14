package org.swdc.dear.layout;

import org.swdc.dear.DearComponent;
import org.swdc.dear.DearDirection;
import org.swdc.imgui.core.ImGUICore;
import org.swdc.imgui.core.imgui.ImGuiStyle;
import org.swdc.imgui.core.imgui.ImVec2;

import java.util.*;

/**
 * BorderLayout，
 * 边界布局
 */
public class DearBorderPane extends DearComponent {


    private Map<DearDirection,DearComponent> components = new HashMap<>();


    @Override
    protected void update() {


        DearComponent top = getTop();
        DearComponent bottom = getBottom();
        DearComponent left = getLeft();
        DearComponent right = getRight();
        DearComponent center = getCenter();

        float borderWidth =  (left == null ? 0 : left.getWidth()) +
                (right == null ? 0 : right.getWidth()) ;

        float borderHeight = (top == null ? 0 : top.getHeight()) +
                (bottom == null ? 0 : bottom.getHeight()) ;


        if (getWidth() == 0) {
            setWidth(borderWidth + (center == null ? 0 : center.getWidth()));
        }
        if (getHeight() == 0) {
            setHeight(borderHeight + (center == null ? 0 : center.getHeight()));
        }

        float centerY = 0;

        if (top != null) {
            top.setWidth(getWidth());
            top.setX(0);
            top.setY(0);
            centerY = top.getHeight();
            top.doUpdate();
        }

        if (center != null) {
            if (left != null && right != null) {

                left.setX(0);
                left.setY(centerY);
                left.setHeight(getHeight() - borderHeight);
                left.doUpdate();

                center.setX(left.getWidth());
                center.setWidth(getWidth() - left.getWidth() - right.getWidth());
                center.setHeight(getHeight() - borderHeight);
                center.setY(centerY);
                center.doUpdate();

                right.setX(left.getWidth() + center.getWidth());
                right.setY(centerY);
                right.setHeight(getHeight() - borderHeight);
                right.doUpdate();

            } else if (left != null) {

                left.setX(0);
                left.setY(centerY);
                left.setHeight(getHeight() - borderHeight );
                left.doUpdate();

                center.setX(left.getWidth());
                center.setWidth(getWidth() - left.getWidth());
                center.setHeight(getHeight() - borderHeight );
                center.setY(centerY);
                center.doUpdate();

            } else if (right != null) {
                center.setX(0);
                center.setWidth(getWidth() - right.getWidth());
                center.setHeight(getHeight() - borderHeight);
                center.setY(centerY);
                center.doUpdate();

                right.setX(center.getWidth());
                right.setY(centerY);
                right.setHeight(getHeight() - borderHeight );
                right.doUpdate();
            }
        }

        if (bottom != null) {
            bottom.setX(0);
            bottom.setWidth(getWidth());
            bottom.setY(centerY + getHeight() - borderHeight);
            bottom.doUpdate();
        }

    }


    public void setLeft(DearComponent component) {
        component.setParent(this);
        components.put(DearDirection.LEFT,component);
    }

    public <T extends DearComponent> T getLeft() {
        return (T)components.get(DearDirection.LEFT);
    }

    public void setRight(DearComponent component) {
        component.setParent(this);
        components.put(DearDirection.RIGHT,component);
    }

    public <T extends DearComponent> T getRight() {
        return (T)components.get(DearDirection.RIGHT);
    }

    public void setTop(DearComponent component) {
        component.setParent(this);
        components.put(DearDirection.TOP,component);
    }

    public <T extends DearComponent> T getTop() {
        return (T)components.get(DearDirection.TOP);
    }

    public void setBottom(DearComponent component) {
        component.setParent(this);
        components.put(DearDirection.BOTTOM,component);
    }

    public <T extends DearComponent> T getBottom() {
        return (T)components.get(DearDirection.BOTTOM);
    }

    public void setCenter(DearComponent component) {

        components.put(DearDirection.CENTER,component);
        component.setParent(this);

    }

    public <T extends DearComponent> T getCenter() {
        return (T)components.get(DearDirection.CENTER);
    }

}
