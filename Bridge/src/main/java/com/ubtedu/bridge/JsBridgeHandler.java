/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.bridge;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebSettings;
import android.webkit.WebView;


/**
 * 处理JS端的{@link IBridgeHandler}实现类
 * @Author qinicy
 * @Date 2018/10/9
 **/
public class JsBridgeHandler extends BaseBridgeHandler {
    private static final String TAG = "JsBridgeHandler";
    private static final String BRIDGE_NAME = "bridgeAndroid";
    private WebView mWebView;
    private Handler mHandler;
    private IBridgeInterface mBridgeInterface;


    public JsBridgeHandler() {
        super();
        mHandler = new Handler(Looper.getMainLooper());
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setWebView(WebView webView) {
        if (webView != null) {
            mWebView = webView;
            WebSettings settings = mWebView.getSettings();
            settings.setJavaScriptEnabled(true);
            registerJavascriptInterface();
        }
    }


    public void release() {

        mWebView = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }


    @Override
    public void invokeMethod(boolean isCall, String args) {
        if (isCall) {
            evaluateJavascript(String.format("window.bridgeJS.call(%s)", args));
        } else {
            evaluateJavascript(String.format("window.bridgeJS.onCallback(%s)", args));
        }
    }


    @SuppressLint("JavascriptInterface")
    @Override
    public void registerFrameworkInterFace(IBridgeInterface bridgeInterface) {
        mBridgeInterface = bridgeInterface;
        registerJavascriptInterface();
    }

    private void registerJavascriptInterface() {
        if (mWebView != null && mBridgeInterface != null) {
            mWebView.addJavascriptInterface(mBridgeInterface, BRIDGE_NAME);
        }
    }

    private void evaluateJavascript(final String script) {
        if (mWebView != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        mWebView.evaluateJavascript(script, null);
                    } else {
                        mWebView.loadUrl("javascript:" + script);
                    }
                }
            });
        }
    }
}
