package com.ubtrobot.smartprojector.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.ubtrobot.smartprojector.GlideApp
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.ActivityEnglishClassroomBinding
import com.ubtrobot.smartprojector.utils.SystemUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EnglishClassroomActivity : AppCompatActivity() {

//    @Inject
//    lateinit var getLearnAppManager: GetLearnAppManager
    private lateinit var binding: ActivityEnglishClassroomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        SystemUtil.toFullScreenMode(this)
        super.onCreate(savedInstanceState)
        binding = ActivityEnglishClassroomBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.toolbar.setTitle("同步课堂")
//            .configBackButton(this)
//
//        GlideApp.with(this)
//                .load(R.mipmap.ic_classroom_teacher)
//                .into(binding.ivClassroomTeacher)
//
//        GlideApp.with(this)
//                .load(R.mipmap.ic_classroom_knowledge)
//                .into(binding.ivClassroomKnowledge)
//
//        GlideApp.with(this)
//                .load(R.mipmap.ic_classroom_assistant)
//                .into(binding.ivClassroomAssistant)
//
//        GlideApp.with(this)
//                .load(R.mipmap.ic_classroom_practise)
//                .into(binding.ivClassroomPractise)
//
//        binding.cardEnglishClassroomTeacher.setSelectListener {
//            getLearnAppManager.startEnglishPage(this, "名师课堂")
//        }
//        binding.cardEnglishClassroomAssist.setSelectListener {
//            getLearnAppManager.startEnglishPage(this, "同步教辅")
//        }
//        binding.cardEnglishClassroomKnowledgePoint.setSelectListener {
//            getLearnAppManager.startEnglishPage(this, "知识点巩固")
//        }
//        binding.cardEnglishClassroomPractise.setSelectListener {
//            getLearnAppManager.startEnglishPage(this, "同步练习")
//        }
//
//        Glide.with(this)
//            .load(R.raw.ic_chinese_detail_bg)
//            .centerCrop()
//            .into(binding.ivBackground)
    }
}