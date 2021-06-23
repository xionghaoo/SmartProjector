package com.ubtrobot.smartprojector.ui.call

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.ubtrobot.smartprojector.Configs
import com.ubtrobot.smartprojector.GlideApp
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.ActivityCallingBinding
import com.ubtrobot.smartprojector.utils.SystemUtil
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtm.*
import timber.log.Timber
import xh.zero.agora_call.AgoraCallManager
import javax.inject.Inject

/**
 * 呼叫中页面
 */
@AndroidEntryPoint
class CallingActivity : BaseCallActivity(), ResultCallback<Void> {

    companion object {

        private const val EXTRA_PEER_ID = "${Configs.PACKAGE_NAME}.CallingActivity.EXTRA_PEER_ID"
        private const val EXTRA_IS_CALLEE = "${Configs.PACKAGE_NAME}.CallingActivity.EXTRA_IS_CALLEE"
        private const val EXTRA_CONTENT = "${Configs.PACKAGE_NAME}.CallingActivity.EXTRA_CONTENT"

        fun start(context: Context, peerId: String?, isCallee: Boolean, content: String?) {
            val i = Intent(context, CallingActivity::class.java)
            i.putExtra(EXTRA_PEER_ID, peerId)
            i.putExtra(EXTRA_IS_CALLEE, isCallee)
            i.putExtra(EXTRA_CONTENT, content)
            context.startActivity(i)
        }
    }

    lateinit var binding: ActivityCallingBinding
    @Inject
    lateinit var agoraCallManager: AgoraCallManager
    private val viewModel: AgoraCallViewModel by viewModels()

    private var player: MediaPlayer? = null
    private var peerId: String? = null
    private var isCallee: Boolean = false
    private var content: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SystemUtil.statusBarTransparent(window)

        peerId = intent.getStringExtra(EXTRA_PEER_ID)
        isCallee = intent.getBooleanExtra(EXTRA_IS_CALLEE, false)
        content = intent.getStringExtra(EXTRA_CONTENT)
        Timber.d("isCallee: ${isCallee}")
        if (isCallee) {
            // 被呼叫
            binding.tvCalling.text = "想和你进行视频通话"
            binding.btnAccept.visibility = View.VISIBLE
            binding.btnAccept.setOnClickListener {
                answerCall()
            }
        } else {
            binding.tvCalling.text = "正在等待宝贝接受视频通话。。。"
            // 主动呼叫
            inviteCall()
        }

        startRinging()

        binding.btnHungUp.setOnClickListener {
            if (isCallee) {
                // 拒绝远端呼叫
                refuseRemote()
            } else {
                // 取消本地呼叫
                cancelLocal()
            }
            finish()
        }

        GlideApp.with(this)
            .load(R.drawable.ic_cat)
            .circleCrop()
            .into(binding.ivCallingAvatar)
    }

    override fun finish() {
        stopRinging()
        if (isCallee && agoraCallManager.remoteInvitation != null) {
            refuseRemote()
        } else {
            cancelLocal()
        }
        super.finish()
    }

    private fun inviteCall() {
        val rtmCallManager = agoraCallManager.rtmCallManager
        val invitation = rtmCallManager.createLocalInvitation(peerId)
        // 设置通道Channel
        invitation.channelId = "${peerId}${viewModel.prefs().userID}"
        invitation.content = content
        rtmCallManager.sendLocalInvitation(invitation, this)
        // 保存本地邀请
        agoraCallManager.localInvitation = invitation
    }

    private fun cancelLocal() {
        Timber.d("cancelLocal: ${agoraCallManager.localInvitation}")
        agoraCallManager.rtmCallManager.cancelLocalInvitation(agoraCallManager.localInvitation, this)
    }

    private fun refuseRemote() {
        agoraCallManager.rtmCallManager.refuseRemoteInvitation(agoraCallManager.remoteInvitation, this)
    }

    private fun startRinging() {
        startRinging(if (isCallee) R.raw.basic_tones else R.raw.basic_ring)
    }

    private fun startRinging(resource: Int) {
        player = MediaPlayer.create(this, resource)
        player?.isLooping = true
        player?.start()
    }

    private fun stopRinging() {
        if (player != null && player!!.isPlaying) {
            player?.stop()
            player?.release()
            player = null
        }
    }

    private fun answerCall() {
        agoraCallManager.rtmCallManager.acceptRemoteInvitation(agoraCallManager.remoteInvitation, this)
    }

    override fun onSuccess(p0: Void?) {

    }

    override fun onFailure(p0: ErrorInfo?) {

    }

    override fun getAgoraManager(): AgoraCallManager = agoraCallManager

    override fun onLocalInvitationReceived(localInvitation: LocalInvitation?) {

    }

    override fun onLocalInvitationAccepted(localInvitation: LocalInvitation?, response: String?) {
        Timber.d("onLocalInvitationAccepted: channel id = ${localInvitation?.channelId}, callee id = ${localInvitation?.calleeId}, content: ${localInvitation?.content}")

        stopRinging()
        val content = localInvitation?.content
        if (content == "video") {
            AgoraVideoActivity.start(this, localInvitation?.channelId, localInvitation?.calleeId)
        } else {
            AgoraVoiceCallActivity.start(this, localInvitation?.channelId, localInvitation?.calleeId)
        }
        finish()
    }

    override fun onLocalInvitationCanceled(localInvitation: LocalInvitation?) {
        Timber.d("onLocalInvitationCanceled: channel id = ${localInvitation?.channelId}, callee id = ${localInvitation?.calleeId}")

        finish()
    }

    override fun onLocalInvitationRefused(localInvitation: LocalInvitation?, response: String?) {
        Timber.d("onLocalInvitationRefused: channel id = ${localInvitation?.channelId}, callee id = ${localInvitation?.calleeId}")

        finish()
    }

    override fun onLocalInvitationFailure(localInvitation: LocalInvitation?, errorCode: Int) {
        Timber.d("onLocalInvitationFailure: channel id = ${localInvitation?.channelId}, callee id = ${localInvitation?.calleeId}")

        finish()
    }

    override fun onRemoteInvitationReceived(remoteInvitation: RemoteInvitation?) {
        // ignore other call
        Timber.d("onRemoteInvitationReceived: channel id = ${remoteInvitation?.channelId}, callee id = ${remoteInvitation?.callerId}")

    }

    override fun onRemoteInvitationRefused(remoteInvitation: RemoteInvitation?) {
        Timber.d("onRemoteInvitationRefused: channel id = ${remoteInvitation?.channelId}, callee id = ${remoteInvitation?.callerId}")

        finish()
    }

    override fun onRemoteInvitationCanceled(remoteInvitation: RemoteInvitation?) {
        Timber.d("onRemoteInvitationCanceled: channel id = ${remoteInvitation?.channelId}, callee id = ${remoteInvitation?.callerId}")

        finish()
    }

    override fun onRemoteInvitationFailure(remoteInvitation: RemoteInvitation?, errorCode: Int) {
        Timber.d("onRemoteInvitationFailure: channel id = ${remoteInvitation?.channelId}, callee id = ${remoteInvitation?.callerId}")
        stopRinging()
    }

    override fun onRemoteInvitationAccepted(remoteInvitation: RemoteInvitation?) {
        Timber.d("onRemoteInvitationAccepted: channel id = ${remoteInvitation?.channelId}, callee id = ${remoteInvitation?.callerId}, state: ${remoteInvitation?.state}")
        val content = remoteInvitation?.content
        if (content == "video") {
            AgoraVideoActivity.start(this, remoteInvitation?.channelId, remoteInvitation?.callerId)
        } else {
            AgoraVoiceCallActivity.start(this, remoteInvitation?.channelId, remoteInvitation?.callerId)
        }
        finish()
    }


}