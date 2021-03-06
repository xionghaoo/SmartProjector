package com.ubtrobot.smartprojector.ui.settings

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ubtrobot.smartprojector.databinding.FragmentSettingsBinding
import com.ubtrobot.smartprojector.repo.Repository
import com.ubtrobot.smartprojector.startPlainActivity
import com.ubtrobot.smartprojector.test.TestActivity
import com.ubtrobot.smartprojector.tuyagw.TuyaGatewayManager
import com.ubtrobot.smartprojector.ui.restrict.ScreenLockActivity
import com.ubtrobot.smartprojector.ui.login.LoginActivity
import com.ubtrobot.smartprojector.ui.restrict.AppWhiteListActivity
import com.ubtrobot.smartprojector.ui.tuya.FamilyManagerActivity
import com.ubtrobot.smartprojector.ui.tuya.TuyaHomeActivity
import com.ubtrobot.smartprojector.ui.video.VideoActivity
import com.ubtrobot.smartprojector.update.UpdateDelegate
import com.ubtrobot.smartprojector.utils.*
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
    private var listener: OnFragmentActionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentActionListener) {
            listener = context
        } else {
            throw IllegalArgumentException("activity must implement OnFragmentActionListener")
        }
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

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
            startPlainActivity(TuyaHomeActivity::class.java)
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

        if (repo.prefs.tuyaHomeName == null) {
            LoginActivity.startWithNewTask(requireContext())
        }

        binding.tvHomeInfo.text = repo.prefs.tuyaHomeName
        binding.tvHomeInfo.setOnClickListener {
            startPlainActivity(FamilyManagerActivity::class.java)
        }

        binding.btnGwActive.setOnClickListener {
            TuyaGatewayManager.instance().activeGateway(repo.prefs.tuyaHomeId) { success ->
                ToastUtil.showToast(requireContext(), "网关激活${if (success == 0) "成功" else "失败"}")
            }
        }
        binding.btnGwUnactive.setOnClickListener {
            TuyaGatewayManager.instance().unactiveGateway { success ->
                ToastUtil.showToast(requireContext(), "网关删除${if (success == 0) "成功" else "失败"}")
            }
        }

        binding.btnTestBluetooth.setOnClickListener {
            listener?.showBluetoothDialog()
        }

        binding.btnInitialGw.setOnClickListener {
            TuyaGatewayManager.instance().initial()
        }

        binding.networkStateLayout.loading()

        binding.btnVideoTest.setOnClickListener {
            startPlainActivity(VideoActivity::class.java)
        }

        binding.tvDisplayInfo.text = SystemUtil.displayInfo(requireContext()).toString()

//        val v = layoutInflater.inflate()
//        dialog = AlertDialog.Builder(requireContext())
//            .setView()
//            .show()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    external fun helloStr() : String?

    interface OnFragmentActionListener {
        fun showBluetoothDialog()
    }

    companion object {
        init {
//            System.loadLibrary("tuya_ext")
        }

        fun newInstance() = SettingsFragment()
    }
}