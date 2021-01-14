package com.ubtrobot.smartprojector.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.keyboard.controllers.NumberKeyboardController
import com.ubtrobot.smartprojector.receivers.ConnectionStateMonitor
import com.ubtrobot.smartprojector.replaceFragment
import com.ubtrobot.smartprojector.repo.Repository
import com.ubtrobot.smartprojector.ui.appmarket.AppMarketFragment
import com.ubtrobot.smartprojector.ui.settings.SettingsFragment
import com.ubtrobot.smartprojector.update.UpdateDelegate
import com.ubtrobot.smartprojector.utils.ResourceUtil
import com.ubtrobot.smartprojector.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
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

    private lateinit var educationFragment: EducationFragment
    private lateinit var magicSpaceFragment: MagicSpaceFragment
    private lateinit var appMarketFragment: AppMarketFragment
    private lateinit var gameMarketFragment: AppMarketFragment
    private lateinit var settingsFragment: SettingsFragment

    private var menuTitles = arrayOf("视频教学", "魔法空间", "应用岛", "游戏岛", "设置")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        educationFragment = EducationFragment.newInstance()
        magicSpaceFragment = MagicSpaceFragment.newInstance()
        appMarketFragment = AppMarketFragment.newInstance()
        gameMarketFragment = AppMarketFragment.newInstance(true)
        settingsFragment = SettingsFragment.newInstance()

        container_menu.removeAllViews()
        menuTitles.forEachIndexed { index, title ->
            val tv = TextView(this)
            tv.text = title
            container_menu.addView(tv)
            val lp = tv.layoutParams as LinearLayout.LayoutParams
            lp.height = ResourceUtil.convertDpToPixel(56f, this).toInt()
            lp.width = LinearLayout.LayoutParams.MATCH_PARENT
            lp.topMargin = ResourceUtil.convertDpToPixel(20f, this).toInt()
            tv.setBackgroundColor(resources.getColor(android.R.color.holo_blue_bright))
            tv.gravity = Gravity.CENTER
            tv.setOnClickListener {
                when (index) {
                    0 -> replaceFragment(educationFragment, R.id.fragment_container)
                    1 -> replaceFragment(magicSpaceFragment, R.id.fragment_container)
                    2 -> replaceFragment(appMarketFragment, R.id.fragment_container)
                    3 -> replaceFragment(gameMarketFragment, R.id.fragment_container)
                    4 -> replaceFragment(settingsFragment, R.id.fragment_container)
                }

            }

        }

        replaceFragment(educationFragment, R.id.fragment_container)

        // 监听网络状态变化
        connectionStateMonitor.setConnectStateListener { isConnected ->
            runOnUiThread {
                if (!isConnected) {
                    ToastUtil.showToast(this, "您已离线，请检查网络连接")
                }
                iv_wifi_state.setImageResource(if (isConnected) R.drawable.ic_wifi_on else R.drawable.ic_wifi_off)
            }
        }
        connectionStateMonitor.enable(applicationContext)

        if (repo.prefs.isScreenLocked) {
            ScreenLockActivity.lock(this)
        }
    }

    private fun isNetworkConnected(context: Context?): Boolean {
        if (context == null) return false

        val connectivityManager =
                context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }

    private fun getWifiSSID() {
        val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun onDestroy() {
        TuyaHomeSdk.onDestroy()
        super.onDestroy()
    }
}