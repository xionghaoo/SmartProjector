package com.ubtedu.ukit.bluetooth.wifi.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.bluetooth.interfaces.IBluetoothItemClickListener;

import java.util.ArrayList;

/**
 * @Author naOKi
 * @Date 2018/11/19
 **/
public class BatchWifiConfigResultItemAdapter extends RecyclerView.Adapter<BatchWifiConfigResultItemAdapter.BatchWifiConfigResultViewHolder> {

    private ArrayList<ConfigResult> items;
    private IBluetoothItemClickListener itemClickListener;

    public BatchWifiConfigResultItemAdapter() {
        this.items = new ArrayList<>();
    }

    public void setItemClickListener(IBluetoothItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public IBluetoothItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void clear() {
        items.clear();
        apply();
    }

    private void apply() {
        notifyDataSetChanged();
    }

    public void setItems(ArrayList<ConfigResult> list) {
        items.clear();
        items.addAll(list);
        apply();
    }

    public boolean isConfigSuccess(int position) {
        if (items == null || items.size() == 0 || items.size() <= position) {
            return false;
        }
        return items.get(position).isSuccess;
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }

    @NonNull
    @Override
    public BatchWifiConfigResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_batch_wifi_config_result, viewGroup, false);
        BatchWifiConfigResultViewHolder vh = new BatchWifiConfigResultViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull BatchWifiConfigResultViewHolder viewHolder, int i) {
        ConfigResult item = items.get(i);
        viewHolder.completeName.setText(item.name);
        viewHolder.completeName.setTextColor(Color.parseColor(item.isSuccess ? "#60D064" : "#FE6F7B"));
        viewHolder.completeImage.setVisibility(item.isSuccess ? View.VISIBLE : View.INVISIBLE);
        viewHolder.itemView.setTag(i);
        if (itemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onBluetoothItemClick(v, (int) v.getTag());
                    }
                }
            });
        }
    }

    public static class BatchWifiConfigResultViewHolder extends RecyclerView.ViewHolder {
        private BatchWifiConfigResultViewHolder(@NonNull View itemView) {
            super(itemView);
            this.completeName = itemView.findViewById(R.id.item_batch_wifi_config_mainboard_complete_name_tv);
            this.completeImage = itemView.findViewById(R.id.item_batch_wifi_config_mainboard_complete_iv);

        }

        private TextView completeName;
        private ImageView completeImage;
    }

    public static class ConfigResult {
        private String name;
        private boolean isSuccess;

        public ConfigResult(String name, boolean isSuccess) {
            this.name = name;
            this.isSuccess = isSuccess;
        }
    }

}
