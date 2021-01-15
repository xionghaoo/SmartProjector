package com.ubtedu.base.net.rxretrofit.interceptor;

import android.text.TextUtils;

import com.ubtedu.base.common.FrameworkConfig;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @Description: 在线缓存拦截
 * @author: qinicy
 * @date: 17/5/2 22:23.
 */
public class OnlineCacheInterceptor implements Interceptor {
    private String cacheControlValue;

    public OnlineCacheInterceptor() {
        this(FrameworkConfig.MAX_AGE_ONLINE);
    }

    public OnlineCacheInterceptor(int cacheControlValue) {
        this.cacheControlValue = String.format(Locale.US,"max-age=%d", cacheControlValue);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        okhttp3.Response originalResponse = chain.proceed(chain.request());
        String cacheControl = originalResponse.header("Cache-Control");
        if (TextUtils.isEmpty(cacheControl) || cacheControl.contains("no-store") || cacheControl.contains("no-cache") || cacheControl
                .contains("must-revalidate") || cacheControl.contains("max-age") || cacheControl.contains("max-stale")) {
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, " + cacheControlValue)
                    .removeHeader("Pragma")
                    .build();

        } else {
            return originalResponse;
        }
    }
}
