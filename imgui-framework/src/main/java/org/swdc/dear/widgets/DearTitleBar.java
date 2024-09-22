package org.swdc.dear.widgets;

import org.swdc.dear.DearColor;
import org.swdc.dear.DearComponent;
import org.swdc.dear.icons.Fontawsome5;
import org.swdc.dear.layout.Alignment;
import org.swdc.dear.layout.DearHBox;
import org.swdc.dear.layout.Justify;
import org.swdc.dear.listeners.MouseEventListener;


public class DearTitleBar extends DearComponent {

    private DearLabel text;

    private DearHBox layout;

    private DearButton close;

    private DearColor textColor;

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
        this.close.setText(Fontawsome5.getFontIcon("times"));
        this.close.setTextHoverColor(new DearColor("#FFF"));

        this.layout.addChild(text);
        this.layout.addChild(this.close);
        this.layout.getPaddings().left(12);
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

    @Override
    public void setComponentBackgroundColor(DearColor componentBackgroundColor) {
        super.setComponentBackgroundColor(componentBackgroundColor);
        close.setBackground(componentBackgroundColor);
    }

    public void setTextColor(DearColor textColor) {
        this.textColor = textColor;
        this.close.setTextColor(textColor);
        this.text.setTextColor(textColor);
    }

    public DearColor getTextColor() {
        return textColor;
    }

    public MouseEventListener getCloseRequestListener() {
        return close.getClickEventListener();
    }
}
