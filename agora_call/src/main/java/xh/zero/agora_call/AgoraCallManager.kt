package xh.zero.agora_call

import android.content.Context
import android.util.Log
import io.agora.rtc.Constants
import io.agora.rtc.RtcEngine
import io.agora.rtm.*
import xh.zero.agora_call.agora.EngineEventListener
import xh.zero.agora_call.agora.IEventListener
import xh.zero.agora_call.utils.FileUtil

class AgoraCallManager(
    private val context: Context,
    private val isDebug: Boolean,
    private val appId: String
) {

    companion object {
        const val TAG = "AgoraCallManager"
    }

    var rtcEngine: RtcEngine
        private set
    var rtmClient: RtmClient
        private set
    var rtmCallManager: RtmCallManager
        private set
    private var eventListener: EngineEventListener? = null

    var localInvitation: LocalInvitation? = null
    var remoteInvitation: RemoteInvitation? = null

    init {
        eventListener = EngineEventListener()

        rtcEngine = RtcEngine.create(context, appId, eventListener)
        rtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
        rtcEngine.enableDualStreamMode(true)
        rtcEngine.enableVideo()
        rtcEngine.setLogFile(FileUtil.rtmLogFile(context))

        rtmClient = RtmClient.createInstance(context, appId, eventListener)
        rtmClient.setLogFile(FileUtil.rtmLogFile(context))

        if (isDebug) {
//            rtcEngine.setParameters("{\"rtc.log_filter\":65535}")
//            rtmClient.setParameters("{\"rtm.log_filter\":65535}")
        }

        rtmCallManager = rtmClient.rtmCallManager
        rtmCallManager.setEventListener(eventListener)
    }

    fun login(rtmToken: String?, userId: String) {
        rtmClient.login(rtmToken, userId, object : ResultCallback<Void?> {
            override fun onSuccess(aVoid: Void?) {
                Log.i(TAG, "rtm client login success: ${userId}")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                Log.i(TAG, "rtm client login failed:" + errorInfo.errorDescription)
            }
        })
    }

    fun registerEventListener(listener: IEventListener?) {
        eventListener?.registerEventListener(listener)
    }

    fun removeEventListener(listener: IEventListener?) {
        eventListener?.removeEventListener(listener)
    }

    fun destroy() {
        RtcEngine.destroy()

        rtmClient.logout(object : ResultCallback<Void?> {
            override fun onSuccess(aVoid: Void?) {
                Log.i(TAG, "rtm client logout success")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                Log.i(TAG, "rtm client logout failed:" + errorInfo.errorDescription)
            }
        })
    }
}