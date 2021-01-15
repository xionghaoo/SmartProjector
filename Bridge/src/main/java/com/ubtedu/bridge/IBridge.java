/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.bridge;

/**
 * @Author qinicy
 * @Date 2018/10/8
 **/
public interface IBridge extends ICall{
    /**
     * 注册业务API到bridge框架，提供给外部模块调用
     * @param api api定义的类对象
     * @param namespace api命名空间名称
     */
    void addBridgeAPI(IBridgeAPI api, String namespace);
    /**
     * bridge框架是否已与外部模块处于可通信状态。在调用{@link ICall#call(String, Object[], OnCallback)}
     * 方法之前，需要判断bridge框架是否处于可通信状态，因为外部模块加载是需要时间的，
     * 只有外部模块加载完了，才可以通信。
     * @return true:可通信，false:不可通信
     */
    boolean isCommunicable();
}
