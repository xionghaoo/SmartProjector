package com.ubtrobot.smartprojector.ui.call

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.core.vo.Status
import com.ubtrobot.smartprojector.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc.Constants
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import xh.zero.agora_call.AgoraCallManager
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AgoraVoiceCallActivity : BaseCallActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agora_voice_call)

        channelId = intent.getStringExtra(EXTRA_CHANNEL_ID)
        peerId = intent.getStringExtra(EXTRA_PEER_UID)?.toInt()

        audioCallTask()
    }

    @AfterPermissionGranted(PERMISSION_REQ_ID_RECORD_AUDIO)
    private fun audioCallTask() {
        if (hasAudioPermission()) {
            initAgoraEngineAndJoinChannel()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "语音通话需要语音权限，请授予",
                PERMISSION_REQ_ID_RECORD_AUDIO,
                Manifest.permission.RECORD_AUDIO
            )
        }
    }

    private fun hasAudioPermission() : Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.RECORD_AUDIO)
    }

    private fun initAgoraEngineAndJoinChannel() {
        val userId = viewModel.prefs().userID?.toInt()
        if (channelId != null && userId != null) {
            joinChannel(channelId!!, userId)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onDestroy() {
        agoraCallManager.rtcEngine.leaveChannel()
        super.onDestroy()
    }

    override fun getAgoraManager(): AgoraCallManager = agoraCallManager

    override fun onUserJoined(uid: Int, elapsed: Int) {
        if (uid == peerId) {
            Timber.d("用户${uid}加入频道")
            runOnUiThread {
                ToastUtil.showToast(this, "用户${uid}加入频道")
            }
        }
    }

    override fun onUserOffline(uid: Int, reason: Int) {
        if (uid == peerId) {
            runOnUiThread { onRemoteUserLeft(uid, reason) }
        }
    }

    fun onLocalAudioMuteClicked(view: View) {
        val iv = view as ImageView
        if (iv.isSelected) {
            iv.isSelected = false
            iv.clearColorFilter()
        } else {
            iv.isSelected = true
            iv.setColorFilter(resources.getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
        }

        agoraCallManager.rtcEngine.muteLocalAudioStream(iv.isSelected)
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

        agoraCallManager.rtcEngine.setEnableSpeakerphone(view.isSelected())
    }

    fun onEncCallClicked(view: View?) {
        finish()
    }

    private fun joinChannel(channel: String, uid: Int) {
        agoraCallManager.rtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION)

        viewModel.getRTCToken(channel, viewModel.prefs().userID!!).observe(this, { r ->
            if (r.status == Status.SUCCESS) {
                val token = r.data?.data as? String
                agoraCallManager.rtcEngine.joinChannel(token, channel, "", uid)
                Timber.d("channel: $channel, userId: $uid, token: ${token}")
            } else if (r.status == Status.ERROR) {
                ToastUtil.showToast(this, "RTM token获取错误")
            }
        })
    }

    private fun onRemoteUserLeft(uid: Int, reason: Int) {
        ToastUtil.showToast(this, String.format(Locale.US, "user %d left %d", uid and 0xFFFFFFFFL.toInt(), reason))
        val tipMsg = findViewById<View>(R.id.quick_tips_when_use_agora_sdk) // optional UI
        tipMsg.visibility = View.VISIBLE
    }

}