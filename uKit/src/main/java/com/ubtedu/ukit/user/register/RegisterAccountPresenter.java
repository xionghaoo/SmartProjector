/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.user.register;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.ubtedu.alpha1x.core.base.Lifecycle.viewmodel.BaseViewModel;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.alpha1x.utils.StringUtil;
import com.ubtedu.base.net.manager.NetworkManager;
import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.mode.ApiResult;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.exception.ExceptionHelper;
import com.ubtedu.ukit.common.locale.LanguageUtil;
import com.ubtedu.ukit.common.locale.UBTLocale;
import com.ubtedu.ukit.user.UserConsts;
import com.ubtedu.ukit.user.UserManager;
import com.ubtedu.ukit.user.vo.response.EmptyResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author binghua.qin
 * @date 2019/02/22
 */
public class RegisterAccountPresenter extends RegisterAccountContracts.Presenter {

    private List<UBTLocale> mLocaleList;
    private CompositeDisposable mDisposables;
    private RegisterViewModel mRegisterViewModel;
    private Map<String, Boolean> mCheckAccountHistoryMap;
    private RegisterAccountInfo mAccount;

    public RegisterAccountPresenter() {
        mDisposables = new CompositeDisposable();
        mCheckAccountHistoryMap = new HashMap<>();
        VerifyCodeCounter.getInstance().clear();
    }


    /**
     * 因为检查账号后，如果账号存在，需要弹dialog提示，使用viewModel处理dialog的显示会更安全
     */
    @Override
    public void initRegisterViewModel() {
        mRegisterViewModel = createViewModel(RegisterViewModel.class);
        mRegisterViewModel.data.observe(mLifecycleOwner, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean accountExist) {
                onCheckAccountResult(accountExist);
            }
        });
    }

    @Override
    public void checkUserExists(final RegisterAccountInfo account) {

        if (account == null) {
            LogUtil.d("account == null");
            return;
        }
        mAccount = account;
        if (mCheckAccountHistoryMap.containsKey(account.getAPIAccount())) {
            boolean accountExist = mCheckAccountHistoryMap.get(account.getAPIAccount());
            onCheckAccountResult(accountExist);
            return;
        }
        if (!NetworkManager.getInstance().isNetworkConnected()) {
            getView().getUIDelegate().toastShort(getView().getContext().getString(R.string.global_network_unavailable));
            return;
        }

        if (account.isPhoneAccount()) {
            if (!StringUtil.isNumeric(account.account)) {
                getView().getUIDelegate().toastShort(mContext.getString(R.string.account_error_invalid_phone_format));
                return;
            }
        } else {
            if (!StringUtil.isEmail(account.account)) {
                getView().getUIDelegate().toastShort(mContext.getString(R.string.account_error_invalid_email_format));
                return;
            }
        }

        getView().getUIDelegate().showLoading(false);
        UserManager.getInstance().checkUserExists(account.getAPIAccount())
                .subscribe(new SimpleRxSubscriber<ApiResult<EmptyResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(ApiResult<EmptyResponse> response) {
                        //此处不关闭loading，请求验证码结束后再关闭
                        mRegisterViewModel.data.postValue(response.code != 0);
                    }

                    @Override
                    public void onError(RxException e) {
                        getView().getUIDelegate().hideLoading();
                        ExceptionHelper.handleException(e.getCode(), e.getMessage());
                    }
                });
    }

    private void onCheckAccountResult(boolean exist) {
        LogUtil.d("exist:" + exist);


        if (mAccount.isRegister()) {
            handleRegisterProcess(exist);
        } else {
            handleResetPasswordProcess(exist);
        }

    }

    private void handleRegisterProcess(boolean exist) {
        //把已经检查过的账号保存起来，重复检查就不需要访问网络了
        mCheckAccountHistoryMap.put(mAccount.getAPIAccount(), exist);
        if (getView() != null) {
            if (exist) {
                getView().getUIDelegate().hideLoading();
                getView().showAccountExistDialog();
            } else {
                requestVerifyCode();
            }
        }
    }

    private void handleResetPasswordProcess(boolean exist) {
        if (getView() != null) {
            if (exist) {
                requestVerifyCode();
            } else {
                getView().getUIDelegate().hideLoading();
                getView().getUIDelegate().toastShort(mContext.getString(R.string.account_reset_password_account_not_exist));
            }
        }
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

    private void requestVerifyCode() {
        RegisterAccountInfo lastRequestAccount = VerifyCodeCounter.getInstance().getLastRequestAccount();
        if (lastRequestAccount != null) {
            //本次使用账号与上一次请求验证码账号相同,且倒计时尚未结束，直接跳转到验证码输入界面
            if (mAccount.getType() == lastRequestAccount.getType()
                    && TextUtils.equals(mAccount.getAPIAccount(), lastRequestAccount.getAPIAccount())
                    && (System.currentTimeMillis() - VerifyCodeCounter.getInstance().getLastRequestTime()) / 1000 < UserConsts.VERIFICATION_CODE_SEND_INTERNAL) {
                getView().getUIDelegate().hideLoading();
                getView().toRegisterVerify();
                return;
            }
        }
        VerifyCodeCounter.getInstance().requestVerifyCode(mAccount).subscribe(new SimpleRxSubscriber<ApiResult<EmptyResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {
                mDisposables.add(d);
            }

            @Override
            public void onNext(ApiResult<EmptyResponse> response) {
                getView().getUIDelegate().hideLoading();
                if (response.sourceRawData.code == 0) {
                    getView().toRegisterVerify();
                } else {
                    LogUtil.d(response.sourceRawData.message);
                    getView().getUIDelegate().toastShort(response.sourceRawData.message);
                }
            }

            @Override
            public void onError(RxException e) {
                e.printStackTrace();
                getView().getUIDelegate().hideLoading();
                boolean consumed = ExceptionHelper.handleException(e.getCode(), e.getMessage());
                if (!consumed) {
                    getView().getUIDelegate().toastShort(
                            getView().getContext().getString(
                                    R.string.account_register_register_code_sand_fail));
                }
            }
        });
    }

    @Override
    public void release() {
        super.release();
        mDisposables.clear();
        VerifyCodeCounter.getInstance().clear();
    }

    public static class RegisterViewModel extends BaseViewModel {
        MediatorLiveData<Boolean> data = new MediatorLiveData<>();
    }
}
