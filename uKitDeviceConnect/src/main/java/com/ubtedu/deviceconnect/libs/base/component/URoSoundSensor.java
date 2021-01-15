package com.ubtedu.deviceconnect.libs.base.component;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocation;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationNames;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationParamKeys;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoSoundCalibrateType;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;

/**
 * @Author naOKi
 * @Date 2019/09/09
 **/
public class URoSoundSensor extends URoSensorComponent<Integer> {

    public URoSoundSensor(int componentId, String componentName, String version, URoProduct product) {
        super(componentId, componentName, version, product);
        componentType = URoComponentType.SOUNDSENSOR;
    }

    @Override
    public boolean read(URoCompletionCallback<Integer> callback) {
        return readInternal(callback);
    }

    public boolean calibrate(URoSoundCalibrateType soundCalibrateType, int value, URoCompletionCallback<Void> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_SENSOR_CALIBRATE_SOUND);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID, componentId);
        invocation.setParameter(URoInvocationParamKeys.Sensor.PARAM_KEY_SOUND_CALIBRATE_TYPE, soundCalibrateType);
        invocation.setParameter(URoInvocationParamKeys.Sensor.PARAM_KEY_CALIBRATE_VALUE, value);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(product);
    }

    @Deprecated
    public Integer getAdcValue() {
        return getSensorValue();
    }

}
