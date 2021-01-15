/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.bridge;

import android.util.Log;
import android.webkit.JavascriptInterface;

import java.util.ArrayList;
import java.util.List;


/**
 * Bridge框架核心类，通过BridgeImpl提供的接口，可以注册Java API、调用其它模块（JS或Unity3D）的API
 *
 * @Author qinicy
 * @Date 2018/10/8
 **/
public class BridgeImpl implements IBridge {
    public static final String TAG = "Bridge";
    private static final String FRAMEWORK_NAMESPACE = "framework";
    private static final String CHECK_BRIDGE_COMMUNICABLE = "framework.checkBridgeCommunicable";
    private IBridgeHandler mBridgeHandler;
    private BridgeInterface mFrameworkInterface;
    private BridgeDispatcher mDispatcher;
    private boolean isBridgeCommunicable = false;
    /**
     * 如果bridge处于未可通信状态调用call方法，会将这个调用缓存起来，直到bridge外部模块可通信状态时，
     * 再执行这些缓存在队列里的请求
     */
    private List<CallArguments> mStartupCalls;


    public BridgeImpl(final IBridgeHandler bridgeHandler) {
        mBridgeHandler = bridgeHandler;
        mFrameworkInterface = new BridgeInterface();
        if (bridgeHandler != null) {
            bridgeHandler.registerFrameworkInterFace(mFrameworkInterface);
            mDispatcher = new BridgeDispatcher(mBridgeHandler);
            FrameworkAPI frameworkAPI = new FrameworkAPI();
            addBridgeAPI(frameworkAPI, frameworkAPI.getNamespace());

        } else {
            throw new IllegalArgumentException("bridgeHandler cannot be null!");
        }

    }

    public void setBridgeCommunicable(boolean bridgeCommunicable) {
        isBridgeCommunicable = bridgeCommunicable;
    }

    @Override
    public void call(String funName, Object[] args, OnCallback callback) {
        if (isBridgeCommunicable) {
            if (mBridgeHandler != null) {
                mBridgeHandler.call(funName, args, callback);
            }
        } else {
            CallArguments arguments = new CallArguments();
            arguments.func = funName;
            arguments.args = args;
            arguments.callback = callback != null ? callback.toString() : null;
            arguments.onCallback = callback;
            if (mStartupCalls == null) {
                mStartupCalls = new ArrayList<>();
            }
            mStartupCalls.add(arguments);
        }

    }


    @Override
    public void addBridgeAPI(IBridgeAPI api, String namespace) {
        if (api != null) {
            mDispatcher.addBridgeAPI(api, namespace);
        }
    }

    @Override
    public boolean isCommunicable() {
        return isBridgeCommunicable;
    }


    private void dispatchStartupQueue() {
        if (mStartupCalls != null) {
            List<CallArguments> arguments = new ArrayList<>(mStartupCalls);
            for (CallArguments arg : arguments) {
                call(arg.func, (Object[]) arg.args, arg.onCallback);
            }
            mStartupCalls.clear();
            mStartupCalls = null;
        }
    }

    public void clearStartupCalls(){
        if (mStartupCalls!=null){
            mStartupCalls.clear();
        }
    }

    /**
     * FrameworkAPI只包含checkBridgeCommunicable方法，用来检测
     */
    private class FrameworkAPI extends BaseBridgeAPI {

        @JavascriptInterface
        public BridgeResult checkBridgeCommunicable() {
            Log.d(TAG, "other ==> android:" + mBridgeHandler.getClass().getSimpleName() + ":checkBridgeCommunicable");
            BridgeResult result = new BridgeResult.Builder()
                    .code(BridgeResultCode.SUCCESS)
                    .isComplete(true)
                    .msg("The bridge is communicable now")
                    .data(BridgeBoolean.TRUE())
                    .build();

            if (!isBridgeCommunicable) {
                isBridgeCommunicable = true;
                //外部模块既然主动查询Native是否init，说明外部模块的bridge已经init了
                dispatchStartupQueue();
            }
            return result;
        }

        @Override
        public String getNamespace() {
            return FRAMEWORK_NAMESPACE;
        }
    }

    private class BridgeInterface implements IBridgeInterface {
        @JavascriptInterface
        @Override
        public String call(String args) {
            Log.d(TAG, "call(" + mBridgeHandler.getClass().getSimpleName() + " ==> android):" + args);
            return mDispatcher.call(args);
        }

        @JavascriptInterface
        @Override
        public void onCallback(String result) {
            mDispatcher.onCallback(result);
        }
    }
}
