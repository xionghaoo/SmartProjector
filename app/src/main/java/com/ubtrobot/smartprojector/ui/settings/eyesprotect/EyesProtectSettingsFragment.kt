package com.ubtrobot.smartprojector.ui.settings.eyesprotect

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.ubtrobot.smartprojector.BuildConfig
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.FragmentEyesProtectSettingsBinding
import com.ubtrobot.smartprojector.repo.Repository
import com.ubtrobot.smartprojector.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import eu.chainfire.libsuperuser.Shell
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

/**
 * 护眼设置项
 */
@AndroidEntryPoint
class EyesProtectSettingsFragment : Fragment() {

    @Inject
    lateinit var repo: Repository
    private var _binding: FragmentEyesProtectSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentEyesProtectSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.menuItemUseTime.setDetail("30分钟")
        binding.menuItemUseTime.setOnSelectListener {
        }
        binding.menuItemRestTime.setDetail("30分钟")

        binding.swSettingsEyesProtect.isChecked = repo.prefs.isEyesProtectOpen
        binding.swSettingsEyesProtect.setOnCheckedChangeListener { _, isChecked ->
            repo.prefs.isEyesProtectOpen = isChecked
            if (isChecked) {
                // 进入护眼倒计时，倒计时可用时长
                eyeProtectionMode()
            }
        }
    }

//    fun showSelectionPage(isShow: Boolean) {
//        if (isShow) {
//            binding.containerMain.visibility = View.GONE
//            binding.subFragmentContainer.visibility = View.VISIBLE
//            childFragmentManager.beginTransaction()
//                .replace(R.id.sub_fragment_container,
//                    EyesProtectSettingsSelectFragment.newInstance(EyesProtectSettingsSelectFragment.TYPE_SELECT_USE_TIME)
//                )
//                .commit()
//        } else {
//            binding.subFragmentContainer.visibility = View.GONE
//            binding.containerMain.visibility = View.VISIBLE
//        }
//    }

    private fun eyeProtectionMode() {
        if (!Shell.SU.available()) return
        CoroutineScope(Dispatchers.Default).launch {
            delay(10 * 1000)
            Shell.Pool.SU.run("pm grant ${BuildConfig.APPLICATION_ID} ${Settings.ACTION_MANAGE_OVERLAY_PERMISSION}")
//            val exitCode2 = Shell.Pool.SU.run("content insert --uri content://settings/system --bind name:s:user_rotation --bind value:i:1")
//            Timber.d("改变系统屏幕方向： ${exitCode2}")
            withContext(Dispatchers.Main) {
                Timber.d("护眼模式")
                if (Build.VERSION.SDK_INT >= 23) {
                    if (Settings.canDrawOverlays(requireContext())) {
                        eyeProtectionDialog()
                    } else {
                        try {
                            startActivityForResult(
                                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION),
                                RC_SYSTEM_ALERT_WINDOW_PERMISSION
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    eyeProtectionDialog()
                }
            }
        }
    }

    private fun eyeProtectionDialog() {
        Timber.d("显示护眼模式弹窗")
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("护眼模式")
            .setMessage("小朋友，你该休息了")
            .create()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        } else {
            dialog.window?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
        }
        dialog.show()
    }

    companion object {
        private const val RC_SYSTEM_ALERT_WINDOW_PERMISSION = 2

        fun newInstance() = EyesProtectSettingsFragment()
    }
}