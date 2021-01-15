/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.home;

import com.ubtedu.alpha1x.utils.GsonUtil;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.alpha1x.utils.SharedPreferenceUtils;
import com.ubtedu.base.net.manager.NetworkManager;
import com.ubtedu.base.net.rxretrofit.exception.RxException;
import com.ubtedu.base.net.rxretrofit.mode.ApiResult;
import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.bridge.BridgeObject;
import com.ubtedu.bridge.BridgeResult;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.common.analysis.Events;
import com.ubtedu.ukit.common.analysis.UBTReporter;
import com.ubtedu.ukit.common.exception.ExceptionHelper;
import com.ubtedu.ukit.common.exception.ResultCode;
import com.ubtedu.ukit.common.ota.OtaResponse;
import com.ubtedu.ukit.common.ota.RxOtaHelper;
import com.ubtedu.ukit.menu.about.MenuAboutFragment;
import com.ubtedu.ukit.project.bridge.BridgeCommunicator;
import com.ubtedu.ukit.project.bridge.functions.Unity3DFunctions;
import com.ubtedu.ukit.user.UserConsts;
import com.ubtedu.ukit.user.UserManager;
import com.ubtedu.ukit.user.vo.UserInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qinicy
 * @date 2018/11/07
 */
public class HomePresenter extends HomeContracts.Presenter {

    private boolean mHadCheckedLoginTokenExpired;

    private boolean mSkipDisconnectBt = false;

    public HomePresenter() {
    }

    @Override
    public void checkLoginOverdue() {
        boolean is7DaysOverdue = checkLogin7DaysOverdue();
        if (!is7DaysOverdue) {
            checkLoginTokenExpired();
        }
    }
    @Override
    public void reportActiveUserEvent() {
        UserInfo user = UserManager.getInstance().getCurrentUser();
        Map<String, String> args = new HashMap<>();
        args.put("userId", user.getUserID());
        UBTReporter.onEvent(Events.Ids.app_user_active, args);
    }

    @Override
    public void loadUnityInfo() {
        if (BridgeCommunicator.getInstance().getUnity3DBridge().isCommunicable()) {
            BridgeCommunicator.getInstance().getUnity3DBridge().call(Unity3DFunctions.getUnityInfo, null)
                    .subscribe(new SimpleRxSubscriber<BridgeResult>() {
                        @Override
                        public void onNext(BridgeResult result) {
                            super.onNext(result);
                            if (result.data instanceof BridgeObject) {
                                BridgeObject obj = (BridgeObject) result.data;
                                String version = obj.optString("version");
                                SharedPreferenceUtils.getInstance(UKitApplication.getInstance()).setValue(MenuAboutFragment.SP_UNITY_VERSION, version);
                            }
                        }
                    });
        }
    }

    private boolean checkLogin7DaysOverdue() {
        if (!UserManager.getInstance().isGuest()) {
            long lastLoginTime = SharedPreferenceUtils.getInstance(mContext).getLongValue(UserConsts.SP_GDPR_7_DAYS_OUT_TIME, 0);
            long nowTime = System.currentTimeMillis();
            LogUtil.d("lastLoginTime:" + lastLoginTime + " now:" + nowTime);
            if (nowTime - lastLoginTime > 7 * 24 * 60 * 60 * 1000) {
                ExceptionHelper.handleException(ResultCode.SERVER_LOGIN_TOKEN_EXPIRED, "");
                return true;
            } else {
                //新需求：如果不大于7天，重置
                SharedPreferenceUtils.getInstance(mContext).setValue(UserConsts.SP_GDPR_7_DAYS_OUT_TIME, System.currentTimeMillis());
            }
        }
        return false;
    }

    @Override
    public void checkLoginTokenExpired() {
        if (!NetworkManager.getInstance().isNetworkConnected()) {
            return;
        }
        if (!mHadCheckedLoginTokenExpired && !UserManager.getInstance().isGuest()) {
            mHadCheckedLoginTokenExpired = true;
            // 用来验证token是否过期。
            UserManager.getInstance().getUserInfo(UserManager.getInstance().getLoginUserToken())
                    .subscribe(new SimpleRxSubscriber<ApiResult<UserInfo>>() {
                        @Override
                        public void onError(RxException e) {
                            super.onError(e);
                            boolean isTokenExpired = ExceptionHelper.isTokenExpired(e);
                            if (isTokenExpired) {
                                ExceptionHelper.handleException(ResultCode.SERVER_LOGIN_TOKEN_EXPIRED, "");
                            }
                        }
                    });
        }
    }

    @Override
    public void otaUpgrade() {
        //为了加快其它OTA的下载速度，先检查下载其它OTA升级，再下载Blockly
        LogUtil.d("checkAllComponentsUpgradeWithoutBlockly");
        RxOtaHelper.checkAllComponentsUpgrade()
                .subscribe(new SimpleRxSubscriber<OtaResponse>() {
                    @Override
                    public void onNext(OtaResponse otaResponse) {
                        super.onNext(otaResponse);
                        //方法内部已经处理其它逻辑，这里只打log
                        LogUtil.i("otaUpgrade:" + GsonUtil.get().toJson(otaResponse));
                    }
                    @Override
                    public void onError(RxException e) {
                        super.onError(e);
                        LogUtil.e("Check otaUpgrade info exception!");
                        e.printStackTrace();
                    }
                });
    }


    @Override
    public void release() {
        super.release();
        NetworkManager.getInstance().unregisterAllObservers();
        if (!mSkipDisconnectBt && BluetoothHelper.isConnected()) {
            BluetoothHelper.disconnect();
        }
    }

    @Override
    public void skipDisconnectBt(boolean skip) {
        mSkipDisconnectBt = skip;
    }

}
