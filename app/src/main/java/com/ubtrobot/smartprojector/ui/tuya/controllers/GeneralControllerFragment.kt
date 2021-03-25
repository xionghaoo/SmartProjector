package com.ubtrobot.smartprojector.ui.tuya.controllers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.builder.TuyaGwSubDevActivatorBuilder
import com.tuya.smart.sdk.api.ITuyaDataCallback
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener
import com.tuya.smart.sdk.bean.DeviceBean
import com.ubtrobot.smartprojector.GlideApp
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.FragmentGeneralControllerBinding
import com.ubtrobot.smartprojector.ui.tuya.TuyaDevice
import com.ubtrobot.smartprojector.ui.tuya.TuyaDeviceCmdAdapter
import com.ubtrobot.smartprojector.utils.PromptDialog
import com.ubtrobot.smartprojector.utils.ResourceUtil
import com.ubtrobot.smartprojector.utils.ToastUtil
import timber.log.Timber
import kotlin.math.roundToInt

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

            binding.btnFindSubDevice.visibility = if (it.isZigBeeWifi) View.VISIBLE else View.GONE
            binding.btnFindSubDevice.setOnClickListener {
                ToastUtil.showToast(requireContext(), "寻找子设备")
                tuyaSubDeviceConfig(device?.id!!)
            }
        }
    }

    private fun getGatewaySubDevices(gwId: String) {
        val gwDevice = TuyaHomeSdk.newGatewayInstance(gwId)
        gwDevice.getSubDevList(object : ITuyaDataCallback<List<DeviceBean>> {
            override fun onSuccess(result: List<DeviceBean>?) {
                binding.containerSubDevList.removeAllViews()
                result?.forEach { d ->
                    val tv = TextView(requireContext())
                    tv.text = "子设备：${d.name}"
                    val padding = resources.getDimension(R.dimen._15dp).roundToInt()
                    tv.setPadding(padding, padding, padding, padding)
                    binding.containerSubDevList.addView(tv)
                }
            }

            override fun onError(errorCode: String?, errorMessage: String?) {
                ToastUtil.showToast(requireContext(), "查询子设备失败")
            }
        })
    }

    // 发现子设备
    private fun tuyaSubDeviceConfig(gwDeviceId: String) {
        binding.tvFindSubDevice.text = "正在寻找子设备"
        val builder = TuyaGwSubDevActivatorBuilder()
                .setDevId(gwDeviceId)
                .setTimeOut(100)
                .setListener(object : ITuyaSmartActivatorListener {
                    override fun onError(errorCode: String?, errorMsg: String?) {
                        Timber.d("tuyaSubDeviceConfig error：$errorCode, $errorMsg")
                        binding.tvFindSubDevice.text = "发生错误：${errorCode}, $errorMsg"
                    }

                    override fun onActiveSuccess(devResp: DeviceBean?) {
                        // 子设备发现回调
                        Timber.d("tuyaSubDeviceConfig onActiveSuccess 发现设备：${devResp?.dpName}, ${devResp?.devId}")
                        binding.tvFindSubDevice.text = "已注册新设备"
                        showDeviceRegisterSuccess(devResp?.iconUrl, devResp?.getName())
                    }

                    override fun onStep(step: String?, data: Any?) {
                        // 网关设备发现回调
//                        Timber.d("tuyaSubDeviceConfig onStep: $step, ${data}")
//                        if (step == "device_find") {
//                            // 发现设备 6c94af80222594a3dfv4r3
//                            val deviceId = data as? String
//                            Timber.d("发现设备： $deviceId")
//                        } else if (step == "device_bind_success") {
//                            val dev = data as? DeviceBean
//                            dev?.apply {
//                                Timber.d("激活设备成功: $dpName, $devId")
//                            }
//                        }
                    }
                })

        val activator = TuyaHomeSdk.getActivatorInstance().newGwSubDevActivator(builder)
        activator.start()
    }

    private fun showDeviceRegisterSuccess(iconUrl: String?, deviceName: String?) {
        PromptDialog.Builder(requireContext())
                .setView(R.layout.dialog_device_register_success)
                .configView { v ->
                    GlideApp.with(requireContext())
                            .load(iconUrl)
                            .into(v.findViewById<ImageView>(R.id.iv_tuya_device_icon))
                    v.findViewById<TextView>(R.id.tv_tuya_device_name).text = deviceName
                }
                .build()
                .show()
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