package com.ubtrobot.smartprojector.utils

import android.app.Activity
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.repo.PreferenceStorage
import com.ubtrobot.smartprojector.ui.settings.eyesprotect.EyesProtectSettingsFragment
import timber.log.Timber


class JXWAppManager(prefs: PreferenceStorage) {

    companion object {
        private const val RC_SYSTEM_ALERT_WINDOW_PERMISSION = 1
    }

    fun startChinesePage(context: Activity) {

    }

    /**
     * 同步点读
     */
    fun startBookFingerRead(context: Activity, subject: String) {
        startApp(
            context,
            "com.jxw.online_study",
            "com.jxw.online_study.activity.BookCaseWrapperActivity",
            subject
        )
    }

    private fun startApp(context: Activity, packageName: String, className: String, subject: String) {
        val i = Intent()
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .setClassName(packageName, className)
            .putExtra("StartArgs", "d:/同步学习/${subject}|e:JWLB")   	//String:必传：跳转Flag
        context.startActivity(i)

//        showFloatButton(context)
    }

    private fun showFloatButton(context: Activity) {

        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(context)) {

                val v = LayoutInflater.from(context.applicationContext).inflate(R.layout.test_float_item, null)
                val param = WindowManager.LayoutParams()
                param.format = PixelFormat.RGBA_8888 // 解决带Alpha的32位png图片失真问题

                param.gravity = Gravity.LEFT or Gravity.TOP //显示在左上角

                param.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                // 在设置宽高
                param.x = 0
                param.y = 0
                param.width = WindowManager.LayoutParams.WRAP_CONTENT
                param.height = WindowManager.LayoutParams.WRAP_CONTENT
                param.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY //设置悬浮窗的层次
                context.windowManager.addView(v, param)
//                eyeProtectionDialog(context)
            } else {
                try {
                    context.startActivityForResult(
                        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION),
                        RC_SYSTEM_ALERT_WINDOW_PERMISSION
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
//            eyeProtectionDialog()
        }


    }

    private fun eyeProtectionDialog(context: Activity) {
        Timber.d("显示护眼模式弹窗")
        val dialog = AlertDialog.Builder(context)
            .setTitle("护眼模式")
            .setMessage("小朋友，你该休息了")
            .create()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        } else {
            dialog.window?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
        }
        dialog.show()
    }
}