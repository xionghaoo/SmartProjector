package com.ubtedu.ukit.bluetooth.wifi.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiAuthMode;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiScanApInfo;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiStatusInfo;
import com.ubtedu.ukit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author naOKi
 * @Date 2018/11/19
 **/
public class WifiConfigItemAdapter extends RecyclerView.Adapter<WifiConfigItemAdapter.WifiViewHolder> {

    private ArrayList<URoWiFiScanApInfo> items;
    private IWifiItemClickListener itemClickListener;

    public synchronized URoWiFiStatusInfo getCurrentConnectWifi() {
        return currentConnectWifi;
    }

    public void setCurrentConnectWifi(URoWiFiStatusInfo wifiInfo) {
        synchronized (this) {
            this.currentConnectWifi = wifiInfo;
        }
        sortListByConnect();
        apply();
    }

    private URoWiFiStatusInfo currentConnectWifi;

    public WifiConfigItemAdapter() {
        this.items = new ArrayList<>();
    }

    public void setItemClickListener(IWifiItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public IWifiItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void addAll(List<URoWiFiScanApInfo> wifiList) {
        clear();
        this.items.addAll(wifiList);
        sortListByConnect();
        apply();
    }

    public void clear() {
        items.clear();
        apply();
    }

    public URoWiFiScanApInfo getItem(int i) {
        if (i < 0 || i >= items.size()) {
            return null;
        }
        return items.get(i);
    }

    private void sortListByConnect() {
        URoWiFiStatusInfo currentConnectWifi = getCurrentConnectWifi();
        if (currentConnectWifi == null || TextUtils.isEmpty(currentConnectWifi.getSsid()) || items.isEmpty()) {
            return;
        }
        ArrayList<URoWiFiScanApInfo> removeList = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (currentConnectWifi.getSsid().equals(items.get(i).getSsid())) {
                removeList.add(items.get(i));
            }
        }
        if (removeList.size() > 0) {
            URoWiFiScanApInfo connectedWifiInfo = removeList.get(0);
            items.removeAll(removeList);
            items.add(0, connectedWifiInfo);
        }
    }

    public int getLevel(int rssi) {
        //todo 信号强度规则待确定
        int level;
        if (rssi >= -55) {
            level = 4;
        } else if (rssi >= -67) {
            level = 3;
        } else if (rssi >= -78) {
            level = 2;
        } else if (rssi >= -88) {
            level = 1;
        } else {
            level = 0;
        }
        return level;
    }

    public boolean isConnect(URoWiFiScanApInfo wifiInfo) {
        if (currentConnectWifi == null) {
            return false;
        }
        synchronized (this) {
            if (currentConnectWifi == null) {
                return false;
            }
            if (TextUtils.equals(currentConnectWifi.getSsid(), wifiInfo.getSsid())) {
                return true;
            }
            return false;
        }
    }

    private void apply() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WifiViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_wifi_info_list, viewGroup, false);
        WifiViewHolder vh = new WifiViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull WifiViewHolder viewHolder, int i) {
        URoWiFiScanApInfo item = items.get(i);
        viewHolder.nameTv.setText(item.getSsid());
        viewHolder.signalIv.setImageLevel(getLevel(item.getRssi()));
        viewHolder.lockIv.setVisibility(item.getAuthMode() == URoWiFiAuthMode.NONE ? View.GONE : View.VISIBLE);
        viewHolder.connectIv.setVisibility(isConnect(item) ? View.VISIBLE : View.GONE);
        viewHolder.itemView.setTag(i);
        if (itemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onWifiItemClick(v, (int) v.getTag());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class WifiViewHolder extends RecyclerView.ViewHolder {
        private WifiViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nameTv = itemView.findViewById(R.id.item_wifi_name_tv);
            this.signalIv = itemView.findViewById(R.id.item_wifi_signal_iv);
            this.lockIv = itemView.findViewById(R.id.item_wifi_lock_iv);
            this.connectIv = itemView.findViewById(R.id.item_wifi_connect_iv);
        }

        private TextView nameTv;
        private ImageView signalIv;
        private ImageView lockIv;
        private ImageView connectIv;
    }

    public interface IWifiItemClickListener {
        void onWifiItemClick(View view, int position);
    }
}
