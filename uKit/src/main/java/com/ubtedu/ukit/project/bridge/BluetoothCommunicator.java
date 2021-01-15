package com.ubtedu.ukit.project.bridge;

import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.SparseArray;

import com.ubtedu.alpha1x.utils.MD5Util;
import com.ubtedu.bridge.BridgeBoolean;
import com.ubtedu.bridge.BridgeResult;
import com.ubtedu.bridge.OnCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoMissionCallback;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocationSequence;
import com.ubtedu.deviceconnect.libs.base.invocation.URoMissionAbortSignal;
import com.ubtedu.deviceconnect.libs.base.model.URoAngleParam;
import com.ubtedu.deviceconnect.libs.base.model.URoColor;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoRotateMotorCommand;
import com.ubtedu.deviceconnect.libs.base.model.URoUkitAngleFeedbackInfo;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoFileStat;
import com.ubtedu.deviceconnect.libs.utils.URoNumberConversionUtil;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.BtInvocationFactory;
import com.ubtedu.ukit.bluetooth.ErrorCollectSequenceCallback;
import com.ubtedu.ukit.bluetooth.IUKitCommandResponse;
import com.ubtedu.ukit.bluetooth.UkitInvocation;
import com.ubtedu.ukit.bluetooth.dialog.SendScriptFileDialogFragment;
import com.ubtedu.ukit.bluetooth.error.BluetoothCommonErrorHelper;
import com.ubtedu.ukit.bluetooth.model.AngleFeedback;
import com.ubtedu.ukit.bluetooth.model.SteeringGear;
import com.ubtedu.ukit.bluetooth.processor.PeripheralErrorCollector;
import com.ubtedu.ukit.bluetooth.processor.PyScriptRunningStateHolder;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.common.utils.MicroPythonUtils;
import com.ubtedu.ukit.project.Workspace;
import com.ubtedu.ukit.project.blockly.UKitAudioVolume;
import com.ubtedu.ukit.project.bridge.api.ActivityHelper;
import com.ubtedu.ukit.project.bridge.api.ToastHelper;
import com.ubtedu.ukit.project.vo.Motion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @Author naOKi
 * @Date 2018/12/18
 **/
public class BluetoothCommunicator {

    private final HashSet<URoMissionAbortSignal> mRunningInvocationSequences;

    private BluetoothCommunicator() {
        mRunningInvocationSequences = new HashSet<>();
    }

    private static class SingletonHolder {
        private final static BluetoothCommunicator instance = new BluetoothCommunicator();
    }

    public static BluetoothCommunicator getInstance() {
        return BluetoothCommunicator.SingletonHolder.instance;
    }

    private static final SparseArray<Float> LED_DELAY_VALUE;

    static {
        LED_DELAY_VALUE = new SparseArray<>();
        LED_DELAY_VALUE.append(0, 2.55f);
        LED_DELAY_VALUE.append(1, 4.4f);
        LED_DELAY_VALUE.append(2, 2.8f);
        LED_DELAY_VALUE.append(3, 1.72f);
        LED_DELAY_VALUE.append(4, 2.95f);
        LED_DELAY_VALUE.append(5, 0.8f);
        LED_DELAY_VALUE.append(6, 2.05f);
        LED_DELAY_VALUE.append(7, 3.4f);
        LED_DELAY_VALUE.append(8, 3.0f);
        LED_DELAY_VALUE.append(9, 0.4f);
        LED_DELAY_VALUE.append(10, 0.3f);
        LED_DELAY_VALUE.append(11, 1.0f);
        LED_DELAY_VALUE.append(12, 2.4f);
        LED_DELAY_VALUE.append(13, 2.04f);
        LED_DELAY_VALUE.append(14, 1.5f);
        LED_DELAY_VALUE.append(15, 8.4f);
    }

    public void setServo(String args, final OnCallback callback) {
        try {
            /*
             * 用JSONObject解析，能发现是否有字段或是结构错误的情况。用Gson无法识别
             */
            JSONObject jsonObject = new JSONObject(args);
            int time = jsonObject.getInt("time");
            JSONArray angles = jsonObject.getJSONArray("angle");
            if (angles.length() == 0) {
                callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
                return;
            }

            ArrayMap idAnglePairs = new ArrayMap();
            for (int i = 0; i < angles.length(); i++) {
                JSONObject angle = angles.getJSONObject(i);
                int id = angle.getInt("id");
                PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.SERVOS, id);
                idAnglePairs.put(id, angle.getInt("degree"));
            }
            URoInvocationSequence invocationSequence = new URoInvocationSequence();
            invocationSequence.action(BtInvocationFactory.turnServos(idAnglePairs, time, 0));
            invocationSequence.delay(time);
            invocationSequence.setCompletionCallback(new URoInvocationSequenceCallback() {
                @Override
                public void onComplete(URoCompletionResult result) {
                    BridgeResult bridgeResult = BridgeResult.SUCCESS();
                    bridgeResult.data = BridgeBoolean.wrap(result.isSuccess());
                    callResult(callback, bridgeResult);
                }
            });
            BluetoothHelper.addCommand(invocationSequence);

//            UKitDeviceActionSequence bas = UKitDeviceActionSequence.newActionSequence();
//            UKitCmdSteeringGearAngle.Builder builder = new UKitCmdSteeringGearAngle.Builder();
//            builder.setRotateTime(time);
//            for (int i = 0; i < angles.length(); i++) {
//                JSONObject angle = angles.getJSONObject(i);
//                int id = angle.getInt("id");
//                PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.PERIPHERAL_STEERING_GEAR, id);
//                builder.addParam(id, angle.getInt("degree"));
//            }
//            bas.command(builder.build(), true);
//            bas.delay(time);
//            addActionSequenceRecord(bas);
//            bas.callback(new IActionSequenceCallback() {
//                @Override
//                public void onActionSequenceFinished(UKitDeviceActionSequence bas, boolean isSuccess) {
//                    removeActionSequenceRecord(bas);
//                    BridgeResult result = BridgeResult.SUCCESS();
//                    result.data = BridgeBoolean.wrap(isSuccess);
//                    callResult(callback, result);
//                }
//            });
//            bas.execute();


        } catch (JSONException e) {
            callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
        } catch (Exception e) {
            callResult(callback, BridgeResult.FAIL());
        }
    }

    public void setServobySpeed(String args, final OnCallback callback) {
        try {
            /*
             * 用JSONObject解析，能发现是否有字段或是结构错误的情况。用Gson无法识别
             */
            JSONArray jsonArray = new JSONArray(args);
            ArrayList<HashMap<String, Object>> grouping = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("id");
                PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.SERVOS, id);
                int direction = jsonObject.getInt("direction");
                String speed = jsonObject.getString("speed").toUpperCase();
                if (direction == SteeringGear.API_STOP) {
                    speed = SteeringGear.Speed.M.name().toUpperCase();
                }
                ArrayList<Integer> itemId = null;
                for (int j = 0; j < grouping.size(); j++) {
                    HashMap<String, Object> item = grouping.get(j);
                    int itemDirection = (Integer) item.get("direction");
                    String itemSpeed = (String) item.get("speed");
                    if (itemDirection == direction && TextUtils.equals(speed, itemSpeed)) {
                        itemId = (ArrayList<Integer>) item.get("id");
                        break;
                    }
                }
                if (itemId == null) {
                    HashMap<String, Object> item = new HashMap<>();
                    itemId = new ArrayList<>();
                    item.put("speed", speed);
                    item.put("direction", direction);
                    item.put("id", itemId);
                    grouping.add(item);
                }
                if (!itemId.contains(id)) {
                    itemId.add(id);
                }
            }
            if (grouping.size() == 0) {
                callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
                return;
            }
//            UKitDeviceActionSequence bas = UKitDeviceActionSequence.newActionSequence();
//            for (int j = 0; j < grouping.size(); j++) {
//                HashMap<String, Object> item = grouping.get(j);
//                int itemDirection = (Integer) item.get("direction");
//                String itemSpeed = (String) item.get("speed");
//                ArrayList<Integer> itemId = (ArrayList<Integer>) item.get("id");
//            UKitCmdSteeringGearLoop.Builder builder = new UKitCmdSteeringGearLoop.Builder();
//            builder.addIds(itemId);
//            builder.setMode(itemDirection);
//            builder.setSpeed(UKitCmdSteeringGearLoop.Speed.valueOf(itemSpeed));
//            bas.command(builder.build(), true);
//            }
//            addActionSequenceRecord(bas);
//            bas.callback(new IActionSequenceCallback() {
//                @Override
//                public void onActionSequenceFinished(UKitDeviceActionSequence bas, boolean isSuccess) {
//                    removeActionSequenceRecord(bas);
//                    BridgeResult result = BridgeResult.SUCCESS();
//                    result.data = BridgeBoolean.wrap(isSuccess);
//                    callResult(callback, result);
//                }
//            });
//            bas.execute();
            URoInvocationSequence invocationSequence = new URoInvocationSequence();
            for (int j = 0; j < grouping.size(); j++) {
                HashMap<String, Object> item = grouping.get(j);
                int itemDirection = (Integer) item.get("direction");
                String itemSpeed = (String) item.get("speed");
                ArrayList<Integer> itemId = (ArrayList<Integer>) item.get("id");
                int speed = SteeringGear.getSpeedInMode(SteeringGear.Speed.valueOf(itemSpeed).getValue(), itemDirection);
                invocationSequence.action(BtInvocationFactory.rotateServos(itemId, speed));
            }
            invocationSequence.setCompletionCallback(new URoInvocationSequenceCallback() {
                @Override
                public void onComplete(URoCompletionResult result) {
                    BridgeResult bridgeResult = BridgeResult.SUCCESS();
                    bridgeResult.data = BridgeBoolean.wrap(result.isSuccess());
                    callResult(callback, bridgeResult);
                }
            });
            invocationSequence.sendTogether(true);
            BluetoothHelper.addCommand(invocationSequence);
        } catch (JSONException e) {
            callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
        } catch (Exception e) {
            callResult(callback, BridgeResult.FAIL());
        }
    }

    public void setServoBySpeedPercent(String args, final OnCallback callback) {
        try {
            /*
             * 用JSONObject解析，能发现是否有字段或是结构错误的情况。用Gson无法识别
             */
            JSONArray jsonArray = new JSONArray(args);
            ArrayList<HashMap<String, Object>> grouping = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("id");
                PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.SERVOS, id);
                int direction = jsonObject.getInt("direction");
                int percent = Math.max(Math.min(jsonObject.getInt("percent"), 100), 0);
                if (direction == SteeringGear.API_STOP || percent == 0) {
                    percent = 0;
                    direction = SteeringGear.API_STOP;
                }
                ArrayList<Integer> itemId = null;
                for (int j = 0; j < grouping.size(); j++) {
                    HashMap<String, Object> item = grouping.get(j);
                    int itemDirection = (Integer) item.get("direction");
                    int itemPercent = (Integer) item.get("percent");
                    if (itemDirection == direction && itemPercent == percent) {
                        itemId = (ArrayList<Integer>) item.get("id");
                        break;
                    }
                }
                if (itemId == null) {
                    HashMap<String, Object> item = new HashMap<>();
                    itemId = new ArrayList<>();
                    item.put("percent", percent);
                    item.put("direction", direction);
                    item.put("id", itemId);
                    grouping.add(item);
                }
                if (!itemId.contains(id)) {
                    itemId.add(id);
                }
            }
            if (grouping.size() == 0) {
                callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
                return;
            }
//            BluetoothActionSequence bas = BluetoothActionSequence.newActionSequence();
//            for (int j = 0; j < grouping.size(); j++) {
//                HashMap<String, Object> item = grouping.get(j);
//                int itemDirection = (Integer) item.get("direction");
//                int itemPercent = (Integer) item.get("percent");
//                ArrayList<Integer> itemId = (ArrayList<Integer>) item.get("id");
//                SteeringGearLoop.Builder builder = new SteeringGearLoop.Builder();
//                builder.addIds(itemId);
//                builder.setMode(itemDirection);
//                builder.setSpeed(SteeringGearLoop.Speed.VF.getValue() * itemPercent / 100);
//                bas.command(builder.build(), true);
//            }
//            addActionSequenceRecord(bas);
//            bas.callback(new IActionSequenceCallback() {
//                @Override
//                public void onActionSequenceFinished(BluetoothActionSequence bas, boolean isSuccess) {
//                    removeActionSequenceRecord(bas);
//                    BridgeResult result = BridgeResult.SUCCESS();
//                    result.data = BridgeBoolean.wrap(isSuccess);
//                    callResult(callback, result);
//                }
//            });
//            bas.execute();

            URoInvocationSequence invocationSequence = new URoInvocationSequence();
            for (int j = 0; j < grouping.size(); j++) {
                HashMap<String, Object> item = grouping.get(j);
                int itemDirection = (Integer) item.get("direction");
                int itemPercent = (Integer) item.get("percent");
                ArrayList<Integer> itemId = (ArrayList<Integer>) item.get("id");
                int speed = SteeringGear.getSpeedInMode(SteeringGear.Speed.VF.getValue() * itemPercent / 100, itemDirection);
                invocationSequence.action(BtInvocationFactory.rotateServos(itemId, speed));
            }
            invocationSequence.setCompletionCallback(new URoInvocationSequenceCallback() {
                @Override
                public void onComplete(URoCompletionResult result) {
                    BridgeResult bridgeResult = BridgeResult.SUCCESS();
                    bridgeResult.data = BridgeBoolean.wrap(result.isSuccess());
                    callResult(callback, bridgeResult);
                }
            });
            invocationSequence.sendTogether(true);
            BluetoothHelper.addCommand(invocationSequence);
        } catch (JSONException e) {
            callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
        } catch (Exception e) {
            callResult(callback, BridgeResult.FAIL());
        }
    }

    public void getPosture(final OnCallback callback) {
        try {
            ArrayList<Integer> expectFeedback = new ArrayList<>();
            if (Workspace.getInstance() != null
                    && Workspace.getInstance().getProject() != null
                    && Workspace.getInstance().getProject().modelInfo != null) {
                expectFeedback.addAll(Workspace.getInstance().getProject().modelInfo.steeringGear);
            }

            IUKitCommandResponse<URoUkitAngleFeedbackInfo> warpCallback = new IUKitCommandResponse<URoUkitAngleFeedbackInfo>() {
                @Override
                public void onUKitCommandResponse(URoCompletionResult<URoUkitAngleFeedbackInfo> result) {
                    if (!result.isSuccess()) {
                        callResult(callback, BridgeResult.FAIL());
                        return;
                    }
                    if (result.getData().getErrorIds() != null && result.getData().getErrorIds().length != 0) {
//                    if ((!result.isSuccess()) || result.getData().size() != expectFeedback.size()) {
                        callResult(callback, BridgeResult.FAIL());
                        BluetoothHelper.getBtHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                String msg = UKitApplication.getInstance().getString(R.string.blockly_anglefeedback_error_msg);
                                ToastHelper.toastShort(msg);
                            }
                        });
                        return;
                    }

//                    URoAngleParam[] bridgeResultData = new URoAngleParam[expectFeedback.size()];
//                    for (int i = 0; i < expectFeedback.size(); i++) {
//                        bridgeResultData[i] = new URoAngleParam(expectFeedback.get(i), result.getData().get(i));
//                    }

                    AngleFeedback angleFeedback = new AngleFeedback(result.getData().getAngles());
                    final String anomalyMsg = angleFeedback.getAnomalyMsg();
                    if (!TextUtils.isEmpty(anomalyMsg)) {
                        callResult(callback, BridgeResult.FAIL());
                        BluetoothHelper.getBtHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                String msg = UKitApplication.getInstance().getApplicationContext().getString(R.string.bluetooth_angle_anomaly_msg, anomalyMsg);
                                BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.ANGLE_ANOMALY, null, msg, null, null);
                            }
                        });
                        return;
                    }
                    BridgeResult bridgeResult = BridgeResult.SUCCESS();

                    bridgeResult.data = result.getData().getAngles();
                    callResult(callback, bridgeResult);
                }
            };

            BluetoothHelper.addCommand(BtInvocationFactory.readbackServos(expectFeedback, false, warpCallback));
        } catch (Exception e) {
            callResult(callback, BridgeResult.FAIL());
        }
    }

    public void servoDropPower(OnCallback callback) {
        if (Workspace.getInstance() == null || Workspace.getInstance().getProject() == null || Workspace.getInstance().getProject().modelInfo == null) {
            callResult(callback, BridgeResult.FAIL());
            return;
        }
        dropPower(Workspace.getInstance().getProject().modelInfo.steeringGear, callback);
    }

    public void setMotorSpeed(String args, final OnCallback callback) {
        try {
            /*
             * 用JSONObject解析，能发现是否有字段或是结构错误的情况。用Gson无法识别
             */
            JSONArray speeds = new JSONArray(args);
            if (speeds.length() == 0) {
                callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
                return;
            }
//            UKitCmdLowMotorStart.Builder builder = new UKitCmdLowMotorStart.Builder();
//            builder.setTime(0xFFFF);
//            for (int i = 0; i < speeds.length(); i++) {
//                JSONObject speed = speeds.getJSONObject(i);
//                int id = speed.getInt("id");
//                PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.PERIPHERAL_LOW_MOTOR, id);
//                builder.addParam(id, speed.getInt("speed"));
//            }
//            BluetoothHelper.addCommand(builder.build(), new IUKitCommandResponse<UKitDummyData>() {
//                @Override
//                public void onUKitCommandResponse(UKitCommandResponse<UKitDummyData> result) {
//                    BridgeResult bridgeResult = BridgeResult.SUCCESS();
//                    bridgeResult.data = BridgeBoolean.wrap(result.isSuccess());
//                    callResult(callback, bridgeResult);
//                }
//            });
            URoRotateMotorCommand[] commands = new URoRotateMotorCommand[speeds.length()];
            for (int i = 0; i < speeds.length(); i++) {
                JSONObject speed = speeds.getJSONObject(i);
                int id = speed.getInt("id");
                PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.MOTOR, id);
                commands[i] = new URoRotateMotorCommand(id, speed.getInt("speed"), Integer.MAX_VALUE);
            }
            BluetoothHelper.addCommand(BtInvocationFactory.rotateMotors(commands, new IUKitCommandResponse<Void>() {
                @Override
                public void onUKitCommandResponse(URoCompletionResult<Void> result) {
                    BridgeResult bridgeResult = BridgeResult.SUCCESS();
                    bridgeResult.data = BridgeBoolean.wrap(result.isSuccess());
                    callResult(callback, bridgeResult);
                }
            }));
        } catch (JSONException e) {
            callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
        } catch (Exception e) {
            callResult(callback, BridgeResult.FAIL());
        }
    }

    public void stopMotor(String args, final OnCallback callback) {
        try {
            /*
             * 用JSONObject解析，能发现是否有字段或是结构错误的情况。用Gson无法识别
             */
            JSONObject jsonObject = new JSONObject(args);
            JSONArray ids = jsonObject.getJSONArray("id");
            if (ids.length() == 0) {
                callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
                return;
            }
//            UKitCmdLowMotorStop.Builder builder = new UKitCmdLowMotorStop.Builder();
//            for (int i = 0; i < ids.length(); i++) {
//                int id = ids.getInt(i);
//                PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.PERIPHERAL_LOW_MOTOR, id);
//                builder.addId(id);
//            }
//            BluetoothHelper.addCommand(builder.build(), new IUKitCommandResponse<UKitDummyData>() {
//                @Override
//                public void onUKitCommandResponse(UKitCommandResponse<UKitDummyData> result) {
//                    BridgeResult bridgeResult = BridgeResult.SUCCESS();
//                    bridgeResult.data = BridgeBoolean.wrap(result.isSuccess());
//                    callResult(callback, bridgeResult);
//                }
//            });
            int[] idArray = new int[ids.length()];
            for (int i = 0; i < ids.length(); i++) {
                int id = ids.getInt(i);
                PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.MOTOR, id);
                idArray[i] = id;
            }
            BluetoothHelper.addCommand(BtInvocationFactory.stopMotors(idArray, new IUKitCommandResponse<Void>() {
                @Override
                public void onUKitCommandResponse(URoCompletionResult<Void> result) {
                    BridgeResult bridgeResult = BridgeResult.SUCCESS();
                    bridgeResult.data = BridgeBoolean.wrap(result.isSuccess());
                    callResult(callback, bridgeResult);
                }
            }));
        } catch (JSONException e) {
            callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
        } catch (Exception e) {
            callResult(callback, BridgeResult.FAIL());
        }
    }

    public void setEmoji(String args, final OnCallback callback) {
        try {
            /*
             * 用JSONObject解析，能发现是否有字段或是结构错误的情况。用Gson无法识别
             */
            JSONObject jsonObject = new JSONObject(args);
            JSONArray emojis = jsonObject.getJSONArray("emoji");
            boolean isdelay = BridgeBoolean.isTrue(jsonObject.optInt("isdelay"));
            if (emojis.length() == 0) {
                callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
                return;
            }
            int times = jsonObject.getInt("times");
            ArrayList<HashMap<String, Object>> grouping = new ArrayList<>();
            for (int i = 0; i < emojis.length(); i++) {
                JSONObject emoji = emojis.getJSONObject(i);
                int id = emoji.getInt("id");
                PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.LED, id);
                int emotionIndex = emoji.getInt("emotionIndex");
                int color = URoNumberConversionUtil.hex2IntegerE(emoji.getString("color").substring(1), 16) & 0xFFFFFF;
                ArrayList<Integer> itemId = null;
                for (int j = 0; j < grouping.size(); j++) {
                    HashMap<String, Object> item = grouping.get(j);
                    int itemEmotionIndex = (Integer) item.get("emotionIndex");
                    int itemColor = (Integer) item.get("color");
                    if (itemEmotionIndex == emotionIndex && itemColor == color) {
                        itemId = (ArrayList<Integer>) item.get("id");
                    }
                }
                if (itemId == null) {
                    HashMap<String, Object> item = new HashMap<>();
                    itemId = new ArrayList<>();
                    item.put("emotionIndex", emotionIndex);
                    item.put("color", color);
                    item.put("id", itemId);
                    grouping.add(item);
                }
                if (!itemId.contains(id)) {
                    itemId.add(id);
                }
            }
            if (grouping.size() == 0) {
                callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
                return;
            }
//            UKitDeviceActionSequence bas = UKitDeviceActionSequence.newActionSequence();
//            float maxDelay = 0.0f;
//            for (int j = 0; j < grouping.size(); j++) {
//                HashMap<String, Object> item = grouping.get(j);
//                int itemEmotionIndex = (Integer) item.get("emotionIndex");
//                int itemColor = (Integer) item.get("color");
//                maxDelay = Math.max(maxDelay, LED_DELAY_VALUE.get(itemEmotionIndex));
//                ArrayList<Integer> itemId = (ArrayList<Integer>) item.get("id");
//                UKitCmdLedEmotion.Builder builder = new UKitCmdLedEmotion.Builder();
//                builder.setTime(times);
//                builder.setEmotionIndex(itemEmotionIndex);
//                builder.setColor(itemColor);
//                builder.addIds(itemId);
//                bas.command(builder.build(), true);
//            }
//            addActionSequenceRecord(bas);
//            if (isdelay) {
//                bas.callback(new IActionSequenceCallback() {
//                    @Override
//                    public void onActionSequenceFinished(UKitDeviceActionSequence bas, boolean isSuccess) {
//                        removeActionSequenceRecord(bas);
//                    }
//                });
//            } else {
//                bas.callback(new IActionSequenceCallback() {
//                    @Override
//                    public void onActionSequenceFinished(UKitDeviceActionSequence bas, boolean isSuccess) {
//                        removeActionSequenceRecord(bas);
//                        BridgeResult result = BridgeResult.SUCCESS();
//                        result.data = BridgeBoolean.wrap(isSuccess);
//                        callResult(callback, result);
//                    }
//                });
//            }
//            bas.execute();
//            if (isdelay) {
////                LogUtils.e("setEmoji time: %d, times: %d, maxDelay: %f", (long)((times * 1000) * maxDelay), times, maxDelay);
//                UKitDeviceActionSequence delayBas = UKitDeviceActionSequence.newActionSequence();
//                delayBas.sleep((long) ((times * 1000) * maxDelay));
//                addActionSequenceRecord(delayBas);
//                delayBas.callback(new IActionSequenceCallback() {
//                    @Override
//                    public void onActionSequenceFinished(UKitDeviceActionSequence bas, boolean isSuccess) {
//                        removeActionSequenceRecord(bas);
//                        BridgeResult result = BridgeResult.SUCCESS();
//                        result.data = BridgeBoolean.wrap(true);
//                        callResult(callback, result);
//                    }
//                });
//                delayBas.execute();
//            }

            URoInvocationSequence invocationSequence = new URoInvocationSequence();
            float maxDelay = 0.0f;
            for (int j = 0; j < grouping.size(); j++) {
                HashMap<String, Object> item = grouping.get(j);
                int itemEmotionIndex = (Integer) item.get("emotionIndex");
                int itemColor = (Integer) item.get("color");
                maxDelay = Math.max(maxDelay, LED_DELAY_VALUE.get(itemEmotionIndex));
                ArrayList<Integer> itemId = (ArrayList<Integer>) item.get("id");
                BluetoothHelper.addCommand(BtInvocationFactory.setLEDEffect(itemEmotionIndex, itemId, new URoColor(itemColor), times, null));
            }
            if (isdelay) {
                invocationSequence.setCompletionCallback(new URoInvocationSequenceCallback() {
                    @Override
                    public void onComplete(URoCompletionResult result) {
                    }
                });
            } else {
                invocationSequence.setCompletionCallback(new URoInvocationSequenceCallback() {
                    @Override
                    public void onComplete(URoCompletionResult result) {
                        BridgeResult bridgeResult = BridgeResult.SUCCESS();
                        bridgeResult.data = BridgeBoolean.wrap(result.isSuccess());
                        callResult(callback, bridgeResult);
                    }
                });
            }
            invocationSequence.sendTogether(true);
            BluetoothHelper.addCommand(invocationSequence);
            if (isdelay) {
//                LogUtils.e("setEmoji time: %d, times: %d, maxDelay: %f", (long)((times * 1000) * maxDelay), times, maxDelay);
                URoInvocationSequence delayInvocation = new URoInvocationSequence();
                delayInvocation.sleep((long) ((times * 1000) * maxDelay));
                delayInvocation.setCompletionCallback(new URoInvocationSequenceCallback() {
                    @Override
                    public void onComplete(URoCompletionResult result) {
                        BridgeResult bridgeResult = BridgeResult.SUCCESS();
                        bridgeResult.data = BridgeBoolean.wrap(true);
                        callResult(callback, bridgeResult);
                    }
                });
                BluetoothHelper.addCommand(delayInvocation);
            }
        } catch (JSONException e) {
            callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
        } catch (Exception e) {
            callResult(callback, BridgeResult.FAIL());
        }
    }

    public void setScenelight(String args, final OnCallback callback) {
        try {
            /*
             * 用JSONObject解析，能发现是否有字段或是结构错误的情况。用Gson无法识别
             */
            JSONObject jsonObject = new JSONObject(args);
            JSONArray scenelights = jsonObject.getJSONArray("scenelight");
            boolean isdelay = BridgeBoolean.isTrue(jsonObject.optInt("isdelay"));
            if (scenelights.length() == 0) {
                callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
                return;
            }
            int times = jsonObject.getInt("times");
            ArrayList<HashMap<String, Object>> grouping = new ArrayList<>();
            for (int i = 0; i < scenelights.length(); i++) {
                JSONObject scenelight = scenelights.getJSONObject(i);
                int id = scenelight.getInt("id");
                PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.LED, id);
                int emotionIndex = scenelight.getInt("emotionIndex");
                ArrayList<Integer> itemId = null;
                for (int j = 0; j < grouping.size(); j++) {
                    HashMap<String, Object> item = grouping.get(j);
                    int itemEmotionIndex = (Integer) item.get("emotionIndex");
                    if (itemEmotionIndex == emotionIndex) {
                        itemId = (ArrayList<Integer>) item.get("id");
                    }
                }
                if (itemId == null) {
                    HashMap<String, Object> item = new HashMap<>();
                    itemId = new ArrayList<>();
                    item.put("emotionIndex", emotionIndex);
                    item.put("id", itemId);
                    grouping.add(item);
                }
                if (!itemId.contains(id)) {
                    itemId.add(id);
                }
            }
            if (grouping.size() == 0) {
                callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
                return;
            }
//            UKitDeviceActionSequence bas = UKitDeviceActionSequence.newActionSequence();
//            float maxDelay = 0.0f;
//            for (int j = 0; j < grouping.size(); j++) {
//                HashMap<String, Object> item = grouping.get(j);
//                int itemEmotionIndex = (Integer) item.get("emotionIndex");
//                maxDelay = Math.max(maxDelay, LED_DELAY_VALUE.get(itemEmotionIndex));
//                ArrayList<Integer> itemId = (ArrayList<Integer>) item.get("id");
//                UKitCmdLedEmotion.Builder builder = new UKitCmdLedEmotion.Builder();
//                builder.setTime(times);
//                builder.setEmotionIndex(itemEmotionIndex);
//                builder.addIds(itemId);
//                bas.command(builder.build(), true);
//            }
//            addActionSequenceRecord(bas);
//            if (isdelay) {
//                bas.callback(new IActionSequenceCallback() {
//                    @Override
//                    public void onActionSequenceFinished(UKitDeviceActionSequence bas, boolean isSuccess) {
//                        removeActionSequenceRecord(bas);
//                    }
//                });
//            } else {
//                bas.callback(new IActionSequenceCallback() {
//                    @Override
//                    public void onActionSequenceFinished(UKitDeviceActionSequence bas, boolean isSuccess) {
//                        removeActionSequenceRecord(bas);
//                        BridgeResult result = BridgeResult.SUCCESS();
//                        result.data = BridgeBoolean.wrap(isSuccess);
//                        callResult(callback, result);
//                    }
//                });
//            }
//            bas.execute();
//            if (isdelay) {
////                LogUtils.e("setScenelight time: %d, times: %d, maxDelay: %f", (long)((times * 1000) * maxDelay), times, maxDelay);
//                UKitDeviceActionSequence delayBas = UKitDeviceActionSequence.newActionSequence();
//                delayBas.sleep((long) ((times * 1000) * maxDelay));
//                addActionSequenceRecord(delayBas);
//                delayBas.callback(new IActionSequenceCallback() {
//                    @Override
//                    public void onActionSequenceFinished(UKitDeviceActionSequence bas, boolean isSuccess) {
//                        removeActionSequenceRecord(bas);
//                        BridgeResult result = BridgeResult.SUCCESS();
//                        result.data = BridgeBoolean.wrap(true);
//                        callResult(callback, result);
//                    }
//                });
//                delayBas.execute();
//            }

            URoInvocationSequence invocationSequence = new URoInvocationSequence();
            float maxDelay = 0.0f;
            for (int j = 0; j < grouping.size(); j++) {
                HashMap<String, Object> item = grouping.get(j);
                int itemEmotionIndex = (Integer) item.get("emotionIndex");
                maxDelay = Math.max(maxDelay, LED_DELAY_VALUE.get(itemEmotionIndex));
                ArrayList<Integer> itemId = (ArrayList<Integer>) item.get("id");
                invocationSequence.action(BtInvocationFactory.setLEDEffect(itemEmotionIndex, itemId, new URoColor(0), times, null));
            }
            if (isdelay) {
                invocationSequence.setCompletionCallback(new URoInvocationSequenceCallback() {
                    @Override
                    public void onComplete(URoCompletionResult result) {
                    }
                });
            } else {
                invocationSequence.setCompletionCallback(new URoInvocationSequenceCallback() {
                    @Override
                    public void onComplete(URoCompletionResult result) {
                        BridgeResult bridgeResult = BridgeResult.SUCCESS();
                        bridgeResult.data = BridgeBoolean.wrap(result.isSuccess());
                        callResult(callback, bridgeResult);
                    }
                });
            }
            invocationSequence.sendTogether(true);
            BluetoothHelper.addCommand(invocationSequence);
            if (isdelay) {
//                LogUtils.e("setScenelight time: %d, times: %d, maxDelay: %f", (long)((times * 1000) * maxDelay), times, maxDelay);
                URoInvocationSequence delayInvocation = new URoInvocationSequence();
                delayInvocation.sleep((long) ((times * 1000) * maxDelay));
                delayInvocation.setCompletionCallback(new URoInvocationSequenceCallback() {
                    @Override
                    public void onComplete(URoCompletionResult result) {
                        BridgeResult bridgeResult = BridgeResult.SUCCESS();
                        bridgeResult.data = BridgeBoolean.wrap(true);
                        callResult(callback, bridgeResult);
                    }
                });
                BluetoothHelper.addCommand(delayInvocation);
            }
        } catch (JSONException e) {
            callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
        } catch (Exception e) {
            callResult(callback, BridgeResult.FAIL());
        }
    }

    public void setLEDs(String args, final OnCallback callback) {
        try {
            /*
             * 用JSONObject解析，能发现是否有字段或是结构错误的情况。用Gson无法识别
             */
            JSONObject jsonObject = new JSONObject(args);
            JSONArray leds = jsonObject.getJSONArray("LEDs");
            if (leds.length() == 0) {
                callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
                return;
            }
            int time = jsonObject.getInt("time");
            boolean isdelay = BridgeBoolean.isTrue(jsonObject.optInt("isdelay")) && time < 25500;
            ArrayList<HashMap<String, Object>> grouping = new ArrayList<>();
            for (int i = 0; i < leds.length(); i++) {
                JSONObject emoji = leds.getJSONObject(i);
                int id = emoji.getInt("id");
                PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.LED, id);
                JSONArray jsonColors = emoji.getJSONArray("colors");
                if (jsonColors.length() != 8) {
                    callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
                    return;
                }
                int[] colors = new int[8];
                for (int j = 0; j < 8; j++) {
                    if (TextUtils.isEmpty(jsonColors.getString(j))) {
                        colors[j] = 0;
                    } else {
                        colors[j] = URoNumberConversionUtil.hex2IntegerE(jsonColors.getString(j).substring(1), 16) & 0xFFFFFF;
                    }
                }
                ArrayList<Integer> itemId = null;
                for (int j = 0; j < grouping.size(); j++) {
                    HashMap<String, Object> item = grouping.get(j);
                    int[] itemColors = (int[]) item.get("colors");
                    if (Arrays.equals(colors, itemColors)) {
                        itemId = (ArrayList<Integer>) item.get("id");
                    }
                }
                if (itemId == null) {
                    HashMap<String, Object> item = new HashMap<>();
                    itemId = new ArrayList<>();
                    item.put("colors", colors);
                    item.put("id", itemId);
                    grouping.add(item);
                }
                if (!itemId.contains(id)) {
                    itemId.add(id);
                }
            }
            if (grouping.size() == 0) {
                callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
                return;
            }
//            UKitDeviceActionSequence bas = UKitDeviceActionSequence.newActionSequence();
//            for (int j = 0; j < grouping.size(); j++) {
//                HashMap<String, Object> item = grouping.get(j);
//                int[] colors = (int[]) item.get("colors");
//                ArrayList<Integer> itemId = (ArrayList<Integer>) item.get("id");
//                UKitCmdLedFace.Builder builder = new UKitCmdLedFace.Builder();
//                builder.setTime(time);
//                for (int k = 0; k < 8; k++) {
//                    if (colors[k] != -1) {
//                        builder.addColor(k + 1, colors[k]);
//                    }
//                }
//                builder.addIds(itemId);
//                bas.command(builder.build(), true);
//            }
//            addActionSequenceRecord(bas);
//            if (isdelay) {
//                bas.callback(new IActionSequenceCallback() {
//                    @Override
//                    public void onActionSequenceFinished(UKitDeviceActionSequence bas, boolean isSuccess) {
//                        removeActionSequenceRecord(bas);
//                    }
//                });
//            } else {
//                bas.callback(new IActionSequenceCallback() {
//                    @Override
//                    public void onActionSequenceFinished(UKitDeviceActionSequence bas, boolean isSuccess) {
//                        removeActionSequenceRecord(bas);
//                        BridgeResult result = BridgeResult.SUCCESS();
//                        result.data = BridgeBoolean.wrap(isSuccess);
//                        callResult(callback, result);
//                    }
//                });
//            }
//            bas.execute();
//            if (isdelay) {
////                LogUtils.e("setLEDs delayTime: %d", time);
//                UKitDeviceActionSequence delayBas = UKitDeviceActionSequence.newActionSequence();
//                delayBas.sleep(time);
//                addActionSequenceRecord(delayBas);
//                delayBas.callback(new IActionSequenceCallback() {
//                    @Override
//                    public void onActionSequenceFinished(UKitDeviceActionSequence bas, boolean isSuccess) {
//                        removeActionSequenceRecord(bas);
//                        BridgeResult result = BridgeResult.SUCCESS();
//                        result.data = BridgeBoolean.wrap(true);
//                        callResult(callback, result);
//                    }
//                });
//                delayBas.execute();
//            }

            URoInvocationSequence invocationSequence = new URoInvocationSequence();
            for (int j = 0; j < grouping.size(); j++) {
                HashMap<String, Object> item = grouping.get(j);
                int[] colors = (int[]) item.get("colors");
                ArrayList<Integer> itemId = (ArrayList<Integer>) item.get("id");
                ArrayList<URoColor> uRoColors = new ArrayList<>();
                for (int i = 0; i < colors.length; i++) {
                    uRoColors.add(new URoColor(colors[i]));
                }
                BluetoothHelper.addCommand(BtInvocationFactory.setLEDColor(itemId, uRoColors, time, null));
            }
            if (isdelay) {
                invocationSequence.setCompletionCallback(new URoInvocationSequenceCallback() {
                    @Override
                    public void onComplete(URoCompletionResult result) {
                    }
                });
            } else {
                invocationSequence.setCompletionCallback(new URoInvocationSequenceCallback() {
                    @Override
                    public void onComplete(URoCompletionResult result) {
                        BridgeResult bridgeResult = BridgeResult.SUCCESS();
                        bridgeResult.data = BridgeBoolean.wrap(result.isSuccess());
                        callResult(callback, bridgeResult);
                    }
                });
            }
            invocationSequence.sendTogether(true);
            BluetoothHelper.addCommand(invocationSequence);
            if (isdelay) {
//                LogUtils.e("setLEDs delayTime: %d", time);
                URoInvocationSequence delayInvocation = new URoInvocationSequence();
                delayInvocation.sleep(time);
                delayInvocation.setCompletionCallback(new URoInvocationSequenceCallback() {
                    @Override
                    public void onComplete(URoCompletionResult result) {
                        BridgeResult bridgeResult = BridgeResult.SUCCESS();
                        bridgeResult.data = BridgeBoolean.wrap(true);
                        callResult(callback, bridgeResult);
                    }
                });
                BluetoothHelper.addCommand(delayInvocation);
            }
        } catch (JSONException e) {
            callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
        } catch (Exception e) {
            callResult(callback, BridgeResult.FAIL());
        }
    }

    @Deprecated
    public void turnOffLeds(final OnCallback callback) {
//        if (Workspace.getInstance() == null || Workspace.getInstance().getProject() == null || Workspace.getInstance().getProject().modelInfo == null) {
//            callResult(callback, BridgeResult.FAIL());
//            return;
//        }
//        ModelInfo modelInfo = Workspace.getInstance().getProject().modelInfo;
//        for (Integer id : modelInfo.led) {
//            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.PERIPHERAL_SENSOR_LIGHTING, id);
//        }
//        UKitCmdLedFace.Builder builder = new UKitCmdLedFace.Builder();
//        builder.addIds(modelInfo.led);
//        builder.setTime(0);
//        for (int i = 0; i < 8; i++) {
//            builder.addColor(i + 1, 0);
//        }
//        BluetoothHelper.addCommand(builder.build(), new IUKitCommandResponse<UKitDummyData>() {
//            @Override
//            public void onUKitCommandResponse(UKitCommandResponse<UKitDummyData> result) {
//                BridgeResult bridgeResult = BridgeResult.SUCCESS();
//                bridgeResult.data = BridgeBoolean.wrap(result.isSuccess());
//                callResult(callback, bridgeResult);
//            }
//        });
    }

    @Deprecated
    public void setUltrasonicLED(String args, final OnCallback callback) {
//        try {
//            /*
//             * 用JSONObject解析，能发现是否有字段或是结构错误的情况。用Gson无法识别
//             */
//            JSONObject jsonObject = new JSONObject(args);
//            int id = jsonObject.getInt("id");
//            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.PERIPHERAL_SENSOR_ULTRASOUND, id);
//            int color = URoNumberConversionUtil.hex2IntegerE(jsonObject.getString("color").substring(1), 16) & 0xFFFFFF;
//            int time = jsonObject.getInt("time");
//            UKitCmdLedUltrasound.Builder builder = new UKitCmdLedUltrasound.Builder();
//            builder.addId(id);
//            builder.setColor(color);
//            if (time < 0) {
//                builder.setMode(UKitCmdLedUltrasound.Mode.TURN_ON);
//                builder.setTime(Integer.MAX_VALUE);
//            } else if (time == 0) {
//                builder.setMode(UKitCmdLedUltrasound.Mode.TURN_OFF);
//            } else {
//                builder.setMode(UKitCmdLedUltrasound.Mode.TURN_ON);
//                builder.setTime(time);
//            }
//            BluetoothHelper.addCommand(builder.build(), new IUKitCommandResponse<UKitDummyData>() {
//                @Override
//                public void onUKitCommandResponse(UKitCommandResponse<UKitDummyData> result) {
//                    BridgeResult bridgeResult = BridgeResult.SUCCESS();
//                    bridgeResult.data = BridgeBoolean.wrap(result.isSuccess());
//                    callResult(callback, bridgeResult);
//                }
//            });
//        } catch (JSONException e) {
//            callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
//        } catch (Exception e) {
//            callResult(callback, BridgeResult.FAIL());
//        }
    }

    public void setGroupUltrasonicLED(String args, final OnCallback callback) {
        try {
            /*
             * 用JSONObject解析，能发现是否有字段或是结构错误的情况。用Gson无法识别
             */
            JSONObject jsonObject = new JSONObject(args);
            JSONArray colors = jsonObject.getJSONArray("colors");
            if (colors.length() == 0) {
                callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
                return;
            }
            int time = jsonObject.getInt("time");
            HashMap<Integer, HashSet<Integer>> grouping = new HashMap<>();
            for (int i = 0; i < colors.length(); i++) {
                JSONObject item = colors.getJSONObject(i);
                int id = item.getInt("id");
                PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.ULTRASOUNDSENSOR, id);
                int color;
                if (time == 0) {
                    color = 0;
                } else {
                    if (TextUtils.isEmpty(item.getString("color"))) {
                        color = 0;
                    } else {
                        color = URoNumberConversionUtil.hex2IntegerE(item.getString("color").substring(1), 16) & 0xFFFFFF;
                    }
                }
                HashSet<Integer> ids = grouping.get(color);
                if (ids == null) {
                    ids = new HashSet<>();
                    grouping.put(color, ids);
                }
                ids.add(id);
            }
            if (grouping.size() == 0) {
                callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
                return;
            }
//            UKitDeviceActionSequence bas = UKitDeviceActionSequence.newActionSequence();
//            for (Map.Entry<Integer, HashSet<Integer>> entry : grouping.entrySet()) {
//                int color = entry.getKey();
//                HashSet<Integer> ids = entry.getValue();
//                UKitCmdLedUltrasound.Builder builder = new UKitCmdLedUltrasound.Builder();
//                builder.addIds(ids);
//                builder.setColor(color);
//                if (time < 0) {
//                    builder.setMode(UKitCmdLedUltrasound.Mode.TURN_ON);
//                    builder.setTime(Integer.MAX_VALUE);
//                } else if (time == 0) {
//                    builder.setMode(UKitCmdLedUltrasound.Mode.TURN_OFF);
//                } else {
//                    builder.setMode(UKitCmdLedUltrasound.Mode.TURN_ON);
//                    builder.setTime(time);
//                }
//                bas.command(builder.build(), true);
//            }
//            addActionSequenceRecord(bas);
//            bas.callback(new IActionSequenceCallback() {
//                @Override
//                public void onActionSequenceFinished(UKitDeviceActionSequence bas, boolean isSuccess) {
//                    removeActionSequenceRecord(bas);
//                    BridgeResult result = BridgeResult.SUCCESS();
//                    result.data = BridgeBoolean.wrap(isSuccess);
//                    callResult(callback, result);
//                }
//            });
//            bas.execute();

            URoInvocationSequence invocationSequence = new URoInvocationSequence();
            for (Map.Entry<Integer, HashSet<Integer>> entry : grouping.entrySet()) {
                int color = entry.getKey();
                HashSet<Integer> ids = entry.getValue();
//                UKitCmdLedUltrasound.Builder builder = new UKitCmdLedUltrasound.Builder();
//                builder.addIds(ids);
//                builder.setColor(color);
//                if (time < 0) {
//                    builder.setMode(UKitCmdLedUltrasound.Mode.TURN_ON);
//                    builder.setTime(Integer.MAX_VALUE);
//                } else if (time == 0) {
//                    builder.setMode(UKitCmdLedUltrasound.Mode.TURN_OFF);
//                } else {
//                    builder.setMode(UKitCmdLedUltrasound.Mode.TURN_ON);
//                    builder.setTime(time);
//                }

                int[] idArray = new int[ids.size()];
                int i = 0;
                for (Integer id : ids) {
                    idArray[i] = id;
                    i++;
                }
                //fixme color转换是否正确，mode 与 time没有传值
                invocationSequence.action(BtInvocationFactory.setUltrasoundColor(idArray, new URoColor(color), time));
            }
            invocationSequence.setCompletionCallback(new URoInvocationSequenceCallback() {
                @Override
                public void onComplete(URoCompletionResult result) {
                    BridgeResult bridgeResult = BridgeResult.SUCCESS();
                    bridgeResult.data = BridgeBoolean.wrap(result.isSuccess());
                    callResult(callback, bridgeResult);
                }
            });
            invocationSequence.sendTogether(true);
            BluetoothHelper.addCommand(invocationSequence);
        } catch (JSONException e) {
            callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
        } catch (Exception e) {
            callResult(callback, BridgeResult.FAIL());
        }
    }

    public void execMotion(String id, final OnCallback callback) {
        try {
            if (Workspace.getInstance() == null || Workspace.getInstance().getProject() == null) {
                callResult(callback, BridgeResult.FAIL());
                return;
            }
            if (TextUtils.isEmpty(id)) {
                callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
                return;
            }
            Motion motion = Workspace.getInstance().getProject().getMotion(id);
//            UKitDeviceActionSequence bas = UKitDeviceActionSequenceFactory.createMotionActionSequence(motion, true, null);
//            bas.callback(new IActionSequenceCallback() {
//                @Override
//                public void onActionSequenceFinished(UKitDeviceActionSequence bas, boolean isSuccess) {
//                    removeActionSequenceRecord(bas);
//                    BridgeResult result = BridgeResult.SUCCESS();
//                    result.data = BridgeBoolean.wrap(isSuccess);
//                    callResult(callback, result);
//                }
//            });
//            bas.listener(new IActionSequenceProcessListener() {
//                @Override
//                public void onProcessChanged(UKitDeviceActionSequence bas, int currentIndex, int totalAction) {
//                    UKitDeviceActionSequence.ActionParameter actionParameter = bas.getNextActionParameter();
//                    if (!(actionParameter instanceof UKitDeviceActionSequence.CommandActionParameter)) {
//                        return;
//                    }
//                    UKitDeviceActionSequence.CommandActionParameter commandActionParameter = (UKitDeviceActionSequence.CommandActionParameter) actionParameter;
//                    UKitCommandRequest command = commandActionParameter.getCommand();
//                    if (!(command instanceof UKitCmdSteeringGearAngle)) {
//                        return;
//                    }
//                    UKitCmdSteeringGearAngle steeringGearAngle = (UKitCmdSteeringGearAngle) command;
//                    ArrayList<Integer> ids = steeringGearAngle.getIdList();
//                    if (ids == null || ids.isEmpty()) {
//                        return;
//                    }
//                    for (Integer id : ids) {
//                        PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.PERIPHERAL_STEERING_GEAR, id);
//                    }
//                }
//
//                @Override
//                public void onProcessBegin(UKitDeviceActionSequence bas) {
//                }
//
//                @Override
//                public void onProcessEnd(UKitDeviceActionSequence bas) {
//                }
//            });
//            addActionSequenceRecord(bas);
//            if (!bas.execute()) {
//                removeActionSequenceRecord(bas);
//                callResult(callback, BridgeResult.FAIL());
//            }
            URoInvocationSequence invocationSequence = BtInvocationFactory.exeMotion(motion);
            invocationSequence.setCompletionCallback(new ErrorCollectSequenceCallback(invocationSequence) {
                @Override
                public void onMissionBegin() {
                    addInvocationSequenceRecord(getAbortSignal());
                }

                @Override
                public void onMissionEnd() {
                    if (getAbortSignal() != null) {
                        removeInvocationSequenceRecord(getAbortSignal());
                    }
                }

                @Override
                public void onComplete(URoCompletionResult result) {
                    if (result.isSuccess()) {
                        BridgeResult bridgeResult = BridgeResult.SUCCESS();
                        bridgeResult.data = BridgeBoolean.wrap(result.isSuccess());
                        callResult(callback, bridgeResult);
                    } else {
                        callResult(callback, BridgeResult.FAIL());
                    }
                }
            });
            BluetoothHelper.addCommand(invocationSequence);
        } catch (Exception e) {
            callResult(callback, BridgeResult.FAIL());
        }
    }

    public void dropPower(ArrayList<Integer> ids, final OnCallback callback) {
        try {
            if (ids == null || ids.isEmpty()) {
                callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
                return;
            }
//            BluetoothHelper.addCommand(UKitCmdSteeringGearAngleFeedback.newInstance(ids, true));
            //设置dropPower用角度回读指令？
            PeripheralErrorCollector.getInstance().setShielded(true);
            UkitInvocation invocation = BtInvocationFactory.readbackServos(ids, true, new IUKitCommandResponse<URoUkitAngleFeedbackInfo>() {
                @Override
                protected void onUKitCommandResponse(URoCompletionResult<URoUkitAngleFeedbackInfo> result) {
                    PeripheralErrorCollector.getInstance().setShielded(false);
                }
            });
            invocation.setSendComponentError(false);
            if (!BluetoothHelper.addCommand(invocation)) {
                PeripheralErrorCollector.getInstance().setShielded(false);
            }
            BridgeResult bridgeResult = BridgeResult.SUCCESS();
            bridgeResult.data = BridgeBoolean.wrap(true);
            callResult(callback, bridgeResult);
        } catch (Exception e) {
            PeripheralErrorCollector.getInstance().setShielded(false);
            callResult(callback, BridgeResult.FAIL());
        }
    }

    public void servoPowerOff(String args, final OnCallback callback) {
        try {
            /*
             * 用JSONObject解析，能发现是否有字段或是结构错误的情况。用Gson无法识别
             */
            JSONArray ids = new JSONArray(args);
            if (ids.length() == 0) {
                callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
                return;
            }
            ArrayList<Integer> idList = new ArrayList<>();
            for (int i = 0; i < ids.length(); i++) {
                int id = ids.getInt(i);
                idList.add(id);
            }
            dropPower(idList, callback);
        } catch (JSONException e) {
            callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
        } catch (Exception e) {
            callResult(callback, BridgeResult.FAIL());
        }
    }

    public void angleFeedback(final boolean isSingle, String args, final OnCallback callback) {
        try {
            /*
             * 用JSONObject解析，能发现是否有字段或是结构错误的情况。用Gson无法识别
             */
            JSONArray ids = new JSONArray(args);
            if (ids.length() == 0) {
                callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
                return;
            }
//            ArrayList<Integer> expectFeedback = new ArrayList<>();
//            for (int i = 0; i < ids.length(); i++) {
//                int id = ids.getInt(i);
//                PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.PERIPHERAL_STEERING_GEAR, id);
//                expectFeedback.add(id);
//            }
//            IUKitCommandResponse<URoUkitLegacyAngleFeedbackInfo> warpCallback = new IUKitCommandResponse<URoUkitLegacyAngleFeedbackInfo>() {
//                @Override
//                public void onUKitCommandResponse(UKitCommandResponse<URoUkitLegacyAngleFeedbackInfo> result) {
//                    BridgeResult bridgeResult;
//                    if (result.getData().getErrorIds() == null || result.getData().getErrorIds().length == 0) {
//                        bridgeResult = BridgeResult.SUCCESS();
//                    } else {
//                        bridgeResult = BridgeResult.FAIL();
//                    }
//                    bridgeResult.complete = BridgeBoolean.wrap(isSingle);
//                    bridgeResult.data = result.getData().getAngles();
//                    callResult(callback, bridgeResult);
//                }
//            };
//            BluetoothHelper.addCommand(UKitCmdSteeringGearAngleFeedback.newInstance(expectFeedback, false), warpCallback);

            int[] idArrays = new int[ids.length()];
            for (int i = 0; i < ids.length(); i++) {
                int id = ids.getInt(i);
                PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.SERVOS, id);
                idArrays[i] = id;
            }
            PeripheralErrorCollector.getInstance().setShielded(false);
            IUKitCommandResponse<URoUkitAngleFeedbackInfo> warpCallback = new IUKitCommandResponse<URoUkitAngleFeedbackInfo>() {
                @Override
                public void onUKitCommandResponse(URoCompletionResult<URoUkitAngleFeedbackInfo> result) {
                    BridgeResult bridgeResult;
                    if (result.isSuccess() && result.getData().getErrorIds().length == 0) {
                        bridgeResult = BridgeResult.SUCCESS();
                    } else {
                        bridgeResult = BridgeResult.FAIL();
                    }
                    URoAngleParam[] angles = result.getData().getAngles();
                    Arrays.sort(angles);
                    bridgeResult.data = angles;
                    bridgeResult.complete = BridgeBoolean.wrap(isSingle);
                    callResult(callback, bridgeResult);
                }
            };
            BluetoothHelper.addCommand(BtInvocationFactory.readbackServos(idArrays, false, 2000L, warpCallback));
        } catch (JSONException e) {
            callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
        } catch (Exception e) {
            callResult(callback, BridgeResult.FAIL());
        }
    }

    public void execMotionFrame(String args, final OnCallback callback) {
        try {
            /*
             * 用JSONObject解析，能发现是否有字段或是结构错误的情况。用Gson无法识别
             */
            JSONObject jsonObject = new JSONObject(args);
            int time = jsonObject.getInt("time");
            JSONArray angles = jsonObject.getJSONArray("angle");
            if (angles.length() == 0) {
                callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
                return;
            }
//            UKitCmdSteeringGearAngle.Builder builder = new UKitCmdSteeringGearAngle.Builder();
//            builder.setRotateTime(time);
//            for (int i = 0; i < angles.length(); i++) {
//                JSONObject angle = angles.getJSONObject(i);
//                int id = angle.getInt("id");
//                PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.PERIPHERAL_STEERING_GEAR, id);
//                builder.addParam(id, angle.getInt("degree"));
//            }
//            BluetoothHelper.addCommand(builder.build(), new IUKitCommandResponse<UKitDummyData>() {
//                @Override
//                public void onUKitCommandResponse(UKitCommandResponse<UKitDummyData> result) {
//                    callResult(callback, result.isSuccess() ? BridgeResult.SUCCESS() : BridgeResult.FAIL());
//                }
//            });

            ArrayMap<Integer, Integer> idAnglePairs = new ArrayMap<>();
            for (int i = 0; i < angles.length(); i++) {
                JSONObject angle = angles.getJSONObject(i);
                int id = angle.getInt("id");
                PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.SERVOS, id);
                idAnglePairs.put(id, angle.getInt("degree"));
            }
            BluetoothHelper.addCommand(BtInvocationFactory.turnServos(idAnglePairs, time, 0, new IUKitCommandResponse<Void>() {
                @Override
                public void onUKitCommandResponse(URoCompletionResult<Void> result) {
                    callResult(callback, result.isSuccess() ? BridgeResult.SUCCESS() : BridgeResult.FAIL());
                }
            }));
        } catch (JSONException e) {
            callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
        } catch (Exception e) {
            callResult(callback, BridgeResult.FAIL());
        }
    }

    public void stopAllActionSequence(final OnCallback callback) {
        cleanupAllInvocationSequence();
        MediaAudioPlayer.getInstance().stopAudio();
        BluetoothHelper.stopRobot();
        if (callback != null) {
            BridgeResult result = BridgeResult.SUCCESS();
            result.data = BridgeBoolean.TRUE();
            callResult(callback, result);
        }
    }

    public void uploadJoystickPython(String args, final OnCallback callback) {

        UKitBaseActivity activity = ActivityHelper.getResumeActivity();
        if (activity != null) {
            SendScriptFileDialogFragment dialogFragment = SendScriptFileDialogFragment.newBuilder()
                    .processingText(activity.getString(R.string.blockly_sending_script_file_title), activity.getString(R.string.blockly_sending_script_file_message))
                    .failureText(activity.getString(R.string.blockly_sending_script_file_failure_title), activity.getString(R.string.blockly_sending_script_file_failure_message), activity.getString(R.string.blockly_sending_script_file_ok))
                    .successText(activity.getString(R.string.blockly_sending_script_file_success_title), activity.getString(R.string.blockly_sending_script_file_success_message), activity.getString(R.string.blockly_sending_script_file_disconnect_bluetooth))
                    .successExtraText(activity.getString(R.string.blockly_sending_script_file_success_extra_message))
                    .successCancelBtnText(activity.getString(R.string.blockly_sending_script_file_close))
                    .build();
            dialogFragment.setConfirmBtnClickListener(new SendScriptFileDialogFragment.OnResultConfirmBtnClickListener() {
                @Override
                public void onConfirmBtnClick(boolean isSuccess) {
                    if (isSuccess) {
                        BluetoothHelper.disconnect();
                    }
                    dialogFragment.dismiss();
                }
            });
            uploadPython(args, callback, false, true, dialogFragment);
        }
    }

    public void uploadOfflinePython(String args, final OnCallback callback) {
        UKitBaseActivity activity = ActivityHelper.getResumeActivity();
        if (activity != null) {
            SendScriptFileDialogFragment dialogFragment = SendScriptFileDialogFragment.newBuilder()
                    .processingText(activity.getString(R.string.blockly_sending_script_file_title), activity.getString(R.string.blockly_sending_script_file_message))
                    .failureText(activity.getString(R.string.blockly_sending_script_file_failure_title), activity.getString(R.string.blockly_sending_script_file_failure_message), activity.getString(R.string.blockly_sending_script_file_ok))
                    .successText(activity.getString(R.string.blockly_sending_script_file_success_title), activity.getString(R.string.blockly_sending_script_file_success_message), activity.getString(R.string.blockly_online_sending_script_file_ok))
                    .successExtraText("")
                    .successCancelBtnText("")
                    .build();
            dialogFragment.setConfirmBtnClickListener(new SendScriptFileDialogFragment.OnResultConfirmBtnClickListener() {
                @Override
                public void onConfirmBtnClick(boolean isSuccess) {
                    dialogFragment.dismiss();
                }
            });
            uploadPython(args, callback, false, false, dialogFragment);
        }
    }

    private void uploadPython(String args, final OnCallback callback, boolean startAgain,boolean isJoystick,SendScriptFileDialogFragment dialogFragment) {
        SendScriptFileDialogFragment.SendScriptDialogUI ui = null;
        try {
            UKitBaseActivity activity = ActivityHelper.getResumeActivity();
            if (activity != null && dialogFragment != null) {
                ui = dialogFragment.getUI();
                dialogFragment.show(activity.getSupportFragmentManager(), "send_script");
            }
        } catch (Throwable e) {
            // do nothing
        }
        sendPython(args, callback, startAgain, false, isJoystick, ui);
    }

    public void startPython(String args, final OnCallback callback) {
        startPython(args, callback, false);
    }

    private void startPython(String args, final OnCallback callback, boolean startAgain) {
        SendScriptFileDialogFragment.SendScriptDialogUI ui = null;
        try {
            UKitBaseActivity activity = ActivityHelper.getResumeActivity();
            if (activity != null) {
                SendScriptFileDialogFragment dialogFragment = SendScriptFileDialogFragment.newBuilder()
                        .autoClose(true)
                        .processingText(activity.getString(R.string.blockly_online_sending_script_file_title), activity.getString(R.string.blockly_online_sending_script_file_message))
                        .failureText(activity.getString(R.string.blockly_online_sending_script_file_failure_title), "", activity.getString(R.string.blockly_online_sending_script_file_ok))
                        .build();
                ui = dialogFragment.getUI();
                dialogFragment.show(activity.getSupportFragmentManager(), "send_script");
            }
        } catch (Throwable e) {
            // do nothing
        }
        sendPython(args, callback, startAgain, true, false, ui);
    }

    private URoMissionAbortSignal mSendPythonSignal;

    public void cancelSendPython() {
        if (mSendPythonSignal != null && !mSendPythonSignal.isAbort()) {
            mSendPythonSignal.abort();
        }
    }

    private void sendPython(String args, final OnCallback callback, boolean startAgain, boolean execution,boolean isJoystick, SendScriptFileDialogFragment.SendScriptDialogUI ui) {
        try {
            /*
             * 用JSONObject解析，能发现是否有字段或是结构错误的情况。用Gson无法识别
             */
            if (!BluetoothHelper.isBluetoothConnected()) {
                if (ui != null) {
                    ui.updateUiStatus(SendScriptFileDialogFragment.Status.FAILURE);
                }
                return;
            }
            String tmpFileName;
            String fileName;
            if (execution) {
                fileName = "/ubt_blockly.py";
                tmpFileName = fileName;
            } else {
                if (isJoystick){
                    fileName = "/ubt_joystick.py";
                }else{
                    fileName = "/ubt_offline.py";
                }
                tmpFileName = fileName + ".tp";
            }
            JSONObject jsonObject = new JSONObject(args);
            String content = jsonObject.getString("content");
            if (!MicroPythonUtils.hasCommonPart()) {
                if (startAgain) {
                    //防止死循环
                    if (ui != null) {
                        ui.updateUiStatus(SendScriptFileDialogFragment.Status.FAILURE);
                    }
                    return;
                }
                MicroPythonUtils.loadCommonPart(new Runnable() {
                    @Override
                    public void run() {
                        sendPython(args, callback, true, execution, isJoystick,ui);
                    }
                });
                return;
            }
            HashSet<String> importList = new HashSet<>();
            String microPythonContent = MicroPythonUtils.conversionPython(importList, content);
            String mergedContent = MicroPythonUtils.mergedMicroPythonContent(importList, microPythonContent);
            if (TextUtils.isEmpty(mergedContent)) {
                if (ui != null) {
                    ui.updateUiStatus(SendScriptFileDialogFragment.Status.FAILURE);
                }
                return;
            }
            URoInvocationSequence sequence = new URoInvocationSequence();
            sequence.setCompletionCallback(new URoInvocationSequenceCallback() {
                @Override
                public void onComplete(URoCompletionResult result) {
                    BridgeResult bridgeResult = BridgeResult.SUCCESS();
                    bridgeResult.data = BridgeBoolean.wrap(result.isSuccess());
                    callResult(callback, bridgeResult);
                    if (ui != null) {
                        ui.updateUiStatus(result.isSuccess() ? SendScriptFileDialogFragment.Status.SUCCESS : SendScriptFileDialogFragment.Status.FAILURE);
                    }
                }

                @Override
                public void onMissionBegin() {
                    mSendPythonSignal = getAbortSignal();
                }

                @Override
                public void onMissionEnd() {
                    if (mSendPythonSignal != null && mSendPythonSignal.isAbort()) {
                        if (ui != null) {
                            ui.updateUiStatus(SendScriptFileDialogFragment.Status.CLOSE);
                        }
                        BridgeResult bridgeResult = BridgeResult.SUCCESS();
                        bridgeResult.data = BridgeBoolean.wrap(false);
                        callResult(callback, bridgeResult);
                    }
                    mSendPythonSignal = null;
                }
            });
            BluetoothHelper.addCommand(BtInvocationFactory.stopExecScript(null));
            UkitInvocation invocation = BtInvocationFactory.getFileStat(fileName, new IUKitCommandResponse<URoFileStat>() {
                @Override
                protected void onUKitCommandResponse(URoCompletionResult<URoFileStat> result) {
                    boolean needSendFile = true;
                    byte[] fileContent = mergedContent.getBytes();
                    if (result.isSuccess() && !TextUtils.isEmpty(result.getData().getMd5())) {
                        String fileMd5 = MD5Util.encodeMd5(fileContent);
                        if (TextUtils.equals(fileMd5.toLowerCase(), result.getData().getMd5().toLowerCase())) {
                            needSendFile = false;
                        }
                    }
                    if (needSendFile) {
                        sequence.action(BtInvocationFactory.sendFileData(fileContent, tmpFileName, new URoMissionCallback() {
                            @Override
                            public void onMissionNextStep(int currentStep, int totalStep) {
                            }

                            @Override
                            public void onProcessPercentChanged(int percent) {
                                if (ui != null) {
//                                  BtLogUtils.d("Percent: %d", percent);
                                    ui.updateUiPercent(percent);
                                }
                            }

                            @Override
                            public void onMissionBegin() {
                            }

                            @Override
                            public void onMissionEnd() {
                            }

                            @Override
                            public void onComplete(URoCompletionResult result) {
                            }
                        }));
                        sequence.sleep(500);
                    } else {
                        if (ui != null) {
                            ui.updateUiPercent(100);
                        }
                    }
                    if (execution) {
                        if(needSendFile && !TextUtils.equals(tmpFileName, fileName)) {
                            sequence.action(BtInvocationFactory.fileRename(tmpFileName, fileName, null));
                        }
                        sequence.action(BtInvocationFactory.stopExecScript(null));
                        sequence.action(BtInvocationFactory.startExecScript(fileName, new IUKitCommandResponse<Void>() {
                            @Override
                            protected void onUKitCommandResponse(URoCompletionResult<Void> result) {
                                if (result.isSuccess()){
                                    PyScriptRunningStateHolder.getInstance().setBlocklyScriptStarted();
                                }
                            }
                        }));
                    } else {
                        if(needSendFile && !TextUtils.equals(tmpFileName, fileName)) {
                            sequence.action(BtInvocationFactory.fileRename(tmpFileName, fileName, null));
                        }
                    }
                    BluetoothHelper.addCommand(sequence);
                }
            });
            invocation.setTimeoutThreshold(1000);
            BluetoothHelper.addCommand(invocation);
        } catch (JSONException e) {
            callResult(callback, BridgeResult.ILLEGAL_ARGUMENTS());
        } catch (Exception e) {
            callResult(callback, BridgeResult.FAIL());
        }
    }

    public void stopPython(String args,final OnCallback callback) {
        try {
            /*
             * 用JSONObject解析，能发现是否有字段或是结构错误的情况。用Gson无法识别
             */
            JSONObject jsonObject = new JSONObject(args);
            boolean skipStop = jsonObject.has("skipStop") && jsonObject.getBoolean("skipStop");//true为跳过调用停脚本，避免停掉运行中的离线脚本
            if (!skipStop) {//需要调用停止脚本
                URoInvocationSequence sequence = new URoInvocationSequence();
                sequence.setCompletionCallback(new IUKitCommandResponse<Void>() {
                    @Override
                    protected void onUKitCommandResponse(URoCompletionResult<Void> result) {
                        BridgeResult bridgeResult = BridgeResult.SUCCESS();
                        bridgeResult.data = BridgeBoolean.wrap(result.isSuccess());
                        callResult(callback, bridgeResult);
                    }
                });
                sequence.action(BtInvocationFactory.stopExecScript(null));
                sequence.action(BtInvocationFactory.stopRunning(null));
                BluetoothHelper.addCommand(sequence);
            } else {
                BridgeResult bridgeResult = BridgeResult.SUCCESS();
                bridgeResult.data = BridgeBoolean.wrap(true);
                callResult(callback, bridgeResult);
            }
        } catch (Exception e) {
            callResult(callback, BridgeResult.FAIL());
        }
    }

    public void playAudio(String args, final OnCallback callback) {
        try {
            /*
             * 用JSONObject解析，能发现是否有字段或是结构错误的情况。用Gson无法识别
             */
            JSONObject jsonObject = new JSONObject(args);
            String type = jsonObject.getString("type");
            String key = jsonObject.getString("key");
            int volume;
            //Todo 预留参数字段，当前blockly接口是没有传入volume参数的
            if (jsonObject.has("volume")) {
                volume = jsonObject.getInt("volume");
            } else {
                volume = UKitAudioVolume.MEDIUM.getVolume();
            }
            URoInvocationSequence sequence = new URoInvocationSequence();
            sequence.action(BtInvocationFactory.setAudioVolume(volume, null), true);
            sequence.action(BtInvocationFactory.startSoundPlay(0, type, key, new IUKitCommandResponse<Void>() {
                @Override
                public void onUKitCommandResponse(URoCompletionResult<Void> result) {
                    callResult(callback, result.isSuccess() ? BridgeResult.SUCCESS() : BridgeResult.FAIL());
                }
            }));
            BluetoothHelper.addCommand(sequence);
        } catch (Exception e) {
            callResult(callback, BridgeResult.FAIL());
        }
    }

    public void stopAudio() {
        try {
            BluetoothHelper.addCommand(BtInvocationFactory.stopAudioPlay(null));
        } catch (Exception e) {
            // do nothing
        }
    }

    public void setLEDStripColor(String args, final OnCallback callback) {
        try {
            JSONObject jsonObject = new JSONObject(args);
            int id = jsonObject.getInt("id");
            int port = jsonObject.getInt("port");
            int startIndex = jsonObject.getInt("startIndex");
            int endIndex = jsonObject.getInt("endIndex");
            int color = URoNumberConversionUtil.hex2IntegerE(jsonObject.getString("color").substring(1), 16) & 0xFFFFFF;
            boolean result = BluetoothHelper.addCommand(BtInvocationFactory.setLEDStripColorContinues(id, new int[]{port}, startIndex, endIndex, color<<8, new IUKitCommandResponse<Void>() {
                @Override
                protected void onUKitCommandResponse(URoCompletionResult<Void> result) {
                    callResult(callback, result.isSuccess() ? BridgeResult.SUCCESS() : BridgeResult.FAIL());
                }
            }));
            if (!result) {
                callResult(callback, BridgeResult.FAIL());
            }
        } catch (Exception e) {
            callResult(callback, BridgeResult.FAIL());
        }
    }

    public void setLEDStripExpression(String args, final OnCallback callback) {
        try {
            JSONObject jsonObject = new JSONObject(args);
            int id = jsonObject.getInt("id");
            int port = jsonObject.getInt("port");
            int type = jsonObject.getInt("type");
            int times = jsonObject.getInt("times");
            boolean result = BluetoothHelper.addCommand(BtInvocationFactory.setLEDStripExpression(id, port, type, times, 0, new IUKitCommandResponse<Void>() {
                @Override
                protected void onUKitCommandResponse(URoCompletionResult<Void> result) {
                    callResult(callback, result.isSuccess() ? BridgeResult.SUCCESS() : BridgeResult.FAIL());
                }
            }));
            if (!result) {
                callResult(callback, BridgeResult.FAIL());
            }
        } catch (Exception e) {
            callResult(callback, BridgeResult.FAIL());
        }
    }

    public void setLEDStripBrightness(String args, final OnCallback callback) {
        try {
            JSONObject jsonObject = new JSONObject(args);
            int id = jsonObject.getInt("id");
            int port = jsonObject.getInt("port");
            int brightness = jsonObject.getInt("brightness");
            boolean result = BluetoothHelper.addCommand(BtInvocationFactory.setLEDStripBrightness(id, port, brightness, new IUKitCommandResponse<Void>() {
                @Override
                protected void onUKitCommandResponse(URoCompletionResult<Void> result) {
                    callResult(callback, result.isSuccess() ? BridgeResult.SUCCESS() : BridgeResult.FAIL());
                }
            }));
            if (!result) {
                callResult(callback, BridgeResult.FAIL());
            }
        } catch (Exception e) {
            callResult(callback, BridgeResult.FAIL());
        }
    }

    public void moveLEDStrip(String args, final OnCallback callback) {
        try {
            JSONObject jsonObject = new JSONObject(args);
            int id = jsonObject.getInt("id");
            int port = jsonObject.getInt("port");
            int pixel = jsonObject.getInt("pixel");
            int times = jsonObject.getInt("times");
            boolean result = BluetoothHelper.addCommand(BtInvocationFactory.moveLEDStrip(id, new int[]{port}, pixel, times, new IUKitCommandResponse<Void>() {
                @Override
                protected void onUKitCommandResponse(URoCompletionResult<Void> result) {
                    callResult(callback, result.isSuccess() ? BridgeResult.SUCCESS() : BridgeResult.FAIL());
                }
            }));
            if (!result) {
                callResult(callback, BridgeResult.FAIL());
            }
        } catch (Exception e) {
            callResult(callback, BridgeResult.FAIL());
        }
    }

    public void closeLEDStrip(String args, final OnCallback callback) {
        try {
            JSONObject jsonObject = new JSONObject(args);
            int id = jsonObject.getInt("id");
            int port = jsonObject.getInt("port");
            boolean result = BluetoothHelper.addCommand(BtInvocationFactory.closeLEDStrip(id, new int[]{port}, new IUKitCommandResponse<Void>() {
                @Override
                protected void onUKitCommandResponse(URoCompletionResult<Void> result) {
                    callResult(callback, result.isSuccess() ? BridgeResult.SUCCESS() : BridgeResult.FAIL());
                }
            }));
            if (!result) {
                callResult(callback, BridgeResult.FAIL());
            }
        } catch (Exception e) {
            callResult(callback, BridgeResult.FAIL());
        }
    }

    private void addInvocationSequenceRecord(URoMissionAbortSignal actionSequence) {
        synchronized (mRunningInvocationSequences) {
            mRunningInvocationSequences.add(actionSequence);
        }
    }

    private void removeInvocationSequenceRecord(URoMissionAbortSignal actionSequence) {
        synchronized (mRunningInvocationSequences) {
            mRunningInvocationSequences.remove(actionSequence);
        }
    }

    public void cleanupAllInvocationSequence() {
        synchronized (mRunningInvocationSequences) {
            for (URoMissionAbortSignal actionSequence : mRunningInvocationSequences) {
                actionSequence.abort();
            }
            mRunningInvocationSequences.clear();
        }
    }

    private static void callResult(OnCallback callback, BridgeResult bridgeResult) {
        if (callback != null) {
            callback.onCallback(bridgeResult);
        }
    }

    private abstract class URoInvocationSequenceCallback extends URoMissionCallback {

        @Override
        public void onMissionNextStep(int currentStep, int totalStep) {

        }

        @Override
        public void onMissionBegin() {
            addInvocationSequenceRecord(getAbortSignal());
        }

        @Override
        public void onMissionEnd() {
            if (getAbortSignal() != null) {
                removeInvocationSequenceRecord(getAbortSignal());
            }
        }
    }
}
