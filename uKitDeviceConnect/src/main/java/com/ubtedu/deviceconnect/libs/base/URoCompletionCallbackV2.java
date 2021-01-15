package com.ubtedu.deviceconnect.libs.base;

import com.ubtedu.deviceconnect.libs.base.model.URoError;

/**
 * @Author naOKi
 * @Date 2018/11/06
 **/
public abstract class URoCompletionCallbackV2<T> implements URoCompletionCallback<T> {

    public final void onComplete(URoCompletionResult<T> result) {
        if(result.isSuccess()) {
            onSuccess(result.getData());
        } else {
            onError(result.getError());
        }
    }

    public abstract void onSuccess(T result);
    public abstract void onError(URoError error);

}
