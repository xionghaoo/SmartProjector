package com.ubtedu.deviceconnect.libs.base.model.event;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.model.URoComponentID;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;

/**
 * @Author naOKi
 * @Date 2019/09/02
 **/
public class URoComponentErrorEvent {

    public final URoLinkModel linkModel;
    public final URoComponentID componentId;
    public final URoError error;

    public URoComponentErrorEvent(@NonNull URoLinkModel linkModel, @NonNull URoComponentType componentType, int id, @NonNull URoError error) {
        this(linkModel, new URoComponentID(componentType, id), error);
    }

    public URoComponentErrorEvent(@NonNull URoLinkModel linkModel, @NonNull URoComponentID componentId, @NonNull URoError error) {
        this.linkModel = linkModel;
        this.componentId = componentId;
        this.error = error;
    }

}
