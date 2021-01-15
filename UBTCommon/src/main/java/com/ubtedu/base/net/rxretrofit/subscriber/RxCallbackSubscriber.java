package com.ubtedu.base.net.rxretrofit.subscriber;

import com.ubtedu.base.net.rxretrofit.callback.ApiCallback;
import com.ubtedu.base.net.rxretrofit.exception.RxException;

import io.reactivex.disposables.Disposable;

/**
 * @Description: 包含回调的订阅者，如果订阅这个，上层在不使用订阅者的情况下可获得回调
 * @author: qinicy
 * @date: 17/5/5 09:35
 */
public class RxCallbackSubscriber<T> extends RxSubscriber<T> {

    protected ApiCallback<T> callBack;

    public RxCallbackSubscriber(ApiCallback<T> callBack) {
        if (callBack == null) {
            throw new NullPointerException("this callback is null!");
        }
        this.callBack = callBack;
    }

    @Override
    public void onError(RxException e) {
        callBack.onError(e);
    }

    @Override
    public void onSubscribe(Disposable d) {
        callBack.onSubscribe(d);
    }

    @Override
    public void onNext(T t) {
        callBack.onNext(t);
    }

    @Override
    public void onComplete() {
        callBack.onComplete();
    }
}
