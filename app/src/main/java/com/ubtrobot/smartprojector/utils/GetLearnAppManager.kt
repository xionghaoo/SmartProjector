package com.ubtrobot.smartprojector.utils

import android.content.Context
import android.content.Intent
import com.ubtrobot.smartprojector.repo.PreferenceStorage

/**
 * 格灵app页面启动器
 */
class GetLearnAppManager(private val prefs: PreferenceStorage) {

    fun startChinesePage(context: Context, flag: String) {
        startPage(context, flag, "语文")
    }

    fun startMathematicsPage(context: Context, flag: String) {
        startPage(context, flag, "数学")
    }

    fun startEnglishPage(context: Context, flag: String) {
        startPage(context, flag, "英语")
    }

    private fun startPage(context: Context, flag: String, course: String) {
        val i = Intent()
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setClassName("com.wyt.youbixuan", "com.wyt.youbixuan.main.InvokeYBXActivity")
                .putExtra("key_intent_flag", flag)   	//String:必传：跳转Flag
                .putExtra("key_main_grade", "1")     	//String:同步学习内容下必传：年级（默认一年级） || （可传入String类型：0~6，不可拼接，需要精确某个年级时候调用）
                .putExtra("key_subject", course)       	//String:选传：学科 ||（可传入：语文，数学，英语，科学，物理，化学，生物，政治，历史，地理）
                .putExtra("key_xueduan", "小学")			//String:选传：（传 2（小学） 3（初中） 4（高中） 可拼接，例如：传2,3（小学、初中），需要精确某些学段时候调用）
                .putExtra("key_change_graSub", false) //Boolean:非必传 || 是否可以切换年级学科 默认false,即不能切换年级学科
        context.startActivity(i)
    }

}