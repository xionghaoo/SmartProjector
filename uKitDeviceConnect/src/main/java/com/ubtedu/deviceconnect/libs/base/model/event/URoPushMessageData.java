package com.ubtedu.deviceconnect.libs.base.model.event;

/**
 * @Author naOKi
 * @Date 2019/10/17
 **/
public class URoPushMessageData<T> {
    private T value;

    URoPushMessageData(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
