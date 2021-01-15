package com.ubtedu.deviceconnect.libs.base.model.event;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.component.URoComponent;
import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;

import java.util.ArrayList;

/**
 * @Author naOKi
 * @Date 2019/09/02
 **/
public class URoComponentChangeEvent {

    public final URoLinkModel linkModel;
    public final ArrayList<URoComponent> components;

    public URoComponentChangeEvent(@NonNull URoLinkModel linkModel, @NonNull ArrayList<URoComponent> components) {
        this.linkModel = linkModel;
        this.components = components;
    }

}
