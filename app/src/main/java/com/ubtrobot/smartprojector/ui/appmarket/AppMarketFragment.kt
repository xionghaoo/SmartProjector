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
import kotlinx.android.synthetic.main.fragment_app_market.*
import timber.log.Timber

/**
 * App市场
 */
class AppMarketFragment : Fragment() {

    private lateinit var adapter: AppInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            context?.packageManager?.apply {
                val launchIntent = getLaunchIntentForPackage(packageName)
                startActivity(launchIntent)
            }
        }
        rc_app_list.adapter = adapter

        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val packageManager = context?.packageManager
        if (packageManager != null) {
            val packageList = packageManager.queryIntentActivities(intent, 0)
            val items = ArrayList<AppInfo>()
            packageList.forEach { info ->
                if (info.activityInfo.packageName != BuildConfig.APPLICATION_ID) {
                    val appInfo: ApplicationInfo = packageManager.getApplicationInfo(info.activityInfo.packageName, 0)
                    val appName = packageManager.getApplicationLabel(appInfo).toString()
                    val icon = packageManager.getApplicationIcon(appInfo)
                    items.add(AppInfo(appName, icon, appInfo.packageName))
                }
            }
            adapter.updateData(items)
        }


    }

    companion object {
        fun newInstance() = AppMarketFragment()
    }
}