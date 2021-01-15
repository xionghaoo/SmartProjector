/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.user.register;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.base.net.manager.NetworkManager;
import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.mode.ApiResult;
import com.ubtedu.base.net.rxretrofit.subscriber.RxSubscriber;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.exception.ExceptionHelper;
import com.ubtedu.ukit.user.UserConsts;
import com.ubtedu.ukit.user.UserManager;
import com.ubtedu.ukit.user.vo.CaptchaVerifyParams;
import com.ubtedu.ukit.user.vo.UserApiParam;
import com.ubtedu.ukit.user.vo.response.EmptyResponse;
import com.ubtedu.ukit.user.vo.response.TokenResponse;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author binghua.qin
 * @date 2019/02/23
 */
public class RegisterVerifyPresenter extends RegisterVerifyContracts.Presenter {
    private final CompositeDisposable mDisposables;
    private RegisterAccountInfo mAccount;

    public RegisterVerifyPresenter(RegisterAccountInfo account) {
        mDisposables = new CompositeDisposable();
        mAccount = account;
    }

    @Override
    public void release() {
        super.release();
        mDisposables.clear();
    }


    @Override
    public void initVerificationCodeTimer() {
        long timeLeft = UserConsts.VERIFICATION_CODE_SEND_INTERNAL - (System.currentTimeMillis() - VerifyCodeCounter.getInstance().getLastRequestTime()) / 1000;
        onVerificationCodeTimerCountDown(timeLeft);
    }


    @Override
    public void requestVerificationCode(boolean isRegister) {
        if (!NetworkManager.getInstance().isNetworkConnected()) {
            getView().getUIDelegate().toastShort(getView().getContext().getString(R.string.global_network_unavailable));
            return;
        }
        RegisterAccountInfo account = mAccount;
        if (account == null) {
            return;
        }
        getView().getUIDelegate().showLoading(false);
        VerifyCodeCounter.getInstance().requestVerifyCode(account)
                .subscribe(new RxSubscriber<ApiResult<EmptyResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(ApiResult<EmptyResponse> response) {
                        if (response.sourceRawData.code == 0) {
                            onVerificationCodeTimerCountDown(UserConsts.VERIFICATION_CODE_SEND_INTERNAL);
                        } else {
                            LogUtil.d(response.sourceRawData.message);
                            getView().getUIDelegate().toastShort(response.sourceRawData.message);
                        }
                    }

                    @Override
                    public void onComplete() {
                        getView().getUIDelegate().hideLoading();
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
    public void checkVerifyCode(String verifyCode, boolean register) {
        if (!NetworkManager.getInstance().isNetworkConnected()) {
            getView().getUIDelegate().toastShort(getView().getContext().getString(R.string.global_network_unavailable));
            return;
        }
        if (register) {
            verifyRegisterCode(verifyCode);
        } else {
            verifyResetPasswordCode(verifyCode);
        }
    }

    private void verifyResetPasswordCode(String verifyCode) {
        LogUtil.d("");
        RegisterAccountInfo account = mAccount;
        UserApiParam param = new UserApiParam();
        param.accountType = account.isPhoneAccount() ? UserApiParam.LOGIN_TYPE_PHONE : UserApiParam.LOGIN_TYPE_EMAIL;
        param.account = account.getAPIAccount();
        param.captcha = verifyCode;
        UserManager.getInstance().verifyResetPasswordCaptcha(param)
                .subscribe(new SimpleRxSubscriber<ApiResult<TokenResponse>>() {
                    @Override
                    public void onError(RxException e) {
                        e.printStackTrace();
                        getView().getUIDelegate().hideLoading();
                        ExceptionHelper.handleException(e.getCode(), e.getMessage());

                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(ApiResult<TokenResponse> result) {
                        super.onNext(result);
                        if (result.sourceRawData != null) {
                            mAccount.userId = result.sourceRawData.userId;
                            mAccount.verifyToken = result.sourceRawData.token;
                            onCheckVerifyCodeResult(true);
                        } else {
                            onCheckVerifyCodeResult(false);
                        }

                    }
                });
    }

    private void verifyRegisterCode(String verifyCode) {
        RegisterAccountInfo account = mAccount;

        account.verifyCode = verifyCode;
        getView().getUIDelegate().showLoading(false);
        CaptchaVerifyParams params = new CaptchaVerifyParams();
        params.account = account.getAPIAccount();
        params.catpcha = verifyCode;
        params.isDelete = "0";
        LogUtil.d("xxxx");
        UserManager.getInstance().verifyCaptcha(params)
                .subscribe(new SimpleRxSubscriber<ApiResult<EmptyResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mDisposables.add(d);
                    }

                    @Override
                    public void onError(RxException e) {
                        super.onError(e);
                        ExceptionHelper.handleException(e.getCode(), e.getMessage());
                        getView().getUIDelegate().hideLoading();
                    }

                    @Override
                    public void onNext(ApiResult<EmptyResponse> result) {
                        super.onNext(result);
                        getView().getUIDelegate().hideLoading();
                        onCheckVerifyCodeResult(result.code == 0);
                    }
                });
    }

    private void onCheckVerifyCodeResult(boolean success) {
        LogUtil.d("success:" + success);
        if (success) {
            toRegisterPassword();
        } else {
            getView().getUIDelegate().toastShort(mContext.getString(R.string.account_register_verify_code_incorrect));
        }
    }

    private void toRegisterPassword() {
        RegisterPasswordActivity.open(mContext, mAccount);
    }

    private void onVerificationCodeTimerCountDown(long needCountTime) {
        getView().onSendCodeComplete();
        final long finalNeedCountTime = needCountTime;
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(needCountTime)
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable d) {
                        mDisposables.add(d);
                    }
                })
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long second) {
                        getView().updateVerificationCodeTime((int) (finalNeedCountTime - second - 1));
                    }
                })
                .subscribe();
    }
}
