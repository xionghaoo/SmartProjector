package com.ubtedu.ukit.bluetooth;

import android.util.ArrayMap;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoMissionCallback;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoUpgradeCallback;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocation;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocationSequence;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationNames;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationParamKeys;
import com.ubtedu.deviceconnect.libs.base.model.URoColor;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentID;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoMainBoardInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoMotionModel;
import com.ubtedu.deviceconnect.libs.base.model.URoRotateMotorCommand;
import com.ubtedu.deviceconnect.libs.base.model.URoSensorData;
import com.ubtedu.deviceconnect.libs.base.model.URoSpeakerInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoSubscribeSensorInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoUkitAngleFeedbackInfo;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoAudioRecord;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoFileStat;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoNetworkState;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiScanApInfo;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiStatusInfo;
import com.ubtedu.ukit.bluetooth.ota.OtaHelper;
import com.ubtedu.ukit.project.vo.Motion;
import com.ubtedu.ukit.project.vo.MotionFrame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BtInvocationFactory {
    private BtInvocationFactory() {
    }

    public static UkitInvocation restartBoard(IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_REBOOT);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation getWiFiList(IUKitCommandResponse<URoWiFiScanApInfo[]> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_GET_WIFI_LIST);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation setWiFiConfig(URoWiFiScanApInfo apInfo, String password, IUKitCommandResponse<URoWiFiStatusInfo> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_SET_WIFI_CONFIG);
        invocation.setParameter("scanApInfo", apInfo);
        invocation.setParameter("password", password);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation getWiFiState(IUKitCommandResponse<URoWiFiStatusInfo> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_GET_WIFI_STATE);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation disconnectWiFi(IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_SET_WIFI_CONNECT_ENABLE);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ENABLE, false);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation getNetworkState(IUKitCommandResponse<URoNetworkState> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_GET_NETWORK_STATE);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation boardSelfCheck(boolean isEnabled) {
        return boardSelfCheck(isEnabled, null);
    }

    public static UkitInvocation faultClear() {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_DEV_FAULT_CLEAR);
        return invocation;
    }

    public static UkitInvocation checkComponent(URoComponentID componentID, IUKitCommandResponse<Boolean> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_COMPONENT_CHECK);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID, componentID);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation boardSelfCheck(boolean isEnabled, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_SELF_CHECK);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ENABLE, isEnabled);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation turnServos(ArrayMap<Integer, Integer> idAnglePairs, int time, int paddingTime) {
        return turnServos(idAnglePairs, time, paddingTime, null);
    }

    public static UkitInvocation turnServos(ArrayMap<Integer, Integer> idAnglePairs, int time, int paddingTime, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(BluetoothHelper.isSmartVersion() ? URoInvocationNames.INVOCATION_BATCH_SERVOS_TURN_SIMULTANEOUS : URoInvocationNames.INVOCATION_SERVOS_TURN);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_TIME, time);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_PADDING_TIME, paddingTime);
        invocation.setParameter(URoInvocationParamKeys.Servos.PARAM_KEY_TURN_ID_ANGLE_PAIR, idAnglePairs);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation rotateServos(ArrayList<Integer> ids, int speed) {
        return rotateServos(ids, speed, null);
    }


    public static UkitInvocation rotateServos(ArrayList<Integer> ids, int speed, IUKitCommandResponse<Void> callback) {
        return rotateServos(integerListToArray(ids), speed, callback);
    }

    public static UkitInvocation rotateServos(int[] ids, int speed) {
        return rotateServos(ids, speed, null);
    }

    public static URoInvocationSequence rotateServos(ArrayList<Integer> ids, int speed, int time) {
        return rotateServos(integerListToArray(ids), speed, time, null);
    }

    public static URoInvocationSequence rotateServos(int[] ids, int speed, int time) {
        return rotateServos(ids, speed, time, null);
    }

    public static URoInvocationSequence rotateServos(int[] ids, int speed, int time, URoMissionCallback<Void> callback) {
        URoInvocationSequence invocationSequence = new URoInvocationSequence();
        invocationSequence.action(rotateServos(ids, speed, null));
        invocationSequence.delay(time);
        invocationSequence.action(stopServos(ids));
        invocationSequence.setCompletionCallback(callback);
        return invocationSequence;
    }

    public static UkitInvocation rotateServos(int[] ids, int speed, IUKitCommandResponse<Void> callback) {
        ArrayMap<Integer, Integer> idSpeedMap = new ArrayMap<>();
        if (ids != null && ids.length > 0) {
            for (int i = 0; i < ids.length; i++) {
                idSpeedMap.put(ids[i], speed);
            }
        }
        return rotateServos(idSpeedMap, callback);
    }

    public static UkitInvocation rotateServos(ArrayMap<Integer, Integer> idSpeedMap, IUKitCommandResponse<Void> callback) {
        ArrayMap<Integer, Integer> validMap = new ArrayMap<>();
        for (int id : idSpeedMap.keySet()) {
            if (isServosIdValid(id)) {
                validMap.put(id, idSpeedMap.get(id));
            }
        }
        UkitInvocation invocation = new UkitInvocation(BluetoothHelper.isSmartVersion() ? URoInvocationNames.INVOCATION_BATCH_SERVOS_ROTATE : URoInvocationNames.INVOCATION_SERVOS_ROTATE);
        invocation.setParameter(URoInvocationParamKeys.Servos.PARAM_KEY_ROTATE_ID_SPEED_PAIR, validMap);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation stopServos(int[] ids) {
        return stopServos(ids, null);
    }

    public static UkitInvocation stopServos(int[] ids, IUKitCommandResponse<Void> callback) {
        if (BluetoothHelper.isSmartVersion()) {
            return rotateServos(servosIdValidFilter(ids), 0, callback);
        } else {
            UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_SERVOS_STOP);
            invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, servosIdValidFilter(ids));
            invocation.setCompletionCallback(callback);
            return invocation;
        }
    }

    public static UkitInvocation readbackServos(ArrayList<Integer> ids, boolean powerOff, IUKitCommandResponse<URoUkitAngleFeedbackInfo> callback) {
        return readbackServos(integerListToArray(ids), powerOff, callback);
    }

    public static UkitInvocation readbackServos(int[] ids, boolean powerOff, IUKitCommandResponse<URoUkitAngleFeedbackInfo> callback) {
        return readbackServos(ids, powerOff, 10000L, callback);
    }

    public static UkitInvocation readbackServos(int[] ids, boolean powerOff, long timeout, IUKitCommandResponse<URoUkitAngleFeedbackInfo> callback) {
        UkitInvocation invocation = new UkitInvocation(BluetoothHelper.isSmartVersion() ? URoInvocationNames.INVOCATION_BATCH_SERVOS_READBACK_SIMULTANEOUS : URoInvocationNames.INVOCATION_BATCH_SERVOS_READBACK);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, servosIdValidFilter(ids));
        invocation.setParameter(URoInvocationParamKeys.Servos.PARAM_KEY_READBACK_POWEROFF, powerOff);
        invocation.setTimeoutThreshold(timeout);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation rotateMotors(int[] ids, int speed) {
        return rotateMotors(ids, speed, Integer.MAX_VALUE, null);
    }

    public static UkitInvocation rotateMotors(int[] ids, int speed, int time) {
        return rotateMotors(ids, speed, time, null);
    }

    public static UkitInvocation rotateMotors(int[] ids, int speed, int time, IUKitCommandResponse<Void> callback) {
        int[] validIds = idValidFilter(ids);
        URoRotateMotorCommand[] commands;
        if (validIds.length > 0) {
            commands = new URoRotateMotorCommand[validIds.length];
            for (int i = 0; i < validIds.length; i++) {
                commands[i] = new URoRotateMotorCommand(validIds[i], speed, time);
            }
        } else {
            commands = new URoRotateMotorCommand[0];
        }
        return rotateMotors(commands, callback);
    }

    public static UkitInvocation rotateMotors(URoRotateMotorCommand[] commands, IUKitCommandResponse<Void> callback) {
        ArrayList<URoRotateMotorCommand> commandArrayList = new ArrayList<>();
        if (commands != null && commands.length > 0) {
            for (int i = 0; i < commands.length; i++) {
                if (isIdValid(commands[i].getId())) {
                    commandArrayList.add(commands[i]);
                }
            }
        }
        commands = new URoRotateMotorCommand[commandArrayList.size()];
        UkitInvocation invocation = new UkitInvocation(BluetoothHelper.isSmartVersion() ? URoInvocationNames.INVOCATION_BATCH_MOTOR_ROTATE : URoInvocationNames.INVOCATION_MOTOR_ROTATE);
        invocation.setParameter(URoInvocationParamKeys.Motor.PARAM_KEY_ROTATE_COMMAND, commandArrayList.toArray(commands));
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation stopMotors(int[] ids) {
        return stopMotors(ids, null);
    }

    public static UkitInvocation stopMotors(int[] ids, IUKitCommandResponse<Void> callback) {
        if (BluetoothHelper.isSmartVersion()) {
            return rotateMotors(ids, 0, Integer.MAX_VALUE, callback);
        } else {
            UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_MOTOR_STOP);
            invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, idValidFilter(ids));
            invocation.setCompletionCallback(callback);
            return invocation;
        }
    }

    public static UkitInvocation takeMotion(URoMotionModel motion) {
        return takeMotion(motion, null);
    }

    public static UkitInvocation takeMotion(URoMotionModel motion, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_TAKE_MOTION);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_MOTION, motion);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation readSensors(List<URoComponentID> ids) {
        return readSensors(ids, null);
    }

    public static UkitInvocation readSensors(URoComponentID singleId, IUKitCommandResponse<URoSensorData> callback) {
        ArrayList<URoComponentID> list = new ArrayList<>();
        list.add(singleId);
        return readSensors(list, callback);
    }

    public static UkitInvocation readSensors(List<URoComponentID> ids, IUKitCommandResponse<URoSensorData> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_BATCH_SENSOR_READ);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, ids);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation setUltrasoundColor(int[] ids, URoColor color, int time) {
        return setUltrasoundColor(ids, color, time, null);
    }

    public static UkitInvocation setUltrasoundColor(int[] ids, URoColor color, int time, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_SENSOR_SET_ULTRASOUND_COLOR);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, ids);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_TIME, time);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_COLOR, color);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation setLEDColor(ArrayList<Integer> ids, ArrayList<URoColor> colors, long time, IUKitCommandResponse<Void> callback) {
        return setLEDColor(integerListToArray(ids), colors, time, callback);
    }

    public static UkitInvocation setLEDColor(int[] ids, ArrayList<URoColor> colors, long time, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_LED_SET_COLOR);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, ids);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_TIME, time);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_COLOR, colors);
        return invocation;
    }

    public static UkitInvocation setLEDEffect(int effectID, ArrayList<Integer> ids, URoColor color, int times, IUKitCommandResponse<Void> callback) {
        return setLEDEffect(effectID, integerListToArray(ids), color, times, callback);
    }

    public static UkitInvocation setLEDEffect(int effectID, int[] ids, URoColor color, int times, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_LED_SHOW_EFFECTS);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, ids);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_COLOR, color);
        invocation.setParameter(URoInvocationParamKeys.Sensor.PARAM_KEY_EFFECT_ID, effectID);
        invocation.setParameter(URoInvocationParamKeys.Sensor.PARAM_KEY_TIMES, times);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation changeID(int oldId, int newId, URoComponentType type, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_MODIFY_ID);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_TYPE, type);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_SRC_ID, oldId);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_NEW_ID, newId);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation setEnable(URoComponentID componentID, boolean enabled) {
        return setEnable(componentID.getId(), componentID.getComponentType(), enabled, null);
    }

    public static UkitInvocation setEnable(int id, URoComponentType type, boolean enabled, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_SENSOR_ENABLE);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID, id);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_TYPE, type);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ENABLE, enabled);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static <R> UkitInvocation readSensor(int id, URoComponentType type, IUKitCommandResponse<R> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_SENSOR_READ);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID, new URoComponentID(type, id));
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation readSpeakerInfo(IUKitCommandResponse<URoSpeakerInfo> callback) {
        //fixme id
        return readSpeakerInfo(0, callback);
    }

    public static UkitInvocation readSpeakerInfo(int id, IUKitCommandResponse<URoSpeakerInfo> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_SPEAKER_INFO);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID, id);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation readMainboardInfo() {
        return readMainboardInfo(null);
    }

    public static UkitInvocation readMainboardInfo(IUKitCommandResponse<URoMainBoardInfo> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_MAIN_BOARD_READ);
        invocation.setCompletionCallback(new URoCompletionCallback<URoMainBoardInfo>() {
            @Override
            public void onComplete(URoCompletionResult<URoMainBoardInfo> result) {
                if (callback != null) {
                    callback.onUKitCommandResponse(new URoCompletionResult<URoMainBoardInfo>(BluetoothHelper.getBoardInfoData(), result.getError()));
                }
            }
        });
        return invocation;
    }

    public static URoInvocation upgradeMainBoard(OtaHelper.OtaVersionInfo versionInfo, URoUpgradeCallback progressCallback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_UPGRADE);
        if (!BluetoothHelper.isSmartVersion()) {
            invocation.setParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_URL, versionInfo.getFile());
        } else {
            invocation.setParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_URL, versionInfo.getUrl());
            invocation.setParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_MD5, versionInfo.getFileMd5());
            invocation.setParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_SIZE, versionInfo.getFileSize());
        }
        invocation.setCompletionCallback(progressCallback);
        return invocation;
    }

    public static UkitInvocation upgradeComponent(URoComponentType componentType, OtaHelper.OtaVersionInfo versionInfo, URoUpgradeCallback progressCallback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_UPGRADE_COMPONENT);
        if (!BluetoothHelper.isSmartVersion()) {
            invocation.setParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_URL, versionInfo.getFile());
            invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, new int[]{0});
        } else {
            invocation.setParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_URL, versionInfo.getUrl());
            invocation.setParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_MD5, versionInfo.getFileMd5());
            invocation.setParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_SIZE, versionInfo.getFileSize());
            invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, BluetoothHelper.getComponentByType(componentType));
        }
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_TYPE, componentType);
        invocation.setCompletionCallback(progressCallback);
        return invocation;
    }

    public static URoInvocationSequence exeMotion(Motion motion) {
        return exeMotion(motion, null);
    }

    public static URoInvocationSequence exeMotion(Motion motion, URoCompletionCallback callback) {
        URoInvocationSequence invocationSequence = new URoInvocationSequence();
        invocationSequence.setCompletionCallback(callback);
        if (motion != null) {
            List<MotionFrame> frames = motion.frames;
            List<Integer> servos = motion.servos;
            if (frames == null || frames.isEmpty() || servos == null || servos.isEmpty()) {
                //格式错误
                return invocationSequence;
            }
            for (MotionFrame frame : frames) {
                if (frame.actived.isEmpty() || frame.angles.isEmpty() || frame.angles.size() != servos.size()) {
                    //格式错误
//                    return invocationSequence;
                    //Math operands should be cast before assignment
                    long time = frame.time;
                    invocationSequence.sleep(time + frame.waittime);
                    continue;
                }
                ArrayMap<Integer, Integer> idAnglePairs = new ArrayMap<>();
                for (Integer id : frame.actived) {
                    int index = servos.indexOf(id);
                    if (index == -1) {
                        //格式错误
                        return invocationSequence;
                    }
                    int angle = frame.angles.get(index);
                    idAnglePairs.put(id, angle);
                }
                long time = frame.time;
                UkitInvocation turnServos = turnServos(idAnglePairs, frame.time, frame.waittime, null);
                turnServos.setTimeoutThreshold(time);
                invocationSequence.action(turnServos);
                invocationSequence.delay(time + frame.waittime);
            }
        }
        return invocationSequence;
    }

    public static UkitInvocation stopRunning(URoCompletionCallback<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_STOP_RUNNING);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation sendFile(String source, String target, URoMissionCallback callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_SEND_FILE);
        invocation.setParameter(URoInvocationParamKeys.File.PARAM_KEY_SOURCE, source);
        invocation.setParameter(URoInvocationParamKeys.File.PARAM_KEY_TARGET, target);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation sendFileData(byte[] data, String target, URoMissionCallback callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_SEND_FILE_DATA);
        invocation.setParameter(URoInvocationParamKeys.File.PARAM_KEY_SOURCE_DATA, data);
        invocation.setParameter(URoInvocationParamKeys.File.PARAM_KEY_TARGET, target);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation recvFile(String source, String target, URoMissionCallback callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_RECV_FILE);
        invocation.setParameter(URoInvocationParamKeys.File.PARAM_KEY_SOURCE, source);
        invocation.setParameter(URoInvocationParamKeys.File.PARAM_KEY_TARGET, target);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation getFileStat(String fileName, IUKitCommandResponse<URoFileStat> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_GET_FILE_STAT);
        invocation.setParameter("path", fileName);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation startExecScript(String fileName, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_START_EXEC_SCRIPT);
        invocation.setParameter(URoInvocationParamKeys.File.PARAM_KEY_TARGET, fileName);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation stopExecScript(IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_STOP_EXEC_SCRIPT);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation updateScriptValue(String key, String value, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_UPDATE_SCRIPT_VALUE);
        invocation.addPairParam("paramt", key, value);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static URoInvocationSequence updateScriptValues(HashMap<String, String> valueMap, IUKitCommandResponse<Void> callback) {
        URoInvocationSequence sequence = new URoInvocationSequence();
        ArrayList<HashMap<String, String>> groupList = groupHashMap(valueMap, 20);
        if (groupList != null && !groupList.isEmpty()) {
            for (int i = 0; i < groupList.size(); i++) {
                URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_UPDATE_SCRIPT_VALUE);
                invocation.setParameter("paramt", groupList.get(i));
                sequence.action(invocation);
            }
        }
        sequence.setCompletionCallback(callback);
        return sequence;
    }

    private static <K, V> ArrayList<HashMap<K, V>> groupHashMap(HashMap<K, V> originMap, int groupSize) {
        if (originMap == null || originMap.isEmpty() || groupSize < 1) {
            return new ArrayList<>();
        }
        ArrayList<HashMap<K, V>> groupList = new ArrayList<>();
        if (originMap.size() < groupSize) {
            groupList.add(originMap);
            return groupList;
        }
        HashMap<K, V> tempMap = new HashMap<>();
        int keyIndex = 0;
        for (Map.Entry<K, V> entry : originMap.entrySet()) {
            keyIndex++;
            tempMap.put(entry.getKey(), entry.getValue());
            if (tempMap.size() == groupSize || keyIndex == originMap.size()) {
                groupList.add(new HashMap<>(tempMap));
                tempMap.clear();
            }
        }
        return groupList;
    }

    public static UkitInvocation cleanupScriptValue(IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_CLEANUP_SCRIPT_VALUE);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation updateScriptEvent(String key, String value, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_UPDATE_SCRIPT_EVENT);
        invocation.addPairParam("paramt", key, value);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation restartScript(IUKitCommandResponse<Void> callback) {
        return updateScriptEvent("control", "restart",callback);
    }


    public static UkitInvocation cleanupScriptEvent(IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_CLEANUP_SCRIPT_EVENT);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation fileRename(String oldName, String newName, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_RENAME_FILE);
        invocation.setParameter("old_name", oldName);
        invocation.setParameter("new_name", newName);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation fileDelete(String path, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_DELETE_FILE);
        invocation.setParameter("path", path);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation subscribeSensor(URoSubscribeSensorInfo subscribeInfo, IUKitCommandResponse<Void> callback) {
        return subscribeSensors(new URoSubscribeSensorInfo[]{subscribeInfo}, callback);
    }

    public static UkitInvocation subscribeSensors(URoSubscribeSensorInfo[] subscribeInfos, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_SENSOR_SUBSCRIBE);
        invocation.setParameter("subscribeInfos", subscribeInfos);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation reboot(IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_REBOOT);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation startAudioPlay(int sessionId, String name, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_START_AUDIO_PLAY);
        invocation.setCompletionCallback(callback);
        invocation.setParameter("session_id", sessionId);
        invocation.setParameter("file_name", name);
        return invocation;
    }

    public static UkitInvocation startSoundPlay(int sessionId, String type, String name, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_START_SOUND_PLAY);
        invocation.setCompletionCallback(callback);
        invocation.setParameter("session_id", sessionId);
        invocation.setParameter("file_type", type);
        invocation.setParameter("file_name", name);
        return invocation;
    }

    public static UkitInvocation stopAudioPlay(IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_STOP_AUDIO_PLAY);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation startAudioRecord(int sessionId, String name, long duration, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_START_AUDIO_RECORD);
        invocation.setCompletionCallback(callback);
        invocation.setParameter("session_id", sessionId);
        invocation.setParameter("file_name", name);
        invocation.setParameter("file_duration", duration);
        return invocation;
    }

    public static UkitInvocation stopAudioRecord(IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_STOP_AUDIO_RECORD);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation cancelAudioRecord(IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_CANCEL_AUDIO_RECORD);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation deleteAudioRecord(URoAudioRecord audioRecord, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_DELETE_AUDIO_RECORD);
        invocation.setCompletionCallback(callback);
        invocation.setParameter("audio_record", audioRecord);
        return invocation;
    }

    public static UkitInvocation renameAudioRecord(URoAudioRecord audioRecord, String newName, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_RENAME_AUDIO_RECORD);
        invocation.setCompletionCallback(callback);
        invocation.setParameter("audio_record", audioRecord);
        invocation.setParameter("new_name", newName);
        return invocation;
    }

    public static UkitInvocation getAudioRecordList(IUKitCommandResponse<URoAudioRecord[]> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_GET_AUDIO_RECORD_LIST);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation getAudioRecordNum(IUKitCommandResponse<Integer> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_GET_AUDIO_RECORD_NUM);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation setAudioVolume(int volume, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_AUDIO_SET_VOLUME);
        invocation.setCompletionCallback(callback);
        invocation.setParameter("volume", volume);
        return invocation;
    }

    public static UkitInvocation setLEDStripColor(int id, int port, int startIndex, int endIndex, int color, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_CMD_LED_BELT_EXP_SET);
        invocation.setParameter("id", id);
        invocation.setParameter("port", port);
        for (int i = startIndex - 1; i < endIndex; i++) {
            invocation.addPairParam("color_lump", i, color);
        }
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation setLEDStripColorContinues(int id, int[] port, int startIndex, int endIndex, int color, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_CMD_LED_BELT_EXPRESSIONS_CONTINUOUS_SET);
        invocation.setParameter("id", id);
        for (int i = 0; i < port.length; i++) {
            String key = "port" + i;
            invocation.addPairParam(key, "port", port[i]);
            invocation.addPairParam(key, "start_pixel", startIndex);
            invocation.addPairParam(key, "end_pixel", endIndex);
            invocation.addPairParam(key, "rgbc", color);
        }
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation setLEDStripExpression(int id, int port, int type, int times, int color, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_CMD_LED_BELT_FIX_EXP_SET);
        invocation.setParameter("id", id);
        invocation.setParameter("port", port);
        invocation.setParameter("expressions_type", type);
        invocation.setParameter("rgbc", color);
        invocation.setParameter("time", times);
        invocation.setCompletionCallback(callback);
        return invocation;
    }

    public static UkitInvocation setLEDStripBrightness(int id, int port, int brightness, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_CMD_LED_BELT_BRIGHTNESS_SET);
        invocation.setCompletionCallback(callback);
        invocation.setParameter("id", id);
        invocation.addPairParam("brightness", port, brightness);
        return invocation;
    }

    public static UkitInvocation moveLEDStrip(int id, int[] port, int pixel, int times, IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_CMD_LED_BELT_MOVE_SET);
        invocation.setCompletionCallback(callback);
        invocation.setParameter("id", id);
        for (int i = 0; i < port.length; i++) {
            String key = "port" + i;
            invocation.addPairParam(key, "port", port[i]);
            invocation.addPairParam(key, "pixel", pixel);
            invocation.addPairParam(key, "time", times);
        }
        return invocation;
    }

    public static UkitInvocation closeLEDStrip(int id, int port[], IUKitCommandResponse<Void> callback) {
        UkitInvocation invocation = new UkitInvocation(URoInvocationNames.INVOCATION_CMD_LED_BELT_OFF_SET);
        invocation.setCompletionCallback(callback);
        invocation.setParameter("id", id);
        invocation.setParameter("ports", port);
        return invocation;
    }

    private static int[] integerListToArray(ArrayList<Integer> list) {
        int size = list != null ? list.size() : 0;
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private static int[] servosIdValidFilter(int[] ids) {
        int length = ids != null ? ids.length : 0;
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            if (isServosIdValid(ids[i])) {
                list.add(ids[i]);
            }
        }
        return integerListToArray(list);
    }

    private static int[] idValidFilter(int[] ids) {
        int length = ids != null ? ids.length : 0;
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            if (isIdValid(ids[i])) {
                list.add(ids[i]);
            }
        }
        return integerListToArray(list);
    }

    private static boolean isServosIdValid(int id) {
        return id >= 1 && id <= 32;
    }

    private static boolean isIdValid(int id) {
        return id >= 1 && id <= 8;
    }
}
