package org.swdc.dear.layout;

import org.swdc.dear.DearComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * BorderLayout，
 * 边界布局
 */
public class DearBorderPane extends DearComponent {

    private enum Location {

        TOP,
        LEFT,
        RIGHT,
        CENTER,
        BOTTOM

    }

    private Map<Location,DearComponent> components = new HashMap<>();

    public void setLeft(DearComponent component) {
        component.setParent(this);
        components.put(Location.LEFT,component);
    }

    public <T extends DearComponent> T getLeft() {
        return (T)components.get(Location.LEFT);
    }

    public void setRight(DearComponent component) {
        component.setParent(this);
        components.put(Location.RIGHT,component);
    }

    public <T extends DearComponent> T getRight() {
        return (T)components.get(Location.RIGHT);
    }

    public void setTop(DearComponent component) {
        component.setParent(this);
        components.put(Location.TOP,component);
    }

    public <T extends DearComponent> T getTop() {
        return (T)components.get(Location.TOP);
    }

    public void setBottom(DearComponent component) {
        component.setParent(this);
        components.put(Location.BOTTOM,component);
    }

    public <T extends DearComponent> T getBottom() {
        return (T)components.get(Location.BOTTOM);
    }

    public void setCenter(DearComponent component) {
        components.put(Location.CENTER,component);
        component.setParent(this);
    }

    public <T extends DearComponent> T getCenter() {
        return (T)components.get(Location.CENTER);
    }

}
