package com.ubtedu.deviceconnect.libs.base.component;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.model.URoColor;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;

import java.util.ArrayList;

/**
 * @Author naOKi
 * @Date 2019/09/09
 **/
public class URoLed extends URoComponent {

    public URoLed(int componentId, String componentName, String version, URoProduct product) {
        super(componentId, componentName, version, product);
        componentType = URoComponentType.LED;
    }

    public boolean setColor(ArrayList<URoColor> colors, long time, URoCompletionCallback<Void> callback) {
        return product.setLEDColor(new int[]{componentId}, colors, time, callback);
    }

    public boolean showEffects(int effectID, URoColor color, int times, URoCompletionCallback<Void> callback) {
        return product.setLEDEffect(effectID, new int[]{componentId}, color, times, callback);
    }

}
