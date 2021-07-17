package com.ubtrobot.smartprojector.ui.infant

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.facebook.react.uimanager.events.TouchEvent
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.FragmentInfantSystemBinding
import com.ubtrobot.smartprojector.launcher.AppManager

/**
 * 幼儿系统
 */
class InfantSystemFragment : Fragment() {

    private val pageTitles = listOf("兴趣培养", "习惯养成", "思维训练", "寓教于乐")

    private var listener: OnFragmentActionListener? = null
    private lateinit var binding: FragmentInfantSystemBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentActionListener) {
            listener = context
        } else {
            throw IllegalArgumentException("Activity must implement OnFragmentActionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

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

        listener?.setPageTitles(pageTitles)

        binding.viewPager.adapter = MainAdapter()
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                listener?.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                listener?.onPageSelected(position)
                listener?.loadInfantMainBackground(position)
            }
        })
        binding.pagerIndicator.setViewPager(binding.viewPager)

        listener?.loadInfantMainBackground(0)
    }

    interface OnFragmentActionListener {
        fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)
        fun onPageSelected(position: Int)
        fun loadInfantMainBackground(page: Int)

        fun setPageTitles(titles: List<String>)
    }

    private inner class MainAdapter : FragmentStateAdapter(this) {

        override fun getItemCount(): Int = pageTitles.size + 1

        override fun createFragment(position: Int): Fragment {
            if (position < itemCount - 1) {
                return InfantMainFragment.newInstance(position)
            } else {
                return AppDesktopFragment.newInstance()
            }
        }
    }

    companion object {
        fun newInstance() = InfantSystemFragment()
    }
}