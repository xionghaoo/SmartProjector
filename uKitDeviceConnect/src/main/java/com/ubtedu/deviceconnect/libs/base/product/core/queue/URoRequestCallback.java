package com.ubtedu.deviceconnect.libs.base.product.core.queue;

/**
 * @Author naOKi
 * @Date 2018/11/06
 **/
public interface URoRequestCallback<T> {
    void onRequestResult(URoResponse<T> result);
}
