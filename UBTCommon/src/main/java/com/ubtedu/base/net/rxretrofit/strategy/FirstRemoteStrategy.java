package com.ubtedu.base.net.rxretrofit.strategy;

import com.ubtedu.base.net.rxretrofit.core.ApiCache;
import com.ubtedu.base.net.rxretrofit.mode.CacheResult;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


/**
 * @Description: 缓存策略--优先网络
 * @author: qinicy
 * @date: 17/5/2 14:32.
 */
public class FirstRemoteStrategy<T> extends CacheStrategy<T> {
    @Override
    public <T> Observable<CacheResult<T>> execute(ApiCache apiCache, String cacheKey, Observable<T> source, Class<T> clazz) {
        Observable<CacheResult<T>> cache = loadCache(apiCache, cacheKey, clazz);
        cache.onErrorReturn(new Function<Throwable, CacheResult<T>>() {
            @Override
            public CacheResult<T> apply(Throwable throwable) {
                return null;
            }
        });
        Observable<CacheResult<T>> remote = loadRemote(apiCache, cacheKey, source);
        return Observable.concat(remote, cache)
                .filter(new Predicate<CacheResult<T>>() {
                    @Override
                    public boolean test(CacheResult<T> tCacheResult) {
                        return tCacheResult != null && tCacheResult.getCacheData() != null;
                    }
                })
                .firstElement()
                .toObservable();
    }
}
