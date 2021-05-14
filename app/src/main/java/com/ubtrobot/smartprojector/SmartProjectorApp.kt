package com.ubtrobot.smartprojector

import android.app.Application
import android.util.Log
import com.facebook.drawee.backends.pipeline.Fresco
import com.liulishuo.filedownloader.FileDownloader
import com.tuya.smart.commonbiz.bizbundle.family.api.AbsBizBundleFamilyService
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk
import com.tuya.smart.wrapper.api.TuyaWrapper
import com.ubtrobot.smartprojector.ui.login.LoginActivity
import com.ubtrobot.smartprojector.utils.ToastUtil
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import xh.zero.voice.VoiceManager


@HiltAndroidApp
class SmartProjectorApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        if (BuildConfig.DEBUG) {
            TuyaHomeSdk.setDebugMode(true)
        }

        // 请不要修改初始化顺序
        Fresco.initialize(this)
        // SDK 初始化
        TuyaHomeSdk.init(this)

        // 业务包初始化
        TuyaWrapper.init(this, { errorCode, urlBuilder -> // 路由未实现回调
            // urlBuilder.target is a router address, urlBuilder.params is a router params
            // urlBuilder.target 目标路由， urlBuilder.params 路由参数
            Log.e("router not implement", urlBuilder.target + urlBuilder.params.toString())
        }) { serviceName -> // 服务未实现回调
            Log.e("service not implement", serviceName)
        }
        TuyaOptimusSdk.init(this)

        // 注册家庭服务，商城业务包可以不注册此服务
        TuyaWrapper.registerService(
            AbsBizBundleFamilyService::class.java, BizBundleFamilyServiceImpl()
        )

        TuyaHomeSdk.setOnNeedLoginListener {
            Timber.d("涂鸦登录失效")
            ToastUtil.showToast(it, "涂鸦登录失效")
            LoginActivity.startWithNewTask(it)
        }

        // 文件下载
        FileDownloader.setup(this)

        // 语音初始化
//        VoiceManager.initial(this, Configs.TENCENT_VOICE_APPKEY)
    }
}