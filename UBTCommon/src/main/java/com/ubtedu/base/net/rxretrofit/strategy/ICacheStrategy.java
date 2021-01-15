package com.ubtedu.base.net.rxretrofit.strategy;

import com.ubtedu.base.net.rxretrofit.core.ApiCache;
import com.ubtedu.base.net.rxretrofit.mode.CacheResult;

import io.reactivex.Observable;


/**
 * @Description: 缓存策略接口
 * @author: qinicy
 * @date: 17/5/2 14:21.
 */
public interface ICacheStrategy<T> {
    <T> Observable<CacheResult<T>> execute(ApiCache apiCache, String cacheKey, Observable<T> source, Class<T> clazz);
}
