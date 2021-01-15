/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.bridge;

import android.webkit.JavascriptInterface;

/**
 * 业务API结果回调
 * @Author qinicy
 * @Date 2018/10/9
 **/
public interface OnCallback {
    /**
     * 给调用者回调结果或者返回被调用者结果
     * @param result 异步API是否已经完成，如果是一些进度回调还没有完成，isComplete需要取值false，
     *                   否则bridge将会移除callback，调用者再也接收不到回调。
     */
    @JavascriptInterface
    void onCallback(BridgeResult result);
}
