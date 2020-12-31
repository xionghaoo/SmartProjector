package com.ubtrobot.smartprojector

import android.app.Application
import com.liulishuo.filedownloader.FileDownloader
import com.tuya.smart.home.sdk.TuyaHomeSdk
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
        
        FileDownloader.setup(this)
    }
}