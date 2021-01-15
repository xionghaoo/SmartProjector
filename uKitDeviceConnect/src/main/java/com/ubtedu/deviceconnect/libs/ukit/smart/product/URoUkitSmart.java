package com.ubtedu.deviceconnect.libs.ukit.smart.product;

import android.text.TextUtils;
import android.util.ArrayMap;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionCallbackHelper;
import com.ubtedu.deviceconnect.libs.base.URoCompletionConverterCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.component.URoComponent;
import com.ubtedu.deviceconnect.libs.base.component.URoSensorComponent;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocation;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocationSequence;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationNames;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationParamKeys;
import com.ubtedu.deviceconnect.libs.base.model.URoColor;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentID;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.model.URoRotateMotorCommand;
import com.ubtedu.deviceconnect.libs.base.model.URoSensorData;
import com.ubtedu.deviceconnect.libs.base.model.URoSubscribeSensorInfo;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommand;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommandConstants;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.ukit.smart.mission.URoFileCommandHelper;
import com.ubtedu.deviceconnect.libs.ukit.smart.mission.URoFileCommandHelper.FileCommandType;
import com.ubtedu.deviceconnect.libs.ukit.smart.mission.URoUkitSmartBoardInfoMission;
import com.ubtedu.deviceconnect.libs.ukit.smart.mission.URoUkitSmartColorCalibrationMission;
import com.ubtedu.deviceconnect.libs.ukit.smart.mission.URoUkitSmartRecvFileMission;
import com.ubtedu.deviceconnect.libs.ukit.smart.mission.URoUkitSmartSendDataMission;
import com.ubtedu.deviceconnect.libs.ukit.smart.mission.URoUkitSmartSendFileMission;
import com.ubtedu.deviceconnect.libs.ukit.smart.mission.URoUkitSmartUpgradeMission;
import com.ubtedu.deviceconnect.libs.ukit.smart.mission.URoUkitSmartVisionUpgradeMission;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoAudioRecord;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoFileStat;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoNetworkState;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoUkitSmartAngleFeedbackInfo;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoUkitSmartComponentMap;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoUkitSmartSpeakerInfoData;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiAuthMode;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiScanApInfo;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiStatusInfo;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.UroComplexItemResult;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileState;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevNetworkInfoGet;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevWifiConnectSet;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevWifiInfoGet;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevWifiListGet;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevWifiSsidSet;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbServoAngleGet;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbSpeakerGet;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceFileList;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceFileNum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public class URoUkitSmart extends URoProduct {

    public URoUkitSmart(String name, String productID) {
        super(name, productID);
    }

    private final static int MAX_SERVOS_ANGLE_VALUE = 238;
    private final static int MIN_SERVOS_ANGLE_VALUE = 2;
    private final static int BASE_SERVOS_ANGLE_VALUE = 120;

    private final static int MAX_SERVOS_SPEED_VALUE = 1000;
    private final static int MIN_SERVOS_SPEED_VALUE = -1000;

    private final static int MAX_MOTOR_SPEED_VALUE = 140;
    private final static int MIN_MOTOR_SPEED_VALUE = -140;

    private static final int COMMAND_FOR_MODE = 1;
    private static final int COMMAND_FOR_SUBSCRIBE_TIME = 2;
    private static final int COMMAND_FOR_SUBSCRIBE_THRESHOLD = 3;
    private static final int COMMAND_FOR_SUBSCRIBE_OFFSET = 4;

    public boolean getWiFiList(URoCompletionCallback<URoWiFiScanApInfo[]> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_GET_WIFI_LIST);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean setWiFiConfig(URoWiFiScanApInfo apInfo, String password, URoCompletionCallback<URoWiFiStatusInfo> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_SET_WIFI_CONFIG);
        invocation.setParameter("scanApInfo", apInfo);
        invocation.setParameter("password", password);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean getWiFiState(URoCompletionCallback<URoWiFiStatusInfo> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_GET_WIFI_STATE);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean getNetworkState(URoCompletionCallback<URoNetworkState> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_GET_NETWORK_STATE);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean startExecScript(String filePath, URoCompletionCallback<Void> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_START_EXEC_SCRIPT);
        invocation.setParameter("file_name", filePath);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean stopExecScript(URoCompletionCallback<Void> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_STOP_EXEC_SCRIPT);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean updateScriptValue(HashMap<String, String> paramt, URoCompletionCallback<Void> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_UPDATE_SCRIPT_VALUE);
        invocation.setParameter("paramt", paramt);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean cleanupScriptValue(URoCompletionCallback<Void> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_CLEANUP_SCRIPT_VALUE);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean setWiFiConnectEnable(boolean enabled, URoCompletionCallback<Void> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_SET_WIFI_CONNECT_ENABLE);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ENABLE, enabled);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean subscribeSensor(URoSubscribeSensorInfo subscribeInfo, URoCompletionCallback<Void> callback) {
        return subscribeSensors(new URoSubscribeSensorInfo[]{subscribeInfo}, callback);
    }

    public boolean subscribeSensors(URoSubscribeSensorInfo[] subscribeInfos, URoCompletionCallback<Void> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_SENSOR_SUBSCRIBE);
        invocation.setParameter("subscribeInfos", subscribeInfos);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean reboot(URoCompletionCallback<Void> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_REBOOT);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean resetStateSet(URoCompletionCallback<Void> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_CMD_RESET_STATE_SET);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    private URoError modifyId(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_TYPE)
                || !invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_SRC_ID)
                || !invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_NEW_ID)) {
            return URoError.INVALID;
        }
        URoComponentType componentType = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_TYPE, null);
        int srcId = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_SRC_ID, Integer.MIN_VALUE);
        int newId =invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_NEW_ID, Integer.MIN_VALUE);
        if(componentType == null || srcId == Integer.MIN_VALUE || newId == Integer.MIN_VALUE) {
            return URoError.INVALID;
        }
        if(componentType.equals(URoComponentType.SPEAKER)) {
            return URoError.NOT_ALLOWED;
        }
        URoRequest request = null;
        if(componentType.equals(URoComponentType.SERVOS)) {
            if(srcId == newId || !checkServosId(srcId) || !checkServosId(newId)) {
                return URoError.INVALID;
            }
            request = new URoRequest(URoCommandConstants.CMD_SRV_ID_SET);
        } else if(componentType.equals(URoComponentType.INFRAREDSENSOR)
                || componentType.equals(URoComponentType.TOUCHSENSOR)
                || componentType.equals(URoComponentType.ULTRASOUNDSENSOR)
                || componentType.equals(URoComponentType.LED)
                || componentType.equals(URoComponentType.COLORSENSOR)
                || componentType.equals(URoComponentType.ENVIRONMENTSENSOR)
                || componentType.equals(URoComponentType.BRIGHTNESSSENSOR)
                || componentType.equals(URoComponentType.SOUNDSENSOR)
                || componentType.equals(URoComponentType.MOTOR)
                || componentType.equals(URoComponentType.SUCKER)
                || componentType.equals(URoComponentType.LED_BELT)) {
            if(srcId == newId || !checkOtherId(srcId) || !checkOtherId(newId)) {
                return URoError.INVALID;
            }
            if(componentType.equals(URoComponentType.INFRAREDSENSOR)) {
                request = new URoRequest(URoCommandConstants.CMD_IR_ID_SET);
            } else if(componentType.equals(URoComponentType.TOUCHSENSOR)) {
                request = new URoRequest(URoCommandConstants.CMD_TCH_ID_SET);
            } else if(componentType.equals(URoComponentType.ULTRASOUNDSENSOR)) {
                request = new URoRequest(URoCommandConstants.CMD_ULT_ID_SET);
            } else if(componentType.equals(URoComponentType.LED)) {
                request = new URoRequest(URoCommandConstants.CMD_LED_ID_SET);
            } else if(componentType.equals(URoComponentType.COLORSENSOR)) {
                request = new URoRequest(URoCommandConstants.CMD_CLR_ID_SET);
            } else if(componentType.equals(URoComponentType.ENVIRONMENTSENSOR)) {
                request = new URoRequest(URoCommandConstants.CMD_TH_ID_SET);
            } else if(componentType.equals(URoComponentType.BRIGHTNESSSENSOR)) {
                request = new URoRequest(URoCommandConstants.CMD_LGT_ID_SET);
            } else if(componentType.equals(URoComponentType.SOUNDSENSOR)) {
                request = new URoRequest(URoCommandConstants.CMD_SND_ID_SET);
            } else if(componentType.equals(URoComponentType.MOTOR)) {
                request = new URoRequest(URoCommandConstants.CMD_MTR_ID_SET);
            } else if(componentType.equals(URoComponentType.SUCKER)) {
                request = new URoRequest(URoCommandConstants.CMD_SUK_ID_SET);
            } else {
                request = new URoRequest(URoCommandConstants.CMD_LED_BELT_ID_SET);
            }
        }
        if(request != null) {
            request.setParameter("id", srcId);
            request.setParameter("new_id", newId);
            request.setTimeoutThreshold(5000L);
        }
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError turnServos(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_TIME)
                || !invocation.hasParameter(URoInvocationParamKeys.Servos.PARAM_KEY_TURN_ID_ANGLE_PAIR)) {
            return URoError.INVALID;
        }
        int time = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_TIME, Integer.MIN_VALUE);
        ArrayMap<Integer, Integer> idAnglePairs = invocation.getParameter(URoInvocationParamKeys.Servos.PARAM_KEY_TURN_ID_ANGLE_PAIR, null);
        if(idAnglePairs == null || idAnglePairs.isEmpty() || time == Integer.MIN_VALUE) {
            return URoError.INVALID;
        }
        idAnglePairs = new ArrayMap<>(idAnglePairs);
        for(Map.Entry<Integer, Integer> entry : idAnglePairs.entrySet()) {
            Integer id = entry.getKey();
            Integer angle = entry.getValue();
            if(id == null || angle == null) {
                return URoError.INVALID;
            }
            if(!checkServosId(id)) {
                return URoError.INVALID;
            }
            entry.setValue(restrictRange(angle + BASE_SERVOS_ANGLE_VALUE, MIN_SERVOS_ANGLE_VALUE, MAX_SERVOS_ANGLE_VALUE));
        }
        URoInvocationSequence sequence = new URoInvocationSequence();
        sequence.setCompletionCallback(invocation.getCompletionCallback());
        for(Map.Entry<Integer, Integer> entry : idAnglePairs.entrySet()) {
            Integer id = entry.getKey();
            Integer angle = entry.getValue();
            URoInvocation request = new URoInvocation(URoInvocationNames.INVOCATION_CMD_SRV_ANGLE_SET);
            request.setParameter("id", id);
            request.setParameter("tar_angle", angle * 10);
            request.setParameter("rotation_time", restrictRange(time, 0, 20 * 0xFF));
            request.setParameter("shielding_time", restrictRange(0, 0, 0xFFFF));
            request.setParameter("mode", 0);
            sequence.action(request);
        }
        return invokeSequence(sequence, true);
//        return invokeSequence(sequence, false);
    }

    private URoError rotateServos(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if (!invocation.hasParameter(URoInvocationParamKeys.Servos.PARAM_KEY_ROTATE_ID_SPEED_PAIR)) {
            if(!invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY)
                    || !invocation.hasParameter(URoInvocationParamKeys.Servos.PARAM_KEY_ROTATE_SPEED)) {
                return URoError.INVALID;
            }
            int[] ids = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, null);
            int speed = invocation.getParameter(URoInvocationParamKeys.Servos.PARAM_KEY_ROTATE_SPEED, Integer.MIN_VALUE);
            if(ids == null || ids.length == 0 || speed == Integer.MIN_VALUE) {
                return URoError.INVALID;
            }
            for(int id : ids) {
                if(!checkServosId(id)) {
                    return URoError.INVALID;
                }
            }
            URoInvocationSequence sequence = new URoInvocationSequence();
            sequence.setCompletionCallback(invocation.getCompletionCallback());
            for(int id : ids) {
                URoInvocation request = new URoInvocation(URoInvocationNames.INVOCATION_CMD_SRV_PWM_SET);
                request.setParameter("id", id);
                request.setParameter("pwm", restrictRange(speed, MIN_SERVOS_SPEED_VALUE, MAX_SERVOS_SPEED_VALUE));
                sequence.action(request);
            }
            return invokeSequence(sequence, true);
        }else {
            ArrayMap<Integer, Integer> idSpeedPairs = invocation.getParameter(URoInvocationParamKeys.Servos.PARAM_KEY_ROTATE_ID_SPEED_PAIR, null);
            if (idSpeedPairs == null || idSpeedPairs.size() == 0) {
                return URoError.INVALID;
            }
            for (Integer id : idSpeedPairs.keySet()) {
                if (id == null || !checkServosId(id) || idSpeedPairs.get(id) == null) {
                    return URoError.INVALID;
                }
            }
            URoInvocationSequence sequence = new URoInvocationSequence();
            sequence.setCompletionCallback(invocation.getCompletionCallback());
            for (int id : idSpeedPairs.keySet()) {
                URoInvocation request = new URoInvocation(URoInvocationNames.INVOCATION_CMD_SRV_PWM_SET);
                request.setParameter("id", id);
                request.setParameter("pwm", restrictRange(idSpeedPairs.get(id), MIN_SERVOS_SPEED_VALUE, MAX_SERVOS_SPEED_VALUE));
                sequence.action(request);
            }
            return invokeSequence(sequence, true);
        }
    }

    private URoError rotateBatchServos(URoInvocation invocation) {
        if (invocation == null) {
            return URoError.INVALID;
        }
        if (!invocation.hasParameter(URoInvocationParamKeys.Servos.PARAM_KEY_ROTATE_ID_SPEED_PAIR)) {
            return URoError.INVALID;
        }
        ArrayMap<Integer, Integer> idSpeedPairs = invocation.getParameter(URoInvocationParamKeys.Servos.PARAM_KEY_ROTATE_ID_SPEED_PAIR, null);
        if (idSpeedPairs == null || idSpeedPairs.size() == 0) {
            return URoError.INVALID;
        }
        for (Integer id : idSpeedPairs.keySet()) {
            if (id == null || !checkServosId(id) || idSpeedPairs.get(id) == null) {
                return URoError.INVALID;
            }
        }
        ArrayList<URoRequest> requests = new ArrayList<>();
        for (int id : idSpeedPairs.keySet()) {
            URoRequest request = new URoRequest(URoCommandConstants.CMD_SRV_PWM_SET);
            request.setParameter("id", id);
            request.setParameter("pwm", restrictRange(idSpeedPairs.get(id), MIN_SERVOS_SPEED_VALUE, MAX_SERVOS_SPEED_VALUE));
            requests.add(request);
        }
        return functionIntelligentDevComplexSet(requests, new URoCompletionConverterCallback<List<UroComplexItemResult<Void>>, Void>(invocation.getCompletionCallback(), true) {
            @Override
            public Void convert(List<UroComplexItemResult<Void>> responseList) throws Throwable {
                for (int i = 0; i < responseList.size(); i++) {
                    UroComplexItemResult<Void> item = responseList.get(i);
                    if (!item.isSuccess()) {
                        sendComponentError(URoComponentType.SERVOS, item.getId(), URoError.CTRL_ERROR);
                    }
                }
                return null;
            }
        });
    }
    private URoRequest getModeRequestByComponentType(URoComponentType type){
        URoRequest request=null;
        if (type.equals(URoComponentType.INFRAREDSENSOR)) {
            request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_IR_SET);
        } else if (type.equals(URoComponentType.TOUCHSENSOR)) {
            request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_TCH_SET);
        } else if (type.equals(URoComponentType.ULTRASOUNDSENSOR)) {
            request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_ULT_SET);
        } else if (type.equals(URoComponentType.COLORSENSOR)) {
            request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_CLR_SET);
        } else if (type.equals(URoComponentType.ENVIRONMENTSENSOR)) {
            request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_TH_SET);
        } else if (type.equals(URoComponentType.BRIGHTNESSSENSOR)) {
            request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_LGT_SET);
        } else if (type.equals(URoComponentType.SOUNDSENSOR)) {
            request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_SND_SET);
        }
        return request;
    }

    private String getInvocationNameByComponentType(URoComponentType type, int cmdForWhat) {
        String invocationName = null;
        if(cmdForWhat == COMMAND_FOR_MODE) {
            if (type.equals(URoComponentType.INFRAREDSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_WORK_MODE_IR_SET;
            } else if (type.equals(URoComponentType.TOUCHSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_WORK_MODE_TCH_SET;
            } else if (type.equals(URoComponentType.ULTRASOUNDSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_WORK_MODE_ULT_SET;
            } else if (type.equals(URoComponentType.COLORSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_WORK_MODE_CLR_SET;
            } else if (type.equals(URoComponentType.ENVIRONMENTSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_WORK_MODE_TH_SET;
            } else if (type.equals(URoComponentType.BRIGHTNESSSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_WORK_MODE_LGT_SET;
            } else if (type.equals(URoComponentType.SOUNDSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_WORK_MODE_SND_SET;
            }
        } else if(cmdForWhat == COMMAND_FOR_SUBSCRIBE_TIME) {
            if (type.equals(URoComponentType.INFRAREDSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_UPLOAD_TIME_IR_SET;
            } else if (type.equals(URoComponentType.TOUCHSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_UPLOAD_TIME_TCH_SET;
            } else if (type.equals(URoComponentType.ULTRASOUNDSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_UPLOAD_TIME_ULT_SET;
            } else if (type.equals(URoComponentType.COLORSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_UPLOAD_TIME_CLR_SET;
            } else if (type.equals(URoComponentType.ENVIRONMENTSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_UPLOAD_TIME_TH_SET;
            } else if (type.equals(URoComponentType.BRIGHTNESSSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_UPLOAD_TIME_LGT_SET;
            } else if (type.equals(URoComponentType.SOUNDSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_UPLOAD_TIME_SND_SET;
            }
        } else if(cmdForWhat == COMMAND_FOR_SUBSCRIBE_THRESHOLD) {
            if (type.equals(URoComponentType.INFRAREDSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_UPLOAD_THRESHOLD_IR_SET;
            } else if (type.equals(URoComponentType.TOUCHSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_UPLOAD_THRESHOLD_TCH_SET;
            } else if (type.equals(URoComponentType.ULTRASOUNDSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_UPLOAD_THRESHOLD_ULT_SET;
            } else if (type.equals(URoComponentType.COLORSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_UPLOAD_THRESHOLD_CLR_SET;
            } else if (type.equals(URoComponentType.ENVIRONMENTSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_UPLOAD_THRESHOLD_TH_SET;
            } else if (type.equals(URoComponentType.BRIGHTNESSSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_UPLOAD_THRESHOLD_LGT_SET;
            } else if (type.equals(URoComponentType.SOUNDSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_UPLOAD_THRESHOLD_SND_SET;
            }
        } else if(cmdForWhat == COMMAND_FOR_SUBSCRIBE_OFFSET) {
            if (type.equals(URoComponentType.INFRAREDSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_UPLOAD_OFFSET_IR_SET;
            } else if (type.equals(URoComponentType.TOUCHSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_UPLOAD_OFFSET_TCH_SET;
            } else if (type.equals(URoComponentType.ULTRASOUNDSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_UPLOAD_OFFSET_ULT_SET;
            } else if (type.equals(URoComponentType.COLORSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_UPLOAD_OFFSET_CLR_SET;
            } else if (type.equals(URoComponentType.ENVIRONMENTSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_UPLOAD_OFFSET_TH_SET;
            } else if (type.equals(URoComponentType.BRIGHTNESSSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_UPLOAD_OFFSET_LGT_SET;
            } else if (type.equals(URoComponentType.SOUNDSENSOR)) {
                invocationName = URoInvocationNames.INVOCATION_CMD_UPLOAD_OFFSET_SND_SET;
            }
        }
        return invocationName;
    }

    private URoError subscribeSensor(URoInvocation invocation) {
        if (invocation == null) {
            return URoError.INVALID;
        }
        if (!invocation.hasParameter("subscribeInfos")) {
            return URoError.INVALID;
        }
        URoSubscribeSensorInfo[] subscribeInfos = invocation.getParameter("subscribeInfos", null);
        if (subscribeInfos == null) {
            return URoError.INVALID;
        }
        HashSet<URoSubscribeSensorInfo> subscribeInfoSet = new HashSet<>();
        for (URoSubscribeSensorInfo subscribeInfo : subscribeInfos) {
            if (subscribeInfo == null || !subscribeInfo.isValid()) {
                return URoError.INVALID;
            }
            subscribeInfoSet.remove(subscribeInfo);
            subscribeInfoSet.add(subscribeInfo);
        }
        ArrayList<URoRequest> requestArrayList = new ArrayList<>();
        for (URoSubscribeSensorInfo subscribeInfo : subscribeInfoSet) {
            URoRequest request = getModeRequestByComponentType(subscribeInfo.getId().getComponentType());
            if (request == null) {
                return URoError.INVALID;
            }
            request.setParameter("id", subscribeInfo.getId().getId());
            request.setParameter("work_mode", subscribeInfo.getMode().getMode());
            if (URoSubscribeSensorInfo.URoSubscribeSensorMode.TIME.equals(subscribeInfo.getMode())) {
                request.setParameter("time", ((int)(subscribeInfo.getValues()[0]/100))*100);
            } else if (URoSubscribeSensorInfo.URoSubscribeSensorMode.THRESHOLD.equals(subscribeInfo.getMode())) {
                request.setParameter("thresholds", subscribeInfo.getValues());
            } else if (URoSubscribeSensorInfo.URoSubscribeSensorMode.OFFSET.equals(subscribeInfo.getMode())) {
                request.setParameter("offsets", subscribeInfo.getValues());
            }
            requestArrayList.add(request);
        }
        return devComplexSetWithSimpleResult(requestArrayList, invocation.getCompletionCallback());
    }

    private URoError readbackServos(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID)
                || !invocation.hasParameter(URoInvocationParamKeys.Servos.PARAM_KEY_READBACK_POWEROFF)) {
            return URoError.INVALID;
        }
        int id = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID, Integer.MIN_VALUE);
        boolean powerOff = invocation.getParameter(URoInvocationParamKeys.Servos.PARAM_KEY_READBACK_POWEROFF, false);
        if(!checkServosId(id)) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SRV_ANGLE_GET);
        request.setParameter("id", id);
        request.setParameter("pwr", powerOff);
        return sendRequest(request, new URoCompletionConverterCallback<PbServoAngleGet.ServoAngleGetResponse, Integer>(invocation.getCompletionCallback()) {
            @Override
            public Integer convert(PbServoAngleGet.ServoAngleGetResponse source) throws Throwable {
                return (source.getCurAngle() / 10) - BASE_SERVOS_ANGLE_VALUE;
            }
        });
    }

    private URoError readbackBatchServos(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY)
                || !invocation.hasParameter(URoInvocationParamKeys.Servos.PARAM_KEY_READBACK_POWEROFF)) {
            return URoError.INVALID;
        }
        int[] ids = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, null);
        boolean powerOff = invocation.getParameter(URoInvocationParamKeys.Servos.PARAM_KEY_READBACK_POWEROFF, false);
        if(ids == null || ids.length == 0) {
            return URoError.INVALID;
        }
        for(int id : ids) {
            if (!checkServosId(id)) {
                return URoError.INVALID;
            }
        }
        URoUkitSmartAngleFeedbackInfo feedbackInfo = new URoUkitSmartAngleFeedbackInfo(ids);
        URoInvocationSequence sequence = new URoInvocationSequence();
        sequence.setCompletionCallback(new URoCompletionCallback() {
            @Override
            public void onComplete(URoCompletionResult result) {
                if (isConnected() && invocation.isSendComponentError()) {
                    Integer[] errorIds = feedbackInfo.getErrorIds();
                    //URoLogUtils.e("errorIds: %s", Arrays.toString(errorIds));
                    for(Integer errorId : errorIds) {
                        sendComponentError(URoComponentType.SERVOS, errorId, URoError.READ_ERROR);
                    }
                }
                URoCompletionCallbackHelper.sendSuccessCallback(feedbackInfo, invocation.getCompletionCallback());
            }
        });
        for(int id : ids) {
            URoInvocation request = new URoInvocation(URoInvocationNames.INVOCATION_CMD_SRV_ANGLE_GET);
            request.setParameter("id", id);
            request.setParameter("pwr", powerOff);
            request.setTimeoutThreshold(invocation.timeoutThreshold());
            request.setCompletionCallback(new URoCompletionCallback<PbServoAngleGet.ServoAngleGetResponse>() {
                @Override
                public void onComplete(URoCompletionResult<PbServoAngleGet.ServoAngleGetResponse> result) {
                    if(result.isSuccess()) {
                        feedbackInfo.addServoAngle(id, (result.getData().getCurAngle() / 10) - BASE_SERVOS_ANGLE_VALUE, true);
                    } else {
                        feedbackInfo.addServoAngle(id, 0, false);
                    }
                }
            });
            sequence.action(request);
        }
        return invokeSequence(sequence, true);
//        return invokeSequence(sequence, false);
    }

    private URoError stopServos(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY)) {
            return URoError.INVALID;
        }
        int[] ids = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, null);
        if(ids == null || ids.length == 0) {
            return URoError.INVALID;
        }
        for(int id : ids) {
            if(!checkServosId(id)) {
                return URoError.INVALID;
            }
        }
        URoInvocationSequence sequence = new URoInvocationSequence();
        sequence.setCompletionCallback(invocation.getCompletionCallback());
        for(int id : ids) {
            URoInvocation request = new URoInvocation(URoInvocationNames.INVOCATION_CMD_SRV_STOP);
            request.setParameter("id", id);
            request.setParameter("mode", 0);
            sequence.action(request);
        }
        return invokeSequence(sequence, true);
//        return invokeSequence(sequence, false);
    }

    private URoError rotateMotor(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Motor.PARAM_KEY_ROTATE_COMMAND)) {
            return URoError.INVALID;
        }
        URoRotateMotorCommand[] commands = invocation.getParameter(URoInvocationParamKeys.Motor.PARAM_KEY_ROTATE_COMMAND, null);
        if(commands == null || commands.length == 0) {
            return URoError.INVALID;
        }
        commands = Arrays.copyOf(commands, commands.length);
        for(URoRotateMotorCommand command : commands) {
            if(!checkOtherId(command.getId())) {
                return URoError.INVALID;
            }
            command.setSpeed(restrictRange(command.getSpeed(), MIN_MOTOR_SPEED_VALUE, MAX_MOTOR_SPEED_VALUE));
            command.setTime(restrictRange(command.getTime(), 0, 6553500));
        }
        URoInvocationSequence sequence = new URoInvocationSequence();
        sequence.setCompletionCallback(invocation.getCompletionCallback());
        for(URoRotateMotorCommand command : commands) {
            URoInvocation request = new URoInvocation(URoInvocationNames.INVOCATION_CMD_MTR_SPEED_SET);
            request.setParameter("id", command.getId());
            request.setParameter("speed", command.getSpeed());
            request.setParameter("time", command.getTime());
            sequence.action(request);
        }
        return invokeSequence(sequence, true);
//        return invokeSequence(sequence, false);
    }

    private URoError rotateBatchMotor(URoInvocation invocation) {
        if (invocation == null) {
            return URoError.INVALID;
        }
        if (!invocation.hasParameter(URoInvocationParamKeys.Motor.PARAM_KEY_ROTATE_COMMAND)) {
            return URoError.INVALID;
        }
        URoRotateMotorCommand[] commands = invocation.getParameter(URoInvocationParamKeys.Motor.PARAM_KEY_ROTATE_COMMAND, null);
        if (commands == null || commands.length == 0) {
            return URoError.INVALID;
        }
        commands = Arrays.copyOf(commands, commands.length);
        for (URoRotateMotorCommand command : commands) {
            if (!checkOtherId(command.getId())) {
                return URoError.INVALID;
            }
            command.setSpeed(restrictRange(command.getSpeed(), MIN_MOTOR_SPEED_VALUE, MAX_MOTOR_SPEED_VALUE));
            command.setTime(restrictRange(command.getTime(), 0, 6553500));
        }
        ArrayList<URoRequest> requests = new ArrayList<>();
        for (URoRotateMotorCommand command : commands) {
            URoRequest request = new URoRequest(URoCommandConstants.CMD_MTR_SPEED_SET);
            request.setParameter("id", command.getId());
            request.setParameter("speed", command.getSpeed());
            request.setParameter("time", command.getTime());
            requests.add(request);
        }
        return functionIntelligentDevComplexSet(requests, new URoCompletionConverterCallback<List<UroComplexItemResult<Void>>, Void>(invocation.getCompletionCallback(), true) {
            @Override
            public Void convert(List<UroComplexItemResult<Void>> responseList) throws Throwable {
                for (int i = 0; i < responseList.size(); i++) {
                    UroComplexItemResult<Void> item = responseList.get(i);
                    if (!item.isSuccess()) {
                        sendComponentError(URoComponentType.MOTOR, item.getId(), URoError.CTRL_ERROR);
                    }
                }
                return null;
            }
        });
    }

    private URoError stopMotor(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY)) {
            return URoError.INVALID;
        }
        int[] ids = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, null);
        if(ids == null || ids.length == 0) {
            return URoError.INVALID;
        }
        for(int id : ids) {
            if(!checkOtherId(id)) {
                return URoError.INVALID;
            }
        }
        URoInvocationSequence sequence = new URoInvocationSequence();
        sequence.setCompletionCallback(invocation.getCompletionCallback());
        for(int id : ids) {
            URoInvocation request = new URoInvocation(URoInvocationNames.INVOCATION_CMD_MTR_STOP);
            request.setParameter("id", id);
            sequence.action(request);
        }
        return invokeSequence(sequence, true);
//        return invokeSequence(sequence, false);
    }

    private URoError enableSensor(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID)
                || !invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_TYPE)
                || !invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_ENABLE)) {
            return URoError.INVALID;
        }
        int componentId = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID, Integer.MIN_VALUE);
        URoComponentType componentType = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_TYPE, null);
        boolean enabled = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ENABLE, false);
        if(!checkOtherId(componentId) || componentType == null) {
            return URoError.INVALID;
        }
        Integer sensorType = URoUkitSmartComponentMap.getExecutionValue(componentType);
        if(sensorType == null) {
            return URoError.INVALID;
        }
        URoCommand cmd;
        if(componentType.equals(URoComponentType.SERVOS)) {
            cmd = enabled ? URoCommandConstants.CMD_SRV_ON : URoCommandConstants.CMD_SRV_OFF;
        } else if(componentType.equals(URoComponentType.INFRAREDSENSOR)) {
            cmd = enabled ? URoCommandConstants.CMD_IR_ON : URoCommandConstants.CMD_IR_OFF;
        } else if(componentType.equals(URoComponentType.TOUCHSENSOR)) {
            cmd = enabled ? URoCommandConstants.CMD_TCH_ON : URoCommandConstants.CMD_TCH_OFF;
        } else if(componentType.equals(URoComponentType.ULTRASOUNDSENSOR)) {
            cmd = enabled ? URoCommandConstants.CMD_ULT_ON : URoCommandConstants.CMD_ULT_OFF;
        } else if(componentType.equals(URoComponentType.LED)) {
            cmd = enabled ? URoCommandConstants.CMD_LED_ON : URoCommandConstants.CMD_LED_OFF;
        } else if(componentType.equals(URoComponentType.COLORSENSOR)) {
            cmd = enabled ? URoCommandConstants.CMD_CLR_ON : URoCommandConstants.CMD_CLR_OFF;
        } else if(componentType.equals(URoComponentType.ENVIRONMENTSENSOR)) {
            cmd = enabled ? URoCommandConstants.CMD_TH_ON : URoCommandConstants.CMD_TH_OFF;
        } else if(componentType.equals(URoComponentType.BRIGHTNESSSENSOR)) {
            cmd = enabled ? URoCommandConstants.CMD_LGT_ON : URoCommandConstants.CMD_LGT_OFF;
        } else if(componentType.equals(URoComponentType.SOUNDSENSOR)) {
            cmd = enabled ? URoCommandConstants.CMD_SND_ON : URoCommandConstants.CMD_SND_OFF;
        } else if(componentType.equals(URoComponentType.MOTOR)) {
            cmd = enabled ? URoCommandConstants.CMD_MTR_ON : URoCommandConstants.CMD_MTR_OFF;
        } else if(componentType.equals(URoComponentType.SUCKER)) {
            cmd = enabled ? URoCommandConstants.CMD_SUK_ON : URoCommandConstants.CMD_SUK_OFF;
        } else if(componentType.equals(URoComponentType.LCD)) {
            cmd = enabled ? URoCommandConstants.CMD_LCD_ON : URoCommandConstants.CMD_LCD_OFF;
        } else {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(cmd);
        request.setParameter("id", componentId);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError checkComponent(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID)) {
            return URoError.INVALID;
        }
        URoComponentID componentId = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID, null);
        if(componentId == null) {
            return URoError.INVALID;
        }
        URoComponentType componentType = componentId.getComponentType();
        if(URoComponentType.SERVOS.equals(componentType)) {
            if(!checkServosId(componentId.getId())) {
                return URoError.INVALID;
            }
        } else {
            if (!checkOtherId(componentId.getId())) {
                return URoError.INVALID;
            }
        }
        URoCommand cmd;
        if(componentType.equals(URoComponentType.SERVOS)) {
            cmd = URoCommandConstants.CMD_SRV_STOP;
        } else if(componentType.equals(URoComponentType.INFRAREDSENSOR)) {
            cmd = URoCommandConstants.CMD_IR_ON;
        } else if(componentType.equals(URoComponentType.TOUCHSENSOR)) {
            cmd = URoCommandConstants.CMD_TCH_ON;
        } else if(componentType.equals(URoComponentType.ULTRASOUNDSENSOR)) {
            cmd = URoCommandConstants.CMD_ULT_ON;
        } else if(componentType.equals(URoComponentType.LED)) {
            cmd = URoCommandConstants.CMD_LED_ON;
        } else if(componentType.equals(URoComponentType.COLORSENSOR)) {
            cmd = URoCommandConstants.CMD_CLR_ON;
        } else if(componentType.equals(URoComponentType.ENVIRONMENTSENSOR)) {
            cmd = URoCommandConstants.CMD_TH_ON;
        } else if(componentType.equals(URoComponentType.BRIGHTNESSSENSOR)) {
            cmd = URoCommandConstants.CMD_LGT_ON;
        } else if(componentType.equals(URoComponentType.SOUNDSENSOR)) {
            cmd = URoCommandConstants.CMD_SND_ON;
        } else if(componentType.equals(URoComponentType.MOTOR)) {
            cmd = URoCommandConstants.CMD_MTR_STOP;
        } else if(componentType.equals(URoComponentType.SUCKER)) {
            cmd = URoCommandConstants.CMD_SUK_ON;
        } else if(componentType.equals(URoComponentType.LED_BELT)) {
            cmd = URoCommandConstants.CMD_LED_BELT_RESET;
        } else if(componentType.equals(URoComponentType.LCD)) {
            cmd = URoCommandConstants.CMD_LCD_ON;
        } else {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(cmd);
        request.setParameter("id", componentId.getId());
        if(componentType.equals(URoComponentType.SERVOS)) {
            request.setParameter("mode", 0);
        }
        return sendRequest(request, new URoCompletionConverterCallback<Object, Boolean>(invocation.getCompletionCallback()) {
            @Override
            public Boolean convert(Object source) throws Throwable {
                return true;
            }
        });
    }

    private URoError motorFaultClear(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_FAULT_CLEAR);
        request.setParameter("dev", URoUkitSmartComponentMap.getExecutionValue(URoComponentType.MOTOR));
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError servosFaultClear(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_FAULT_CLEAR);
        request.setParameter("dev", URoUkitSmartComponentMap.getExecutionValue(URoComponentType.SERVOS));
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError devFaultClear(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_FAULT_CLEAR);
        request.setParameter("dev", 0xFF);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError readSensor(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID)) {
            return URoError.INVALID;
        }
        URoComponentID componentId = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID, null);
        if(componentId == null) {
            return URoError.INVALID;
        }
        URoComponentType componentType = componentId.getComponentType();
        if(!checkOtherId(componentId.getId()) || !(URoComponentType.INFRAREDSENSOR.equals(componentType)
                || URoComponentType.TOUCHSENSOR.equals(componentType)
                || URoComponentType.ULTRASOUNDSENSOR.equals(componentType)
                || URoComponentType.COLORSENSOR.equals(componentType)
                || URoComponentType.ENVIRONMENTSENSOR.equals(componentType)
                || URoComponentType.BRIGHTNESSSENSOR.equals(componentType)
                || URoComponentType.SOUNDSENSOR.equals(componentType))) {
            return URoError.INVALID;
        }
        URoCommand cmd;
        if(componentType.equals(URoComponentType.INFRAREDSENSOR)) {
            cmd = URoCommandConstants.CMD_IR_DIS_GET;
        } else if(componentType.equals(URoComponentType.TOUCHSENSOR)) {
            cmd = URoCommandConstants.CMD_TCH_TYPE_GET;
        } else if(componentType.equals(URoComponentType.ULTRASOUNDSENSOR)) {
            cmd = URoCommandConstants.CMD_ULT_DIS_GET;
        } else if(componentType.equals(URoComponentType.COLORSENSOR)) {
            cmd = URoCommandConstants.CMD_CLR_RGB_GET;
        } else if(componentType.equals(URoComponentType.ENVIRONMENTSENSOR)) {
            cmd = URoCommandConstants.CMD_TH_GET;
        } else if(componentType.equals(URoComponentType.BRIGHTNESSSENSOR)) {
            cmd = URoCommandConstants.CMD_LGT_VALUE_GET;
        } else {
            cmd = URoCommandConstants.CMD_SND_ADC_VALUE_GET;
        }
        URoRequest request = new URoRequest(cmd);
        request.setParameter("id", componentId.getId());
        URoError error = sendRequest(request, null);
        if(!error.isSuccess()) {
            URoCompletionCallbackHelper.sendErrorCallback(error, invocation.getCompletionCallback());
            return error;
        }
        URoComponent component = getComponent(componentId);
        if(component instanceof URoSensorComponent) {
            URoCompletionCallbackHelper.sendCallback(new URoCompletionResult(((URoSensorComponent) component).getSensorValue(), error), invocation.getCompletionCallback());
        } else {
            URoCompletionCallbackHelper.sendErrorCallback(URoError.UNKNOWN, invocation.getCompletionCallback());
        }
        return URoError.SUCCESS;
    }

    private URoError readBatchSensor(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY)) {
            return URoError.INVALID;
        }
        Collection<URoComponentID> idList = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, null);
        if(idList == null || idList.isEmpty()) {
            return URoError.INVALID;
        }
        for(URoComponentID componentId : idList) {
            if (!checkOtherId(componentId.getId()) || !(URoComponentType.INFRAREDSENSOR.equals(componentId.getComponentType())
                    || URoComponentType.TOUCHSENSOR.equals(componentId.getComponentType())
                    || URoComponentType.ULTRASOUNDSENSOR.equals(componentId.getComponentType())
                    || URoComponentType.COLORSENSOR.equals(componentId.getComponentType())
                    || URoComponentType.ENVIRONMENTSENSOR.equals(componentId.getComponentType())
                    || URoComponentType.BRIGHTNESSSENSOR.equals(componentId.getComponentType())
                    || URoComponentType.SOUNDSENSOR.equals(componentId.getComponentType()))) {
                return URoError.INVALID;
            }
        }
        boolean hasError = false;
        for(URoComponentID componentId : idList) {
            URoComponentType componentType = componentId.getComponentType();
            URoCommand cmd;
            if(componentType.equals(URoComponentType.INFRAREDSENSOR)) {
                cmd = URoCommandConstants.CMD_IR_DIS_GET;
            } else if(componentType.equals(URoComponentType.TOUCHSENSOR)) {
                cmd = URoCommandConstants.CMD_TCH_TYPE_GET;
            } else if(componentType.equals(URoComponentType.ULTRASOUNDSENSOR)) {
                cmd = URoCommandConstants.CMD_ULT_DIS_GET;
            } else if(componentType.equals(URoComponentType.COLORSENSOR)) {
                cmd = URoCommandConstants.CMD_CLR_RGB_GET;
            } else if(componentType.equals(URoComponentType.ENVIRONMENTSENSOR)) {
                cmd = URoCommandConstants.CMD_TH_GET;
            } else if(componentType.equals(URoComponentType.BRIGHTNESSSENSOR)) {
                cmd = URoCommandConstants.CMD_LGT_VALUE_GET;
            } else {
                cmd = URoCommandConstants.CMD_SND_ADC_VALUE_GET;
            }
            URoRequest request = new URoRequest(cmd);
            request.setParameter("id", componentId.getId());
            URoError error = sendRequest(request, null);
            if(!error.isSuccess()) {
                hasError = true;
            }
        }
        URoError error = hasError ? URoError.UNKNOWN : URoError.SUCCESS;
        if(!error.isSuccess()) {
            URoCompletionCallbackHelper.sendErrorCallback(error, invocation.getCompletionCallback());
            return error;
        }
        HashMap<URoComponentID, Object> data = new HashMap<>();
        for(URoComponentID componentId : idList) {
            URoComponent component = getComponent(componentId);
            if(component instanceof URoSensorComponent) {
                data.put(componentId, ((URoSensorComponent) component).getSensorValue());
            } else {
                data.put(componentId, null);
            }
        }
        URoCompletionCallbackHelper.sendCallback(new URoCompletionResult(new URoSensorData(data), error), invocation.getCompletionCallback());
        return URoError.SUCCESS;
    }

    private URoError setUltrasoundColor(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY)
                || !invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_TIME)
                || !invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_COLOR)) {
            return URoError.INVALID;
        }
        int[] ids = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, null);
        int time = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_TIME, 0);
        URoColor color = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_COLOR, null);
        if(ids == null || ids.length == 0 || color == null) {
            return URoError.INVALID;
        }
        for(int id : ids) {
            if(!checkOtherId(id)) {
                return URoError.INVALID;
            }
        }
        int speed = 0;
        if(time < 0 || time >= 6553500) {
            time = 0xFFFFFFFF;
            speed = 0xFF;
        } else {
            time = restrictRange(time, 0, 6553499);
        }
        URoInvocationSequence sequence = new URoInvocationSequence();
        sequence.setCompletionCallback(invocation.getCompletionCallback());
        for(int id : ids) {
            URoInvocation request = new URoInvocation(URoInvocationNames.INVOCATION_CMD_ULT_LIGHT_SET);
            request.setParameter("id", id);
            request.setParameter("r", color.getRed());
            request.setParameter("g", color.getGreen());
            request.setParameter("b", color.getBlue());
            request.setParameter("mode", 1);
            request.setParameter("speed", speed);
            request.setParameter("time", time);
            sequence.action(request);
        }
        return invokeSequence(sequence, true);
//        return invokeSequence(sequence, false);
    }

    private URoError setLedColor(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY)
                || !invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_TIME)
                || !invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_COLOR)) {
            return URoError.INVALID;
        }
        int[] ids = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, null);
        long time = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_TIME, Long.MIN_VALUE);
        ArrayList<URoColor> colors = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_COLOR, null);
        if(ids == null || ids.length == 0 || colors == null || colors.size() != 8) {
            return URoError.INVALID;
        }
        for(int id : ids) {
            if(!checkOtherId(id)) {
                return URoError.INVALID;
            }
        }
        time = restrictRange(time, 0, 25500);
        if(time == 25500) {
            time = 0xFFFFFFFF;
        }
        URoInvocationSequence sequence = new URoInvocationSequence();
        sequence.setCompletionCallback(invocation.getCompletionCallback());
        for(int id : ids) {
            URoInvocation request = new URoInvocation(URoInvocationNames.INVOCATION_CMD_LED_EXP_SET);
            request.setParameter("id", id);
            request.setParameter("time", (int)time);
            for(int i = 0; i < 8; i++) {
                int rgb = colors.get(i).getColor();
                request.setParameter("rgbc" + (i + 1), (rgb << 8) & 0xFFFFFF00);
            }
            sequence.action(request);
        }
        return invokeSequence(sequence, true);
//        return invokeSequence(sequence, false);
    }

    private URoError showLedEffects(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY)
                || !invocation.hasParameter(URoInvocationParamKeys.Sensor.PARAM_KEY_TIMES)
                || !invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_COLOR)
                || !invocation.hasParameter(URoInvocationParamKeys.Sensor.PARAM_KEY_EFFECT_ID)) {
            return URoError.INVALID;
        }
        int[] ids = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, null);
        int effectID = invocation.getParameter(URoInvocationParamKeys.Sensor.PARAM_KEY_EFFECT_ID, Integer.MIN_VALUE);
        int times = invocation.getParameter(URoInvocationParamKeys.Sensor.PARAM_KEY_TIMES, Integer.MIN_VALUE);
        URoColor color = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_COLOR, null);
        if(ids == null || ids.length == 0 || color == null || effectID == Integer.MIN_VALUE || times == Integer.MIN_VALUE) {
            return URoError.INVALID;
        }
        for(int id : ids) {
            if(!checkOtherId(id)) {
                return URoError.INVALID;
            }
        }
        URoInvocationSequence sequence = new URoInvocationSequence();
        sequence.setCompletionCallback(invocation.getCompletionCallback());
        for(int id : ids) {
            URoInvocation request = new URoInvocation(URoInvocationNames.INVOCATION_CMD_LED_FIX_EXP_SET);
            request.setParameter("id", id);
            request.setParameter("expressions_type", effectID);
            request.setParameter("time", times);
            request.setParameter("rgbc", (color.getColor() << 8) & 0xFFFFFF00);
            sequence.action(request);
        }
        return invokeSequence(sequence, true);
//        return invokeSequence(sequence, false);
    }

    private URoError readSpeakerInfo(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SPK_GET);
        request.setParameter("id", 1);
        return sendRequest(request, new URoCompletionConverterCallback<PbSpeakerGet.SpeakerGetResponse, URoUkitSmartSpeakerInfoData>(invocation.getCompletionCallback()) {
            @Override
            public URoUkitSmartSpeakerInfoData convert(PbSpeakerGet.SpeakerGetResponse source) throws Throwable {
                return new URoUkitSmartSpeakerInfoData(source);
            }
        });
    }

    private URoError upgrade(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_URL)
                || !invocation.hasParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_MD5)
                || !invocation.hasParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_SIZE)) {
            return URoError.INVALID;
        }
        String url = invocation.getParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_URL, null);
        String md5 = invocation.getParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_MD5, null);
        int size = invocation.getParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_SIZE, 0);
        URoUkitSmartUpgradeMission mission = new URoUkitSmartUpgradeMission(this, url, md5, size, 0x2, 1);
        mission.setCallback(invocation.getCompletionCallback());
        mission.start();
        return URoError.SUCCESS;
    }

    private URoError upgradeComponent(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_URL)
                || !invocation.hasParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_MD5)
                || !invocation.hasParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_SIZE)
                || !invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_TYPE)
                || !invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY)) {
            return URoError.INVALID;
        }
        String url = invocation.getParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_URL, null);
        String md5 = invocation.getParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_MD5, null);
        int size = invocation.getParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_SIZE, 0);
        URoComponentType componentType = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_TYPE, null);
        int[] ids = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, null);
        Integer dev = URoUkitSmartComponentMap.getExecutionValue(componentType);
        if(dev == null) {
            return URoError.INVALID;
        }
        if(!URoComponentType.TOUCHSENSOR.equals(componentType)
                && !URoComponentType.INFRAREDSENSOR.equals(componentType)
                && !URoComponentType.LED.equals(componentType)
                && !URoComponentType.SPEAKER.equals(componentType)
                && !URoComponentType.SERVOS.equals(componentType)
                && !URoComponentType.LED_BELT.equals(componentType)
                && !URoComponentType.VISION.equals(componentType)) {
            return URoError.INVALID;
        }
        ArrayList<Integer> idArray = new ArrayList<>();
        for(int id : ids) {
            idArray.add(id);
        }
        URoUkitSmartUpgradeMission mission;
        if(URoComponentType.VISION.equals(componentType)) {
            String type = invocation.getParameter("subtype", "firmware");
            mission = new URoUkitSmartVisionUpgradeMission(this, url, md5, size, dev, idArray, type);
        } else {
            mission = new URoUkitSmartUpgradeMission(this, url, md5, size, dev, idArray);
        }
        mission.setCallback(invocation.getCompletionCallback());
        mission.start();
        return URoError.SUCCESS;
    }

    private URoError sendFile(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.File.PARAM_KEY_SOURCE)
                || !invocation.hasParameter(URoInvocationParamKeys.File.PARAM_KEY_TARGET)) {
            return URoError.INVALID;
        }
        String source = invocation.getParameter(URoInvocationParamKeys.File.PARAM_KEY_SOURCE, null);
        String target = invocation.getParameter(URoInvocationParamKeys.File.PARAM_KEY_TARGET, null);
        URoComponentType componentType = invocation.getParameter("componentType", null);
        URoUkitSmartSendFileMission mission = new URoUkitSmartSendFileMission(this, source, target, componentType);
        mission.setCallback(invocation.getCompletionCallback());
        mission.start();
        return URoError.SUCCESS;
    }

    private URoError sendFileData(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.File.PARAM_KEY_SOURCE_DATA)
                || !invocation.hasParameter(URoInvocationParamKeys.File.PARAM_KEY_TARGET)) {
            return URoError.INVALID;
        }
        byte[] sourceData = invocation.getParameter(URoInvocationParamKeys.File.PARAM_KEY_SOURCE_DATA, null);
        String target = invocation.getParameter(URoInvocationParamKeys.File.PARAM_KEY_TARGET, null);
        URoComponentType componentType = invocation.getParameter("componentType", null);
        URoUkitSmartSendDataMission mission = new URoUkitSmartSendDataMission(this, sourceData, target, componentType);
        mission.setCallback(invocation.getCompletionCallback());
        mission.start();
        return URoError.SUCCESS;
    }

    private URoError recvFile(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.File.PARAM_KEY_SOURCE)
                || !invocation.hasParameter(URoInvocationParamKeys.File.PARAM_KEY_TARGET)) {
            return URoError.INVALID;
        }
        String source = invocation.getParameter(URoInvocationParamKeys.File.PARAM_KEY_SOURCE, null);
        String target = invocation.getParameter(URoInvocationParamKeys.File.PARAM_KEY_TARGET, null);
        URoComponentType componentType = invocation.getParameter("componentType", null);
        URoUkitSmartRecvFileMission mission = new URoUkitSmartRecvFileMission(this, source, target, componentType);
        mission.setCallback(invocation.getCompletionCallback());
        mission.start();
        return URoError.SUCCESS;
    }

    private URoError renameFile(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter("old_name")
                || !invocation.hasParameter("new_name")) {
            return URoError.INVALID;
        }
        String oldPath = invocation.getParameter("old_name", null);
        String newPath = invocation.getParameter("new_name", null);
        if(TextUtils.isEmpty(oldPath) || TextUtils.isEmpty(newPath)) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(URoFileCommandHelper.FileCommandType.FILE_RENAME, invocation.getParameter("componentType", null)));
        request.setParameter("old_name", oldPath);
        request.setParameter("new_name", newPath);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError deleteFile(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter("path")) {
            return URoError.INVALID;
        }
        String path = invocation.getParameter("path", null);
        if(TextUtils.isEmpty(path)) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(URoFileCommandHelper.FileCommandType.FILE_DEL, invocation.getParameter("componentType", null)));
        request.setParameter("path", path);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError startExecScript(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.File.PARAM_KEY_TARGET)) {
            return URoError.INVALID;
        }
        String target = invocation.getParameter(URoInvocationParamKeys.File.PARAM_KEY_TARGET, null);
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_EXEC);
        request.setParameter("file_name", target);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError stopExecScript(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_STOP);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError updateScriptValue(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter("paramt")) {
            return URoError.INVALID;
        }
        HashMap<String, String> paramt = invocation.getParameter("paramt", null);
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_PARAM_UPDATE);
        request.setParameter("paramt", paramt);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError cleanupScriptValue(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_PARAM_CLEAR);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError updateScriptEvent(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter("paramt")) {
            return URoError.INVALID;
        }
        HashMap<String, String> paramt = invocation.getParameter("paramt", null);
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_EVENT_UPDATE);
        request.setParameter("paramt", paramt);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError cleanupScriptEvent(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_EVENT_CLEAR);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError readMainBoardInfo(URoInvocation invocation) {
        URoUkitSmartBoardInfoMission mission = new URoUkitSmartBoardInfoMission(this);
        mission.setCallback(invocation.getCompletionCallback());
        mission.start();
        return URoError.SUCCESS;
    }

    private URoError stopRunning(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_COMBO_OFF);
        request.setParameter("dev", 0xFF);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError setSelfCheck(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        URoCompletionCallbackHelper.sendSuccessCallback(null, invocation.getCompletionCallback());
        return URoError.SUCCESS;
    }

    private URoError calibrateSensorColor(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID)) {
            return URoError.INVALID;
        }
        int id = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID, null);
        if(checkOtherId(id)) {
            return URoError.INVALID;
        }
        URoUkitSmartColorCalibrationMission mission = new URoUkitSmartColorCalibrationMission(this, id);
        mission.setCallback(invocation.getCompletionCallback());
        mission.start();
        return URoError.SUCCESS;
    }

    private URoError getWiFiList(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_WIFI_LIST_GET);
        return sendRequest(request, new URoCompletionConverterCallback<PbIntelligentDevWifiListGet.IntelligentDevWifiListGetResponse, URoWiFiScanApInfo[]>(invocation.getCompletionCallback()) {
            @Override
            public URoWiFiScanApInfo[] convert(PbIntelligentDevWifiListGet.IntelligentDevWifiListGetResponse source) throws Throwable {
                ArrayList<URoWiFiScanApInfo> list = new ArrayList<>();
                List<PbIntelligentDevWifiListGet.IntelligentDevWifiListGetResponse.ap_info_t> apList = source.getApInfoList();
                for(PbIntelligentDevWifiListGet.IntelligentDevWifiListGetResponse.ap_info_t ap : apList) {
                    URoWiFiScanApInfo apInfo = new URoWiFiScanApInfo(ap.getSsid(), ap.getRssi(), URoWiFiAuthMode.findByCode(ap.getAuthmode()));
                    list.add(apInfo);
                }
                return list.toArray(new URoWiFiScanApInfo[list.size()]);
            }
        });
    }

    private URoError setWiFiConfig(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter("scanApInfo")
                || !invocation.hasParameter("password")) {
            return URoError.INVALID;
        }
        URoWiFiScanApInfo apInfo = invocation.getParameter("scanApInfo", null);
        String password = invocation.getParameter("password", null);
        if(apInfo == null || password == null) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_WIFI_SSID_SET);
        request.setParameter("ssid", apInfo.getSsid());
        request.setParameter("password", password);
        request.setTimeoutThreshold(invocation.timeoutThreshold());
        return sendRequest(request, new URoCompletionConverterCallback<PbIntelligentDevWifiSsidSet.IntelligentDevWifiSsidSetResponse, URoWiFiStatusInfo>(invocation.getCompletionCallback()) {
            @Override
            public URoWiFiStatusInfo convert(PbIntelligentDevWifiSsidSet.IntelligentDevWifiSsidSetResponse source) throws Throwable {
                URoWiFiStatusInfo.URoWiFiState state = URoWiFiStatusInfo.URoWiFiState.findByCode(source.getWifiState());
                URoWiFiStatusInfo.URoWiFiDisconnectReason disconnectReason = URoWiFiStatusInfo.URoWiFiDisconnectReason.findByCode(source.getWifiDisconnectReason());
                String ssid = source.getSsid();
                int rssi = source.getRssi();
                URoWiFiAuthMode authMode = URoWiFiAuthMode.findByCode(source.getAuthmode());
                return new URoWiFiStatusInfo(state, disconnectReason, ssid, rssi, authMode);
            }
        });
    }

    private URoError getWiFiState(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_WIFI_INFO_GET);
        return sendRequest(request, new URoCompletionConverterCallback<PbIntelligentDevWifiInfoGet.IntelligentDevWifiInfoGetResponse, URoWiFiStatusInfo>(invocation.getCompletionCallback()) {
            @Override
            public URoWiFiStatusInfo convert(PbIntelligentDevWifiInfoGet.IntelligentDevWifiInfoGetResponse source) throws Throwable {
                URoWiFiStatusInfo.URoWiFiState state = URoWiFiStatusInfo.URoWiFiState.findByCode(source.getWifiState());
                URoWiFiStatusInfo.URoWiFiDisconnectReason disconnectReason = URoWiFiStatusInfo.URoWiFiDisconnectReason.findByCode(source.getWifiDisconnectReason());
                String ssid = source.getSsid();
                int rssi = source.getRssi();
                URoWiFiAuthMode authMode = URoWiFiAuthMode.findByCode(source.getAuthmode());
                return new URoWiFiStatusInfo(state, disconnectReason, ssid, rssi, authMode);
            }
        });
    }

    private URoError getNetworkState(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_NETWORK_INFO_GET);
        return sendRequest(request, new URoCompletionConverterCallback<PbIntelligentDevNetworkInfoGet.IntelligentDevNetworkInfoGetResponse, URoNetworkState>(invocation.getCompletionCallback()) {
            @Override
            public URoNetworkState convert(PbIntelligentDevNetworkInfoGet.IntelligentDevNetworkInfoGetResponse source) throws Throwable {
                URoNetworkState state = URoNetworkState.findByCode(source.getNetworkState());
                return state;
            }
        });
    }

    private URoError setWiFiConnectEnable(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_ENABLE)) {
            return URoError.INVALID;
        }
        boolean enabled = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ENABLE, false);
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_WIFI_CONNECT_SET);
        request.setParameter("status", enabled);
        return sendRequest(request, new URoCompletionConverterCallback<PbIntelligentDevWifiConnectSet.IntelligentDevWifiConnectSetResponse, Void>(invocation.getCompletionCallback()) {
            @Override
            public Void convert(PbIntelligentDevWifiConnectSet.IntelligentDevWifiConnectSetResponse source) throws Throwable {
                return null;
            }
        });
    }

    private URoError reboot(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_RESET);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError getAudioRecordList(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_FILE_LIST);
        return sendRequest(request, new URoCompletionConverterCallback<PbVoiceFileList.VoiceFileListResponse, URoAudioRecord[]>(invocation.getCompletionCallback()) {
            @Override
            public URoAudioRecord[] convert(PbVoiceFileList.VoiceFileListResponse source) throws Throwable {
                ArrayList<URoAudioRecord> list = new ArrayList<>();
                List<PbVoiceFileList.VoiceFileListResponse.file_info> fileList = source.getListList();
                for(PbVoiceFileList.VoiceFileListResponse.file_info file : fileList) {
                    URoAudioRecord apInfo = new URoAudioRecord(file.getFileName(), file.getFileDuration(), file.getFileDate(), null);
                    list.add(apInfo);
                }
                return list.toArray(new URoAudioRecord[list.size()]);
            }
        });
    }

    private URoError getAudioRecordNum(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_FILE_NUM);
        return sendRequest(request, new URoCompletionConverterCallback<PbVoiceFileNum.VoiceFileNumResponse, Integer>(invocation.getCompletionCallback()) {
            @Override
            public Integer convert(PbVoiceFileNum.VoiceFileNumResponse source) throws Throwable {
                return source.getFileNum();
            }
        });
    }

    private URoError startAudioPlay(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter("file_name")) {
            return URoError.INVALID;
        }
        String fileName = invocation.getParameter("file_name", null);
        int sessionId = invocation.getParameter("session_id", 0);
        if(TextUtils.isEmpty(fileName)) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_RECORD_PLAY_START);
        request.setParameter("file_name", fileName);
        request.setParameter("session_id", sessionId);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError startSoundPlay(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter("file_name")
                || !invocation.hasParameter("file_type")) {
            return URoError.INVALID;
        }
        String fileName = invocation.getParameter("file_name", null);
        String fileType = invocation.getParameter("file_type", null);
        int sessionId = invocation.getParameter("session_id", 0);
        if(TextUtils.isEmpty(fileName) || TextUtils.isEmpty(fileType)) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_EFFECT_PLAY);
        request.setParameter("file_type", fileType);
        request.setParameter("file_name", fileName);
        request.setParameter("session_id", sessionId);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError stopAudioPlay(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_FILE_PLAY_STOP);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError startAudioRecord(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter("file_name")
                || !invocation.hasParameter("file_duration")) {
            return URoError.INVALID;
        }
        String fileName = invocation.getParameter("file_name", null);
        long duration = invocation.getParameter("file_duration", 0L);
        int sessionId = invocation.getParameter("session_id", 0);
        if(TextUtils.isEmpty(fileName) || duration == 0) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_START_RECORD);
        request.setParameter("file_name", fileName);
        request.setParameter("file_duration", duration);
        request.setParameter("session_id", sessionId);
        request.setTimeoutThreshold(invocation.timeoutThreshold());
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError stopAudioRecord(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_STOP_RECORD);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError renameAudioRecord(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        String newName = invocation.getParameter("new_name", null);
        URoAudioRecord audioRecord = invocation.getParameter("audio_record", null);
        if(TextUtils.isEmpty(newName) || audioRecord == null) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_FILE_RENAME);
        request.setParameter("old_name", audioRecord.getName());
        request.setParameter("new_name", newName);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError deleteAudioRecord(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        URoAudioRecord audioRecord = invocation.getParameter("audio_record", null);
        if(audioRecord == null) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_FILE_DEL);
        request.setParameter("file_name", audioRecord.getName());
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError audioSetVolume(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter("volume")) {
            return URoError.INVALID;
        }
        int volume = invocation.getParameter("volume", 70);
        volume = restrictRange(volume, 0, 100);
        volume = (volume / 7) * 7;
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_SET_VOLUME);
        request.setParameter("value", volume);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError cancelAudioRecord(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_RECORD_CANCEL);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private <T> URoError functionIntelligentDevComplexGet(ArrayList<URoRequest> requests, URoCompletionCallback<List<UroComplexItemResult<T>>> completionCallback) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_COMPLEX_GET);
        request.setParameter("requestArray",requests);
        return sendRequest(request, completionCallback);
    }

    private URoError functionIntelligentDevComplexSet(ArrayList<URoRequest> requests, URoCompletionCallback<List<UroComplexItemResult<Void>>> completionCallback) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_COMPLEX_SET);
        request.setParameter("requestArray",requests);
        return sendRequest(request, completionCallback);
    }

    private URoError devComplexSetWithSimpleResult(ArrayList<URoRequest> requests, URoCompletionCallback<Void> completionCallback) {
        return functionIntelligentDevComplexSet(requests, new URoCompletionConverterCallback<List<UroComplexItemResult<Void>>,Void>(completionCallback) {
            @Override
            public Void convert(List<UroComplexItemResult<Void>> source) throws Throwable {
                return null;
            }
        });
    }

    private URoError readbackBatchServosSimultaneous(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY)
                || !invocation.hasParameter(URoInvocationParamKeys.Servos.PARAM_KEY_READBACK_POWEROFF)) {
            return URoError.INVALID;
        }
        int[] ids = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, null);
        boolean powerOff = invocation.getParameter(URoInvocationParamKeys.Servos.PARAM_KEY_READBACK_POWEROFF, false);
        if(ids == null || ids.length == 0) {
            return URoError.INVALID;
        }
        for(int id : ids) {
            if (!checkServosId(id)) {
                return URoError.INVALID;
            }
        }
        URoUkitSmartAngleFeedbackInfo feedbackInfo = new URoUkitSmartAngleFeedbackInfo(ids);
        ArrayList<URoRequest> requests=new ArrayList<>();
        for(int id : ids) {
            URoRequest request = new URoRequest(URoCommandConstants.CMD_SRV_ANGLE_GET);
            request.setParameter("id", id);
            request.setParameter("pwr", powerOff);
            requests.add(request);
        }
        return functionIntelligentDevComplexGet(requests, new URoCompletionConverterCallback<List<UroComplexItemResult<PbServoAngleGet.ServoAngleGetResponse>>, URoUkitSmartAngleFeedbackInfo>(invocation.getCompletionCallback(), true) {
            @Override
            public URoUkitSmartAngleFeedbackInfo convert(List<UroComplexItemResult<PbServoAngleGet.ServoAngleGetResponse>> resultList) throws Throwable {
                for (int i = 0; i < resultList.size(); i++) {
                    UroComplexItemResult<PbServoAngleGet.ServoAngleGetResponse> itemResult = resultList.get(i);
                    if (itemResult.isSuccess()) {
                        feedbackInfo.addServoAngle(itemResult.getId(), (itemResult.getData().getCurAngle() / 10) - BASE_SERVOS_ANGLE_VALUE, true);
                    } else {
                        feedbackInfo.addServoAngle(itemResult.getId(), 0, false);
                    }
                }
                if (isConnected() && invocation.isSendComponentError()) {
                    Integer[] errorIds = feedbackInfo.getErrorIds();
                    //URoLogUtils.e("errorIds: %s", Arrays.toString(errorIds));
                    for (Integer errorId : errorIds) {
                        sendComponentError(URoComponentType.SERVOS, errorId, URoError.READ_ERROR);
                    }
                }
                return feedbackInfo;
            }
        });
    }

    private URoError turnBatchServosSimultaneous(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_TIME)
                || !invocation.hasParameter(URoInvocationParamKeys.Servos.PARAM_KEY_TURN_ID_ANGLE_PAIR)) {
            return URoError.INVALID;
        }
        int time = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_TIME, Integer.MIN_VALUE);
        ArrayMap<Integer, Integer> idAnglePairs = invocation.getParameter(URoInvocationParamKeys.Servos.PARAM_KEY_TURN_ID_ANGLE_PAIR, null);
        if(idAnglePairs == null || idAnglePairs.isEmpty() || time == Integer.MIN_VALUE) {
            return URoError.INVALID;
        }
        idAnglePairs = new ArrayMap<>(idAnglePairs);
        for(Map.Entry<Integer, Integer> entry : idAnglePairs.entrySet()) {
            Integer id = entry.getKey();
            Integer angle = entry.getValue();
            if(id == null || angle == null) {
                return URoError.INVALID;
            }
            if(!checkServosId(id)) {
                return URoError.INVALID;
            }
            entry.setValue(restrictRange(angle + BASE_SERVOS_ANGLE_VALUE, MIN_SERVOS_ANGLE_VALUE, MAX_SERVOS_ANGLE_VALUE));
        }
        ArrayList<URoRequest> requests=new ArrayList<>();
        for(Map.Entry<Integer, Integer> entry : idAnglePairs.entrySet()) {
            Integer id = entry.getKey();
            Integer angle = entry.getValue();
            URoRequest request = new URoRequest(URoCommandConstants.CMD_SRV_ANGLE_SET);
            request.setParameter("id", id);
            request.setParameter("tar_angle", angle * 10);
            request.setParameter("rotation_time", restrictRange(time, 0, 20 * 0xFF));
            request.setParameter("shielding_time", restrictRange(0, 0, 0xFFFF));
            request.setParameter("mode", 0);
            requests.add(request);
        }
        return functionIntelligentDevComplexSet(requests, new URoCompletionConverterCallback<List<UroComplexItemResult<Void>>, Void>(invocation.getCompletionCallback(),true) {
            @Override
            public Void convert(List<UroComplexItemResult<Void>> responseList) throws Throwable {
                for (int i = 0; i < responseList.size(); i++) {
                    UroComplexItemResult<Void> item = responseList.get(i);
                    if (!item.isSuccess()) {
                        sendComponentError(URoComponentType.SERVOS, item.getId(), URoError.CTRL_ERROR);
                    }
                }
                return null;
            }
        });
    }

    private URoError getFileStat(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter("path")) {
            return URoError.INVALID;
        }
        String path = invocation.getParameter("path", null);
        if(TextUtils.isEmpty(path)) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(URoFileCommandHelper.FileCommandType.FILE_STATE, invocation.getParameter("componentType", null)));
        request.setParameter("path", path);
        return sendRequest(request, new URoCompletionConverterCallback<PbFileState.FileStateResponse, URoFileStat>(invocation.getCompletionCallback()) {
            @Override
            public URoFileStat convert(PbFileState.FileStateResponse source) throws Throwable {
                String name = source.getFileName();
                long size = source.getFileSize();
                URoFileStat.Type type;
                if(source.getFileMode() >= URoFileStat.Type.values().length || source.getFileMode() < 0) {
                    type = URoFileStat.Type.NOT_EXIST;
                } else {
                    type = URoFileStat.Type.values()[source.getFileMode()];
                }
                byte[] md5Data = source.getFileMd5().toByteArray();
                StringBuilder sb = new StringBuilder();
                for(byte eachByte : md5Data) {
                    sb.append(String.format(Locale.US,"%02x", eachByte));
                }
                String md5 = sb.toString();
                return new URoFileStat(name, size, type, md5);
            }
        });
    }

//    private URoError rotateMotorWithPWM(URoInvocation invocation) {
//        //todo useless
//        return URoError.INVALID;
//    }

//    private URoError calibrateSensorSound(URoInvocation invocation) {
//        //todo useless
//        return URoError.INVALID;
//    }

//    private URoError calibrateSensorBrightness(URoInvocation invocation) {
//        //todo useless
//        return URoError.INVALID;
//    }

//    private URoError takeMotion(URoInvocation invocation) {
//        //todo useless, later
//        return URoError.INVALID;
//    }

    /* ==================== PENETRATE =================== */

    private URoError functionSnGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SN_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSnSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SN_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionHwVer(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_HW_VER, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSwVer(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SW_VER, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionPtVer(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_PT_VER, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionRecover(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_RECOVER, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionReset(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_RESET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionHeartbeat(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_HEARTBEAT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionStart(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_START, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionStop(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_STOP, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionStateGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_STATE_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionTypeGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_TYPE_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionRandomGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_RANDOM_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSubtypeSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SUBTYPE_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionParaSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_PARA_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionParaGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_PARA_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionDevStatusLedSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_DEV_STATUS_LED_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionMcuSnGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_MCU_SN_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionFileSendMaxPackSize(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(FileCommandType.FILE_SEND_MAX_PACK_SIZE, invocation.getParameter("componentType", null)), invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionFileSendStart(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(FileCommandType.FILE_SEND_START, invocation.getParameter("componentType", null)), invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionFileSending(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(FileCommandType.FILE_SENDING, invocation.getParameter("componentType", null)), invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionFileSendOver(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(FileCommandType.FILE_SEND_OVER, invocation.getParameter("componentType", null)), invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionFileSendStop(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(FileCommandType.FILE_SEND_STOP, invocation.getParameter("componentType", null)), invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionFileRecvMaxPackSize(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(FileCommandType.FILE_RECV_MAX_PACK_SIZE, invocation.getParameter("componentType", null)), invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionFileRecvStart(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(FileCommandType.FILE_RECV_START, invocation.getParameter("componentType", null)), invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionFileRecving(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(FileCommandType.FILE_RECVING, invocation.getParameter("componentType", null)), invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionFileRecvOver(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(FileCommandType.FILE_RECV_OVER, invocation.getParameter("componentType", null)), invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionFileRecvStop(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(FileCommandType.FILE_RECV_STOP, invocation.getParameter("componentType", null)), invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionFileDel(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(FileCommandType.FILE_DEL, invocation.getParameter("componentType", null)), invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionFileRename(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(FileCommandType.FILE_RENAME, invocation.getParameter("componentType", null)), invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionFileState(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(FileCommandType.FILE_STATE, invocation.getParameter("componentType", null)), invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionDirMake(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(FileCommandType.DIR_MAKE, invocation.getParameter("componentType", null)), invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionDirDel(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(FileCommandType.DIR_DEL, invocation.getParameter("componentType", null)), invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionDirFCntGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(FileCommandType.DIR_F_CNT_GET, invocation.getParameter("componentType", null)), invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionDirFInfoGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoFileCommandHelper.getCommandByDev(FileCommandType.DIR_F_INFO_GET, invocation.getParameter("componentType", null)), invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSrvAngleSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SRV_ANGLE_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSrvAngleGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SRV_ANGLE_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSrvPwmSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SRV_PWM_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSrvWheelSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SRV_WHEEL_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSrvStop(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SRV_STOP, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSrvCalibrationSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SRV_CALIBRATION_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSrvCalibrationGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SRV_CALIBRATION_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSrvFaultGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SRV_FAULT_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSrvFaultClear(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SRV_FAULT_CLEAR, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSrvLimitSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SRV_LIMIT_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSrvStatusGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SRV_STATUS_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSrvInfoGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SRV_INFO_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionMtrSpeedSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_MTR_SPEED_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionMtrStop(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_MTR_STOP, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionMtrSpeedGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_MTR_SPEED_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionMtrPwmUpperLimitSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_MTR_PWM_UPPER_LIMIT_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionMtrPwmSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_MTR_PWM_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionMtrPwmGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_MTR_PWM_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionMtrCntGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_MTR_CNT_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionMtrCntReset(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_MTR_CNT_RESET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionMtrFaultGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_MTR_FAULT_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionMtrFaultClear(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_MTR_FAULT_CLEAR, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIrAdcGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_IR_ADC_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIrDisGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_IR_DIS_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIrOnoffGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_IR_ONOFF_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionThGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_TH_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSndAdcValueGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SND_ADC_VALUE_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSndHysteresisTimeSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SND_HYSTERESIS_TIME_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSndHysteresisTimeGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SND_HYSTERESIS_TIME_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSndCalibrateSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SND_CALIBRATE_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSndCalibrateGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SND_CALIBRATE_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLedFixExpSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LED_FIX_EXP_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLedExpSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LED_EXP_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUltDisGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_ULT_DIS_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionTchTimesGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_TCH_TIMES_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionTchTypeGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_TCH_TYPE_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionTchHysteresisTimeSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_TCH_HYSTERESIS_TIME_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionTchHysteresisTimeGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_TCH_HYSTERESIS_TIME_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionTchStateGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_TCH_STATE_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionClrRgbGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_CLR_RGB_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionClrCalOnoff(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_CLR_CAL_ONOFF, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionClrCalStateGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_CLR_CAL_STATE_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionBatPwrPct(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_BAT_PWR_PCT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionBatStatusGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_BAT_STATUS_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionBatTemperature(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_BAT_TEMPERATURE, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionBatVol(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_BAT_VOL, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionBatChgCur(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_BAT_CHG_CUR, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionBatDchgCur(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_BAT_DCHG_CUR, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionBatCapacity(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_BAT_CAPACITY, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLgtValueGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LGT_VALUE_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLgtStdvalueSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LGT_STDVALUE_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLgtStdvalueGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LGT_STDVALUE_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLgtCoeGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LGT_COE_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevComboOff(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_COMBO_OFF, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevScriptExec(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_EXEC, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevScriptStop(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_STOP, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevScriptExecReport(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_EXEC_REPORT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevScriptParamUpdate(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_PARAM_UPDATE, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevScriptParamClear(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_PARAM_CLEAR, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevScriptEventUpdate(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_EVENT_UPDATE, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevScriptEventClear(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_EVENT_CLEAR, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevSensorRead(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_SENSOR_READ, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSrvIdSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SRV_ID_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionMtrIdSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_MTR_ID_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIrIdSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_IR_ID_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionThIdSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_TH_ID_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSndIdSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SND_ID_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLedIdSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LED_ID_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUltIdSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_ULT_ID_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionTchIdSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_TCH_ID_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionClrIdSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_CLR_ID_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLgtIdSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LGT_ID_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSukIdSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SUK_ID_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLedBeltIdSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LED_BELT_ID_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionActionCompletedReport(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_ACTION_COMPLETED_REPORT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUpdateInfoSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPDATE_INFO_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionFileDownloadProgressReport(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_FILE_DOWNLOAD_PROGRESS_REPORT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUpdateStatusReport(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPDATE_STATUS_REPORT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUltLightSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_ULT_LIGHT_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSrvOn(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SRV_ON, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSrvOff(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SRV_OFF, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionMtrOn(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_MTR_ON, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionMtrOff(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_MTR_OFF, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIrOn(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_IR_ON, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIrOff(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_IR_OFF, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionThOn(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_TH_ON, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionThOff(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_TH_OFF, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSndOn(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SND_ON, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSndOff(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SND_OFF, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLedOn(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LED_ON, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLedOff(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LED_OFF, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUltOn(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_ULT_ON, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUltOff(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_ULT_OFF, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionTchOn(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_TCH_ON, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionTchOff(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_TCH_OFF, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionClrOn(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_CLR_ON, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionClrOff(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_CLR_OFF, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLgtOn(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LGT_ON, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLgtOff(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LGT_OFF, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSpkGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SPK_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSukOn(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SUK_ON, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSukOff(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SUK_OFF, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLcdOn(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LCD_ON, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLcdOff(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LCD_OFF, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }


    private URoError functionSrvReset(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SRV_RESET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionMtrReset(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_MTR_RESET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIrReset(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_IR_RESET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionThReset(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_TH_RESET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSndReset(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SND_RESET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLedReset(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LED_RESET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUltReset(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_ULT_RESET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionTchReset(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_TCH_RESET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionClrReset(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_CLR_RESET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLgtReset(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LGT_RESET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSpkReset(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SPK_RESET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSukReset(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SUK_RESET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLedBeltReset(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LED_BELT_RESET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLcdReset(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LCD_RESET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleReset(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_RESET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }


    private URoError functionSrvSwVer(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SRV_SW_VER, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionMtrSwVer(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_MTR_SW_VER, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIrSwVer(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_IR_SW_VER, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionThSwVer(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_TH_SW_VER, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSndSwVer(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SND_SW_VER, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLedSwVer(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LED_SW_VER, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUltSwVer(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_ULT_SW_VER, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionTchSwVer(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_TCH_SW_VER, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionClrSwVer(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_CLR_SW_VER, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLgtSwVer(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LGT_SW_VER, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSpkSwVer(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SPK_SW_VER, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSukSwVer(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SUK_SW_VER, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLedBeltSwVer(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LED_BELT_SW_VER, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLcdSwVer(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LCD_SW_VER, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleSwVer(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_SW_VER, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }


    private URoError functionIntelligentDevSelfCheckingReport(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_SELF_CHECKING_REPORT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevInfoGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_INFO_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevUpdateReport(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_UPDATE_REPORT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevDebugSwitch(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_DEBUG_SWITCH, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevWifiSsidSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_WIFI_SSID_SET, invocation);
        request.setTimeoutThreshold(20000L);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevWifiListGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_WIFI_LIST_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevRecordingFileReport(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_RECORDING_FILE_REPORT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevRecordingFileInfo(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_RECORDING_FILE_INFO, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevFaultClear(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_FAULT_CLEAR, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }


    private URoError functionIntelligentDevChangeSwitchSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_CHANGE_SWITCH_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevWifiInfoGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_WIFI_INFO_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevWifiInfoReport(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_WIFI_INFO_REPORT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevWifiConnectSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_WIFI_CONNECT_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }


    private URoError functionIntelligentDevNetworkInfoGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_NETWORK_INFO_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevNetworkInfoReport(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_NETWORK_INFO_REPORT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevVersionInfoGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_VERSION_INFO_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }


    private URoError functionBatStatusReport(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_BAT_STATUS_REPORT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }


//    private URoError functionWorkModeGet(URoInvocation invocation) {
//        URoRequest request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_GET, invocation);
//        return sendRequest(request, invocation.getCompletionCallback());
//    }
//
//    private URoError functionWorkModeSet(URoInvocation invocation) {
//        URoRequest request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_SET, invocation);
//        return sendRequest(request, invocation.getCompletionCallback());
//    }
//
//    private URoError functionUploadTimeSet(URoInvocation invocation) {
//        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_TIME_SET, invocation);
//        return sendRequest(request, invocation.getCompletionCallback());
//    }
//
//    private URoError functionUploadThresholdSet(URoInvocation invocation) {
//        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_THRESHOLD_SET, invocation);
//        return sendRequest(request, invocation.getCompletionCallback());
//    }
//
//    private URoError functionUploadOffsetSet(URoInvocation invocation) {
//        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_OFFSET_SET, invocation);
//        return sendRequest(request, invocation.getCompletionCallback());
//    }

    private URoError functionIrDisPush(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_IR_DIS_PUSH, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionThPush(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_TH_PUSH, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSndAdcValuePush(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SND_ADC_VALUE_PUSH, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUltDisPush(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_ULT_DIS_PUSH, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionTchTypePush(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_TCH_TYPE_PUSH, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionClrRgbPush(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_CLR_RGB_PUSH, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLgtValuePush(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LGT_VALUE_PUSH, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionWorkModeIrGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_IR_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionWorkModeIrSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_IR_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUploadTimeIrSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_TIME_IR_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUploadThresholdIrSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_THRESHOLD_IR_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUploadOffsetIrSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_OFFSET_IR_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }
    private URoError functionWorkModeThGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_TH_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionWorkModeThSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_TH_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUploadTimeThSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_TIME_TH_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUploadThresholdThSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_THRESHOLD_TH_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUploadOffsetThSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_OFFSET_TH_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }
    private URoError functionWorkModeSndGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_SND_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionWorkModeSndSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_SND_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUploadTimeSndSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_TIME_SND_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUploadThresholdSndSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_THRESHOLD_SND_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUploadOffsetSndSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_OFFSET_SND_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }
    private URoError functionWorkModeUltGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_ULT_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionWorkModeUltSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_ULT_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUploadTimeUltSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_TIME_ULT_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUploadThresholdUltSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_THRESHOLD_ULT_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUploadOffsetUltSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_OFFSET_ULT_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }
    private URoError functionWorkModeTchGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_TCH_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionWorkModeTchSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_TCH_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUploadTimeTchSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_TIME_TCH_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUploadThresholdTchSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_THRESHOLD_TCH_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUploadOffsetTchSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_OFFSET_TCH_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }
    private URoError functionWorkModeClrGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_CLR_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionWorkModeClrSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_CLR_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUploadTimeClrSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_TIME_CLR_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUploadThresholdClrSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_THRESHOLD_CLR_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUploadOffsetClrSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_OFFSET_CLR_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }
    private URoError functionWorkModeLgtGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_LGT_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionWorkModeLgtSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_WORK_MODE_LGT_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUploadTimeLgtSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_TIME_LGT_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUploadThresholdLgtSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_THRESHOLD_LGT_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionUploadOffsetLgtSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPLOAD_OFFSET_LGT_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }


    private URoError functionUpdateStop(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_UPDATE_STOP, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionDevUpdateStop(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_DEV_UPDATE_STOP, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }


    private URoError functionVrFileDel(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_FILE_DEL, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVrFileRename(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_FILE_RENAME, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVrFileList(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_FILE_LIST, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVrStartRecord(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_START_RECORD, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVrStopRecord(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_STOP_RECORD, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVrRecordInfoReport(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_RECORD_INFO_REPORT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVrEffectPlay(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_EFFECT_PLAY, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVrRecordPlayStart(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_RECORD_PLAY_START, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVrFilePlayStop(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_FILE_PLAY_STOP, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVrPlayInfoReport(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_PLAY_INFO_REPORT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVrTtsPlay(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_TTS_PLAY, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVrTtsInfoReport(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_TTS_INFO_REPORT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVrAsrStart(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_ASR_START, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVrAsrStop(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_ASR_STOP, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVrAsrInfoReport(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_ASR_INFO_REPORT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVrSetVolume(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_SET_VOLUME, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVrGetVolume(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_GET_VOLUME, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVrSetLanguage(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_SET_LANGUAGE, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVrGetLanguage(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_GET_LANGUAGE, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }


    private URoError functionVrFileNum(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VR_FILE_NUM, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevBleMacGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_BLE_MAC_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevWifiMacGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_WIFI_MAC_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevIpInfoGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_IP_INFO_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }


    private URoError functionSuckerStatusSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SUCKER_STATUS_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionSuckerStatusGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SUCKER_STATUS_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }


    private URoError functionIntelligentDevDebugInfoReport(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_DEBUG_INFO_REPORT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }


    private URoError functionIntelligentDevRedirectSwitchSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_REDIRECT_SWITCH_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionIntelligentDevStreamSwitchSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_INTELLIGENT_DEV_STREAM_SWITCH_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError resetStateSet(URoInvocation invocation) {
        if (invocation==null){
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_RESET_STATE_SET);
        return sendRequest(request, invocation.getCompletionCallback());
    }


    private URoError functionLedBeltExpSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LED_BELT_EXP_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLedBeltLedsNumGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LED_BELT_LEDS_NUM_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLedBeltFixExpSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LED_BELT_FIX_EXP_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLedBeltBrightnessSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LED_BELT_BRIGHTNESS_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLedBeltOffSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LED_BELT_OFF_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLedBeltMoveSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LED_BELT_MOVE_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLedBeltExpressionsContinuousSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LED_BELT_EXPRESSIONS_CONTINUOUS_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLedBeltExpressionsContinuousBreathSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LED_BELT_EXPRESSIONS_CONTINUOUS_BREATH_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }


    private URoError functionLcdGuiVerGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LCD_GUI_VER_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLcdClear(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LCD_CLEAR, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLcdBackLightSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LCD_BACK_LIGHT_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLcdBackgroundColorSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LCD_BACKGROUND_COLOR_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLcdBarDisplay(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LCD_BAR_DISPLAY, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLcdStaticText(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LCD_STATIC_TEXT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLcdRollText(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LCD_ROLL_TEXT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLcdIcon(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LCD_ICON, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLcdInnerPic(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LCD_INNER_PIC, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLcdPicDisplay(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LCD_PIC_DISPLAY, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLcdSensorDisplay(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LCD_SENSOR_DISPLAY, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLcdSwitchPage(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LCD_SWITCH_PAGE, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLcdGetPage(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LCD_GET_PAGE, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLcdTestPic(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LCD_TEST_PIC, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionLcdRecover(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LCD_RECOVER, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }


    private URoError functionVisionmoduleMidOffset(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_MID_OFFSET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleJpegQualitySet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_JPEG_QUALITY_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleIdentify(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_IDENTIFY, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleFaceRecord(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_FACE_RECORD, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleFaceInfolistGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_FACE_INFOLIST_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleModelSwitch(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_MODEL_SWITCH, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleFaceNameModify(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_FACE_NAME_MODIFY, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleFaceDelete(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_FACE_DELETE, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleOnlineIdentify(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_ONLINE_IDENTIFY, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleWifiSsidSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_WIFI_SSID_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleWifiListGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_WIFI_LIST_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleWifiConnectSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_WIFI_CONNECT_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleWifiInfoGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_WIFI_INFO_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleWifiInfoReport(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_WIFI_INFO_REPORT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleNetworkInfoGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_NETWORK_INFO_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleNetworkInfoReport(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_NETWORK_INFO_REPORT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleWifiMacGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_WIFI_MAC_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleIpInfoGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_IP_INFO_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleBtInfoGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_BT_INFO_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleBtInfoReport(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_BT_INFO_REPORT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleBleMacGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_BLE_MAC_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmodulePcLinkInfoGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_PC_LINK_INFO_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmodulePcLinkInfoReport(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_PC_LINK_INFO_REPORT, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleProductInfoGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_PRODUCT_INFO_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmoduleVersionInfoGet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_VERSION_INFO_GET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmodulePartInfoSet(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_PART_INFO_SET, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmodulePartDownloadProgressQuery(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_PART_DOWNLOAD_PROGRESS_QUERY, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError functionVisionmodulePartSwVer(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_VISIONMODULE_PART_SW_VER, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
    }

}
