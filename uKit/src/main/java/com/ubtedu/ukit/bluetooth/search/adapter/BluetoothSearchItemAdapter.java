package com.ubtedu.ukit.bluetooth.search.adapter;

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

/**
 * @Author naOKi
 * @Date 2018/11/19
 **/
public class BluetoothSearchItemAdapter extends BaseBluetoothSearchItemAdapter<BluetoothSearchItemAdapter.BluetoothDeviceViewHolder, BluetoothSearchItemAdapter.BluetoothSearchResult> {

    @Override
    public void doAdd(URoUkitBluetoothInfo deviceInfo) {
        /**
         * 信号强度标准，参考的https://www.netspotapp.com/cn/what-is-rssi-level.html
         * rssi数值钟负值越大，信号强度越弱
         * 信号强度 <= -100，信号强度=-100（几乎不可用）
         * 信号强度 >= -50，信号强度=-50（信号很强）
         * 分别用0~3级信号强度表示，数值越小，强度越高
         * <-90区间，信号非常弱，级别位3
         * <-80区间，信号弱，级别位2
         * <-70区间，信号一般，级别位1
         * 其他区间，信号强，级别位0
         **/
//        if(device != null) {
//            LogUtils.e("%s[%s]: %d", device.getName(), device.getAddress(), rssi);
//        }
        int level;
            /*if (rssi >= -50) {
                level = 0;
            } else if (rssi >= -60) {
                level = 1;
            } else if (rssi >= -70) {
                level = 2;
            } else {
                level = 3;
            }*/
        if (deviceInfo.rssi < -90) {
            level = 3;
        } else if (deviceInfo.rssi < -80) {
            level = 2;
        } else if (deviceInfo.rssi < -70) {
            level = 1;
        } else {
            level = 0;
        }
        URoBluetoothDevice device = deviceInfo.device;
        String name = BluetoothHelper.formatDeviceName(device.getName());
//            if(!TextUtils.isEmpty(device.getName()) && device.getName().startsWith("uKit2_")) {
//                name = device.getName();
//            } else if(TextUtils.equals("jimu", device.getName().toLowerCase())) {
//                name = "uKit";//格式化名字 Jimu -> uKit
//            } else {
//                name = "uKit_" + device.getName().substring(5).toUpperCase();//格式化名字 Jimu_XXXX -> uKit_XXXX
//            }
        BluetoothSearchResult result = new BluetoothSearchResult(deviceInfo, name, level);
        int index = items.indexOf(result);
        if (index >= 0) {
            BluetoothSearchResult item = items.get(index);
            item.level = result.level;
        } else {
            items.add(result);
        }
//            RcLogUtils.e("%s, %d", result.name, result.level);
        apply();
    }

    @NonNull
    @Override
    public BluetoothDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_bluetooth_search, viewGroup, false);
        BluetoothDeviceViewHolder vh = new BluetoothDeviceViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothDeviceViewHolder viewHolder, int i) {
        BluetoothSearchResult item = getItem(i);
        viewHolder.name.setText(item.getName());
        viewHolder.image.setImageLevel(item.getLevel());
        viewHolder.itemView.setTag(i);
        if (getItemClickListener() != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getItemClickListener() != null) {
                        getItemClickListener().onBluetoothItemClick(v, (int) v.getTag());
                    }
                }
            });
        }
    }

    public static class BluetoothDeviceViewHolder extends RecyclerView.ViewHolder {
        private BluetoothDeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.item_bluetooth_search_name_tv);
            this.image = itemView.findViewById(R.id.item_bluetooth_search_signal_iv);
        }

        private TextView name;
        private ImageView image;
    }

    public static class BluetoothSearchResult extends BaseBluetoothSearchItemAdapter.BluetoothSearchResult {
        private int level;

        public BluetoothSearchResult(URoUkitBluetoothInfo device, String name, int level) {
            super(device, name);
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }

}
