package com.ubtrobot.smartprojector.ui.call

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.SurfaceView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ubtrobot.smartprojector.databinding.ActivityAgoraVideoBinding
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration
import xh.zero.agora_call.AgoraCallManager
import javax.inject.Inject

@AndroidEntryPoint
class AgoraVideoActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_CHANNEL_ID = "EXTRA_CHANNEL_ID"
        private const val EXTRA_PEER_UID = "EXTRA_PEER_UID"

        fun start(context: Context?, channelId: String, peerId: String) {
            val i = Intent(context, AgoraVideoActivity::class.java)
            i.putExtra(EXTRA_CHANNEL_ID, channelId)
            i.putExtra(EXTRA_PEER_UID, peerId)
            context?.startActivity(i)
        }
    }

    @Inject
    lateinit var agoraCallManager: AgoraCallManager
    private val viewModel: AgoraCallViewModel by viewModels()

    private lateinit var binding: ActivityAgoraVideoBinding

    private var channelId: String? = null
    private var peerId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgoraVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    }

    private fun setupLocalPreview() {
        val surfaceView = setupVideo(viewModel.prefs().agoraUID?.toInt()!!, true)
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
}