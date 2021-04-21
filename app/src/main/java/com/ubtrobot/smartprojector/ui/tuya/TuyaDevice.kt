package com.ubtrobot.smartprojector.ui.tuya

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class TuyaDevice(
        val name: String,
        val iconUrl: String?,
        val id: String,
        val productionId: String,
        val isOnline: Boolean,
        val isZigBeeWifi: Boolean,
        val isZigBeeSubDevice: Boolean,
        val categoryCode: String,
        val schema: String,
        val dps: List<TuyaDeviceCmd>,
        var isInCategory: Boolean = false
) : Parcelable

@Keep
@Parcelize
data class TuyaDeviceCmd(
    val deviceId: String,
    val key: String,
    val value: String?
) : Parcelable