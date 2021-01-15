/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.bridge.platform.android;

import com.ubtedu.bridge.BaseBridgeHandler;
import com.ubtedu.bridge.IBridgeInterface;
import com.ubtedu.bridge.platform.other.OtherFrameworkInterFaceRegister;

/**
 * @Author qinicy
 * @Date 2019/3/29
 **/
public class AndroidHandler extends BaseBridgeHandler {
    @Override
    public void invokeMethod(boolean isCall, String args) {
        if (isCall) {
            call(args);
        } else {
            onCallback(args);
        }
    }

    @Override
    public void registerFrameworkInterFace(IBridgeInterface bridgeInterface) {
        super.registerFrameworkInterFace(bridgeInterface);
        OtherFrameworkInterFaceRegister.getInstance().register(bridgeInterface);
    }

    private String call(String args) {
        return AndroidFrameworkInterFaceRegister.getInstance().getBridgeInterface().call(args);
    }

    private void onCallback(String result) {
        AndroidFrameworkInterFaceRegister.getInstance().getBridgeInterface().onCallback(result);
    }
}
