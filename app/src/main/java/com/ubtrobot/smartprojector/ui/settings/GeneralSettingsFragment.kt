package com.ubtrobot.smartprojector.ui.settings

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.ubtrobot.smartprojector.databinding.FragmentSettingsLightBinding
import com.ubtrobot.smartprojector.databinding.FragmentSettingsNetworkBinding
import com.ubtrobot.smartprojector.databinding.FragmentSettingsVolumeBinding
import com.ubtrobot.smartprojector.utils.SystemUtil
import com.ubtrobot.smartprojector.utils.WifiUtil
import kotlin.math.roundToInt

/**
 * 通用系统设置，亮度、音量，网络
 */
class GeneralSettingsFragment : Fragment() {
    private var type: Int = 0

    private var _bindingLight: FragmentSettingsLightBinding? = null
    private val bindingLight get() = _bindingLight!!
    private var _bindingVolume: FragmentSettingsVolumeBinding? = null
    private val bindingVolume get() = _bindingVolume!!
    private var _bindingNetwork: FragmentSettingsNetworkBinding? = null
    private val bindingNetwork get() = _bindingNetwork!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getInt(ARG_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        when (type) {
            TYPE_LIGHT_ADJUST -> {
                _bindingLight = FragmentSettingsLightBinding.inflate(inflater, container, false)
                return bindingLight.root
            }
            TYPE_VOLUME_ADJUST -> {
                _bindingVolume = FragmentSettingsVolumeBinding.inflate(inflater, container, false)
                return bindingVolume.root
            }
            TYPE_NETWORK -> {
                _bindingNetwork = FragmentSettingsNetworkBinding.inflate(inflater, container, false)
                return bindingNetwork.root
            }
            else -> {
                throw IllegalStateException("Not matched GeneralSettingsFragment layout")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when(type) {
            TYPE_LIGHT_ADJUST -> bindLightFragment()
            TYPE_VOLUME_ADJUST -> bindVolumeFragment()
            TYPE_NETWORK -> bindNetworkFragment()
        }
    }

    /**
     * 亮度调节
     */
    private fun bindLightFragment() {
        val cr = requireContext().contentResolver
        Settings.System.putInt(
            cr,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        )
        val brightness = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS)
        bindingLight.sbAdjustLight.progress = (brightness / 255f * 100).roundToInt()
        bindingLight.sbAdjustLight.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                activity?.apply {
                    Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS, (progress / 100f * 255).roundToInt())
                    val layoutpars: WindowManager.LayoutParams = window.attributes
                    layoutpars.buttonBrightness = progress / 255f
                    window.attributes = layoutpars
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

    }

    /**
     * 音量调节
     */
    private fun bindVolumeFragment() {
        context?.apply {
            val mgr = getSystemService(Context.AUDIO_SERVICE) as AudioManager?
            if (mgr != null) {
                val musicVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC)
                bindingVolume.sbAdjustVolume.progress = (musicVolume / 15f * 100).roundToInt()
                bindingVolume.sbAdjustVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        mgr.setStreamVolume(AudioManager.STREAM_MUSIC, (progress / 100f * 15).roundToInt(), AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }
                })
            }
        }

    }

    /**
     * 网络设置
     */
    private fun bindNetworkFragment() {
        WifiUtil.getWifiSSID(requireContext()) { ssid ->
            val wifiLevel = WifiUtil.getWifiLevel(requireContext())
            if (ssid != null) {
                bindingNetwork.tvSettingsWifiName.text = ssid
                bindingNetwork.tvSettingsWifiLevel.text = "信号${if (wifiLevel >= 2) "较强" else "较弱"}"
            } else {
                bindingNetwork.containerSettingsConnectedNetwork.visibility = View.GONE
                bindingNetwork.splitLine1.visibility = View.GONE
            }
        }

        bindingNetwork.menuItemToNetworkSetting.setDetail("前往设置")
        bindingNetwork.menuItemToNetworkSetting.setOnSelectListener {
            SystemUtil.openSettingsWifi(context)
        }
    }

    companion object {

        const val TYPE_LIGHT_ADJUST = 0
        const val TYPE_VOLUME_ADJUST = 1
        const val TYPE_NETWORK = 2

        private const val ARG_TYPE = "ARG_TYPE"

        fun newInstance(type: Int) =
                GeneralSettingsFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_TYPE, type)
                    }
                }
    }
}