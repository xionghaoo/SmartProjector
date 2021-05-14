package com.ubtrobot.smartprojector.ui.tuya.controllers

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tuya.smart.centralcontrol.TuyaLightDevice
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.sdk.api.IResultCallback
import com.tuya.smart.sdk.centralcontrol.api.ILightListener
import com.tuya.smart.sdk.centralcontrol.api.ITuyaLightDevice
import com.tuya.smart.sdk.centralcontrol.api.bean.LightDataPoint
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.FragmentWifiLampControllerBinding
import com.ubtrobot.smartprojector.ui.tuya.TuyaDevice
import com.ubtrobot.smartprojector.ui.tuya.TuyaDp
import com.ubtrobot.smartprojector.utils.ToastUtil
import okhttp3.internal.toHexString
import timber.log.Timber
import kotlin.math.roundToInt

/**
 * wifi灯控制器
 */
class WifiLampControllerFragment : Fragment() {
    private var device: TuyaDevice? = null
    private var _binding: FragmentWifiLampControllerBinding? = null
    private val binding get() = _binding!!
    private var adjustColor: Int = 0
    private var lightValue: Int = 0
    private lateinit var lightDev: ITuyaLightDevice
    private var colorH: Int = 0
    private var colorS: Int = 0
    private var colorV: Int = 0

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
        _binding = FragmentWifiLampControllerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        device?.also {
            lightDev = TuyaLightDevice(it.id)
            lightDev.registerLightListener(object : ILightListener {
                override fun onDpUpdate(p0: LightDataPoint?) {

                }

                override fun onRemoved() {

                }

                override fun onStatusChanged(status: Boolean) {

                }

                override fun onNetworkStatusChanged(p0: Boolean) {

                }

                override fun onDevInfoUpdate() {

                }
            })

            val hsv = lightDev.lightDataPoint.colorHSV
            colorH = hsv.h
            colorS = hsv.s
            colorV = hsv.v

            binding.sbAdjustColourH.progress = (colorH.toFloat() / 360 * 100).roundToInt()
            binding.sbAdjustColourS.progress = colorS
            binding.sbAdjustColourV.progress = colorV
        }

//        Timber.d("schema: ${device?.schema}")
        if (device != null) {
            device?.dps?.forEach { dp ->
                Timber.d("key: ${dp.key}, value: ${dp.value}")
            }
            // 填充UI
            device?.dps?.forEach { dp ->
                when (dp.key) {
                    CMD_TOGGLE_LAMP -> {
                        binding.swLamp.isChecked = dp.value == "true"
                        binding.swLamp.text = if (dp.value == "true") "开" else "关"
                    }
                    CMD_ADJUST_BRIGHT -> {
                        Timber.d("value: ${dp.value}")
                        // max 255, min 25
                        val bright = dp.value?.toInt() ?: 0
                        lightValue = bright
                        var progress = ((if (bright <= 25) 0 else bright - 25).toFloat() / (255 - 25) * 100).roundToInt()
                        if (progress < 0) {
                            progress = 0
                        } else {
                            if (progress > 100) progress = 100
                        }
                        binding.sbLight.progress = progress
                        binding.sbLight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                            override fun onProgressChanged(
                                seekBar: SeekBar?,
                                progress: Int,
                                fromUser: Boolean
                            ) {
                                binding.tvLightProgress.text = progress.toString()
                                lightValue = progress * (255 - 25) / 100 + 25
                                adjustBright(lightValue)
                            }

                            override fun onStartTrackingTouch(seekBar: SeekBar?) {

                            }

                            override fun onStopTrackingTouch(seekBar: SeekBar?) {

                            }
                        })

                        binding.tvLightProgress.text = progress.toString()
                    }
                    CMD_COLOUR -> {
                        binding.sbAdjustColourH.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                            override fun onProgressChanged(
                                seekBar: SeekBar?,
                                progress: Int,
                                fromUser: Boolean
                            ) {
                                colorH = (progress.toFloat() / 100 * 360).roundToInt()
                                lightDev.colorHSV(colorH, colorS, colorV, null)
                            }

                            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                            }

                            override fun onStopTrackingTouch(seekBar: SeekBar?) {

                            }
                        })
                        binding.sbAdjustColourS.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                            override fun onProgressChanged(
                                seekBar: SeekBar?,
                                progress: Int,
                                fromUser: Boolean
                            ) {
                                colorS = progress
                                lightDev.colorHSV(colorH, colorS, colorV, null)
                            }

                            override fun onStartTrackingTouch(seekBar: SeekBar?) {

                            }

                            override fun onStopTrackingTouch(seekBar: SeekBar?) {

                            }
                        })
                        binding.sbAdjustColourV.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                            override fun onProgressChanged(
                                seekBar: SeekBar?,
                                progress: Int,
                                fromUser: Boolean
                            ) {
                                colorV = progress
                                lightDev.colorHSV(colorH, colorS, colorV, null)
                            }

                            override fun onStartTrackingTouch(seekBar: SeekBar?) {

                            }

                            override fun onStopTrackingTouch(seekBar: SeekBar?) {

                            }
                        })
                    }
                }
            }

            // 解析控制指令
            val dps = Gson().fromJson<List<TuyaDp>>(device!!.schema, object : TypeToken<List<TuyaDp>>() {}.type)
            dps?.forEach {
                when (it.id) {
                    1 -> {
                        binding.swLamp.setOnCheckedChangeListener { _, isChecked ->
                            toggle(isChecked)
                        }
                    }
                    2 -> {

                    }
                }
            }
        }
    }

    /**
     * 灯开关指令
     */
    private fun toggle(isOpen: Boolean) {
//        val d = TuyaHomeSdk.newDeviceInstance(device?.id)
//        val cmd = "{\"${CMD_TOGGLE_LAMP}\": ${isOpen}}"
//        Timber.d("发送指令：$cmd")
//        d.publishDps(cmd, object : IResultCallback {
//            override fun onError(code: String?, error: String?) {
//                Timber.d("指令发送失败：$code, $error")
//            }
//
//            override fun onSuccess() {
//                binding.swLamp.text = if (isOpen) "开" else "关"
//            }
//        })
        sendCmd(CMD_TOGGLE_LAMP, isOpen.toString())
    }

    /**
     * 调节灯亮度指令
     */
    private fun adjustBright(value: Int) {
//        val d = TuyaHomeSdk.newDeviceInstance(device?.id)
//        val cmd = "{\"${CMD_ADJUST_BRIGHT}\": ${value}}"
//        Timber.d("发送指令：$cmd")
//        d.publishDps(cmd, object : IResultCallback {
//            override fun onError(code: String?, error: String?) {
//                Timber.d("指令发送失败：$code, $error")
//            }
//
//            override fun onSuccess() {
//
//            }
//        })
        sendCmd(CMD_ADJUST_BRIGHT, value.toString())
    }

    private fun adjustLightColour(color: String) {
        sendCmd(CMD_COLOUR, color)
    }

    private fun sendCmd(dp: String, value: String) {
        val d = TuyaHomeSdk.newDeviceInstance(device?.id)
        val cmd = "{\"${dp}\": ${value}}"
        Timber.d("发送指令：$cmd")
        d.publishDps(cmd, object : IResultCallback {
            override fun onError(code: String?, error: String?) {
                Timber.d("指令发送失败：$code, $error")
            }

            override fun onSuccess() {

            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TUYA_DEVICE = "ARG_TUYA_DEVICE"
        private const val CMD_TOGGLE_LAMP = "1" //led_switch
        private const val CMD_ADJUST_BRIGHT = "3" //bright_value
        private const val CMD_COLOUR = "5" //bright_value

        fun newInstance(dev: TuyaDevice) =
            WifiLampControllerFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TUYA_DEVICE, dev)
                }
            }
    }
}