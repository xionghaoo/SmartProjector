package com.ubtrobot.smartprojector.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.ActivityMainBinding
import com.ubtrobot.smartprojector.receivers.ConnectionStateMonitor
import com.ubtrobot.smartprojector.replaceFragment
import com.ubtrobot.smartprojector.repo.Repository
import com.ubtrobot.smartprojector.ui.appmarket.AppMarketFragment
import com.ubtrobot.smartprojector.ui.cartoonbook.CartoonBookFragment
import com.ubtrobot.smartprojector.ui.game.GameFragment
import com.ubtrobot.smartprojector.ui.restrict.ScreenLockActivity
import com.ubtrobot.smartprojector.ui.settings.SettingsFragment
import com.ubtrobot.smartprojector.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        private const val RC_SYSTEM_ALERT_WINDOW_PERMISSION = 2

        fun startWithNewTask(context: Context?) {
            val i = Intent(context, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context?.startActivity(i)
        }
    }

    @Inject
    lateinit var connectionStateMonitor: ConnectionStateMonitor
    @Inject
    lateinit var repo: Repository

    private lateinit var mainFragment: MainFragment
    private lateinit var educationFragment: EducationFragment
    private lateinit var magicSpaceFragment: MagicSpaceFragment
    private lateinit var appMarketFragment: AppMarketFragment
    private lateinit var gameMarketFragment: GameFragment
    private lateinit var settingsFragment: SettingsFragment
    private lateinit var cartoonBookFragment: CartoonBookFragment

    private var menus: ArrayList<TextView> = ArrayList()

    private var menuTitles = arrayOf("视频教学", "魔法空间", "应用岛", "游戏岛", "设置", "绘本")

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SystemUtil.statusBarTransparent(window)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SystemUtil.setDarkStatusBar(window)
        Timber.d("display info: ${SystemUtil.displayInfo(this)}")

//        Glide.with(this)
//                .load(R.drawable.ic_cat)
//                .circleCrop()
//                .into(iv_avatar)

        mainFragment = MainFragment.newInstance()
        educationFragment = EducationFragment.newInstance()
        magicSpaceFragment = MagicSpaceFragment.newInstance()
        appMarketFragment = AppMarketFragment.newInstance()
        gameMarketFragment = GameFragment.newInstance()
        settingsFragment = SettingsFragment.newInstance()
        cartoonBookFragment = CartoonBookFragment.newInstance()

        menus.clear()
        menus.add(binding.tvMenuAi)
        menus.add(binding.tvMenuProgram)
        menus.add(binding.tvMenuInteract)
        menus.add(binding.tvMenuPuzzle)
        menus.add(binding.tvMenuApplication)
        refreshSelectedStatus(binding.tvMenuAi)
        binding.apply {
            tvMenuAi.setOnClickListener {
                refreshSelectedStatus(tvMenuAi)
                replaceFragment(mainFragment, R.id.fragment_container)
            }
            tvMenuProgram.setOnClickListener {
                refreshSelectedStatus(tvMenuProgram)
                replaceFragment(magicSpaceFragment, R.id.fragment_container)
            }
            tvMenuInteract.setOnClickListener {
                refreshSelectedStatus(tvMenuInteract)
                replaceFragment(gameMarketFragment, R.id.fragment_container)
            }
            tvMenuPuzzle.setOnClickListener {
                refreshSelectedStatus(tvMenuPuzzle)
                replaceFragment(cartoonBookFragment, R.id.fragment_container)
            }
            tvMenuApplication.setOnClickListener {
                refreshSelectedStatus(tvMenuApplication)
                replaceFragment(appMarketFragment, R.id.fragment_container)
            }
        }


//        container_menu.removeAllViews()
//        menuTitles.forEachIndexed { index, title ->
//            val tv = TextView(this)
//            tv.text = title
//            container_menu.addView(tv)
//            val lp = tv.layoutParams as LinearLayout.LayoutParams
//            lp.height = ResourceUtil.convertDpToPixel(56f, this).toInt()
//            lp.width = LinearLayout.LayoutParams.MATCH_PARENT
//            lp.topMargin = ResourceUtil.convertDpToPixel(20f, this).toInt()
//            tv.setBackgroundColor(resources.getColor(android.R.color.holo_blue_bright))
//            tv.gravity = Gravity.CENTER
//            tv.setOnClickListener {
//                when (index) {
//                    0 -> replaceFragment(educationFragment, R.id.fragment_container)
//                    1 -> replaceFragment(magicSpaceFragment, R.id.fragment_container)
//                    2 -> replaceFragment(appMarketFragment, R.id.fragment_container)
//                    3 -> replaceFragment(gameMarketFragment, R.id.fragment_container)
//                    4 -> replaceFragment(settingsFragment, R.id.fragment_container)
//                    5 -> replaceFragment(cartoonBookFragment, R.id.fragment_container)
//                }
//
//            }
//
//        }

        replaceFragment(mainFragment, R.id.fragment_container)

        // 监听网络状态变化
//        connectionStateMonitor.setConnectStateListener { isConnected ->
//            runOnUiThread {
//                if (!isConnected) {
//                    ToastUtil.showToast(this, "您已离线，请检查网络连接")
//                }
//                iv_wifi_state.setImageResource(if (isConnected) R.drawable.ic_wifi_on else R.drawable.ic_wifi_off)
//            }
//        }
//        connectionStateMonitor.enable(applicationContext)

        // 检查锁屏状态
        if (repo.prefs.isScreenLocked) {
            ScreenLockActivity.lock(this)
        }

        eyeProtectionMode()
    }

    override fun onDestroy() {
//        TuyaHomeSdk.onDestroy()
        super.onDestroy()
    }

    private fun refreshSelectedStatus(v: View) {
       menus.forEach { m ->
           if (v.id == m.id) {
               m.setBackgroundResource(R.drawable.shape_menu_selected)
           } else {
               m.setBackgroundResource(R.drawable.shape_menu_normal)
           }
       }
    }

    private fun eyeProtectionMode() {
        RootExecutor.exec(
            cmd = RootCommand.grantPermission(Manifest.permission.SYSTEM_ALERT_WINDOW),
            success = {
                ToastUtil.showToast(this, "权限申请成功")

                // 显示护眼弹窗
                CoroutineScope(Dispatchers.Default).launch {
                    delay(20 * 1000)
                    withContext(Dispatchers.Main) {
                        Timber.d("护眼模式")
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (Settings.canDrawOverlays(this@MainActivity)) {
                                eyeProtectionDialog()
                            } else {
                                try {
                                    startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION), RC_SYSTEM_ALERT_WINDOW_PERMISSION)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } else {
                            eyeProtectionDialog()
                        }
                    }
                }
            },
            failure = {
                ToastUtil.showToast(this, "权限申请成功")
            }
        )
    }

    private fun eyeProtectionDialog() {
        Timber.d("显示护眼模式弹窗")
        val dialog = AlertDialog.Builder(this@MainActivity)
            .setTitle("护眼模式")
            .setMessage("小朋友，你该休息了")
            .create()
        dialog.window?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
        dialog.show()
    }

}