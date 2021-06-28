package com.ubtrobot.smartprojector.ui.call

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ubtrobot.smartprojector.Configs
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.ActivityCallWithParentBinding
import com.ubtrobot.smartprojector.utils.SystemUtil
import com.ubtrobot.smartprojector.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import timber.log.Timber
import xh.zero.agora_call.AgoraCallManager
import java.util.HashSet
import javax.inject.Inject

@AndroidEntryPoint
class CallWithParentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCallWithParentBinding
    @Inject
    lateinit var agoraCallManager: AgoraCallManager

    override fun onCreate(savedInstanceState: Bundle?) {
        SystemUtil.toFullScreenMode(this)
        super.onCreate(savedInstanceState)
        binding = ActivityCallWithParentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.configBackButton(this)
        binding.toolbar.setTitle("和家长通话")

        binding.btnVideoCall.setOnClickListener {
            callPeer(Configs.agoraPeerUserId, "video")
        }

        binding.btnAudioCall.setOnClickListener {
            callPeer(Configs.agoraPeerUserId, "audio")
        }
    }

    private fun callPeer(number: String, content: String?) {
        val peer: String = number
        val peerSet: MutableSet<String> = HashSet()
        peerSet.add(peer)
        // 查询呼叫号码是否在线
        agoraCallManager.rtmClient.queryPeersOnlineStatus(peerSet, object :
            ResultCallback<Map<String, Boolean>> {
            override fun onSuccess(p0: Map<String, Boolean>?) {
                CallingActivity.start(this@CallWithParentActivity, peer, false, content)
            }

            override fun onFailure(p0: ErrorInfo?) {
                ToastUtil.showToast(this@CallWithParentActivity, "您呼叫的号码不在线")
                Timber.e(p0?.errorDescription)
            }
        })
    }
}