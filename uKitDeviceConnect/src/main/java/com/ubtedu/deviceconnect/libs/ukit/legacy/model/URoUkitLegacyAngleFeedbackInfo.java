package com.ubtedu.deviceconnect.libs.ukit.legacy.model;

import com.ubtedu.deviceconnect.libs.base.model.URoAngleParam;
import com.ubtedu.deviceconnect.libs.base.model.URoUkitAngleFeedbackInfo;

import java.util.ArrayList;

/**
 * @Author naOKi
 * @Date 2018/11/12
 **/
public class URoUkitLegacyAngleFeedbackInfo extends URoUkitAngleFeedbackInfo {

    protected final static int BASE_ANGLE_VALUE = 120;

    public URoUkitLegacyAngleFeedbackInfo(URoAngleParam[] angles) {
        super(angles);
    }

    public URoUkitLegacyAngleFeedbackInfo(byte[] bizData) {
        super(null);
        ArrayList<URoAngleParam> list = new ArrayList<>();
        for(int i = 0; i < bizData.length; i += 6) {
            if(bizData[i + 1] != (byte)0xAA) {
                continue;
            }
            int id = bizData[i];
            int angle = (bizData[i + 4] & 0xFF) << 8 | (bizData[i + 5] & 0xFF);
            list.add(new URoAngleParam(id, angle - BASE_ANGLE_VALUE));
        }
        angles = list.toArray(new URoAngleParam[list.size()]);
    }

}
