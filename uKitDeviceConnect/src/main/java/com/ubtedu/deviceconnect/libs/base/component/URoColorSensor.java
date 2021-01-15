package com.ubtedu.deviceconnect.libs.base.component;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocation;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationNames;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationParamKeys;
import com.ubtedu.deviceconnect.libs.base.model.URoColor;
import com.ubtedu.deviceconnect.libs.base.model.URoColorSensorCalibrateStep;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;

/**
 * @Author naOKi
 * @Date 2019/09/09
 **/
public class URoColorSensor extends URoSensorComponent<URoColor> {

    private URoColor color;

    public URoColorSensor(int componentId, String componentName, String version, URoProduct product) {
        super(componentId, componentName, version, product);
        componentType = URoComponentType.COLORSENSOR;
    }

    @Override
    public boolean read(URoCompletionCallback<URoColor> callback) {
        return readInternal(callback);
    }

    public boolean calibrate(URoCompletionCallback<URoColorSensorCalibrateStep> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_SENSOR_CALIBRATE_COLOR);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID, componentId);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(product);
    }

    @Deprecated
    public URoColor getColorValue() {
        return getSensorValue();
    }

}
