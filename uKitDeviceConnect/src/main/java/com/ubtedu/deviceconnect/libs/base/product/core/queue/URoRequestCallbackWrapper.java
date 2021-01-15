package com.ubtedu.deviceconnect.libs.base.product.core.queue;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;

/**
 * @Author naOKi
 * @Date 2018/11/06
 **/
public class URoRequestCallbackWrapper<T> implements URoCompletionCallback<URoResponse<T>> {

    private URoRequestCallback<T> callback;

    public URoRequestCallbackWrapper(URoRequestCallback<T> callback) {
        this.callback = callback;
    }

    @Override
    public final void onComplete(URoCompletionResult<URoResponse<T>> result) {
        URoResponse<T> data = result.getData();
        if (callback != null) {
            callback.onRequestResult(data);
        }
    }

}
