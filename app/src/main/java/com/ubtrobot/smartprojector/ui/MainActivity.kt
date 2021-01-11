package com.ubtrobot.smartprojector.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.replaceFragment
import com.ubtrobot.smartprojector.update.UpdateDelegate
import com.ubtrobot.smartprojector.utils.ResourceUtil
import com.ubtrobot.smartprojector.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        fun startWithNewTask(context: Context?) {
            val i = Intent(context, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context?.startActivity(i)
        }
    }

    private lateinit var educationFragment: EducationFragment
    private lateinit var magicSpaceFragment: MagicSpaceFragment
    private lateinit var updateDelegate: UpdateDelegate

    private var menuTitles = arrayOf("视频教学", "魔法空间")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo

        educationFragment = EducationFragment.newInstance()
        magicSpaceFragment = MagicSpaceFragment.newInstance()

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
                }

            }

        }

        replaceFragment(educationFragment, R.id.fragment_container)

        updateDelegate = UpdateDelegate(this)
        updateDelegate.showVersionUpdateDialog(
                url = "http://cdn.llsapp.com/android/LLS-v4.0-595-20160908-143200.apk",
                versionName = "test1.0.0",
                content = "版本更新测试",
                isForce = true,
                cancel = {

                }
        )

    }

    private fun getWifiSSID() {
        val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun onDestroy() {
        TuyaHomeSdk.onDestroy()
        super.onDestroy()
    }
}