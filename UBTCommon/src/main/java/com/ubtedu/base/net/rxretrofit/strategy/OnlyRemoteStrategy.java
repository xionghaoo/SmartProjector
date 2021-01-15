package com.ubtedu.base.net.rxretrofit.strategy;

import com.ubtedu.base.net.rxretrofit.core.ApiCache;
import com.ubtedu.base.net.rxretrofit.mode.CacheResult;

import io.reactivex.Observable;

/**
 * @Description: 缓存策略--只取网络
 * @author: qinicy
 * @date: 17/5/2 14:30.
 */
public class OnlyRemoteStrategy<T> extends CacheStrategy<T> {
    @Override
    public <T> Observable<CacheResult<T>> execute(ApiCache apiCache, String cacheKey, Observable<T> source, Class<T> clazz) {
        return loadRemote(apiCache, cacheKey, source);
    }
}
