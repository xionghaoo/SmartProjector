package xh.zero.agora_call

import android.content.Context
import android.util.Log
import io.agora.rtc.Constants
import io.agora.rtc.RtcEngine
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import io.agora.rtm.RtmCallManager
import io.agora.rtm.RtmClient
import xh.zero.agora_call.utils.FileUtil

class AgoraCallManager {

    companion object {
        const val TAG = "AgoraCallManager"
    }

    private lateinit var mRtcEngine: RtcEngine
    private lateinit var mRtmClient: RtmClient
    private lateinit var rtmCallManager: RtmCallManager
    private var mEventListener: EngineEventListener? = null

    fun initial(context: Context, isDebug: Boolean, appId: String, userId: String, rtcToken: String, rtmToken: String) {
        mEventListener = EngineEventListener()

        mRtcEngine = RtcEngine.create(context, appId, mEventListener)
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
        mRtcEngine.enableDualStreamMode(true)
        mRtcEngine.enableVideo()
        mRtcEngine.setLogFile(FileUtil.rtmLogFile(context))

        mRtmClient = RtmClient.createInstance(context, appId, mEventListener)
        mRtmClient.setLogFile(FileUtil.rtmLogFile(context))

        if (isDebug) {
            mRtcEngine.setParameters("{\"rtc.log_filter\":65535}")
            mRtmClient.setParameters("{\"rtm.log_filter\":65535}")
        }

        rtmCallManager = mRtmClient.rtmCallManager
        rtmCallManager.setEventListener(mEventListener)

        mRtmClient.login(rtmToken, userId, object : ResultCallback<Void?> {
            override fun onSuccess(aVoid: Void?) {
                Log.i(TAG, "rtm client login success")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                Log.i(TAG, "rtm client login failed:" + errorInfo.errorDescription)
            }
        })
    }

    fun destroy() {
        RtcEngine.destroy()

        mRtmClient.logout(object : ResultCallback<Void?> {
            override fun onSuccess(aVoid: Void?) {
                Log.i(TAG, "rtm client logout success")
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                Log.i(TAG, "rtm client logout failed:" + errorInfo.errorDescription)
            }
        })
    }
}