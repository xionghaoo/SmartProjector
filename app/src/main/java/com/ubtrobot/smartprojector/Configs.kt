package com.ubtrobot.smartprojector

class Configs {
    companion object {
        const val MQTT_SERVER_URI = "tcp://broker.emqx.io:1883"

        const val HOST = "http://${BuildConfig.DOMAIN_RELEASE}"

        // ucode网页版
        const val UCODE_URL = "http://ide.ubtrobot.com/"

        const val TENCENT_DINGDANG_APPKEY = "93200b80b2f311eb9b89153abe2c55df"

        const val DEFAULT_HOME = "default-home"
    }
}