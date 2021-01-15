package com.ubtedu.ukit.project.controller.settings.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.project.controller.interfaces.IControllerItemClickListener;
import com.ubtedu.ukit.project.controller.utils.ControllerConstValue;

/**
 * @Author naOKi
 * @Date 2018-11-30
 **/
public class SettingIconItemAdapter extends RecyclerView.Adapter<SettingIconItemAdapter.SettingItemViewHolder> {
	
	private static final int[] ICON_RES_ARRAY = ControllerConstValue.ICON_RES_ARRAY;
	
	private int mSelectedItem = -1;
	
	private IControllerItemClickListener itemClickListener;
	
	@NonNull
	@Override
	public SettingItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_project_controller_setting_icon, viewGroup, false);
		SettingItemViewHolder vh = new SettingItemViewHolder(itemView);
		return vh;
	}
	
	public void setItemClickListener(IControllerItemClickListener itemClickListener) {
		this.itemClickListener = itemClickListener;
	}
	
	public IControllerItemClickListener getItemClickListener() {
		return itemClickListener;
	}
	
	public void setSelectedItem(int selectedItem) {
		if(selectedItem == mSelectedItem) {
			return;
		}
		this.mSelectedItem = selectedItem;
		notifyDataSetChanged();
	}
	
	public int getSelectedItem() {
		return mSelectedItem;
	}
	
	@Override
	public void onBindViewHolder(@NonNull SettingItemViewHolder viewHolder, int i) {
		int resId = ICON_RES_ARRAY[i];
		viewHolder.icon.setImageResource(resId);
		if(mSelectedItem == i) {
			viewHolder.icon.setBackgroundResource(R.drawable.popup_list_act_icon_sel);
			viewHolder.mark.setVisibility(View.VISIBLE);
		} else {
			viewHolder.icon.setBackgroundResource(R.drawable.popup_list_act_icon_nor);
			viewHolder.mark.setVisibility(View.GONE);
		}
		viewHolder.itemView.setTag(i);
		if(itemClickListener != null) {
			viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(itemClickListener != null) {
						itemClickListener.onItemClick(v, (int)v.getTag());
					}
				}
			});
		}
	}
	
	@Override
	public int getItemCount() {
		return ICON_RES_ARRAY.length;
	}
	
	public static class SettingItemViewHolder extends RecyclerView.ViewHolder {
		private SettingItemViewHolder(@NonNull View itemView) {
			super(itemView);
			icon = itemView.findViewById(R.id.item_project_controller_setting_icon_iv);
			mark = itemView.findViewById(R.id.item_project_controller_setting_icon_mark);
		}
		private final ImageView icon;
		private final ImageView mark;
	}
	
}
