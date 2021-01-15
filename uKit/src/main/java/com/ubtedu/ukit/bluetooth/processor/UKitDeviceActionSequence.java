///**
// * @2018 UBTECH Robotics Corp. All rights reserved (C)
// **/
//package com.ubtedu.ukit.bluetooth.processor;
//
//import android.bluetooth.BluetoothDevice;
//
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.DeviceManager;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.command.UKitCommandRequest;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.command.UKitCommandResponse;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.device.UKitRemoteDevice;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.executor.IUKitCommandResponse;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.executor.UKitCommandPriority;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.interfaces.IUKitCommandRecipient;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.interfaces.IUKitDeviceConnectStateChangeListener;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.interfaces.IUKitDeviceUpgradeMsgListener;
//import com.ubtedu.deviceconnect.libs.bluetooth.manager.base.model.UKitPeripheralType;
//import com.ubtedu.deviceconnect.libs.utils.LogUtils;
//import com.ubtedu.ukit.bluetooth.BluetoothHelper;
//import com.ubtedu.ukit.bluetooth.upgrade.BoardFrameworkUpgradeProcess;
//import com.ubtedu.ukit.bluetooth.upgrade.SensorFrameworkUpgradeProcess;
//import com.ubtedu.ukit.bluetooth.upgrade.SteeringGearFrameworkUpgradeProcess;
//
//import java.util.ArrayList;
//
///**
// * @Author naOKi
// * @Date 2018/11/27
// **/
//public class UKitDeviceActionSequence implements IUKitCommandRecipient {
//
//    private ArrayList<ActionParameter> actions = new ArrayList<>();
//    private ArrayList<ActionParameter> executeActions = new ArrayList<>();
//    private IActionSequenceCallback callback = null;
//    private IActionSequenceProcessListener listener = null;
//
//    private boolean loop = false;
//
//    private boolean hasExecute = false;
//    private boolean hasAbort = false;
//    private boolean hasFinish = false;
//    private boolean sentCallback = false;
//    private int total = -1;
//    private int index = 0;
//
//    private long lastActionTime = -1;
//
//    private IUKitCommandRecipient recipient;
//
//    private Runnable timeoutCallback = new Runnable() {
//        @Override
//        public void run() {
//            UKitDeviceActionSequence.this.abort();
//            UKitDeviceActionSequence.sendCallback(UKitDeviceActionSequence.this, false);
//        }
//    };
//
//    private Runnable ignoreFailureTimeoutCallback = new Runnable() {
//        @Override
//        public void run() {
//            UKitDeviceActionSequence.next(UKitDeviceActionSequence.this);
//        }
//    };
//
//    public abstract class ActionParameter {}
//
//    public class ConnectActionParameter extends ActionParameter {
//        private BluetoothDevice device;
//        private IUKitDeviceConnectStateChangeListener listener;
//        private long timeout;
//        private ConnectActionParameter(BluetoothDevice device, long timeout, IUKitDeviceConnectStateChangeListener listener) {
//            this.device = device;
//            this.listener = listener;
//            this.timeout = timeout;
//        }
//    }
//
//    public class CommandActionParameter extends ActionParameter {
//        private UKitCommandRequest command;
//        private IUKitCommandResponse callback;
//        private boolean ignoreFailure;
//        private long timeout;
//        private UKitCommandPriority priority;
//        private CommandActionParameter(UKitCommandRequest command, long timeout, boolean ignoreFailure, UKitCommandPriority priority, IUKitCommandResponse callback) {
//            this.command = command;
//            this.callback = callback;
//            this.ignoreFailure = ignoreFailure;
//            this.timeout = timeout;
//            this.priority = priority;
//        }
//        public UKitCommandRequest getCommand() {
//            return command;
//        }
//    }
//
//    public class DelayActionParameter extends ActionParameter {
//        private long delayTimeMs;
//        private long timeout;
//        private DelayActionParameter(long delayTimeMs, long timeout) {
//            this.delayTimeMs = delayTimeMs;
//            this.timeout = timeout;
//        }
//    }
//
//    public class SleepActionParameter extends ActionParameter {
//        private long sleepTimeMs;
//        private long timeout;
//        private SleepActionParameter(long sleepTimeMs, long timeout) {
//            this.sleepTimeMs = sleepTimeMs;
//            this.timeout = timeout;
//        }
//    }
//
//    public class UpgradeActionParameter extends ActionParameter {
//        private boolean isBoard;
//        private boolean isSteeringGear;
//        private UKitPeripheralType sensorType;
//        private String version;
//        private String file;
//        private IUKitDeviceUpgradeMsgListener callback;
//        private long timeout;
//        private UpgradeActionParameter(boolean isBoard, boolean isSteeringGear, UKitPeripheralType sensorType, String version, String file, long timeout, IUKitDeviceUpgradeMsgListener callback) {
//            this.isBoard = isBoard;
//            this.isSteeringGear = isSteeringGear;
//            this.sensorType = sensorType;
//            this.version = version;
//            this.file = file;
//            this.callback = callback;
//            this.timeout = timeout;
//        }
//    }
//
//    public void setRecipient(IUKitCommandRecipient recipient) {
//        this.recipient = recipient;
//    }
//
//    private UKitDeviceActionSequence() {
//        recipient = DeviceManager.getInstance().getDefaultDevice();
//    }
//
//    public static UKitDeviceActionSequence newActionSequence() {
//        return new UKitDeviceActionSequence();
//    }
//
//    public static UKitDeviceActionSequence newActionSequence(UKitDeviceActionSequence actionSequence) {
//        UKitDeviceActionSequence result = new UKitDeviceActionSequence();
//        result.add(actionSequence);
//        result.listener(actionSequence.listener);
//        result.callback(actionSequence.callback);
//        return result;
//    }
//
//    public UKitDeviceActionSequence connect(BluetoothDevice device) {
//        return connect(device, null);
//    }
//
//    public UKitDeviceActionSequence connect(BluetoothDevice device, IUKitDeviceConnectStateChangeListener listener) {
//        return connect(device, 30 * 1000, listener);
//    }
//
//    public UKitDeviceActionSequence connect(BluetoothDevice device, long timeout, IUKitDeviceConnectStateChangeListener listener) {
//        if(!hasExecute) {
//            actions.add(new ConnectActionParameter(device, timeout, listener));
//        }
//        return this;
//    }
//
//    public UKitDeviceActionSequence delay(long delayTimeMs) {
//        return delay(delayTimeMs, -1);
//    }
//
//    public UKitDeviceActionSequence delay(long delayTimeMs, long timeout) {
//        if(!hasExecute) {
//            actions.add(new DelayActionParameter(delayTimeMs, timeout));
//        }
//        return this;
//    }
//
//    public UKitDeviceActionSequence sleep(long sleepTimeMs) {
//        return sleep(sleepTimeMs, -1);
//    }
//
//    public UKitDeviceActionSequence sleep(long sleepTimeMs, long timeout) {
//        if(!hasExecute) {
//            actions.add(new SleepActionParameter(sleepTimeMs, timeout));
//        }
//        return this;
//    }
//
//    public UKitDeviceActionSequence command(UKitCommandRequest command) {
//        return command(command, false, null);
//    }
//
//    public UKitDeviceActionSequence command(UKitCommandRequest command, IUKitCommandResponse callback) {
//        return command(command, false, UKitCommandPriority.NORMAL, callback);
//    }
//
//    public UKitDeviceActionSequence command(UKitCommandRequest command, boolean ignoreFailure) {
//        return command(command, ignoreFailure, null);
//    }
//
//    public UKitDeviceActionSequence command(UKitCommandRequest command, boolean ignoreFailure, IUKitCommandResponse callback) {
//        return command(command, 10 * 1000, ignoreFailure, UKitCommandPriority.NORMAL, callback);
//    }
//
//    public UKitDeviceActionSequence command(UKitCommandRequest command, boolean ignoreFailure, UKitCommandPriority priority, IUKitCommandResponse callback) {
//        return command(command, 10 * 1000, ignoreFailure, priority, callback);
//    }
//
//    public UKitDeviceActionSequence command(UKitCommandRequest command, long timeout, boolean ignoreFailure, IUKitCommandResponse callback) {
//        return command(command, timeout, ignoreFailure, UKitCommandPriority.NORMAL, callback);
//    }
//
//    public UKitDeviceActionSequence command(UKitCommandRequest command, long timeout, boolean ignoreFailure, UKitCommandPriority priority, IUKitCommandResponse callback) {
//        if(!hasExecute) {
//            actions.add(new CommandActionParameter(command, timeout, ignoreFailure, priority, callback));
//        }
//        return this;
//    }
//
//    public UKitDeviceActionSequence upgrade(boolean isBoard, boolean isSteeringGear, UKitPeripheralType sensorType, String version, String file, IUKitDeviceUpgradeMsgListener callback) {
//        return upgrade(isBoard, isSteeringGear, sensorType, version, file, 240 * 1000, callback);
//    }
//
//    public UKitDeviceActionSequence upgrade(boolean isBoard, boolean isSteeringGear, UKitPeripheralType sensorType, String version, String file, long timeout, IUKitDeviceUpgradeMsgListener callback) {
//        if(!hasExecute) {
//            actions.add(new UpgradeActionParameter(isBoard, isSteeringGear, sensorType, version, file, timeout, callback));
//        }
//        return this;
//    }
//
//    public UKitDeviceActionSequence add(UKitDeviceActionSequence actionSequence) {
//        if(!hasExecute) {
//            actions.addAll(actionSequence.actions);
//        }
//        return this;
//    }
//
//    public UKitDeviceActionSequence callback(IActionSequenceCallback callback) {
//        if(!hasExecute) {
//            this.callback = callback;
//        }
//        return this;
//    }
//
//    public UKitDeviceActionSequence listener(IActionSequenceProcessListener listener) {
//        if(!hasExecute) {
//            this.listener = listener;
//        }
//        return this;
//    }
//
//    public boolean execute() {
//        if(actions.isEmpty()) {
//            return false;
//        }
////        LogUtils.e("BAS.execute: %s %b", this.toString(), hasAbort);
//        innerExecute();
//        return true;
//    }
//
//    private void innerExecute() {
//        hasExecute = true;
//        if(listener != null) {
//            listener.onProcessBegin(this);
//        }
//        executeActions.addAll(actions);
//        total = actions.size();
//        next(this);
//    }
//
//    public boolean abort() {
//        if(!hasExecute || hasAbort) {
//            return false;
//        }
//        hasAbort = true;
//        actions.clear();
//        executeActions.clear();
//        total = -1;
//        index = 0;
//        return true;
//    }
//
//    public UKitDeviceActionSequence loop(boolean loop) {
//        this.loop = loop;
//        return this;
//    }
//
//    @Override
//    public <T> boolean addCommandRequest(UKitCommandRequest cmd, UKitCommandPriority priority, Object tag, IUKitCommandResponse<T> callback) {
//        return false;
//    }
//
//    public int getActionCount() {
//        return actions.size();
//    }
//
//    public boolean hasExecute() {
//        return hasExecute;
//    }
//
//    public boolean hasAbort() {
//        return hasAbort;
//    }
//
//    public boolean hasFinish() {
//        return hasAbort || hasFinish;
//    }
//
//    public ActionParameter getNextActionParameter() {
//        if(executeActions == null || executeActions.isEmpty()) {
//            return null;
//        }
//        return executeActions.get(0);
//    }
//
//    private static void next(final UKitDeviceActionSequence sequence) {
////        LogUtils.e("BAS.next: %s %b", sequence.toString(), sequence.hasAbort);
//        BluetoothHelper.getBtHandler().removeCallbacks(sequence.timeoutCallback);
//        BluetoothHelper.getBtHandler().removeCallbacks(sequence.ignoreFailureTimeoutCallback);
//        if(sequence.hasAbort) {
//            if (!sequence.hasFinish && sequence.listener != null) {
//                sequence.listener.onProcessEnd(sequence);
//            }
//            return;
//        }
//        if(sequence.executeActions.isEmpty()) {
//            if(sequence.loop) {
//                sequence.index = 0;
//                sequence.executeActions.addAll(sequence.actions);
//            } else {
//                sendCallback(sequence, true);
//                return;
//            }
//        }
//        if(sequence.listener != null) {
//            sequence.listener.onProcessChanged(sequence, sequence.index++, sequence.total);
//            if(sequence.executeActions.isEmpty()) {
//                return;
//            }
//        }
//        ActionParameter actionParameter = sequence.executeActions.remove(0);
//        if(actionParameter instanceof ConnectActionParameter) {
//            ConnectActionParameter connectActionParameter = (ConnectActionParameter)actionParameter;
//            if(connectActionParameter.device == null) {
//                sendCallback(sequence, false);
//                return;
//            }
//            if(connectActionParameter.timeout > 0) {
//                BluetoothHelper.getBtHandler().postDelayed(sequence.timeoutCallback, connectActionParameter.timeout);
//            }
//            sequence.lastActionTime = System.currentTimeMillis();
//            BluetoothHelper.connect(connectActionParameter.device, InnerDeviceConnectProcessListener.getInstance(sequence, connectActionParameter.listener));
//        }
//        if(actionParameter instanceof CommandActionParameter) {
//            if(!BluetoothHelper.isBluetoothConnected()) {
//                sendCallback(sequence, false);
//                return;
//            }
//            CommandActionParameter commandActionParameter = (CommandActionParameter)actionParameter;
//            if(commandActionParameter.timeout > 0) {
//                Runnable callback = commandActionParameter.ignoreFailure ? sequence.ignoreFailureTimeoutCallback : sequence.timeoutCallback;
//                BluetoothHelper.getBtHandler().postDelayed(callback, commandActionParameter.timeout);
//            }
//            sequence.lastActionTime = System.currentTimeMillis();
//            BluetoothHelper.addCommand(commandActionParameter.command, commandActionParameter.priority, InnerExecutionCallback.getInstance(sequence, commandActionParameter.ignoreFailure, commandActionParameter.callback));
//        }
//        if(actionParameter instanceof DelayActionParameter) {
//            DelayActionParameter delayActionParameter = (DelayActionParameter)actionParameter;
//            long delayTimeMs;
//            if(sequence.lastActionTime > 0) {
//                delayTimeMs = sequence.lastActionTime + delayActionParameter.delayTimeMs - System.currentTimeMillis();
//                if (delayTimeMs <= 0) {
//                    next(sequence);
//                    return;
//                }
//            } else {
//                delayTimeMs = delayActionParameter.delayTimeMs;
//            }
//            if(delayActionParameter.timeout > 0) {
//                BluetoothHelper.getBtHandler().postDelayed(sequence.timeoutCallback, delayActionParameter.timeout);
//            }
//            sequence.lastActionTime = System.currentTimeMillis();
//            InnerDelayAction.getInstance(sequence, delayTimeMs).start();
//        }
//        if(actionParameter instanceof SleepActionParameter) {
//            SleepActionParameter sleepActionParameter = (SleepActionParameter)actionParameter;
//            if(sleepActionParameter.timeout > 0) {
//                BluetoothHelper.getBtHandler().postDelayed(sequence.timeoutCallback, sleepActionParameter.timeout);
//            }
//            sequence.lastActionTime = System.currentTimeMillis();
//            InnerDelayAction.getInstance(sequence, sleepActionParameter.sleepTimeMs).start();
//        }
//        if(actionParameter instanceof UpgradeActionParameter) {
//            UpgradeActionParameter upgradeActionParameter = (UpgradeActionParameter)actionParameter;
//            boolean result = false;
//            if(upgradeActionParameter.timeout > 0) {
//                BluetoothHelper.getBtHandler().postDelayed(sequence.timeoutCallback, upgradeActionParameter.timeout);
//            }
//            sequence.lastActionTime = System.currentTimeMillis();
//            if(upgradeActionParameter.isBoard) {
//                result = BoardFrameworkUpgradeProcess.upgrade(upgradeActionParameter.version, upgradeActionParameter.file, InnerUpgradeMsgListener.getInstance(sequence, upgradeActionParameter.callback));
//            } else if(upgradeActionParameter.isSteeringGear) {
//                result = SteeringGearFrameworkUpgradeProcess.upgrade(upgradeActionParameter.version, upgradeActionParameter.file, InnerUpgradeMsgListener.getInstance(sequence, upgradeActionParameter.callback));
//            } else if(upgradeActionParameter.sensorType != null) {
//                result = SensorFrameworkUpgradeProcess.upgrade(upgradeActionParameter.sensorType, upgradeActionParameter.version, upgradeActionParameter.file, InnerUpgradeMsgListener.getInstance(sequence, upgradeActionParameter.callback));
//            }
//            if(!result) {
//                sendCallback(sequence, false);
//            }
//        }
//    }
//
//    private static void sendCallback(final UKitDeviceActionSequence sequence, final boolean result) {
//        if(sequence.sentCallback) {
//            return;
//        }
//        sequence.sentCallback = true;
//        sequence.abort();
//        BluetoothHelper.getBtHandler().removeCallbacks(sequence.timeoutCallback);
//        BluetoothHelper.getBtHandler().removeCallbacks(sequence.ignoreFailureTimeoutCallback);
//        BluetoothHelper.getBtHandler().post(new Runnable() {
//            @Override
//            public void run() {
//                if(sequence.callback != null) {
//                    sequence.callback.onActionSequenceFinished(sequence, result);
//                }
//            }
//        });
//        if (sequence.listener != null) {
//            sequence.listener.onProcessEnd(sequence);
//        }
//        sequence.hasFinish = true;
//    }
//
//    private static class InnerExecutionCallback implements IUKitCommandResponse {
//        private UKitDeviceActionSequence sequence;
//        private IUKitCommandResponse outsideCallback;
//        private boolean ignoreFailure;
//
//        public InnerExecutionCallback(UKitDeviceActionSequence sequence, boolean ignoreFailure, IUKitCommandResponse outsideCallback) {
//            this.sequence = sequence;
//            this.outsideCallback = outsideCallback;
//            this.ignoreFailure = ignoreFailure;
//        }
//
//        private static InnerExecutionCallback getInstance(UKitDeviceActionSequence sequence, boolean ignoreFailure, IUKitCommandResponse outsideCallback) {
//            return new InnerExecutionCallback(sequence, ignoreFailure, outsideCallback);
//        }
//
//        @Override
//        public void onUKitCommandResponse(UKitCommandResponse result) {
//            if(outsideCallback != null) {
//                outsideCallback.onUKitCommandResponse(result);
//            }
//            if(ignoreFailure || (result != null && result.isSuccess())) {
//                next(sequence);
//            } else {
//                sendCallback(sequence, false);
//            }
//        }
//    }
//
//    private static class InnerDeviceConnectProcessListener implements IUKitDeviceConnectStateChangeListener {
//        private UKitDeviceActionSequence sequence;
//        private IUKitDeviceConnectStateChangeListener outsideListener;
//
//        public InnerDeviceConnectProcessListener(UKitDeviceActionSequence sequence, IUKitDeviceConnectStateChangeListener outsideListener) {
//            this.sequence = sequence;
//            this.outsideListener = outsideListener;
//        }
//
//        private static InnerDeviceConnectProcessListener getInstance(UKitDeviceActionSequence sequence, IUKitDeviceConnectStateChangeListener outsideListener) {
//            return new InnerDeviceConnectProcessListener(sequence, outsideListener);
//        }
//
//        @Override
//        public void onConnectStateChanged(UKitRemoteDevice device, boolean oldState, boolean newState, boolean verified) {
//            if(outsideListener != null) {
//                outsideListener.onConnectStateChanged(device, oldState, newState, verified);
//            }
//            if(newState) {
//                next(sequence);
//            } else {
//                sendCallback(sequence, false);
//            }
//        }
//    }
//
//    private static class InnerUpgradeMsgListener implements IUKitDeviceUpgradeMsgListener {
//        private UKitDeviceActionSequence sequence;
//        private IUKitDeviceUpgradeMsgListener outsideListener;
//
//        public InnerUpgradeMsgListener(UKitDeviceActionSequence sequence, IUKitDeviceUpgradeMsgListener outsideListener) {
//            this.sequence = sequence;
//            this.outsideListener = outsideListener;
//        }
//
//        private static InnerUpgradeMsgListener getInstance(UKitDeviceActionSequence sequence, IUKitDeviceUpgradeMsgListener outsideListener) {
//            return new InnerUpgradeMsgListener(sequence, outsideListener);
//        }
//
//        @Override
//        public void onPercentChanged(int percent) {
//            if(outsideListener != null) {
//                outsideListener.onPercentChanged(percent);
//            }
//        }
//
//        @Override
//        public void onBeginUpgrade() {
//            if(outsideListener != null) {
//                outsideListener.onBeginUpgrade();
//            }
//        }
//
//        @Override
//        public void onEndUpgrade(int code, String msg) {
//            if(outsideListener != null) {
//                outsideListener.onEndUpgrade(code, msg);
//            }
//            if(code == 0) {
//                next(sequence);
//            } else {
//                LogUtils.e("Upgrade failure: %s", msg);
//                sendCallback(sequence, false);
//            }
//        }
//
//        @Override
//        public void onBeginTransmission() {
//            if(outsideListener != null) {
//                outsideListener.onBeginTransmission();
//            }
//        }
//
//        @Override
//        public void onEndTransmission() {
//            if(outsideListener != null) {
//                outsideListener.onEndTransmission();
//            }
//        }
//
//        @Override
//        public void onBeginWriteFlash() {
//            if(outsideListener != null) {
//                outsideListener.onBeginWriteFlash();
//            }
//        }
//
//        @Override
//        public void onEndWriteFlash() {
//            if(outsideListener != null) {
//                outsideListener.onEndWriteFlash();
//            }
//        }
//    }
//
//    private static class InnerDelayAction extends Thread {
//        private UKitDeviceActionSequence sequence;
//        private long delayTimeMs;
//
//        public InnerDelayAction(UKitDeviceActionSequence sequence, long delayTimeMs) {
//            super("UKitDeviceActionSequence-Delay");
//            this.sequence = sequence;
//            this.delayTimeMs = delayTimeMs;
//        }
//
//        private static InnerDelayAction getInstance(UKitDeviceActionSequence sequence, long delayTimeMs) {
//            return new InnerDelayAction(sequence, delayTimeMs);
//        }
//
//        @Override
//        public void run() {
//            try {
//                Thread.sleep(delayTimeMs);
//            } catch (Exception e) {
//                // ignore
//            }
//            BluetoothHelper.getBtHandler().post(new Runnable() {
//                @Override
//                public void run() {
//                    next(sequence);
//                }
//            });
//        }
//    }
//
//}
