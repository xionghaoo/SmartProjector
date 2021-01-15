package com.ubtedu.deviceconnect.libs.base.component;

import android.util.ArrayMap;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionCallbackHelper;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocation;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationNames;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationParamKeys;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;

/**
 * @Author naOKi
 * @Date 2019/09/09
 **/
public class URoServos extends URoComponent {

    public URoServos(int componentId, String componentName, String version, URoProduct product) {
        super(componentId, componentName, version, product);
        componentType = URoComponentType.SERVOS;
    }

    public boolean turnTo(int angle, int time, URoCompletionCallback<Void> callback) {
        ArrayMap<Integer, Integer> idAnglePairs = new ArrayMap<>();
        idAnglePairs.put(componentId, angle);
        return product.turnServos(idAnglePairs, time, callback);
    }

    public boolean rotate(int speed, int time, URoCompletionCallback<Void> callback) {
        return product.rotateServos(new int[]{componentId}, speed, time, callback);
    }

    public boolean readback(boolean powerOff, URoCompletionCallback<Integer> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_SERVOS_READBACK);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID, componentId);
        invocation.setParameter(URoInvocationParamKeys.Servos.PARAM_KEY_READBACK_POWEROFF, powerOff);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(product);
    }

    public boolean stop(URoCompletionCallback<Void> callback) {
        return product.stopServos(new int[]{componentId}, callback);
    }

//    public boolean readOffset(boolean isPowerOff, URoCompletionCallback<Integer> callback) {
//        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_SERVOS_READ_OFFSET);
//        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID, componentId);
//        invocation.setParameter(URoInvocationParamKeys.Servos.PARAM_KEY_READBACK_POWEROFF, isPowerOff);
//        invocation.setCompletionCallback(callback);
//        return invocation.sendToTarget(product);
//    }

//    public boolean setOffset(int offset, URoCompletionCallback<Void> callback) {
//        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_SERVOS_SET_OFFSET);
//        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID, componentId);
//        invocation.setParameter(URoInvocationParamKeys.Servos.PARAM_KEY_OFFSET, offset);
//        invocation.setCompletionCallback(callback);
//        return invocation.sendToTarget(product);
//    }

    public boolean readVersion(URoCompletionCallback<String> callback) {
//        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_SERVOS_READ_VERSION);
//        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID, componentId);
//        invocation.setCompletionCallback(callback);
//        return invocation.sendToTarget(product);
        URoCompletionCallbackHelper.sendSuccessCallback(version, callback);
        return true;
    }

}
