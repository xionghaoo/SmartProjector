package com.ubtedu.deviceconnect.libs.ukit.legacy.product;

import android.text.TextUtils;
import android.util.ArrayMap;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallbackHelper;
import com.ubtedu.deviceconnect.libs.base.URoCompletionConverterCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.component.URoComponent;
import com.ubtedu.deviceconnect.libs.base.component.URoSensorComponent;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocation;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocationSequence;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationNames;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationParamKeys;
import com.ubtedu.deviceconnect.libs.base.mission.URoMission;
import com.ubtedu.deviceconnect.libs.base.model.URoColor;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentID;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.model.URoRotateMotorCommand;
import com.ubtedu.deviceconnect.libs.base.model.URoSensorData;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommandConstants;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.ukit.legacy.mission.URoUkitLegacyAngleFeedbackMission;
import com.ubtedu.deviceconnect.libs.ukit.legacy.mission.URoUkitLegacyBoardInfoMission;
import com.ubtedu.deviceconnect.libs.ukit.legacy.mission.URoUkitLegacyMainBoardUpgradeMission;
import com.ubtedu.deviceconnect.libs.ukit.legacy.mission.URoUkitLegacySensorUpgradeMission;
import com.ubtedu.deviceconnect.libs.ukit.legacy.mission.URoUkitLegacyServosUpgradeMission;
import com.ubtedu.deviceconnect.libs.ukit.legacy.model.URoUkitLegacyAngleFeedbackInfo;
import com.ubtedu.deviceconnect.libs.ukit.legacy.model.URoUkitLegacyComponentMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public class URoUkitLegacy extends URoProduct {

    public URoUkitLegacy(String name, String productID) {
        super(name, productID);
    }

    private final static int MAX_SERVOS_ANGLE_VALUE = 238;
    private final static int MIN_SERVOS_ANGLE_VALUE = 2;
    private final static int BASE_SERVOS_ANGLE_VALUE = 120;

    private final static int MAX_SERVOS_SPEED_VALUE = 1000;
    private final static int MIN_SERVOS_SPEED_VALUE = -1000;

    private final static int MAX_MOTOR_SPEED_VALUE = 140;
    private final static int MIN_MOTOR_SPEED_VALUE = -140;

    private URoError readMainBoardInfo(URoInvocation invocation) {
        URoUkitLegacyBoardInfoMission mission = new URoUkitLegacyBoardInfoMission(this);
        mission.setCallback(invocation.getCompletionCallback());
        mission.start();
        return URoError.SUCCESS;
    }

    private URoError stopRunning(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_BOARD_STOP);
        return sendRequest(request, invocation.getCompletionCallback());
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
            request = new URoRequest(URoCommandConstants.CMD_STEERING_GEAR_MODIFY);
            request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_SRC_ID, srcId);
            request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_NEW_ID, newId);
            request.setTimeoutThreshold(5000L);
        } else if(componentType.equals(URoComponentType.INFRAREDSENSOR)
                || componentType.equals(URoComponentType.TOUCHSENSOR)
                || componentType.equals(URoComponentType.ULTRASOUNDSENSOR)
                || componentType.equals(URoComponentType.LED)
                || componentType.equals(URoComponentType.COLORSENSOR)
                || componentType.equals(URoComponentType.ENVIRONMENTSENSOR)
                || componentType.equals(URoComponentType.BRIGHTNESSSENSOR)
                || componentType.equals(URoComponentType.SOUNDSENSOR)
                || componentType.equals(URoComponentType.MOTOR)) {
            if(srcId == newId || !checkOtherId(srcId) || !checkOtherId(newId)) {
                return URoError.INVALID;
            }
            Integer sensorType = URoUkitLegacyComponentMap.getModifyValue(componentType);
            if(sensorType == null) {
                return URoError.INVALID;
            }
            request = new URoRequest(URoCommandConstants.CMD_SENSOR_MODIFY);
            request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_TYPE, sensorType);
            request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_SRC_ID, srcId);
            request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_NEW_ID, newId);
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
        URoRequest request = new URoRequest(URoCommandConstants.CMD_STEERING_GEAR_ANGLE);
        request.setParameter(URoInvocationParamKeys.Servos.PARAM_KEY_TURN_ID_ANGLE_PAIR, idAnglePairs);
        request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_TIME, restrictRange(time, 0, 20 * 0xFF));
        request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_PADDING_TIME, restrictRange(0, 0, 0xFFFF));
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError rotateServos(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if (!invocation.hasParameter(URoInvocationParamKeys.Servos.PARAM_KEY_ROTATE_ID_SPEED_PAIR)) {
            if (!invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY)
                    || !invocation.hasParameter(URoInvocationParamKeys.Servos.PARAM_KEY_ROTATE_SPEED)) {
                return URoError.INVALID;
            }
            int[] ids = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, null);
            int speed = invocation.getParameter(URoInvocationParamKeys.Servos.PARAM_KEY_ROTATE_SPEED, Integer.MIN_VALUE);
            if (ids == null || ids.length == 0 || speed == Integer.MIN_VALUE) {
                return URoError.INVALID;
            }
            for (int id : ids) {
                if (!checkServosId(id)) {
                    return URoError.INVALID;
                }
            }
            URoRequest request = new URoRequest(URoCommandConstants.CMD_STEERING_GEAR_LOOP);
            request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, ids);
            request.setParameter(URoInvocationParamKeys.Servos.PARAM_KEY_ROTATE_SPEED, restrictRange(speed, MIN_SERVOS_SPEED_VALUE, MAX_SERVOS_SPEED_VALUE));
            return sendRequest(request, invocation.getCompletionCallback());
        } else {
            ArrayMap<Integer, Integer> idSpeedPairs = invocation.getParameter(URoInvocationParamKeys.Servos.PARAM_KEY_ROTATE_ID_SPEED_PAIR, null);
            if (idSpeedPairs == null || idSpeedPairs.size() == 0) {
                return URoError.INVALID;
            }
            for (Integer id : idSpeedPairs.keySet()) {
                if (id == null || !checkServosId(id) || idSpeedPairs.get(id) == null) {
                    return URoError.INVALID;
                }
            }
            //以speed为key，id数组为value
            ArrayMap<Integer, ArrayList<Integer>> speedIdsMap = new ArrayMap<>();
            for (Map.Entry<Integer, Integer> idSpeedEntry : idSpeedPairs.entrySet()) {
                Integer speed = idSpeedEntry.getValue();
                ArrayList<Integer> ids = speedIdsMap.get(speed);
                if (ids == null) {
                    ids = new ArrayList<>();
                }
                ids.add(idSpeedEntry.getKey());
                speedIdsMap.put(speed, ids);
            }
            URoInvocationSequence sequence = new URoInvocationSequence();
            for (int speed : speedIdsMap.keySet()) {
                URoInvocation request = new URoInvocation(URoInvocationNames.INVOCATION_CMD_STEERING_GEAR_LOOP);
                ArrayList<Integer> idList = speedIdsMap.get(speed);
                int[] ids = new int[idList.size()];
                for (int i = 0; i < idList.size(); i++) {
                    ids[i] = idList.get(i);
                }
                request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, ids);
                request.setParameter(URoInvocationParamKeys.Servos.PARAM_KEY_ROTATE_SPEED, restrictRange(speed, MIN_SERVOS_SPEED_VALUE, MAX_SERVOS_SPEED_VALUE));
                sequence.action(request);
            }
            sequence.setCompletionCallback(invocation.getCompletionCallback());
            return invokeSequence(sequence, true);
        }
    }

    private URoError functionSteeringGearLoop(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_STEERING_GEAR_LOOP, invocation);
        return sendRequest(request, invocation.getCompletionCallback());
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
        URoUkitLegacyAngleFeedbackMission mission = new URoUkitLegacyAngleFeedbackMission(this, new int[]{id}, powerOff);
        mission.setCallback(new URoCompletionConverterCallback<URoUkitLegacyAngleFeedbackInfo, Integer>(invocation.getCompletionCallback()) {
            @Override
            public Integer convert(URoUkitLegacyAngleFeedbackInfo source) throws Throwable {
                if(source.getAngles().length != 1) {
                    throw new IllegalStateException();
                }
                return source.getAngles()[0].angle;
            }
        });
        mission.start();
        return URoError.SUCCESS;
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
        URoUkitLegacyAngleFeedbackMission mission = new URoUkitLegacyAngleFeedbackMission(this, ids, powerOff);
        mission.setCallback(invocation.getCompletionCallback());
        mission.start();
        return URoError.SUCCESS;
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
        URoRequest request = new URoRequest(URoCommandConstants.CMD_STEERING_GEAR_LOOP);
        request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, ids);
        request.setParameter(URoInvocationParamKeys.Servos.PARAM_KEY_ROTATE_SPEED, 0);
        return sendRequest(request, invocation.getCompletionCallback());
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
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LOW_MOTOR_START);
        request.setParameter(URoInvocationParamKeys.Motor.PARAM_KEY_ROTATE_COMMAND, commands);
        return sendRequest(request, invocation.getCompletionCallback());
    }

//    private URoError rotateMotorWithPWM(URoInvocation invocation) {
//        //todo useless
//        return URoError.INVALID;
//    }

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
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LOW_MOTOR_STOP);
        request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, ids);
        return sendRequest(request, invocation.getCompletionCallback());
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
        Integer sensorType = URoUkitLegacyComponentMap.getExecutionValue(componentType);
        if(sensorType == null) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SENSOR_SWITCH);
        request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, new int[]{componentId});
        request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_TYPE, sensorType);
        request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ENABLE, enabled);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError motorFaultClear(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LOW_MOTOR_FIX);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError servosFaultClear(URoInvocation invocation) {
        URoRequest request = new URoRequest(URoCommandConstants.CMD_STEERING_GEAR_FIX);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError devFaultClear(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        URoInvocationSequence sequence = new URoInvocationSequence();
        sequence.setCompletionCallback(invocation.getCompletionCallback());
        sequence.action(new URoInvocation(URoInvocationNames.INVOCATION_SERVOS_FAULT_CLEAR));
        sequence.action(new URoInvocation(URoInvocationNames.INVOCATION_MOTOR_FAULT_CLEAR));
        return invokeSequence(sequence, false);
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
            if (!checkServosId(componentId.getId())) {
                return URoError.INVALID;
            }
        } else if(URoComponentType.SPEAKER.equals(componentType)) {
            if (componentId.getId() != 1) {
                return URoError.INVALID;
            }
        } else {
            if (!checkOtherId(componentId.getId())) {
                return URoError.INVALID;
            }
        }
        if(URoComponentType.SERVOS.equals(componentType)) {
            URoRequest request = new URoRequest(URoCommandConstants.CMD_STEERING_GEAR_LOOP);
            request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, new int[]{componentId.getId()});
            request.setParameter(URoInvocationParamKeys.Servos.PARAM_KEY_ROTATE_SPEED, 0);
            return sendRequest(request, new URoCompletionConverterCallback<Object, Boolean>(invocation.getCompletionCallback()) {
                @Override
                public Boolean convert(Object source) throws Throwable {
                    return true;
                }
            });
        } else if(URoComponentType.MOTOR.equals(componentType)) {
            URoRequest request = new URoRequest(URoCommandConstants.CMD_LOW_MOTOR_STOP);
            request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, new int[]{componentId.getId()});
            return sendRequest(request, new URoCompletionConverterCallback<Object, Boolean>(invocation.getCompletionCallback()) {
                @Override
                public Boolean convert(Object source) throws Throwable {
                    return true;
                }
            });
        } else if(URoComponentType.INFRAREDSENSOR.equals(componentType)
                || URoComponentType.TOUCHSENSOR.equals(componentType)
                || URoComponentType.LED.equals(componentType)
                || URoComponentType.COLORSENSOR.equals(componentType)
                || URoComponentType.ULTRASOUNDSENSOR.equals(componentType)) {
            URoRequest request = new URoRequest(URoCommandConstants.CMD_SENSOR_SWITCH);
            Integer sensorType = URoUkitLegacyComponentMap.getExecutionValue(componentType);
            request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, new int[]{componentId.getId()});
            request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_TYPE, sensorType);
            request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ENABLE, true);
            return sendRequest(request, new URoCompletionConverterCallback<Object, Boolean>(invocation.getCompletionCallback()) {
                @Override
                public Boolean convert(Object source) throws Throwable {
                    return true;
                }
            });
        } else if(URoComponentType.ENVIRONMENTSENSOR.equals(componentType)
                || URoComponentType.BRIGHTNESSSENSOR.equals(componentType)
                || URoComponentType.SOUNDSENSOR.equals(componentType)) {
            URoRequest request = new URoRequest(URoCommandConstants.CMD_SENSOR_VALUE);
            request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, new URoComponentID[]{componentId});
            return sendRequest(request, new URoCompletionConverterCallback<Object, Boolean>(invocation.getCompletionCallback()) {
                @Override
                public Boolean convert(Object source) throws Throwable {
                    return true;
                }
            });
        } else if(URoComponentType.SPEAKER.equals(componentType)) {
            URoRequest request = new URoRequest(URoCommandConstants.CMD_SENSOR_SPEAKER_INFO);
            return sendRequest(request, new URoCompletionConverterCallback<Object, Boolean>(invocation.getCompletionCallback()) {
                @Override
                public Boolean convert(Object source) throws Throwable {
                    return true;
                }
            });
        }
        return URoError.INVALID;
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
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SENSOR_VALUE);
        request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, new URoComponentID[]{componentId});
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
        URoComponentID[] ids = idList.toArray(new URoComponentID[idList.size()]);
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SENSOR_VALUE);
        request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, ids);
        URoError error = sendRequest(request, null);
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

//    private URoError calibrateSensorColor(URoInvocation invocation) {
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
        if(time < 0) {
            time = 0xFFFF;
        } else {
            time = restrictRange(time / 100, 0, 0xFFFF);
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LED_FACE);
        request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_TYPE, URoComponentType.ULTRASOUNDSENSOR);
        request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, ids);
        request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_TIME, time);
        request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_COLOR, color);
        return sendRequest(request, invocation.getCompletionCallback());
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
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LED_FACE);
        request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_TYPE, URoComponentType.LED);
        request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, ids);
        request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_COLOR, colors);
        request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_TIME, restrictRange(time, 0, 25500));
        return sendRequest(request, invocation.getCompletionCallback());
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
        URoRequest request = new URoRequest(URoCommandConstants.CMD_LED_EMOTION);
        request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, ids);
        request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_COLOR, color);
        request.setParameter(URoInvocationParamKeys.Sensor.PARAM_KEY_EFFECT_ID, effectID);
        request.setParameter(URoInvocationParamKeys.Sensor.PARAM_KEY_TIMES, times);
        return sendRequest(request, invocation.getCompletionCallback());
    }

    private URoError readSpeakerInfo(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_SENSOR_SPEAKER_INFO);
        return sendRequest(request, invocation.getCompletionCallback());
    }

//    private URoError takeMotion(URoInvocation invocation) {
//        //todo useless, later
//        return URoError.INVALID;
//    }

    private URoError upgrade(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_URL)) {
            return URoError.INVALID;
        }
        String path = invocation.getParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_URL, null);
        if(TextUtils.isEmpty(path)) {
            return URoError.INVALID;
        }
        URoUkitLegacyMainBoardUpgradeMission mission = new URoUkitLegacyMainBoardUpgradeMission(this, path);
        mission.setCallback(invocation.getCompletionCallback());
        mission.start();
        return URoError.SUCCESS;
    }

    private URoError upgradeComponent(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_URL)
                || !invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_TYPE)) {
            return URoError.INVALID;
        }
        String path = invocation.getParameter(URoInvocationParamKeys.Upgrade.PARAM_KEY_URL, null);
        URoComponentType componentType = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_TYPE, null);
        if(TextUtils.isEmpty(path) || componentType == null) {
            return URoError.INVALID;
        }
        URoMission mission = null;
        if(URoComponentType.SERVOS.equals(componentType)) {
            mission = new URoUkitLegacyServosUpgradeMission(this, path);
        } else if(URoComponentType.INFRAREDSENSOR.equals(componentType)
                || URoComponentType.TOUCHSENSOR.equals(componentType)
                || URoComponentType.LED.equals(componentType)
                || URoComponentType.SPEAKER.equals(componentType)) {
            mission = new URoUkitLegacySensorUpgradeMission(this, path, componentType);
        }
        if(mission != null) {
            mission.setCallback(invocation.getCompletionCallback());
            mission.start();
            return URoError.SUCCESS;
        }
        return URoError.INVALID;
    }

    private URoError setSelfCheck(URoInvocation invocation) {
        if(invocation == null) {
            return URoError.INVALID;
        }
        if(!invocation.hasParameter(URoInvocationParamKeys.Component.PARAM_KEY_ENABLE)) {
            return URoError.INVALID;
        }
        boolean enabled = invocation.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ENABLE, true);
        URoRequest request = new URoRequest(URoCommandConstants.CMD_BOARD_SELF_CHECK);
        request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ENABLE, enabled);
        return sendRequest(request, invocation.getCompletionCallback());
    }

}
