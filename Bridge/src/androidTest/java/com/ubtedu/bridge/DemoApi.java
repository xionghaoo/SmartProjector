/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.bridge;

import android.webkit.JavascriptInterface;

/**
 * @Author qinicy
 * @Date 2019/3/27
 **/
public class DemoApi extends BaseBridgeAPI {
    /**
     * 命名空间，别的模块调用时，func参数需要添加命名空间。如:
     * {
     * "id": 1,
     * "func": "demo.jsApi",
     * "args": ["Hello"],
     * "callback": "callbackName"
     * }
     */
    @Override
    public String getNamespace() {
        return "demo";
    }

    /**
     * 如果是JavascriptAPI，定义API时需要添加JavascriptInterface注解
     */
    @Override
    public boolean isJavascriptAPI() {
        return true;
    }

    /**
     * 不再建议这么使用
     *如果API返回值是BridgeResult，API内部的执行逻辑成功或失败，可以通过BridgeResult返回
     */
    @JavascriptInterface
    public BridgeResult apiReturnBridgeResult(String message) {
        if (message != null) {
            return BridgeResult.SUCCESS().data(message);
        }else {
            return BridgeResult.FAIL();
        }
    }
    /**
     *如果API返回值非BridgeResult，API内部的执行逻辑成功或失败，视情况而定。如果成功，直接返回。
     * 如果失败，throw一个Exception，bridge框架会捕捉该Exception
     */
    @JavascriptInterface
    public int apiReturnOther(String message) throws Exception {
        if (message != null) {
            return 0;
        }
        throw BridgeException.ILLEGAL_ARGUMENTS();
    }

    /**
     *不再建议这么使用
     */
    @Deprecated
    @JavascriptInterface
    public void apiReturnBridgeResultAsync(String message,APICallback<String> callback) {
        if (message != null) {
            callback.onCallback(BridgeResult.SUCCESS().data(message));
        }else {
            callback.onCallback(BridgeResult.ILLEGAL_ARGUMENTS());
        }
    }

    @JavascriptInterface
    public void apiReturnOtherAsync(String message,APICallback<String> callback) throws Exception {
        if (message != null) {
            callback.onCallback(message,true);
            return;
        }
        //如果API内部有错误，抛出异常，bridge框架层会捕捉到传给调用者
        throw BridgeException.ILLEGAL_ARGUMENTS();
    }

}
