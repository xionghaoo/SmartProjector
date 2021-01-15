package com.ubtedu.ukit.bluetooth.model;

import com.ubtedu.deviceconnect.libs.base.model.URoAngleParam;

import java.util.ArrayList;

public class AngleFeedback {

    protected final static int BASE_ANGLE_VALUE = 120;

    protected URoAngleParam[] angles;
    private Integer[] errorIds;

    public AngleFeedback(URoAngleParam[] angles) {
        this.angles = angles;
    }

    public AngleFeedback(byte[] bizData) {
    }

    public URoAngleParam[] getAngles() {
        return angles;
    }

    private Integer[] getAnomalyIds() {
        ArrayList<Integer> anomalyIds = new ArrayList<>();
        if(angles != null) {
            for (URoAngleParam angleParam : angles) {
                int angle = angleParam.angle + BASE_ANGLE_VALUE;
                if(angle < 2 || angle > 238) {
                    anomalyIds.add(angleParam.id);
                }
            }
        }
        return anomalyIds.toArray(new Integer[anomalyIds.size()]);
    }

    public String getAnomalyMsg() {
        StringBuilder sb = new StringBuilder();
        Integer[] anomalyIds = getAnomalyIds();
        boolean first = true;
        if(anomalyIds.length != 0) {
            for (Integer anomalyId : anomalyIds) {
                if (first) {
                    first = false;
                } else {
                    sb.append(",");
                }
                sb.append("ID-").append(anomalyId);
            }
        }
        return sb.toString();
    }

    public Integer[] getErrorIds() {
        return errorIds;
    }

    public void setErrorIds(Integer[] errorIds) {
        this.errorIds = errorIds;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for(URoAngleParam angle : angles) {
            sb.append("ID").append(angle.id).append(":").append(angle.angle).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

}
