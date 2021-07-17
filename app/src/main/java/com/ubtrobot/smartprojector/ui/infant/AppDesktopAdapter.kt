package com.ubtrobot.smartprojector.ui.infant

import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ubtrobot.smartprojector.ui.appmarket.AppMarketFragment
import com.ubtrobot.smartprojector.ui.elementary.ElementayMainFragment
import timber.log.Timber

class AppDesktopAdapter(frag: Fragment) : FragmentStateAdapter(frag) {
    private val MAX_APP_NUM = 18

    private var appNum: Int = 0
    private var appPageNum: Int = 0

    private val appGridList = ArrayList<AppMarketFragment>()

//    override fun getCount(): Int = pageTitles.size + appGridList.size

    override fun getItemCount(): Int = appGridList.size

//    override fun getItem(position: Int): Fragment {
//        if (position < pageTitles.size) {
//            return ElementayMainFragment.newInstance(position)
//        } else {
//            val gridPosition = position - pageTitles.size
//            val frag = appGridList[gridPosition]
//            frag.setPosition(gridPosition)
//            return frag
//        }
//    }

    override fun createFragment(position: Int): Fragment {
        val frag = appGridList[position]
        frag.setPosition(position)
        return frag
    }

    // 删除页需要用到
//    override fun getItemPosition(obj: Any): Int {
//        return PagerAdapter.POSITION_NONE
//    }

    // 初始化
    fun setAppNum(num: Int) {
        appNum = num
        appPageNum = if (appNum % MAX_APP_NUM == 0) appNum / MAX_APP_NUM else appNum / MAX_APP_NUM + 1
        appGridList.clear()
        for (i in 0.until(appPageNum)) {
            appGridList.add(AppMarketFragment.newInstance(MAX_APP_NUM, appPageNum))
        }
        Timber.d("num: ${num}, appPageNum = $appPageNum")
//        binding.pagerIndicator.updateItems(count)
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
//            binding.pagerIndicator.updateItems(count)
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
//            binding.pagerIndicator.updateItems(count)
            notifyDataSetChanged()
        } else {
            updateAppGrids()
        }
    }
}