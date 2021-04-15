package com.ubtrobot.smartprojector.ui.tuya

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuya.smart.api.MicroContext
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback
import com.tuya.smart.panelcaller.api.AbsPanelCallerService
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.FragmentTuyaHomeBinding
import com.ubtrobot.smartprojector.repo.Repository
import com.ubtrobot.smartprojector.ui.login.LoginActivity
import com.ubtrobot.smartprojector.ui.tuya.controllers.GeneralControllerFragment
import com.ubtrobot.smartprojector.ui.tuya.controllers.WifiLampControllerFragment
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
//    private var homeId: Long? = null

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
        adapter = TuyaDeviceAdapter(emptyList()) { index, item ->
            showController(item)

            val service = MicroContext.getServiceManager().findServiceByInterface<AbsPanelCallerService>(AbsPanelCallerService::class.java.name)
            service.goPanelWithCheckAndTip(requireActivity(), item.id)
        }
        binding.rcDeviceList.adapter = adapter
        binding.btnAddNewDevice.setOnClickListener {
            if (repo.prefs.currentHomeId != -1L) {
                NewDeviceActivity.start(requireContext(), repo.prefs.currentHomeId)
            }
        }

        binding.networkLayout.loading()
        if (repo.prefs.currentHomeId == -1L) {
            homeQuery()
        } else {
            binding.tvHome.text = "家庭：${repo.prefs.currentHomeName}, ${repo.prefs.currentHomeId}"
            binding.refreshLayout.setOnRefreshListener {
                homeDevicesQuery(repo.prefs.currentHomeId)
            }
            homeDevicesQuery(repo.prefs.currentHomeId)
        }
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
//                    homeId = home.homeId
                    repo.prefs.currentHomeId = home.homeId
                    repo.prefs.currentHomeName = home.name
                } else {
                    binding.networkLayout.empty()
                    binding.refreshLayout.finishRefresh(true)
                }
            }

            override fun onError(errorCode: String?, error: String?) {
                binding.networkLayout.error()
                binding.refreshLayout.finishRefresh(false)

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
    private fun homeDevicesQuery(homeId: Long?) {
        if (homeId == null) {
            ToastUtil.showToast(requireContext(), "未找到家庭")
            return
        }
        TuyaHomeSdk.newHomeInstance(homeId).getHomeDetail(object : ITuyaHomeResultCallback {
            override fun onSuccess(bean: HomeBean?) {
                val deviceList = bean?.deviceList
                Timber.d("家庭设备查询成功： ${deviceList?.size}")
                val items = ArrayList<TuyaDevice>()
                deviceList?.forEach { d ->
                    // gwType: If device is virtual, the filed value is "v" , else is "s"
                    Timber.d(
                            "设备：${d.name}, ${d.devId}, gwType: ${d.gwType}, pid: ${d.productId}, " +
                                    "lat: ${d.lat}, lng: ${d.lon}, ${d.categoryCode}, isOnline: ${d.isOnline}, " +
                                    "isLocalOnline: ${d.isLocalOnline}"
                    )

                    val cmds = ArrayList<TuyaDeviceCmd>()
                    d.dps.forEach { p ->
                        cmds.add(TuyaDeviceCmd(d.devId, p.key, p.value?.toString() ?: ""))
                        Timber.d("功能点: ${p.key}, ${p.value}")
                    }
                    if (d.categoryCode == TuyaCategory.ZIGBEE_SOS) {
                        TuyaUtil.registerTuyaDeviceListener(requireContext(), d.devId)
                    }
                    items.add(
                            TuyaDevice(
                                    if (d.getName() == null) "未命名" else d.getName(),
                                    d.iconUrl,
                                    d.devId,
                                    d.productId,
                                    d.isOnline,
                                    d.isZigBeeWifi,
                                    d.isZigBeeSubDev,
                                    d.categoryCode,
                                    d.schema,
                                    cmds
                            )
                    )
                }
                adapter.updateData(items)
                if (items.isNotEmpty()) {
                    val firstItem = items.first()
                    showController(firstItem)
//                    cmdAdapter.updateData(firstItem.dps)
//                    binding.tvProdName.text = "产品id：${firstItem.name}"
//                    getGatewaySubDevices(firstItem.id)
                }
                binding.networkLayout.success()
                binding.refreshLayout.finishRefresh(true)
            }

            override fun onError(errorCode: String?, errorMsg: String?) {
                binding.networkLayout.error()
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