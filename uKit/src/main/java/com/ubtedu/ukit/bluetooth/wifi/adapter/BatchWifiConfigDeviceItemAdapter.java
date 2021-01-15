package com.ubtedu.ukit.bluetooth.wifi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.deviceconnect.libs.base.connector.URoBluetoothDevice;
import com.ubtedu.deviceconnect.libs.ukit.connector.URoUkitBluetoothInfo;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.search.adapter.BaseBluetoothSearchItemAdapter;

import java.util.ArrayList;

/**
 * @Author xinningduan
 * @Date 2020.02.27
 **/
public class BatchWifiConfigDeviceItemAdapter extends BaseBluetoothSearchItemAdapter<BatchWifiConfigDeviceItemAdapter.BluetoothDeviceViewHolder, BatchWifiConfigDeviceItemAdapter.BluetoothSearchResult> {
    public ArrayList<URoUkitBluetoothInfo> getCheckDeviceList() {
        ArrayList<URoUkitBluetoothInfo> list = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isCheck()) {
                list.add(items.get(i).getDevice());
            }
        }
        return list;
    }

    @Override
    public void doAdd(URoUkitBluetoothInfo deviceInfo) {
        URoBluetoothDevice device = deviceInfo.device;
        String name = BluetoothHelper.formatDeviceName(device.getName());
        BluetoothSearchResult result = new BluetoothSearchResult(deviceInfo, name);
        int index = items.indexOf(result);
        if (index < 0) {
            items.add(result);
        }
        apply();
    }

    @NonNull
    @Override
    public BluetoothDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_batch_wifi_config_device_check, viewGroup, false);
        BluetoothDeviceViewHolder vh = new BluetoothDeviceViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothDeviceViewHolder viewHolder, int i) {
        final BluetoothSearchResult item = getItem(i);
        viewHolder.name.setText(item.getName());
        viewHolder.checkBox.setSelected(item.isCheck());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.checkBox.setSelected(!item.isCheck());
                item.setCheck(!item.isCheck());
            }
        });
    }

    public static class BluetoothDeviceViewHolder extends RecyclerView.ViewHolder {
        private BluetoothDeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.item_batch_wifi_config_device_name_tv);
            this.checkBox = itemView.findViewById(R.id.item_batch_wifi_config_device_check_iv);
        }

        private TextView name;
        private ImageView checkBox;
    }

    public static class BluetoothSearchResult extends BaseBluetoothSearchItemAdapter.BluetoothSearchResult {
        private boolean isCheck;

        public BluetoothSearchResult(URoUkitBluetoothInfo device, String name) {
            super(device, name);
        }

        public void setCheck(boolean check) {
            isCheck = check;
        }

        public boolean isCheck() {
            return isCheck;
        }
    }

}
