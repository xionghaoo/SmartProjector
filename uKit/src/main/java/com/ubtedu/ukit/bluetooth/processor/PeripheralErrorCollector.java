package com.ubtedu.ukit.bluetooth.processor;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.ubtedu.alpha1x.utils.GsonUtil;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.bridge.BridgeObject;
import com.ubtedu.bridge.BridgeResult;
import com.ubtedu.bridge.BridgeResultCode;
import com.ubtedu.bridge.OnCallback;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoComponentErrorListener;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoConnectStatusChangeListener;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentID;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.BtInvocationFactory;
import com.ubtedu.ukit.bluetooth.error.BluetoothCommonErrorHelper;
import com.ubtedu.ukit.bluetooth.peripheral.error.PeripheralErrorActivity;
import com.ubtedu.ukit.bluetooth.peripheral.error.PeripheralErrorItem;
import com.ubtedu.ukit.common.analysis.Events;
import com.ubtedu.ukit.common.analysis.UBTReporter;
import com.ubtedu.ukit.project.bridge.BridgeCommunicator;
import com.ubtedu.ukit.project.bridge.functions.BlocklyFunctions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.ubtedu.ukit.bluetooth.error.BluetoothCommonErrorHelper.CommonError.PLAY_RECORDING_ERROR;

/**
 * @Author naOKi
 * @Date 2018/12/14
 **/
public class PeripheralErrorCollector implements URoConnectStatusChangeListener, URoComponentErrorListener {
    private HashSet<URoComponentID> mErrorPeripheral = new HashSet<>();
    /**
     * 影响错误收集的条件有2个：{@link #state} 与 {@link #blocked}
     * state==stop或blocked==true时都会停止收集，为了等待回调onReportComponentError收集错误信息，在设置前面两种情况时需要延时
     */
    private long DELAY_FOR_COLLECT_ERROR = 200L;
    private long mLastTerminateTime = -1L;

    private HashSet<String> mErrorRecordingSet=new HashSet<>();

    private PeripheralErrorCollector() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onReportComponentError(URoProduct product, URoComponentID component, URoError error) {
        //BtLogUtils.e("ComponentError: %s", PeripheralErrorCollector.getInstance().toString());
        //BtLogUtils.e("ComponentError: %s-%d", component.getComponentType().getDisplayName(), component.getId());
        if (CollectorState.STOP.equals(state) || blocked || shielded) {
            return;
        }
        boolean needStop = false;
        if (URoComponentType.SERVOS.equals(component.getComponentType()) || URoComponentType.MOTOR.equals(component.getComponentType())) {
            needStop = true;
        }
        //BtLogUtils.e("ComponentError: needStop->%b", needStop);
        if (needStop) {
            if (mLastTerminateTime == -1 || (System.currentTimeMillis() - mLastTerminateTime) > DELAY_FOR_COLLECT_ERROR) {
                mLastTerminateTime = System.currentTimeMillis();
                //BtLogUtils.e("ComponentError: terminateExecution");
                BluetoothHelper.terminateExecution(false);
            }
        }
        mErrorPeripheral.add(component);
    }

    public void onReportRecordingError(String name) {
        if (TextUtils.isEmpty(name)){
            return;
        }
        if (CollectorState.STOP.equals(state) || blocked || shielded) {
            return;
        }
        mErrorRecordingSet.add(name);
    }

    @Override
    public void onConnectStatusChanged(URoProduct product, URoConnectStatus connectStatus) {
        if (connectStatus == URoConnectStatus.DISCONNECTED) {
            resetCollectorState();
        }
    }

    public HashSet<URoComponentID> getErrorPeripheral() {
        return mErrorPeripheral;
    }

    public void setErrorPeripheral(HashSet<URoComponentID> errorPeripheral) {
        this.mErrorPeripheral = errorPeripheral;
    }

    public void clearErrorPeripheral() {
        mErrorPeripheral.clear();
    }

    public enum ErrorCollectorType {
        NONE, BLOCKLY, MOTION_PREVIEW, MOTION_RECORDING, CONTROLLER
    }

    private static PeripheralErrorCollector instance = null;

    private HashSet<UsedPeripheral> usedPeripherals = new HashSet<>();
    private CollectorState state = CollectorState.STOP;
    private boolean blocked = false;
    private boolean shielded=false;
    private ErrorCollectorType type = ErrorCollectorType.NONE;
    private Handler mHandler;

    public static PeripheralErrorCollector getInstance() {
        synchronized (PeripheralErrorCollector.class) {
            if (instance == null) {
                instance = new PeripheralErrorCollector();
            }
            return instance;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("PeripheralErrorCollector { Type:%s State:%s }", type.name(), state.name());
    }

    public static void register() {
        BluetoothHelper.addConnectStatusChangeListener(getInstance());
        BluetoothHelper.addComponentErrorListener(getInstance());
    }

//    @Override
//    public void onPeripheralErrorAppeared(UKitRemoteDevice device, UKitPeripheralErrorData peripheralErrorData) {
//        if (CollectorState.STOP.equals(state) || blocked) {
//            return;
//        }
//        boolean needStop = false;
//        if (URoComponentType.PERIPHERAL_STEERING_GEAR.equals(peripheralErrorData.getType()) || URoComponentType.PERIPHERAL_LOW_MOTOR.equals(peripheralErrorData.getType())) {
//            needStop = true;
//        }
//        if (needStop) {
//            BluetoothHelper.terminateExecution(false);
//        }
//    }

//    @Override
//    public void onConnectStateChanged(UKitRemoteDevice device, boolean oldState, boolean newState, boolean verified) {
//        if (!newState) {
//            resetCollectorState();
//        }
//    }

    public CollectorState getCollectorState() {
        return state;
    }

    private void resetCollectorState() {
        this.state = CollectorState.STOP;
        BluetoothHelper.addCommand(BtInvocationFactory.boardSelfCheck(false));
    }

    public void setBlocked(boolean blocked) {
        if (blocked) {
            mHandler.postDelayed(mBlockRunnable, DELAY_FOR_COLLECT_ERROR);
        } else {
            mHandler.removeCallbacks(mBlockRunnable);
            this.blocked = blocked;
        }
    }

    public void setShielded(boolean shielded) {
        this.shielded = shielded;
    }

    private Runnable mBlockRunnable = new Runnable() {
        @Override
        public void run() {
            PeripheralErrorCollector.this.blocked = true;
        }
    };

    public void updateCollectorState(CollectorState state, ErrorCollectorType type) {
        synchronized (this) {
            if (this.state.equals(state)) {
                return;
            }
            if (CollectorState.START.equals(state)) {
                mHandler.removeCallbacks(StopCollectRunnable.getInstance(ErrorCollectorType.NONE));
                this.state = state;
                this.type = type == null ? ErrorCollectorType.NONE : type;
                clearErrorPeripheral();
                usedPeripherals.clear();
                mErrorRecordingSet.clear();
                BluetoothHelper.addCommand(BtInvocationFactory.boardSelfCheck(true));
            } else {
                mHandler.postDelayed(StopCollectRunnable.getInstance(type), DELAY_FOR_COLLECT_ERROR);
            }
        }
    }

    public boolean isCurrentType(ErrorCollectorType type) {
        return this.type != null && this.type.equals(type);
    }

    public void showErrorIfNotEmpty() {
        BluetoothHelper.getBtHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!getErrorPeripheral().isEmpty()) {
                    BluetoothHelper.getBtHandler().post(ShowPeripheralErrorRunnable.getInstance(PeripheralErrorCollector.this.type));
                }
            }
        }, DELAY_FOR_COLLECT_ERROR);

    }

    public void appendUsedPeripheral(URoComponentType type, Integer id) {
        if (CollectorState.STOP.equals(state)) {
            return;
        }
        if (URoComponentType.SERVOS.equals(type)) {
            if (id < 1 || id > 32) {
                return;
            }
        } else {
            if (id < 1 || id > 8) {
                return;
            }
        }
        synchronized (PeripheralErrorCollector.class) {
            UsedPeripheral usedPeripheral = new UsedPeripheral(type, id);
            usedPeripherals.add(usedPeripheral);
        }
    }

    private static class StopCollectRunnable implements Runnable {
        private ErrorCollectorType targetType = ErrorCollectorType.NONE;
        private static StopCollectRunnable mInstance = null;

        private static StopCollectRunnable getInstance(ErrorCollectorType type) {
            synchronized (StopCollectRunnable.class) {
                if (mInstance == null) {
                    mInstance = new StopCollectRunnable();
                }
                mInstance.targetType=type;
                return mInstance;
            }
        }

        @Override
        public void run() {
            PeripheralErrorCollector.getInstance().state = CollectorState.STOP;
            BluetoothHelper.getBtHandler().post(ShowPeripheralErrorRunnable.getInstance(PeripheralErrorCollector.getInstance().type));
            PeripheralErrorCollector.getInstance().setBlocked(false);
            PeripheralErrorCollector.getInstance().type = targetType == null ? ErrorCollectorType.NONE : targetType;
            targetType = ErrorCollectorType.NONE;
            BluetoothHelper.addCommand(BtInvocationFactory.boardSelfCheck(false));
        }
    }

    private static class ShowPeripheralErrorRunnable implements Runnable {
        private static ShowPeripheralErrorRunnable mInstance = null;

        private ErrorCollectorType type;

        private static ShowPeripheralErrorRunnable getInstance(ErrorCollectorType type) {
            synchronized (ShowPeripheralErrorRunnable.class) {
                if (mInstance == null) {
                    mInstance = new ShowPeripheralErrorRunnable();
                }
                mInstance.setErrorCollectorType(type);
                return mInstance;
            }
        }

        public void setErrorCollectorType(ErrorCollectorType type) {
            this.type = type;
        }

        private boolean contains(HashSet<UsedPeripheral> usedPeripherals, PeripheralErrorItem error) {
            if (usedPeripherals == null || usedPeripherals.isEmpty() || error == null) {
                return false;
            }
            if (error.isSteeringGear()) {
                for (UsedPeripheral usedPeripheral : usedPeripherals) {
                    if (URoComponentType.SERVOS.equals(usedPeripheral.getType()) && usedPeripheral.getId().equals(error.getId())) {
                        return true;
                    }
                }
            } else {
                for (UsedPeripheral usedPeripheral : usedPeripherals) {
                    if (usedPeripheral.getType().equals(error.getType()) && usedPeripheral.getId().equals(error.getId())) {
                        return true;
                    }
                }
            }
            return false;
        }

        private ArrayList<PeripheralErrorItem> filter(HashSet<PeripheralErrorItem> errors, HashSet<UsedPeripheral> usedPeripherals) {
            ArrayList<PeripheralErrorItem> filtedErrors = new ArrayList<>();
            for (PeripheralErrorItem error : errors) {
                if (contains(usedPeripherals, error)) {
                    filtedErrors.add(error);
                }
            }
            return filtedErrors;
        }

        private void open() {
            HashSet<PeripheralErrorItem> errorItems = new HashSet<>();
            ArrayList<URoComponentID> errorList = BluetoothHelper.getPeripheralError();
            for (URoComponentID errorData : errorList) {
                if (URoComponentType.SERVOS.equals(errorData.getComponentType())) {
                    errorItems.add(PeripheralErrorItem.newSteeringGearError(errorData.getId()));
                } else {
                    errorItems.add(PeripheralErrorItem.newSensorError(errorData.getId(), errorData.getComponentType()));
                }
            }
            ArrayList<PeripheralErrorItem> errors = filter(errorItems, instance.usedPeripherals);
            if (!errors.isEmpty()) {
                instance.resetCollectorState();
                instance.usedPeripherals.clear();
                BluetoothHelper.resetPeripheralError();
            }

            PeripheralErrorActivity.openPeripheralErrorActivity(UKitApplication.getInstance(), errors);
            reportPeripheralErrorEvent(errors);

            HashSet<String> recordingErrors=new HashSet<>(instance.mErrorRecordingSet);
            instance.mErrorRecordingSet.clear();
            showPlayRecordingErrorDialog(recordingErrors);
        }

        private void showPlayRecordingErrorDialog(HashSet<String> recordingErrors) {
            if (recordingErrors == null || recordingErrors.isEmpty()) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (String name : recordingErrors) {
                sb.append(name);
                sb.append(", ");
            }
            String errorNames = sb.toString();
            errorNames = errorNames.substring(0, errorNames.length() - 2);//-2因为分隔符为逗号加空格
            BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(PLAY_RECORDING_ERROR, null, errorNames, null, null);
        }

        private void reportPeripheralErrorEvent(ArrayList<PeripheralErrorItem> errors) {
            if (errors != null && errors.size() > 0) {
                ArrayList<PeripheralErrorItem> temps = new ArrayList<>(errors);
                ArrayList<Map<String, String>> errorMapList = new ArrayList<>();
                for (PeripheralErrorItem error : temps) {
                    Map<String, String> errorMap = new HashMap<>();
                    if (error.isSteeringGear()) {
                        errorMap.put("type", URoComponentType.SERVOS.name());
                    } else {
                        if (error.getType() != null) {
                            errorMap.put("type", error.getType().name());
                        }
                    }

                    errorMap.put("id", String.valueOf(error.getId()));
                    errorMapList.add(errorMap);
                }

                Map<String, String> args = new HashMap<>(1);
                args.put("errors", GsonUtil.get().toJson(errorMapList));
                UBTReporter.onEvent(Events.Ids.app_peripheral_abnormal, args);
            }
        }

        private void parseBlocklyUsedSensors(URoComponentType type, JSONArray array) throws JSONException {
            if (type == null || array == null || array.length() == 0) {
                return;
            }
            int count = array.length();
            for (int i = 0; i < count; i++) {
                instance.usedPeripherals.add(new UsedPeripheral(type, array.getInt(i)));
            }
        }

        private void parseBlocklyUsedSensors(BridgeObject jsonObject) {
            try {
                parseBlocklyUsedSensors(URoComponentType.INFRAREDSENSOR, jsonObject.getJSONArray("infrared"));
                parseBlocklyUsedSensors(URoComponentType.TOUCHSENSOR, jsonObject.getJSONArray("touch"));
                parseBlocklyUsedSensors(URoComponentType.ULTRASOUNDSENSOR, jsonObject.getJSONArray("ultrasound"));
                parseBlocklyUsedSensors(URoComponentType.COLORSENSOR, jsonObject.getJSONArray("color"));
                parseBlocklyUsedSensors(URoComponentType.ENVIRONMENTSENSOR, jsonObject.getJSONArray("humiture"));
                parseBlocklyUsedSensors(URoComponentType.BRIGHTNESSSENSOR, jsonObject.getJSONArray("envLight"));
                parseBlocklyUsedSensors(URoComponentType.SOUNDSENSOR, jsonObject.getJSONArray("sound"));
                parseBlocklyUsedSensors(URoComponentType.LED_BELT, jsonObject.getJSONArray("lightBox"));
            } catch (Exception e) {
                LogUtil.e(e.getMessage());
            }
        }

        @Override
        public void run() {
            if (ErrorCollectorType.BLOCKLY.equals(type) && BridgeCommunicator.getInstance().getBlocklyBridge(false).isCommunicable()) {
                BridgeCommunicator.getInstance().getBlocklyBridge(false).call(BlocklyFunctions.getUsedSensors, null, new OnCallback() {
                    @Override
                    public void onCallback(BridgeResult result) {
                        if (result.code == BridgeResultCode.SUCCESS && result.data instanceof BridgeObject) {
                            parseBlocklyUsedSensors((BridgeObject) result.data);
                        }
                        open();
                    }
                });
            } else {
                open();
            }
        }
    }

    public enum CollectorState {
        START, STOP
    }

    private static class UsedPeripheral {
        private final URoComponentType type;
        private final Integer id;

        private UsedPeripheral(URoComponentType type, Integer id) {
            this.type = type;
            this.id = id;
        }

        public URoComponentType getType() {
            return type;
        }

        public Integer getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UsedPeripheral)) return false;
            UsedPeripheral that = (UsedPeripheral) o;
            if (getType() != that.getType()) return false;
            return getId().equals(that.getId());
        }

        @Override
        public int hashCode() {
            int result = getType().hashCode();
            result = 31 * result + getId().hashCode();
            return result;
        }
    }

}
