package com.ubtedu.base.net.rxretrofit.config;

import android.content.Context;

import com.ubtedu.base.net.rxretrofit.core.ApiCache;
import com.ubtedu.base.net.rxretrofit.request.GetRequest;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * @Description: 网络请求入口
 * @author: qinicy
 * @date: 17/5/1 15:07
 */
public class RxNet {
    private static RxNet rxNet;
    private static Context context;
    private static OkHttpClient.Builder okHttpBuilder;
    private static Retrofit.Builder retrofitBuilder;
    private static ApiCache.Builder apiCacheBuilder;

    private final NetGlobalConfig NET_GLOBAL_CONFIG = NetGlobalConfig.getInstance();

    private RxNet() {
    }

    public static RxNet getInstance() {
        if (rxNet == null) {
            synchronized (RxNet.class) {
                if (rxNet == null) {
                    rxNet = new RxNet();
                }
            }
        }
        return rxNet;
    }

    public static void init(Context appContext) {
        if (context == null && appContext != null) {
            context = appContext.getApplicationContext();
            okHttpBuilder = new OkHttpClient.Builder();
            retrofitBuilder = new Retrofit.Builder();
            apiCacheBuilder = new ApiCache.Builder(context);
        }
    }

    public static Context getContext() {
        if (context == null) {
            throw new IllegalStateException("Please call RxNet.init(this) in Application to initialize!");
        }
        return context;
    }

    public static OkHttpClient.Builder getOkHttpBuilder() {
        return okHttpBuilder;
    }

    public static Retrofit.Builder getRetrofitBuilder() {
        return retrofitBuilder;
    }

    public static ApiCache.Builder getApiCacheBuilder() {
        return apiCacheBuilder;
    }

    public NetGlobalConfig config() {
        return NET_GLOBAL_CONFIG;
    }

    public static GetRequest get() {
        return new GetRequest();
    }
}
