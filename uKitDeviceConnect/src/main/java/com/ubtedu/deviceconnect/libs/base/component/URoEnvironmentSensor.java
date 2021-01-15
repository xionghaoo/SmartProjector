package com.ubtedu.deviceconnect.libs.base.component;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoHumitureValue;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;

/**
 * @Author naOKi
 * @Date 2019/09/09
 **/
public class URoEnvironmentSensor extends URoSensorComponent<URoHumitureValue> {

    public URoEnvironmentSensor(int componentId, String componentName, String version, URoProduct product) {
        super(componentId, componentName, version, product);
        componentType = URoComponentType.ENVIRONMENTSENSOR;
    }

    @Override
    public boolean read(URoCompletionCallback<URoHumitureValue> callback) {
        return readInternal(callback);
    }

    @Deprecated
    public Integer getHumidityValue() {
        URoHumitureValue humitureValue = getSensorValue();
        return humitureValue != null ? humitureValue.getHumidity() : null;
    }

    @Deprecated
    public Float getTemperatureValue() {
        URoHumitureValue humitureValue = getSensorValue();
        return humitureValue != null ? humitureValue.getTemperature() : null;
    }

}
