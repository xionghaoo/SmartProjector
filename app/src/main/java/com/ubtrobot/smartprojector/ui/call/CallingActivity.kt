package com.ubtrobot.smartprojector.ui.call

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import com.ubtrobot.smartprojector.Configs
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.ActivityCallingBinding
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtm.*
import timber.log.Timber
import xh.zero.agora_call.AgoraCallManager
import javax.inject.Inject

/**
 * 呼叫
 */
@AndroidEntryPoint
class CallingActivity : BaseCallActivity(), ResultCallback<Void> {

    companion object {

        private const val EXTRA_PEER_ID = "${Configs.PACKAGE_NAME}.CallingActivity.EXTRA_PEER_ID"
        private const val EXTRA_IS_CALLEE = "${Configs.PACKAGE_NAME}.CallingActivity.EXTRA_IS_CALLEE"

        fun start(context: Context, peerId: String?, isCallee: Boolean) {
            val i = Intent(context, CallingActivity::class.java)
            i.putExtra(EXTRA_PEER_ID, peerId)
            i.putExtra(EXTRA_IS_CALLEE, isCallee)
            context.startActivity(i)
        }
    }

    lateinit var binding: ActivityCallingBinding
    @Inject
    lateinit var agoraCallManager: AgoraCallManager

    private var player: MediaPlayer? = null
    private var peerId: String? = null
    private var isCallee: Boolean = false

//    private lateinit var agoraListenerDelegate: AgoraListenerDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallingBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        agoraListenerDelegate = AgoraListenerDelegate(this, agoraCallManager, AgoraListenerDelegate.Type.CALLING)

        peerId = intent.getStringExtra(EXTRA_PEER_ID)
        isCallee = intent.getBooleanExtra(EXTRA_IS_CALLEE, false)
        Timber.d("isCallee: ${isCallee}")
        if (isCallee) {
            // 被呼叫
            binding.tvCalling.text = "有新的呼叫邀请。。。"
            binding.btnAccept.visibility = View.VISIBLE
            binding.btnAccept.setOnClickListener {
                answerCall()
            }
        } else {
            binding.tvCalling.text = "正在呼叫。。。"
            // 主动呼叫
            inviteCall()
        }

        startRinging()

        binding.btnHungUp.setOnClickListener {
            if (isCallee) {
                // 拒绝远端呼叫
//                agoraCallManager.rtmCallManager.refuseRemoteInvitation(agoraCallManager.remoteInvitation, this)
                refuseRemote()
            } else {
                // 取消本地呼叫
//                agoraCallManager.rtmCallManager.cancelLocalInvitation(agoraCallManager.localInvitation, this)
                cancelLocal()
            }
            finish()
        }
    }

    override fun onDestroy() {
        Timber.d("onDestroy")
        if (isCallee && agoraCallManager.remoteInvitation != null) {
            refuseRemote()
        } else {
            cancelLocal()
        }
        stopRinging()
//        agoraListenerDelegate.destroy()
        super.onDestroy()
    }

    override fun getAgoraManager(): AgoraCallManager = agoraCallManager

    override fun onLocalInvitationAccepted(localInvitation: LocalInvitation?, response: String?) {
        Timber.d("onLocalInvitationAccepted: channel id = ${localInvitation?.channelId}, callee id = ${localInvitation?.calleeId}")

        stopRinging()
        AgoraVideoActivity.start(this, localInvitation?.content, localInvitation?.calleeId)
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
        AgoraVideoActivity.start(this, remoteInvitation?.content, remoteInvitation?.callerId)
        finish()
    }

    private fun inviteCall() {
        val rtmCallManager = agoraCallManager.rtmCallManager
        val invitation = rtmCallManager.createLocalInvitation(peerId)
        // 设置通道Channel
        invitation.content = Configs.agoraChannel
        rtmCallManager.sendLocalInvitation(invitation, this)
        // 保存本地邀请
        agoraCallManager.localInvitation = invitation
    }

    private fun cancelLocal() {
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


}