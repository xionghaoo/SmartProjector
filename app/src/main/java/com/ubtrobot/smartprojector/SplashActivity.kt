package com.ubtrobot.smartprojector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.ubtrobot.smartprojector.repo.SharedPreferenceStorage
import com.ubtrobot.smartprojector.ui.MainActivity
import com.ubtrobot.smartprojector.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * 启动页面
 */
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var perfs: SharedPreferenceStorage

    private val handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val username = perfs.loginUsername
        handler.postDelayed({
            if (username != null) {
                startPlainActivity(MainActivity::class.java)
            } else {
                startPlainActivity(LoginActivity::class.java)
            }
            finish()
        }, 200)
    }
}