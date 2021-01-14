package com.ubtrobot.smartprojector.ui.restrict

import android.content.Intent
import android.content.pm.ApplicationInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ubtrobot.smartprojector.BuildConfig
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.repo.table.ThirdApp
import com.ubtrobot.smartprojector.ui.appmarket.AppMarketFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_app_white_list.*
import javax.inject.Inject

/**
 * App白名单
 */
@AndroidEntryPoint
class AppWhiteListActivity : AppCompatActivity() {

    private lateinit var adapter: AppWhiteListAdapter

    private val viewModel: RestrictViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_white_list)

        rc_app_white_list.layoutManager = LinearLayoutManager(this)
        adapter = AppWhiteListAdapter(emptyList())
        rc_app_white_list.adapter = adapter

        viewModel.loadThirdApps().observe(this, Observer { r ->
            if (r.isEmpty()) {
                queryThirdApps()
            } else {
                r.forEach { app ->
                    val appInfo = packageManager.getApplicationInfo(app.packageName, 0)
                    app.icon = packageManager.getApplicationIcon(appInfo)
                }
                adapter.updateData(r)
            }
        })
    }

    private fun queryThirdApps() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        packageManager.apply {
            val packageList = queryIntentActivities(intent, 0)
            val items = ArrayList<ThirdApp>()
            packageList.forEach { info ->
                val appInfo: ApplicationInfo = getApplicationInfo(info.activityInfo.packageName, 0)
                val appName = getApplicationLabel(appInfo).toString()
                val icon = getApplicationIcon(appInfo)
                if (info.activityInfo.packageName != BuildConfig.APPLICATION_ID) {
                    val app = ThirdApp()
                    app.packageName = appInfo.packageName
                    app.name = appName
                    app.icon = icon
                    items.add(app)
                }
            }
            viewModel.saveThirdApps(items)
        }
    }
}