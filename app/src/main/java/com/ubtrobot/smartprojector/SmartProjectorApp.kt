package com.ubtrobot.smartprojector

import android.app.Application
import com.liulishuo.filedownloader.FileDownloader
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.ubtrobot.smartprojector.ui.login.LoginActivity
import com.ubtrobot.smartprojector.utils.ToastUtil
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class SmartProjectorApp : Application() {

    override fun onCreate() {
        super.onCreate()

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
            LoginActivity.startWithNewTask(it)
        }

        FileDownloader.setup(this)
    }
}