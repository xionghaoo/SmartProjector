/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.unity;

import android.os.Handler;
import android.os.Looper;

import com.ubtedu.bridge.BaseBridgeHandler;
import com.ubtedu.bridge.IBridgeInterface;
import com.unity3d.player.UnityPlayer;

/**
 * @Author qinicy
 * @Date 2018/10/12
 **/
public class UnityBridgeHandler extends BaseBridgeHandler {
    private final static String UNITY_ADAPTER = "SDKAdapter";
    private final static String CALL = "call";
    private final static String ONCALLBACK = "onCallback";
    private Handler mHandler;

    public UnityBridgeHandler() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void registerFrameworkInterFace(IBridgeInterface bridgeInterface) {
        super.registerFrameworkInterFace(bridgeInterface);
        UnityBridgeInterface.getInstance().init(bridgeInterface);
    }

    @Override
    public void invokeMethod(boolean isCall, String args) {
        sendMessageToUnity(isCall ? CALL : ONCALLBACK, args);
    }

    private void sendMessageToUnity(final String method, final String args) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                UnityPlayer.UnitySendMessage(UNITY_ADAPTER, method, args);
            }
        });
    }
}
