package com.ubtedu.ukit.bluetooth.wifi;

import android.view.View;

import com.ubtedu.deviceconnect.libs.ukit.connector.URoUkitBluetoothInfo;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.bluetooth.dialog.BatchWifiConfigDialogFragment;
import com.ubtedu.ukit.bluetooth.interfaces.IBluetoothItemClickListener;
import com.ubtedu.ukit.bluetooth.search.BaseBluetoothSearchActivity;
import com.ubtedu.ukit.bluetooth.search.BaseBluetoothSearchPresenter;
import com.ubtedu.ukit.bluetooth.wifi.adapter.BatchWifiConfigDeviceItemAdapter;
import com.ubtedu.ukit.bluetooth.wifi.adapter.BatchWifiConfigResultItemAdapter;

import java.util.ArrayList;

/**
 * @Author naOKi
 * @Date 2018/11/19
 **/
public class BatchWifiConfigActivity extends BaseBluetoothSearchActivity implements IBluetoothItemClickListener {

    private BatchWifiConfigDeviceItemAdapter mBluetoothDeviceItemAdapter;
    private BatchWifiConfigResultItemAdapter mWifiConfigResultItemAdapter;

    @Override
    protected void init() {
        mMobileWifiArea.setVisibility(View.VISIBLE);
        mBluetoothDeviceItemAdapter = new BatchWifiConfigDeviceItemAdapter();
        mWifiConfigResultItemAdapter = new BatchWifiConfigResultItemAdapter();
        mWifiConfigResultItemAdapter.setItemClickListener(this);
        setAdapters(mBluetoothDeviceItemAdapter, mWifiConfigResultItemAdapter);
        String ssid = getPresenter().getMobileWifiSSid();
        mCurrentMobileWifiNameTv.setText(ssid == null ? "" : ssid);
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mWifiConfigBtn) {
            if (getString(R.string.batch_config_wifi_start_config_btn).equals(mWifiConfigBtn.getText().toString())) {
                ArrayList<URoUkitBluetoothInfo> checkList = mBluetoothDeviceItemAdapter.getCheckDeviceList();
                if (checkList == null || checkList.size() == 0) {
                    getUIDelegate().toastShort(getString(R.string.batch_config_wifi_choose_device_none_msg));
                    return;
                }
                PasswordInputDialog mPasswordInputDialog = new PasswordInputDialog();
                mPasswordInputDialog.setTitleText(getString(R.string.batch_config_wifi_input_password_dialog_title));
                mPasswordInputDialog.ignoreTextEmpty(true);
                mPasswordInputDialog.setOnConfirmClickListener(new PasswordInputDialog.OnConfirmClickListener() {
                    @Override
                    public boolean confirm(String text) {
                        getPresenter().batchConfigWifi(checkList, mCurrentMobileWifiNameTv.getText().toString(), text);
                        return false;
                    }
                });
                mPasswordInputDialog.show(getSupportFragmentManager(), "input_wifi_password");
            } else {
                mBluetoothDeviceRecyclerView.setVisibility(View.VISIBLE);
                mWifiConfigResultRecyclerView.setVisibility(View.GONE);
                mWifiConfigResultItemAdapter.clear();
                mWifiConfigBtn.setText(R.string.batch_config_wifi_start_config_btn);
            }
        }
    }

    @Override
    public void onBluetoothItemClick(View view, int position) {
        if (!mWifiConfigResultItemAdapter.isConfigSuccess(position)) {
            getUIDelegate().toastShort(getString(R.string.batch_config_wifi_failed_msg));
        }
    }

    @Override
    protected BaseBluetoothSearchPresenter createPresenter() {
        return new BatchWifiConfigPresenter();
    }

    @Override
    protected BatchWifiConfigUI createUIView() {
        return new BatchWifiConfigUI();
    }

    class BatchWifiConfigUI extends BaseBluetoothSearchUI {
        private BatchWifiConfigDialogFragment dialogFragment;

        @Override
        public void updateStartSearchUkitRobotView() {
            super.updateStartSearchUkitRobotView();
            mWifiConfigResultRecyclerView.setVisibility(View.GONE);
            mWifiConfigBtn.setVisibility(View.GONE);
        }

        @Override
        public void startConfigWifi() {
            mBluetoothDeviceRecyclerView.setVisibility(View.GONE);
            mWifiConfigResultRecyclerView.setVisibility(View.VISIBLE);
            mWifiConfigBtn.setText(R.string.batch_config_wifi_choose_device_btn);
            dialogFragment = BatchWifiConfigDialogFragment.newBuilder().build();
            dialogFragment.show(getSupportFragmentManager(), "show_batch_wifi_dialog");
        }

        @Override
        public void updateStopSearchUkitRobotView() {
            super.updateStopSearchUkitRobotView();
            mWifiConfigBtn.setText(R.string.batch_config_wifi_start_config_btn);
            mWifiConfigBtn.setVisibility(mBluetoothDeviceItemAdapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
            mSearchText.setVisibility(View.GONE);
        }

        @Override
        public void updateConfigProgress(int progress) {
            if (dialogFragment != null) {
                dialogFragment.setProgress(progress);
            }
        }

        @Override
        public void updateConfigResult(ArrayList<Boolean> configResultList) {
            ArrayList<URoUkitBluetoothInfo> checkedDevices = mBluetoothDeviceItemAdapter.getCheckDeviceList();
            ArrayList<BatchWifiConfigResultItemAdapter.ConfigResult> results = new ArrayList<>();
            for (int i = 0; i < checkedDevices.size(); i++) {
                BatchWifiConfigResultItemAdapter.ConfigResult result = new BatchWifiConfigResultItemAdapter.ConfigResult(checkedDevices.get(i).device.getName(), configResultList.size() > i ? configResultList.get(i) : false);
                results.add(result);
            }
            mWifiConfigResultItemAdapter.setItems(results);
        }

        @Override
        public void onWifiPasswordWrong() {
            getUIDelegate().toastShort(getString(R.string.bluetooth_wifi_connect_fail_password_error));
            if (dialogFragment != null) {
                dialogFragment.dismiss();
            }
        }
    }

}
