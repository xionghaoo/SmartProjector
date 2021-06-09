package com.ubtrobot.smartprojector.ui.login

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.tuya.smart.android.user.api.IUidLoginCallback
import com.tuya.smart.android.user.bean.User
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.ubtrobot.smartprojector.BuildConfig
import com.ubtrobot.smartprojector.core.vo.Status
import com.ubtrobot.smartprojector.databinding.ActivityLoginBinding
import com.ubtrobot.smartprojector.ui.BaseActivity
import com.ubtrobot.smartprojector.ui.MainActivity
import com.ubtrobot.smartprojector.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber


/**
 * 登录页面
 */
@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    companion object {
        private const val RC_READ_PHONE_STATE_PERMISSION = 3

        fun startWithNewTask(context: Context?) {
            val i = Intent(context, LoginActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context?.startActivity(i)
        }
    }

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (BuildConfig.DEBUG) {
            binding.edtLoginUsername.setText("18617039316")
            binding.edtLoginPassword.setText("123456")
        }
        binding.btnLogin.setOnClickListener {
            val username = binding.edtLoginUsername.text.toString()
            val password = binding.edtLoginPassword.text.toString()

//            TuyaHomeSdk.getUserInstance().loginWithPhonePassword(
//                "86",
//                username,
//                password,
//                object : ILoginCallback {
//                    override fun onSuccess(user: User?) {
//                        MainActivity.startWithNewTask(this@LoginActivity)
//                        ToastUtil.showToast(this@LoginActivity, "登录成功")
//                    }
//
//                    override fun onError(code: String?, error: String?) {
//                        ToastUtil.showToast(this@LoginActivity, "登陆失败")
//                    }
//                }
//            )
            // 序列号登陆
            viewModel.login().observe(this, { loginResult ->
                if (loginResult.status == Status.SUCCESS) {
                    val userId = loginResult.data?.user?.userId?.toString()
                    viewModel.prefs().agoraUID = userId
                    // 涂鸦账号登陆
                    tuyaUidLogin(userId)
                }
                if (loginResult.status == Status.LOADING) {
                    loadingDialog.show()
                }
                if (loginResult.status == Status.ERROR) {
                    loadingDialog.dismiss()
                }
            })
        }

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

    private fun tuyaUidLogin(uid: String?) {
        if (uid == null) {
            ToastUtil.showToast(this, "用户id不能为空")
            loadingDialog.dismiss()
            return
        }
        TuyaHomeSdk.getUserInstance().loginOrRegisterWithUid(
            "86",
            uid,
            // 密码
            viewModel.prefs().serialNumber,
            true,
            object : IUidLoginCallback {
                override fun onSuccess(user: User?, homeId: Long) {
                    viewModel.prefs().tuyaHomeId = homeId
                    loadingDialog.dismiss()
//                    viewModel.prefs().
                    MainActivity.startWithNewTask(this@LoginActivity)
                    ToastUtil.showToast(this@LoginActivity, "登录成功")
                }

                override fun onError(code: String?, error: String?) {
                    loadingDialog.dismiss()
                    ToastUtil.showToast(this@LoginActivity, "登陆失败")
                }
            }
        )
    }

    /**
     * 生成序列号二维码
     */
    private fun generateQrcode(serialNumber: String) {
        Timber.d("serialNumber: ${serialNumber}")
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(serialNumber, BarcodeFormat.QR_CODE, 400, 400)
            binding.qrcodeSerialNumber.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @AfterPermissionGranted(RC_READ_PHONE_STATE_PERMISSION)
    fun getSerialNumberTask() {
        if (hasReadPhoneStatePermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                // Android 10 以上拿不到序列号
                val serialNumber = Build.getSerial()
                viewModel.saveSerialNumber(serialNumber)
                generateQrcode(serialNumber)
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