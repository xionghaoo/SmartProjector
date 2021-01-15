package com.ubtedu.ukit.bluetooth.wifi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.ubtedu.alpha1x.core.ActivityStack;
import com.ubtedu.alpha1x.core.base.fragment.OnDialogFragmentDismissListener;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiAuthMode;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiScanApInfo;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiStatusInfo;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.connect.BluetoothConnectActivity;
import com.ubtedu.ukit.bluetooth.search.BluetoothSearchActivity;
import com.ubtedu.ukit.bluetooth.wifi.adapter.WifiConfigItemAdapter;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;
import com.ubtedu.ukit.project.bridge.api.ToastHelper;

import java.util.List;

public class WifiConfigActivity extends UKitBaseActivity<WifiConfigContracts.Presenter, WifiConfigContracts.UI> implements WifiConfigItemAdapter.IWifiItemClickListener {
    private WifiConfigItemAdapter mWifiItemAdapter;
    private RecyclerView mWifiRecyclerView;
    private Button mSearchBtn;
    private Button mDisconnectBtn;
    private ImageView mExitBtn;
    private TextView mSearchText;
    private LottieAnimationView mSearchAnim;
    private ImageView mEmptyIcon;
    private View mIconArea;

    public static void entryWifiConfigActivity(Context context) {
        Intent intent = new Intent(context, WifiConfigActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPresenter().init();
    }

    @Override
    protected void onInitViews() {
        super.onInitViews();
        setContentView(R.layout.activity_wifi_config);
        mWifiItemAdapter = new WifiConfigItemAdapter();
        mWifiRecyclerView = findViewById(R.id.bluetooth_wifi_search_result_list);
        mWifiRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mWifiRecyclerView.setAdapter(mWifiItemAdapter);
        mWifiRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSearchText = findViewById(R.id.bluetooth_wifi_search_icon_tv);
        mSearchAnim = findViewById(R.id.bluetooth_wifi_search_anim_iv);
        mEmptyIcon = findViewById(R.id.bluetooth_wifi_search_empty_iv);
        mIconArea = findViewById(R.id.bluetooth_wifi_search_icon_area);

        mSearchBtn = findViewById(R.id.bluetooth_wifi_search_btn);
        bindSafeClickListener(mSearchBtn);

        mExitBtn = findViewById(R.id.bluetooth_wifi_config_exit_btn);
        bindSafeClickListener(mExitBtn);

        mDisconnectBtn = findViewById(R.id.bluetooth_wifi_disconnect_btn);
        bindSafeClickListener(mDisconnectBtn);

        mWifiItemAdapter.setItemClickListener(this);
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        if (v == mSearchBtn) {
            if (TextUtils.equals(mSearchBtn.getText(), getString(R.string.bluetooth_search_cancel))) {
                getPresenter().stopSearchWifi();
            } else {
                if (!BluetoothHelper.isBluetoothConnected()) {
                    showNotConnectDialog();
                    return;
                }
                getPresenter().searchWifiList();
            }
        } else if (v == mExitBtn) {
            finish();
        } else if (v == mDisconnectBtn) {
            if (!BluetoothHelper.isBluetoothConnected()) {
                showNotConnectDialog();
                return;
            }
            getPresenter().disconnectWifi(mWifiItemAdapter.getCurrentConnectWifi());
        }
    }

    @Override
    protected WifiConfigContracts.Presenter createPresenter() {
        return new WifiConfigPresenter();
    }

    @Override
    protected WifiConfigContracts.UI createUIView() {
        return new WifiConfigUI();
    }

    @Override
    public void onWifiItemClick(View view, int position) {
        if (!BluetoothHelper.isBluetoothConnected()) {
            showNotConnectDialog();
            return;
        }
        URoWiFiScanApInfo wifiInfo = mWifiItemAdapter.getItem(position);
        if (wifiInfo != null) {
            if (mWifiItemAdapter.isConnect(wifiInfo)) {
                return;
            }
            if (wifiInfo.getAuthMode() == URoWiFiAuthMode.NONE) {
                getUIDelegate().showLoading(false, getString(R.string.bluetooth_wifi_connecting));
                getPresenter().connectWifi(wifiInfo, "");
            } else {
                showPasswordInputDialog(wifiInfo, "");
            }
        }
    }

    private URoWiFiScanApInfo mLastConnectWiFiInfo;
    private String mLastConnectWiFiPassword;
    private PasswordInputDialog mPasswordInputDialog;

    private void showPasswordInputDialog(final URoWiFiScanApInfo wifiInfo, String initPassword) {
        if (mPasswordInputDialog != null && mPasswordInputDialog.isVisible()) {
            return;
        }
        this.mLastConnectWiFiPassword = initPassword;
        this.mLastConnectWiFiInfo = wifiInfo;
        mPasswordInputDialog = new PasswordInputDialog();
        mPasswordInputDialog.setOnConfirmClickListener(new PasswordInputDialog.OnConfirmClickListener() {
            @Override
            public boolean confirm(String text) {
                if (!BluetoothHelper.isBluetoothConnected()) {
                    ToastHelper.toastShort(getString(R.string.bluetooth_wifi_connect_fail));
                    return false;
                }
                mLastConnectWiFiPassword = text;
                getUIDelegate().showLoading(false, getString(R.string.bluetooth_wifi_connecting));
                getPresenter().connectWifi(wifiInfo, text);
                return false;
            }
        });
        mPasswordInputDialog.setDismissListener(new OnDialogFragmentDismissListener() {
            @Override
            public void onDismiss(Object... value) {
                mPasswordInputDialog = null;
            }
        });
        mPasswordInputDialog.setContentText(initPassword);
        mPasswordInputDialog.show(getSupportFragmentManager(), "input_wifi_password");
    }

    class WifiConfigUI extends WifiConfigContracts.UI {
        @Override
        public void startSearchWifi() {
            mDisconnectBtn.setVisibility(View.GONE);
            mSearchBtn.setText(R.string.bluetooth_search_cancel);
            mSearchBtn.setBackgroundResource(R.drawable.selector_bluetooth_search_cancel_btn);
            mWifiRecyclerView.setVisibility(View.GONE);
            mWifiItemAdapter.clear();
            mIconArea.setVisibility(View.VISIBLE);
            mEmptyIcon.setVisibility(View.GONE);
            mSearchText.setVisibility(View.VISIBLE);
            mSearchText.setText(R.string.bluetooth_searching);
            mSearchAnim.setVisibility(View.VISIBLE);
            mSearchAnim.playAnimation();
        }

        @Override
        public void stopSearchWifi() {
            mSearchBtn.setBackgroundResource(R.drawable.selector_bluetooth_search_btn);
            if (mWifiItemAdapter.getItemCount() == 0) {
                mWifiRecyclerView.setVisibility(View.GONE);
                mIconArea.setVisibility(View.VISIBLE);
                mEmptyIcon.setVisibility(View.VISIBLE);
                mSearchText.setVisibility(View.VISIBLE);
                mSearchText.setText(R.string.bluetooth_wifi_not_found);
                mDisconnectBtn.setVisibility(View.GONE);
            } else {
                mWifiRecyclerView.setVisibility(View.VISIBLE);
                mSearchText.setVisibility(View.GONE);
                mIconArea.setVisibility(View.GONE);
                if (mWifiItemAdapter.getCurrentConnectWifi() == null) {
                    mDisconnectBtn.setVisibility(View.GONE);
                } else {
                    mDisconnectBtn.setVisibility(View.VISIBLE);
                }
            }
            mSearchBtn.setText(R.string.bluetooth_search);
            mSearchAnim.setVisibility(View.GONE);
            mSearchAnim.pauseAnimation();
        }

        @Override
        public void updateWifiList(List<URoWiFiScanApInfo> wifiList) {
            mWifiItemAdapter.addAll(wifiList);
            stopSearchWifi();
        }

        @Override
        public void updateWifiStatus(URoWiFiStatusInfo wifiStatus) {
            if (wifiStatus != null && wifiStatus.getState() == URoWiFiStatusInfo.URoWiFiState.CONNECTED) {
                if (mWifiRecyclerView.getVisibility() == View.VISIBLE) {
                    mDisconnectBtn.setVisibility(View.VISIBLE);
                } else {
                    mDisconnectBtn.setVisibility(View.GONE);
                }
                mWifiItemAdapter.setCurrentConnectWifi(wifiStatus);
            } else {
                mDisconnectBtn.setVisibility(View.GONE);
                mWifiItemAdapter.setCurrentConnectWifi(null);
            }
        }

        @Override
        public void showWifiConnectResult(URoWiFiStatusInfo wifiStatus) {
            updateWifiStatus(wifiStatus);
            if (wifiStatus != null && wifiStatus.getState() == URoWiFiStatusInfo.URoWiFiState.CONNECTED) {
                ToastHelper.toastShort(getString(R.string.bluetooth_wifi_connect_success));
                mLastConnectWiFiInfo = null;
                mLastConnectWiFiPassword = null;
                if (BluetoothHelper.isConnected()) {
                    ActivityStack.finishActivity(BluetoothConnectActivity.class);
                }
                finish();
            } else {
                if (wifiStatus == null || URoWiFiStatusInfo.URoWiFiDisconnectReason.REASON_4WAY_HANDSHAKE_TIMEOUT != wifiStatus.getDisconnectReason()) {
                    ToastHelper.toastShort(getString(R.string.bluetooth_wifi_connect_fail));
                } else {
                    ToastHelper.toastShort(getString(R.string.bluetooth_wifi_connect_fail_password_error));
                    if (mLastConnectWiFiInfo != null) {
                        showPasswordInputDialog(mLastConnectWiFiInfo, mLastConnectWiFiPassword);
                    }
                }
            }
        }
    }

    private void showNotConnectDialog() {
        PromptDialogFragment.newBuilder(this)
                .type(PromptDialogFragment.Type.NORMAL)
                .title(getString(R.string.bluetooth_not_connect_title))
                .message(getString(R.string.bluetooth_not_connect_msg))
                .positiveButtonText(getString(R.string.bluetooth_connect_text))
                .negativeButtonText(getString(R.string.bluetooth_search_cancel))
                .onPositiveClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(WifiConfigActivity.this, BluetoothSearchActivity.class);
                        startActivity(intent);
                        ActivityStack.finishActivity(BluetoothConnectActivity.class);
                        finish();
                    }
                })
                .cancelable(false)
                .build()
                .show(getSupportFragmentManager(), "not_connect");
    }
}
