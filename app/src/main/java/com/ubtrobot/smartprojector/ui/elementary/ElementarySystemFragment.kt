package com.ubtrobot.smartprojector.ui.elementary

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.FragmentElementarySystemBinding
import com.ubtrobot.smartprojector.launcher.AppManager
import com.ubtrobot.smartprojector.ui.appmarket.AppMarketFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * 小学系统
 */
@AndroidEntryPoint
class ElementarySystemFragment : Fragment() {

    private var pageTitles = listOf("智能学习", "语文", "英语", "数学", "编程", "直播")

    private lateinit var binding: FragmentElementarySystemBinding

    private lateinit var screenAdapter: ScreenAdapter

    private var listener: OnFragmentActionListener? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.d("on receiver: ${intent?.action}")
            when(intent?.action) {
                Intent.ACTION_PACKAGE_INSTALL -> {

                }
                Intent.ACTION_PACKAGE_ADDED -> {
                    // 应用安装
                    screenAdapter.addApp()
                }
                Intent.ACTION_PACKAGE_REMOVED -> {
                    // 应用卸载
                    screenAdapter.removeApp()
                }
                Intent.ACTION_PACKAGE_CHANGED -> {
                    // 应用更新
                    screenAdapter.updateAppGrids()
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentActionListener) {
            listener = context
        } else {
            throw IllegalStateException("Activity must implement OnFragmentActionListener")
        }
    }

    override fun onDetach() {
        listener = null
        activity?.unregisterReceiver(receiver)
        super.onDetach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentElementarySystemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listener?.setPageTitles(pageTitles)

        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL)
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addDataScheme("package")
        activity?.registerReceiver(receiver, intentFilter)

        AppManager.getInstance(requireContext()).getAllApps()
        AppManager.getInstance(requireContext()).addUpdateListener { apps ->
            screenAdapter.setAppNum(apps.size)
            true
        }

        screenAdapter = ScreenAdapter()
        binding.viewPager.adapter = screenAdapter
        // 缓存3页
        binding.viewPager.offscreenPageLimit = 3
        binding.pagerIndicator.setViewPager(binding.viewPager)
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                listener?.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                listener?.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        listener?.loadElementarySystemBackground()
    }

//    fun addApp() {
//        screenAdapter.addApp()
//    }
//
//    fun removeApp() {
//        screenAdapter.removeApp()
//    }
//
//    fun updateAppGrids() {
//        screenAdapter.updateAppGrids()
//    }
//
//    fun setAppNum(num: Int) {
//        screenAdapter.setAppNum(num)
//    }

    private inner class ScreenAdapter : FragmentStatePagerAdapter(
        childFragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

        private val MAX_APP_NUM = 18

        private var appNum: Int = 0
        private var appPageNum: Int = 0

        private val appGridList = ArrayList<AppMarketFragment>()

        override fun getCount(): Int = pageTitles.size + appGridList.size

        override fun getItem(position: Int): Fragment {
            if (position < pageTitles.size) {
                return ElementayMainFragment.newInstance(position)
            } else {
                val gridPosition = position - pageTitles.size
                val frag = appGridList[gridPosition]
                frag.setPosition(gridPosition)
                return frag
            }
        }

        // 删除页需要用到
        override fun getItemPosition(obj: Any): Int {
            return PagerAdapter.POSITION_NONE
        }

        fun setAppNum(num: Int) {
            appNum = num
            appPageNum = if (appNum % MAX_APP_NUM == 0) appNum / MAX_APP_NUM else appNum / MAX_APP_NUM + 1
            appGridList.clear()
            for (i in 0.until(appPageNum)) {
                appGridList.add(AppMarketFragment.newInstance(MAX_APP_NUM, appPageNum))
            }
            binding.pagerIndicator.updateItems(count)
            notifyDataSetChanged()
        }

        fun updateAppGrids() {
            appGridList.forEach { gridFrag ->
                if (gridFrag.isAdded) {
                    gridFrag.updateApps()
                }
            }
        }

        fun addApp() {
            if (appNum % MAX_APP_NUM == 0) {
                // 添加新的一页
                appNum += 1
                appPageNum = appNum / MAX_APP_NUM
                appGridList.add(AppMarketFragment.newInstance(MAX_APP_NUM, appPageNum))
                binding.pagerIndicator.updateItems(count)
                notifyDataSetChanged()
            } else {
                appNum += 1
                updateAppGrids()
            }
        }

        fun removeApp() {
            appNum -= 1
            if (appNum % MAX_APP_NUM == 0) {
                // 减少一页
                appPageNum = appNum / MAX_APP_NUM
                if (appGridList.isNotEmpty()) {
                    appGridList.removeAt(appGridList.size - 1)
                }
                binding.pagerIndicator.updateItems(count)
                notifyDataSetChanged()
            } else {
                updateAppGrids()
            }
        }
    }

    interface OnFragmentActionListener {
        fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)
        fun onPageSelected(position: Int)

        fun loadElementarySystemBackground()

        fun setPageTitles(titles: List<String>)
    }

    companion object {
        fun newInstance() = ElementarySystemFragment()
    }
}