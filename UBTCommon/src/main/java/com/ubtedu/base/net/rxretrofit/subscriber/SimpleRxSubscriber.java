package com.ubtedu.base.net.rxretrofit.subscriber;

import com.ubtedu.base.net.rxretrofit.exception.RxException;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * @author qinicy
 * @data 2017/9/5
 */

public class SimpleRxSubscriber<T> extends RxSubscriber<T> {
    @Override
    public void onError(RxException e) {

    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onNext(@NonNull T t) {

    }

    @Override
    public void onComplete() {

    }
}
