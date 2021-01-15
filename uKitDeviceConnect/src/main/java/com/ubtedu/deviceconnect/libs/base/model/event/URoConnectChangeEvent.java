package com.ubtedu.deviceconnect.libs.base.model.event;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;

/**
 * @Author naOKi
 * @Date 2019/09/02
 **/
public class URoConnectChangeEvent {

    public final URoLinkModel linkModel;
    public final URoConnectStatus status;

    public URoConnectChangeEvent(@NonNull URoLinkModel linkModel, @NonNull URoConnectStatus status) {
        this.linkModel = linkModel;
        this.status = status;
    }

}
