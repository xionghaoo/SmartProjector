package com.ubtrobot.smartprojector.ui.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.tuya.smart.android.user.api.ILoginCallback
import com.tuya.smart.android.user.bean.User
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.ubtrobot.smartprojector.BuildConfig
import com.ubtrobot.smartprojector.ui.MainActivity
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

/**
 * 登录页面
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    companion object {
        fun startWithNewTask(context: Context?) {
            val i = Intent(context, LoginActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context?.startActivity(i)
        }
    }

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (BuildConfig.DEBUG) {
            edt_login_username.setText("18617039316")
            edt_login_password.setText("123456")
        }
        btn_login.setOnClickListener {
            val username = edt_login_username.text.toString()
            val password = edt_login_password.text.toString()

//            if (username == "abc" && password == "123") {
//                viewModel.saveLoginInfo(username)
//                MainActivity.startWithNewTask(this)
//            } else {
//                ToastUtil.showToast(this, "账号密码错误")
//            }

            TuyaHomeSdk.getUserInstance().loginWithPhonePassword(
                "86",
                username,
                password,
                object : ILoginCallback {
                    override fun onSuccess(user: User?) {
                        MainActivity.startWithNewTask(this@LoginActivity)
                        ToastUtil.showToast(this@LoginActivity, "登录成功")
                    }

                    override fun onError(code: String?, error: String?) {
                        ToastUtil.showToast(this@LoginActivity, "登陆失败")
                    }
                }
            )
        }
    }
}