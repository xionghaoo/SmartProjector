package com.ubtrobot.smartprojector.ui.settings

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.FragmentSettingsBinding
import com.ubtrobot.smartprojector.repo.Repository
import com.ubtrobot.smartprojector.startPlainActivity
import com.ubtrobot.smartprojector.test.TestActivity
import com.ubtrobot.smartprojector.tuyagw.TuyaGatewayManager
import com.ubtrobot.smartprojector.ui.restrict.ScreenLockActivity
import com.ubtrobot.smartprojector.ui.tuya.TuyaActivity
import com.ubtrobot.smartprojector.ui.cartoonbook.FlipTestActivity
import com.ubtrobot.smartprojector.ui.restrict.AppWhiteListActivity
import com.ubtrobot.smartprojector.ui.tuya.TuyaDeviceCategoryActivity
import com.ubtrobot.smartprojector.update.UpdateDelegate
import com.ubtrobot.smartprojector.utils.RootCommand
import com.ubtrobot.smartprojector.utils.RootExecutor
import com.ubtrobot.smartprojector.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * 设置页面
 */
@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var updateDelegate: UpdateDelegate
    @Inject
    lateinit var repo: Repository
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateDelegate = UpdateDelegate(requireActivity())

        binding.btnVersionCheck.setOnClickListener {
            updateDelegate.showVersionUpdateDialog(
                    url = "http://cdn.llsapp.com/android/LLS-v4.0-595-20160908-143200.apk",
                    versionName = "test1.0.0",
                    content = "版本更新测试",
                    isForce = false,
                    cancel = {

                    }
            )
        }
        
        binding.btnStartTuya.setOnClickListener {
            startPlainActivity(TuyaActivity::class.java)
        }

        binding.edtPwd.setText(repo.prefs.screenLockPwd)
        binding.btnLockScreen.setOnClickListener {
            val pwd = binding.edtPwd.text.toString()
            if (pwd.isNotBlank() && pwd.length == 4) {
                repo.prefs.screenLockPwd = pwd
                ScreenLockActivity.lock(requireContext())
            } else {
                ToastUtil.showToast(requireContext(), "锁屏密码必须是四位数字")
            }

        }

        binding.btnWhiteList.setOnClickListener {
            startPlainActivity(AppWhiteListActivity::class.java)
        }

        binding.btnFlipTest.setOnClickListener {
            startPlainActivity(FlipTestActivity::class.java)
        }

        binding.btnRootCmd.setOnClickListener {
//            RootExecutor.exec(
//                cmd = RootCommand.grantPermission(Manifest.permission.SYSTEM_ALERT_WINDOW),
//                success = {
//                    ToastUtil.showToast(requireContext(), "权限申请成功")
//                },
//                failure = {
//                    ToastUtil.showToast(requireContext(), "权限申请成功")
//                }
//            )
        }

//        tv_jni.text = "haha, ${helloStr()}"

        binding.btnTest.setOnClickListener {
            startPlainActivity(TestActivity::class.java)
        }

        binding.btnDeviceSelect.setOnClickListener {
            startPlainActivity(TuyaDeviceCategoryActivity::class.java)
        }

        binding.tvHomeInfo.text = repo.prefs.currentHomeName

        binding.btnGwActive.setOnClickListener {
            TuyaGatewayManager.instance().activeGateway(repo.prefs.currentHomeId) { success ->
                ToastUtil.showToast(requireContext(), "网关激活${if (success == 0) "成功" else "失败"}")
            }
        }
        binding.btnGwUnactive.setOnClickListener {
            TuyaGatewayManager.instance().unactiveGateway { success ->
                ToastUtil.showToast(requireContext(), "网关删除${if (success == 0) "成功" else "失败"}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    external fun helloStr() : String?

    companion object {
        init {
//            System.loadLibrary("tuya_ext")
        }

        fun newInstance() = SettingsFragment()
    }
}