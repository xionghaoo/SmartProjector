package com.ubtrobot.smartprojector

import android.app.Application
import android.content.Context
import android.text.TextUtils
import com.liulishuo.filedownloader.FileDownloader
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.ubtedu.alpha1x.core.base.delegate.EventDelegate
import com.ubtedu.alpha1x.utils.AppUtil
import com.ubtedu.alpha1x.utils.LogUtil
import com.ubtedu.alpha1x.utils.SharedPreferenceUtils
import com.ubtedu.base.UBTCrashManager
import com.ubtedu.base.net.manager.NetworkManager
import com.ubtedu.deviceconnect.libs.base.URoSDK
import com.ubtedu.ukit.application.BasicEventDelegate
import com.ubtedu.ukit.application.UKitApplication
import com.ubtedu.ukit.bluetooth.ota.OtaHelper
import com.ubtedu.ukit.common.cloud.CloudStorageManager
import com.ubtedu.ukit.common.exception.ExceptionHelper
import com.ubtedu.ukit.common.files.FileHelper
import com.ubtedu.ukit.common.locale.LanguageUtil
import com.ubtedu.ukit.common.utils.AnimatorHelper
import com.ubtedu.ukit.menu.settings.Settings
import com.ubtedu.ukit.user.UserManager
import com.ubtrobot.smartprojector.utils.ToastUtil
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class SmartProjectorApp : UKitApplication() {
    override fun onCreate() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        TuyaHomeSdk.init(this)
        if (BuildConfig.DEBUG) {
            TuyaHomeSdk.setDebugMode(true)
        }

        TuyaHomeSdk.setOnNeedLoginListener {
            Timber.d("涂鸦登录失效")
            ToastUtil.showToast(it, "涂鸦登录失效")
        }

        super.onCreate()

        FileDownloader.setup(this)
    }
}