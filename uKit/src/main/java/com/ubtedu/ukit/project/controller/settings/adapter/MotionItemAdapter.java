package com.ubtedu.ukit.project.controller.settings.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.common.base.UKitUIDelegate;
import com.ubtedu.ukit.project.MotionPlayer;
import com.ubtedu.ukit.project.bridge.api.ToastHelper;
import com.ubtedu.ukit.project.controller.interfaces.IControllerItemClickListener;
import com.ubtedu.ukit.project.vo.Motion;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @Author naOKi
 * @Date 2018-12-07
 **/
public class MotionItemAdapter extends RecyclerView.Adapter<MotionItemAdapter.SettingItemViewHolder> {

    private String selectedMotionId = null;

    private ArrayList<Motion> motions = new ArrayList<>();
    private MotionPlayer motionPlayer = new MotionPlayer();
    private IControllerItemClickListener itemClickListener;

    private long lastPressedTime = -1;

    private UKitUIDelegate uiDelegate = null;

    public MotionItemAdapter() {
        motionPlayer.setListener(new MotionPlayer.OnMotionPlayStateChangeListener() {
            @Override
            public void onMotionPlayStateChanged() {
                notifyDataSetChanged();
            }
        });
    }

    @NonNull
    @Override
    public SettingItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_project_controller_motion_list, viewGroup, false);
        SettingItemViewHolder vh = new SettingItemViewHolder(itemView);
        return vh;
    }

    public void setItemClickListener(IControllerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public IControllerItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public String getSelectedMotionId() {
        return selectedMotionId;
    }

    public void setSelectedMotionId(String selectedMotionId) {
        this.selectedMotionId = selectedMotionId;
        notifyDataSetChanged();
    }

    public void setMotion(List<Motion> motions) {
        this.motions.clear();
        if (motions != null) {
            this.motions.addAll(motions);
        }
        notifyDataSetChanged();
    }

    public void clearMotion() {
        this.motions.clear();
        notifyDataSetChanged();
    }

    public void stopMotion(){
        motionPlayer.stopMotion();
    }

    public void setUIDelegate(UKitUIDelegate uiDelegate) {
        this.uiDelegate = uiDelegate;
    }

    private void showToast(int resId) {
        showToast(UKitApplication.getInstance().getString(resId));
    }

    private void showToast(String msg) {
        ToastHelper.toastShort(msg);
    }

    private String formatTime(long time) {
        if (time != 0) {
            time = Math.max((time + 999) / 1000, 1);
        }
        return String.format(Locale.US, "%02d:%02d", time / 60, time % 60);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingItemViewHolder viewHolder, int i) {
        Motion motion = motions.get(i);
        String id = motion.id;
        viewHolder.name.setText(motion.name);
        viewHolder.time.setText(formatTime(motion.totaltime));
        viewHolder.play.setBackgroundResource(TextUtils.equals(id, motionPlayer.getPlayMotionId()) ? R.drawable.popup_list_stop_btn_nor : R.drawable.popup_list_play_btn_nor);
        viewHolder.mark.setVisibility(TextUtils.equals(id, selectedMotionId) ? View.VISIBLE : View.GONE);
        viewHolder.play.setTag(i);
        viewHolder.itemView.setTag(i);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                Motion motion = getItem(position);
                selectedMotionId = motion.id;
                notifyDataSetChanged();
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, position);
                }
            }
        });
        viewHolder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - lastPressedTime < 200L) {
                    return;
                }
                lastPressedTime = System.currentTimeMillis();
                int position = (int) v.getTag();
                Motion motion = getItem(position);
                if (TextUtils.equals(motionPlayer.getPlayMotionId(), motion.id)) {
                    motionPlayer.stopMotion();
                } else {
                    motionPlayer.playMotion(motion);
                }
            }
        });
        viewHolder.invalidArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //无效点击区域，使得不要误触发选中
            }
        });
    }

    @Override
    public int getItemCount() {
        return motions == null ? 0 : motions.size();
    }

    public Motion getItem(int position) {
        return motions.get(position);
    }

    public static class SettingItemViewHolder extends RecyclerView.ViewHolder {
        private SettingItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.project_motion_list_item_name_tv);
            time = itemView.findViewById(R.id.project_motion_list_item_time_tv);
            mark = itemView.findViewById(R.id.project_motion_list_item_name_mark);
            play = itemView.findViewById(R.id.project_motion_list_item_play_btn);
            invalidArea = itemView.findViewById(R.id.project_motion_list_item_invalid_area);
        }

        private final TextView name;
        private final TextView time;
        private final ImageView mark;
        private final Button play;
        private final View invalidArea;
    }

}
