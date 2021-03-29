package com.ubtrobot.smartprojector.ui.tuya

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback
import com.tuya.smart.utils.ToastUtil
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.ActivityCreateFamilyBinding
import timber.log.Timber

/**
 * 涂鸦 - 创建家庭
 */
class CreateFamilyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateFamilyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateFamilyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSaveFamily.setOnClickListener {
            val familyName = binding.edtFamilyName.text.toString()
            if (familyName.isNotBlank()) {
                createFamily(familyName, listOf("classroom1"))
            } else {
                ToastUtil.shortToast(this, "家庭名称不能为空")
            }
        }
    }

    private fun createFamily(familyName: String, classroomList: List<String>) {
        TuyaHomeSdk.getHomeManagerInstance().createHome(
            familyName,
            0.0,
            0.0,
            null,
            classroomList,
            object : ITuyaHomeResultCallback {
                override fun onSuccess(bean: HomeBean?) {
//                    homeId = bean?.homeId
                    Timber.d("创建家庭成功， ${bean?.homeId}")
                }

                override fun onError(errorCode: String?, errorMsg: String?) {
                    Timber.d("创建家庭失败: $errorCode, $errorMsg")
                }
            }
        )
    }
}