/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.http;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import com.ubtedu.base.net.rxretrofit.RxClient;
import com.ubtedu.ukit.api.APIs;
import com.ubtedu.ukit.application.AppConfigs;
import com.ubtedu.ukit.application.ServerConfig;
import com.ubtedu.ukit.application.UKitApplication;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * @Author qinicy
 * @Date 2019/4/22
 **/
public class RxClientFactory {
    private final static int READ_TIMEOUT = 20;
    private final static int CONNECT_TIMEOUT = 10000;
    @SuppressLint("StaticFieldLeak")
    private static RxClient sAPIsRxClient;
    private static APIs.UserApiService mUserApiService;
    private static APIs.UserDataService mUserDataService;
    private static APIs.FileService mFileService;

    private static RxClient createRxClient(String baseUrl) {
        return new RxClient.Builder(UKitApplication.getInstance())
                .baseUrl(baseUrl)
                .hostnameVerifier(getHostnameVerifier())
                .interceptor(new UKitHeadersInterceptor())
                .log(AppConfigs.HTTP_LOG_ENABLE)
                .connectTimeout(CONNECT_TIMEOUT)
                .readTimeout(READ_TIMEOUT)
                .build();
    }


    @Nullable
    public static APIs.UserApiService getUserApiService() {
        if (mUserApiService == null) {
            mUserApiService = getAPIRxClient().create(APIs.UserApiService.class);
        }
        return mUserApiService;
    }

    @Nullable
    public static APIs.UserDataService getUserDataService() {
        if (mUserDataService == null) {
            mUserDataService = getAPIRxClient().create(APIs.UserDataService.class);
        }
        return mUserDataService;
    }

    @Nullable
    public static APIs.FileService getFileService() {
        if (mFileService == null) {
            mFileService = getAPIRxClient().create(APIs.FileService.class);
        }
        return mFileService;
    }


    /**
     * app切换地区后，需要重置
     */
    public static void reset() {
        sAPIsRxClient = null;
        mUserApiService = null;
        mUserDataService = null;
        mFileService = null;
    }

    public static RxClient getAPIRxClient() {
        if (sAPIsRxClient == null) {
            sAPIsRxClient = createRxClient(ServerConfig.getAPIServer());
        }
        return sAPIsRxClient;
    }


    private static HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }
}

