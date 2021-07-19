package com.ubtrobot.smartprojector.ui.profile

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ubtrobot.smartprojector.GlideApp
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.ActivityProfileBinding
import com.ubtrobot.smartprojector.utils.SystemUtil
import dagger.hilt.android.AndroidEntryPoint
import xh.zero.voice.TencentVoiceManager
import javax.inject.Inject


@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    companion object {
        private const val RC_SYSTEM_ALERT_WINDOW_PERMISSION = 5
    }

    private lateinit var binding: ActivityProfileBinding

    private val viewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var voiceManager: TencentVoiceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        SystemUtil.toFullScreenMode(this)
        super.onCreate(savedInstanceState)
        SystemUtil.statusBarTransparent(window)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GlideApp.with(this)
            .load(R.mipmap.img_profile_bg)
            .centerCrop()
            .into(binding.ivProfileBg)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

//        val sp = SpannableString("今天学习时长 6 小时")
//        val sizeSpan = AbsoluteSizeSpan(resources.getDimension(R.dimen._40sp).toInt(), true)
//        sp.setSpan(sizeSpan, 6, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        val boldSpan = StyleSpan(Typeface.BOLD_ITALIC) //加粗
//        sp.setSpan(boldSpan, 6, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        binding.tvProfileTodayLearnTime.text = sp

        setSpanText(binding.tvProfileTodayLearnTime, "今天学习时长 6 小时", 7, 8)
        setSpanText(binding.tvProfileTotalLearnTime, "学习总天数 12 天", 6, 9)


//        binding.toolbar.setTitle("个人中心")
//        binding.toolbar.configBackButton(this)
//
//        binding.btnSettings.setOnClickListener {
//            startPlainActivity(SettingsActivity::class.java)
//        }
//
//        binding.btnCallWithParent.setOnClickListener {
//            startPlainActivity(CallWithParentActivity::class.java)
//        }
//
//        binding.tvTestInfo.text = "屏幕信息：${SystemUtil.displayInfo(this)}"
//
//        binding.tvUserName.text = "用户名：台灯${viewModel.prefs().userID}"
//
//        binding.btnVoiceTest.setOnClickListener {
//            if (Settings.canDrawOverlays(this)) {
//                voiceManager.startRecognize()
//            } else {
//                try {
//                    startActivityForResult(
//                        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION), RC_SYSTEM_ALERT_WINDOW_PERMISSION
//                    )
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
//
//        binding.btnElementarySystem.setOnClickListener {
//            MainActivity.startWithSingleTop(this, MainActivity.SYSTEM_ELEMENTARY)
//        }
//        binding.btnInfantSystem.setOnClickListener {
//            MainActivity.startWithSingleTop(this, MainActivity.SYSTEM_INFANT)
//        }
    }

    private fun setSpanText(tv: TextView, txt: String, start: Int, end: Int) {
        val sp = SpannableString(txt)
        val sizeSpan = AbsoluteSizeSpan(resources.getDimension(R.dimen._40sp).toInt(), false)
        sp.setSpan(sizeSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val boldSpan = StyleSpan(Typeface.BOLD_ITALIC) //加粗
        sp.setSpan(boldSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv.text = sp
    }

}