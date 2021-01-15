package com.ubtedu.deviceconnect.libs.base.product.core.queue;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionCallbackHelper;
import com.ubtedu.deviceconnect.libs.base.model.URoError;

/**
 * @Author naOKi
 * @Date 2018/11/06
 **/
public class URoRequestCompletionCallback<T> implements URoRequestCallback<T> {

    private URoCompletionCallback<T> completionCallback;

    public URoRequestCompletionCallback(URoCompletionCallback<T> completionCallback) {
        this.completionCallback = completionCallback;
    }

    @Override
    public void onRequestResult(URoResponse<T> result) {
        if(completionCallback == null) {
            return;
        }
        if(result.isAborted()) {
            URoCompletionCallbackHelper.sendErrorCallback(URoError.ABORT, completionCallback);
            return;
        }
        if(result.isSuccess()) {
            URoCompletionCallbackHelper.sendSuccessCallback(result.getData(), completionCallback);
        } else {
            URoError error;
            if(result.getErrorCode() == URoError.UkitError.PT_ERR_OVERTIME) {
                error = URoError.TIMEOUT;
            } else {
                error = URoError.UNKNOWN;
            }
            URoCompletionCallbackHelper.sendErrorCallback(error, completionCallback);
        }
    }

}
