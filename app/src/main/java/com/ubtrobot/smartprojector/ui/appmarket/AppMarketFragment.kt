package com.ubtrobot.smartprojector.ui.appmarket

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.ubtrobot.smartprojector.BuildConfig
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.repo.table.ThirdApp
import kotlinx.android.synthetic.main.fragment_app_market.*

/**
 * 已安装的App列表
 */
class AppMarketFragment : Fragment() {

    private var isGame: Boolean = false

    private lateinit var adapter: AppInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            isGame = getBoolean(ARG_IS_GAME)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_app_market, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rc_app_list.layoutManager = GridLayoutManager(context, 6)
        adapter = AppInfoAdapter(emptyList()) { packageName ->
            // 点击图标时启动app
            requireActivity().packageManager.apply {
                val launchIntent = getLaunchIntentForPackage(packageName)
                startActivity(launchIntent)
            }
        }
        rc_app_list.adapter = adapter

        // 获取已安装的app列表
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        requireActivity().packageManager.apply {
            val packageList = queryIntentActivities(intent, 0)
            val items = ArrayList<ThirdApp>()
            packageList.forEach { info ->
                val appInfo: ApplicationInfo = getApplicationInfo(info.activityInfo.packageName, 0)
                val appName = getApplicationLabel(appInfo).toString()
                val icon = getApplicationIcon(appInfo)
                if (isGame) {
                    if (GAME_LIST.contains(appName)) {
                        val app = ThirdApp()
                        app.packageName = appInfo.packageName
                        app.name = appName
                        app.icon = icon
                        items.add(app)
                    }
                } else {
                    if (info.activityInfo.packageName != BuildConfig.APPLICATION_ID
                        && !GAME_LIST.contains(appName)) {
                        val app = ThirdApp()
                        app.packageName = appInfo.packageName
                        app.name = appName
                        app.icon = icon
                        items.add(app)
                    }
                }
            }
            adapter.updateData(items)
        }
    }

    companion object {
        val GAME_LIST = arrayOf("Blockly WebView")
        private const val ARG_IS_GAME = "ARG_IS_GAME"
        fun newInstance(isGame: Boolean = false) = AppMarketFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARG_IS_GAME, isGame)
            }
        }
    }
}