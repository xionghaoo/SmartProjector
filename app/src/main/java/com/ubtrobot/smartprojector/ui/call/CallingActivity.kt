package com.ubtrobot.smartprojector.ui.call

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ubtrobot.smartprojector.Configs
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.ActivityCallingBinding
import com.ubtrobot.smartprojector.ui.AgoraListenerDelegate
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtm.*
import timber.log.Timber
import xh.zero.agora_call.AgoraCallManager
import javax.inject.Inject

/**
 * 呼叫
 */
@AndroidEntryPoint
class CallingActivity : AppCompatActivity(), ResultCallback<Void> {

    companion object {

        private const val EXTRA_PEER_ID = "${Configs.PACKAGE_NAME}.CallingActivity.EXTRA_PEER_ID"
        private const val EXTRA_IS_CALLEE = "${Configs.PACKAGE_NAME}.CallingActivity.EXTRA_IS_CALLEE"

        fun start(context: Context, peerId: String, isCallee: Boolean) {
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

    private lateinit var agoraListenerDelegate: AgoraListenerDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        agoraListenerDelegate = AgoraListenerDelegate(this, agoraCallManager, AgoraListenerDelegate.Type.CALLING)

        peerId = intent.getStringExtra(EXTRA_PEER_ID)
        isCallee = intent.getBooleanExtra(EXTRA_IS_CALLEE, false)
        Timber.d("isCallee: ${isCallee}")
        if (isCallee) {
            // 被呼叫

        } else {
            // 主动呼叫
            inviteCall()
        }

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
        agoraListenerDelegate.destroy()
        super.onDestroy()
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
        val player = MediaPlayer.create(this, resource)
        player.isLooping = true
        player.start()
    }

    private fun answerCall(invitation: RemoteInvitation) {
        agoraCallManager.rtmCallManager.acceptRemoteInvitation(invitation, this)
    }

    private fun cancelLocalInvitation() {
        val rtmCallManager = agoraCallManager.rtmCallManager
        rtmCallManager.cancelLocalInvitation(agoraCallManager.localInvitation, this)
    }

    private fun refuseRemoteInvitation(invitation: RemoteInvitation) {
        agoraCallManager.rtmCallManager.refuseRemoteInvitation(invitation, this)
    }

    override fun onSuccess(p0: Void?) {

    }

    override fun onFailure(p0: ErrorInfo?) {

    }


}