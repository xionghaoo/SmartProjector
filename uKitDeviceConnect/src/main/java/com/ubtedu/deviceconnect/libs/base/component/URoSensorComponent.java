package com.ubtedu.deviceconnect.libs.base.component;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoConverter;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocation;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationNames;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationParamKeys;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentID;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;

/**
 * @Author naOKi
 * @Date 2019/09/09
 **/
public abstract class URoSensorComponent<T> extends URoComponent {

    private URoSensorData<T> sensorData;

    public URoSensorComponent(int componentId, String componentName, String version, URoProduct product) {
        super(componentId, componentName, version, product);
    }

    public abstract boolean read(URoCompletionCallback<T> callback);

    protected boolean readInternal(URoCompletionCallback<T> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_SENSOR_READ);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID, new URoComponentID(componentType, componentId));
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(product);
    }

    public T getSensorValue() {
        return sensorData != null ? sensorData.getDataValue() : null;
    }

    public void setSensorValue(T dataValue) {
        this.sensorData = new URoSensorData<>(dataValue);
    }

    public <S> void setSensorValueByConverter(URoConverter<S,T> converter) {
        if(converter == null) {
            return;
        }
        this.sensorData = new URoSensorData<>(converter.getResult());
    }

    private class URoSensorData<T> {
        private T dataValue;
        private long dataSetMs;
        public URoSensorData(T dataValue) {
            this.dataValue = dataValue;
            this.dataSetMs = System.currentTimeMillis();
        }

        public boolean isValid() {
            return System.currentTimeMillis() - dataSetMs < 1100;
        }

        public T getDataValue() {
            return isValid() ? dataValue : null;
        }
    }

}
