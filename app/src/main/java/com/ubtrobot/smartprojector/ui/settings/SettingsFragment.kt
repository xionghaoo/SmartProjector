package com.ubtrobot.smartprojector.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.repo.Repository
import com.ubtrobot.smartprojector.startPlainActivity
import com.ubtrobot.smartprojector.ui.restrict.ScreenLockActivity
import com.ubtrobot.smartprojector.ui.TuyaActivity
import com.ubtrobot.smartprojector.ui.cartoonbook.FlipTestActivity
import com.ubtrobot.smartprojector.ui.restrict.AppWhiteListActivity
import com.ubtrobot.smartprojector.update.UpdateDelegate
import com.ubtrobot.smartprojector.utils.RootExecutor
import com.ubtrobot.smartprojector.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

/**
 * 设置页面
 */
@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var updateDelegate: UpdateDelegate
    @Inject
    lateinit var repo: Repository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateDelegate = UpdateDelegate(requireActivity())

        btn_version_check.setOnClickListener {
            updateDelegate.showVersionUpdateDialog(
                    url = "http://cdn.llsapp.com/android/LLS-v4.0-595-20160908-143200.apk",
                    versionName = "test1.0.0",
                    content = "版本更新测试",
                    isForce = false,
                    cancel = {

                    }
            )
        }
        
        btn_start_tuya.setOnClickListener {
            startPlainActivity(TuyaActivity::class.java)
        }

        edt_pwd.setText(repo.prefs.screenLockPwd)
        btn_lock_screen.setOnClickListener {
            val pwd = edt_pwd.text.toString()
            if (pwd.isNotBlank() && pwd.length == 4) {
                repo.prefs.screenLockPwd = pwd
                ScreenLockActivity.lock(requireContext())
            } else {
                ToastUtil.showToast(requireContext(), "锁屏密码必须是四位数字")
            }

        }

        btn_white_list.setOnClickListener {
            startPlainActivity(AppWhiteListActivity::class.java)
        }

        btn_flip_test.setOnClickListener {
            startPlainActivity(FlipTestActivity::class.java)
        }

    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}