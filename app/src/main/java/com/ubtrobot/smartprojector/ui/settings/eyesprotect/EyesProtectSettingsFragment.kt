package com.ubtrobot.smartprojector.ui.settings.eyesprotect

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.FragmentEyesProtectSettingsBinding

/**
 * 护眼设置项
 */
class EyesProtectSettingsFragment : Fragment() {

    private var _binding: FragmentEyesProtectSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentEyesProtectSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.menuItemUseTime.setDetail("30分钟")
        binding.menuItemUseTime.setOnSelectListener {
        }
        binding.menuItemRestTime.setDetail("30分钟")

    }

//    fun showSelectionPage(isShow: Boolean) {
//        if (isShow) {
//            binding.containerMain.visibility = View.GONE
//            binding.subFragmentContainer.visibility = View.VISIBLE
//            childFragmentManager.beginTransaction()
//                .replace(R.id.sub_fragment_container,
//                    EyesProtectSettingsSelectFragment.newInstance(EyesProtectSettingsSelectFragment.TYPE_SELECT_USE_TIME)
//                )
//                .commit()
//        } else {
//            binding.subFragmentContainer.visibility = View.GONE
//            binding.containerMain.visibility = View.VISIBLE
//        }
//    }

    companion object {
        fun newInstance() = EyesProtectSettingsFragment()
    }
}