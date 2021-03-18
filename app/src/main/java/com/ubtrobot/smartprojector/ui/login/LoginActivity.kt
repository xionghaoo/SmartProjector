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
import com.ubtrobot.smartprojector.databinding.ActivityLoginBinding
import com.ubtrobot.smartprojector.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
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