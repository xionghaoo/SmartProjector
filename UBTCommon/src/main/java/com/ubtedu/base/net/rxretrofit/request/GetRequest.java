package com.ubtedu.base.net.rxretrofit.request;

import com.ubtedu.base.net.rxretrofit.callback.ApiCallback;
import com.ubtedu.base.net.rxretrofit.mode.CacheResult;
import com.ubtedu.base.net.rxretrofit.subscriber.RxCallbackSubscriber;
import com.ubtedu.base.utils.ClassUtil;

import io.reactivex.Observable;


/**
 * @Description: Get请求
 * @author: qinicy
 * @date: 17/5/1 16:05
 */
public class GetRequest extends BaseRequest<GetRequest> {

    @Override
    protected <T> Observable<T> execute(Class<T> clazz) {
        return apiService.get(suffixUrl, params).compose(this.norTransformer(clazz));
    }

    @Override
    protected <T> Observable<CacheResult<T>> cacheExecute(Class<T> clazz) {
        return execute(clazz).compose(apiCache.transformer(cacheMode, clazz));
    }

    @Override
    protected <T> void execute(ApiCallback<T> apiCallback) {
        if (isLocalCache) {
            this.cacheExecute(ClassUtil.getTClass(apiCallback))
                    .subscribe(new RxCallbackSubscriber(apiCallback));
        }
        this.execute(ClassUtil.getTClass(apiCallback))
                .subscribe(new RxCallbackSubscriber(apiCallback));
    }
}
