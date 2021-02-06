package com.ubtrobot.smartprojector.ui.tuya.controllers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.sdk.api.ITuyaDataCallback
import com.tuya.smart.sdk.bean.DeviceBean
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.FragmentGeneralControllerBinding
import com.ubtrobot.smartprojector.ui.tuya.TuyaDevice
import com.ubtrobot.smartprojector.ui.tuya.TuyaDeviceCmdAdapter
import com.ubtrobot.smartprojector.utils.ResourceUtil
import com.ubtrobot.smartprojector.utils.ToastUtil

class GeneralControllerFragment : Fragment() {
    private var device: TuyaDevice? = null
    private lateinit var cmdAdapter: TuyaDeviceCmdAdapter
    private var _binding: FragmentGeneralControllerBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            device = it.getParcelable(ARG_TUYA_DEVICE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGeneralControllerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rcCmdList.layoutManager = LinearLayoutManager(requireContext())
        cmdAdapter = TuyaDeviceCmdAdapter(requireContext(), emptyList())
        binding.rcCmdList.adapter = cmdAdapter
        device?.also {
            cmdAdapter.updateData(it.dps)
            getGatewaySubDevices(it.id)
        }
    }

    private fun getGatewaySubDevices(gwId: String) {
        val gwDevice = TuyaHomeSdk.newGatewayInstance(gwId)
        gwDevice.getSubDevList(object : ITuyaDataCallback<List<DeviceBean>> {
            override fun onSuccess(result: List<DeviceBean>?) {
                binding.containerSubDevList.removeAllViews()
                result?.forEach { d ->
                    val tv = TextView(requireContext())
                    tv.text = "子设备：${d.devId}"
                    val padding = ResourceUtil.convertDpToPixel(15f, requireContext()).toInt()
                    tv.setPadding(padding, padding, padding, padding)
                    binding.containerSubDevList.addView(tv)
                }
            }

            override fun onError(errorCode: String?, errorMessage: String?) {
                ToastUtil.showToast(requireContext(), "查询子设备失败")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TUYA_DEVICE = "ARG_TUYA_DEVICE"

        fun newInstance(dev: TuyaDevice) =
            GeneralControllerFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TUYA_DEVICE, dev)
                }
            }
    }
}