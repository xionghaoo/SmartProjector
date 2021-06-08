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
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
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
        super.onCreate(savedInstanceState)
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                // Android 10 以上拿不到序列号
                perfs.serialNumber = Build.getSerial()

                val username = perfs.loginUsername
                handler.postDelayed({
                    if (username != null) {
                        startPlainActivity(MainActivity::class.java)
                    } else {
                        startPlainActivity(LoginActivity::class.java)
                    }
                    finish()
                }, 200)
            } else {
                ToastUtil.showToast(this, "获取序列号失败")
            }
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