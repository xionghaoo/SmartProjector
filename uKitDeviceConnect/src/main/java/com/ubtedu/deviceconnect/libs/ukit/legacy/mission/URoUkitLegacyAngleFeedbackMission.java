package com.ubtedu.deviceconnect.libs.ukit.legacy.mission;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationParamKeys;
import com.ubtedu.deviceconnect.libs.base.mission.URoCommandMission;
import com.ubtedu.deviceconnect.libs.base.model.URoAngleParam;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommand;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommandConstants;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.legacy.model.URoUkitLegacyAngleFeedbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author naOKi
 * @Date 2019/09/17
 **/
public class URoUkitLegacyAngleFeedbackMission extends URoCommandMission<URoUkitLegacyAngleFeedbackInfo> {

    private int[] ids;
    private boolean powerOff;
    private ArrayList<URoAngleParam> angleParams;
    private List<Integer> expectIds;

    private final static long TIMEOUT_MS = 1000L;

    public URoUkitLegacyAngleFeedbackMission(@NonNull URoProduct product, int[] ids, boolean powerOff) {
        super(product);
        this.ids = ids;
        this.powerOff = powerOff;
        this.angleParams = new ArrayList<>();
        this.expectIds = new ArrayList<>();
    }

    @Override
    protected void onMissionStart() throws Throwable {
        int id;
        if(ids.length == 1) {
            id = ids[0];
        } else {
            id = 0;
        }
        angleParams.clear();
        expectIds.clear();
        for(int _id : ids) {
            expectIds.add(_id);
        }
        URoRequest request = new URoRequest(URoCommandConstants.CMD_STEERING_GEAR_ANGLE_FEEDBACK);
        request.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID, id);
        request.setParameter(URoInvocationParamKeys.Servos.PARAM_KEY_READBACK_POWEROFF, powerOff);
        performNext(URoCommandConstants.CMD_STEERING_GEAR_ANGLE_FEEDBACK, request);
        setupIntercept();
        resetTimeout(TIMEOUT_MS);
    }

    @Override
    protected void onMissionTimeout() {
        URoUkitLegacyAngleFeedbackInfo result = new URoUkitLegacyAngleFeedbackInfo(angleParams.toArray(new URoAngleParam[angleParams.size()]));
        result.setErrorIds(expectIds.toArray(new Integer[expectIds.size()]));
        for(Integer id : expectIds) {
            sendComponentError(URoComponentType.SERVOS, id, URoError.READ_ERROR);
        }
        notifyComplete(result);
    }

    @Override
    protected URoResponse onInterceptResponse(URoCommand cmd, URoResponse response) {
        if(!URoCommandConstants.CMD_STEERING_GEAR_ANGLE_FEEDBACK.equals(cmd)) {
            return null;
        }
        URoUkitLegacyAngleFeedbackInfo data = (URoUkitLegacyAngleFeedbackInfo)response.getData();
        URoAngleParam[] angles = data.getAngles();
        if(angles != null && angles.length != 0) {
            resetTimeout(TIMEOUT_MS);
            for (URoAngleParam angle : angles) {
                if(expectIds.contains(angle.id)) {
                    angleParams.add(angle);
                    expectIds.remove(Integer.valueOf(angle.id));
                    if(expectIds.isEmpty()) {
                        URoUkitLegacyAngleFeedbackInfo result = new URoUkitLegacyAngleFeedbackInfo(angleParams.toArray(new URoAngleParam[angleParams.size()]));
                        notifyComplete(result);
                        return null;
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void onPreviousResult(Object identity, URoCompletionResult result) throws Throwable {
        if (URoCommandConstants.CMD_STEERING_GEAR_ANGLE_FEEDBACK.equals(identity)) {
            resetTimeout(TIMEOUT_MS);
        }
    }

}
