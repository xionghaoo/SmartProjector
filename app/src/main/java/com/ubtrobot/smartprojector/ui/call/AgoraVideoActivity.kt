package com.ubtrobot.smartprojector.ui.call

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.SurfaceView
import androidx.activity.viewModels
import com.ubtrobot.smartprojector.databinding.ActivityAgoraVideoBinding
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration
import io.agora.rtm.RemoteInvitation
import timber.log.Timber
import xh.zero.agora_call.AgoraCallManager
import javax.inject.Inject

@AndroidEntryPoint
class AgoraVideoActivity : BaseCallActivity() {

    companion object {
        private const val EXTRA_CHANNEL_ID = "EXTRA_CHANNEL_ID"
        private const val EXTRA_PEER_UID = "EXTRA_PEER_UID"

        fun start(context: Context?, channelId: String?, peerId: String?) {
            val i = Intent(context, AgoraVideoActivity::class.java)
            i.putExtra(EXTRA_CHANNEL_ID, channelId)
            i.putExtra(EXTRA_PEER_UID, peerId)
            context?.startActivity(i)
        }
    }

    @Inject
    lateinit var agoraCallManager: AgoraCallManager
    private val viewModel: AgoraCallViewModel by viewModels()
//    lateinit var agoraListenerDelegate: AgoraListenerDelegate

    private lateinit var binding: ActivityAgoraVideoBinding

    private var channelId: String? = null
    private var peerId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgoraVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        channelId = intent.getStringExtra(EXTRA_CHANNEL_ID)
        peerId = intent.getStringExtra(EXTRA_PEER_UID)?.toInt()

//        agoraListenerDelegate = AgoraListenerDelegate(
//            activity = this,
//            agoraCallManager = agoraCallManager,
//            type = AgoraListenerDelegate.Type.VIDEO,
//            onUserJoined = { uid, elapsed ->
//                if (uid == peerId) {
//                    runOnUiThread {
//                        binding.remotePreviewLayout.addView(setupVideo(uid, false))
//                    }
//                }
//            },
//            onUserOffline = { uid, reason ->
//                if (uid == peerId) finish()
//            }
//        )
        binding.btnMute.isActivated = true

        agoraCallManager.rtcEngine.setClientRole(io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER)
        agoraCallManager.rtcEngine.setVideoEncoderConfiguration(
            VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x480,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
            )
        )

        setupLocalPreview()

        val userId = viewModel.prefs().userID?.toInt()
        if (channelId != null && userId != null) {
            joinChannel("test", userId)
        } else {
            throw IllegalArgumentException("channel id and user id cannot be null")
        }

        binding.btnEndcall.setOnClickListener {
            finish()
        }
        binding.btnMute.setOnClickListener {
            agoraCallManager.rtcEngine.muteLocalAudioStream(binding.btnMute.isActivated)
        }
        binding.btnSwitchCamera.setOnClickListener {
            agoraCallManager.rtcEngine.switchCamera()
        }
    }

    override fun onDestroy() {
        agoraCallManager.rtcEngine.leaveChannel()
//        agoraListenerDelegate.destroy()
        super.onDestroy()
    }

    override fun getAgoraManager(): AgoraCallManager = agoraCallManager

    override fun onUserJoined(uid: Int, elapsed: Int) {
        if (uid == peerId) {
            runOnUiThread {
                binding.remotePreviewLayout.addView(setupVideo(uid, false))
            }
        }
    }

    override fun onUserOffline(uid: Int, reason: Int) {
        if (uid == peerId) finish()
    }

    override fun onRemoteInvitationReceived(remoteInvitation: RemoteInvitation?) {
        // ignore other call
    }

    private fun setupLocalPreview() {
        val surfaceView = setupVideo(viewModel.prefs().userID?.toInt()!!, true)
        surfaceView!!.setZOrderOnTop(true)
        binding.localPreviewLayout.addView(surfaceView)
    }

    private fun setupVideo(uid: Int, local: Boolean): SurfaceView? {
        val surfaceView = RtcEngine.CreateRendererView(applicationContext)
        if (local) {
            agoraCallManager.rtcEngine.setupLocalVideo(
                VideoCanvas(
                    surfaceView,
                    VideoCanvas.RENDER_MODE_HIDDEN, uid
                )
            )
        } else {
            agoraCallManager.rtcEngine.setupRemoteVideo(
                VideoCanvas(
                    surfaceView,
                    VideoCanvas.RENDER_MODE_HIDDEN, uid
                )
            )
        }
        return surfaceView
    }

    private fun joinChannel(channel: String, uid: Int) {
        Timber.d("channel: $channel, uid: $uid, token: ${viewModel.prefs().rtcToken}")
        agoraCallManager.rtcEngine.joinChannel(viewModel.prefs().rtcToken, channel, "", uid)
    }


}