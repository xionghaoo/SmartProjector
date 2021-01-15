/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.bridge;

import android.util.Log;

import androidx.test.runner.AndroidJUnit4;

import com.ubtedu.bridge.platform.android.AndroidAPI;
import com.ubtedu.bridge.platform.android.AndroidHandler;
import com.ubtedu.bridge.platform.android.AndroidNamespaceAPI;
import com.ubtedu.bridge.platform.other.OtherCallback;
import com.ubtedu.bridge.platform.other.OtherPlatform;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @Author qinicy
 * @Date 2019/3/29
 **/
@RunWith(AndroidJUnit4.class)
public class BridgeTest {
    public final static String TAG = "TestRunner#BridgeTest";
    private BridgeImpl mAndroidBridge;
    private OtherPlatform mOtherPlatform;



    @Before
    public void init() {
        if (mAndroidBridge == null) {
            //初始化android bridge
            AndroidHandler androidHandler = new AndroidHandler();
            mAndroidBridge = new BridgeImpl(androidHandler);
            //注册API，提供给其它平台调用
            AndroidAPI androidAPI = new AndroidAPI();
            AndroidNamespaceAPI androidNamespaceAPI = new AndroidNamespaceAPI();
            mAndroidBridge.addBridgeAPI(androidAPI, androidAPI.getNamespace());
            mAndroidBridge.addBridgeAPI(androidNamespaceAPI, androidNamespaceAPI.getNamespace());
            mAndroidBridge.setBridgeCommunicable(true);
        }

        if (mOtherPlatform == null) {
            mOtherPlatform = new OtherPlatform();
        }
    }

    @Test
    public void testOtherCallAndroidApiReturnBridgeResult() {
        final String message = "Hello android#apiReturnBridgeResult";
        Object[] args = new Object[]{message};
        String resultJson = mOtherPlatform.call("apiReturnBridgeResult", args, null);
        Assert.assertNotNull(resultJson);

        String log = "testOtherCallAndroidApiReturnBridgeResult --> " + resultJson;
        Log.d(TAG,log);
        Assert.assertTrue(TestUtils.checkResultFormat(resultJson));
    }

    @Test
    public void testOtherCallAndroidApiReturnOtherAsync() {
        final String message = "Hello android#apiReturnOtherAsync";
        Object[] args = new Object[]{message};
        mOtherPlatform.call("apiReturnOtherAsync", args, new OtherCallback() {
            @Override
            public void onCallback(String resultJson) {
                Assert.assertNotNull(resultJson);
                String log = "testOtherCallAndroidApiReturnOtherAsync --> " + resultJson;
                Log.d(TAG,log);
                Assert.assertTrue(TestUtils.checkResultFormat(resultJson));
            }
        });
    }

    @Test
    public void testAndroidCallOtherApi(){
        mAndroidBridge.call("xxx", null, new OnCallback() {
            @Override
            public void onCallback(BridgeResult result) {

            }
        });
    }


}
