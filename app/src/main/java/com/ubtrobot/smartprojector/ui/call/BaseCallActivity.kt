package com.ubtrobot.smartprojector.ui.call

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.ubtrobot.smartprojector.SmartProjectorApp
import io.agora.rtm.LocalInvitation
import io.agora.rtm.RemoteInvitation
import timber.log.Timber
import xh.zero.agora_call.AgoraCallManager
import xh.zero.agora_call.agora.IEventListener

abstract class BaseCallActivity : AppCompatActivity(), IEventListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAgoraManager().registerEventListener(this)
    }

    override fun onDestroy() {
        getAgoraManager().removeEventListener(this)
        super.onDestroy()
    }

    abstract fun getAgoraManager() : AgoraCallManager

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
        Timber.d("onRemoteInvitationReceived: channel id = ${remoteInvitation?.channelId}, callee id = ${remoteInvitation?.callerId}, content: ${remoteInvitation?.content}")
        getAgoraManager().remoteInvitation = remoteInvitation
        CallingActivity.start(this, remoteInvitation?.callerId, true, remoteInvitation?.content)
    }

    override fun onRemoteInvitationAccepted(remoteInvitation: RemoteInvitation?) {
    }

    override fun onRemoteInvitationRefused(remoteInvitation: RemoteInvitation?) {
    }

    override fun onRemoteInvitationCanceled(remoteInvitation: RemoteInvitation?) {
        finish()
    }

    override fun onRemoteInvitationFailure(remoteInvitation: RemoteInvitation?, errorCode: Int) {
    }

    override fun onRemoteAudioStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
        // 加入通道后才有的回调
        // 语音通话
        Timber.d("onRemoteAudioStateChanged: $uid, $state")
    }

    override fun onRemoteVideoStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
//        SmartProjectorApp.isVideo = true
        // 视频通话
        Timber.d("onRemoteVideoStateChanged: $uid, $state")
    }
}