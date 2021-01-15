/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.bridge.platform.other;

import com.ubtedu.bridge.BaseBridgeHandler;
import com.ubtedu.bridge.IBridgeInterface;
import com.ubtedu.bridge.platform.android.AndroidFrameworkInterFaceRegister;

/**
 * @Author qinicy
 * @Date 2019/3/29
 **/
public class OtherHandler extends BaseBridgeHandler {
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
        AndroidFrameworkInterFaceRegister.getInstance().register(bridgeInterface);
    }

    private String call(String args) {
        return OtherFrameworkInterFaceRegister.getInstance().getBridgeInterface().call(args);
    }

    private void onCallback(String result) {
        OtherFrameworkInterFaceRegister.getInstance().getBridgeInterface().onCallback(result);
    }
}
