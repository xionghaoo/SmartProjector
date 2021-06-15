package com.ubtrobot.smartprojector.ui.call

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ubtrobot.smartprojector.Configs
import com.ubtrobot.smartprojector.R
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import timber.log.Timber
import xh.zero.agora_call.AgoraCallManager
import java.util.*
import javax.inject.Inject

/**
 *
 */
@AndroidEntryPoint
class CallWithPatriarchActivity : AppCompatActivity() {


    @Inject
    lateinit var agoraCallManager: AgoraCallManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_with_patriarch)

        // 填写蝌蚪助手用户ID
        callPeer(Configs.agoraPeerUserId)
    }

    private fun callPeer(number: String) {
        val peer: String = number
        val peerSet: MutableSet<String> = HashSet()
        peerSet.add(peer)
        agoraCallManager.rtmClient.queryPeersOnlineStatus(peerSet, object : ResultCallback<Map<String, Boolean>> {
            override fun onSuccess(p0: Map<String, Boolean>?) {
                CallingActivity.start(this@CallWithPatriarchActivity, peer, false)
            }

            override fun onFailure(p0: ErrorInfo?) {
                Timber.e(p0?.errorDescription)
            }
        })
    }
}