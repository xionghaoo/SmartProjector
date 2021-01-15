package com.ubtedu.deviceconnect.libs.ukit.legacy.model;

import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author naOKi
 * @Date 2019/09/23
 **/
public class URoUkitLegacyComponentMap {

    private URoUkitLegacyComponentMap() {}

    private static final HashMap<URoComponentType, Integer> modifyMap;
    private static final HashMap<URoComponentType, Integer> executionMap;

    static {
        modifyMap = new HashMap<>();
        modifyMap.put(URoComponentType.SERVOS, -1);
        modifyMap.put(URoComponentType.INFRAREDSENSOR, 1);
        modifyMap.put(URoComponentType.TOUCHSENSOR, 2);
        modifyMap.put(URoComponentType.ULTRASOUNDSENSOR, 6);
        modifyMap.put(URoComponentType.LED, 4);
        modifyMap.put(URoComponentType.SPEAKER, 8);
        modifyMap.put(URoComponentType.COLORSENSOR, 9);
        modifyMap.put(URoComponentType.ENVIRONMENTSENSOR, 11);
        modifyMap.put(URoComponentType.BRIGHTNESSSENSOR, 12);
        modifyMap.put(URoComponentType.SOUNDSENSOR, 13);
        modifyMap.put(URoComponentType.MOTOR, 10);
        executionMap = new HashMap<>();
        executionMap.put(URoComponentType.SERVOS, -1);
        executionMap.put(URoComponentType.INFRAREDSENSOR, 1);
        executionMap.put(URoComponentType.TOUCHSENSOR, 2);
        executionMap.put(URoComponentType.ULTRASOUNDSENSOR, 6);
        executionMap.put(URoComponentType.LED, 4);
        executionMap.put(URoComponentType.SPEAKER, 8);
        executionMap.put(URoComponentType.COLORSENSOR, 9);
        executionMap.put(URoComponentType.ENVIRONMENTSENSOR, 11);
        executionMap.put(URoComponentType.BRIGHTNESSSENSOR, 12);
        executionMap.put(URoComponentType.SOUNDSENSOR, 13);
        executionMap.put(URoComponentType.MOTOR, 14);
    }

    public static Integer getModifyValue(URoComponentType componentType) {
        return modifyMap.get(componentType);
    }

    public static Integer getExecutionValue(URoComponentType componentType) {
        return executionMap.get(componentType);
    }

    public static URoComponentType getTypeByValue(int value) {
        for(Map.Entry<URoComponentType, Integer> entry : executionMap.entrySet()) {
            if(entry.getValue() == value) {
                return entry.getKey();
            }
        }
        URoLogUtils.e("Cannot find ComponentType for %d", value);
        return null;
    }

    public static int getSensorDataLength(URoComponentType componentType) {
        if(componentType == null) {
            return 2;
        }
        switch (componentType) {
        case ENVIRONMENTSENSOR:
            return 4;
        case COLORSENSOR:
            return 11;
        case TOUCHSENSOR:
            return 1;
        case SERVOS:
        case LED:
        case SPEAKER:
        case MOTOR:
            return 0;
        default:
            return 2;
        }

    }

}
