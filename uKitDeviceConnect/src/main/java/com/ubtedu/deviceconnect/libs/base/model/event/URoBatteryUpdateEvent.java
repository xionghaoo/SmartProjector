package com.ubtedu.deviceconnect.libs.base.model.event;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.component.URoComponent;
import com.ubtedu.deviceconnect.libs.base.model.URoBatteryInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;

import java.util.ArrayList;

/**
 * @Author naOKi
 * @Date 2019/09/02
 **/
public class URoBatteryUpdateEvent {

    public final URoLinkModel linkModel;
    public final URoBatteryInfo batteryInfo;

    public URoBatteryUpdateEvent(@NonNull URoLinkModel linkModel, @NonNull URoBatteryInfo batteryInfo) {
        this.linkModel = linkModel;
        this.batteryInfo = batteryInfo;
    }

}
