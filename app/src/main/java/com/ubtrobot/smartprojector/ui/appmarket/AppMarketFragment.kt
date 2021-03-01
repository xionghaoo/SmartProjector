package com.ubtrobot.smartprojector.ui.appmarket

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherApps
import android.os.Bundle
import android.os.UserHandle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.ubtrobot.smartprojector.BuildConfig
import com.ubtrobot.smartprojector.databinding.FragmentAppMarketBinding
import com.ubtrobot.smartprojector.repo.table.ThirdApp

/**
 * 已安装的App列表
 */
class AppMarketFragment : Fragment() {

    private var isGame: Boolean = false

    private lateinit var adapter: AppInfoAdapter

    private var _binding: FragmentAppMarketBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            isGame = getBoolean(ARG_IS_GAME)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentAppMarketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rcAppList.layoutManager = GridLayoutManager(context, 6)
        adapter = AppInfoAdapter(emptyList()) { pkgName ->
            // 点击图标时启动app
            requireActivity().packageManager.apply {
                val launchIntent = getLaunchIntentForPackage(pkgName)
                startActivity(launchIntent)

                // TODO App关闭测试
//                CoroutineScope(Dispatchers.Default).launch {
//                    delay(10 * 1000)
//                    Timber.d("10s后关闭启动的App")
//                    withContext(Dispatchers.Main) {
//                        RootExecutor.exec(
//                            cmd = RootCommand.stopApp(pkgName),
//                            success = {
//                                Timber.d("关闭${pkgName}成功")
//                                ToastUtil.showToast(requireContext(), "关闭${pkgName}成功")
//                            },
//                            failure = {
//                                Timber.d("关闭${pkgName}失败")
//                                ToastUtil.showToast(requireContext(), "关闭${pkgName}失败")
//                            }
//                        )
//                    }
//                }
            }
        }
        binding.rcAppList.adapter = adapter

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
            adapter.updateData(items.filterIndexed { index, _ -> index < 10 })
        }


    }

    private class Test : LauncherApps.Callback() {
        override fun onPackageRemoved(packageName: String?, user: UserHandle?) {

        }

        override fun onPackageAdded(packageName: String?, user: UserHandle?) {

        }

        override fun onPackageChanged(packageName: String?, user: UserHandle?) {

        }

        override fun onPackagesAvailable(
            packageNames: Array<out String>?,
            user: UserHandle?,
            replacing: Boolean
        ) {

        }

        override fun onPackagesUnavailable(
            packageNames: Array<out String>?,
            user: UserHandle?,
            replacing: Boolean
        ) {

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