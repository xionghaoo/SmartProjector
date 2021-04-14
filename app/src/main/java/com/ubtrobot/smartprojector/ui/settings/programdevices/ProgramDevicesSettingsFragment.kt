package com.ubtrobot.smartprojector.ui.settings.programdevices

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayout
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.FragmentProgramDevicesSettingsBinding
import com.ubtrobot.smartprojector.utils.ToastUtil
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

        addCategory("两季")

        addCategory("小家电")
    }

    private fun addCategory(title: String) {
        val categoryView = layoutInflater.inflate(R.layout.list_item_settings_device_category, null)
        binding.listDevices.addView(categoryView)
        val tvTitle = categoryView.findViewById<TextView>(R.id.tv_settings_device_category_title)
        tvTitle.text = title
        val categoryBox = categoryView.findViewById<FlexboxLayout>(R.id.container_settings_devices)
        val lp = categoryBox.layoutParams
        lp.height = (5 / 2f).roundToInt() * resources.getDimension(R.dimen._226dp).roundToInt() + (5 / 2) * resources.getDimension(R.dimen._40dp).roundToInt()
        addItemView(categoryBox, "TCL抽湿机")
        addItemView(categoryBox, "TCL抽湿机5")
        addItemView(categoryBox, "TCL抽湿机2")
        addItemView(categoryBox, "TCL抽湿机3")
        addItemView(categoryBox, "TCL抽湿机4")
    }

    private fun addItemView(categoryBox: FlexboxLayout, title: String) {
        val itemView = layoutInflater.inflate(R.layout.list_item_settings_device_item, null)
        categoryBox.addView(itemView)
        val itemLp = itemView.layoutParams as FlexboxLayout.LayoutParams
        itemLp.width = resources.getDimension(R.dimen._536dp).roundToInt()
        itemLp.height = resources.getDimension(R.dimen._226dp).roundToInt()
        itemView.findViewById<TextView>(R.id.tv_settings_device_name).text = title
    }

    companion object {
        fun newInstance() = ProgramDevicesSettingsFragment()
    }
}