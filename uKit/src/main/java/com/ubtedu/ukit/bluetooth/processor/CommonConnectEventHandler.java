package com.ubtedu.ukit.bluetooth.processor;

import com.ubtedu.alpha1x.core.base.delegate.EventDelegate;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoConnectStatusChangeListener;
import com.ubtedu.deviceconnect.libs.base.model.URoSerialNumberInfo;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.BasicEventDelegate;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.connect.BluetoothReconnectTask;
import com.ubtedu.ukit.bluetooth.utils.BtLogUtils;
import com.ubtedu.ukit.common.analysis.UBTReporter;
import com.ubtedu.ukit.project.bridge.MotionDesigner;
import com.ubtedu.ukit.project.bridge.api.ToastHelper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author naOKi
 * @Date 2018/11/08
 **/
public class CommonConnectEventHandler implements URoConnectStatusChangeListener {

    private CommonConnectEventHandler() {
    }

    private static CommonConnectEventHandler instance = null;

    private static TimerTask timerTask = null;
    private static Timer timer = null;

    public static CommonConnectEventHandler getInstance() {
        synchronized (CommonConnectEventHandler.class) {
            if (instance == null) {
                instance = new CommonConnectEventHandler();
            }
            return instance;
        }
    }

    private void startTimer() {
        synchronized (CommonConnectEventHandler.class) {
            if (timerTask != null) {
                return;
            }
            timerTask = new TimerTask() {
                final UKitApplication uKitApplication = UKitApplication.getInstance();

                @Override
                public void run() {
                    if (isAppVisible(uKitApplication)) {
                        showDisconnectMsg(uKitApplication);
                        timer.purge();
                        timer.cancel();
                        timerTask = null;
                        timer = null;
                    }
                }
            };
            timer = new Timer();
            timer.schedule(timerTask, 0, 1000L);
        }
    }

    private void showDisconnectMsg(final UKitApplication app) {
        BluetoothHelper.getBtHandler().post(new Runnable() {
            @Override
            public void run() {
                if (!mToastOpenFlag){
                    return;
                }
                if (BluetoothReconnectTask.getInstance().isRebooting()){
                    return;
                }
                String msg = app.getString(R.string.bluetooth_disconnect_msg);
                ToastHelper.toastShort(msg);
            }
        });
    }

    private boolean isAppVisible(UKitApplication app) {
        EventDelegate eventDelegate = app.getEventDelegate();
        if (!(eventDelegate instanceof BasicEventDelegate)) {
            return false;
        }
        BasicEventDelegate basicEventDelegate = (BasicEventDelegate) eventDelegate;
        return basicEventDelegate.isAppVisible();
    }

//    @Override
//    public void onConnectStateChanged(UKitRemoteDevice device, boolean oldState, boolean newState, boolean verified) {
//        if (newState && verified) {
//            BtLogUtils.w("蓝牙连接成功: [%s]", device != null ? device.getDeviceName() : "Unknown");
//            URoSerialNumberInfo data = device.getSerialNumberData();
//            if (data.isValid()) {
//                UBTReporter.reportSerialNumber(data.getSerialNumber());
//            }
//        }
//        if (!newState && verified) {
//            BtLogUtils.w("蓝牙断开连接");
//            BluetoothHelper.terminateExecution();
//            UKitApplication uKitApplication = UKitApplication.getInstance();
//            if (isAppVisible(uKitApplication)) {
//                showDisconnectMsg(uKitApplication);
//            } else {
//                startTimer();
//            }
//            MotionDesigner.getInstance().updateChargingFlag(false);
//        }
//    }

    @Override
    public void onConnectStatusChanged(URoProduct product, URoConnectStatus connectStatus) {
        if (connectStatus==URoConnectStatus.CONNECTED) {
            BtLogUtils.w("蓝牙连接成功: [%s]", product != null ? product.getName() : "Unknown");
            URoSerialNumberInfo data = BluetoothHelper.getSerialNumberData();
            if (data != null && data.isValid()) {
                UBTReporter.reportSerialNumber(data.getSerialNumber());
            }
        }
        if (connectStatus==URoConnectStatus.DISCONNECTED) {
            BtLogUtils.w("蓝牙断开连接");
            BluetoothHelper.terminateExecution();
            UKitApplication uKitApplication = UKitApplication.getInstance();
            if (isAppVisible(uKitApplication)) {
                showDisconnectMsg(uKitApplication);
            } else {
                startTimer();
            }
            MotionDesigner.getInstance().updateChargingFlag(false);
        }
    }
    private boolean mToastOpenFlag=true;
    public void setToastOpen(boolean openFlag){
        mToastOpenFlag=openFlag;
    }
}
