package org.swdc.dear.widgets;

import org.swdc.dear.DearComponent;
import org.swdc.dear.DearWindow;
import org.swdc.dear.layout.Alignment;
import org.swdc.dear.layout.DearHBox;
import org.swdc.dear.layout.Justify;
import org.swdc.dear.listeners.MouseEventListener;


public class DearTitleBar extends DearComponent {

    private DearLabel text;

    private DearHBox layout;

    private DearButton close;

    private MouseEventListener closeRequestListener;

    public DearTitleBar() {
        this.layout = new DearHBox();
        this.layout.setJustify(Justify.BETWEEN);
        this.layout.setAlignment(Alignment.CENTER);

        this.text = new DearLabel();
        this.text.setText("Window");

        this.close = new DearButton();
        this.close.setWidth(48);
        this.close.setHeight(48);
        this.close.setText("X");

        this.layout.addChild(text);
        this.layout.addChild(this.close);
        this.layout.setHeight(48);
    }

    @Override
    public float getHeight() {
        return layout.getHeight();
    }

    @Override
    public float getWidth() {
        return layout.getWidth();
    }

    @Override
    public void setHeight(float height) {
        layout.setHeight(height);
    }

    @Override
    public void setWidth(float width) {
        layout.setWidth(width);
    }

    @Override
    protected void update() {
        layout.doUpdate();
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public String getText() {
        return text.getText();
    }

    public void setCloseRequestListener(MouseEventListener closeRequestListener) {
        close.setClickEventListener(closeRequestListener);
    }

    public MouseEventListener getCloseRequestListener() {
        return close.getClickEventListener();
    }
}
