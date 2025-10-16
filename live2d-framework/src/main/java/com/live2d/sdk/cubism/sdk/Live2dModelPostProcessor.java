package com.live2d.sdk.cubism.sdk;

/**
 * 模型后处理接口，在一个模型被加载后尚未用于渲染的时候，
 * 通过本接口可以进行一些额外的处理操作，例如修改模型渲染时的ParameterId等。
 * @param <M> 模型类，与渲染器相关。例如Live2dJOGLModel，Live2dLWGLModel等。
 */
public interface Live2dModelPostProcessor<M> {

    void process(M model);

}
