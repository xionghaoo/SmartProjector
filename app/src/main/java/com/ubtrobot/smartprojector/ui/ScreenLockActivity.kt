package com.ubtrobot.smartprojector.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.keyboard.controllers.NumberKeyboardController
import com.ubtrobot.smartprojector.repo.Repository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_screen_lock.*
import javax.inject.Inject

/**
 * 锁屏页面
 */
@AndroidEntryPoint
class ScreenLockActivity : AppCompatActivity() {

    companion object {
        fun lock(context: Context?) {
            val intent = Intent(context, ScreenLockActivity::class.java)
            context?.startActivity(intent)
        }
    }

    @Inject
    lateinit var repo: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_lock)

        // 启动前添加动画
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        repo.prefs.isScreenLocked = true

        number_keyboard.setController(
            NumberKeyboardController(edt_lock_password.onCreateInputConnection(
                EditorInfo()
            ))
        )
        edt_lock_password.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                val pwd = text?.toString() ?: ""
                if (pwd.length == 4 && pwd == repo.prefs.screenLockPwd) {
                    repo.prefs.isScreenLocked = false
                    finish()
                    // 销毁时添加退出动画
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }
            }
        )
    }

    override fun onBackPressed() {
        // 锁屏页面禁止返回
    }
}