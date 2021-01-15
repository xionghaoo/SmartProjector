/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.bluetooth.peripheral.error;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoComponentErrorListener;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoConnectStatusChangeListener;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoMissionCallback;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocationSequence;
import com.ubtedu.deviceconnect.libs.base.invocation.URoMissionAbortSignal;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentID;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.BtInvocationFactory;
import com.ubtedu.ukit.bluetooth.IUKitCommandResponse;
import com.ubtedu.ukit.common.base.UKitBaseActivity;

import java.util.ArrayList;
import java.util.HashSet;

import static com.ubtedu.deviceconnect.libs.base.model.URoComponentType.BRIGHTNESSSENSOR;
import static com.ubtedu.deviceconnect.libs.base.model.URoComponentType.ENVIRONMENTSENSOR;
import static com.ubtedu.deviceconnect.libs.base.model.URoComponentType.SOUNDSENSOR;

/**
 * @Author naOKi
 * @Date 2018-12-12
 **/
public class PeripheralErrorActivity extends UKitBaseActivity implements URoComponentErrorListener, URoConnectStatusChangeListener {

    private final static String EXTRA_ERROR_ITEMS = "_extra_error_items";

    private Button mCancelBtn;
    private Button mFixBtn;
    private Button mConfirmBtn;
    private Button mCloseBtn;
    private RecyclerView mErrorRv;

    private PeripheralErrorItemAdapter mAdapter;
    private ArrayList<PeripheralErrorItem> errors = null;

    private URoMissionAbortSignal mFixAbort;
    public static void openPeripheralErrorActivity(Context context, ArrayList<PeripheralErrorItem> errors) {
        if (context == null || errors == null || errors.isEmpty()||!BluetoothHelper.isConnected()) {
            return;
        }
        Intent intent = new Intent(context, PeripheralErrorActivity.class);
        intent.putExtra(EXTRA_ERROR_ITEMS, errors);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_peripheral_error_repair);
        initView();
        if (savedInstanceState != null) {
            initArguments(savedInstanceState);
        } else {
            initArguments(getIntent().getExtras());
        }

//        BluetoothHelper.addComponentErrorListener(this);
        BluetoothHelper.addConnectStatusChangeListener(this);
    }

    private void initArguments(Bundle args) {
        if (args != null) {
            errors = (ArrayList<PeripheralErrorItem>) args.getSerializable(EXTRA_ERROR_ITEMS);
        }
        mAdapter.setPeripheralError(errors);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_ERROR_ITEMS, errors);
    }

    private void initView() {
        mCancelBtn = findViewById(R.id.peripheral_error_list_cancel_btn);
        bindSafeClickListener(mCancelBtn);
        mFixBtn = findViewById(R.id.peripheral_error_list_fix_btn);
        bindSafeClickListener(mFixBtn);
        mConfirmBtn = findViewById(R.id.peripheral_error_list_confirm_btn);
        bindSafeClickListener(mConfirmBtn);
        mCloseBtn = findViewById(R.id.peripheral_error_list_close_btn);
        bindSafeClickListener(mCloseBtn);
        mErrorRv = findViewById(R.id.peripheral_error_list_rcv);
        mAdapter = new PeripheralErrorItemAdapter();
        mErrorRv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mErrorRv.setAdapter(mAdapter);
        mErrorRv.setLayoutManager(new LinearLayoutManager(this));
        mErrorRv.setHasFixedSize(true);
    }

    private void updateCloseButton(boolean isBluetoothConnect) {
        mCloseBtn.setVisibility(isBluetoothConnect ? View.GONE : View.VISIBLE);
        mFixBtn.setVisibility(isBluetoothConnect ? View.VISIBLE : View.GONE);
        mCancelBtn.setVisibility(isBluetoothConnect ? View.VISIBLE : View.GONE);
        mConfirmBtn.setVisibility(isBluetoothConnect ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        if (v == mConfirmBtn) {
            finish();
        }
        if (v == mCancelBtn) {
            finish();
        }
        if (v == mCloseBtn) {
            finish();
        }
        if (v == mFixBtn) {
            if (!BluetoothHelper.isConnected()) {
                updateCloseButton(false);
                return;
            }
            mFixFailSensor.clear();
            URoInvocationSequence bas = new URoInvocationSequence();
            FixExecutionCallback lastExecutionCallback = null;
//            bas.command(FixSteeringGear.newInstance());
//            bas.command(FixMotor.newInstance());
            bas.action(BtInvocationFactory.faultClear(),true);
            for (int i = 0; i < errors.size(); i++) {
                if (PeripheralErrorItemAdapter.Status.FIXED.equals(mAdapter.getErrorStatus(i))) {
                    continue;
                }
                PeripheralErrorItem error = errors.get(i);
                if (lastExecutionCallback != null) {
                    lastExecutionCallback.setNextIndex(i);
                }
                lastExecutionCallback = new FixExecutionCallback<Boolean>(i);
                switch (error.getType()) {
                    case SERVOS:
                    case MOTOR:
                    case INFRAREDSENSOR:
                    case TOUCHSENSOR:
                    case LED:
                    case ULTRASOUNDSENSOR:
                    case COLORSENSOR:
//                    case SPEAKER://蓝牙音箱目前无法检测到异常
                    case SOUNDSENSOR://声音传感器不能用打开传感器指令来修复问题，只能用读取指令来检测
                    case ENVIRONMENTSENSOR://温湿度传感器不能用打开传感器指令来修复问题，只能用读取指令来检测
                    case BRIGHTNESSSENSOR:
                    case LED_BELT:
                        bas.action(BtInvocationFactory.checkComponent(new URoComponentID(error.getType(), error.getId()),lastExecutionCallback));
                        break;
                }
//                if (error.isSteeringGear()) {
//                    bas.command(new UKitCmdSteeringGearLoop.Builder().setMode(UKitCmdSteeringGearLoop.Mode.STOP).addId(error.getId()).setIgnoreConflict(true).build(), true, lastExecutionCallback);
//                } else if (URoComponentType.INFRAREDSENSOR.equals(error.getType())) {
//                    bas.command(new UKitCmdSensorSwitch.Builder().setEnable(true).setSensorType(URoComponentType.PERIPHERAL_SENSOR_INFRARED.getPeripheralType()).addId(error.getId()).build(), true, lastExecutionCallback);
//                } else if (URoComponentType.TOUCHSENSOR.equals(error.getType())) {
//                    bas.command(new UKitCmdSensorSwitch.Builder().setEnable(true).setSensorType(URoComponentType.PERIPHERAL_SENSOR_TOUCH.getPeripheralType()).addId(error.getId()).build(), true, lastExecutionCallback);
//                } else if (URoComponentType.BRIGHTNESSSENSOR.equals(error.getType())) {
//                    bas.command(new UKitCmdSensorSwitch.Builder().setEnable(true).setSensorType(URoComponentType.PERIPHERAL_SENSOR_LIGHTING.getPeripheralType()).addId(error.getId()).build(), true, lastExecutionCallback);
//                } else if (URoComponentType.ULTRASOUNDSENSOR.equals(error.getType())) {
//                    bas.command(new UKitCmdSensorSwitch.Builder().setEnable(true).setSensorType(URoComponentType.PERIPHERAL_SENSOR_ULTRASOUND.getPeripheralType()).addId(error.getId()).build(), true, lastExecutionCallback);
//                } else if (URoComponentType.SPEAKER.equals(error.getType())) {
//                    //蓝牙音箱目前无法检测到异常
////                    bas.command(new UKitCmdSensorSwitch.Builder().setEnable(true).setSensorType(URoComponentType.PERIPHERAL_SENSOR_SPEAKER.getPeripheralType()).addId(error.getId()).build(), true, lastExecutionCallback);
//                } else if (URoComponentType.COLORSENSOR.equals(error.getType())) {
//                    bas.command(new UKitCmdSensorSwitch.Builder().setEnable(true).setSensorType(URoComponentType.PERIPHERAL_SENSOR_COLOR.getPeripheralType()).addId(error.getId()).build(), true, lastExecutionCallback);
//                } else if (URoComponentType.ENVIRONMENTSENSOR.equals(error.getType())) {
//                    //温湿度传感器不能用打开传感器指令来修复问题，只能用读取指令来检测
//                    bas.command(new UKitCmdSensorValue.Builder().addPeripheralId(URoComponentType.PERIPHERAL_SENSOR_HUMITURE, error.getId()).build(), true, lastExecutionCallback);
//                } else if (URoComponentType.BRIGHTNESSSENSOR.equals(error.getType())) {
//                    bas.command(new UKitCmdSensorValue.Builder().addPeripheralId(URoComponentType.PERIPHERAL_SENSOR_ENV_LIGHT, error.getId()).build(), true, lastExecutionCallback);
//                } else if (URoComponentType.SOUNDSENSOR.equals(error.getType())) {
//                    //声音传感器不能用打开传感器指令来修复问题，只能用读取指令来检测
//                    bas.command(new UKitCmdSensorValue.Builder().addPeripheralId(URoComponentType.PERIPHERAL_SENSOR_SOUND, error.getId()).build(), true, lastExecutionCallback);
//                } else if (URoComponentType.MOTOR.equals(error.getType())) {
////                    bas.command(new UKitCmdLowMotorStart.Builder().addParam(error.getId(), 0).setTime(0xFFFF).build(), true);
//                    bas.command(new UKitCmdLowMotorStop.Builder().addId(error.getId()).build(), true, lastExecutionCallback);
//                }
//            }
            }
            bas.sleep(1, true);
//            bas.listener(new IActionSequenceProcessListener() {
//                @Override
//                public void onProcessChanged(UKitDeviceActionSequence bas, int currentIndex, int totalAction) {
//                }
//
//                @Override
//                public void onProcessBegin(UKitDeviceActionSequence bas) {
//                    mCancelBtn.setEnabled(false);
//                    mFixBtn.setEnabled(false);
//                    mAdapter.updateStatus(mAdapter.getFirstError(), PeripheralErrorItemAdapter.Status.FIXING);
//                }
//
//                @Override
//                public void onProcessEnd(UKitDeviceActionSequence bas) {
//                    mCancelBtn.setEnabled(true);
//                    mFixBtn.setEnabled(true);
//                    mAdapter.stopFixingStatus();
//                    if (!mAdapter.hasErrorStatus()) {
//                        mConfirmBtn.setVisibility(View.VISIBLE);
//                        mCancelBtn.setVisibility(View.GONE);
//                    }
//                }
//            });
            bas.setCompletionCallback(new URoMissionCallback() {
                @Override
                public void onMissionNextStep(int currentStep, int totalStep) {

                }

                @Override
                public void onMissionBegin() {
                    mFixAbort=getAbortSignal();
                    mCancelBtn.setEnabled(false);
                    mFixBtn.setEnabled(false);
                    mAdapter.updateStatus(mAdapter.getFirstError(), PeripheralErrorItemAdapter.Status.FIXING);
                }

                @Override
                public void onMissionEnd() {
                    mFixAbort=null;
                }

                @Override
                public void onComplete(URoCompletionResult result) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCancelBtn.setEnabled(true);
                            mFixBtn.setEnabled(true);
                            mAdapter.stopFixingStatus();
                            if (!mAdapter.hasErrorStatus()) {
                                mConfirmBtn.setVisibility(View.VISIBLE);
                                mCancelBtn.setVisibility(View.GONE);
                            }
                        }
                    }, 210);//需要等待是否有使用读值修复的传感器修复失败，使用读值修复时，拔掉传感器导致的读值失败在外设异常回调中获取，FixExecutionCallback中result为success

                }
            });

            BluetoothHelper.addCommand(bas);
            mCancelBtn.setEnabled(false);
            mFixBtn.setEnabled(false);
            mAdapter.updateStatus(mAdapter.getFirstError(), PeripheralErrorItemAdapter.Status.FIXING);
        }
    }

    private HashSet<URoComponentID> mFixFailSensor = new HashSet<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onReportComponentError(URoProduct product, URoComponentID component, URoError error) {
        if (component.getComponentType() != null && (component.getComponentType().equals(SOUNDSENSOR) || component.getComponentType().equals(ENVIRONMENTSENSOR) || component.getComponentType().equals(BRIGHTNESSSENSOR))) {
            mFixFailSensor.add(component);
        }
    }

    @Override
    public void onConnectStatusChanged(URoProduct product, URoConnectStatus connectStatus) {
        if (connectStatus == URoConnectStatus.CONNECTED) {
            updateCloseButton(true);
        } else if (connectStatus == URoConnectStatus.DISCONNECTED) {
            if (mFixAbort!=null&&!mFixAbort.isAbort()){
                mFixAbort.abort();
            }
            mAdapter.stopFixingStatus();
            updateCloseButton(false);
        }
    }

    private class FixExecutionCallback<T> extends IUKitCommandResponse<T> {
        private int index;
        private int nextIndex = -1;

        private FixExecutionCallback(int index) {
            this.index = index;
        }

        public void setNextIndex(int nextIndex) {
            this.nextIndex = nextIndex;
        }

        @Override
        public void onUKitCommandResponse(URoCompletionResult<T> result) {
//            if (result.getData() instanceof URoSensorData && result.isSuccess()) {//当使用读值修复传感器时，此处success无效，因为即使外设异常也为true
//                HashMap<URoComponentID, Object> map = ((URoSensorData) result.getData()).getResult();
//                if (map.keySet() == null || map.keySet().size() == 0) {
//                    mAdapter.updateStatus(index, PeripheralErrorItemAdapter.Status.ERROR);
//                } else {
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            for (URoComponentID id :
//                                    ((URoSensorData) result.getData()).getResult().keySet()) {
//                                mAdapter.updateStatus(index, mFixFailSensor.contains(id) ? PeripheralErrorItemAdapter.Status.ERROR : PeripheralErrorItemAdapter.Status.FIXED);
//                            }
//                        }
//                    }, 200);//使用读值修复传感器，需等待是否有外设异常，如有外设异常则修复失败
//                }
//            } else {
                mAdapter.updateStatus(index, result.isSuccess() ? PeripheralErrorItemAdapter.Status.FIXED : PeripheralErrorItemAdapter.Status.ERROR);
//            }
            if (nextIndex != -1) {
                mAdapter.updateStatus(nextIndex, PeripheralErrorItemAdapter.Status.FIXING);
            }
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        BluetoothHelper.removeComponentErrorListener(this);
        BluetoothHelper.removeConnectStatusChangeListener(this);
        super.onDestroy();
    }
}
