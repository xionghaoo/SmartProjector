package com.ubtedu.deviceconnect.libs.base.component;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.model.URoColor;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;

/**
 * @Author naOKi
 * @Date 2019/09/09
 **/
public class URoUltrasoundSensor extends URoSensorComponent<Integer> {

    public URoUltrasoundSensor(int componentId, String componentName, String version, URoProduct product) {
        super(componentId, componentName, version, product);
        componentType = URoComponentType.ULTRASOUNDSENSOR;
    }

    @Override
    public boolean read(URoCompletionCallback<Integer> callback) {
        return readInternal(callback);
    }

    public boolean setColor(URoColor color, URoCompletionCallback<Void> callback) {
        return product.setUltrasoundColor(new int[]{componentId}, color, callback);
    }

    @Deprecated
    public Integer getDistanceValue() {
        return getSensorValue();
    }

}
