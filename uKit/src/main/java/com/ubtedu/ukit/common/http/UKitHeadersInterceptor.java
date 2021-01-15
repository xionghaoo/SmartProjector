/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.http;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.application.PrivacyInfo;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.user.UserManager;
import com.ubtedu.ukit.user.vo.UserInfo;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * UBT新的鉴权方式
 *
 * @Author qinicy
 * @Date 2018/11/20
 **/
public class UKitHeadersInterceptor implements Interceptor {

    public final static String HEADER_X_UBT_APPID = "X-UBT-AppId";
    public final static String HEADER_X_UBT_SIGN = "X-UBT-Sign";
    public final static String HEADER_X_UBT_DEVICEID = "X-UBT-DeviceId";
    public final static String HEADER_AUTHORIZATION = "authorization";

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder()
                .header(HEADER_X_UBT_APPID, String.valueOf(PrivacyInfo.getUBTAppId()))
                .header(HEADER_X_UBT_SIGN, PrivacyInfo.getUBTSign())
                .header(HEADER_X_UBT_DEVICEID, UKitApplication.getInstance().generateDeviceToken());

        String authorization = original.header(HEADER_AUTHORIZATION);
        LogUtil.i("authorization:" + authorization + "  url:" + original.url().toString());
        UserInfo user = UserManager.getInstance().getCurrentUser();
        if (user != null && !user.isGuest() && authorization == null) {
            //authorization如果有，不覆盖
            requestBuilder.header(HEADER_AUTHORIZATION, UserManager.getInstance().getLoginUserToken());
        }
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
