/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.bridge.platform.other;

import com.ubtedu.bridge.IBridgeInterface;

/**
 * @Author qinicy
 * @Date 2019/3/29
 **/
public class OtherFrameworkInterFaceRegister {
    private IBridgeInterface mBridgeInterface;
    private OtherFrameworkInterFaceRegister() {
    }

    private static class SingletonHolder {
        private final static OtherFrameworkInterFaceRegister instance = new OtherFrameworkInterFaceRegister();
    }

    public static OtherFrameworkInterFaceRegister getInstance() {
        return OtherFrameworkInterFaceRegister.SingletonHolder.instance;
    }
    public void register(IBridgeInterface i){
        mBridgeInterface = i;
    }

    public IBridgeInterface getBridgeInterface() {
        return mBridgeInterface;
    }
}
