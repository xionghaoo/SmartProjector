package com.ubtrobot.smartprojector.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.tuya.smart.utils.ToastUtil
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.ui.MainActivity
import com.ubtrobot.smartprojector.widgets.SystemCardView

class AppSettingsContentAdapter(
    private val context: Activity,
    private val viewModel: SettingsViewModel,
    private val titles: List<String>,
    private val onSystemChange: () -> Unit
) : RecyclerView.Adapter<AppSettingsContentAdapter.ItemViewHolder>() {

    private var selectedSystemIndex = 0

    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layout = when (viewType) {
            0 -> R.layout.vp_item_settings_page1
            1 -> R.layout.vp_item_settings_page2
            else -> R.layout.vp_item_settings_page2
        }
        return ItemViewHolder(inflater.inflate(layout, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        when (position) {
            0 -> {
                // 系统切换
                val cardSystemInfant = holder.itemView.findViewById<SystemCardView>(R.id.card_settings_system_infant)
                val cardSystemElementary = holder.itemView.findViewById<SystemCardView>(R.id.card_settings_system_elementary)
                val cardSystemSecondary = holder.itemView.findViewById<SystemCardView>(R.id.card_settings_system_secondary)
                when (viewModel.prefs().systemType) {
                    0 -> cardSystemInfant.enableSystem(true)
                    1 -> cardSystemElementary.enableSystem(true)
                    2 -> cardSystemSecondary.enableSystem(true)
                }

                cardSystemInfant.setOnClickListener {
                    selectedSystemIndex = 0
                    cardSystemInfant.enableSystem(true)
                    cardSystemElementary.enableSystem(false)
                    cardSystemSecondary.enableSystem(false)

                    viewModel.prefs().systemType = 0

                    MainActivity.startWithSingleTop(context, MainActivity.SYSTEM_INFANT)
                    onSystemChange()
                    context.finish()
                }
                cardSystemElementary.setOnClickListener {
                    selectedSystemIndex = 1
                    cardSystemInfant.enableSystem(false)
                    cardSystemElementary.enableSystem(true)
                    cardSystemSecondary.enableSystem(false)

                    viewModel.prefs().systemType = 1

                    MainActivity.startWithSingleTop(context, MainActivity.SYSTEM_ELEMENTARY)
                    onSystemChange()
                    context.finish()
                }
                cardSystemSecondary.setOnClickListener {
                    selectedSystemIndex = 2
                    cardSystemInfant.enableSystem(false)
                    cardSystemElementary.enableSystem(false)
                    cardSystemSecondary.enableSystem(true)

                    viewModel.prefs().systemType = 2

                    ToastUtil.showToast(context, "开发中")
                }
            }
            1 -> {
                holder.itemView.findViewById<Button>(R.id.btn_demo_network).setOnClickListener {
                    context.startActivity(Intent(context, SettingsActivity::class.java))
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int = titles.size
}