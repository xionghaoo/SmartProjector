/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.bridge.platform.other;


import com.ubtedu.bridge.BridgeFieldKeys;
import com.ubtedu.bridge.BridgeImpl;
import com.ubtedu.bridge.BridgeResult;
import com.ubtedu.bridge.OnCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * @Author qinicy
 * @Date 2019/3/29
 **/
public class OtherPlatform {
    private final BridgeImpl mOtherBridge;
    private int mCallId;

    public OtherPlatform() {
        //初始化其它平台的bridge，模拟别的平台
        OtherHandler otherHandler = new OtherHandler();
        mOtherBridge = new BridgeImpl(otherHandler);
        //注册API，提供给android平台调用
        OtherAPI otherAPI = new OtherAPI();
        OtherNamespaceAPI otherNamespaceAPI = new OtherNamespaceAPI();
        mOtherBridge.addBridgeAPI(otherAPI, otherAPI.getNamespace());
        mOtherBridge.addBridgeAPI(otherNamespaceAPI, otherNamespaceAPI.getNamespace());
        mOtherBridge.setBridgeCommunicable(true);
    }

    public String call(String func, Object[] args, final OtherCallback callback) {

        JSONObject callArgs = new JSONObject();
        try {
            callArgs.put(BridgeFieldKeys.KEY_ID, ++mCallId);
            callArgs.put(BridgeFieldKeys.KEY_FUNC, func);
            if (callback != null) {
                callArgs.put(BridgeFieldKeys.KEY_CALLBACK, "callback-" + mCallId);
            }
            callArgs.put(BridgeFieldKeys.KEY_ARGS, new JSONArray(Arrays.asList(args)));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String callArgsJson = callArgs.toString();
        if (callback == null) {
            return OtherFrameworkInterFaceRegister.getInstance().getBridgeInterface().call(callArgsJson);
        } else {
            mOtherBridge.call(func, args, new OnCallback() {
                @Override
                public void onCallback(BridgeResult result) {
                    callback.onCallback(result.toJson());
                }
            });
            return null;
        }
    }
}
