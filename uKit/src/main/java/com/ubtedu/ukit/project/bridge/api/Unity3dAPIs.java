/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.bridge.api;

import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.ubtedu.alpha1x.utils.GsonUtil;
import com.ubtedu.bridge.BaseBridgeAPI;
import com.ubtedu.bridge.BridgeArray;
import com.ubtedu.bridge.BridgeObject;
import com.ubtedu.bridge.BridgeResult;
import com.ubtedu.bridge.OnCallback;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoConnectStatusChangeListener;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.processor.PeripheralErrorCollector;
import com.ubtedu.ukit.common.utils.UuidUtil;
import com.ubtedu.ukit.home.HomeActivity;
import com.ubtedu.ukit.project.UserDataSynchronizer;
import com.ubtedu.ukit.project.bridge.MotionDesigner;
import com.ubtedu.ukit.project.vo.OfficialProject;
import com.ubtedu.ukit.project.vo.Project;
import com.ubtedu.ukit.user.UserManager;

import java.io.File;


/**
 * @Author qinicy
 * @Date 2018/10/26
 **/
public class Unity3dAPIs extends BaseBridgeAPI implements URoConnectStatusChangeListener {
    public final static String NAME_SPACE = "unity";
    private Handler mHandler;
    private MotionDesigner mDesigner;

    public Unity3dAPIs() {
        mHandler = new Handler(Looper.getMainLooper());
        mDesigner = MotionDesigner.getInstance();
        BluetoothHelper.addConnectStatusChangeListener(this);
    }

    public void servoPowerOff(BridgeArray servoIdsJson, OnCallback callback) {
        //JSON格式检查交给MotionDesigner
        mDesigner.servoPowerOff(servoIdsJson, callback);
    }

    public void singleProgram(BridgeArray servoIds, OnCallback callback) {
        //JSON格式检查交给MotionDesigner
        mDesigner.singleProgram(servoIds, callback);
    }

    public void startAutoProgram(BridgeArray servoIds, Number internal, OnCallback callback) {
        //JSON格式检查交给MotionDesigner
        mDesigner.startAutoProgram(servoIds, internal, callback);
    }

    public BridgeResult stopAutoProgram() {
        return mDesigner.stopAutoProgram();
    }

    public void execMotion(BridgeObject motionFrame, OnCallback callback) {
        //JSON格式检查交给MotionDesigner
        mDesigner.execMotion(motionFrame, callback);
    }

    public BridgeResult createProject(String officialFilePath) {
        if (getActivity() instanceof HomeActivity) {
            if (officialFilePath != null) {
                File officialProjectDir = new File(officialFilePath);
                OfficialProject project = null;
                if (officialProjectDir.exists() && officialProjectDir.isDirectory()) {
                    Project p = UserDataSynchronizer.loadProjectFromFile(officialProjectDir);
                    if (p != null) {
                        String json = GsonUtil.get().toJson(p);
                        project = GsonUtil.get().toObject(json, OfficialProject.class);
                    }
                }
                if (project == null) {
                    project = new OfficialProject();
                }
                project.officialPath = officialFilePath;
                final Project finalProject = project;
                finalProject.isModified = true;
                finalProject.userId = UserManager.getInstance().getLoginUserId();
                finalProject.projectId = UuidUtil.createUUID();
                finalProject.createTime = System.currentTimeMillis();
                finalProject.localModifyTime = finalProject.createTime;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() instanceof HomeActivity) {
                            ((HomeActivity) getActivity()).openOfficialProject(finalProject);
                        } else {
                            mHandler.postDelayed(this, 100);
                        }
                    }
                });
            }
        }
        return BridgeResult.SUCCESS();
    }

    public BridgeResult onUnityLoadingComplete() {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (getActivity() instanceof HomeActivity) {
                    ((HomeActivity) getActivity()).onUnityLoadingComplete();
                } else {
                    mHandler.postDelayed(this, 300);
                }
            }
        });

        return BridgeResult.SUCCESS();
    }

    public BridgeResult startCollectError(String type) {
        switch (type) {
            case "preview":
                PeripheralErrorCollector.getInstance().updateCollectorState(PeripheralErrorCollector.CollectorState.START, PeripheralErrorCollector.ErrorCollectorType.MOTION_PREVIEW);
                break;
            case "recording":
                MotionDesigner.getInstance().setRecording(true);
                PeripheralErrorCollector.getInstance().updateCollectorState(PeripheralErrorCollector.CollectorState.START, PeripheralErrorCollector.ErrorCollectorType.MOTION_RECORDING);
                break;
            default:
                return BridgeResult.FAIL();
        }
        return BridgeResult.SUCCESS();
    }

    public BridgeResult stopCollectError() {
        PeripheralErrorCollector.getInstance().updateCollectorState(PeripheralErrorCollector.CollectorState.STOP, null);
        MotionDesigner.getInstance().setRecording(false);
        return BridgeResult.SUCCESS();
    }

    @Override
    public String getNamespace() {
        return NAME_SPACE;
    }

//    @Override
//    public void onConnectStateChanged(UKitRemoteDevice device, boolean oldState, boolean newState, boolean verified) {
//        if (!newState) {
//            mDesigner.stopAutoProgram();
//        }
//        //unity不需要
////        Object[] args = new Object[]{isConnected ? BridgeBoolean.TRUE() : BridgeBoolean.FALSE()};
////
////        if (BridgeCommunicator.getInstance().getUnity3DBridge().isCommunicable()) {
////            BridgeCommunicator.getInstance().getUnity3DBridge().call(Unity3DFunctions.notifyBluetoothStateChange, args, null);
////        }
//    }

    public void notifyEnterBuildScene() {
        if (BluetoothHelper.isConnected()) {
            BluetoothHelper.disconnect();
        }
    }

    private AppCompatActivity getActivity() {
        return ActivityHelper.getResumeActivity();
    }

    @Override
    public void onConnectStatusChanged(URoProduct product, URoConnectStatus connectStatus) {
        if (connectStatus == URoConnectStatus.DISCONNECTED) {
            mDesigner.stopAutoProgram();
        }
    }
}
