package com.ubtrobot.smartprojector.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
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
import com.ubtrobot.smartprojector.ui.call.BaseCallActivity
import com.ubtrobot.smartprojector.ui.elementary.ElementarySystemFragment
import com.ubtrobot.smartprojector.ui.elementary.ElementayMainFragment
import com.ubtrobot.smartprojector.ui.infant.InfantSystemFragment
import com.ubtrobot.smartprojector.ui.secondary.SecondarySystemFragment
import com.ubtrobot.smartprojector.ui.profile.ProfileActivity
import com.ubtrobot.smartprojector.ui.restrict.ScreenLockActivity
import com.ubtrobot.smartprojector.utils.*
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import xh.zero.agora_call.AgoraCallManager
import xh.zero.voice.TencentVoiceManager
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class MainActivity : BaseCallActivity(), ElementarySystemFragment.OnFragmentActionListener, ElementayMainFragment.OnFragmentActionListener {

    companion object {

        private var instance: MainActivity? = null

        fun getLauncher() = instance

        fun setLauncher(launcher: MainActivity?) {
            instance = launcher
        }

        const val SYSTEM_INFANT = 0
        const val SYSTEM_ELEMENTARY = 1
        const val SYSTEM_SECONDARY = 2

        private const val RC_READ_PHONE_STATE_PERMISSION = 3
        private const val RC_PERMISSIONS = 4

        private const val EXTRA_DATA_SYSTEM = "${Configs.PACKAGE_NAME}.MainActivity.EXTRA_DATA_SYSTEM"

        fun startWithNewTask(context: Context?) {
            val i = Intent(context, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context?.startActivity(i)
        }

        fun startWithSingleTop(context: Context?, system: Int) {
            val i = Intent(context, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            i.putExtra(EXTRA_DATA_SYSTEM, system)
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

//    private lateinit var screenAdapter: ScreenAdapter

    // app 安装卸载监听
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.d("on receiver: ${intent?.action}")
            when(intent?.action) {
                Intent.ACTION_PACKAGE_INSTALL -> {

                }
                Intent.ACTION_PACKAGE_ADDED -> {
                    // 应用安装
                    elementarySystemFragment.addApp()
                }
                Intent.ACTION_PACKAGE_REMOVED -> {
                    // 应用卸载
                    elementarySystemFragment.removeApp()
                }
                Intent.ACTION_PACKAGE_CHANGED -> {
                    // 应用更新
                    elementarySystemFragment.updateAppGrids()
                }
            }
        }
    }

    private var pageTitles = arrayOf("智能学习", "语文", "英语", "数学", "编程", "直播")

    private lateinit var binding: ActivityMainBinding

    private lateinit var elementarySystemFragment: ElementarySystemFragment
    private var systemType: Int = SYSTEM_ELEMENTARY

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

        systemType = intent.getIntExtra(EXTRA_DATA_SYSTEM, SYSTEM_ELEMENTARY)
        loadSystem()

        requestPermissionsTask()

        elementarySystemFragment = ElementarySystemFragment.newInstance()
        replaceFragment(elementarySystemFragment, R.id.main_fragment_container)
        initialStatusBar()

        binding.tvPageTitle.text = pageTitles[0]

        AppManager.getInstance(this).getAllApps()
        AppManager.getInstance(this).addUpdateListener { apps ->
            elementarySystemFragment.setAppNum(apps.size)
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

        initialTuyaHome()

        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL)
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addDataScheme("package")
        registerReceiver(receiver, intentFilter)

        initialAgoraToken()

        Glide.with(this)
            .load(R.mipmap.ic_launcher_bg)
            .centerCrop()
            .into(binding.ivMainBackground)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        systemType = intent.getIntExtra(EXTRA_DATA_SYSTEM, SYSTEM_ELEMENTARY)
        loadSystem()
    }

    private fun loadSystem() {
        when(systemType) {
            SYSTEM_INFANT -> replaceFragment(InfantSystemFragment.newInstance(), R.id.main_fragment_container)
            SYSTEM_ELEMENTARY -> replaceFragment(ElementarySystemFragment.newInstance(), R.id.main_fragment_container)
            SYSTEM_SECONDARY -> replaceFragment(SecondarySystemFragment.newInstance(), R.id.main_fragment_container)
        }
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

    /**
     * 初始化状态栏
     */
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

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
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
    }

    // -------------------------- OnFragmentActionListener Method End -------------------------------

}