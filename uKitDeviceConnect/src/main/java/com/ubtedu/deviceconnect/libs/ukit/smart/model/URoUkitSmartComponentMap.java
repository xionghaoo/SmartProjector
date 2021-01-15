package com.ubtedu.deviceconnect.libs.ukit.smart.model;

import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author naOKi
 * @Date 2019/09/23
 **/
public class URoUkitSmartComponentMap {

    private URoUkitSmartComponentMap() {}

    private static final HashMap<URoComponentType, Integer> executionMap;

    static {
        executionMap = new HashMap<>();
        executionMap.put(URoComponentType.SERVOS, 0x3);
        executionMap.put(URoComponentType.INFRAREDSENSOR, 0x5);
        executionMap.put(URoComponentType.TOUCHSENSOR, 0xb);
        executionMap.put(URoComponentType.ULTRASOUNDSENSOR, 0x9);
        executionMap.put(URoComponentType.LED, 0x8);
        executionMap.put(URoComponentType.SPEAKER, 0x17);
        executionMap.put(URoComponentType.COLORSENSOR, 0xd);
        executionMap.put(URoComponentType.ENVIRONMENTSENSOR, 0x6);
        executionMap.put(URoComponentType.BRIGHTNESSSENSOR, 0x11);
        executionMap.put(URoComponentType.SOUNDSENSOR, 0x7);
        executionMap.put(URoComponentType.MOTOR, 0x4);
        executionMap.put(URoComponentType.LED_BELT, 0x1A);
        executionMap.put(URoComponentType.LCD, 0x1B);
        executionMap.put(URoComponentType.VISION, 0x20);
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
        return null;
    }

}
