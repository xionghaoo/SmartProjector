package com.ubtrobot.smartprojector.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.tuya.smart.android.user.api.ILoginCallback
import com.tuya.smart.android.user.api.IRegisterCallback
import com.tuya.smart.android.user.api.IValidateCallback
import com.tuya.smart.android.user.bean.User
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_tuya.*
import timber.log.Timber

class TuyaActivity : AppCompatActivity() {

    private lateinit var wifiManager: WifiManager

    private val wifiScanReceiver = object : BroadcastReceiver() {

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
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

        radar_view.start()

        // wifi ssid扫描
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiScanReceiver, intentFilter)

        val success = wifiManager.startScan()
        if (!success) {
            // scan failure handling
            scanFailure()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        radar_view.stop()
    }

    private fun scanSuccess() {
        val results = wifiManager.scanResults
        Timber.d("scanSuccess: ${results.size}")
    }

    private fun scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        val results = wifiManager.scanResults
        Timber.d("scanFailure: ${results.size}")
    }
}