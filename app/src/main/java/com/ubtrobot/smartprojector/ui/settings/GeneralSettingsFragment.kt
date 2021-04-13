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
import com.ubtrobot.smartprojector.databinding.FragmentSettingsVolumeBinding
import com.ubtrobot.smartprojector.utils.SystemUtil
import timber.log.Timber
import kotlin.math.roundToInt

/**
 * 通用系统设置，亮度、音量
 */
class GeneralSettingsFragment : Fragment() {
    private var type: Int = 0

    private var _bindingLight: FragmentSettingsLightBinding? = null
    private val bindingLight get() = _bindingLight!!
    private var _bindingVolume: FragmentSettingsVolumeBinding? = null
    private val bindingVolume get() = _bindingVolume!!

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

    companion object {

        const val TYPE_LIGHT_ADJUST = 0
        const val TYPE_VOLUME_ADJUST = 1

        private const val ARG_TYPE = "ARG_TYPE"

        fun newInstance(type: Int) =
                GeneralSettingsFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_TYPE, type)
                    }
                }
    }
}