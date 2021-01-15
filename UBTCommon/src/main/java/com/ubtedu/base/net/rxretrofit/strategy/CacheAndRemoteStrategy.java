package com.ubtedu.base.net.rxretrofit.strategy;

import com.ubtedu.base.net.rxretrofit.core.ApiCache;
import com.ubtedu.base.net.rxretrofit.mode.CacheResult;

import io.reactivex.Observable;
import io.reactivex.functions.Predicate;

/**
 * @Description: 缓存策略--缓存和网络
 * @author: qinicy
 * @date: 17/5/2 14:33.
 */
public class CacheAndRemoteStrategy<T> extends CacheStrategy<T> {
    @Override
    public <T> Observable<CacheResult<T>> execute(ApiCache apiCache, String cacheKey, Observable<T> source, final Class<T> clazz) {
        Observable<CacheResult<T>> cache = loadCache(apiCache, cacheKey, clazz);
        final Observable<CacheResult<T>> remote = loadRemote(apiCache, cacheKey, source);
        return Observable.concat(cache, remote).filter(new Predicate<CacheResult<T>>() {
            @Override
            public boolean test(CacheResult<T> tCacheResult) {
                return tCacheResult.getCacheData() != null;
            }
        });
    }
}
