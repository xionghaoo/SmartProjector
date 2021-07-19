package com.ubtrobot.smartprojector.utils

import android.app.Activity
import android.content.Context
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
import timber.log.Timber

enum class JxwAppType {
    LEARN_PINYIN, PIANPANG_BUSHOU, BIHUA_NAME, BISHUN_RULE,
    LEARN_CHINESE, ZICI_LISTEN, CHINESE_INTEREST, ORAL_ENGLISH_TEST,
    MATH_LEARN_NUMBER, MATH_FORMULA, CHINESE_ANIME_IDIOM, CHINESE_ANIME_GUOXUE,
    CHINESE_DICT, MATH_ARITHMETIC, MATH_FOCUS_PRACTISE, ENGLISH_MEMORIZE_WORDS,
    CHINESE_EXTENSION, TRANSLATE, ZHIJIAN_CAHCI
}

class JXWAppManager(private val prefs: PreferenceStorage) {

    companion object {
        private const val RC_SYSTEM_ALERT_WINDOW_PERMISSION = 1
    }

    /**
     * 其他页面
     */
    fun startOtherPage(context: Context, type: JxwAppType) {
        when (type) {
            // 拼音学习
            JxwAppType.LEARN_PINYIN ->
                startOtherApp(context, "com.jxw.learnchinesepinyin", "com.jxw.learnchinesepinyin.activity.MainActivity")
            // 偏旁部首
            JxwAppType.PIANPANG_BUSHOU ->
                startOtherApp(context, "com.example.pianpangbushou", "com.example.viewpageindicator.MainActivity")
            // 笔画名称
            JxwAppType.BIHUA_NAME ->
                startOtherApp(context, "com.jxw.bihuamingcheng", "com.example.viewpageindicator.MainActivity")
            // 笔顺规则
            JxwAppType.BISHUN_RULE ->
                startOtherApp(context, "com.jxw.bishunguize", "com.example.viewpageindicator.MainActivity")
            // 汉字学习
            JxwAppType.LEARN_CHINESE ->
                startOtherApp(context, "com.jxw.characterlearning", "com.jxw.characterlearning.MainActivity")
            // 字词听写
            JxwAppType.ZICI_LISTEN ->
                startOtherApp(context, "com.jxw.handwrite", "com.jxw.handwrite.ZymsActivity")
            // 语文写作
//            JxwAppType.CHINESE_WRITE ->
//                startOtherApp(context, "com.jxw.yuwenxiezuo", "com.jxw.yuwenxiezuo.MainActivity")
            // 趣味语文
            JxwAppType.CHINESE_INTEREST ->
                startOtherApp(context, "com.jxw.qwyw", "com.jxw.qwyw.MainActivity")
            // 口语测评
            JxwAppType.ORAL_ENGLISH_TEST ->
                startOtherApp(context, "com.jxw.singsound", "com.jxw.singsound.ui.ZXSplashActivity")
            // 认识数字
            JxwAppType.MATH_LEARN_NUMBER ->
                startOtherApp(context, "com.jxw.studydigital", "com.jxw.studydigital.StuDydigitalActivity")
            // 算术口诀
            JxwAppType.MATH_ARITHMETIC ->
                startOtherApp(context, "com.example.arithmeticformula", "com.example.arithmeticformula.MainActivity")
            // 动漫学成语
            JxwAppType.CHINESE_ANIME_IDIOM ->
                startOtherApp(context, "com.jxw.dmxcy", "com.jxw.dmxcy.MainActivity")
            // 动漫学国学
            JxwAppType.CHINESE_ANIME_GUOXUE ->
                startOtherApp(context, "com.jxw.dmxgx", "com.jxw.dmxgx.MainActivity")
            // 指尖查词
            JxwAppType.CHINESE_DICT ->
                startOtherApp(context, "com.jxw.zncd", "com.jxw.zncd.MainActivity")
            // 数学公式
            JxwAppType.MATH_FORMULA ->
                startOtherApp(context, "com.oirsdfg89.flg", "com.nsc.mathformulas.MainActivity")
            // 专注力训练
            JxwAppType.MATH_FOCUS_PRACTISE -> {
                startOtherApp(context, "com.jxw.schultegrid", "com.jxw.schultegrid.SettingActivity")
            }
            // 五维记单词
            JxwAppType.ENGLISH_MEMORIZE_WORDS -> {
                startOtherApp(context, "com.jxw.wuweijidanci", "com.jxw.wuweijidanci.MainActivity")
            }
            // 专题学习
            JxwAppType.CHINESE_EXTENSION -> {
                startOtherApp(context, "com.jxw.special.video", "com.jxw.special.activity.MainActivity")
            }
            // 中英互译
            JxwAppType.TRANSLATE -> {
                startOtherApp(context, "com.tech.translate", "com.tech.translate.MainActivity")
            }
            // 指尖查词
            JxwAppType.ZHIJIAN_CAHCI -> {
                startOtherApp(context, "com.jxw.zncd", "com.jxw.zncd.MainActivity")
            }
        }
    }

    /**
     * 固化数据
     */
    fun startFixDataPage(context: Context, item: String) {
        try {
            val i = Intent()
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setClassName("com.jxw.online_study", "com.jxw.online_study.activity.XBookStudyActivity")
                .putExtra("StartArgs", "f:/ansystem/固化数据/${item}.JXW")
            context.startActivity(i)
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtil.showToast(context, "页面启动失败")
        }
    }

    /**
     * 同步辅导
     */
    fun startSyncAssistPage(context: Context, subject: String) {
        try {
            val i = Intent()
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setClassName("com.jxw.online_study", "com.jxw.online_study.activity.BookCaseWrapperActivity")
                .putExtra("StartArgs", "d:/同步学习/${subject}|e:JWFD")
            context.startActivity(i)
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtil.showToast(context, "页面启动失败")
        }
    }

    /**
     * 同步点读
     */
    fun startBookFingerRead(context: Context, subject: String) {
        try {
            val i = Intent()
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setClassName("com.jxw.online_study", "com.jxw.online_study.activity.BookCaseWrapperActivity")
                .putExtra("StartArgs", "d:/同步学习/${subject}|e:JWLB")   	//String:必传：跳转Flag
            context.startActivity(i)
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtil.showToast(context, "页面启动失败")
        }
    }

    /**
     * 名师课堂
     */
    fun startFamousTeacherClassroom(context: Context, subject: String) {
        try {
            val i = Intent()
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                .setClassName("com.jxw.mskt.video", "com.jxw.mskt.filelist.activity.FileListActivity")
                .setClassName("com.jxw.mskt.video", "com.jxw.mskt.video.MainActivity")
                .putExtra("StartArgs","d: ${prefs.grade}|e: $subject")
            context.startActivity(i)
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtil.showToast(context, "页面启动失败")
        }
    }

    /**
     * 指尖点读
     * subject: keben - K12指读, huiben - 绘本
     */
    fun startFingerRead(context: Context, subject: String) {
        try {
            val i = Intent()
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setClassName("com.jxw.huiben", "com.jxw.huiben.activity.SplashActivity")
                .putExtra("StartArgs", subject)
            context.startActivity(i)
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtil.showToast(context, "指尖点读启动失败")
        }
    }

    private fun startOtherApp(context: Context, packageName: String, className: String) {
        try {
            val i = Intent()
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setClassName(packageName, className)
            context.startActivity(i)
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtil.showToast(context, "页面启动失败")
        }
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