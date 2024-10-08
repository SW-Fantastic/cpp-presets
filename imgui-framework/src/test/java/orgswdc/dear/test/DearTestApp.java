package orgswdc.dear.test;

import org.swdc.dear.DearApplication;
import org.swdc.dear.DearColor;
import org.swdc.dear.DearWindow;
import org.swdc.dear.icons.Fontawsome5;
import org.swdc.dear.layout.DearBorderPane;
import org.swdc.dear.layout.DearHBox;
import org.swdc.dear.layout.DearVBox;
import org.swdc.dear.layout.Justify;
import org.swdc.dear.widgets.*;
import org.swdc.imgui.core.ImGUICore;

import java.io.File;

public class DearTestApp extends DearApplication {

    private DearWindow window = null;

    @Override
    protected void active() {

        DearHBox box = new DearHBox();
        DearLabel label = new DearLabel();
        label.setText("Hello");
        label.setWidth(80);
        label.setHeight(40);

        DearLabel lblWorld = new DearLabel();
        lblWorld.setText("World !!!");
        lblWorld.setWidth(120);
        lblWorld.setHeight(40);

        DearVBox vBox = new DearVBox();
        vBox.setWidth(200);

        DearLabel vbxLA = new DearLabel();
        vbxLA.setText("Hello world");
        vbxLA.setWidth(160);
        vbxLA.setHeight(40);
        vBox.addChild(vbxLA);

        DearButton vbxBtn = new DearButton();
        vbxBtn.setText("Button");
        vbxBtn.setBorder(true);
        vbxBtn.setBorderColor(new DearColor("#F00"));
        vbxBtn.setHeight(60);
        vbxBtn.setWidth(120);
        vbxBtn.setBackground(new DearColor("#4B8FFF"));
        vbxBtn.setTextColor(new DearColor("#FFF"));
        vbxBtn.setClickEventListener(() -> {
            System.err.println("clicked");
        });
        vBox.addChild(vbxBtn);

        DearField field = new DearField();
        field.setWidth(120);
        field.setBorderColor(new DearColor("#999"));
        field.setHeight(40);
        field.setBorder(true);
        vBox.addChild(field);

        DearTextArea area = new DearTextArea();
        area.setBorder(true);
        area.setWidth(120);
        area.setHeight(80);
        area.setBorderColor(new DearColor("#999"));
        vBox.addChild(area);

        DearComboBox comb = new DearComboBox();
        comb.setWidth(120);
        comb.setHeight(38);
        comb.getPaddings().top(6).bottom(6);
        comb.setItemPadding(18);
        comb.setBorder(true);
        comb.setBorderColor(new DearColor("#999"));
        vBox.addChild(comb);

        DearLabel vbxLB = new DearLabel();
        vbxLB.setText("Hello world 2");
        vbxLB.setWidth(160);
        vbxLB.setHeight(40);
        vBox.addChild(vbxLB);

        box.setSpacing(8);
        box.addChild(label);
        box.addChild(lblWorld);
        box.addChild(vBox);

        DearBorderPane borderPane = new DearBorderPane();

        DearButton top = new DearButton();
        top.setText("Top label");
        top.setWidth(120);
        top.setHeight(60);
        borderPane.setTop(top);

        DearLabel left = new DearLabel();
        left.setText("Left label");
        left.setWidth(80);
        left.setHeight(40);
        borderPane.setLeft(left);

        DearLabel right = new DearLabel();
        right.setText("Right");
        right.setHeight(40);
        right.setWidth(60);
        borderPane.setRight(right);

        DearLabel center = new DearLabel();
        center.setText("Center");
        center.setWidth(120);
        center.setHeight(40);
        borderPane.setCenter(center);

        DearCheckBox cbx = new DearCheckBox(   "CheckBox " + Fontawsome5.getFontIcon("power-off"));
        cbx.setWidth(120);
        cbx.setHeight(60);
        borderPane.setBottom(cbx);

        box.addChild(borderPane);
        box.setComponentBackgroundColor(new DearColor("#FFF"));
        box.setJustify(Justify.START);
        window = new DearWindow(this,"主窗口3");
        window.setContent(box);
        window.show();

    }

    public static void main(String[] args) {

        DearTestApp app = new DearTestApp();
        app.setDefaultFont(new File("imgui-framework/SourceHanSansCN-Normal.otf"));
        app.launch(args);

    }

}
