package com.ubtedu.base.net.rxretrofit.strategy;

import com.ubtedu.base.gson.GSONUtil;
import com.ubtedu.base.net.rxretrofit.core.ApiCache;
import com.ubtedu.base.net.rxretrofit.mode.CacheResult;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


/**
 * @Description: 缓存策略
 * @author: qinicy
 * @date: 17/5/2 14:28.
 */
public abstract class CacheStrategy<T> implements ICacheStrategy<T> {
    <T> Observable<CacheResult<T>> loadCache(final ApiCache apiCache, final String key, final Class<T>
            clazz) {
        return apiCache.<T>get(key).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return s != null;
            }

        }).map(new Function<String, CacheResult<T>>() {
            @Override
            public CacheResult<T> apply(String s) {
                T t = GSONUtil.gson().fromJson(s, clazz);
                return new CacheResult<T>(true, t);
            }
        });
    }

    <T> Observable<CacheResult<T>> loadRemote(final ApiCache apiCache, final String key, Observable<T> source) {
        return source.map(new Function<T, CacheResult<T>>() {
            @Override
            public CacheResult<T> apply(T t) {
                apiCache.put(key, t).subscribeOn(Schedulers.io()).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean status) {
                    }
                });
                return new CacheResult<T>(false, t);
            }

        });
    }
}
