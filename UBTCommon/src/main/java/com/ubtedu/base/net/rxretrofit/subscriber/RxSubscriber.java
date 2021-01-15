package com.ubtedu.base.net.rxretrofit.subscriber;

import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.mode.ApiCode;

import io.reactivex.Observer;


/**
 * @Description: API统一订阅者，采用弱引用管理上下文，防止内存泄漏
 * @author: qinicy
 * @date: 17/5/5 14:07
 */
public  abstract class RxSubscriber<T> implements Observer<T> {

    @Override
    public void onError(Throwable e) {
        if (e instanceof RxException) {
            onError((RxException) e);
        } else {
            onError(new RxException(e, ApiCode.Request.UNKNOWN));
        }
    }

    public abstract void onError(RxException e);
}
