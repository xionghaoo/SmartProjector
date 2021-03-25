package com.ubtrobot.ifly

import android.content.Context
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechUtility

class IflyManager {
    companion object {

        var APPID: String? = null

        fun initial(context: Context, appId: String) {
            SpeechUtility.createUtility(context, SpeechConstant.APPID +"=$appId")
            APPID = appId
        }
    }
}