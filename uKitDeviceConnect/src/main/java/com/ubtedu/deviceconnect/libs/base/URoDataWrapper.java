package com.ubtedu.deviceconnect.libs.base;

/**
 * @Author naOKi
 * @Date 2019/09/23
 **/
public class URoDataWrapper<T> {
    private final T data;

    public URoDataWrapper(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
