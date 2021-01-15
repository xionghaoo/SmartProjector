/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.user.register;

import com.ubtedu.alpha1x.utils.GsonUtil;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.alpha1x.utils.MD5Util;
import com.ubtedu.alpha1x.utils.SharedPreferenceUtils;
import com.ubtedu.base.net.manager.NetworkManager;
import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.mode.ApiResult;
import com.ubtedu.base.net.rxretrofit.subscriber.RxSubscriber;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.BasicEventDelegate;
import com.ubtedu.ukit.application.PrivacyInfo;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.common.analysis.LoginType;
import com.ubtedu.ukit.common.exception.ExceptionHelper;
import com.ubtedu.ukit.project.UserDataSynchronizer;
import com.ubtedu.ukit.user.UserActivityStack;
import com.ubtedu.ukit.user.UserConsts;
import com.ubtedu.ukit.user.UserManager;
import com.ubtedu.ukit.user.login.LoginActivity;
import com.ubtedu.ukit.user.vo.GdprUserPactInfo;
import com.ubtedu.ukit.user.vo.UpdateUserInfo;
import com.ubtedu.ukit.user.vo.UserApiParam;
import com.ubtedu.ukit.user.vo.UserInfo;
import com.ubtedu.ukit.user.vo.response.EmptyResponse;
import com.ubtedu.ukit.user.vo.response.UserOPResponse;

import java.util.ArrayList;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author binghua.qin
 * @date 2019/02/23
 */
public class RegisterPasswordPresenter extends RegisterPasswordContracts.Presenter {
    private final CompositeDisposable mDisposables;
    private RegisterAccountInfo mAccount;

    public RegisterPasswordPresenter() {
        mDisposables = new CompositeDisposable();
    }

    @Override
    public void register(final RegisterAccountInfo account, String password) {
        if (account == null) {
            LogUtil.e("account == null");
            return;
        }
        if (!NetworkManager.getInstance().isNetworkConnected()) {
            getView().getUIDelegate().toastShort(getView().getContext().getString(R.string.global_network_unavailable));
            return;
        }
        mAccount = account;

        getView().getUIDelegate().showLoading(false);

        UserApiParam registerParam = new UserApiParam();
        registerParam.account = account.getAPIAccount();
        registerParam.accountType = account.isPhoneAccount() ? UserApiParam.LOGIN_TYPE_PHONE : UserApiParam.LOGIN_TYPE_EMAIL;
        registerParam.appId = PrivacyInfo.getUBTAppId();
        registerParam.captcha = account.verifyCode;
        registerParam.password = MD5Util.encodeByMD5(password);

        UserManager.getInstance().register(registerParam)
                .subscribe(new RxSubscriber<ApiResult<UserOPResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(ApiResult<UserOPResponse> response) {
                        int code = response.sourceRawData.code;
                        if (code == 0) {
                            UserInfo user = response.sourceRawData.userInfo;
                            user.setLogin(true);
                            user.setToken(response.sourceRawData.tokenObject.token);

                            onRegisterSuccess(user);

                        } else {
                            LogUtil.e("注册失败：code = " + response.sourceRawData.code);
                            getView().getUIDelegate().toastShort(response.sourceRawData.message);
                        }
                    }

                    @Override
                    public void onComplete() {
                        getView().getUIDelegate().hideLoading();
                    }

                    @Override
                    public void onError(RxException e) {
                        getView().getUIDelegate().hideLoading();
                        e.printStackTrace();
                        ExceptionHelper.handleException(e.getCode(), e.getMessage());
                    }
                });

    }

    @Override
    public void resetPassword(final RegisterAccountInfo account, String password) {

        if (!NetworkManager.getInstance().isNetworkConnected()) {
            getView().getUIDelegate().toastShort(getView().getContext().getString(R.string.global_network_unavailable));
            return;
        }
        getView().getUIDelegate().showLoading(false);
        UserApiParam param = new UserApiParam();
        param.token = account.verifyToken;
        param.userId = account.userId;
        param.password = MD5Util.encodeByMD5(password);
        UserManager.getInstance().resetPassword(param)
                .subscribe(new RxSubscriber<ApiResult<EmptyResponse>>() {
                    @Override
                    public void onError(RxException e) {
                        ExceptionHelper.handleException(e.getCode(), e.getMessage());
                        getView().getUIDelegate().hideLoading();
                        e.printStackTrace();
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(ApiResult<EmptyResponse> result) {
                        if (result.sourceRawData != null) {
                            int code = result.sourceRawData.code;
                            if (code == 0) {
                                UserActivityStack.finishAll();
                                LoginActivity.open(getView().getContext(), LoginActivity.sIsShowBackButton);
                                getView().getUIDelegate().toastShort(
                                        getView().getContext().getString(R.string.account_reset_password_success));
                                UserManager.getInstance().clearLocalAccount();
                                UserManager.getInstance().loginGuest();
                                UserManager.getInstance().recordLoginAccountInfo(UserManager.getInstance().getCurrentUser(), null);
                                UserDataSynchronizer.getInstance().sync(false).subscribe(new SimpleRxSubscriber<>());
                                notifyLoginStateChanged();
                            } else {
                                ExceptionHelper.handleException(code, result.sourceRawData.message);
                            }
                        } else {
                            getView().getUIDelegate().toastShort(mContext.getString(R.string.account_reset_password_fail));
                        }

                    }

                    @Override
                    public void onComplete() {
                        getView().getUIDelegate().hideLoading();
                    }
                });
    }

    private void notifyLoginStateChanged() {
        UserInfo user = UserManager.getInstance().getCurrentUser();
        if (user != null && UKitApplication.getInstance().getEventDelegate() instanceof BasicEventDelegate) {
            LoginType type = LoginType.guest;
            BasicEventDelegate delegate = (BasicEventDelegate) UKitApplication.getInstance().getEventDelegate();
            delegate.onLoginStateChange(user, type);
        }
    }

    private void onRegisterSuccess(UserInfo user) {
        saveUserPact(user);
        updateUserNickname(user);
        UserManager.getInstance().setLoginUser(user);
        UserManager.getInstance().recordLoginAccountInfo(user, mAccount);
        getView().onLoginSuccess(false);
    }

    private void updateUserNickname(UserInfo user) {
        if (mAccount.nickName != null) {
            user.setNickName(mAccount.nickName);
            UpdateUserInfo info = new UpdateUserInfo(user);
            UserManager.getInstance().modifyUserInfo(info, user.getToken()).subscribe(new SimpleRxSubscriber<>());
        }
    }

    private void saveUserPact(UserInfo user) {
        String json = SharedPreferenceUtils.getInstance(mContext).getStringValue(UserConsts.SP_GDPR_LASTEST_UBT_PACTS);
        ArrayList<GdprUserPactInfo> pactInfos = GsonUtil.get().toObjectList(json, GdprUserPactInfo.class);
        if (pactInfos != null) {
            UserManager.getInstance().saveUserPactInfo(pactInfos, user.getToken())
                    .subscribe(new SimpleRxSubscriber<>());
        }
    }
}
