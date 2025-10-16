package com.live2d.sdk.cubism.sdk;

import com.live2d.sdk.cubism.framework.id.CubismId;
import com.live2d.sdk.cubism.framework.model.CubismModel;
import com.live2d.sdk.cubism.framework.utils.jsonparser.ACubismJsonValue;
import com.live2d.sdk.cubism.framework.utils.jsonparser.CubismJson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 这是一个神奇的类，它允许我们跳过部分Live2D网格的渲染，
 * 通过Live2D Cubism Viewer，我们可以到不需要渲染的网格的Index，
 * 并且通过filter.json在渲染的过程中移除它们。
 */
public class Live2dFilterJson {

    public CubismJson json;

    private int[] skipVertexIndices;

    private List<String> parts = new ArrayList<>();

    public Live2dFilterJson(byte[] buffer) {

        if (buffer == null || buffer.length == 0) {
            skipVertexIndices = new int[0];
        } else {
            json = CubismJson.create(buffer);
            List<ACubismJsonValue> values = json.getRoot().get("Graphics").getList();
            if (values != null) {
                skipVertexIndices = values.stream()
                        .map(v -> v.getString("-1"))
                        .map(Double::parseDouble)
                        .mapToInt(Double::intValue)
                        .filter(i -> i != -1)
                        .toArray();
            }
            List<ACubismJsonValue> parts = json.getRoot().get("Parts").getList();
            if (parts != null) {
                this.parts = parts.stream()
                        .map(ACubismJsonValue::getString)
                        .collect(Collectors.toList());
            }
        }

    }

    public void setupModel(CubismModel model) {

        for (int i = 0; i < model.getPartCount(); i ++) {
            CubismId partId = model.getPartId(i);
            if (this.parts.contains(partId.getString())) {
                model.setPartOpacity(i, 0);
            }
        }
        model.setFilteredIndices(skipVertexIndices);

    }

}
