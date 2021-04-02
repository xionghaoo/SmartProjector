package com.ubtrobot.smartprojector.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.ubtrobot.smartprojector.BuildConfig
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.ActivityChineseDetailBinding
import com.ubtrobot.smartprojector.utils.GetLearnAppManager
import com.ubtrobot.smartprojector.utils.SystemUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChineseDetailActivity : AppCompatActivity() {

    companion object {

        private const val EXTRA_TYPE = "${BuildConfig.APPLICATION_ID}.ChineseDetailActivity.EXTRA_TYPE"

        const val TYPE_CLASSROOM = 0
        const val TYPE_INTEREST = 1

        fun start(context: Context?, type: Int) {
            val i = Intent(context, ChineseDetailActivity::class.java)
            i.putExtra(EXTRA_TYPE, type)
            context?.startActivity(i)
        }
    }

    private lateinit var binding: ActivityChineseDetailBinding

    @Inject
    lateinit var getLearnAppManager: GetLearnAppManager

    override fun onCreate(savedInstanceState: Bundle?) {
        SystemUtil.toFullScreenMode(this)
        super.onCreate(savedInstanceState)
        binding = ActivityChineseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
        val type = intent.getIntExtra(EXTRA_TYPE, TYPE_CLASSROOM)

        binding.tvTitle.text = when(type) {
            TYPE_CLASSROOM -> "同步课堂"
            TYPE_INTEREST -> "趣味专题"
            else -> "同步课堂"
        }

        when(type) {
            TYPE_CLASSROOM -> {
                binding.tvTitle.text = "同步课堂"
                binding.containerChineseClassroom.visibility = View.VISIBLE

                binding.cardChineseClassroomTeacher.setSelectListener {
                    getLearnAppManager.startChinesePage(this, "名师课堂")
                }
                binding.cardChineseClassroomAssist.setSelectListener {
                    getLearnAppManager.startChinesePage(this, "同步教辅")
                }
                binding.cardChineseClassroomKonwledgePoint.setSelectListener {
                    getLearnAppManager.startChinesePage(this, "知识点巩固")
                }
                binding.cardChineseClassroomPractise.setSelectListener {
                    getLearnAppManager.startChinesePage(this, "同步练习")
                }
            }
            TYPE_INTEREST -> {
                binding.tvTitle.text = "趣味专题"
                binding.containerChineseInterest.visibility = View.VISIBLE

                binding.cardChineseIntersetClassroom.setSelectListener {
                    getLearnAppManager.startChinesePage(this, "国学课堂")
                }
                binding.cardChineseIntersetIdiom.setSelectListener {
                    getLearnAppManager.startChinesePage(this, "成语典故")
                }
            }
        }

        Glide.with(this)
            .load(R.raw.ic_chinese_detail_bg)
            .centerCrop()
            .into(binding.ivBackground)
    }

}