package com.ubtedu.ukit.bluetooth.search;

import android.os.Bundle;
import android.view.View;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.connect.BluetoothConnectActivity;
import com.ubtedu.ukit.bluetooth.error.BluetoothCommonErrorHelper;
import com.ubtedu.ukit.bluetooth.interfaces.IBluetoothItemClickListener;
import com.ubtedu.ukit.bluetooth.ota.OtaHelper;
import com.ubtedu.ukit.bluetooth.search.adapter.BluetoothSearchItemAdapter;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;

/**
 * @Author naOKi
 * @Date 2018/11/19
 **/
public class BluetoothSearchActivity extends BaseBluetoothSearchActivity implements IBluetoothItemClickListener {

    private boolean showUpgradeIdentity = true;
    private boolean showWiFiConfigIdentity = true;

    public static final String EXTRA_SHOW_UPGRADE_IDENTITY = "_extra_show_upgrade_identity";
    public static final String EXTRA_SHOW_WIFI_CONFIG_IDENTITY = "_extra_show_wifi_config_identity";

    private BluetoothSearchItemAdapter mBluetoothDeviceItemAdapter;

    @Override
    protected void initArguments(Bundle args) {
        if (args != null) {
            showUpgradeIdentity = args.getBoolean(EXTRA_SHOW_UPGRADE_IDENTITY, true);
            showWiFiConfigIdentity = args.getBoolean(EXTRA_SHOW_WIFI_CONFIG_IDENTITY, true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_SHOW_UPGRADE_IDENTITY, showUpgradeIdentity);
        outState.putBoolean(EXTRA_SHOW_WIFI_CONFIG_IDENTITY, showWiFiConfigIdentity);
    }

    @Override
    protected void init() {
        mBluetoothDeviceItemAdapter = new BluetoothSearchItemAdapter();
        mBluetoothDeviceItemAdapter.setItemClickListener(this);
        setAdapters(mBluetoothDeviceItemAdapter, null);
    }

    @Override
    protected BaseBluetoothSearchContracts.Presenter createPresenter() {
        return new BluetoothSearchPresenter();
    }

    @Override
    protected BaseBluetoothSearchContracts.UI createUIView() {
        return new BluetoothSearchUI();
    }

    private void showConnectFailureDialog() {
        PromptDialogFragment.newBuilder(this)
                .type(PromptDialogFragment.Type.NORMAL)
                .title(getString(R.string.bluetooth_connect_failure))
                .message(getString(R.string.bluetooth_connect_failure_msg))
                .positiveButtonText(getString(R.string.bluetooth_connect_failure_btn_text))
                .showNegativeButton(false)
                .cancelable(true)
                .build()
                .show(getSupportFragmentManager(), "connect_failure");
    }

    private void showVersionDeprecatedDialog() {
        PromptDialogFragment.newBuilder(this)
                .type(PromptDialogFragment.Type.NORMAL)
                .title(getString(R.string.bluetooth_deprecated_board_dialog_title))
                .message(getString(R.string.bluetooth_deprecated_board_dialog_message))
                .positiveButtonText(getString(R.string.bluetooth_connect_failure_btn_text))
                .showNegativeButton(false)
                .cancelable(true)
                .build()
                .show(getSupportFragmentManager(), "connect_failure");
    }

    private void showVersionErrorDialog() {
        PromptDialogFragment.newBuilder(this)
                .type(PromptDialogFragment.Type.NORMAL)
                .title(getString(R.string.bluetooth_error_mcu_board_dialog_title))
                .message(getString(R.string.bluetooth_error_mcu_board_dialog_message))
                .positiveButtonText(getString(R.string.bluetooth_connect_failure_btn_text))
                .showNegativeButton(false)
                .cancelable(true)
                .build()
                .show(getSupportFragmentManager(), "connect_failure");
    }

    private void entryConnectActivity() {
        BluetoothConnectActivity.entryBluetoothConnectActivity(this, BluetoothConnectActivity.ENTRY_MODE_PREPARE, showUpgradeIdentity, showWiFiConfigIdentity);
        finish();
    }

    @Override
    public void onBluetoothItemClick(View view, int position) {
        BluetoothSearchItemAdapter.BluetoothSearchResult searchResult = mBluetoothDeviceItemAdapter.getItem(position);
        getPresenter().connectUkitRobot(searchResult.getName(), searchResult.getDevice());
    }

    class BluetoothSearchUI extends BaseBluetoothSearchUI {

        @Override
        public void updateStartConnectUkitRobotView(String name) {
            BluetoothSearchActivity.this.getUIDelegate().showLoading(false, getString(R.string.bluetooth_connecting_device, name));
        }

        @Override
        public void updateConnectResult(boolean isSuccess) {
            getUIDelegate().hideLoading();
            if (isSuccess) {
                if (OtaHelper.isDeprecatedBoardVersion()) {
                    showVersionDeprecatedDialog();
                    BluetoothHelper.disconnect();
                    return;
                }
                if (OtaHelper.isBoardVersionInfoError()) {
                    showVersionErrorDialog();
                    BluetoothHelper.disconnect();
                    return;
                }
                if (BluetoothHelper.isEmptyBattery()) {
                    BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.EMPTY_POWER);
                    BluetoothHelper.disconnect();
                    return;
                }
                entryConnectActivity();
            } else {
                showConnectFailureDialog();
            }
        }
    }

}
