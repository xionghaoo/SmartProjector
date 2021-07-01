package com.ubtrobot.smartprojector.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.ubtrobot.smartprojector.*
import com.ubtrobot.smartprojector.databinding.ActivityProfileBinding
import com.ubtrobot.smartprojector.ui.call.CallWithParentActivity
import com.ubtrobot.smartprojector.ui.call.CallingActivity
import com.ubtrobot.smartprojector.ui.settings.SettingsActivity
import com.ubtrobot.smartprojector.utils.SystemUtil
import com.ubtrobot.smartprojector.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import timber.log.Timber
import xh.zero.agora_call.AgoraCallManager
import xh.zero.voice.TencentVoiceManager
import java.util.HashSet
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private val viewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var voiceManager: TencentVoiceManager

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

        binding.btnCallWithParent.setOnClickListener {
            startPlainActivity(CallWithParentActivity::class.java)
        }

        binding.tvTestInfo.text = "屏幕信息：${SystemUtil.displayInfo(this)}"

        binding.tvUserName.text = "用户名：台灯${viewModel.prefs().userID}"

        binding.btnVoiceTest.setOnClickListener {
            voiceManager.startRecognize()
        }
    }

}