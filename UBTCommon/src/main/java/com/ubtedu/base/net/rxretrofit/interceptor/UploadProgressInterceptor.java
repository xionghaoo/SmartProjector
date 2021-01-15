package com.ubtedu.base.net.rxretrofit.interceptor;

import com.ubtedu.base.net.rxretrofit.body.UploadProgressRequestBody;
import com.ubtedu.base.net.rxretrofit.callback.UploadProgressCallback;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Description: 上传进度拦截
 * @author: qinicy
 * @date: 17/5/6 15:10
 */
public class UploadProgressInterceptor implements Interceptor {

    private UploadProgressCallback uploadProgressCallback;

    public UploadProgressInterceptor(UploadProgressCallback uploadProgressCallback) {
        this.uploadProgressCallback = uploadProgressCallback;
        if (uploadProgressCallback == null) {
            throw new NullPointerException("this uploadProgressCallback must not null.");
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        if (originalRequest.body() == null) {
            return chain.proceed(originalRequest);
        }
        Request progressRequest = originalRequest.newBuilder()
                .method(originalRequest.method(),
                        new UploadProgressRequestBody(originalRequest.body(), uploadProgressCallback))
                .build();
        return chain.proceed(progressRequest);
    }
}
