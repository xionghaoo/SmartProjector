package com.ubtrobot.smartprojector.ui.tuya

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.ActivityTuyaDeviceCategoryBinding
import com.ubtrobot.smartprojector.repo.Repository
import com.ubtrobot.smartprojector.utils.SystemUtil
import com.ubtrobot.smartprojector.widgets.TuyaDeviceView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * 设备总览
 */
@AndroidEntryPoint
class TuyaDeviceCategoryActivity : AppCompatActivity() {

    @Inject
    lateinit var repo: Repository
    private lateinit var binding: ActivityTuyaDeviceCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        SystemUtil.statusBarTransparent(window)
        super.onCreate(savedInstanceState)
        binding = ActivityTuyaDeviceCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createCategoryOne()

        createCategoryTwo()
    }

    private fun createCategoryOne() {
        binding.containerCategory1.removeAllViews()
        val itemView = TuyaDeviceView(this)
        itemView.setTitle("灯")
        itemView.setIcon(R.drawable.ic_production_lamp)
        itemView.setOnClickListener {
            NewDeviceActivity.start(this, repo.prefs.currentHomeId)
        }
        binding.containerCategory1.addView(itemView)
    }

    private fun createCategoryTwo() {
        binding.containerCategory2.removeAllViews()
        val itemView = TuyaDeviceView(this)
        itemView.setTitle("sos")
        itemView.setIcon(R.drawable.ic_production_sos)
        itemView.setOnClickListener {
            NewDeviceActivity.start(this, repo.prefs.currentHomeId)
        }
        binding.containerCategory2.addView(itemView)
    }
}