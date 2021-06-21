package com.ubtrobot.smartprojector.ui.call

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.tuya.smart.utils.ToastUtil
import com.ubtrobot.smartprojector.Configs
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.core.vo.Status
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import timber.log.Timber
import xh.zero.agora_call.AgoraCallManager
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AgoraVoiceCallActivity : AppCompatActivity() {

    companion object {
        private val LOG_TAG = AgoraVoiceCallActivity::class.java.simpleName
        private const val PERMISSION_REQ_ID_RECORD_AUDIO = 22

        private const val EXTRA_CHANNEL_ID = "EXTRA_CHANNEL_ID"
        private const val EXTRA_PEER_UID = "EXTRA_PEER_UID"

        fun start(context: Context?, channelId: String?, peerId: String?) {
            val i = Intent(context, AgoraVoiceCallActivity::class.java)
            i.putExtra(EXTRA_CHANNEL_ID, channelId)
            i.putExtra(EXTRA_PEER_UID, peerId)
            context?.startActivity(i)
        }
    }

    @Inject
    lateinit var agoraCallManager: AgoraCallManager
    private val viewModel: AgoraCallViewModel by viewModels()

    private var channelId: String? = null
    private var peerId: Int? = null

    private var mRtcEngine: RtcEngine? = null
    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {

        /**
         * Occurs when a remote user (Communication)/host (Live Broadcast) leaves the channel.
         *
         * There are two reasons for users to become offline:
         *
         * Leave the channel: When the user/host leaves the channel, the user/host sends a goodbye message. When this message is received, the SDK determines that the user/host leaves the channel.
         * Drop offline: When no data packet of the user or host is received for a certain period of time (20 seconds for the communication profile, and more for the live broadcast profile), the SDK assumes that the user/host drops offline. A poor network connection may lead to false detections, so we recommend using the Agora RTM SDK for reliable offline detection.
         *
         * @param uid ID of the user or host who
         * leaves
         * the channel or goes offline.
         * @param reason Reason why the user goes offline:
         *
         * USER_OFFLINE_QUIT(0): The user left the current channel.
         * USER_OFFLINE_DROPPED(1): The SDK timed out and the user dropped offline because no data packet was received within a certain period of time. If a user quits the call and the message is not passed to the SDK (due to an unreliable channel), the SDK assumes the user dropped offline.
         * USER_OFFLINE_BECOME_AUDIENCE(2): (Live broadcast only.) The client role switched from the host to the audience.
         */
        override fun onUserOffline(uid: Int, reason: Int) { // Tutorial Step 4
            runOnUiThread { onRemoteUserLeft(uid, reason) }
        }

        /**
         * Occurs when a remote user stops/resumes sending the audio stream.
         * The SDK triggers this callback when the remote user stops or resumes sending the audio stream by calling the muteLocalAudioStream method.
         *
         * @param uid ID of the remote user.
         * @param muted Whether the remote user's audio stream is muted/unmuted:
         *
         * true: Muted.
         * false: Unmuted.
         */
        override fun onUserMuteAudio(uid: Int, muted: Boolean) { // Tutorial Step 6
            runOnUiThread { onRemoteUserVoiceMuted(uid, muted) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agora_voice_call)

        channelId = intent.getStringExtra(EXTRA_CHANNEL_ID)
        peerId = intent.getStringExtra(EXTRA_PEER_UID)?.toInt()

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
            initAgoraEngineAndJoinChannel()
        }
    }

    private fun initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine() // Tutorial Step 1
        if (channelId != null && peerId != null) {
            joinChannel(channelId!!, peerId!!)
        }
    }

    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        Log.i(LOG_TAG, "checkSelfPermission $permission $requestCode")
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(permission),
                requestCode
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode)
        when (requestCode) {
            PERMISSION_REQ_ID_RECORD_AUDIO -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    initAgoraEngineAndJoinChannel()
                } else {
                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO)
                    finish()
                }
            }
        }
    }

    private fun showLongToast(msg: String?) {
        runOnUiThread { Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show() }
    }

    override fun onDestroy() {
        super.onDestroy()
        leaveChannel()
        RtcEngine.destroy()
        mRtcEngine = null
    }

    // Tutorial Step 7
    fun onLocalAudioMuteClicked(view: View) {
        val iv = view as ImageView
        if (iv.isSelected) {
            iv.isSelected = false
            iv.clearColorFilter()
        } else {
            iv.isSelected = true
            iv.setColorFilter(resources.getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
        }

        // Stops/Resumes sending the local audio stream.
        mRtcEngine!!.muteLocalAudioStream(iv.isSelected)
    }

    // Tutorial Step 5
    fun onSwitchSpeakerphoneClicked(view: View) {
        val iv = view as ImageView
        if (iv.isSelected) {
            iv.isSelected = false
            iv.clearColorFilter()
        } else {
            iv.isSelected = true
            iv.setColorFilter(resources.getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
        }

        // Enables/Disables the audio playback route to the speakerphone.
        //
        // This method sets whether the audio is routed to the speakerphone or earpiece. After calling this method, the SDK returns the onAudioRouteChanged callback to indicate the changes.
        mRtcEngine!!.setEnableSpeakerphone(view.isSelected())
    }

    // Tutorial Step 3
    fun onEncCallClicked(view: View?) {
        finish()
    }

    // Tutorial Step 1
    private fun initializeAgoraEngine() {
        mRtcEngine = try {
            RtcEngine.create(baseContext, Configs.agoraAppId, mRtcEventHandler)
        } catch (e: Exception) {
            Log.e(LOG_TAG, Log.getStackTraceString(e))
            throw RuntimeException(
                """
    NEED TO check rtc sdk init fatal error
    ${Log.getStackTraceString(e)}
    """.trimIndent()
            )
        }
    }

    // Tutorial Step 2
    private fun joinChannel(channel: String, uid: Int) {
//        var accessToken: String? = getString(R.string.agora_access_token)
//        if (TextUtils.equals(accessToken, "") || TextUtils.equals(
//                accessToken,
//                "#YOUR ACCESS TOKEN#"
//            )
//        ) {
//            accessToken = null // default, no token
//        }

        // Sets the channel profile of the Agora RtcEngine.
        // CHANNEL_PROFILE_COMMUNICATION(0): (Default) The Communication profile. Use this profile in one-on-one calls or group calls, where all users can talk freely.
        // CHANNEL_PROFILE_LIVE_BROADCASTING(1): The Live-Broadcast profile. Users in a live-broadcast channel have a role as either broadcaster or audience. A broadcaster can both send and receive streams; an audience can only receive streams.
        mRtcEngine!!.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION)

        viewModel.getRTCToken(channel, viewModel.prefs().userID!!).observe(this, Observer { r ->
            if (r.status == Status.SUCCESS) {
                val token = r.data?.data as? String
                agoraCallManager.rtcEngine.joinChannel(token, channel, "", uid)
                Timber.d("channel: $channel, uid: $uid, token: ${token}")
            } else if (r.status == Status.ERROR) {
                ToastUtil.showToast(this, "RTM token获取错误")
            }
        })

        // Allows a user to join a channel.
        mRtcEngine!!.joinChannel(
            viewModel.prefs().rtcToken,
            channelId,
            "Extra Optional Data",
            0
        ) // if you do not specify the uid, we will generate the uid for you
    }

    // Tutorial Step 3
    private fun leaveChannel() {
        mRtcEngine!!.leaveChannel()
    }

    // Tutorial Step 4
    private fun onRemoteUserLeft(uid: Int, reason: Int) {
        showLongToast(String.format(Locale.US, "user %d left %d", uid and 0xFFFFFFFFL.toInt(), reason))
        val tipMsg = findViewById<View>(R.id.quick_tips_when_use_agora_sdk) // optional UI
        tipMsg.visibility = View.VISIBLE
    }

    // Tutorial Step 6
    private fun onRemoteUserVoiceMuted(uid: Int, muted: Boolean) {
        showLongToast(
            String.format(
                Locale.US,
                "user %d muted or unmuted %b",
                uid and 0xFFFFFFFFL.toInt(),
                muted
            )
        )
    }
}