package com.ubtrobot.smartprojector

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.tuya.smart.utils.ToastUtil
import com.ubtrobot.smartprojector.repo.SharedPreferenceStorage
import com.ubtrobot.smartprojector.ui.MainActivity
import com.ubtrobot.smartprojector.ui.login.LoginActivity
import com.ubtrobot.smartprojector.utils.SystemUtil
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import javax.inject.Inject

/**
 * 启动页面
 */
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    companion object {
        private const val RC_READ_PHONE_STATE_PERMISSION = 1
    }

    @Inject
    lateinit var perfs: SharedPreferenceStorage

    private val handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        SystemUtil.toFullScreenMode(this)
        super.onCreate(savedInstanceState)
        SystemUtil.statusBarTransparent(window)
        setContentView(R.layout.activity_splash)

        getSerialNumberTask()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    @AfterPermissionGranted(RC_READ_PHONE_STATE_PERMISSION)
    fun getSerialNumberTask() {
        if (hasReadPhoneStatePermission()) {
            var userId: String? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                Timber.d("本机序列号：${Build.getSerial()}")
                // TODO 加个0为了测试，后端序列号要求8位以上, Android 10 以上拿不到序列号
                perfs.serialNumber = Build.getSerial() + "a"
                userId = perfs.userID

            } else {
                ToastUtil.showToast(this, "获取序列号失败")
            }
            // TODO 测试
            if (BuildConfig.DEBUG) {
                perfs.serialNumber = "00110011a"
                userId = perfs.userID
            }
            handler.postDelayed({
                if (userId != null) {
                    startPlainActivity(MainActivity::class.java)
                } else {
                    startPlainActivity(LoginActivity::class.java)
                }
                finish()
            }, 200)
        } else {
            EasyPermissions.requestPermissions(
                this,
                "App需要读取本机序列号，请授予权限",
                RC_READ_PHONE_STATE_PERMISSION,
                Manifest.permission.READ_PHONE_STATE
            )
        }
    }

    private fun hasReadPhoneStatePermission() : Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.READ_PHONE_STATE)
    }
}