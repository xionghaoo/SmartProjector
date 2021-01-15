/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.unity;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.bridge.IBridgeInterface;

/**
 * @Author qinicy
 * @Date 2018/10/26
 **/
public class UnityBridgeInterface implements IBridgeInterface {
    private IBridgeInterface mBase;

    private static class SingletonHolder {
        private final static UnityBridgeInterface instance = new UnityBridgeInterface();
    }

    public static UnityBridgeInterface getInstance() {
        return UnityBridgeInterface.SingletonHolder.instance;
    }

    public void init(IBridgeInterface base) {
        LogUtil.i("UnityBridgeInterface init");
        this.mBase = base;
    }

    @Override
    public String call(String args) {
        if (mBase != null) {
            return mBase.call(args);
        }
        return null;
    }

    @Override
    public void onCallback(String result) {
        if (mBase != null) {
            mBase.onCallback(result);
        }
    }
}
