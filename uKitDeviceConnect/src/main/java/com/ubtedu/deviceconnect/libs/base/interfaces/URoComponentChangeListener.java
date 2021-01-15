package com.ubtedu.deviceconnect.libs.base.interfaces;

import com.ubtedu.deviceconnect.libs.base.component.URoComponent;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;

import java.util.ArrayList;

/**
 * @Author naOKi
 * @Date 2018/11/06
 **/
public interface URoComponentChangeListener {
    void onComponentChanged(URoProduct product, ArrayList<URoComponent> components);
}
