/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.bridge;

/**
 * 把外部模块调用API的执行结果发送回去
 * @Author qinicy
 * @Date 2018/10/10
 **/
public interface ISendCallback {
    /**
     *
     * @param result {@link BridgeResult}
     */
    void sendCallbackResult(BridgeResult result);
}
