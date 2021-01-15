package com.ubtedu.ukit.user.register;

import androidx.annotation.NonNull;

import com.ubtedu.base.net.rxretrofit.mode.ApiResult;
import com.ubtedu.ukit.common.annotation.CommonType;
import com.ubtedu.ukit.menu.region.RegionHelper;
import com.ubtedu.ukit.menu.region.RegionInfo;
import com.ubtedu.ukit.menu.settings.Settings;
import com.ubtedu.ukit.user.UserManager;
import com.ubtedu.ukit.user.vo.UserApiParam;
import com.ubtedu.ukit.user.vo.response.EmptyResponse;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class VerifyCodeCounter {
    public static VerifyCodeCounter getInstance() {
        return Holder.INSTANCE;
    }

    private VerifyCodeCounter() {
    }

    private static class Holder {
        private static final VerifyCodeCounter INSTANCE = new VerifyCodeCounter();
    }

    private long mLastRequestTime;
    private RegisterAccountInfo mLastRequestAccount;

    public long getLastRequestTime() {
        return mLastRequestTime;
    }

    public RegisterAccountInfo getLastRequestAccount() {
        return mLastRequestAccount;
    }

    public Observable<ApiResult<EmptyResponse>> requestVerifyCode(@NonNull RegisterAccountInfo account) {
        final UserApiParam param = new UserApiParam();
        param.account = account.getAPIAccount();
        param.accountType = account.isPhoneAccount() ? UserApiParam.LOGIN_TYPE_PHONE : UserApiParam.LOGIN_TYPE_EMAIL;
        param.purpose = account.isRegister() ? CommonType.CAPTCHA_FOR_REG_OR_BIND : CommonType.CAPTCHA_FOR_RESET_PASSWORD;
        // 做国内手机号格式校验
        param.zhFlag = RegionInfo.REGION_CN.equals(Settings.getRegion().name) ? 1 : 0;

        param.lanCode = getLanguageCode();
        return UserManager.getInstance().getCaptcha(param).doOnNext(new Consumer<ApiResult<EmptyResponse>>() {
            @Override
            public void accept(ApiResult<EmptyResponse> emptyResponseApiResult) throws Exception {
                if (emptyResponseApiResult.sourceRawData.code == 0) {
                    mLastRequestTime = System.currentTimeMillis();
                    mLastRequestAccount = new RegisterAccountInfo(account);
                }
            }
        });
    }

    private String getLanguageCode() {
        return RegionHelper.getAPIRegionLanguage();
    }

    public void clear() {
        mLastRequestTime = 0;
        mLastRequestAccount = null;
    }
}
