package com.ubtedu.deviceconnect.libs.base;

import com.ubtedu.deviceconnect.libs.base.interfaces.URoConverterDelegate;
import com.ubtedu.deviceconnect.libs.base.model.URoError;

/**
 * @Author naOKi
 * @Date 2018/11/06
 **/
public abstract class URoCompletionConverterCallback<S, T> implements URoCompletionCallback<S>, URoConverterDelegate<S, T> {

    private URoCompletionCallback<T> callback;
    private boolean alwaysExeConvert = false;

    public URoCompletionConverterCallback(URoCompletionCallback<T> callback) {
        this.callback = callback;
    }

    public URoCompletionConverterCallback(URoCompletionCallback<T> callback, boolean alwaysExeConvert) {
        this.callback = callback;
        this.alwaysExeConvert = alwaysExeConvert;
    }

    @Override
    public void onComplete(URoCompletionResult<S> result) {
        try {
            if (callback != null || alwaysExeConvert) {
                S source = result.getData();
                T target = null;
                if (source != null) {
                    target = convert(source);
                }
                if (callback != null) {
                    callback.onComplete(new URoCompletionResult<>(target, result.getError()));
                }
            }
        } catch (Throwable e) {
            URoCompletionCallbackHelper.sendErrorCallback(URoError.UNKNOWN, callback);
        }
    }

}
