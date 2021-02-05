package com.ubtrobot.smartprojector.ui.tuya

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback
import com.tuya.smart.sdk.api.IDevListener
import com.tuya.smart.sdk.api.ISubDevListener
import com.tuya.smart.sdk.api.ITuyaDataCallback
import com.tuya.smart.sdk.bean.DeviceBean
import com.ubtrobot.smartprojector.databinding.FragmentTuyaHomeBinding
import com.ubtrobot.smartprojector.ui.login.LoginActivity
import com.ubtrobot.smartprojector.utils.ResourceUtil
import com.ubtrobot.smartprojector.utils.ToastUtil
import com.ubtrobot.smartprojector.utils.TuyaUtil
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

        binding.rcDeviceList.layoutManager = LinearLayoutManager(requireContext())
        binding.rcCmdList.layoutManager = LinearLayoutManager(requireContext())
        cmdAdapter = TuyaDeviceCmdAdapter(requireContext(), emptyList())
        binding.rcCmdList.adapter = cmdAdapter
        adapter = TuyaDeviceAdapter(emptyList()) { item ->
            cmdAdapter.updateData(item.dps)
            binding.tvProdName.text = "产品id：${item.name}"

            getGatewaySubDevices(item.id)

        }
        binding.rcDeviceList.adapter = adapter
        homeQuery()

        binding.btnAddNewDevice.setOnClickListener {
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
                    Timber.d("设备：${d.dpName}, ${d.devId}, gwType: ${d.gwType}, pid: ${d.productId}, " +
                            "category: ${d.category}, ${d.categoryCode}, isOnline: ${d.isOnline}")

                    val cmds = ArrayList<TuyaDeviceCmd>()
                    d.dps.forEach { p ->
                        cmds.add(TuyaDeviceCmd(d.devId, p.key, p.value))
                        Timber.d("功能点: ${p.key}, ${p.value}")
                    }
                    if (d.isZigBeeSubDev) {
                        // 注册zigbee子设备监听, 需要放到全局监听
//                        TuyaUtil.registerTuyaDeviceListener(requireContext(), d.devId)
                    }
                    if (d.isZigBeeWifi) {
                        val gwDev = TuyaHomeSdk.newGatewayInstance(d.devId)
                        gwDev.registerSubDevListener(object : ISubDevListener {
                            override fun onSubDevDpUpdate(cid: String?, dpStr: String?) {
                                Timber.d("onSubDevDpUpdate: $cid, $dpStr")
                            }

                            override fun onSubDevRemoved(devId: String?) {
                                Timber.d("onSubDevRemoved: $devId")
                            }

                            override fun onSubDevAdded(devId: String?) {
                                Timber.d("onSubDevAdded: $devId")

                            }

                            override fun onSubDevInfoUpdate(devId: String?) {
                                Timber.d("onSubDevInfoUpdate: $devId")

                            }

                            override fun onSubDevStatusChanged(onlines: MutableList<String>?, offlines: MutableList<String>?) {
                                Timber.d("onSubDevStatusChanged: ${onlines?.size}")

                            }
                        })
                    }
                    items.add(TuyaDevice(d.productId, d.devId, d.isOnline, d.isZigBeeWifi, d.categoryCode, cmds))
                }
                adapter.updateData(items)
                if (items.isNotEmpty()) {
                    val firstItem = items.first()
                    cmdAdapter.updateData(firstItem.dps)
                    binding.tvProdName.text = "产品id：${firstItem.name}"
                    getGatewaySubDevices(firstItem.id)
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

    companion object {

        fun newInstance() = TuyaHomeFragment()
    }
}