package com.ubtedu.deviceconnect.libs.base.model;

/**
 * @Author naOKi
 * @Date 2018/11/12
 **/
public class URoUkitAngleFeedbackInfo {

    protected URoAngleParam[] angles;
    private Integer[] errorIds = new Integer[0];

    public URoUkitAngleFeedbackInfo(URoAngleParam[] angles) {
        this.angles = angles;
    }

    public URoAngleParam[] getAngles() {
        return angles;
    }

    public Integer[] getErrorIds() {
        return errorIds;
    }

    public void setErrorIds(Integer[] errorIds) {
        this.errorIds = errorIds;
    }

    @Override
    public String toString() {
        URoAngleParam[] angles = getAngles();
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for(URoAngleParam angle : angles) {
            sb.append("ID-").append(angle.id).append(":").append(angle.angle).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

}
