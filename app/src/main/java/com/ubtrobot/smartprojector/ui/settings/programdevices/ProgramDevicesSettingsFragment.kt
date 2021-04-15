package com.ubtrobot.smartprojector.ui.settings.programdevices

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayout
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback
import com.ubtrobot.smartprojector.GlideApp
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.FragmentProgramDevicesSettingsBinding
import com.ubtrobot.smartprojector.ui.login.LoginActivity
import com.ubtrobot.smartprojector.ui.tuya.TuyaDevice
import com.ubtrobot.smartprojector.ui.tuya.TuyaDeviceCmd
import com.ubtrobot.smartprojector.utils.ToastUtil
import com.ubtrobot.smartprojector.utils.TuyaUtil
import timber.log.Timber
import kotlin.math.roundToInt

class ProgramDevicesSettingsFragment : Fragment() {

    private var _binding: FragmentProgramDevicesSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProgramDevicesSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSettingsDeviceAdd.setOnClickListener {
            ToastUtil.showToast(requireContext(), "添加新设备")
        }

//        addCategory("两季")
//
//        addCategory("小家电")

        binding.networkLayout.loading()
        homeQuery()
    }

    private fun updateDevicesCategory(devices: List<TuyaDevice>) {
        binding.listDevices.post {
            binding.listDevices.removeAllViews()
            // 设备分类
            addCategory("两季", devices.slice(0..3))
            addCategory("小家电", devices.slice(4..5))
            addCategory("其他", devices.slice(6 until devices.size))
        }
    }

    private fun addCategory(title: String, items: List<TuyaDevice>) {
        val categoryView = layoutInflater.inflate(R.layout.list_item_settings_device_category, null)
        binding.listDevices.addView(categoryView)
        val tvTitle = categoryView.findViewById<TextView>(R.id.tv_settings_device_category_title)
        tvTitle.text = title
        val categoryBox = categoryView.findViewById<FlexboxLayout>(R.id.container_settings_devices)
//        Timber.d("post listDevices width: ${binding.listDevices.width}")
        val lp = categoryBox.layoutParams
        val rowNum = (items.size / 2f).roundToInt()
        lp.height = rowNum * resources.getDimension(R.dimen._226dp).roundToInt() + (rowNum - 1) * resources.getDimension(R.dimen._40dp).roundToInt()
        categoryBox.removeAllViews()
        // 148 = 60 + 60 + 28
        val itemWidth = (binding.listDevices.width - resources.getDimension(R.dimen._148dp).roundToInt()) / 2
        items.forEach { item ->
            addItemView(categoryBox, item, itemWidth)
        }
    }

    private fun addItemView(categoryBox: FlexboxLayout, item: TuyaDevice, itemWidth: Int) {
        val itemView = layoutInflater.inflate(R.layout.list_item_settings_device_item, null)
        categoryBox.addView(itemView)
        val itemLp = itemView.layoutParams as FlexboxLayout.LayoutParams
        itemLp.width = itemWidth
        itemLp.height = resources.getDimension(R.dimen._226dp).roundToInt()
        itemView.findViewById<TextView>(R.id.tv_settings_device_name).text = item.name
        val icon = itemView.findViewById<ImageView>(R.id.iv_settings_device_icon)
        GlideApp.with(requireContext())
            .load(item.iconUrl)
            .into(icon)
        GlideApp.with(requireContext())
            .load(if (item.isOnline) R.mipmap.ic_settings_device_online else R.mipmap.ic_settings_device_bg)
            .into(itemView.findViewById(R.id.iv_item_background))
        if (item.isZigBeeWifi) {
            GlideApp.with(requireContext())
                .load(R.mipmap.ic_settings_device_type_wifi)
                .into(itemView.findViewById(R.id.iv_settings_device_type))
        } else {

        }


        itemView.setOnClickListener {
            ToastUtil.showToast(requireContext(), "点击设备")
        }
    }

    /**
     * 家庭查询
     */
    private fun homeQuery() {
        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(object : ITuyaGetHomeListCallback {
            override fun onSuccess(homeBeans: MutableList<HomeBean>?) {
                if (homeBeans?.isNotEmpty() == true) {
                    val home = homeBeans.first()
                    queryDevices(home.homeId)
                } else {
                    binding.networkLayout.empty()
                }
            }

            override fun onError(errorCode: String?, error: String?) {
                ToastUtil.showToast(requireContext(), "家庭查询错误：${errorCode}, $error")
                binding.networkLayout.error()
//                binding.refreshLayout.finishRefresh(false)

                TuyaUtil.handleOnError(requireContext(), errorCode)
            }
        })
    }

    /**
     * 家庭下的设备查询
     */
    private fun queryDevices(homeId: Long) {
        TuyaHomeSdk.newHomeInstance(homeId).getHomeDetail(object : ITuyaHomeResultCallback {
            override fun onSuccess(bean: HomeBean?) {
                val deviceList = bean?.deviceList
                val items = ArrayList<TuyaDevice>()
                deviceList?.forEach { d ->
                    val cmds = ArrayList<TuyaDeviceCmd>()
                    d.dps.forEach { p ->
                        cmds.add(TuyaDeviceCmd(d.devId, p.key, p.value?.toString() ?: ""))
//                        Timber.d("功能点: ${p.key}, ${p.value}")
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
                binding.networkLayout.success()
                updateDevicesCategory(items)
            }

            override fun onError(errorCode: String?, errorMsg: String?) {
                ToastUtil.showToast(requireContext(), "设备查询错误：${errorCode}, $errorMsg")
                binding.networkLayout.error()
                TuyaUtil.handleOnError(requireContext(), errorCode)
            }
        })
    }

    companion object {
        fun newInstance() = ProgramDevicesSettingsFragment()
    }
}