package com.ubtrobot.smartprojector.ui.tuya

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.ActivityFamilyManagerBinding
import com.ubtrobot.smartprojector.replaceFragment
import com.ubtrobot.smartprojector.startPlainActivity
import timber.log.Timber
import kotlin.math.roundToInt

/**
 * 涂鸦 - 家庭管理页面
 */
class FamilyManagerActivity : AppCompatActivity(),
    FamilyDetailFragment.OnFragmentActionListener {

    private lateinit var binding: ActivityFamilyManagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFamilyManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.btnCreateHome.setOnClickListener {
//            startPlainActivity(CreateFamilyActivity::class.java)
//        }

        familyQuery()
    }

    private fun familyQuery() {
        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(object : ITuyaGetHomeListCallback {
            override fun onSuccess(homeBeans: MutableList<HomeBean>?) {
                Timber.d("家庭数量: ${homeBeans?.size}")
//                if (homeBeans?.isNotEmpty() == true) {
//                    val home = homeBeans.first()
////                    homeId = home.homeId
//                    Timber.d("家庭：${home.homeId}, ${home.name}, ${home.deviceList.size}")
//                }

                binding.containerFamilyList.removeAllViews()
                homeBeans?.forEachIndexed { index, home ->
                    val v = createFamilyView(index, home.name, home.homeId)
                    addMasterItemView(v)
                }
                addCreateFamilyView()
            }

            override fun onError(errorCode: String?, error: String?) {
                Timber.d("家庭查询失败: $errorCode, $error")
            }
        })
    }

    private fun addCreateFamilyView() {
        val v = TextView(this)
        v.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen._24sp))
        v.gravity = Gravity.CENTER
        v.text = "创建家庭"
        v.setBackgroundColor(resources.getColor(android.R.color.holo_blue_light))
        addMasterItemView(v)
        v.setOnClickListener {
            startPlainActivity(CreateFamilyActivity::class.java)
        }
    }

    private fun addMasterItemView(v: View) {
        binding.containerFamilyList.addView(v)
        val lp = v.layoutParams as LinearLayout.LayoutParams
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT
        lp.height = resources.getDimension(R.dimen._100dp).roundToInt()
        lp.topMargin = resources.getDimension(R.dimen._20dp).roundToInt()
    }

    private fun createFamilyView(index: Int, homeName: String, homeId: Long) : View {
        val v = TextView(this)
        v.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen._24sp))
        v.gravity = Gravity.CENTER
        v.text = homeName
        v.setBackgroundColor(resources.getColor(android.R.color.holo_blue_light))
        v.setOnClickListener {
            replaceFragment(FamilyDetailFragment.newInstance(homeId), R.id.detail_fragment_container)
        }
        if (index == 0) replaceFragment(FamilyDetailFragment.newInstance(homeId), R.id.detail_fragment_container)
        return v
    }

    override fun onDeleteHome() {
        familyQuery()
    }
}