package com.ubtrobot.smartprojector.test

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ubtrobot.smartprojector.GlideApp
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.ActivityDemoImageBinding
import com.ubtrobot.smartprojector.utils.SystemUtil

class DemoImageActivity : AppCompatActivity() {

    companion object {
        const val KNOWLEDGE_MY = 0
        const val KNOWLEDGE_MATH = 1
        const val KNOWLEDGE_MATH_WRONG_BOOK = 2
        const val KNOWLEDGE_MATH_PAPER_CENTER = 3
        const val KNOWLEDGE_MATH_TEST = 4
        const val KNOWLEDGE_MATH_TEACH_SYNC = 5
        const val KNOWLEDGE_MATH_TEST_UNIT_1 = 6
        const val KNOWLEDGE_MATH_TEACH_SYNC_POINTS = 7

        private const val EXTRA_DATA_KNOWLEDGE_TYPE = "com.ubtrobot.smartprojector.DemoImageActivity.EXTRA_DATA_KNOWLEDGE_TYPE"

        fun start(context: Context?, type: Int) {
            val intent = Intent(context, DemoImageActivity::class.java)
            intent.putExtra(EXTRA_DATA_KNOWLEDGE_TYPE, type)
            context?.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityDemoImageBinding
    private var type: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        SystemUtil.toFullScreenMode(this)
        super.onCreate(savedInstanceState)
        binding = ActivityDemoImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SystemUtil.statusBarTransparent(window)

        type = intent.getIntExtra(EXTRA_DATA_KNOWLEDGE_TYPE, -1)

        val image: Int? = when(type) {
            KNOWLEDGE_MY -> R.raw.img_konwledge_my
            KNOWLEDGE_MATH -> R.raw.img_knowledge_math
            KNOWLEDGE_MATH_WRONG_BOOK -> R.raw.img_knowledge_math_wrong_book
            KNOWLEDGE_MATH_PAPER_CENTER -> R.raw.img_knowledge_math_paper_center
            KNOWLEDGE_MATH_TEST -> R.raw.img_knowledge_math_test
            KNOWLEDGE_MATH_TEACH_SYNC -> R.raw.img_knowledge_math_teach_sync
            KNOWLEDGE_MATH_TEST_UNIT_1 -> R.raw.img_konwledge_math_test_unit1
            KNOWLEDGE_MATH_TEACH_SYNC_POINTS -> R.raw.img_knowledge_math_teach_sync_points
            else -> null
        }

        GlideApp.with(this)
            .load(image)
            .into(binding.demoIvContent)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        when (type) {
            KNOWLEDGE_MATH -> {
                binding.demoBtnMathWrongBook.visibility = View.VISIBLE
                binding.demoBtnMathWrongBook.setOnClickListener {
                    start(this, KNOWLEDGE_MATH_WRONG_BOOK)
                }
                binding.demoBtnMathPaperCenter.visibility = View.VISIBLE
                binding.demoBtnMathPaperCenter.setOnClickListener {
                    start(this, KNOWLEDGE_MATH_PAPER_CENTER)
                }
                binding.demoBtnMathTest.visibility = View.VISIBLE
                binding.demoBtnMathTest.setOnClickListener {
                    start(this, KNOWLEDGE_MATH_TEST)
                }
                binding.demoBtnMathTeachSync.visibility = View.VISIBLE
                binding.demoBtnMathTeachSync.setOnClickListener {
                    start(this, KNOWLEDGE_MATH_TEACH_SYNC)
                }
            }
            KNOWLEDGE_MATH_TEACH_SYNC -> {
                binding.demoBtnTeachSyncPoints.visibility = View.VISIBLE
                binding.demoBtnTeachSyncPoints.setOnClickListener {
                    start(this, KNOWLEDGE_MATH_TEACH_SYNC_POINTS)
                }
                binding.demoBtnTeachSyncPoints1.visibility = View.VISIBLE
                binding.demoBtnTeachSyncPoints1.setOnClickListener {
                    start(this, KNOWLEDGE_MATH_TEACH_SYNC_POINTS)
                }
                binding.demoBtnTeachSyncPoints2.visibility = View.VISIBLE
                binding.demoBtnTeachSyncPoints2.setOnClickListener {
                    start(this, KNOWLEDGE_MATH_TEACH_SYNC_POINTS)
                }
                binding.demoBtnTeachSyncPoints3.visibility = View.VISIBLE
                binding.demoBtnTeachSyncPoints3.setOnClickListener {
                    start(this, KNOWLEDGE_MATH_TEACH_SYNC_POINTS)
                }
            }
            KNOWLEDGE_MATH_TEST -> {
                binding.demoBtnTestUnit1.visibility = View.VISIBLE
                binding.demoBtnTestUnit1.setOnClickListener {
                    start(this, KNOWLEDGE_MATH_TEST_UNIT_1)
                }
            }
        }

    }
}