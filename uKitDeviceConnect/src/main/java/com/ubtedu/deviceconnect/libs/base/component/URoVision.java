package com.ubtedu.deviceconnect.libs.base.component;

import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;

/**
 * @Author naOKi
 * @Date 2019/12/20
 **/
public class URoVision extends URoComponent {

    public URoVision(int componentId, String componentName, String version, URoProduct product) {
        super(componentId, componentName, version, product);
        componentType = URoComponentType.VISION;
    }

}
