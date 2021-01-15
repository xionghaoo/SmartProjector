package com.ubtedu.base.net.rxretrofit.callback;

import com.ubtedu.base.net.rxretrofit.exception.RxException;

import io.reactivex.disposables.Disposable;

/**
 * @Description: API操作回调
 * @author: qinicy
 * @date: 17/5/5 09:39
 */
public abstract class ApiCallback<T> {
    public abstract void onSubscribe(Disposable d);

    public abstract void onError(RxException e);

    public abstract void onComplete();

    public abstract void onNext(T t);
}
