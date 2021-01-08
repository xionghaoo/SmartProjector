package com.ubtrobot.smartprojector.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
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

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (BuildConfig.DEBUG) {
            edt_login_username.setText("abc")
            edt_login_password.setText("123")
        }
        btn_login.setOnClickListener {
            val username = edt_login_username.text.toString()
            val password = edt_login_password.text.toString()

            if (username == "abc" && password == "123") {
                viewModel.saveLoginInfo(username)
                MainActivity.startWithNewTask(this)
            } else {
                ToastUtil.showToast(this, "账号密码错误")
            }
        }
    }
}