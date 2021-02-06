package com.ubtrobot.smartprojector.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.FragmentMagicSpaceBinding
import com.ubtrobot.smartprojector.startPlainActivity
import com.ubtrobot.smartprojector.ui.tuya.TuyaActivity
import com.ubtrobot.smartprojector.ui.tuya.TuyaHomeActivity
import timber.log.Timber

/**
 * 魔法空间
 */
class MagicSpaceFragment : Fragment() {

    private var _binding: FragmentMagicSpaceBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMagicSpaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        btn_ukit.setOnClickListener {
//            startPlainActivity(HomeActivity::class.java)
//        }

        binding.cardDevice.setOnClickListener {
            startPlainActivity(TuyaHomeActivity::class.java)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        fun newInstance() = MagicSpaceFragment()

    }
}