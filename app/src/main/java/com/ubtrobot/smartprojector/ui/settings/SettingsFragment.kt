package com.ubtrobot.smartprojector.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.startPlainActivity
import com.ubtrobot.smartprojector.ui.ScreenLockActivity
import com.ubtrobot.smartprojector.ui.TuyaActivity
import com.ubtrobot.smartprojector.update.UpdateDelegate
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * 设置页面
 */
class SettingsFragment : Fragment() {

    private lateinit var updateDelegate: UpdateDelegate


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

        btn_lock_screen.setOnClickListener {
            ScreenLockActivity.lock(requireContext())
        }

    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}