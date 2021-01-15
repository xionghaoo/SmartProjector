package com.ubtedu.deviceconnect.libs.base.component;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoTouchSensorValue;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;

/**
 * @Author naOKi
 * @Date 2019/09/09
 **/
public class URoTouchSensor extends URoSensorComponent<URoTouchSensorValue> {

    public URoTouchSensor(int componentId, String componentName, String version, URoProduct product) {
        super(componentId, componentName, version, product);
        componentType = URoComponentType.TOUCHSENSOR;
    }

    @Override
    public boolean read(URoCompletionCallback<URoTouchSensorValue> callback) {
        return readInternal(callback);
    }

    @Deprecated
    public URoTouchSensorValue getStateValue() {
        return getSensorValue();
    }

}
