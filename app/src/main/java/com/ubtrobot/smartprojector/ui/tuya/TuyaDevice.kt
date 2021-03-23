package com.ubtrobot.smartprojector.ui.tuya

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TuyaDevice(
        val name: String,
        val iconUrl: String?,
        val id: String,
        val productionId: String,
        val isOnline: Boolean,
        val isZigBeeWifi: Boolean,
        val categoryCode: String,
        val schema: String,
        val dps: List<TuyaDeviceCmd>
) : Parcelable

@Parcelize
data class TuyaDeviceCmd(
    val deviceId: String,
    val key: String,
    val value: String?
) : Parcelable