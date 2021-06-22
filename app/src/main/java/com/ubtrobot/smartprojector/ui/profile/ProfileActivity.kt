package com.ubtrobot.smartprojector.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ubtrobot.smartprojector.*
import com.ubtrobot.smartprojector.databinding.ActivityProfileBinding
import com.ubtrobot.smartprojector.ui.call.CallWithPatriarchActivity
import com.ubtrobot.smartprojector.ui.call.CallingActivity
import com.ubtrobot.smartprojector.ui.settings.SettingsActivity
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
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    @Inject
    lateinit var agoraCallManager: AgoraCallManager

    override fun onCreate(savedInstanceState: Bundle?) {
        SystemUtil.toFullScreenMode(this)
        super.onCreate(savedInstanceState)
        SystemUtil.statusBarTransparent(window)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setTitle("个人中心")
        binding.toolbar.configBackButton(this)

        binding.btnSettings.setOnClickListener {
            startPlainActivity(SettingsActivity::class.java)
        }

        GlideApp.with(this)
            .load(R.mipmap.ic_launcher_bg)
            .centerCrop()
            .into(binding.ivProfileBg)

        binding.btnVideoCall.setOnClickListener {
            callPeer(Configs.agoraPeerUserId, "video")
        }

        binding.btnVoiceCall.setOnClickListener {
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
                CallingActivity.start(this@ProfileActivity, peer, false, content)
            }

            override fun onFailure(p0: ErrorInfo?) {
                ToastUtil.showToast(this@ProfileActivity, "您呼叫的号码不在线")
                Timber.e(p0?.errorDescription)
            }
        })
    }
}