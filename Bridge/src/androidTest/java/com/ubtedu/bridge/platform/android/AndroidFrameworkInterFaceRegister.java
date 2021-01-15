/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.bridge.platform.android;

import com.ubtedu.bridge.IBridgeInterface;

/**
 * @Author qinicy
 * @Date 2019/3/29
 **/
public class AndroidFrameworkInterFaceRegister {
    private IBridgeInterface mBridgeInterface;
    private AndroidFrameworkInterFaceRegister() {
    }

    private static class SingletonHolder {
        private final static AndroidFrameworkInterFaceRegister instance = new AndroidFrameworkInterFaceRegister();
    }

    public static AndroidFrameworkInterFaceRegister getInstance() {
        return AndroidFrameworkInterFaceRegister.SingletonHolder.instance;
    }
    public void register(IBridgeInterface i){
        mBridgeInterface = i;
    }

    public IBridgeInterface getBridgeInterface() {
        return mBridgeInterface;
    }
}
