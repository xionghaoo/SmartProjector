package com.ubtrobot.smartprojector.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.tuya.smart.android.user.api.ILoginCallback
import com.tuya.smart.android.user.api.IRegisterCallback
import com.tuya.smart.android.user.api.IValidateCallback
import com.tuya.smart.android.user.bean.User
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.utils.ToastUtil
import com.ubtrobot.smartprojector.wifi.AccessPoint
import kotlinx.android.synthetic.main.activity_tuya.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

/**
 * 涂鸦测试页面
 */
class TuyaActivity : AppCompatActivity() {

    companion object {
        private const val RC_LOCATION_PERMISSION = 2
    }

    private lateinit var wifiManager: WifiManager

    private val wifiScanReceiver = object : BroadcastReceiver() {

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            stopScan()
            if (success) {
                scanSuccess()
            } else {
                scanFailure()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tuya)

        btn_phone_validate_code.setOnClickListener {
            TuyaHomeSdk.getUserInstance().getValidateCode(
                "86",
                "18617039316",
                object : IValidateCallback {
                    override fun onSuccess() {
                        Timber.d("验证成功")
                        ToastUtil.showToast(this@TuyaActivity, "验证码已发送")
                    }

                    override fun onError(code: String?, error: String?) {
                        Timber.d("验证失败：$code, $error")
                        ToastUtil.showToast(this@TuyaActivity, "验证失败：$code, $error")
                    }
                }
            )
        }

        btn_register.setOnClickListener {
            val account = edt_account.text.toString()
            val password = edt_password.text.toString()
            val validateCode = edt_validate_code.text.toString()
            TuyaHomeSdk.getUserInstance().registerAccountWithPhone(
                "86",
                account,
                password,
                validateCode,
                object : IRegisterCallback {
                    override fun onSuccess(user: User?) {
                        Timber.d("注册成功")
                        ToastUtil.showToast(this@TuyaActivity, "注册成功")
                    }

                    override fun onError(code: String?, error: String?) {
                        Timber.d("注册失败")
                        ToastUtil.showToast(this@TuyaActivity, "注册失败: $error")
                    }
                }
            )
        }

        btn_login.setOnClickListener {
            val account = edt_account.text.toString()
            val password = edt_password.text.toString()
            TuyaHomeSdk.getUserInstance().loginWithPhonePassword(
                "86",
                account,
                password,
                object : ILoginCallback {
                    override fun onSuccess(user: User?) {
                        ToastUtil.showToast(this@TuyaActivity, "登录成功")
                    }

                    override fun onError(code: String?, error: String?) {
                        ToastUtil.showToast(this@TuyaActivity, "登陆失败")
                    }
                }
            )
        }

        btn_wifi_scan.setOnClickListener {
            wifiScanTask()
        }

        btn_wifi_scan_stop.setOnClickListener {
            stopScan()
        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        stopScan()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(RC_LOCATION_PERMISSION)
    private fun wifiScanTask() {
        if (hasFineLocationPermission()) {
            startScan()
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "App需要位置权限进行Wifi扫描，请授予",
                    RC_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun startScan() {
        // wifi ssid扫描
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiScanReceiver, intentFilter)

        radar_view.visibility = View.VISIBLE
        radar_view.start()
        val success = wifiManager.startScan()
        Timber.d("wifi scan: $success")
        if (!success) {
            // scan failure handling
            scanFailure()
        }
    }

    private fun stopScan() {
        unregisterReceiver(wifiScanReceiver)
        radar_view.stop()
        radar_view.visibility = View.GONE
    }

    private fun scanSuccess() {
        ToastUtil.showToast(this, "扫描完成")
        val results = wifiManager.scanResults.map { r -> AccessPoint(r.SSID) }
        Timber.d("scanSuccess: ${results.size}")
        results.forEach { p ->
            Timber.d("wifi ssid: ${p.ssid}")
        }
    }

    private fun scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        val results = wifiManager.scanResults
        Timber.d("scanFailure: ${results.size}")
    }


    private fun hasFineLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(
                this, Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}