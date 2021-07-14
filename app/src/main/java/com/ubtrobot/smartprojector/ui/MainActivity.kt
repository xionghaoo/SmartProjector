package com.ubtrobot.smartprojector.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginStart
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.tuya.smart.api.service.MicroServiceManager
import com.tuya.smart.commonbiz.bizbundle.family.api.AbsBizBundleFamilyService
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback
import com.ubtrobot.smartprojector.*
import com.ubtrobot.smartprojector.core.vo.Status
import com.ubtrobot.smartprojector.databinding.ActivityMainBinding
import com.ubtrobot.smartprojector.launcher.AppManager
import com.ubtrobot.smartprojector.receivers.ConnectionStateMonitor
import com.ubtrobot.smartprojector.ui.appmarket.AppMarketFragment
import com.ubtrobot.smartprojector.ui.call.BaseCallActivity
import com.ubtrobot.smartprojector.ui.call.CallingActivity
import com.ubtrobot.smartprojector.ui.profile.ProfileActivity
import com.ubtrobot.smartprojector.ui.restrict.ScreenLockActivity
import com.ubtrobot.smartprojector.ui.settings.SettingsActivity
import com.ubtrobot.smartprojector.ui.settings.SettingsFragment
import com.ubtrobot.smartprojector.ui.settings.eyesprotect.EyesProtectSettingsFragment
import com.ubtrobot.smartprojector.ui.video.VideoItem
import com.ubtrobot.smartprojector.ui.video.VideoPlayerActivity
import com.ubtrobot.smartprojector.utils.*
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtm.RemoteInvitation
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import xh.zero.agora_call.AgoraCallManager
import xh.zero.voice.TencentVoiceManager
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


@AndroidEntryPoint
class MainActivity : BaseCallActivity(), MainFragment.OnFragmentActionListener {

    companion object {

        private var instance: MainActivity? = null

        fun getLauncher() = instance

        fun setLauncher(launcher: MainActivity?) {
            instance = launcher
        }

        private const val RC_READ_PHONE_STATE_PERMISSION = 3
        private const val RC_PERMISSIONS = 4

        fun startWithNewTask(context: Context?) {
            val i = Intent(context, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context?.startActivity(i)
        }
    }

    @Inject
    lateinit var connectionStateMonitor: ConnectionStateMonitor
    @Inject
    lateinit var agoraCallManager: AgoraCallManager
    @Inject
    lateinit var voiceManager: TencentVoiceManager

    private val viewModel: MainViewModel by viewModels()

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
                    // 应用更新
                    screenAdapter.updateAppGrids()
                }
            }
        }
    }

    private var pageTitles = arrayOf("智能学习", "语文", "英语", "数学", "编程", "直播")

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        SystemUtil.toFullScreenMode(this)
        super.onCreate(savedInstanceState)
        setLauncher(this)
        SystemUtil.statusBarTransparent(window)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.d("display info: ${SystemUtil.displayInfo(this)}")
        Timber.d("navigation bar height: ${SystemUtil.getNavigationBarHeight(this)}")
        Timber.d("status bar height: ${SystemUtil.getStatusBarHeight(resources)}")

        requestPermissionsTask()

        initialStatusBar()

        screenAdapter = ScreenAdapter()
        binding.viewPager.adapter = screenAdapter
        // 缓存3页
        binding.viewPager.offscreenPageLimit = 3
        binding.pagerIndicator.setViewPager(binding.viewPager)
        binding.tvPageTitle.text = pageTitles[0]
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
            ) {
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
                if (position == pageTitles.size - 1) {
                    val tranX = binding.containerMainHeader.translationX
                    if (tranX != 0f) {
                        binding.containerMainHeader.animate().cancel()
                        binding.containerMainHeader.animate()
                                .translationX(0f)
                                .start()
                    }
                }
                if (position < pageTitles.size) {
                    binding.tvPageTitle.text = pageTitles[position]
                }

//                val bg = when (position) {
//                    0 -> R.raw.ic_assistant_bg
//                    1 -> R.raw.ic_chinese_bg
//                    2 -> R.raw.ic_english_bg
//                    3 -> R.raw.ic_mathematics_bg
//                    4 -> R.raw.ic_program_bg
//                    else -> R.raw.background
//                }
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
        if (viewModel.prefs().isScreenLocked) {
            ScreenLockActivity.lock(this)
        }

        // 头像跳转
        binding.containerAvatar.setOnClickListener {
            startPlainActivity(ProfileActivity::class.java)
        }

        // TODO 测试
//        binding.tvPageTitle.setOnClickListener {
//            val items = ArrayList<VideoItem>()
//            items.add(VideoItem("视频1", MockData.video1))
//            items.add(VideoItem("视频2", MockData.video2))
//            items.add(VideoItem("视频3", MockData.video3))
//            items.add(VideoItem("视频4", MockData.video4))
//            VideoPlayerActivity.start(this, items)
//        }

//        RootExecutor.exec(
//                cmd = "pm grant ${BuildConfig.APPLICATION_ID} ${Manifest.permission.SYSTEM_ALERT_WINDOW}",
//                success = {
//                    eyeProtectionMode()
//                },
//                failure = {
//                    Timber.e("SYSTEM_ALERT_WINDOW 权限申请失败")
//                }
//        )

        // 拓展网关初始化
//        TuyaGatewayManager.instance().initial()

        initialTuyaHome()

        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL)
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addDataScheme("package")
        registerReceiver(receiver, intentFilter)

        Glide.with(this@MainActivity)
            .load(R.mipmap.ic_launcher_bg)
            .centerCrop()
            .into(binding.ivMainBackground)

//        if (Build.VERSION.SDK_INT >= 26) {
//            test()
//        }
        // TODO 模拟序列号
//        viewModel.prefs().serialNumber = "12345678"
        initialAgoraToken()
    }

    // ---------------------- 读取SN 测试------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    @AfterPermissionGranted(RC_READ_PHONE_STATE_PERMISSION)
    fun test() {
        if (hasReadPhoneStatePermission()) {
            val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            val imei: String? = telephonyManager.imei
            Timber.d("设备SN号： ${Build.getSerial()}, imei: $imei")
        } else {
            EasyPermissions.requestPermissions(
                this,
                "App需要获取设备ID的权限，请授予",
                RC_READ_PHONE_STATE_PERMISSION,
                Manifest.permission.READ_PHONE_STATE
            )
        }

//        RootExecutor.exec(
//                cmd = "pm grant ${BuildConfig.APPLICATION_ID} ${Manifest.permission.READ_PHONE_STATE}",
//                success = {
//                    val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
//                    val imei: String? = telephonyManager.imei
//                    Timber.d("设备SN号： ${Build.getSerial()}, imei: $imei")
//                },
//                failure = {
//                    Timber.e("SYSTEM_ALERT_WINDOW 权限申请失败")
//                }
//        )

    }

    private fun hasReadPhoneStatePermission() : Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.READ_PHONE_STATE)
    }
    // ---------------------- 读取SN 测试------------------------

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        setLauncher(null)
        connectionStateMonitor.disable()
        agoraCallManager.destroy()
        voiceManager.release()
        super.onDestroy()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            window.decorView.systemUiVisibility = flags
        }
    }

    @AfterPermissionGranted(RC_PERMISSIONS)
    private fun requestPermissionsTask() {
        if (hasPermissions()) {
            viewModel.prefs().serialNumber?.also {
                voiceManager.initial(it, BuildConfig.VERSION_NAME, this)
            }
        } else {
            EasyPermissions.requestPermissions(
                this,
                "App需要申请相机和麦克风权限，请授予",
                RC_PERMISSIONS,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    private fun hasPermissions() : Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    }

    override fun getAgoraManager(): AgoraCallManager = agoraCallManager

    /**
     * 获取声网token
     */
    private fun initialAgoraToken() {
        val userId = viewModel.prefs().userID ?: return

        viewModel.getRTMToken(userId).observe(this, { r ->
            if (r.status == Status.SUCCESS) {
                val token = r.data?.data as? String
                viewModel.prefs().rtmToken = token
                agoraCallManager.login(token, userId)
            }
        })
    }

    private fun initialStatusBar() {
        connectionStateMonitor.enable(this)
        connectionStateMonitor.setConnectStateListener { isConnected ->
            runOnUiThread {
                binding.ivWifiStatus.setImageResource(
                        if (isConnected) R.drawable.ic_wifi_on else R.drawable.ic_wifi_off
                )
            }
        }

        binding.ivWifiStatus.setImageResource(
                if (SystemUtil.isNetworkConnected(this)) R.drawable.ic_wifi_on else R.drawable.ic_wifi_off
        )
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
                    viewModel.prefs().tuyaHomeId = home.homeId
                    viewModel.prefs().tuyaHomeName = home.name

                    Timber.d("home name: ${home.name}, home id: ${home.homeId}")

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

    fun getItemOptionView() = binding.itemOptionView

    // Launcher 禁止返回
    override fun onBackPressed() {

    }

    // -------------------------- OnFragmentActionListener Method Start -------------------------------
    override fun onItemSelected(v: View, align: HomeMenuDialog.Align, data: ArrayList<HomeMenuData>, type: HomeMenuDialog.Type) {
        HomeMenuDialog(
            context = this,
            rootView = binding.root,
            target = v,
            align = align,
            listData = data
        ).show(type)
    }
    // -------------------------- OnFragmentActionListener Method End -------------------------------

    private inner class ScreenAdapter : FragmentStatePagerAdapter(
            supportFragmentManager,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

        private val MAX_APP_NUM = 18

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
                    gridFrag.updateApps()
                }
            }
        }

        fun addApp() {
            if (appNum % MAX_APP_NUM == 0) {
                // 添加新的一页
                appNum += 1
                appPageNum = appNum / MAX_APP_NUM
                appGridList.add(AppMarketFragment.newInstance(MAX_APP_NUM, appPageNum))
                binding.pagerIndicator.updateItems(count)
                notifyDataSetChanged()
            } else {
                appNum += 1
                updateAppGrids()
            }
        }

        fun removeApp() {
            appNum -= 1
            if (appNum % MAX_APP_NUM == 0) {
                // 减少一页
                appPageNum = appNum / MAX_APP_NUM
                if (appGridList.isNotEmpty()) {
                    appGridList.removeAt(appGridList.size - 1)
                }
                binding.pagerIndicator.updateItems(count)
                notifyDataSetChanged()
            } else {
                updateAppGrids()
            }
        }

    }

}