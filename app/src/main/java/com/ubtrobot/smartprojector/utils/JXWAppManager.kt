package com.ubtrobot.smartprojector.utils

import android.content.Context
import android.content.Intent
import com.ubtrobot.smartprojector.repo.PreferenceStorage

class JXWAppManager(prefs: PreferenceStorage) {

    /**
     * 同步点读
     */
    fun startBookRead(context: Context, subject: String) {
        startApp(
            context,
            "com.jxw.online_study",
            "com.jxw.online_study.activity.BookCaseWrapperActivity",
            subject
        )
    }

    private fun startApp(context: Context, packageName: String, className: String, subject: String) {
        val i = Intent()
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            .setClassName(packageName, className)
            .putExtra("StartArgs", "d:/同步学习/${subject}|e:JWLB")   	//String:必传：跳转Flag
        context.startActivity(i)
    }
}