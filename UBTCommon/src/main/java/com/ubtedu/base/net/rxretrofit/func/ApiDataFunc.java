package com.ubtedu.base.net.rxretrofit.func;

import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.mode.ApiResult;

import io.reactivex.functions.Function;

/**
 * @Description: ApiResult<T>转T
 * @author: qinicy
 * @date: 17/5/417:55
 */
public class ApiDataFunc<T> implements Function<ApiResult<T>, T> {
    public ApiDataFunc() {
    }

    @Override
    public T apply(ApiResult<T> response) {
        if (RxException.isSuccess(response)) {
            return response.data;
        } else {
            return (T) new RxException(new Throwable(response.msg), response.code);
        }
    }
}
