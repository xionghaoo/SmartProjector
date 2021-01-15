package com.ubtedu.deviceconnect.libs.base.component;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;

/**
 * @Author naOKi
 * @Date 2019/09/09
 **/
public class URoInfraredSensor extends URoSensorComponent<Integer> {

    public URoInfraredSensor(int componentId, String componentName, String version, URoProduct product) {
        super(componentId, componentName, version, product);
        componentType = URoComponentType.INFRAREDSENSOR;
    }

    @Override
    public boolean read(URoCompletionCallback<Integer> callback) {
        return readInternal(callback);
    }

    @Deprecated
    public Integer getDistanceValue() {
        return getSensorValue();
    }

}
