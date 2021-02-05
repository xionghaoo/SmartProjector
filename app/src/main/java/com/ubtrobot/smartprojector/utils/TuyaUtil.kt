package com.ubtrobot.smartprojector.utils

import android.content.Context
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.sdk.api.IDevListener
import timber.log.Timber

class TuyaUtil {
    companion object {
        fun registerTuyaDeviceListener(context: Context, devId: String) {
            val device = TuyaHomeSdk.newDeviceInstance(devId)
            device.unRegisterDevListener()
            device.registerDevListener(object : IDevListener {
                override fun onDpUpdate(devId: String?, dpStr: String?) {
                    Timber.d("onDpUpdate: $devId, $dpStr")
                    ToastUtil.showToast(context, "收到SOS警报")
                }

                override fun onRemoved(devId: String?) {
                    Timber.d("onRemoved: $devId")
                }

                override fun onStatusChanged(devId: String?, online: Boolean) {
                    Timber.d("onStatusChanged: $devId")
                }

                override fun onNetworkStatusChanged(devId: String?, status: Boolean) {
                    Timber.d("onNetworkStatusChanged: $devId")
                }

                override fun onDevInfoUpdate(devId: String?) {
                    Timber.d("onDevInfoUpdate: $devId")
                }
            })
        }
    }
}