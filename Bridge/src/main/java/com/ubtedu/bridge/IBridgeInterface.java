/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.bridge;

import android.webkit.JavascriptInterface;

/**
 * Bridge框架接口，对外部模块暴露{@link #call(String)}和{@link OnCallback}接口。
 * @Author qinicy
 * @Date 2018/10/8
 **/
public interface IBridgeInterface {
    /**
     * other ==> android，bridge外部模块调用android call接口
     * @param args {@link CallArguments}
     * @return bridge result
     */
    @JavascriptInterface
    String call(String args);

    /**
     * other ==> android，bridge外部模块调用android onCallback接口
     * @param result {@link BridgeResult}
     */
    @JavascriptInterface
    void onCallback(String result);
}
