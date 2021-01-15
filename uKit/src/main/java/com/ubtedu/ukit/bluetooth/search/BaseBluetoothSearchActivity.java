package com.ubtedu.ukit.bluetooth.search;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.ubtedu.deviceconnect.libs.ukit.connector.URoUkitBluetoothInfo;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.search.adapter.BaseBluetoothSearchItemAdapter;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;
import com.ubtedu.ukit.common.ota.AppMarketUtils;
import com.ubtedu.ukit.bluetooth.utils.LocationServiceHelper;
import com.ubtedu.ukit.menu.settings.TargetDevice;
import com.ubtedu.ukit.permission.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseBluetoothSearchActivity extends UKitBaseActivity<BaseBluetoothSearchContracts.Presenter, BaseBluetoothSearchContracts.UI> {
    protected RecyclerView mBluetoothDeviceRecyclerView;
    protected RecyclerView mWifiConfigResultRecyclerView;
    private BaseBluetoothSearchItemAdapter mBluetoothSearchItemAdapter;
    protected Button mSearchBtn;
    protected ImageView mExitBtn;
    protected TextView mSearchText;
    protected TextView mSearchCheckBluetoothText;
    protected LottieAnimationView mSearchAnim;
    protected ImageView mEmptyIcon;
    protected View mIconArea;
    protected TextView mCurrentMobileWifiNameTv;
    protected View mMobileWifiArea;
    protected Button mWifiConfigBtn;

    private boolean shouldShowOpenDialog = false;
    private boolean shouldShowOpenLocationServiceDialog = false;
    public static final int REQUEST_PERMISSION_CODE = 38;
    public static final int ON_CREATE_REQUEST_PERMISSION_CODE = 39;
    private static final String[] mRequiredPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE||requestCode == ON_CREATE_REQUEST_PERMISSION_CODE) {
            List<String> deniedPermissions = PermissionUtil.getDeniedPermissions(permissions, grantResults);
            if (permissions.length != 0 && deniedPermissions.size() == 0) {
                startSearchUkitRobot(requestCode == ON_CREATE_REQUEST_PERMISSION_CODE);
            } else {
                PermissionUtil.showDeniedDialog(this, deniedPermissions, REQUEST_PERMISSION_CODE, new PermissionUtil.OnClickDeniedDialogListener() {
                    @Override
                    public void onCloseBtnClick() {
                        finish();
                    }
                });
            }
        }
    }



    private void startSearchUkitRobot(boolean invokeOnCreate) {
        if (!PermissionUtil.isPermissionsGranted(this, mRequiredPermissions)) {
            PermissionUtil.requestPermissions(this, mRequiredPermissions, invokeOnCreate?ON_CREATE_REQUEST_PERMISSION_CODE:REQUEST_PERMISSION_CODE);
            return;
        }
        if (!BluetoothHelper.isEnabled()) {
            if (invokeOnCreate) {
                shouldShowOpenDialog = true;
            } else {
                showOpenBluetoothDialog();
            }
            return;
        }

        if (!LocationServiceHelper.isCompactLocationServiceAvailable(this)) {
            if (invokeOnCreate) {
                shouldShowOpenLocationServiceDialog = true;
            } else {
                showOpenLocationServiceDialog();
            }
            return;
        }
        getPresenter().startSearchUkitRobot();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shouldShowOpenDialog) {
            shouldShowOpenDialog = false;
            showOpenBluetoothDialog();
            return;
        }
        if (shouldShowOpenLocationServiceDialog) {
            shouldShowOpenLocationServiceDialog = false;
            showOpenLocationServiceDialog();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            initArguments(savedInstanceState);
        } else {
            initArguments(getIntent().getExtras());
        }
        setContentView(R.layout.activity_bluetooth_search);
        initView();
        init();
        startSearchUkitRobot(true);
    }

    protected abstract void init();

    public void setAdapters(BaseBluetoothSearchItemAdapter deviceAdapter, RecyclerView.Adapter wifiConfigAdapter) {
        mBluetoothSearchItemAdapter = deviceAdapter;
        mBluetoothDeviceRecyclerView.setAdapter(mBluetoothSearchItemAdapter);
        if (wifiConfigAdapter != null) {
            mWifiConfigResultRecyclerView.setAdapter(wifiConfigAdapter);
        }
    }

    protected void initArguments(Bundle args) {
    }

    private void initView() {
        mBluetoothDeviceRecyclerView = findViewById(R.id.bluetooth_search_result_list);
        mBluetoothDeviceRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mBluetoothDeviceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWifiConfigResultRecyclerView = findViewById(R.id.bluetooth_search_batch_config_wifi_result_list);
        mWifiConfigResultRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mWifiConfigResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchText = findViewById(R.id.bluetooth_search_icon_tv);
        mSearchCheckBluetoothText = findViewById(R.id.bluetooth_search_icon_bluetooth_check_tv);
        mSearchAnim = findViewById(R.id.bluetooth_search_anim_iv);
        mEmptyIcon = findViewById(R.id.bluetooth_search_empty_iv);
        mIconArea = findViewById(R.id.bluetooth_search_icon_area);
        mSearchBtn = findViewById(R.id.bluetooth_search_btn);
        bindSafeClickListener(mSearchBtn);
        mExitBtn = findViewById(R.id.bluetooth_search_exit_btn);
        bindSafeClickListener(mExitBtn);
        mWifiConfigBtn = findViewById(R.id.bluetooth_search_batch_config_wifi_choose_device_btn);
        bindSafeClickListener(mWifiConfigBtn);
        mMobileWifiArea = findViewById(R.id.bluetooth_search_current_mobile_wifi_name_area);
        mCurrentMobileWifiNameTv = findViewById(R.id.bluetooth_search_mobile_wifi_name_tv);
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        if (v == mSearchBtn) {
            if (TextUtils.equals(mSearchBtn.getText(), getString(R.string.bluetooth_search_cancel))) {
                getPresenter().stopSearchUkitRobot();
            } else {
                startSearchUkitRobot(false);
            }
        } else if (v == mExitBtn) {
            finish();
        }
    }

    private void showOpenBluetoothDialog() {
        PromptDialogFragment.newBuilder(this)
                .type(PromptDialogFragment.Type.NORMAL)
                .title(getString(R.string.bluetooth_connect_open_bluetooth_title))
                .message(getString(R.string.bluetooth_connect_open_bluetooth_msg))
                .positiveButtonText(getString(R.string.bluetooth_connect_confirm_btn))
                .onPositiveClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);/* android.settings.BLUETOOTH_SETTINGS */
                        if (AppMarketUtils.isIntentAvailable(BaseBluetoothSearchActivity.this, intent)) {
                            BaseBluetoothSearchActivity.this.startActivity(intent);
                        } else {
                            intent = new Intent(Settings.ACTION_SETTINGS);
                            if (AppMarketUtils.isIntentAvailable(BaseBluetoothSearchActivity.this, intent)) {
                                BaseBluetoothSearchActivity.this.startActivity(intent);
                            }
                        }
                    }
                })
                .showNegativeButton(false)
                .cancelable(true)
                .build()
                .show(getSupportFragmentManager(), "open_bluetooth");
    }

    private void showOpenLocationServiceDialog() {
        LocationServiceHelper.openLocationServiceDialog(this);
    }

    public class BaseBluetoothSearchUI extends BaseBluetoothSearchContracts.UI {
        @Override
        public void updateStartSearchUkitRobotView() {
            mSearchBtn.setText(R.string.bluetooth_search_cancel);
            mSearchBtn.setBackgroundResource(R.drawable.selector_bluetooth_search_cancel_btn);
            mBluetoothDeviceRecyclerView.setVisibility(View.GONE);
            mBluetoothSearchItemAdapter.clear();
            mIconArea.setVisibility(View.VISIBLE);
            mEmptyIcon.setVisibility(View.GONE);
            mSearchText.setVisibility(View.VISIBLE);
            mSearchText.setText(R.string.bluetooth_searching);
            mSearchCheckBluetoothText.setVisibility(View.GONE);
            mSearchAnim.setVisibility(View.VISIBLE);
            mSearchAnim.playAnimation();
        }

        @Override
        public void updateStopSearchUkitRobotView() {
            mSearchBtn.setBackgroundResource(R.drawable.selector_bluetooth_search_btn);
            if (mBluetoothSearchItemAdapter.getItemCount() == 0) {
                mSearchBtn.setText(R.string.bluetooth_search_click);
                mIconArea.setVisibility(View.VISIBLE);
                mEmptyIcon.setVisibility(View.VISIBLE);
                mSearchText.setVisibility(View.VISIBLE);
                boolean isUkit2 = com.ubtedu.ukit.menu.settings.Settings.getTargetDevice() == TargetDevice.UKIT2;
                int currentDeviceName = isUkit2 ? R.string.bluetooth_search_name_ukit2 : R.string.bluetooth_search_name_ukit1;
                int changeDeviceName = isUkit2 ? R.string.bluetooth_search_name_ukit1 : R.string.bluetooth_search_name_ukit2;
                mSearchText.setText(getString(R.string.bluetooth_search_not_found_change_device_msg, getString(currentDeviceName), getString(changeDeviceName)));
                mSearchCheckBluetoothText.setVisibility(View.VISIBLE);
            } else {
                mSearchText.setVisibility(View.GONE);
                mSearchCheckBluetoothText.setVisibility(View.GONE);
                mSearchBtn.setText(R.string.bluetooth_search);
                mIconArea.setVisibility(View.GONE);
            }
            mSearchAnim.setVisibility(View.GONE);
            mSearchAnim.pauseAnimation();
        }

        @Override
        public void addNewFindDevice(URoUkitBluetoothInfo device) {
            mBluetoothSearchItemAdapter.add(device);
            if (mBluetoothDeviceRecyclerView.getVisibility() != View.VISIBLE) {
                mBluetoothDeviceRecyclerView.setVisibility(View.VISIBLE);
                mIconArea.setVisibility(View.GONE);
                mSearchAnim.setVisibility(View.GONE);
                mSearchAnim.pauseAnimation();
            }
        }

        @Override
        public void updateStartConnectUkitRobotView(String name) {

        }

        @Override
        public void updateConnectResult(boolean isSuccess) {

        }

        @Override
        public void startConfigWifi() {

        }

        @Override
        public void updateConfigProgress(int progress) {

        }

        @Override
        public void updateConfigResult(ArrayList<Boolean> configResultList) {

        }

        @Override
        public void onWifiPasswordWrong() {

        }
    }

}
