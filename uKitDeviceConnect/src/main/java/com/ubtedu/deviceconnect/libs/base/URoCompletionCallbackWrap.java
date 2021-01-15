package com.ubtedu.deviceconnect.libs.base;

import com.ubtedu.deviceconnect.libs.base.model.URoError;

/**
 * @Author naOKi
 * @Date 2018/11/06
 **/
public abstract class URoCompletionCallbackWrap<T> implements URoCompletionCallback<T> {

    private URoCompletionCallback callback;

    public URoCompletionCallbackWrap(URoCompletionCallback callback) {
        this.callback = callback;
    }

    public abstract boolean onCompleteInternal(URoCompletionResult<T> result);

    protected void sendCallbackDirectly(URoCompletionResult<T> result) {
        URoCompletionCallbackHelper.sendCallback(result, callback);
    }

    @Override
    public void onComplete(URoCompletionResult<T> result) {
        if(!onCompleteInternal(result)) {
            URoCompletionCallbackHelper.sendCallback(new URoCompletionResult(result.getData(), URoError.UNKNOWN), callback);
        } else {
            URoCompletionCallbackHelper.sendCallback(result, callback);
        }
    }

}
