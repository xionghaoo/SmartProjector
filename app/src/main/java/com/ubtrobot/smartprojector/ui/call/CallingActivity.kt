package com.ubtrobot.smartprojector.ui.call

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ubtrobot.smartprojector.Configs
import com.ubtrobot.smartprojector.R
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtm.*
import xh.zero.agora_call.AgoraCallManager
import javax.inject.Inject

/**
 * 呼叫
 */
@AndroidEntryPoint
class CallingActivity : AppCompatActivity(), RtmCallEventListener, ResultCallback<Void> {

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

    @Inject
    lateinit var agoraCallManager: AgoraCallManager

    private var player: MediaPlayer? = null
    private var peerId: String? = null
    private var isCallee: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling)

        peerId = intent.getStringExtra(EXTRA_PEER_ID)
        isCallee = intent.getBooleanExtra(EXTRA_IS_CALLEE, false)

        if (!isCallee) {
            // 主动呼叫
            inviteCall()
        }
    }

    private fun inviteCall() {
        val rtmCallManager = agoraCallManager.rtmCallManager
        val invitation = rtmCallManager.createLocalInvitation(peerId)
        // 设置通道Channel
        invitation.content = ""
        rtmCallManager.sendLocalInvitation(invitation, this)
    }

    private fun startRinging() {
        startRinging(if (isCallee) R.raw.basic_tones else R.raw.basic_ring)
    }

    private fun startRinging(resource: Int): MediaPlayer? {
        val player = MediaPlayer.create(this, resource)
        player.isLooping = true
        player.start()
        return player
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

    override fun onLocalInvitationReceivedByPeer(p0: LocalInvitation?) {
    }

    override fun onLocalInvitationAccepted(p0: LocalInvitation?, p1: String?) {
        // 本地呼叫邀请被接受
    }

    override fun onLocalInvitationRefused(p0: LocalInvitation?, p1: String?) {

    }

    override fun onLocalInvitationCanceled(p0: LocalInvitation?) {
    }

    override fun onLocalInvitationFailure(p0: LocalInvitation?, p1: Int) {
    }

    override fun onRemoteInvitationReceived(invitation: RemoteInvitation?) {
        agoraCallManager.remoteInvitation = invitation
    }

    override fun onRemoteInvitationAccepted(p0: RemoteInvitation?) {
        // 接受远程呼叫
    }

    override fun onRemoteInvitationRefused(p0: RemoteInvitation?) {
    }

    override fun onRemoteInvitationCanceled(p0: RemoteInvitation?) {
    }

    override fun onRemoteInvitationFailure(p0: RemoteInvitation?, p1: Int) {
    }
}