package com.ubtedu.deviceconnect.libs.base;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.model.URoError;

/**
 * @Author naOKi
 * @Date 2018/11/06
 **/
public class URoCompletionResult<T> {

    private T data;
    private URoError error;

    public URoCompletionResult(T data, @NonNull URoError error) {
        this.data = data;
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public URoError getError() {
        return error;
    }

    public boolean isSuccess() {
        return error.isSuccess();
    }

}
