/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.bridge;


/**
 * {@link IBridgeHandler}是Bridge框架中负责处理与外部（JS或Unity3D）交互通信的抽象类，
 * 与不同外部模块交互通信需要不同的实现类，Javascript的实现类是{@link JsBridgeHandler},
 * 其它平台的实现类需要去实现。</br>{@link IBridgeHandler}主要负责两件事:
 *
 * <ul>
 *     <li>把BridgeInterface注册到外部模块，向外部模块暴露`call`和`onCallback`接口</li>
 *     <li>调用外部模块暴露的`call`和`onCallback`接口</li>
 * </ul>
 * @Author qinicy
 * @Date 2018/10/8
 **/
public interface IBridgeHandler extends ICall,OnCallback,ISendCallback{
    /**
     * 注册{@link IBridgeInterface#call(String)}或{@link IBridgeInterface#onCallback(String)}两个bridge
     * 框架接口，暴露给外部模块调用
     * @param bridgeInterface {@link IBridgeInterface#call(String)}或{@link IBridgeInterface#onCallback(String)}
     */
    void registerFrameworkInterFace(IBridgeInterface bridgeInterface);
}
