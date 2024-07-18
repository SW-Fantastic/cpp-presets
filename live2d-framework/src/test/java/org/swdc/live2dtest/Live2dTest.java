package org.swdc.live2dtest;

import com.live2d.sdk.cubism.core.ICubismLogger;
import com.live2d.sdk.cubism.framework.CubismFramework;
import com.live2d.sdk.cubism.framework.model.CubismMoc;
import com.live2d.sdk.cubism.framework.model.CubismModel;
import org.bytedeco.javacpp.Loader;
import org.swdc.live2d.core.Live2dCore;

import java.io.FileInputStream;
import java.io.IOException;

public class Live2dTest {

    public static void main(String[] args) throws IOException {

        if (!CubismFramework.isInitialized()) {
            CubismFramework.Option option = new CubismFramework.Option();
            option.setLogFunction(new ICubismLogger() {
                @Override
                public void print(String msg) {
                    System.err.println(msg);
                }

            });

            CubismFramework.startUp(option);
            CubismFramework.initialize();
        }

        FileInputStream fileInputStream = new FileInputStream("live2d-framework/assets/Haru/Haru.moc3");
        byte[] mocData = fileInputStream.readAllBytes();
        CubismMoc moc = CubismMoc.create(mocData);
        CubismModel model = moc.createModel();
        System.err.println(moc.getMocVersion());
        model.close();
        moc.delete();

    }

}
