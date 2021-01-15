package com.ubtedu.base.net.rxretrofit.func;

import com.ubtedu.base.net.rxretrofit.exception.RxException;

import io.reactivex.Observable;
import io.reactivex.functions.Function;


/**
 * @Description: Throwable转Observable<T>
 * @author: qinicy
 * @date: 17/5/5 16:00
 */
public class ApiErrFunc<T> implements Function<Throwable, Observable<T>> {

    @Override
    public Observable<T> apply(Throwable throwable) {
        return Observable.error(RxException.handleException(throwable));
    }
}
