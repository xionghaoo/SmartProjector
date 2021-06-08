package com.ubtrobot.smartprojector.ui

import android.app.Activity
import io.agora.rtm.LocalInvitation
import io.agora.rtm.RemoteInvitation
import xh.zero.agora_call.AgoraCallManager
import xh.zero.agora_call.agora.IEventListener

class AgoraCallDelegate(
    private val activity: Activity,
    private val agoraCallManager: AgoraCallManager
) : IEventListener {

    init {
        agoraCallManager.registerEventListener(this)
    }

    override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
    }

    override fun onUserJoined(uid: Int, elapsed: Int) {
    }

    override fun onUserOffline(uid: Int, reason: Int) {
    }

    override fun onConnectionStateChanged(status: Int, reason: Int) {
    }

    override fun onPeersOnlineStatusChanged(map: MutableMap<String, Int>?) {
    }

    override fun onLocalInvitationReceived(localInvitation: LocalInvitation?) {
    }

    override fun onLocalInvitationAccepted(localInvitation: LocalInvitation?, response: String?) {
    }

    override fun onLocalInvitationRefused(localInvitation: LocalInvitation?, response: String?) {
    }

    override fun onLocalInvitationCanceled(localInvitation: LocalInvitation?) {
    }

    override fun onLocalInvitationFailure(localInvitation: LocalInvitation?, errorCode: Int) {
    }

    override fun onRemoteInvitationReceived(remoteInvitation: RemoteInvitation?) {
    }

    override fun onRemoteInvitationAccepted(remoteInvitation: RemoteInvitation?) {
    }

    override fun onRemoteInvitationRefused(remoteInvitation: RemoteInvitation?) {
    }

    override fun onRemoteInvitationCanceled(remoteInvitation: RemoteInvitation?) {
    }

    override fun onRemoteInvitationFailure(remoteInvitation: RemoteInvitation?, errorCode: Int) {
    }
}