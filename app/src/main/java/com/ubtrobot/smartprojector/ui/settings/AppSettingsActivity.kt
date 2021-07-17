package com.ubtrobot.smartprojector.ui.settings

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.compose.ui.graphics.Color
import androidx.core.view.children
import androidx.viewpager2.widget.ViewPager2
import com.ubtrobot.smartprojector.GlideApp
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.ActivityAppSettingsBinding
import com.ubtrobot.smartprojector.utils.SystemUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppSettingsBinding

    private val tabTitles = listOf("系统切换", "蝌蚪助手", "版本升级", "关于蝌蚪")
    private var currentSelectedTab = 0

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        SystemUtil.toFullScreenMode(this)
        super.onCreate(savedInstanceState)
        binding = ActivityAppSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SystemUtil.statusBarTransparent(window)

        GlideApp.with(this)
            .load(R.mipmap.img_profile_bg)
            .centerCrop()
            .into(binding.ivProfileBg)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        tabTitles.forEachIndexed { index, title ->
            val tab = TextView(this)
            tab.text = title
            tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen._40sp))
            tab.gravity = Gravity.CENTER
            tab.setTextColor(resources.getColor(R.color.white))
            if (index == 0) {
                tab.typeface = Typeface.DEFAULT_BOLD
                tab.background = getDrawable(R.drawable.shape_profile_tab_bg)
            }
            binding.containerTab.addView(tab)
            val lp = tab.layoutParams as LinearLayout.LayoutParams
            lp.width = resources.getDimension(R.dimen._240dp).toInt()
            lp.height = LinearLayout.LayoutParams.MATCH_PARENT
            tab.setOnClickListener { v ->
                currentSelectedTab = index
                binding.containerTab.children.forEach { child ->
                    child.background = null
                    (child as TextView).typeface = Typeface.DEFAULT
                }
                v.background = getDrawable(R.drawable.shape_profile_tab_bg)
                (v as TextView).typeface = Typeface.DEFAULT_BOLD

                binding.viewPager.setCurrentItem(currentSelectedTab, false)
            }
        }

        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.adapter = AppSettingsContentAdapter(this, viewModel, tabTitles)

    }


}