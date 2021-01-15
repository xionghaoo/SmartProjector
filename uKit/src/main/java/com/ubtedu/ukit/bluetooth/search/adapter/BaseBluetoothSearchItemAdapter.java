package com.ubtedu.ukit.bluetooth.search.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.deviceconnect.libs.ukit.connector.URoUkitBluetoothInfo;
import com.ubtedu.ukit.bluetooth.interfaces.IBluetoothItemClickListener;

import java.util.ArrayList;

public abstract class BaseBluetoothSearchItemAdapter<H extends RecyclerView.ViewHolder, R extends BaseBluetoothSearchItemAdapter.BluetoothSearchResult> extends RecyclerView.Adapter<H> {
    protected final Object modifyLock = new Object();
    protected ArrayList<R> items;
    private IBluetoothItemClickListener itemClickListener;

    public BaseBluetoothSearchItemAdapter() {
        this.items = new ArrayList<>();
    }

    public void setItemClickListener(IBluetoothItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public IBluetoothItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void clear() {
        synchronized (modifyLock) {
            items.clear();
            apply();
        }
    }

    public void apply() {
        notifyDataSetChanged();
    }

    public R getItem(int i) {
        synchronized (modifyLock) {
            if (i < 0 || i >= items.size()) {
                return null;
            }
            return items.get(i);
        }
    }

    public void add(URoUkitBluetoothInfo deviceInfo) {
        synchronized (modifyLock) {
            doAdd(deviceInfo);
        }
    }

    protected abstract void doAdd(URoUkitBluetoothInfo deviceInfo);

    @Override
    public int getItemCount() {
        synchronized (modifyLock) {
            return items != null ? items.size() : 0;
        }
    }

    public static class BluetoothSearchResult {
        private URoUkitBluetoothInfo device;
        private String name;

        public BluetoothSearchResult(URoUkitBluetoothInfo device, String name) {
            this.device = device;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BaseBluetoothSearchItemAdapter.BluetoothSearchResult)) return false;

            BaseBluetoothSearchItemAdapter.BluetoothSearchResult that = (BaseBluetoothSearchItemAdapter.BluetoothSearchResult) o;

            return device.equals(that.device);
        }

        @Override
        public int hashCode() {
            return device.hashCode();
        }

        public URoUkitBluetoothInfo getDevice() {
            return device;
        }

        public String getName() {
            return name;
        }

    }
}
