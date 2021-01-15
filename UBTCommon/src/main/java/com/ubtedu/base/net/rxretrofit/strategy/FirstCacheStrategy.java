package com.ubtedu.base.net.rxretrofit.strategy;

import com.ubtedu.base.net.rxretrofit.core.ApiCache;
import com.ubtedu.base.net.rxretrofit.mode.CacheResult;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


/**
 * @Description: 缓存策略--优先缓存
 * @author: qinicy
 * @date: 17/5/2 14:31.
 */
public class FirstCacheStrategy<T> extends CacheStrategy<T> {
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
        return Observable
                .concat(cache, remote).filter(new Predicate<CacheResult<T>>() {
                    @Override
                    public boolean test(CacheResult<T> tCacheResult) {
                        return tCacheResult != null && tCacheResult.getCacheData() != null;
                    }
                })
                .firstElement()
                .toObservable();
    }
}
