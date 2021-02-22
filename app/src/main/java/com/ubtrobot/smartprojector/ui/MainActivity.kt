package com.ubtrobot.smartprojector.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.ActivityMainBinding
import com.ubtrobot.smartprojector.receivers.ConnectionStateMonitor
import com.ubtrobot.smartprojector.replaceFragment
import com.ubtrobot.smartprojector.repo.Repository
import com.ubtrobot.smartprojector.ui.appmarket.AppMarketFragment
import com.ubtrobot.smartprojector.ui.cartoonbook.CartoonBookFragment
import com.ubtrobot.smartprojector.ui.game.GameFragment
import com.ubtrobot.smartprojector.ui.login.LoginActivity
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
        Timber.d("display info: ${SystemUtil.displayInfo(this)}")

//        Glide.with(this)
//                .load(R.drawable.ic_cat)
//                .circleCrop()
//                .into(iv_avatar)

//        mainFragment = MainFragment.newInstance(0)
//        educationFragment = EducationFragment.newInstance()
//        magicSpaceFragment = MagicSpaceFragment.newInstance()
//        appMarketFragment = AppMarketFragment.newInstance()
//        gameMarketFragment = GameFragment.newInstance()
//        settingsFragment = SettingsFragment.newInstance()
//        cartoonBookFragment = CartoonBookFragment.newInstance()

//        menus.clear()
//        menus.add(binding.tvMenuAi)
//        menus.add(binding.tvMenuProgram)
//        menus.add(binding.tvMenuInteract)
//        menus.add(binding.tvMenuPuzzle)
//        menus.add(binding.tvMenuApplication)
//        refreshSelectedStatus(binding.tvMenuAi)
//        binding.apply {
//            tvMenuAi.setOnClickListener {
//                refreshSelectedStatus(tvMenuAi)
//                replaceFragment(mainFragment, R.id.fragment_container)
//            }
//            tvMenuProgram.setOnClickListener {
//                refreshSelectedStatus(tvMenuProgram)
//                replaceFragment(magicSpaceFragment, R.id.fragment_container)
//            }
//            tvMenuInteract.setOnClickListener {
//                refreshSelectedStatus(tvMenuInteract)
//                replaceFragment(gameMarketFragment, R.id.fragment_container)
//            }
//            tvMenuPuzzle.setOnClickListener {
//                refreshSelectedStatus(tvMenuPuzzle)
//                replaceFragment(cartoonBookFragment, R.id.fragment_container)
//            }
//            tvMenuApplication.setOnClickListener {
//                refreshSelectedStatus(tvMenuApplication)
//                replaceFragment(appMarketFragment, R.id.fragment_container)
//            }
//        }
//
//        replaceFragment(mainFragment, R.id.fragment_container)

        binding.viewPager.adapter = ScreenAdapter()
        // 缓存3页
        binding.viewPager.offscreenPageLimit = 3
        binding.pagerIndicator.setViewPager(binding.viewPager)


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

        initialTuyaHome()
    }

    override fun onDestroy() {
//        TuyaHomeSdk.onDestroy()
        super.onDestroy()
    }

    /**
     * 涂鸦home初始化
     */
    private fun initialTuyaHome() {
        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(object : ITuyaGetHomeListCallback {
            override fun onSuccess(homeBeans: MutableList<HomeBean>?) {
                Timber.d("家庭数量: ${homeBeans?.size}")
                if (homeBeans?.isNotEmpty() == true) {
                    val home = homeBeans.first()
                    repo.prefs.currentHomeId = home.homeId
                    repo.prefs.currentHomeName = home.name
//                    binding.tvHome.text = "家庭：${home.name}, ${home.homeId}"
//                    Timber.d("家庭：${home.homeId}, ${home.name}, ${home.deviceList.size}")
//                    homeDevicesQuery(home.homeId)
//                    homeId = home.homeId
                } else {
//                    binding.refreshLayout.finishRefresh(true)
                }
            }

            override fun onError(errorCode: String?, error: String?) {
//                binding.refreshLayout.finishRefresh(false)

                Timber.d("家庭查询失败: $errorCode, $error")
                if (errorCode == "USER_SESSION_LOSS") {
//                    ToastUtil.showToast(requireContext(), "登录已失效")
//                    LoginActivity.startWithNewTask(requireContext())
                }
            }
        })
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
                    delay(60 * 1000)
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Timber.d("onkeydown: ${keyCode}, ${event?.action}")
        return super.onKeyDown(keyCode, event)

    }

    // Launcher 禁止返回
    override fun onBackPressed() {

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

    private inner class ScreenAdapter : FragmentStatePagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount(): Int = 3

        override fun getItem(position: Int): Fragment = MainFragment.newInstance(position)
    }

}