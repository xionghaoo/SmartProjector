/**
 * ©2012-2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.user.login;

import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.ubtedu.alpha1x.core.base.Lifecycle.viewmodel.BaseViewModel;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.alpha1x.utils.MD5Util;
import com.ubtedu.alpha1x.utils.StringUtil;
import com.ubtedu.base.net.manager.NetworkManager;
import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.mode.ApiResult;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.BasicEventDelegate;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.common.analysis.LoginType;
import com.ubtedu.ukit.common.exception.ExceptionHelper;
import com.ubtedu.ukit.common.exception.ResultCode;
import com.ubtedu.ukit.common.locale.LanguageUtil;
import com.ubtedu.ukit.common.locale.UBTLocale;
import com.ubtedu.ukit.user.UserManager;
import com.ubtedu.ukit.user.gdpr.GdprAgeActivity;
import com.ubtedu.ukit.user.gdpr.GdprContracts;
import com.ubtedu.ukit.user.gdpr.GdprHelper;
import com.ubtedu.ukit.user.gdpr.GdprPactHandler;
import com.ubtedu.ukit.user.vo.CommonLoginParam;
import com.ubtedu.ukit.user.vo.GdprUserPactInfo;
import com.ubtedu.ukit.user.vo.LoginAccountInfo;
import com.ubtedu.ukit.user.vo.UserInfo;
import com.ubtedu.ukit.user.vo.response.CommonLoginResponse;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * @author qinicy
 * @date 2018/11/06
 */
public class LoginPresenter extends LoginContracts.Presenter {
    private final CompositeDisposable mDisposables;
    private UserInfo mUser;
    private LoginAccountInfo mAccount;
    private List<UBTLocale> mLocaleList;

    private Runnable mOnAcceptUbtPactTask;

    private LoginViewModel mViewModel;

    private GdprHelper mGdprHelper;

    public LoginPresenter() {
        mDisposables = new CompositeDisposable();
    }

    @Override
    public void initGdprPacts() {
        mGdprHelper = new GdprHelper(mContext, mGdprContracts);
        mGdprHelper.initGdprPacts();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initViewModel();
    }

    private void initViewModel() {
        mViewModel = createViewModel(LoginViewModel.class);
        mViewModel.mShowGdprDialog.observe(mLifecycleOwner, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isShow) {
                if (isShow) {
                    toShowInteractiveUserPact();
                }
            }
        });
    }

    @Override
    public void loadLocaleInfos() {
        getView().getUIDelegate().showLoading(false);
        if (mLocaleList == null) {
            List<UBTLocale> locales = LanguageUtil.getI18NCountryListTranslate(mContext);
            if (locales != null && locales.size() > 0) {
                mLocaleList = locales;
                getView().onLoadLocaleInfosFinish(mLocaleList);
            }
        }
        getView().getUIDelegate().hideLoading();
    }

    @Override
    public void login(final LoginAccountInfo account, String password) {
        if (!NetworkManager.getInstance().isNetworkConnected()) {
            getView().getUIDelegate().toastShort(getView().getContext().getString(R.string.global_network_unavailable));
            return;
        }
        if (TextUtils.isEmpty(account.account) || TextUtils.isEmpty(password)) {
            LogUtil.e("account or password is empty");
            return;
        }

        if (StringUtil.containsEmoji(account.account)) {
            LogUtil.e("account or password is empty");
            getView().getUIDelegate().toastShort(getView().getContext().getString(R.string.account_error_invalid_account_emoji));
            return;
        }
        if (account.isPhoneAccount()) {
            boolean isPhone = StringUtil.isNumeric(account.account);
            if (!isPhone) {
                getView().getUIDelegate().toastShort(mContext.getString(R.string.account_error_invalid_phone_format));
                return;
            }

        } else if (account.isEmailAccount()) {
            boolean isEmail = StringUtil.isEmail(account.account);
            if (!isEmail) {
                getView().getUIDelegate().toastShort(mContext.getString(R.string.account_error_invalid_email_format));
                return;
            }
        }

        mAccount = account;

        CommonLoginParam param = new CommonLoginParam();
        param.userType = account.getType();
        //ToE账号使用sha256加密，ToC账号用md5
        if (account.isEduAccount()) {
            param.userPwd = MD5Util.encodeSha256(password);
        } else {
            param.userPwd = MD5Util.encodeByMD5(password);
        }
        param.userName = account.getAPIAccount();

        getView().getUIDelegate().showLoading(false);
        UserManager.getInstance().commonLogin(param)

                .flatMap(new Function<ApiResult<CommonLoginResponse>, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(ApiResult<CommonLoginResponse> result) throws Exception {
                        if (result.data != null) {
                            UserInfo user = result.data.userInfo;
                            user.setAccount(account.account);
                            user.setLogin(true);
                            user.setToken(result.data.token);
                            mUser = user;

                            return mGdprHelper.checkUserPact(user);
                        }
                        return Observable.error(new RxException(ResultCode.ACCOUNT_PASSWD_ERROR, ""));
                    }
                })
                .subscribe(new SimpleRxSubscriber<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(Boolean isUserAcceptUBTPact) {
                        super.onNext(isUserAcceptUBTPact);

                        if (getView() != null) {
                            getView().getUIDelegate().hideLoading();
                        }

                        if (isUserAcceptUBTPact) {
                            onLoginUserSuccess();
                        } else {
                            mViewModel.mShowGdprDialog.postValue(true);
                            mOnAcceptUbtPactTask = new Runnable() {
                                @Override
                                public void run() {
                                    mGdprHelper.saveGdprPact();
                                }
                            };
                        }
                    }

                    @Override
                    public void onError(RxException e) {
                        super.onError(e);
                        if (getView() != null) {
                            getView().getUIDelegate().hideLoading();
                        }
                        e.printStackTrace();
                        boolean eat = ExceptionHelper.handleException(e.getCode(), e.getMessage());
                        if (!eat) {
                            ExceptionHelper.handleException(ResultCode.SERVER_NO_RESULT, "");
                        }
                    }
                });
    }


    @Override
    public void loginGuest() {
        mDisposables.clear();
        UserManager.getInstance().loginGuest();
        UserManager.getInstance().recordLoginAccountInfo(UserManager.getInstance().getCurrentUser(), null);
        if (getView() != null) {
            getView().onLoginSuccess(true);
        }
        notifyLoginStateChanged();
    }

    @Override
    public void toRegister() {
        final Runnable toRegister = new Runnable() {
            @Override
            public void run() {
                mContext.startActivity(new Intent(mContext, GdprAgeActivity.class));
            }
        };

        mGdprHelper.getUbtPactAsync().subscribe(new SimpleRxSubscriber<List<GdprUserPactInfo>>() {
            @Override
            public void onError(RxException e) {
                super.onError(e);
                ExceptionHelper.handleException(e.getCode(), e.getMessage());
            }

            @Override
            public void onComplete() {
                super.onComplete();
                mViewModel.mShowGdprDialog.postValue(true);
                mOnAcceptUbtPactTask = toRegister;
            }
        });
    }

    @Override
    public void toShowReadOnlyGdprPact(@GdprUserPactInfo.PactType final int type) {
        mGdprHelper.showGdprPacts(type);
    }


    @Override
    public void onAcceptGdprPact() {
        if (mOnAcceptUbtPactTask != null) {
            mOnAcceptUbtPactTask.run();
        }
    }

    private void onLoginUserSuccess() {
        if (mUser != null) {
            UserManager.getInstance().setLoginUser(mUser);
            UserManager.getInstance().recordLoginAccountInfo(mUser, mAccount);
            if (getView() != null) {
                getView().onLoginSuccess(false);
            }
        }
        notifyLoginStateChanged();
    }

    private void notifyLoginStateChanged() {
        UserInfo user = UserManager.getInstance().getCurrentUser();
        if (user != null && UKitApplication.getInstance().getEventDelegate() instanceof BasicEventDelegate) {
            LoginType type = LoginType.guest;

            if (mAccount != null) {
                if (mAccount.isPhoneAccount()) {
                    type = LoginType.phone;
                } else if (mAccount.isEmailAccount()) {
                    type = LoginType.email;
                } else if (mAccount.isEduAccount()) {
                    type = LoginType.edu;
                }
            }

            BasicEventDelegate delegate = (BasicEventDelegate) UKitApplication.getInstance().getEventDelegate();
            delegate.onLoginStateChange(user, type);
        }
    }


    private void toShowInteractiveUserPact() {
        if (mGdprHelper.getGdprPacts() != null && mGdprHelper.getGdprPacts().size() > 0) {
            GdprPactHandler handler = new GdprPactHandler((AppCompatActivity) mContext);
            handler.start(mGdprHelper.getGdprPacts(), new GdprPactHandler.OnGdprPactCallback() {
                @Override
                public void onResult(boolean isAccepted) {
                    if (isAccepted) {
                        onAcceptGdprPact();
                    }
                }
            });
        }
    }

    @Override
    public void release() {
        mDisposables.clear();
        mGdprHelper.release();
        super.release();
    }

    public static class LoginViewModel extends BaseViewModel {
        MediatorLiveData<Boolean> mShowGdprDialog = new MediatorLiveData<>();
    }


    private GdprContracts mGdprContracts = new GdprContracts() {
        @Override
        public void showLoading(boolean isCancelable) {
            if (getView() != null) {
                getView().getUIDelegate().showLoading(isCancelable);
            }
        }

        @Override
        public void hideLoading() {
            if (getView() != null) {
                getView().getUIDelegate().hideLoading();
            }
        }

        @Override
        public void showReadOnlyGdprPactDialog(GdprUserPactInfo info) {
            if (getView() != null) {
                getView().showGdprPactDialog(info.type, info.abstractText, info.url);
            }
        }

        @Override
        public void showInteractiveGdprPacts(List<GdprUserPactInfo> infos) {
            if (getView() != null) {
                getView().showGdprPactDialog(GdprUserPactInfo.GDPR_TYPE_ALL, "", "");
            }
        }


        @Override
        public void toastShort(String string) {
            if (getView() != null) {
                getView().getUIDelegate().toastShort(string);
            }
        }

        @Override
        public void onLoginAcceptGdprPact(boolean success) {
            if (success) {
                LoginPresenter.this.onLoginUserSuccess();
            }
        }
    };

}
