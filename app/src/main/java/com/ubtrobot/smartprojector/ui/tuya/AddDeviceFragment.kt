package com.ubtrobot.smartprojector.ui.tuya

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.tuya.smart.android.common.utils.NetworkUtil
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.builder.ActivatorBuilder
import com.tuya.smart.sdk.api.ITuyaActivator
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener
import com.tuya.smart.sdk.bean.DeviceBean
import com.tuya.smart.sdk.enums.ActivatorModelEnum
import com.ubtrobot.smartprojector.GlideApp
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.FragmentAddDeviceBinding
import com.ubtrobot.smartprojector.repo.Repository
import com.ubtrobot.smartprojector.utils.*
import com.ubtrobot.smartprojector.widgets.RadarView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AddDeviceFragment : Fragment() {

    @Inject
    lateinit var repo: Repository

    private var activator: ITuyaActivator? = null
    private var homeId: Long? = null
    private var wifiSSID: String? = null

    private var _binding: FragmentAddDeviceBinding? = null
    private val binding get() = _binding!!

    private var isStartSearch = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.apply {
            homeId = getLong(EXTRA_HOME_ID)
        }
    }

    override fun onDetach() {
        activator?.stop()
        super.onDetach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddDeviceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStartConnect.setOnClickListener {
            if (!SystemUtil.isNetworkConnected(requireContext())) {
                ToastUtil.showToast(requireContext(), "网络连接已断开")
                return@setOnClickListener
            }
            if (repo.prefs.wifiPwd == null) {
                ToastUtil.showToast(requireContext(), "请先配置Wifi")
            } else {
                if (isStartSearch) {
                    isStartSearch = false
                    binding.btnStartConnect.text = "搜索设备"
                    toggleRadarView(false)
                    activator?.stop()
                    binding.tvDeviceConnectStatus.text = ""
                } else {
                    getToken(homeId)
                    isStartSearch = true
                    binding.btnStartConnect.text = "停止搜索"
                }
            }
        }

        // 获取wifi名称
        wifiSSID = WifiUtil.getWifiSSID(requireContext())
        if (wifiSSID == null) {
            binding.tvConnectedWifiName.text = "wifi未连接，点我去设置"
            binding.tvConnectedWifiName.setOnClickListener {
                SystemUtil.openSettingsWifi(requireContext())
            }
        } else {
            binding.tvConnectedWifiName.text = wifiSSID
        }
        binding.edtConnectedWifiPassword.setText(repo.prefs.wifiPwd)
        binding.btnSetWifi.setOnClickListener {
            val pwd = binding.edtConnectedWifiPassword.text.toString()
            if (pwd.isNotEmpty()) {
                repo.prefs.wifiPwd = pwd
                ToastUtil.showToast(requireContext(), "已设置wifi")
            } else {
                ToastUtil.showToast(requireContext(), "wifi密码不能为空")
            }
        }

//        view.findViewById<View>(R.id.btn_set_wifi_pwd).setOnClickListener {
//            PromptDialog.Builder(requireContext())
//                .setView(R.layout.dialog_config_home_wifi)
//                .configView { v ->
//                    v.findViewById<TextView>(R.id.tv_wifi_ssid).text = wifiSSID
//                    v.findViewById<EditText>(R.id.edt_wifi_pwd).setText(repo.prefs.wifiPwd)
//                }
//                .addOperation(OperationType.CONFIRM, R.id.btn_confirm, true) { v ->
//                    val pwd = v.findViewById<EditText>(R.id.edt_wifi_pwd).text.toString()
//                    repo.prefs.wifiPwd = pwd
//                }
//                .build()
//                .show()
//        }

//        view.findViewById<TextView>(R.id.tv_new_device_home_info).text = "家庭：${homeId}"
    }

    // 配网token
    private fun getToken(homeId: Long?) {
        Timber.d("get home id: $homeId")
        if (homeId == null) return
        toggleRadarView(true)
        TuyaHomeSdk.getActivatorInstance().getActivatorToken(homeId, object :
            ITuyaActivatorGetToken {
            override fun onSuccess(token: String?) {
                Timber.d("get token success: $token")
//                wiredNetConfig(token)
                findGWDevice(token)
            }

            override fun onFailure(errorCode: String?, errorMsg: String?) {
                Timber.d("get token failre: $errorMsg")
            }
        })
    }

    private fun findGWDevice(token: String?) {
        Timber.d("SSID: ${wifiSSID}, wifiPWD: ${repo.prefs.wifiPwd}")
        binding.tvDeviceConnectStatus.text = "正在寻找设备..."
        val builder = ActivatorBuilder()
            .setSsid(wifiSSID)
            .setContext(activity)
            .setPassword(repo.prefs.wifiPwd)
            .setActivatorModel(ActivatorModelEnum.TY_EZ)
            .setTimeOut(100)
            .setToken(token)
            .setListener(object : ITuyaSmartActivatorListener {
                override fun onError(errorCode: String?, errorMsg: String?) {
                    Timber.d("findGWDevice error：$errorCode, $errorMsg")
                    binding.tvDeviceConnectStatus.text = "发生错误: $errorCode, $errorMsg"
                    toggleRadarView(false)
                }

                override fun onActiveSuccess(devResp: DeviceBean?) {
                    // 网关子设备发现回调
                    Timber.d("发现设备：${devResp?.dpName}")
                    toggleRadarView(false)
                }

                override fun onStep(step: String?, data: Any?) {
                    toggleRadarView(false)
                    // 网关设备发现回调
                    if (step == "device_find") {
                        // 发现设备 6c94af80222594a3dfv4r3
                        val deviceId = data as? String
                        binding.tvDeviceConnectStatus.text = "已发现设备: $deviceId, 正在向云端注册..."
                    } else if (step == "device_bind_success") {
                        binding.tvDeviceConnectStatus.text = "设备注册成功"
                        val dev = data as? DeviceBean
                        showDeviceRegisterSuccess(dev?.iconUrl, dev?.getName())
                    }
                }
            })

        activator = TuyaHomeSdk.getActivatorInstance().newMultiActivator(builder)
        activator?.start()
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

    fun toggleRadarView(enable: Boolean) {
        if (enable) {
            binding.radarView.visibility = View.VISIBLE
            binding.radarView.start()
        } else {
            binding.radarView.stop()
            binding.radarView.visibility = View.GONE
        }
    }

    companion object {
        private const val EXTRA_HOME_ID = "EXTRA_HOME_ID"

        fun newInstance(homeId: Long) = AddDeviceFragment().apply {
            arguments = Bundle().apply {
                putLong(EXTRA_HOME_ID, homeId)
            }
        }
    }
}