package com.ubtrobot.smartprojector.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.tuya.smart.api.service.MicroServiceManager
import com.tuya.smart.commonbiz.bizbundle.family.api.AbsBizBundleFamilyService
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback
import com.ubtrobot.smartprojector.databinding.ActivityMainBinding
import com.ubtrobot.smartprojector.launcher.AppManager
import com.ubtrobot.smartprojector.receivers.ConnectionStateMonitor
import com.ubtrobot.smartprojector.repo.Repository
import com.ubtrobot.smartprojector.startPlainActivity
import com.ubtrobot.smartprojector.tuyagw.TuyaGatewayManager
import com.ubtrobot.smartprojector.ui.appmarket.AppMarketFragment
import com.ubtrobot.smartprojector.ui.cartoonbook.CartoonBookFragment
import com.ubtrobot.smartprojector.ui.game.GameFragment
import com.ubtrobot.smartprojector.ui.restrict.ScreenLockActivity
import com.ubtrobot.smartprojector.ui.settings.SettingsActivity
import com.ubtrobot.smartprojector.ui.settings.SettingsFragment
import com.ubtrobot.smartprojector.utils.*
import dagger.hilt.android.AndroidEntryPoint
import eu.chainfire.libsuperuser.Shell
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {

        private var instance: MainActivity? = null

        fun getLauncher() = instance

        fun setLauncher(launcher: MainActivity?) {
            instance = launcher
        }

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

//    private var menus: ArrayList<TextView> = ArrayList()

    private lateinit var screenAdapter: ScreenAdapter

    // app 安装卸载监听
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.d("on receiver: ${intent?.action}")
            when(intent?.action) {
                Intent.ACTION_PACKAGE_INSTALL -> {

                }
                Intent.ACTION_PACKAGE_ADDED -> {
                    // 应用安装
                    screenAdapter.addApp()
                }
                Intent.ACTION_PACKAGE_REMOVED -> {
                    // 应用卸载
                    screenAdapter.removeApp()
                }
                Intent.ACTION_PACKAGE_CHANGED -> {

                }
            }
        }
    }

    private var pageTitles = arrayOf("同步语文", "同步英语", "同步数学", "AI编程", "智能辅导", "优必选严选")

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLauncher(this)

        SystemUtil.statusBarTransparent(window)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val display = SystemUtil.displayInfo(this)
//        Timber.d("display info: ${SystemUtil.displayInfo(this)}")

        screenAdapter = ScreenAdapter()
        binding.viewPager.adapter = screenAdapter
        // 缓存3页
        binding.viewPager.offscreenPageLimit = 3
        binding.pagerIndicator.setViewPager(binding.viewPager)

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
//                Timber.d("position: $position, positionOffset: $positionOffset, pixel: $positionOffsetPixels, screen width = ${display.widthPixels}")
                if (position == pageTitles.size - 1) {
                    // 缩放动画
//                    val offsetConvertValue = if (positionOffset < 0.5) 1 - 2 * positionOffset else 0f
//                    binding.containerMainHeader.alpha = offsetConvertValue
//                    binding.containerMainHeader.scaleX = offsetConvertValue
//                    binding.containerMainHeader.scaleY = offsetConvertValue
                    // 平移动画
                    binding.containerMainHeader.translationX = -positionOffsetPixels.toFloat()
                }
            }

            override fun onPageSelected(position: Int) {
                if (position < pageTitles.size) {
//                    binding.containerMainHeader.visibility = View.VISIBLE
                    binding.tvPageTitle.text = pageTitles[position]
                } else {
//                    binding.containerMainHeader.visibility = View.GONE
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        AppManager.getInstance(this).getAllApps()
        AppManager.getInstance(this).addUpdateListener { apps ->
            screenAdapter.setAppNum(apps.size)
            true
        }

        // 检查锁屏状态
        if (repo.prefs.isScreenLocked) {
            ScreenLockActivity.lock(this)
        }

        binding.containerAvatar.setOnClickListener {
            startPlainActivity(SettingsActivity::class.java)
        }

        eyeProtectionMode()

        // 拓展网关初始化
        TuyaGatewayManager.instance().initial()

        initialTuyaHome()

        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL)
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addDataScheme("package")
        registerReceiver(receiver, intentFilter)
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        setLauncher(null)
        super.onDestroy()
    }

    /**.
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

                    val service = MicroServiceManager.getInstance()
                        .findServiceByInterface<AbsBizBundleFamilyService>(
                            AbsBizBundleFamilyService::class.java.name
                        )
                    service.currentHomeId = home.homeId
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

//    private fun refreshSelectedStatus(v: View) {
//       menus.forEach { m ->
//           if (v.id == m.id) {
//               m.setBackgroundResource(R.drawable.shape_menu_selected)
//           } else {
//               m.setBackgroundResource(R.drawable.shape_menu_normal)
//           }
//       }
//    }

    fun getItemOptionView() = binding.itemOptionView

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
                                    startActivityForResult(
                                        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION),
                                        RC_SYSTEM_ALERT_WINDOW_PERMISSION
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                         1       }
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

//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        Timber.d("onkeydown: ${keyCode}, ${event?.action}")
//        return super.onKeyDown(keyCode, event)
//
//    }

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

    private inner class ScreenAdapter : FragmentStatePagerAdapter(
        supportFragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

        private val MAX_APP_NUM = 20

        private var appNum: Int = 0
        private var appPageNum: Int = 0

        private val appGridList = ArrayList<AppMarketFragment>()

        override fun getCount(): Int = pageTitles.size + appGridList.size

        override fun getItem(position: Int): Fragment {
            if (position < pageTitles.size) {
                return MainFragment.newInstance(position)
            } else {
                val gridPosition = position - pageTitles.size
                val frag = appGridList[gridPosition]
                frag.setPosition(gridPosition)
                return frag
            }
        }

        // 删除页需要用到
        override fun getItemPosition(obj: Any): Int {
            return PagerAdapter.POSITION_NONE
        }

        fun setAppNum(num: Int) {
            appNum = num
            appPageNum = if (appNum % MAX_APP_NUM == 0) appNum / MAX_APP_NUM else appNum / MAX_APP_NUM + 1
            appGridList.clear()
            for (i in 0.until(appPageNum)) {
                appGridList.add(AppMarketFragment.newInstance(MAX_APP_NUM, appPageNum))
            }
            binding.pagerIndicator.updateItems(count)
            notifyDataSetChanged()
        }

        fun updateAppGrids() {
            appGridList.forEach { gridFrag ->
                if (gridFrag.isAdded) {
                    Timber.d("updateAppGrids: ${gridFrag.javaClass.name}")
                    gridFrag.updateApps()
                }
            }
        }

        fun addApp() {
            if (appNum % MAX_APP_NUM == 0) {
                // 添加新的一页
                appNum += 1
                appPageNum = if (appNum % MAX_APP_NUM == 0) appNum / MAX_APP_NUM else appNum / MAX_APP_NUM + 1
                appGridList.add(AppMarketFragment.newInstance(MAX_APP_NUM, appPageNum))
                binding.pagerIndicator.updateItems(count)
                notifyDataSetChanged()
            } else {
                updateAppGrids()
            }
        }

        fun removeApp() {
            if ((appNum - 1) % MAX_APP_NUM == 0) {
                appNum -= 1
                appPageNum = if (appNum % MAX_APP_NUM == 0) appNum / MAX_APP_NUM else appNum / MAX_APP_NUM + 1
                if (appGridList.isNotEmpty()) {
                    appGridList.removeAt(appGridList.size - 1)
                }
                binding.pagerIndicator.updateItems(count)
                notifyDataSetChanged()
            } else {
                updateAppGrids()
            }
        }

//        fun updateApps() {
//            AppManager.getInstance(this@MainActivity).getAllApps()
//            AppManager.getInstance(this@MainActivity).addUpdateListener { apps ->
//                val newAppNum = apps.size
//                val pageNum = if (newAppNum % MAX_APP_NUM == 0) newAppNum / MAX_APP_NUM else newAppNum / MAX_APP_NUM + 1
//                if (pageNum != appPageNum) {
//                    setAppNum(pageNum)
//                } else {
//                    updateAppGrids()
//                }
//                false
//            }
//        }
    }

}