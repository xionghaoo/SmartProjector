/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.bridge;

import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;


/**
 * @Author qinicy
 * @Date 2018/10/9
 **/
public abstract class BaseBridgeHandler implements IBridgeHandler {
    private static final String TAG = "BaseBridgeHandler";
    private Map<Integer, OnCallback> mCallbackMap;
    private Map<Integer, String> mFuncMap;
    private int mCallID;

    public BaseBridgeHandler() {
        mCallbackMap = new HashMap<>();
        mFuncMap = new HashMap<>();
    }

    /**
     * 检查call方法的参数是否有效
     * @param args call方法的参数
     * @return 是否有效
     */
    private boolean checkCallArguments(CallArguments args) {
        if (args != null) {
            if (!TextUtils.isEmpty(args.func)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查结果是否有效
     * @param result 结果
     * @return 是否有效
     */
    protected boolean checkResult(BridgeResult result) {
        if (result != null) {
            if (result.id >= 0) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void call(String funName, Object[] args, OnCallback callback) {
        CallArguments arguments = new CallArguments();
        arguments.func = funName;
        arguments.callback = callback != null?callback.toString():null;
        arguments.onCallback = callback;
        if (args != null && args.length > 0) {
            arguments.args = args;
        }
        call(arguments, callback);
    }

    private synchronized void call(CallArguments args, OnCallback callback) {

        if (!checkCallArguments(args)) {
            if (callback != null) {
                callback.onCallback(
                        new BridgeResult.Builder()
                                .code(BridgeResultCode.ILLEGAL_ARGUMENTS)
                                .isComplete(true)
                                .build());
            }
            return;
        }
        mCallID++;
        args.id = mCallID;
        String json = args.toJson();
        Log.d(TAG, "call(android ==> " + getClass().getSimpleName() + "):" + json);

        //把方法信息记录起来，以便输出日志方便查看
        String funcInfo = args.func;

        funcInfo += args.args != null ? args.funcArgsToJson() : "[]";

        mFuncMap.put(mCallID, funcInfo);

        //把callback保存起来
        if (callback != null) {
            mCallbackMap.put(mCallID, callback);
        }
        invokeMethod(true, json);
    }


    /**
     * android ==> other，发送结果给外部模块
     */
    @Override
    public void sendCallbackResult(BridgeResult result) {
        if (checkResult(result)) {
            invokeMethod(false, result.toJson());
        }
    }

    /**
     * other ==> android，接收外部模块发送过来的结果
     */
    @Override
    public void onCallback(BridgeResult result) {
        if (checkResult(result)) {
            OnCallback callback = mCallbackMap.get(result.id);
            String funcInfo = mFuncMap.get(result.id);
            Log.d(TAG, "onCallback(" + getClass().getSimpleName() + " ==> android):" + funcInfo + " --> " + result);
            if (callback != null) {
                callback.onCallback(result);
                if (BridgeBoolean.isTrue(result.complete)) {
                    mCallbackMap.remove(result.id);
                    mFuncMap.remove(result.id);
                }
            }
        }
    }

    /**
     * 执行bridge框架定义的{@link IBridgeInterface#call(String)}或{@link IBridgeInterface#onCallback(String)}
     * 方法，由实现类实现该方法
     * @param isCall true:{@link IBridgeInterface#call(String)},false:{@link IBridgeInterface#onCallback(String)}
     * @param args bridge框架定义的call方法参数如：
    {
    "id": 11102,
    "func": "method name",
    "args": [],
    "callback": "callbackName"
    }
     */
    public abstract void invokeMethod(boolean isCall, String args);


    @Override
    public void registerFrameworkInterFace(IBridgeInterface bridgeInterface) {

    }
}
