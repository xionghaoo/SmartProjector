package com.ubtrobot.smartprojector.ui.tuya

data class TuyaDevice(
        val name: String,
        val id: String,
        val isOnline: Boolean,
        val isZigBeeWifi: Boolean,
        val categoryCode: String,
        val dps: List<TuyaDeviceCmd>
)

data class TuyaDeviceCmd(
    val deviceId: String,
    val key: String,
    val value: Any?
)