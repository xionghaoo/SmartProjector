package com.ubtrobot.smartprojector.ui.tuya

import androidx.annotation.Keep

@Keep
class DeviceCategory {
    val name: String = ""
    var order: Int = 0
    val categoryCodes: List<String> = emptyList()
    @Transient
    val devices: ArrayList<TuyaDevice> = ArrayList()
}