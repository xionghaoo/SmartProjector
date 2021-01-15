package com.ubtedu.deviceconnect.libs.base.mission;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.product.URoProduct;

/**
 * @Author naOKi
 * @Date 2019/08/19
 **/
public abstract class URoProductInitMission extends URoCommandMission<Void> {

    public URoProductInitMission(@NonNull URoProduct product) {
        super(product);
    }

}
