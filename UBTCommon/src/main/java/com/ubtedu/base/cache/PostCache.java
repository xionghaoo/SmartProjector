package com.ubtedu.base.cache;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import okhttp3.Cache;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.cache.CacheRequest;
import okhttp3.internal.cache.CacheStrategy;
import okhttp3.internal.cache.InternalCache;

/**
 * Created by qinicy on 2017/8/14.
 */

public class PostCache implements Closeable, Flushable {
    private Cache mCache;
    public final InternalCache internalCache = new InternalCache() {
        @Override public Response get(Request request) {

            Response response = null;
            try {
                Request getRequest = request.newBuilder().build();
                Field methodName = getRequest.getClass().getDeclaredField("method");
                methodName.setAccessible(true);
                methodName.set(getRequest,"GET");
                Method get = mCache.getClass().getDeclaredMethod("get",Request.class);
                get.setAccessible(true);
                response = (Response) get.invoke(mCache,getRequest);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override public CacheRequest put(Response response) {
            CacheRequest cacheRequest = null;
            try {
                Response mirrorResponse = response.newBuilder().build();
                Request mirrorRequest =mirrorResponse.request();
                Field methodName = mirrorRequest.getClass().getDeclaredField("method");
                methodName.setAccessible(true);
                methodName.set(mirrorRequest,"GET");
                Method put = mCache.getClass().getDeclaredMethod("put",Response.class);
                put.setAccessible(true);
                cacheRequest = (CacheRequest) put.invoke(mCache,mirrorResponse);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            return cacheRequest;
        }

        @Override public void remove(Request request) {
            try {
                Method remove = mCache.getClass().getDeclaredMethod("remove",Request.class);
                remove.setAccessible(true);
                remove.invoke(mCache,request);

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        @Override public void update(Response cached, Response network) {

            try {
                Method update = mCache.getClass().getDeclaredMethod("update",Response.class,Response.class);
                update.setAccessible(true);
                update.invoke(mCache,cached,network);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        @Override public void trackConditionalCacheHit() {
            try {
                Method trackConditionalCacheHit = mCache.getClass().getDeclaredMethod("trackConditionalCacheHit");
                trackConditionalCacheHit.setAccessible(true);
                trackConditionalCacheHit.invoke(mCache);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        @Override public void trackResponse(CacheStrategy cacheStrategy) {
            try {
                Method trackResponse = mCache.getClass().getDeclaredMethod("trackResponse",CacheStrategy.class);
                trackResponse.setAccessible(true);
                trackResponse.invoke(mCache,cacheStrategy);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    };



    public PostCache(Cache cache) {
        mCache = cache;
    }


    @Override
    public void close() throws IOException {
        mCache.close();
    }

    @Override
    public void flush() throws IOException {
        mCache.flush();
    }
}
