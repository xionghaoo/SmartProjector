package com.ubtrobot.smartprojector.ui.tuya

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback
import com.tuya.smart.sdk.api.IDevListener
import com.tuya.smart.sdk.api.ISubDevListener
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.FragmentTuyaHomeBinding
import com.ubtrobot.smartprojector.startPlainActivity
import com.ubtrobot.smartprojector.ui.login.LoginActivity
import com.ubtrobot.smartprojector.utils.ToastUtil
import kotlinx.android.synthetic.main.fragment_tuya_home.*
import timber.log.Timber


class TuyaHomeFragment : Fragment() {

    private lateinit var adapter: TuyaDeviceAdapter
    private lateinit var cmdAdapter: TuyaDeviceCmdAdapter
    private var _binding: FragmentTuyaHomeBinding? = null
    private val binding get() = _binding!!
    private var homeId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTuyaHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rc_device_list.layoutManager = LinearLayoutManager(requireContext())
        rc_cmd_list.layoutManager = LinearLayoutManager(requireContext())
        cmdAdapter = TuyaDeviceCmdAdapter(emptyList())
        rc_cmd_list.adapter = cmdAdapter
        adapter = TuyaDeviceAdapter(emptyList()) { item ->
            Timber.d("device cmd: ${item.dps.size}")
            cmdAdapter.updateData(item.dps)
        }
        rc_device_list.adapter = adapter
        homeQuery()

        btn_add_new_device.setOnClickListener {
            if (homeId != null) {
                NewDeviceActivity.start(requireContext(), homeId!!)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * 家庭查询
     */
    private fun homeQuery() {
        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(object : ITuyaGetHomeListCallback {
            override fun onSuccess(homeBeans: MutableList<HomeBean>?) {
                Timber.d("家庭数量: ${homeBeans?.size}")
                if (homeBeans?.isNotEmpty() == true) {
                    val home = homeBeans.first()
                    binding.tvHome.text = "家庭：${home.name}, ${home.homeId}"
                    Timber.d("家庭：${home.homeId}, ${home.name}, ${home.deviceList.size}")
                    homeDevicesQuery(home.homeId)
                    homeId = home.homeId
                }
            }

            override fun onError(errorCode: String?, error: String?) {
                Timber.d("家庭查询失败: $errorCode, $error")
                if (errorCode == "USER_SESSION_LOSS") {
                    ToastUtil.showToast(requireContext(), "登录已失效")
                    LoginActivity.startWithNewTask(requireContext())
                }
            }
        })
    }

    /**
     * 家庭设备查询
     */
    private fun homeDevicesQuery(homeId: Long) {
        TuyaHomeSdk.newHomeInstance(homeId).getHomeDetail(object : ITuyaHomeResultCallback {
            override fun onSuccess(bean: HomeBean?) {
                val deviceList = bean?.deviceList
                Timber.d("家庭设备查询成功： ${deviceList?.size}")
                val items = ArrayList<TuyaDevice>()
                deviceList?.forEach { d ->
                    // gwType: If device is virtual, the filed value is "v" , else is "s"
                    Timber.d("设备：${d.dpName}, ${d.devId}, gwType: ${d.gwType}, pid: ${d.productId}, isZigBeeWifi: ${d.isZigBeeWifi}, isOnline: ${d.isOnline}")
                    val cmds = ArrayList<TuyaDeviceCmd>()
                    d.dps.forEach { p ->
                        cmds.add(TuyaDeviceCmd(d.devId, p.key, p.value))
                        Timber.d("功能点: ${p.key}, ${p.value}")
                    }
//                    if (d.isZigBeeWifi) {
//                        gwDeviceId = d.devId
//                    } else {
//                        sosDeviceId = d.devId
//                    }
                    // 添加涂鸦设备
//                    if (d.isZigBeeWifi) {
////                        TuyaHomeSdk.newGatewayInstance(d.devId).registerSubDevListener(object : ISubDevListener {
////                            override fun onSubDevDpUpdate(cid: String?, dpStr: String?) {
////
////                            }
////
////                            override fun onSubDevRemoved(devId: String?) {
////
////                            }
////
////                            override fun onSubDevAdded(devId: String?) {
////
////                            }
////
////                            override fun onSubDevInfoUpdate(devId: String?) {
////
////                            }
////
////                            override fun onSubDevStatusChanged(
////                                onlines: MutableList<String>?,
////                                offlines: MutableList<String>?
////                            ) {
////
////                            }
////                        })
////                    }
                    if (d.isZigBeeSubDev) {
                        // 注册zigbee子设备监听
                        val device = TuyaHomeSdk.newDeviceInstance(d.devId)
                        device.registerDevListener(object : IDevListener {
                            override fun onDpUpdate(devId: String?, dpStr: String?) {
                                Timber.d("onDpUpdate: $devId, $dpStr")
                                ToastUtil.showToast(requireContext(), "收到SOS警报")
                            }

                            override fun onRemoved(devId: String?) {
                                Timber.d("onRemoved: $devId")
                            }

                            override fun onStatusChanged(devId: String?, online: Boolean) {
                                Timber.d("onStatusChanged: $devId")
                            }

                            override fun onNetworkStatusChanged(devId: String?, status: Boolean) {
                                Timber.d("onNetworkStatusChanged: $devId")
                            }

                            override fun onDevInfoUpdate(devId: String?) {
                                Timber.d("onDevInfoUpdate: $devId")
                            }
                        })
                    }
                    items.add(TuyaDevice(d.productId, d.devId, d.isOnline, d.isZigBeeWifi, cmds))
                }
                adapter.updateData(items)
                if (items.isNotEmpty()) {
                    cmdAdapter.updateData(items.first().dps)
                }
            }

            override fun onError(errorCode: String?, errorMsg: String?) {
                Timber.d("家庭设备查询失败: $errorCode, $errorMsg")
                if (errorCode == "USER_SESSION_LOSS") {
                    ToastUtil.showToast(requireContext(), "登录已失效")
                    LoginActivity.startWithNewTask(requireContext())
                }
            }
        })
    }

    companion object {

        fun newInstance() = TuyaHomeFragment()
    }
}