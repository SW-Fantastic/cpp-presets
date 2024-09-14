package org.swdc.dear;

import java.util.HashMap;
import java.util.Map;

public class DearSizeBox {

    private Map<DearDirection, Float> paddings = new HashMap<>();


    public float paddingLeft() {
        return paddings.getOrDefault(DearDirection.LEFT, 0f);
    }

    public float paddingRight() {
        return paddings.getOrDefault(DearDirection.RIGHT, 0f);
    }

    public float paddingTop() {
        return paddings.getOrDefault(DearDirection.TOP,0f);
    }

    public float paddingBottom() {
        return paddings.getOrDefault(DearDirection.BOTTOM, 0f);
    }

    public DearSizeBox paddingLeft(float val) {
        paddings.put(DearDirection.LEFT, val);
        return this;
    }

    public DearSizeBox paddingRight(float val) {
        paddings.put(DearDirection.RIGHT, val);
        return this;
    }

    public DearSizeBox paddingTop(float val) {
        paddings.put(DearDirection.TOP, val);
        return this;
    }

    public DearSizeBox paddingBottom(float val) {
        paddings.put(DearDirection.BOTTOM, val);
        return this;
    }

}
