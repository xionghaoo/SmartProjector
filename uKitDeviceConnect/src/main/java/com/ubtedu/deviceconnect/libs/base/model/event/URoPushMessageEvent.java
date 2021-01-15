package com.ubtedu.deviceconnect.libs.base.model.event;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;

/**
 * @Author naOKi
 * @Date 2019/09/02
 **/
public class URoPushMessageEvent<T> {

    public final URoLinkModel linkModel;
    public final URoPushMessageType type;
    public final int subType;
    public final URoPushMessageData<T> data;

    public URoPushMessageEvent(@NonNull URoLinkModel linkModel, @NonNull URoPushMessageType type, T data) {
        this(linkModel, type, 0, data);
    }

    public URoPushMessageEvent(@NonNull URoLinkModel linkModel, @NonNull URoPushMessageType type, int subType, T data) {
        this.linkModel = linkModel;
        this.type = type;
        this.subType = subType;
        this.data = new URoPushMessageData<>(data);
    }

}
