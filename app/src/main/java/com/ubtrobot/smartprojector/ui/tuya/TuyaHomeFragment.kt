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
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.FragmentTuyaHomeBinding
import com.ubtrobot.smartprojector.repo.Repository
import com.ubtrobot.smartprojector.ui.login.LoginActivity
import com.ubtrobot.smartprojector.ui.tuya.controllers.GeneralControllerFragment
import com.ubtrobot.smartprojector.ui.tuya.controllers.WifiLampControllerFragment
import com.ubtrobot.smartprojector.utils.ResourceUtil
import com.ubtrobot.smartprojector.utils.ToastUtil
import com.ubtrobot.smartprojector.utils.TuyaUtil
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TuyaHomeFragment : Fragment() {

    private lateinit var adapter: TuyaDeviceAdapter
//    private lateinit var cmdAdapter: TuyaDeviceCmdAdapter
    private var _binding: FragmentTuyaHomeBinding? = null
    private val binding get() = _binding!!
    private var homeId: Long? = null

    @Inject
    lateinit var repo: Repository

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
//        binding.rcCmdList.layoutManager = LinearLayoutManager(requireContext())
//        cmdAdapter = TuyaDeviceCmdAdapter(requireContext(), emptyList())
//        binding.rcCmdList.adapter = cmdAdapter
        adapter = TuyaDeviceAdapter(emptyList()) { index, item ->
//            cmdAdapter.updateData(item.dps)
//            binding.tvProdName.text = "产品id：${item.name}"

            showController(item)

        }
        binding.rcDeviceList.adapter = adapter
        binding.btnAddNewDevice.setOnClickListener {
            if (homeId != null) {
                NewDeviceActivity.start(requireContext(), homeId!!)
            }
        }

        binding.refreshLayout.setOnRefreshListener {
            homeDevicesQuery(repo.prefs.currentHomeId)
        }
        homeDevicesQuery(repo.prefs.currentHomeId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showController(item: TuyaDevice) {
        val controller = if (item.categoryCode == TuyaCategory.WIFI_ATMOSPHERE_LAMP) {
            WifiLampControllerFragment.newInstance(item)
        } else {
            GeneralControllerFragment.newInstance(item)
        }
        childFragmentManager.beginTransaction()
            .replace(R.id.sub_fragment_container, controller)
            .commit()
    }

//    /**
//     * 家庭查询
//     */
//    private fun homeQuery() {
//        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(object : ITuyaGetHomeListCallback {
//            override fun onSuccess(homeBeans: MutableList<HomeBean>?) {
//                Timber.d("家庭数量: ${homeBeans?.size}")
//                if (homeBeans?.isNotEmpty() == true) {
//                    val home = homeBeans.first()
//                    binding.tvHome.text = "家庭：${home.name}, ${home.homeId}"
//                    Timber.d("家庭：${home.homeId}, ${home.name}, ${home.deviceList.size}")
//                    homeDevicesQuery(home.homeId)
//                    homeId = home.homeId
//                } else {
//                    binding.refreshLayout.finishRefresh(true)
//                }
//            }
//
//            override fun onError(errorCode: String?, error: String?) {
//                binding.refreshLayout.finishRefresh(false)
//
//                Timber.d("家庭查询失败: $errorCode, $error")
//                if (errorCode == "USER_SESSION_LOSS") {
//                    ToastUtil.showToast(requireContext(), "登录已失效")
//                    LoginActivity.startWithNewTask(requireContext())
//                }
//            }
//        })
//    }

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
                        cmds.add(TuyaDeviceCmd(d.devId, p.key, p.value?.toString() ?: ""))
                        Timber.d("功能点: ${p.key}, ${p.value}")
                    }
                    if (d.categoryCode == "zig_sos") {
                        TuyaUtil.registerTuyaDeviceListener(requireContext(), d.devId)
                    }
                    if (d.isZigBeeWifi) {

                    }
                    items.add(TuyaDevice(d.productId, d.devId, d.isOnline, d.isZigBeeWifi, d.categoryCode, d.schema, cmds))
                }
                adapter.updateData(items)
                if (items.isNotEmpty()) {
                    val firstItem = items.first()
                    showController(firstItem)
//                    cmdAdapter.updateData(firstItem.dps)
//                    binding.tvProdName.text = "产品id：${firstItem.name}"
//                    getGatewaySubDevices(firstItem.id)
                }
                binding.refreshLayout.finishRefresh(true)
            }

            override fun onError(errorCode: String?, errorMsg: String?) {
                binding.refreshLayout.finishRefresh(false)
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