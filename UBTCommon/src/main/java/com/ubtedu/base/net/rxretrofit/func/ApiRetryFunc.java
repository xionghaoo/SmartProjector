package com.ubtedu.base.net.rxretrofit.func;

import com.ubtedu.base.net.rxretrofit.exception.RxException;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;


/**
 * @Description: 重试机制
 * @author: qinicy
 * @date: 2017-05-04 17:19
 */
public class ApiRetryFunc implements Function<Observable<? extends Throwable>, Observable<?>> {

    private final int maxRetries;
    private final int retryDelayMillis;
    private int retryCount;

    public ApiRetryFunc(int maxRetries, int retryDelayMillis) {
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
    }

    @Override
    public Observable<?> apply(Observable<? extends Throwable> observable) {
        return observable
                .flatMap(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Throwable throwable) {
                        if (++retryCount <= maxRetries) {

                            return Observable.timer(retryDelayMillis, TimeUnit.MILLISECONDS);
                        }
                        return Observable.error(RxException.handleException(throwable));
                    }

                });
    }
}
