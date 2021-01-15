package com.ubtedu.base.net.rxretrofit.func;

import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.mode.ApiResult;

import io.reactivex.Observable;
import io.reactivex.functions.Function;


/**
 * Created by qinicy on 2017/5/7.
 */

public class ApiResultHandleFunc<T> implements Function<ApiResult<T>, Observable<T>> {

    @Override
    public Observable<T> apply(ApiResult<T> result) {
        if (RxException.isSuccess(result)) {
            return Observable.just(result.data);
        } else {
            RxException e = new RxException(new Throwable(result.msg), result.code);
            return Observable.error(e);
        }
    }
}
