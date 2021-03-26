package com.ubtrobot.ifly

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.iflytek.cloud.*
import com.iflytek.cloud.util.ResourceUtil
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE
import java.lang.Exception

class WakeupManager(private val context: Context) {

    companion object {
        const val TAG = "WakeupManger"
    }

    private val curThresh = 1450
    private val keep_alive = "1"
    private val ivwNetMode = "0"
    private var ivm = VoiceWakeuper.createWakeuper(context, null)

    private val wakeupListener: WakeuperListener = object : WakeuperListener {
        override fun onBeginOfSpeech() {

        }

        override fun onResult(result: WakeuperResult?) {
            // 唤醒回调
            val json = result?.resultString
            if (json != null) {
                val r = Gson().fromJson(json, WakeupResult::class.java)
                Log.d(TAG, "唤醒回调成功： ${json}")
            }
        }

        override fun onError(p0: SpeechError?) {

        }

        override fun onEvent(p0: Int, p1: Int, p2: Int, p3: Bundle?) {

        }

        override fun onVolumeChanged(p0: Int) {

        }
    }

    fun startWakeupListening() {
        ivm = VoiceWakeuper.getWakeuper()
        ivm?.apply {
            try {
                // 清空参数
                setParameter(SpeechConstant.PARAMS, null)
                // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
                setParameter(SpeechConstant.IVW_THRESHOLD, "0:$curThresh")
                // 设置唤醒模式
                setParameter(SpeechConstant.IVW_SST, "wakeup")
                // 设置持续进行唤醒
                setParameter(SpeechConstant.KEEP_ALIVE, keep_alive)
                // 设置闭环优化网络模式
                setParameter(SpeechConstant.IVW_NET_MODE, ivwNetMode)
                // 设置唤醒资源路径
                setParameter(SpeechConstant.IVW_RES_PATH, getResource())
                // 设置唤醒录音保存路径，保存最近一分钟的音频
                setParameter(
                    SpeechConstant.IVW_AUDIO_PATH,
                    Environment.getExternalStorageDirectory().path + "/msc/ivw.wav"
                )
                setParameter(SpeechConstant.AUDIO_FORMAT, "wav")
                // 如有需要，设置 NOTIFY_RECORD_DATA 以实时通过 onEvent 返回录音音频流字节
                //mIvw.setParameter( SpeechConstant.NOTIFY_RECORD_DATA, "1" );
                // 启动唤醒
                /*	mIvw.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");*/

                // 如有需要，设置 NOTIFY_RECORD_DATA 以实时通过 onEvent 返回录音音频流字节
                //mIvw.setParameter( SpeechConstant.NOTIFY_RECORD_DATA, "1" );
                // 启动唤醒
                /*	mIvw.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");*/
                startListening(wakeupListener)
            } catch (e: Exception) {
                Log.e(TAG, "唤醒初始化失败")
                e.printStackTrace()
            }

        }
    }

    fun stopWakeupListening() {
        ivm?.stopListening()
    }

    private fun getResource(): String? {
        return ResourceUtil.generateResourcePath(
            context,
            RESOURCE_TYPE.assets,
            "ivw/${IflyManager.APPID}.jet"
        )
    }
}