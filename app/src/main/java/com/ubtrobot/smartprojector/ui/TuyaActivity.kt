package com.ubtrobot.smartprojector.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    }
}