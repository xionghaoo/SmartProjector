package com.ubtrobot.smartprojector.ui.infant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.facebook.react.uimanager.events.TouchEvent
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.FragmentInfantSystemBinding
import com.ubtrobot.smartprojector.launcher.AppManager

/**
 * 幼儿系统
 */
class InfantSystemFragment : Fragment() {

    private lateinit var binding: FragmentInfantSystemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInfantSystemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(requireContext())
            .load(R.mipmap.ic_launcher_bg)
            .centerCrop()
            .into(binding.ivMainBackground)

        binding.viewPager.adapter = MainAdapter()

//        childFragmentManager.beginTransaction()
//            .replace(R.id.child_fragment_container, AppDesktopFragment.newInstance())
//            .commit()
    }

    private inner class MainAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            if (position < itemCount - 1) {
                return InfantMainFragment.newInstance()
            } else {
                return AppDesktopFragment.newInstance()
            }
        }
    }

    companion object {
        fun newInstance() = InfantSystemFragment()
    }
}