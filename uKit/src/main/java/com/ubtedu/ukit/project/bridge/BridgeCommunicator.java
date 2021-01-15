/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.bridge;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.bridge.JsBridgeHandler;
import com.ubtedu.ukit.project.bridge.api.BlocklyAPIs;
import com.ubtedu.ukit.project.bridge.api.GlobalAPIs;
import com.ubtedu.ukit.project.bridge.api.Unity3dAPIs;
import com.ubtedu.ukit.unity.UnityBridgeHandler;

/**
 * @Author qinicy
 * @Date 2018/12/3
 **/
public class BridgeCommunicator {

    private GlobalAPIs mGlobalAPIs;
    //blockly
    private RxBridge mBlocklyBridge;
    private JsBridgeHandler mBlocklyBridgeHandler;
    private BlocklyAPIs mBlocklyAPIs;
    //ControllerBlockly
    private RxBridge mControllerBlocklyBridge;
    private JsBridgeHandler mControllerBlocklyBridgeHandler;
    private BlocklyAPIs mControllerBlocklyAPIs;
    //Unity3D
    private RxBridge mUnity3DBridge;
    private UnityBridgeHandler mUnity3DBridgeHandler;
    private Unity3dAPIs mUnity3dAPIs;

    private BridgeCommunicator() {
    }

    private static class SingletonHolder {
        private final static BridgeCommunicator instance = new BridgeCommunicator();
    }

    public static BridgeCommunicator getInstance() {
        return BridgeCommunicator.SingletonHolder.instance;
    }

    public void init() {
        mGlobalAPIs = new GlobalAPIs();
        initUnity3DBridge();
        LogUtil.d("");
    }
    public void release() {
        LogUtil.d("");
        mGlobalAPIs = null;
        mBlocklyBridge = null;
        mBlocklyBridgeHandler = null;
        mBlocklyAPIs = null;
        mControllerBlocklyBridge = null;
        mControllerBlocklyBridgeHandler = null;
        mControllerBlocklyAPIs = null;
        mUnity3DBridge = null;
        mUnity3DBridgeHandler = null;
        mUnity3dAPIs = null;
    }
    public RxBridge getBlocklyBridge(boolean isControllerBlockly) {
        if (isControllerBlockly){
            return getControllerBlocklyBridge();
        }
        if (mBlocklyBridge == null){
            initBlocklyBridge();
        }
        return mBlocklyBridge;
    }
    public JsBridgeHandler getBlocklyBridgeHandler(boolean isControllerBlockly) {
        if (isControllerBlockly){
            return getControllerBlocklyBridgeHandler();
        }
        if (mBlocklyBridgeHandler == null){
            initBlocklyBridge();
        }
        return mBlocklyBridgeHandler;
    }


    public RxBridge getUnity3DBridge() {
        return mUnity3DBridge;
    }

    private RxBridge getControllerBlocklyBridge() {
        if (mControllerBlocklyBridge == null){
            initControllerBlocklyBridge();
        }
        return mControllerBlocklyBridge;
    }
    private JsBridgeHandler getControllerBlocklyBridgeHandler() {
        if (mControllerBlocklyBridgeHandler == null){
            initControllerBlocklyBridge();
        }
        return mControllerBlocklyBridgeHandler;
    }
    private void initBlocklyBridge(){
        mBlocklyBridgeHandler = new JsBridgeHandler();
        mBlocklyBridge = new RxBridge(mBlocklyBridgeHandler);
        mBlocklyAPIs = new BlocklyAPIs();
        mBlocklyBridge.addBridgeAPI(mGlobalAPIs, mGlobalAPIs.getNamespace());
        mBlocklyBridge.addBridgeAPI(mBlocklyAPIs, mBlocklyAPIs.getNamespace());
    }
    private void initControllerBlocklyBridge(){
        mControllerBlocklyBridgeHandler = new JsBridgeHandler();
        mControllerBlocklyBridge = new RxBridge(mControllerBlocklyBridgeHandler);
        mControllerBlocklyAPIs = new BlocklyAPIs();
        mControllerBlocklyBridge.addBridgeAPI(mGlobalAPIs, mGlobalAPIs.getNamespace());
        mControllerBlocklyBridge.addBridgeAPI(mControllerBlocklyAPIs, mControllerBlocklyAPIs.getNamespace());
    }
    private void initUnity3DBridge(){
        mUnity3DBridgeHandler = new UnityBridgeHandler();
        mUnity3DBridge = new RxBridge(mUnity3DBridgeHandler);
        mUnity3DBridge.addBridgeAPI(mGlobalAPIs, mGlobalAPIs.getNamespace());
        mUnity3dAPIs = new Unity3dAPIs();
        mUnity3DBridge.addBridgeAPI(mUnity3dAPIs, mUnity3dAPIs.getNamespace());
    }
}
