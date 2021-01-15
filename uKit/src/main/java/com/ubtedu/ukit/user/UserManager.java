/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.user;

import android.content.Context;

import com.ubtedu.alpha1x.utils.SharedPreferenceUtils;
import com.ubtedu.base.net.rxretrofit.RxClient;
import com.ubtedu.base.net.rxretrofit.mode.ApiResult;
import com.ubtedu.ukit.api.APIs;
import com.ubtedu.ukit.application.PrivacyInfo;
import com.ubtedu.ukit.common.http.RxClientFactory;
import com.ubtedu.ukit.project.vo.Project;
import com.ubtedu.ukit.user.vo.CaptchaVerifyParams;
import com.ubtedu.ukit.user.vo.CheckIMEIParams;
import com.ubtedu.ukit.user.vo.CommonLoginParam;
import com.ubtedu.ukit.user.vo.GdprUserPactInfo;
import com.ubtedu.ukit.user.vo.IdeaFeedBackBody;
import com.ubtedu.ukit.user.vo.LoginAccountInfo;
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

/**
 * @Author qinicy
 * @Date 2018/11/6
 **/
public class UserManager {
    private static UserManager mThis;
    private boolean isInited;
    private Context mContext;
    /**
     * 记录当前登录的用户
     */
    private UserInfo mCurrentUser;

    

    private UserManager() {

    }

    private static class SingletonHolder {
        private final static UserManager instance = new UserManager();
    }

    public static UserManager getInstance() {
        return UserManager.SingletonHolder.instance;
    }



    public UserManager init(Context context) {
        if (!isInited) {
            mContext = context.getApplicationContext();
            
            isInited = true;
        }

        return this;
    }



    public void recordLoginAccountInfo(UserInfo user, LoginAccountInfo account) {
        //记录当前登录的用户
        SharedPreferenceUtils.getInstance(mContext)
                .setValue(UserConsts.SP_CURRENT_LOGIN_USER, user);

        if (!user.isGuest()) {
            //记录当前登录时间，用来做每隔七天自动登出
            SharedPreferenceUtils.getInstance(mContext)
                    .setValue(UserConsts.SP_GDPR_7_DAYS_OUT_TIME, System.currentTimeMillis());
            //记录当前登录类型
            if (account != null) {
                SharedPreferenceUtils.getInstance(mContext)
                        .setValue(UserConsts.SP_LAST_LOGIN_ACCOUNT_INFO, account);
            }
        }
    }


    public void loginGuest() {
        setLoginUser(createGuest());
    }

    public boolean isGuest() {
        return mCurrentUser == null || mCurrentUser.isGuest();
    }

    public UserInfo getCurrentUser() {
        if (mCurrentUser == null) {
            mCurrentUser = SharedPreferenceUtils.getInstance(mContext)
                    .getObjectValue(UserConsts.SP_CURRENT_LOGIN_USER, UserInfo.class);
        }
        if (mCurrentUser == null) {

            mCurrentUser = createGuest();
        }
        return mCurrentUser;
    }

    public void setLoginUser(UserInfo currentUser) {
        mCurrentUser = currentUser;
        if (mCurrentUser == null) {
            mCurrentUser = createGuest();
        }
    }

    private UserInfo createGuest() {
        UserInfo guest = new UserInfo();
        guest.setUserId(0);
        guest.setGuest(true);
        guest.setNickName("Guest");
        return guest;
    }

    /**
     * 清除本地账号缓存。
     */
    public void clearLocalAccount() {
        SharedPreferenceUtils.getInstance(mContext)
                .setValue(UserConsts.SP_CURRENT_LOGIN_USER, "");
    }

    /**
     * 获取当前登录的用户ID
     *
     * @return user id
     */
    public String getLoginUserId() {
        return String.valueOf(getCurrentUser().getUserId());
    }

    public String getLoginUserAccount() {
        if (getCurrentUser() != null) {
            return getCurrentUser().getAccount();
        }
        return "";
    }

    public String getLoginUserNickname() {
        if (getCurrentUser().getNickName() != null) {
            return getCurrentUser().getNickName();
        }
        return "";
    }

    public String getLoginUserAvatarUrl() {
        return getCurrentUser().getUserImage();
    }

    public String getLoginUserToken() {
        if (getCurrentUser().getToken() != null) {
            return getCurrentUser().getToken();
        }
        return "";
    }


    /**
     * 需要userInfo，authorization
     */
    public Observable<ApiResult<UserInfoResponse>> modifyUserInfo(UpdateUserInfo userInfo, String authorization) {
        return getAPIClient().call(getUserApiService().modifyUserInfo(userInfo, authorization));
    }


    /**
     * 需要authorization,
     */
    public Observable<ApiResult<UserInfo>> getUserInfo(String authorization) {
        return getAPIClient().call(getUserApiService().getUserInfo(authorization));
    }

    /**
     * 需要account, accountType, appId, captcha, password
     */
    public Observable<ApiResult<UserOPResponse>> register(UserApiParam registerParam) {
        return getAPIClient().call(getUserApiService().register(registerParam));
    }


    /**
     * 使用{@link #commonLogin(CommonLoginParam)}替代
     */
    @Deprecated
    public Observable<ApiResult<UserOPResponse>> login(UserApiParam loginParam) {
        return getAPIClient().call(getUserApiService().login(loginParam));
    }

    /**
     * 登录，可以登录优必选账号，也可以登录教育平台账号
     *
     * @param param
     * @return
     */
    public Observable<ApiResult<CommonLoginResponse>> commonLogin(CommonLoginParam param) {
        return getAPIClient().call(getUserApiService().commonLogin(param));
    }

    private RxClient getAPIClient(){
        return RxClientFactory.getAPIRxClient();
    }

    private APIs.UserDataService getUserDataService(){
        return RxClientFactory.getUserDataService();
    }
    public APIs.UserApiService getUserApiService() {
        return RxClientFactory.getUserApiService();
    }
    public Observable<ApiResult<EmptyResponse>> updateEduPassword(UpdatePwdParam param) {
        return getAPIClient().call(getUserApiService().updatePassword(param));
    }

    /**
     * 需要authorization,
     */
    public Observable<ApiResult<EmptyResponse>> logout(UserApiParam param) {
        return getAPIClient().call(getUserApiService().logout(param.authorization));
    }

    /**
     * 需要password, userId, token。其中userId从获取验证码的response中返回。
     */
    public Observable<ApiResult<EmptyResponse>> resetPassword(UserApiParam resetPasswordParam) {
        return getAPIClient().call(getUserApiService().resetPassword(resetPasswordParam));
    }

    /**
     * 获取用户已同意的隐私条款和使用政策
     */
    public Observable<ApiResult<List<GdprUserPactInfo>>> getUserPactInfo(String productId, String userId,
                                                                         String appId, String sign) {
        return getAPIClient().call(getUserApiService().userPactInfo(productId, userId, appId, sign));
    }

    /**
     * 保存用户已同意的隐私条款和使用政策
     */
    public Observable<ApiResult<EmptyResponse>> saveUserPactInfo(List<GdprUserPactInfo> gdprUserPactInfos,
                                                                 String authorization) {
        return getAPIClient().call(getUserApiService().saveUserPact(gdprUserPactInfos, authorization));
    }

    /**
     * 获取最新的隐私条款和使用政策
     */
    public Observable<ApiResult<List<GdprUserPactInfo>>> pactInfo(String productId, int type, String lang, String appId, String sign) {
        return getAPIClient().call(getUserApiService().pactInfo(productId, type, lang, appId, sign));
    }

    /**
     * 根据版本获取隐私条款和使用政策
     */
    public Observable<ApiResult<List<GdprUserPactInfo>>> pactUrl(String productId, String version, int type, String appId, String sign) {
        return getAPIClient().call(getUserApiService().pactUrl(productId, version, type, appId, sign));
    }

    public Observable<ApiResult<EmptyResponse>> getUserIMEI(String account) {
        return getAPIClient().call(getUserApiService().getUserIMEI(account, String.valueOf(PrivacyInfo.getUBTAppId()), PrivacyInfo.getUBTSign()));
    }

    public Observable<ApiResult<EmptyResponse>> postUserIMEI(String imei, String token) {
        return getAPIClient().call(getUserApiService().postUserIMEI(imei, String.valueOf(PrivacyInfo.getUBTAppId()), token));
    }

    public Observable<ApiResult<EmptyResponse>> checkUserIMEI(CheckIMEIParams params, String token) {
        return getAPIClient().call(getUserApiService().checkUserIMEI(params, String.valueOf(PrivacyInfo.getUBTAppId()), token));
    }

    public Observable<ApiResult<UserData>> getUserData() {
        return getAPIClient().call(getUserDataService().getUserData());
    }

    public Observable<ApiResult<EmptyResponse>> syncUserProject(Project project) {
        return getAPIClient().call(getUserDataService().syncUserProject(project));
    }

    public Observable<ApiResult<EmptyResponse>> deleteUserProject(String projectId) {
        return getAPIClient().call(getUserDataService().deleteUserProject(projectId));
    }


    public Observable<ApiResult<EmptyResponse>> pushFeedback(String productId, IdeaFeedBackBody body) {
        return getAPIClient().call(getUserApiService().pushFeedback(productId, body));
    }

    /**
     * 需要account
     */
    public Observable<ApiResult<EmptyResponse>> deleteUser(String password) {

        return getAPIClient().call(getUserApiService().deleteUser(password, 0, getLoginUserToken()));
    }

    /**
     * 需要account
     */
    public Observable<ApiResult<Object>> deleteUserOfDebug(String account) {
        return getAPIClient().call(getUserApiService().deleteUserOfDebug(account));
    }


    /**
     * 需要account，accountType，purpose，lancode, 可选zhFlag
     */
    public Observable<ApiResult<EmptyResponse>> getCaptcha(UserApiParam param) {
        return getAPIClient().call(getUserApiService().getCaptcha(param.account,
                String.valueOf(param.accountType),
                param.purpose,
                param.zhFlag,
                param.lanCode));
    }

    /**
     * 需要account, accountType, captcha
     */
    public Observable<ApiResult<EmptyResponse>> verifyCaptcha(CaptchaVerifyParams params) {
        return getAPIClient().call(getUserApiService().verifyCaptcha(params));
    }

    /**
     * 需要account, accountType, captcha
     */
    public Observable<ApiResult<TokenResponse>> verifyResetPasswordCaptcha(UserApiParam param) {
        return getAPIClient().call(getUserApiService().verifyResetPasswordCaptcha(param.account,
                param.accountType,
                param.captcha));
    }

    /**
     * 需要account,
     */
    public Observable<ApiResult<EmptyResponse>> checkUserExists(String account) {
        return getAPIClient().call(getUserApiService().checkUserExists(account));
    }

}
