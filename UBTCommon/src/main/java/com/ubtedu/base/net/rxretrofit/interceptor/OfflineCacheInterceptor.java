package com.ubtedu.base.net.rxretrofit.interceptor;

import android.content.Context;

import com.ubtedu.base.common.FrameworkConfig;
import com.ubtedu.base.net.manager.NetworkManager;

import java.io.IOException;
import java.util.Locale;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Description: 离线缓存拦截
 * @author: qinicy
 * @date: 17/5/2 22:36.
 */
public class OfflineCacheInterceptor implements Interceptor {
    private Context context;
    private String cacheControlValue;

    public OfflineCacheInterceptor(Context context) {
        this(context, FrameworkConfig.MAX_AGE_OFFLINE);
    }

    public OfflineCacheInterceptor(Context context, int cacheControlValue) {
        this.context = context;
        this.cacheControlValue = String.format(Locale.US,"max-stale=%d", cacheControlValue);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!NetworkManager.getInstance().isNetworkConnected()) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
            Response response = chain.proceed(request);
            return response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, " + cacheControlValue)
                    .removeHeader("Pragma")
                    .build();
        }
        return chain.proceed(request);
    }
}
