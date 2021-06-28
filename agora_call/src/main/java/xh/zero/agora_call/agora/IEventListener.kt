package xh.zero.agora_call.agora

import io.agora.rtm.LocalInvitation
import io.agora.rtm.RemoteInvitation

interface IEventListener {
    fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int)
    fun onUserJoined(uid: Int, elapsed: Int)
    fun onUserOffline(uid: Int, reason: Int)
    fun onConnectionStateChanged(status: Int, reason: Int)
    fun onPeersOnlineStatusChanged(map: Map<String, Int>?)
    fun onLocalInvitationReceived(localInvitation: LocalInvitation?)
    fun onLocalInvitationAccepted(localInvitation: LocalInvitation?, response: String?)
    fun onLocalInvitationRefused(localInvitation: LocalInvitation?, response: String?)
    fun onLocalInvitationCanceled(localInvitation: LocalInvitation?)
    fun onLocalInvitationFailure(localInvitation: LocalInvitation?, errorCode: Int)
    fun onRemoteInvitationReceived(remoteInvitation: RemoteInvitation?)
    fun onRemoteInvitationAccepted(remoteInvitation: RemoteInvitation?)
    fun onRemoteInvitationRefused(remoteInvitation: RemoteInvitation?)
    fun onRemoteInvitationCanceled(remoteInvitation: RemoteInvitation?)
    fun onRemoteInvitationFailure(remoteInvitation: RemoteInvitation?, errorCode: Int)
    fun onRemoteAudioStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int)
    fun onRemoteVideoStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int)
}