/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.bridge.api;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.ubtedu.alpha1x.core.base.delegate.EventDelegate;
import com.ubtedu.bridge.BaseBridgeAPI;
import com.ubtedu.bridge.BridgeBoolean;
import com.ubtedu.bridge.BridgeObject;
import com.ubtedu.bridge.BridgeResult;
import com.ubtedu.bridge.OnCallback;
import com.ubtedu.ukit.BuildConfig;
import com.ubtedu.ukit.application.PrivacyInfo;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.error.BluetoothCommonErrorHelper;
import com.ubtedu.ukit.bluetooth.processor.CommonBoardNetworkStateHolder;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.common.eventbus.VisibilityEvent;
import com.ubtedu.ukit.common.utils.PlatformUtil;
import com.ubtedu.ukit.home.HomeActivity;
import com.ubtedu.ukit.menu.region.RegionInfo;
import com.ubtedu.ukit.menu.settings.Settings;
import com.ubtedu.ukit.menu.settings.TargetDevice;
import com.ubtedu.ukit.project.Workspace;
import com.ubtedu.ukit.project.bridge.BluetoothCommunicator;
import com.ubtedu.ukit.project.bridge.arguments.AppInfoArguments;
import com.ubtedu.ukit.project.bridge.arguments.ConfigArguments;
import com.ubtedu.ukit.project.bridge.arguments.NetworkStateArguments;
import com.ubtedu.ukit.project.bridge.arguments.ScreenBorder;
import com.ubtedu.ukit.project.vo.ModelInfo;
import com.ubtedu.ukit.user.UserManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.Iterator;


/**
 * 处理JS和Unity3D共有逻辑的业务接口。
 *
 * @Author qinicy
 * @Date 2018/12/3
 **/
public class GlobalAPIs extends BaseBridgeAPI {
    public final static String NAME_SPACE = "global";
    private Handler mHandler;
    private UKitApplication mAPP;
    private APIHelper mHelper;

    public GlobalAPIs() {
        mHandler = new Handler(Looper.getMainLooper());
        mAPP = UKitApplication.getInstance();
        mHelper = new APIHelper();
    }

    @JavascriptInterface
    public BridgeResult toast(final String message) {
        return mHelper.toast(message);
    }

    @JavascriptInterface
    public void prompt(BridgeObject jsonObject, final OnCallback callback) {
        mHelper.prompt(jsonObject, callback);
    }

    @JavascriptInterface
    public void promptEdit(BridgeObject jsonObject, final OnCallback callback) {
        mHelper.promptEdit(jsonObject, callback);
    }

    @JavascriptInterface
    public void interactivePromptEdit(final Number isBlockly, BridgeObject jsonObject, final OnCallback callback) {
        mHelper.interactivePromptEdit(isBlockly, jsonObject, callback);
    }

    private UKitBaseActivity getActivity() {
        return ActivityHelper.getResumeActivity();
    }

    @JavascriptInterface
    public BridgeResult showLoading(BridgeObject args) {
        return mHelper.showLoading(args);
    }

    @JavascriptInterface
    public BridgeResult hideLoading() {
        return mHelper.hideLoading();
    }

    @JavascriptInterface
    public BridgeResult isBluetoothConnect() {
        BridgeResult result = BridgeResult.SUCCESS();
        boolean isConnect = BluetoothHelper.isConnected();
        if (isConnect) {
            result.data = BridgeBoolean.TRUE();
        } else {
            result.data = BridgeBoolean.FALSE();
        }
        return result;
    }

    @JavascriptInterface
    public BridgeResult isConditionSatisfy() {
        BridgeResult result = BridgeResult.SUCCESS();
        boolean satisfy = true;
        if (!BluetoothHelper.isConnected()) {
            satisfy = false;
            BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.BLUETOOTH_NOT_CONNECTED);
        } else if (Settings.isChargingProtection() && BluetoothHelper.isCharging()) {
            satisfy = false;
            BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.CHARGING_PROTECTION);
        }
        if (satisfy) {
            result.data = BridgeBoolean.TRUE();
        } else {
            result.data = BridgeBoolean.FALSE();
        }
        return result;
    }

    @JavascriptInterface
    public BridgeResult toggleShowHomeTabbar(final Number isShow) {
        BridgeResult result = BridgeResult.SUCCESS();
        if (isShow != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() instanceof HomeActivity) {
                        ((HomeActivity) getActivity()).toggleToggleShowHomeTabbar(BridgeBoolean.isTrue(isShow.intValue()));
                    }
                }
            });
        }
        return result;
    }

    @JavascriptInterface
    public BridgeResult toggleShowProjectTabbar(final Number isShow) {
        BridgeResult result = BridgeResult.SUCCESS();
        EventBus.getDefault().post(new VisibilityEvent(BridgeBoolean.isTrue(isShow.intValue())));
        return result;
    }

    @JavascriptInterface
    public BridgeResult getModelInfo() {
        BridgeResult result = BridgeResult.SUCCESS();
        if (Workspace.getInstance().getProject() != null) {
            ModelInfo modelInfo = Workspace.getInstance().getProject().modelInfo;
            if (modelInfo != null) {
                result.data = modelInfo;
            } else {
                result.data = new ModelInfo();
            }
        }
        return result;
    }

    @JavascriptInterface
    public BridgeResult getScreenBorder() {
        BridgeResult result = BridgeResult.SUCCESS();
        result.data = new ScreenBorder(0, 0, 0, 0);
        return result;
    }

    @JavascriptInterface
    public BridgeResult getAppInfo() {
        BridgeResult result = BridgeResult.SUCCESS();
        AppInfoArguments arguments = new AppInfoArguments();
        arguments.appId = String.valueOf(PrivacyInfo.getUBTAppId());
        arguments.appKey = PrivacyInfo.getAppKey();
        arguments.deviceId = UKitApplication.getInstance().generateDeviceToken();
        arguments.authorization = UserManager.getInstance().getLoginUserToken();
        arguments.logEnabled = BridgeBoolean.wrap(BuildConfig.DEBUG);
        result.data = arguments;
        return result;
    }

    @JavascriptInterface
    public BridgeResult getConfigs() {
        BridgeResult result = BridgeResult.SUCCESS();
        ConfigArguments arguments = new ConfigArguments();
        String lang = Settings.getUnityLanguage();
        if (TextUtils.isEmpty(lang)) {
            lang = RegionInfo.LANGUAGE_CN;
        }
        arguments.language = lang;
        arguments.tagName = Settings.getUnityTag();
        arguments.devicePlatform = PlatformUtil.getDevicePlatform();
        arguments.hasAccelerometerSensor = BridgeBoolean.wrap(PlatformUtil.hasAccelerometerSensor());
        //如果没有选择过，返回ukit2.0主控
        int target = Settings.getTargetDevice();
        if (target == TargetDevice.NONE) {
            target = TargetDevice.UKIT2;
        }
        arguments.deviceVersion = target;
        result.data = arguments;
        return result;
    }

    @JavascriptInterface
    public void reportEvent(BridgeObject e) {
        EventDelegate.Event event = new EventDelegate.Event();
        event.id = e.optString("id");
        event.type = e.optInt("type");

        event.sessionStart = BridgeBoolean.isTrue(e.optInt("sessionStart"));
        event.page = e.optString("page");
        JSONObject obj = e.optJSONObject("keyStrings");
        if (obj != null) {
            Iterator<String> iterator = obj.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                event.keyStrings.put(key, obj.optString(key));
            }
        }

        EventDelegate delegate = mAPP.getEventDelegate();

        if (event.id != null && delegate != null) {
            delegate.onEvent(mAPP, event);
        }
    }

    @JavascriptInterface
    public BridgeResult getNetworkState() {
        BridgeResult result = BridgeResult.SUCCESS();
        result.data = NetworkStateArguments.getNetworkState();
        return result;
    }

    @Override
    public String getNamespace() {
        return NAME_SPACE;
    }
}