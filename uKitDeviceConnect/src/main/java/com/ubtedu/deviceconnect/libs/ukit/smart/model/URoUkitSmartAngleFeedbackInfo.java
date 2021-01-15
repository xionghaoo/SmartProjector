package com.ubtedu.deviceconnect.libs.ukit.smart.model;

import com.ubtedu.deviceconnect.libs.base.model.URoAngleParam;
import com.ubtedu.deviceconnect.libs.base.model.URoUkitAngleFeedbackInfo;

import java.util.ArrayList;

/**
 * @Author naOKi
 * @Date 2018/11/12
 **/
public class URoUkitSmartAngleFeedbackInfo extends URoUkitAngleFeedbackInfo {

    private ArrayList<URoAngleParam> angleList = new ArrayList<>();
    private ArrayList<Integer> errorIds = new ArrayList<>();
    private ArrayList<Integer> expectIds = new ArrayList<>();

    public URoUkitSmartAngleFeedbackInfo(int[] ids) {
        super(null);
        this.errorIds.clear();
        this.expectIds.clear();
        for(int id : ids) {
            this.errorIds.add(id);
            this.expectIds.add(id);
        }
    }

    public boolean hasAllExpect() {
        return expectIds.isEmpty();
    }

    public void addServoAngle(Integer id, int angle, boolean isSuccess) {
//        URoLogUtils.e("Id: %d, Angle: %d, Result: %b", id, angle, isSuccess);
//        URoLogUtils.e("AngleList: %s, ErrorIds: %s, ExpectIds: %s", angleList, errorIds, expectIds);
        if(!expectIds.contains(id)) {
            return;
        }
        expectIds.remove(id);
        if(isSuccess) {
            errorIds.remove(id);
            angleList.add(new URoAngleParam(id, angle));
        }
    }

    @Override
    public Integer[] getErrorIds() {
        return errorIds.toArray(new Integer[errorIds.size()]);
    }

    @Override
    public URoAngleParam[] getAngles() {
        return angleList.toArray(new URoAngleParam[angleList.size()]);
    }

    @Override
    public void setErrorIds(Integer[] errorIds) {
        // do nothing
    }

}
