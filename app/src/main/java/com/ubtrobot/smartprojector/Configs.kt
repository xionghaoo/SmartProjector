package com.ubtrobot.smartprojector

class Configs {
    companion object {
        const val MQTT_SERVER_URI = "tcp://broker.emqx.io:1883"

        const val HOST = "https://${BuildConfig.DOMAIN_RELEASE}/"

        const val PACKAGE_NAME = BuildConfig.APPLICATION_ID

        // ucode网页版
        const val UCODE_URL = "http://ide.ubtrobot.com/"

        const val DINGDANG_APPKEY = "93200b80b2f311eb9b89153abe2c55df"
        const val DINGDANG_ACCESSTOKEN = "5801264bdffa446db7607998869f2b0c"

        const val ubtAppId = "940010010"
        const val ubtProduct = "90101"
        const val ubtAppKey = "c3ba293280655a66165dd8cdf4e58217"

        const val agoraAppId = "cf309a3e129847bcb31703c7e6283721"
        const val agoraChannel = "test"
        const val agoraPeerUserId = "1234"
    }
}