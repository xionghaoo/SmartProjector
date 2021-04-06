package com.ubtrobot.smartprojector.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.ActivityMathematicsClassroomBinding
import com.ubtrobot.smartprojector.utils.GetLearnAppManager
import com.ubtrobot.smartprojector.utils.SystemUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MathematicsClassroomActivity : AppCompatActivity() {

    @Inject
    lateinit var getLearnAppManager: GetLearnAppManager
    private lateinit var binding: ActivityMathematicsClassroomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        SystemUtil.toFullScreenMode(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMathematicsClassroomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setTitle("同步课堂")
            .configBackButton(this)


        binding.cardMathematicsClassroomTeacher.setSelectListener {
            getLearnAppManager.startMathematicsPage(this, "名师课堂")
        }
        binding.cardMathematicsClassroomAssist.setSelectListener {
            getLearnAppManager.startMathematicsPage(this, "同步教辅")
        }
        binding.cardMathematicsClassroomKnowledgePoint.setSelectListener {
            getLearnAppManager.startMathematicsPage(this, "知识点巩固")
        }
        binding.cardMathematicsClassroomPractise.setSelectListener {
            getLearnAppManager.startMathematicsPage(this, "同步练习")
        }

        Glide.with(this)
            .load(R.raw.ic_chinese_detail_bg)
            .centerCrop()
            .into(binding.ivBackground)
    }
}