package com.ubtrobot.smartprojector.ui

import android.app.Activity
import io.agora.rtm.LocalInvitation
import io.agora.rtm.RemoteInvitation
import timber.log.Timber
import xh.zero.agora_call.AgoraCallManager
import xh.zero.agora_call.agora.IEventListener

class AgoraListenerDelegate(
    private val activity: Activity,
    private val agoraCallManager: AgoraCallManager,
    private val type: Type
) : IEventListener {

    enum class Type {
        MAIN, CALLING
    }

    init {
        agoraCallManager.registerEventListener(this)
    }

    fun destroy() {
        agoraCallManager.removeEventListener(this)
    }

    override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
        Timber.d("onJoinChannelSuccess")

    }

    override fun onUserJoined(uid: Int, elapsed: Int) {
        Timber.d("onUserJoined")

    }

    override fun onUserOffline(uid: Int, reason: Int) {
        Timber.d("onUserOffline")

    }

    override fun onConnectionStateChanged(status: Int, reason: Int) {
        Timber.d("onConnectionStateChanged")
    }

    override fun onPeersOnlineStatusChanged(map: MutableMap<String, Int>?) {
        Timber.d("onPeersOnlineStatusChanged: ${map?.size}")
    }

    override fun onLocalInvitationReceived(localInvitation: LocalInvitation?) {
        Timber.d("onLocalInvitationReceived: channel id = ${localInvitation?.channelId}, callee id = ${localInvitation?.calleeId}")

    }

    override fun onLocalInvitationAccepted(localInvitation: LocalInvitation?, response: String?) {
        // 本地呼叫邀请被接受
        Timber.d("onLocalInvitationAccepted: channel id = ${localInvitation?.channelId}, callee id = ${localInvitation?.calleeId}")
    }

    override fun onLocalInvitationRefused(localInvitation: LocalInvitation?, response: String?) {
        Timber.d("onLocalInvitationRefused: channel id = ${localInvitation?.channelId}, callee id = ${localInvitation?.calleeId}")
    }

    override fun onLocalInvitationCanceled(localInvitation: LocalInvitation?) {
        Timber.d("onLocalInvitationCanceled: channel id = ${localInvitation?.channelId}, callee id = ${localInvitation?.calleeId}")
    }

    override fun onLocalInvitationFailure(localInvitation: LocalInvitation?, errorCode: Int) {
        Timber.d("onLocalInvitationFailure: channel id = ${localInvitation?.channelId}, callee id = ${localInvitation?.calleeId}")
    }

    override fun onRemoteInvitationReceived(remoteInvitation: RemoteInvitation?) {
        agoraCallManager.remoteInvitation = remoteInvitation
        // 有新的远程呼叫
        Timber.d("onRemoteInvitationAccepted: channel id = ${remoteInvitation?.channelId}, callee id = ${remoteInvitation?.callerId}")

    }

    override fun onRemoteInvitationAccepted(remoteInvitation: RemoteInvitation?) {
        // 接受远程呼叫
        Timber.d("onRemoteInvitationAccepted: channel id = ${remoteInvitation?.channelId}, callee id = ${remoteInvitation?.callerId}")
    }

    override fun onRemoteInvitationRefused(remoteInvitation: RemoteInvitation?) {
        Timber.d("onRemoteInvitationRefused: channel id = ${remoteInvitation?.channelId}, callee id = ${remoteInvitation?.callerId}")

    }

    override fun onRemoteInvitationCanceled(remoteInvitation: RemoteInvitation?) {
        Timber.d("onRemoteInvitationCanceled: channel id = ${remoteInvitation?.channelId}, callee id = ${remoteInvitation?.callerId}")
    }

    override fun onRemoteInvitationFailure(remoteInvitation: RemoteInvitation?, errorCode: Int) {
        Timber.d("onRemoteInvitationFailure: channel id = ${remoteInvitation?.channelId}, callee id = ${remoteInvitation?.callerId}")
    }
}