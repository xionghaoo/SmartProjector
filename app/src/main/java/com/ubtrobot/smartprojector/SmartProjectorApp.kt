package com.ubtrobot.smartprojector

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.liulishuo.filedownloader.FileDownloader
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.ubtrobot.smartprojector.receivers.ConnectionStateMonitor
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