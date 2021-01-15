package com.ubtedu.ukit.bluetooth;

import android.util.ArrayMap;

import com.ubtedu.deviceconnect.libs.base.interfaces.URoMissionCallback;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocation;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocationSequence;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocationSequence.URoInvocationParameter;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationNames;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationParamKeys;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.ukit.bluetooth.processor.PeripheralErrorCollector;

public abstract class ErrorCollectSequenceCallback extends URoMissionCallback {
    public ErrorCollectSequenceCallback(URoInvocationSequence invocationSequence) {
        this.invocationSequence = invocationSequence;
    }

    private URoInvocationSequence invocationSequence;

    @Override
    public void onMissionNextStep(int currentStep, int totalStep) {
        if (currentStep >= invocationSequence.getInvokes().size()) {
            return;
        }
        URoInvocationSequence.URoInvokeParameter uRoInvokeParameter = invocationSequence.getInvokes().get(currentStep);
        if (!(uRoInvokeParameter instanceof URoInvocationParameter)) {
            return;
        }
        URoInvocation parameter = ((URoInvocationParameter) uRoInvokeParameter).getInvocation();
        if ((!parameter.getInvocationName().equals(URoInvocationNames.INVOCATION_BATCH_SERVOS_ROTATE)) && (!parameter.getInvocationName().equals(URoInvocationNames.INVOCATION_BATCH_SERVOS_TURN_SIMULTANEOUS)) && (!parameter.getInvocationName().equals(URoInvocationNames.INVOCATION_SERVOS_ROTATE)) && (!parameter.getInvocationName().equals(URoInvocationNames.INVOCATION_SERVOS_TURN))) {
            return;
        }

        if (parameter.getInvocationName().equals(URoInvocationNames.INVOCATION_SERVOS_TURN) || parameter.getInvocationName().equals(URoInvocationNames.INVOCATION_BATCH_SERVOS_TURN_SIMULTANEOUS)) {
            ArrayMap<Integer, Integer> idMap = parameter.getParameter(URoInvocationParamKeys.Servos.PARAM_KEY_TURN_ID_ANGLE_PAIR, null);
            if (idMap == null || idMap.size() == 0) {
                return;
            }
            for (Integer id : idMap.keySet()) {
                PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.SERVOS, id);
            }
        } else {
            int[] idArray = parameter.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, null);
            if (idArray == null || idArray.length == 0) {
                return;
            }
            for (int i = 0; i < idArray.length; i++) {
                PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.SERVOS, idArray[i]);
            }
        }
    }
}
