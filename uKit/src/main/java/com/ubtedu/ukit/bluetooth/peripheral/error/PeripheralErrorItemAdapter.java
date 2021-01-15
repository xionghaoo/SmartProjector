package com.ubtedu.ukit.bluetooth.peripheral.error;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.ukit.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author naOKi
 * @Date 2018-12-12
 **/
public class PeripheralErrorItemAdapter extends RecyclerView.Adapter<PeripheralErrorItemAdapter.PeripheralErrorViewHolder> {
	
	private ArrayList<PeripheralErrorItem> errors = new ArrayList<>();
	private HashMap<PeripheralErrorItem, Status> statusMap = new HashMap<>();
	
	public enum Status {
		ERROR, FIXING, FIXED
	}
	
	@NonNull
	@Override
	public PeripheralErrorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_peripheral_error_list, viewGroup, false);
		PeripheralErrorViewHolder vh = new PeripheralErrorViewHolder(itemView);
		return vh;
	}
	
	public void setPeripheralError(List<PeripheralErrorItem> errors) {
		this.errors.clear();
		if(errors != null) {
			this.errors.addAll(errors);
		}
		notifyDataSetChanged();
	}

	public void updateStatus(int index, Status status) {
		if(errors == null || index < 0 || index >= errors.size() || status == null) {
			return;
		}
		PeripheralErrorItem errorItem = errors.get(index);
		statusMap.put(errorItem, status);
		notifyDataSetChanged();
	}

	public int getFirstError() {
		if(errors == null || errors.size() == 0) {
			return -1;
		}
		for(int i = 0; i < errors.size(); i++) {
			PeripheralErrorItem errorItem = errors.get(i);
			Status status = statusMap.get(errorItem);
			if(status == null || Status.ERROR.equals(status)) {
				return i;
			}
		}
		return -1;
	}

	public Status getErrorStatus(int index) {
		if(errors == null || index < 0 || index >= errors.size()) {
			return Status.ERROR;
		}
		PeripheralErrorItem errorItem = errors.get(index);
		return statusMap.containsKey(errorItem) ? statusMap.get(errorItem) : Status.ERROR;
	}

	public void stopFixingStatus() {
		if(errors == null) {
			return;
		}
		HashMap<PeripheralErrorItem, Status> tmp = new HashMap<>(statusMap);
		statusMap.clear();
		for(Map.Entry<PeripheralErrorItem, Status> entry : tmp.entrySet()) {
			Status status = entry.getValue();
			PeripheralErrorItem errorItem = entry.getKey();
			if(Status.FIXING.equals(status)) {
				status = Status.ERROR;
			}
			statusMap.put(errorItem, status);
		}
		notifyDataSetChanged();
	}

	public boolean hasErrorStatus() {
		if(errors == null || statusMap == null || statusMap.isEmpty()) {
			return false;
		}
		return errors.size() != statusMap.size() || statusMap.containsValue(Status.ERROR);
	}

	@Override
	public void onBindViewHolder(@NonNull PeripheralErrorViewHolder viewHolder, int i) {
		PeripheralErrorItem errorItem = errors.get(i);
		viewHolder.name.setText(errorItem.toString());
		Status status = statusMap.get(errorItem);
		if(status == null || status.equals(Status.ERROR)) {
			viewHolder.animator.cancel();
			viewHolder.mark.setImageResource(R.drawable.popup_list_abnormal_icon);
			viewHolder.mark.setRotation(0);
		} else if(status.equals(Status.FIXING)) {
			viewHolder.mark.setImageResource(R.drawable.popup_list_refresh_icon);
			viewHolder.animator.start();
		} else {
			viewHolder.animator.cancel();
			viewHolder.mark.setImageResource(R.drawable.popup_list_ok_icon);
			viewHolder.mark.setRotation(0);
		}
	}

	@Override
	public int getItemCount() {
		return errors == null ? 0 : errors.size();
	}
	
	public PeripheralErrorItem getItem(int position) {
		return errors.get(position);
	}
	
	public static class PeripheralErrorViewHolder extends RecyclerView.ViewHolder {
		private PeripheralErrorViewHolder(@NonNull View itemView) {
			super(itemView);
			name = itemView.findViewById(R.id.project_motion_list_item_name_tv);
			mark = itemView.findViewById(R.id.project_motion_list_item_name_mark);
			animator = ObjectAnimator.ofFloat(mark, "rotation", 0, 360);
			animator.setDuration(1000);
			animator.setRepeatMode(ValueAnimator.RESTART);
			animator.setRepeatCount(ValueAnimator.INFINITE);
		}
		private final TextView name;
		private final ImageView mark;
		private final ObjectAnimator animator;
	}
	
}
