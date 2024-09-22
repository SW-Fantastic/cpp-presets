package org.swdc.dear;

import java.util.HashMap;
import java.util.Map;

public class DearSizeBox {

    private Map<DearDirection, Float> paddings = new HashMap<>();


    public float left() {
        return paddings.getOrDefault(DearDirection.LEFT, 0f);
    }

    public float right() {
        return paddings.getOrDefault(DearDirection.RIGHT, 0f);
    }

    public float top() {
        return paddings.getOrDefault(DearDirection.TOP,0f);
    }

    public float bottom() {
        return paddings.getOrDefault(DearDirection.BOTTOM, 0f);
    }

    public DearSizeBox left(float val) {
        paddings.put(DearDirection.LEFT, val);
        return this;
    }

    public DearSizeBox right(float val) {
        paddings.put(DearDirection.RIGHT, val);
        return this;
    }

    public DearSizeBox top(float val) {
        paddings.put(DearDirection.TOP, val);
        return this;
    }

    public DearSizeBox bottom(float val) {
        paddings.put(DearDirection.BOTTOM, val);
        return this;
    }

}
