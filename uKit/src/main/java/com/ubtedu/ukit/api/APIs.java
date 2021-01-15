package com.ubtedu.ukit.api;

import com.ubtedu.base.net.rxretrofit.mode.ApiResult;
import com.ubtedu.ukit.common.cloud.UploadSignature;
import com.ubtedu.ukit.project.vo.Project;
import com.ubtedu.ukit.user.vo.CaptchaVerifyParams;
import com.ubtedu.ukit.user.vo.CheckIMEIParams;
import com.ubtedu.ukit.user.vo.CommonLoginParam;
import com.ubtedu.ukit.user.vo.GdprUserPactInfo;
import com.ubtedu.ukit.user.vo.IdeaFeedBackBody;
import com.ubtedu.ukit.user.vo.OldUserInfo;
import com.ubtedu.ukit.user.vo.UpdatePwdParam;
import com.ubtedu.ukit.user.vo.UpdateUserInfo;
import com.ubtedu.ukit.user.vo.UserApiParam;
import com.ubtedu.ukit.user.vo.UserData;
import com.ubtedu.ukit.user.vo.UserInfo;
import com.ubtedu.ukit.user.vo.response.CommonLoginResponse;
import com.ubtedu.ukit.user.vo.response.EmptyResponse;
import com.ubtedu.ukit.user.vo.response.TokenResponse;
import com.ubtedu.ukit.user.vo.response.UserInfoResponse;
import com.ubtedu.ukit.user.vo.response.UserOPResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by qinicy on 2017/5/12.
 */

public interface APIs {
    /**
     * V2版用户接口，2018-4-17
     */
    interface UserApiService {

        @GET(Urls.UserUrl.UserIMEI)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<EmptyResponse>> getUserIMEI(@Query("account") String account,
                                                         @Header("X-UBT-AppId") String appId,
                                                         @Header("X-UBT-Sign") String sign);

        @POST(Urls.UserUrl.UserIMEI)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<EmptyResponse>> postUserIMEI(@Query("imei") String imei,
                                                          @Header("appId") String appId,
                                                          @Header("authorization") String authorization);

        @POST(Urls.UserUrl.CheckIMEI)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<EmptyResponse>> checkUserIMEI(@Body CheckIMEIParams params,
                                                           @Header("appId") String appId,
                                                           @Header("authorization") String authorization);

        @GET(Urls.GDPRUrl.PactInfo)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<List<GdprUserPactInfo>>> pactInfo(@Query("productId") String productId,
                                                               @Query("type") int type,
                                                               @Query("lan") String lang,
                                                               @Header("X-UBT-AppId") String appId,
                                                               @Header("X-UBT-Sign") String sign);


        @GET(Urls.GDPRUrl.PactUrl)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<List<GdprUserPactInfo>>> pactUrl(@Query("productId") String productId,
                                                              @Query("version") String version,
                                                              @Query("type") int type,
                                                              @Header("X-UBT-AppId") String appId,
                                                              @Header("X-UBT-Sign") String sign);

        @GET(Urls.GDPRUrl.UserPactInfo)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<List<GdprUserPactInfo>>> userPactInfo(@Query("productId") String productId,
                                                                   @Query("userId") String userId,
                                                                   @Header("X-UBT-AppId") String appId,
                                                                   @Header("X-UBT-Sign") String sign);

        @POST(Urls.GDPRUrl.SaveUserPact)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<EmptyResponse>> saveUserPact(@Body List<GdprUserPactInfo> gdprUserPactInfos,
                                                          @Header("authorization") String authorization);


        /**
         * 删除用户，正式
         *
         * @param password
         * @param isThird       是否第三方账号{0：否，1：是}
         * @param authorization token
         * @return
         */
        @DELETE(Urls.UserUrl.UserUrl)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<EmptyResponse>> deleteUser(@Query("pwd") String password,
                                                        @Query("isThird") int isThird,
                                                        @Header("authorization") String authorization);

        /**
         * 删除用户，用于调试
         */
        @GET(Urls.UserUrl.UserUrl)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<Object>> deleteUserOfDebug(@Query("account") String account);

        /**
         * 修改用户信息
         */
        @PATCH(Urls.UserUrl.UserUrl)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<UserInfoResponse>> modifyUserInfo(@Body UpdateUserInfo user,
                                                               @Header("authorization") String authorization);

        /**
         * 获取验证码
         */
        @GET(Urls.UserUrl.GetCaptchaUrl)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<EmptyResponse>> getCaptcha(
                @Query("account") String account,
                @Query("accountType") String accountType,
                @Query("purpose") int purpose,
                @Query("zhFlag") int zhFlag,
                @Header("language") String language
        );


        @POST(Urls.UserUrl.GetCaptchaVerifyUrl)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<EmptyResponse>> verifyCaptcha(@Body CaptchaVerifyParams params);

        /**
         * 获取验证码校验结果
         */
        @GET(Urls.UserUrl.GetResetPasswordCaptchaVerifyUrl)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<TokenResponse>> verifyResetPasswordCaptcha(
                @Query("account") String account,
                @Query("accountType") int accountType,
                @Query("captcha") String captcha
        );

        /**
         * 判断用户是否存在
         */
        @GET(Urls.UserUrl.GetUserCheckUrl)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<EmptyResponse>> checkUserExists(
                @Query("account") String account
        );

        /**
         * 获取用户信息
         */
        @GET(Urls.UserUrl.GetUserInfoUrl)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<UserInfo>> getUserInfo(
                @Header("authorization") String authorization
        );

        /**
         * 使用{@link EduUserApiService#commonLogin(CommonLoginParam)}替代
         */
        @Deprecated
        @PUT(Urls.UserUrl.PutUserLoginUrl)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<UserOPResponse>> login(@Body UserApiParam login);

        /**
         * 退出登录
         */
        @DELETE(Urls.UserUrl.DeleteUserLogoutUrl)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<EmptyResponse>> logout(@Header("authorization") String authorization);

        /**
         * 修改密码
         */
        @PATCH(Urls.UserUrl.PatchModifyUserPasswordUrl)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<EmptyResponse>> modifyPassword(@Header("authorization") String authorization,
                                                            @Body UserApiParam param);

        /**
         * 重置密码
         */
        @PATCH(Urls.UserUrl.PatchResetUserPasswordUrl)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<EmptyResponse>> resetPassword(@Body UserApiParam resetPasswordParam);

        /**
         * 验证密码是否正确
         */
        @GET(Urls.UserUrl.GetUserPasswordVerifyUrl)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<EmptyResponse>> verifyPassword(
                @Query("password") String passwordMd5,
                @Header("authorization") String authorization);

        /**
         * 用户注册
         */
        @POST(Urls.UserUrl.PostRegisterUrl)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<UserOPResponse>> register(@Body UserApiParam registerParam);

        @PUT(Urls.UserUrl.PutRefreshUserTokenUrl)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<EmptyResponse>> refreshToken(@Header("authorization") String authorization);

        @GET(Urls.UserUrl.GetOldUserByAccount)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<OldUserInfo>> getOldUserByAccount(@Query("userAccount") String userAccount);

        //意见反馈
        @POST(Urls.UserUrl.FeedBack)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<EmptyResponse>> pushFeedback(@Header("product") String product, @Body IdeaFeedBackBody feedbackParam);

        /**
         * 用户登录，包含优必选和EDU登录方式
         */
        @POST(Urls.EduUserUrl.COMMON_LOGIN_URL)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<CommonLoginResponse>> commonLogin(@Body CommonLoginParam param);

        /**
         * EDU账号修改密码
         */
        @PUT(Urls.EduUserUrl.UPDATE_PASSWORD_URL)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<EmptyResponse>> updatePassword(@Body UpdatePwdParam param);
    }

    interface FileService {
        /**
         * 获取文件上传签名
         *
         * @param cloudType   存储云（1，七牛；2，阿里云；）
         * @param storageType 空间类型（1，公共空间；2，私有空间；）
         * @param validTime   过期时间(900-3600),单位秒
         * @return
         */
        @GET(Urls.FileControllerUrl.UpSignature)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Call<UploadSignature> getUploadSignature(
                @Query("pName") String projectName,
                @Query("cloudType") int cloudType,
                @Query("storageType") int storageType,
                @Query("validTime") int validTime
        );

        /**
         * 获取私有空间文件可读链接
         *
         * @param appId
         * @param sign
         * @param deviceId
         * @param projectName
         * @param cloudType   存储云（1，七牛；2，阿里云；）
         * @param urls        文件URL(可多个，以','隔开)
         * @param validTime   过期时间(900-3600),单位秒
         * @return
         */
        @GET(Urls.FileControllerUrl.UpSignature)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<Object>> getDownload(
                @Query("X-UBT-AppId") String appId,
                @Query("X-UBT-Sign") String sign,
                @Query("X-UBT-DeviceId") String deviceId,
                @Query("pName") String projectName,
                @Query("cloudType") int cloudType,
                @Query("urls") String urls,
                @Query("validTime") int validTime
        );
    }

    interface UserDataService {
        @POST(Urls.UserDataUrl.userProject)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<EmptyResponse>> syncUserProject(@Body Project project);

        @DELETE(Urls.UserDataUrl.userProject)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<EmptyResponse>> deleteUserProject(@Query("projectId") String projectId);


        @GET(Urls.UserDataUrl.userData)
        @Headers({"Content-Type:application/json", "charset:UTF-8"})
        Observable<ApiResult<UserData>> getUserData();
    }


}
