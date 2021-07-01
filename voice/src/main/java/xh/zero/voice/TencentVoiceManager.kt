package xh.zero.voice

import android.content.Context
import android.util.Log
import com.tencent.ai.tvs.api.TVSApi
import com.tencent.ai.tvs.capability.userinterface.data.ASRTextMessageBody
import com.tencent.ai.tvs.capability.userinterface.data.ASRTextMessageBody.AsrClassifierInfo
import com.tencent.ai.tvs.gateway.data.GatewayRespHeader
import com.tencent.ai.tvs.tvsinterface.*

class TencentVoiceManager(
    private val context: Context,
    private val appKey: String,
    private val accessToken: String
) {

    companion object {
        private const val TAG = "TencentVoiceManager"
    }

    private var pluginProvider: DefaultPluginProvider = DefaultPluginProvider(context, DingdangMediaPlayer(
        TestMediaPlayer(context)
    ))

    fun initial(dsn: String, appVersion: String) {

        val ret = TVSApi.getInstance().init(pluginProvider, context, appKey, accessToken, dsn, object : IAuthInfoListener {
            override fun onMissingClientId(p0: Boolean) {
                // 本地没有clientId，需要传入ClientId，或访客授权
                Log.d(TAG, "onMissingClientId: $p0")

                // 访客授权
                TVSApi.getInstance().authManager.setGuestClientId()
            }

            override fun onInitTokenSucceed(p0: String?) {
                // SDK首次启动后，获取Token或者刷票成功，定时刷票不会回调
                Log.d(TAG, "onInitTokenSucceed: $p0")
            }

            override fun onInitTokenFailed(
                p0: String?,
                p1: Int,
                p2: String?,
                p3: GatewayRespHeader?,
                p4: String?
            ) {
                // 获取Token或者刷票失败，云端未注册成功，语音功能无法使用，需要重试
                Log.d(TAG, "onInitTokenFailed: $p0")
            }
        }, mapOf(
            // 设置设备版本号
            Pair(TVSApi.QUA_KEY_VN, "0.0.0.1"),
            Pair(TVSApi.QUA_KEY_APPVN, appVersion)
        ))
        if (ret == ResultCode.RESULT_OK) {
            Log.d(TAG, "小微初始化成功")
            initDialogManager()
        } else {
            Log.d(TAG, "小微初始化失败")
        }
    }

    private fun initDialogManager() : Int {
        val dialogManager = TVSApi.getInstance().dialogManager
        val ret = dialogManager.init("/sdcard/tencent/aifile", true)
        dialogManager.setOuterAudioRecorder(VoiceRecord())
//        dialogManager.setWakeupDialogOptions(DialogOptions().)
        dialogManager.enableVoiceCapture()
        if (ret == ResultCode.RESULT_OK) {
            // 注册语音事件回调
            dialogManager.addRecognizeListener(recognizeListener)
            dialogManager.addTTSListener(ttsListener)
            dialogManager.addWakeupListener(wakeupListener)
        }
        return ret
    }

    /**
     * 启动语音识别
     *
     * @return dialogRequestId 本次会话的唯一id，后续结果的回调均会携带此id
     */
    fun startRecognize() {
        val dialogManager = TVSApi.getInstance().dialogManager
        if (dialogManager != null) {
            Log.d(TAG, "启动语音识别")
            val dialogOptions = DialogOptions()
            dialogOptions.tag = "tadpole_voice"
            // 如果要保存音频
            dialogManager.startRecognize(IRecognizeListener.RECO_TYPE_MANUAL, dialogOptions, null)
        }
    }

    fun playPrev() {
        TVSApi.getInstance().mediaPlayerManager.playPrevMedia(object : IPlaybackListener {
            override fun onSucceed() {
                Log.d(TAG, "播放上一曲成功")
            }

            override fun onFailed(p0: Int, p1: String?) {
                Log.d(TAG, "播放上一曲失败: $p0, $p1")
            }
        })
    }

    fun playNext() {
        TVSApi.getInstance().mediaPlayerManager.playNextMedia(object : IPlaybackListener {
            override fun onSucceed() {
                Log.d(TAG, "播放下一曲成功")
            }

            override fun onFailed(p0: Int, p1: String?) {
                Log.d(TAG, "播放下一曲失败: $p0, $p1")
            }
        })
    }

    fun release() {
        val tvsApi = TVSApi.getInstance()
//        tvsApi.removeCommunicationListener(mPhoneCallCallback)
//        tvsApi.removeCustomSkillHandler(mCustomSkillCallback)
//        tvsApi.removeCustomDataHandler(mCustomDataCallback)
        val dialogManager = tvsApi.dialogManager
        dialogManager?.setOuterAudioRecorder(null)
        dialogManager?.removeRecognizeListener(recognizeListener)
        dialogManager?.removeWakeupListener(wakeupListener)
        dialogManager?.removeTTSListener(ttsListener)
        tvsApi.release()
    }

    /**
     * 语音识别流程的状态回调
     */
    private val recognizeListener: IRecognizeListener = object : IRecognizeListener {
        override fun onRecognizationStart(recoType: Int, dialogRequestId: String?, tag: String?) {
            Log.i(TAG, "onRecognizationStart : $dialogRequestId, tag : $tag")
            //demo在启动语音识别的时候，停止正在响铃的闹钟，接入方可根据闹钟UI，自行调用停止闹钟。
//            if (AlertControlManager.getInstance() != null
//                && AlertControlManager.getInstance().isAlertPlaying()
//            ) {
//                Log.i(com.tencent.dingdangsampleapp.TVSSDKProxy.TAG, "stopPlayingAlert ")
//                AlertControlManager.getInstance().stopPlayingAlert()
//            }
        }

        override fun onStartRecord(recoType: Int, dialogRequestId: String?, tag: String?) {
            Log.i(TAG, "onStartRecord : $dialogRequestId tag = $tag")
//            if (mListener != null) {
//                mListener.printLog("开始录音", false)
//            }
        }

        override fun onSpeechStart(recoType: Int, dialogRequestId: String?, tag: String?) {
            Log.i(TAG, "onSpeechStart : $dialogRequestId tag = $tag")
            // 没有本地VAD，这个回调不会触发
//            if (mListener != null) {
//                mListener.printLog("检测到说话开始", false)
//            }
        }

        override fun onSpeechEnd(recoType: Int, dialogRequestId: String?, tag: String?) {
            Log.i(TAG, "onSpeechEnd : $dialogRequestId tag = $tag")
//            if (mListener != null) {
//                mListener.printLog("检测到说话结束", false)
//            }
        }

        override fun onFinishRecord(recoType: Int, dialogRequestId: String?, tag: String?) {
            Log.i(TAG, "onFinishRecord : $dialogRequestId tag = $tag")
//            if (mListener != null) {
//                mListener.printLog("结束录音", false)
//            }
        }

        override fun onGetASRText(
            dialogRequestId: String?,
            asrText: String?,
            isFinal: Boolean,
            status: String?,
            userInfo: ASRTextMessageBody.UserInfo?,
            asrClassifierInfos: List<AsrClassifierInfo>?
        ) {
            Log.i(TAG, "onGetASRText : " + dialogRequestId + ", asrText : " + asrText + ", isFinal : " + isFinal + ", status : " + status)
//            if (mListener != null) {
//                mListener.onGetASRText(
//                    dialogRequestId,
//                    asrText,
//                    isFinal,
//                    status,
//                    userInfo,
//                    asrClassifierInfos
//                )
//            }
        }

        override fun onGetSessionId(dialogRequestId: String?, sessionId: String?) {
            Log.i(TAG, "onGetSessionId : $dialogRequestId sessionId = $sessionId")
//            if (mListener != null) {
//                mListener.printLog("获取到sessionId：$sessionId", false)
//            }
        }

        override fun onGetResponse(
            recoType: Int,
            dialogRequestId: String?,
            sessionId: String?,
            tag: String?
        ) {
            Log.i(TAG, "onGetResponse : $dialogRequestId sessionId = $sessionId tag = $tag")
//            if (mListener != null) {
//                mListener.printLog("收到服务器数据, sessionId：$sessionId", false)
//            }
        }

        override fun onRecognizationFinished(
            errorCode: Int,
            recoType: Int,
            dialogRequestId: String?,
            sessionId: String?,
            tag: String?,
            dialogExtraData: DialogExtraData?
        ) {
            Log.i(TAG,
                "onRecognizationFinished : " + dialogRequestId + ", sessionId : " + sessionId + " tag = " + tag
                    + " errorCode = " + errorCode
            )
//            if (dialogExtraData != null) {
//                Log.i(
//                    com.tencent.dingdangsampleapp.TVSSDKProxy.TAG,
//                    "onRecognizationFinished, dialogExtraData : $dialogExtraData"
//                )
//            }
//            if (mListener != null) {
//                mListener.printLog("会话结束", false)
//            }
        }

        override fun onVolume(volume: Int) {
            //Log.i(TAG, "onVolume : " + volume);
        }

        override fun onRecognizeError(
            errorCode: Int,
            errorMessage: String?,
            recoType: Int,
            dialogRequestId: String?,
            tag: String?
        ) {
            Log.i(TAG, "onRecognizeError : $dialogRequestId, errorCode : $errorCode, msg : $errorMessage tag = $tag")
//            if (mListener != null) {
//                mListener.printLog("出现错误，错误码：$errorCode, errorMessage : $errorMessage", false)
//            }
        }

        override fun onRecognizeCancel(recoType: Int, dialogRequestId: String?, tag: String?) {
            Log.i(TAG, "onRecognizeCancel : $dialogRequestId tag = $tag")
        }

        override fun onSaveRecord(tag: String?, recordPath: String?) {
            Log.i(TAG, "onSaveRecord : $tag, $recordPath")
        }
    }

    /**
     * TTS播报的回调
     */
    private val ttsListener: ITTSListener = object : ITTSListener {
        override fun onGetTTSText(dialogRequestId: String, text: String, tag: String) {
            Log.i(
                TAG,
                "onGetTTSText : $dialogRequestId tag = $tag, text = $text"
            )
        }

        override fun onTTSStarted(dialogRequestId: String, tag: String) {
            Log.i(
                TAG,
                "onTTSStarted : $dialogRequestId tag = $tag"
            )
//            if (mListener != null) {
//                mListener.printLog("语音播报开始", false)
//            }
        }

        override fun onTTSFinished(dialogRequestId: String, complete: Boolean, tag: String) {
            Log.i(
                TAG,
                "onTTSFinished : $dialogRequestId tag = $tag"
            )
//            if (mListener != null) {
//                mListener.printLog("语音播报结束", false)
//            }
        }
    }

    private val wakeupListener = object : IWakeUpListener {
        override fun onWakeUpSucceed(p0: String?) {
            Log.d(TAG, "onWakeUpSucceed: $p0")
        }

        override fun onModelRsp(p0: Int) {
            Log.d(TAG, "onModelRsp: $p0")
        }

        override fun onWakeUpDataRsp(p0: String?, p1: ByteArray?, p2: Int, p3: Int) {
            Log.d(TAG, "onWakeUpDataRsp: $p0")

        }
    }
}