package com.ubtedu.deviceconnect.libs.base.model;

import java.util.HashMap;

/**
 * @Author naOKi
 * @Date 2019/09/11
 **/
public class URoSensorData {

    private HashMap<URoComponentID, Object> result;

    public URoSensorData(HashMap<URoComponentID, Object> result) {
        this.result = result;
    }

    public HashMap<URoComponentID, Object> getResult() {
        return result;
    }

}
