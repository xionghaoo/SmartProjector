package com.ubtrobot.smartprojector.ui.tuya

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback
import com.tuya.smart.sdk.api.IResultCallback
import com.tuya.smart.utils.ToastUtil
import com.ubtrobot.smartprojector.databinding.FragmentFamilyDetailBinding
import timber.log.Timber


/**
 * 涂鸦 - 家庭详情
 */
class FamilyDetailFragment : Fragment() {
    private var homeId: Long? = null
    private var _binding: FragmentFamilyDetailBinding? = null
    private val binding get() = _binding!!
    private var listener: OnFragmentActionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentActionListener) {
            listener = context
        }
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            homeId = it.getLong(ARG_HOME_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFamilyDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getHomeDetail(homeId)

        binding.btnDeleteHome.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("确定解散家庭？")
                .setPositiveButton("是") { _, _ ->
                    deleteHome(homeId)
                }
                .setNegativeButton("否", null)
                .show()
        }

    }

    private fun getHomeDetail(homeId: Long?) {
        if (homeId == null) return
        TuyaHomeSdk.newHomeInstance(homeId).getHomeLocalCache(object : ITuyaHomeResultCallback {
            override fun onSuccess(bean: HomeBean) {
                activity?.runOnUiThread {
                    binding.tvFamilyDetailName.text = bean.name
                    binding.tvFamilyClassroom.text = "当前房间数：${bean.rooms.size}"
                    binding.tvFamilyDevices.text = "当前设备数：${bean.deviceList.size}"
                }
            }

            override fun onError(errorCode: String, errorMsg: String) {
                Timber.d("onError: $errorCode, $errorMsg")
            }
        })
    }

    private fun deleteHome(homeId: Long?) {
        if (homeId == null) return
        TuyaHomeSdk.newHomeInstance(homeId).dismissHome(object : IResultCallback {
            override fun onError(code: String?, error: String?) {
                ToastUtil.shortToast(requireContext(), "解散家庭失败：$error")
            }

            override fun onSuccess() {
                ToastUtil.shortToast(requireContext(), "解散家庭成功")
                listener?.onDeleteHome()
            }
        })
    }

    interface OnFragmentActionListener {
        fun onDeleteHome()
    }

    companion object {
        private const val ARG_HOME_ID = "ARG_HOME_ID"

        fun newInstance(homeId: Long) =
            FamilyDetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_HOME_ID, homeId)
                }
            }
    }
}