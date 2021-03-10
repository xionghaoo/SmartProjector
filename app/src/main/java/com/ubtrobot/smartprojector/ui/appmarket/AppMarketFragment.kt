package com.ubtrobot.smartprojector.ui.appmarket

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.ubtrobot.smartprojector.databinding.FragmentAppMarketBinding
import com.ubtrobot.smartprojector.launcher.AppManager
import timber.log.Timber


/**
 * 已安装的App列表
 */
class AppMarketFragment : Fragment() {

    private var appMaxNum: Int = 0
    private var position: Int = 0
    private var pageNum: Int = 0

    private lateinit var adapter: AppInfoAdapter

    private var _binding: FragmentAppMarketBinding? = null
    private val binding get() = _binding!!

    fun setPosition(p: Int) {
        position = p
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            appMaxNum = getInt(ARG_LIMIT_APP_NUM)
            pageNum = getInt(ARG_PAGE_NUM)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
//        Timber.d("onViewCreated")
        updateApps()
        // 获取已安装的app列表
//        val intent = Intent(Intent.ACTION_MAIN)
//        intent.addCategory(Intent.CATEGORY_LAUNCHER)
//        requireActivity().packageManager.apply {
//            val packageList = queryIntentActivities(intent, 0)
//            val items = ArrayList<ThirdApp>()
//            packageList.forEach { info ->
//                val appInfo: ApplicationInfo = getApplicationInfo(info.activityInfo.packageName, 0)
//                val appName = getApplicationLabel(appInfo).toString()
//                val icon = getApplicationIcon(appInfo)
//                if (isGame) {
//                    if (GAME_LIST.contains(appName)) {
//                        val app = ThirdApp()
//                        app.packageName = appInfo.packageName
//                        app.name = appName
//                        app.icon = icon
//                        items.add(app)
//                    }
//                } else {
//                    if (info.activityInfo.packageName != BuildConfig.APPLICATION_ID
//                        && !GAME_LIST.contains(appName)) {
//                        val app = ThirdApp()
//                        app.packageName = appInfo.packageName
//                        app.name = appName
//                        app.icon = icon
//                        items.add(app)
//                    }
//                }
//            }
//            adapter.updateData(items)
//        }
    }

    fun updateApps() {
        AppManager.getInstance(context).getAllApps()
        AppManager.getInstance(context).addUpdateListener { apps ->
            adapter.updateData(apps.filterIndexed { index, _ ->
                index >= position * appMaxNum && index < position * appMaxNum + appMaxNum
            })
            false
        }
    }

    companion object {
        val GAME_LIST = arrayOf("Blockly WebView")
        private const val ARG_LIMIT_APP_NUM = "ARG_LIMIT_APP_NUM"
        private const val ARG_PAGE_NUM = "ARG_PAGE_NUM"
        fun newInstance(limitAppNum: Int, pageNum: Int) = AppMarketFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_LIMIT_APP_NUM, limitAppNum)
                putInt(ARG_PAGE_NUM, pageNum)
            }
        }
    }
}