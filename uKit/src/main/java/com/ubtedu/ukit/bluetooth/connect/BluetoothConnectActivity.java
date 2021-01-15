package com.ubtedu.ukit.bluetooth.connect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.component.URoSpeaker;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoMainBoardInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoSerialNumberInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoSpeakerInfo;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoNetworkState;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiStatusInfo;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.BtInvocationFactory;
import com.ubtedu.ukit.bluetooth.IUKitCommandResponse;
import com.ubtedu.ukit.bluetooth.connect.adapter.BluetoothConnectItemAdapter;
import com.ubtedu.ukit.bluetooth.connect.widget.PeripheralView;
import com.ubtedu.ukit.bluetooth.error.BluetoothCommonErrorHelper;
import com.ubtedu.ukit.bluetooth.interfaces.IBluetoothItemClickListener;
import com.ubtedu.ukit.bluetooth.search.BluetoothSearchActivity;
import com.ubtedu.ukit.bluetooth.wifi.WifiConfigActivity;
import com.ubtedu.ukit.common.analysis.Events;
import com.ubtedu.ukit.common.analysis.UBTReporter;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;
import com.ubtedu.ukit.project.vo.ModelInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author naOKi
 * @Date 2018/11/20
 **/
public class BluetoothConnectActivity extends UKitBaseActivity<BluetoothConnectContracts.Presenter, BluetoothConnectContracts.UI> implements IBluetoothItemClickListener {

    private BluetoothConnectItemAdapter mBluetoothConnectItemAdapter;

    private RecyclerView mConnectItemRecyclerView;
    private ImageView mExitBtn;
    private TextView mConnectHint;
    private Button mRecheckBtn;
    private Button mConfirmBtn;
    private Button mDisconnectBtn;
    private Button mWifiConfigBtn;

    private PeripheralView mBoardView;
    private TextView mBoardNameTv;
    private TextView mWifiNameTv;
    private ImageView mWifiIconIv;
    private LinearLayout mWifiIconArea;
    private LinearLayout mNeedWiFiConfigMessage;

    public static final int ENTRY_MODE_PREPARE = 1;
    public static final int ENTRY_MODE_CONNECTED = 2;

    public static final String EXTRA_ENTRY_MODE = "_extra_entry_mode";
    public static final String EXTRA_SHOW_UPGRADE_IDENTITY = "_extra_show_upgrade_identity";
    public static final String EXTRA_SHOW_WIFI_CONFIG_IDENTITY = "_extra_show_wifi_config_identity";

    private int mode;

    private boolean blockBtnEvent = false;

    private boolean isUpgradeAbort = false;

    private boolean showUpgradeIdentity = true;
    private boolean showWiFiConfigIdentity = true;

//    private boolean shouldShowAbnormalDialog = false;
//    private boolean shouldShowConflictDialog = false;

    private boolean shouldShowSpeakerDialog = false;

    private boolean shouldShowLowPowerDialog = false;

    public static void entryBluetoothConnectActivity(Context context, int mode, boolean showUpgradeIdentity, boolean showWiFiConfigIdentity) {
        Intent intent = new Intent(context, BluetoothConnectActivity.class);
        intent.putExtra(EXTRA_ENTRY_MODE, mode);
        intent.putExtra(EXTRA_SHOW_UPGRADE_IDENTITY, showUpgradeIdentity);
        intent.putExtra(EXTRA_SHOW_WIFI_CONFIG_IDENTITY, showWiFiConfigIdentity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            initArguments(savedInstanceState);
        } else {
            initArguments(getIntent().getExtras());
        }
        setContentView(R.layout.activity_bluetooth_connect);
        initView();
        initData(true);
    }

    private void initArguments(Bundle args) {
        if (args != null) {
            showUpgradeIdentity = args.getBoolean(EXTRA_SHOW_UPGRADE_IDENTITY, true);
            showWiFiConfigIdentity = args.getBoolean(EXTRA_SHOW_WIFI_CONFIG_IDENTITY, true);
            mode = getIntent().getIntExtra(EXTRA_ENTRY_MODE, ENTRY_MODE_PREPARE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_SHOW_UPGRADE_IDENTITY, showUpgradeIdentity);
        outState.putBoolean(EXTRA_SHOW_WIFI_CONFIG_IDENTITY, showWiFiConfigIdentity);
        outState.putInt(EXTRA_ENTRY_MODE, mode);
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        quitConnect();
//    }

    private void initView() {
        mBluetoothConnectItemAdapter = new BluetoothConnectItemAdapter();
        mBluetoothConnectItemAdapter.setShowIdentity(mode == ENTRY_MODE_PREPARE);
        mBluetoothConnectItemAdapter.setShowUpgradeable(showUpgradeIdentity);
        mBluetoothConnectItemAdapter.setItemClickListener(this);
        mConnectItemRecyclerView = findViewById(R.id.bluetooth_connect_item_list);
        mConnectItemRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mConnectItemRecyclerView.setAdapter(mBluetoothConnectItemAdapter);
        mConnectItemRecyclerView.setLayoutManager(new LinearLayoutManager(this, 0, false));

        mRecheckBtn = findViewById(R.id.bluetooth_connect_recheck_btn);
        bindSafeClickListener(mRecheckBtn);
        mConfirmBtn = findViewById(R.id.bluetooth_connect_confirm_btn);
        bindSafeClickListener(mConfirmBtn);
        mDisconnectBtn = findViewById(R.id.bluetooth_connect_disconnect_btn);
        bindSafeClickListener(mDisconnectBtn);
        mWifiConfigBtn = findViewById(R.id.bluetooth_wifi_config_btn);
        bindSafeClickListener(mWifiConfigBtn);
        mConnectHint = findViewById(R.id.bluetooth_connect_msg_tv);

        mBoardView = findViewById(R.id.bluetooth_connect_board_item);
        mBoardView.setAsMotherBoard("");
        mBoardView.setIdVisible(View.GONE);

        bindSafeClickListener(mBoardView);

        mWifiIconIv = findViewById(R.id.bluetooth_wifi_icon_iv);
        mWifiIconArea = findViewById(R.id.bluetooth_connect_wifi_area);
        mWifiNameTv = findViewById(R.id.bluetooth_connect_wifi_name_tv);
        mBoardNameTv = findViewById(R.id.bluetooth_connect_board_name_tv);
        mBoardNameTv.setText(BluetoothHelper.getDeviceName());
        bindSafeClickListener(mWifiIconArea);

        mExitBtn = findViewById(R.id.bluetooth_connect_exit_btn);
        bindSafeClickListener(mExitBtn);

        mNeedWiFiConfigMessage = findViewById(R.id.bluetooth_new_version_need_wifi_config_message_area);

        if (BluetoothHelper.isSmartVersion()) {
            mWifiConfigBtn.setVisibility(showWiFiConfigIdentity ? View.VISIBLE : View.GONE);
            mWifiIconArea.setVisibility(View.VISIBLE);
        } else {
            mWifiIconArea.setVisibility(View.GONE);
            mWifiConfigBtn.setVisibility(View.GONE);
        }

        if (mode == ENTRY_MODE_PREPARE) {
            mConnectHint.setText(R.string.bluetooth_connect_success_check_hint);
            mRecheckBtn.setVisibility(View.VISIBLE);
            mConfirmBtn.setVisibility(View.VISIBLE);
            mDisconnectBtn.setVisibility(View.GONE);
        } else {
            mConnectHint.setText(R.string.bluetooth_connect_success_hint);
            mRecheckBtn.setVisibility(View.GONE);
            mConfirmBtn.setVisibility(View.GONE);
            mDisconnectBtn.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        initData(false);
    }

    private void initData(boolean invokeOnCreate) {
        if (invokeOnCreate && mode == ENTRY_MODE_PREPARE) {
            if (BluetoothHelper.isLowBattery()) {
                shouldShowLowPowerDialog = true;
            }
        }
        setButtonBlocked(false);
        getPresenter().init();
        if (mode == ENTRY_MODE_PREPARE) {
            if (needShowUpgradeBtn() && !mBluetoothConnectItemAdapter.hasAbnormalItems() && !mBluetoothConnectItemAdapter.hasUnmatchItems()) {
                mConfirmBtn.setText(R.string.bluetooth_connect_upgrade_btn);
                if (BluetoothHelper.isSmartVersion() && !getPresenter().isBoardNetworkConnected()) {
                    mNeedWiFiConfigMessage.setVisibility(View.VISIBLE);
                } else {
                    mNeedWiFiConfigMessage.setVisibility(View.INVISIBLE);
                }
            } else {
                mConfirmBtn.setText(R.string.bluetooth_connect_confirm_btn);
                mNeedWiFiConfigMessage.setVisibility(View.INVISIBLE);
            }
            if (mBluetoothConnectItemAdapter.hasAbnormalItems()
                    || (isUpgradeBtnShowing() && BluetoothHelper.isSmartVersion() && !getPresenter().isBoardNetworkConnected())) {
                mConfirmBtn.setEnabled(false);
            } else {
                mConfirmBtn.setEnabled(true);
                if (showUpgradeIdentity) {
                    if (invokeOnCreate) {
                        shouldShowSpeakerDialog = true;
                    } else {
                        showSpeakerDialog();
                    }
                }
            }
        }
    }

    private boolean isUpgradeBtnShowing() {
        return getString(R.string.bluetooth_connect_upgrade_btn).equals(mConfirmBtn.getText().toString());
    }

    private boolean needShowUpgradeBtn() {
        return showUpgradeIdentity && (mBluetoothConnectItemAdapter.hasUpgradeableItems() || mBluetoothConnectItemAdapter.hasConflictItems() || getPresenter().isBoardUpgradeable());
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(shouldShowAbnormalDialog) {
//            shouldShowAbnormalDialog = false;
//            showAbnormalDialog();
//        } else if(shouldShowConflictDialog) {
//            shouldShowConflictDialog = false;
//            showConflictDialog();
//        }
        if (shouldShowSpeakerDialog) {
            shouldShowSpeakerDialog = false;
            showSpeakerDialog();
        }
        if (shouldShowLowPowerDialog) {
            shouldShowLowPowerDialog = false;
            BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.LOW_POWER);
        }
    }

    @Override
    protected void onPause() {
        if (!isFinishing() && getPresenter().isUpgrading()) {
            isUpgradeAbort = true;
            showUpgradeFailureDialog();
            getUIView().updateAllProcessFinish(false);
        }
        super.onPause();
        getPresenter().upgradeAbort();
    }

    private void showSpeakerDialog() {
        URoMainBoardInfo bid = BluetoothHelper.getBoardInfoData();
        if (bid == null || bid.speaker.getAvailableIds().isEmpty()) {
            return;
        }
        BluetoothHelper.addCommand(BtInvocationFactory.readSpeakerInfo(new IUKitCommandResponse<URoSpeakerInfo>() {
            @Override
            public void onUKitCommandResponse(URoCompletionResult<URoSpeakerInfo> result) {
                if (BluetoothConnectActivity.this.isFinishing() || BluetoothConnectActivity.this.isDestroyed()) {
                    return;
                }
                if (!result.isSuccess() || result.getData() == null) {
                    return;
                }
                String name = result.getData().getName();
                PromptDialogFragment.newBuilder(BluetoothConnectActivity.this)
                        .type(PromptDialogFragment.Type.NORMAL)
                        .title(getString(R.string.bluetooth_peripheral_speaker))
                        .message(getString(R.string.bluetooth_peripheral_speaker_hint, name))
                        .positiveButtonText(getString(R.string.bluetooth_connect_confirm_btn))
                        .showNegativeButton(false)
                        .cancelable(false)
                        .build()
                        .show(getSupportFragmentManager(), "speaker_info");
            }
        }));
//        BluetoothHelper.addCommand(UKitCmdSensorSpeakerInfo.newInstance(), new IUKitCommandResponse<UKitSpeakerInfoData>() {
//            @Override
//            public void onUKitCommandResponse(UKitCommandResponse<UKitSpeakerInfoData> result) {
//                if (BluetoothConnectActivity.this.isFinishing() || BluetoothConnectActivity.this.isDestroyed()) {
//                    return;
//                }
//                if (!result.isSuccess() || result.getData() == null) {
//                    return;
//                }
//                UKitSpeakerInfoData sid = result.getData();
//                String name = sid.getName();
//                PromptDialogFragment.newBuilder(BluetoothConnectActivity.this)
//                        .type(PromptDialogFragment.Type.NORMAL)
//                        .title(getString(R.string.bluetooth_peripheral_speaker))
//                        .message(getString(R.string.bluetooth_peripheral_speaker_hint, name))
//                        .positiveButtonText(getString(R.string.bluetooth_connect_confirm_btn))
//                        .showNegativeButton(false)
//                        .cancelable(false)
//                        .build()
//                        .show(getSupportFragmentManager(), "speaker_info");
//            }
//        });
    }

    private void showAbnormalDialog() {
        PromptDialogFragment.newBuilder(this)
                .type(PromptDialogFragment.Type.BIG)
                .title(getString(R.string.bluetooth_connect_abnormal_title))
                .message(getString(R.string.bluetooth_connect_abnormal_msg))
                .positiveButtonText(getString(R.string.bluetooth_connect_confirm_btn))
                .showNegativeButton(false)
                .image(R.drawable.bluetooth_popup_pic_abnormal)
                .cancelable(false)
                .build()
                .show(getSupportFragmentManager(), "connect_abnormal");
    }

    private void showUpgradeFailureDialog() {
        String error = getString(R.string.bluetooth_connect_upgrade_failure_msg);
        if (BluetoothHelper.isSmartVersion() && BluetoothHelper.isConnected() && !getPresenter().isBoardNetworkConnected()) {
            error = getString(R.string.bluetooth_connect_upgrade_failure_wifi_error_msg);
        }
        PromptDialogFragment.newBuilder(this)
                .type(PromptDialogFragment.Type.BIG)
                .title(getString(R.string.bluetooth_connect_upgrade_failure_title))
                .message(error)
                .positiveButtonText(getString(R.string.bluetooth_connect_confirm_btn))
                .onPositiveClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setButtonBlocked(false);
                        if (BluetoothHelper.isSmartVersion()) {
                            refreshUpgradeBtnByWiFiState(getPresenter().isBoardNetworkConnected());
                        }
                    }
                })
                .showNegativeButton(false)
                .image(R.drawable.bluetooth_popup_pic_upgradefailed)
                .cancelable(true)
                .build()
                .show(getSupportFragmentManager(), "upgrade_failure");
    }

    private void showConflictDialog() {
        PromptDialogFragment.newBuilder(this)
                .type(PromptDialogFragment.Type.BIG)
                .title(getString(R.string.bluetooth_connect_conflict_title))
                .message(getString(R.string.bluetooth_connect_conflict_msg))
                .positiveButtonText(getString(R.string.bluetooth_connect_confirm_btn))
                .showNegativeButton(false)
                .image(R.drawable.bluetooth_popup_pic_idrepeat)
                .cancelable(true)
                .build()
                .show(getSupportFragmentManager(), "connect_conflict");
    }

    private void showUnmatchDialog(String title, String message) {
        PromptDialogFragment.newBuilder(this)
                .type(PromptDialogFragment.Type.NORMAL)
                .title(title)
                .message(message)
                .positiveButtonText(getString(R.string.bluetooth_connect_confirm_btn))
                .showNegativeButton(false)
                .cancelable(true)
                .build()
                .show(getSupportFragmentManager(), "connect_unmatch");
    }

    private void showPeripheralUnMatchDialog() {
        String error = getString(R.string.bluetooth_update_accessory_configuration);
        PromptDialogFragment.newBuilder(this)
                .type(PromptDialogFragment.Type.NORMAL)
                .title(getString(R.string.bluetooth_update_accessory_configuration_title))
                .message(error)
                .positiveButtonText(getString(R.string.bluetooth_connect_confirm_btn))
                .onPositiveClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!BluetoothHelper.isBluetoothConnected()) {
                            showNotConnectDialog();
                            return;
                        }
                        getPresenter().saveConfig();
                        initData(false);
                        if (!needShowUpgradeBtn()) {
                            getPresenter().preparePeripheral();
                            finish();
                        } else {//还需要升级
                            BluetoothHelper.setVerification(false);
                        }
                    }
                })
                .build()
                .show(getSupportFragmentManager(), "peripheral_match_error");
    }

    private void showPeripheralInfoDialog(boolean isBoard, BluetoothConnectItemAdapter.PeripheralItem peripheralItem) {
        int titleRes = -1;
        StringBuilder sb = new StringBuilder();
        if (isBoard) {
            if (BluetoothHelper.getBoardInfoData() == null && !BluetoothHelper.isBluetoothConnected()) {
                showNotConnectDialog();
                return;
            }
            titleRes = R.string.bluetooth_connect_motherboard;
            sb.append(getString(R.string.bluetooth_peripheral_version)).append(": ").append(BluetoothHelper.getBoardInfoData().boardVersion);
        } else {
            sb.append("ID-").append(peripheralItem.getId());
            sb.append("\n").append(getString(R.string.bluetooth_peripheral_version)).append(": ").append(peripheralItem.getVersion());
            if (peripheralItem.isSteeringGear()) {
                titleRes = R.string.bluetooth_peripheral_steering_gear;
            } else {
                switch (peripheralItem.getType()) {
                    case MOTOR:
                        titleRes = R.string.bluetooth_peripheral_motor;
                        break;
                    case INFRAREDSENSOR:
                        titleRes = R.string.bluetooth_peripheral_infrared;
                        break;
                    case ULTRASOUNDSENSOR:
                        titleRes = R.string.bluetooth_peripheral_ultrasound;
                        break;
                    case LED:
                        titleRes = R.string.bluetooth_peripheral_lighting;
                        break;
                    case TOUCHSENSOR:
                        titleRes = R.string.bluetooth_peripheral_touch;
                        break;
                    case ENVIRONMENTSENSOR:
                        titleRes = R.string.bluetooth_peripheral_humiture;
                        break;
                    case BRIGHTNESSSENSOR:
                        titleRes = R.string.bluetooth_peripheral_env_light;
                        break;
                    case SOUNDSENSOR:
                        titleRes = R.string.bluetooth_peripheral_sound;
                        break;
                    case COLORSENSOR:
                        titleRes = R.string.bluetooth_peripheral_color;
                        break;
                    case SPEAKER:
                        titleRes = R.string.bluetooth_peripheral_speaker;
                        URoSpeaker sid = BluetoothHelper.getSpeaker();
                        if (sid != null) {
                            sb.append("\n").append(getString(R.string.bluetooth_peripheral_mac)).append(": ").append(sid.getNameValue());
                        } else {
                            sb.append("\n").append(getString(R.string.bluetooth_peripheral_mac)).append(": ").append("Unknown");
                        }
                        break;
                    case LED_BELT:
                        titleRes = R.string.bluetooth_peripheral_led_box;
                        break;
                }
            }
        }

        PromptDialogFragment.newBuilder(this)
                .type(PromptDialogFragment.Type.NORMAL)
                .title(getString(titleRes))
                .message(sb.toString())
                .positiveButtonText(getString(R.string.bluetooth_connect_confirm_btn))
                .showNegativeButton(false)
                .cancelable(true)
                .build()
                .show(getSupportFragmentManager(), "peripheral_info");
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
                        Intent intent = new Intent(BluetoothConnectActivity.this, BluetoothSearchActivity.class);
                        intent.putExtra(EXTRA_SHOW_UPGRADE_IDENTITY, showUpgradeIdentity);
                        intent.putExtra(EXTRA_SHOW_WIFI_CONFIG_IDENTITY, showWiFiConfigIdentity);
                        startActivity(intent);
                        finish();
                    }
                })
                .cancelable(false)
                .build()
                .show(getSupportFragmentManager(), "not_connect");
    }

    private void quitConnect() {
        if (mode == ENTRY_MODE_PREPARE) {
            getPresenter().disconnect();
        }
    }

    private boolean isEverythingOk() {
        boolean result = true;
        if (showUpgradeIdentity) {
            result = result && !mBluetoothConnectItemAdapter.hasUpgradeableItems();
            result = result && !getPresenter().isBoardUpgradeable();
            result = result && !mBluetoothConnectItemAdapter.hasConflictItems();
        }
        result = result && !mBluetoothConnectItemAdapter.hasAbnormalItems();
        result = result && !mBluetoothConnectItemAdapter.hasUnmatchItems();
        return result;
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        if (v == mExitBtn) {
            quitConnect();
            finish();
        } else if (v == mRecheckBtn) {
            if (!BluetoothHelper.isBluetoothConnected()) {
                showNotConnectDialog();
                return;
            }
            getPresenter().reloadBoardInfo();
        } else if (v == mConfirmBtn) {
            if (BluetoothHelper.getBoardInfoData() == null && !BluetoothHelper.isBluetoothConnected()) {
                showNotConnectDialog();
                return;
            }
            if (isUpgradeBtnShowing()) {
                if (BluetoothHelper.isLowBattery()) {
                    BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.LOW_POWER);
                    return;
                }
                setButtonBlocked(true);
                isUpgradeAbort = false;
                getPresenter().upgrade();
            } else {
                if (mBluetoothConnectItemAdapter.hasUnmatchItems()) {
                    showPeripheralUnMatchDialog();
                    return;
                }
                getPresenter().preparePeripheral();
                getPresenter().saveConfig();
                finish();
            }

            if (isUpgradeBtnShowing()) {
                UBTReporter.onSessionEvent(Events.Ids.app_peripheral_upgradle_duration, true, null);
            }
        } else if (v == mDisconnectBtn) {
            getPresenter().disconnect();
            finish();
        } else if (v == mBoardView) {
            showPeripheralInfoDialog(true, null);
        } else if (v == mWifiConfigBtn) {
            if (!BluetoothHelper.isBluetoothConnected()) {
                showNotConnectDialog();
                return;
            }
            WifiConfigActivity.entryWifiConfigActivity(BluetoothConnectActivity.this);
        } else if (v == mWifiIconArea) {
            if (getPresenter().isWiFiConnected() && !getPresenter().isBoardNetworkConnected()) {
                getUIDelegate().toastShort(getString(R.string.bluetooth_wifi_network_unavailable_msg));
            }
        }
    }

    @Override
    public void onBluetoothItemClick(View view, int position) {
        if (blockBtnEvent && mode == ENTRY_MODE_PREPARE) {
            return;
        }
        BluetoothConnectItemAdapter.PeripheralItem peripheralItem = mBluetoothConnectItemAdapter.getItem(position);
//        if(peripheralItem.isAbnormal()) {
//            showAbnormalDialog();
//        } else if(peripheralItem.isConflict()) {
//            showConflictDialog();
//        } else {
//            showPeripheralInfoDialog(false, peripheralItem);
//        }
        if (peripheralItem.isUnmatch()) {
            String title, message;
            if (peripheralItem.isSteeringGear()) {
                title = getString(R.string.bluetooth_connect_unmatch_title, getString(R.string.bluetooth_peripheral_steering_gear));
                message = getString(R.string.bluetooth_connect_unmatch_msg, getString(R.string.bluetooth_peripheral_steering_gear));
            } else {
                title = getString(R.string.bluetooth_connect_unmatch_title, getString(R.string.bluetooth_peripheral_motor));
                message = getString(R.string.bluetooth_connect_unmatch_msg, getString(R.string.bluetooth_peripheral_motor));
            }
            showUnmatchDialog(title, message);
        } else if (peripheralItem.isAbnormal()) {
            showConflictDialog();
        } else if (peripheralItem.getType() != URoComponentType.LED_STRIP) {
            showPeripheralInfoDialog(false, peripheralItem);
        }
    }

    private void setButtonBlocked(boolean blockBtnEvent) {
        this.blockBtnEvent = blockBtnEvent;
        setButtonEnabled(!blockBtnEvent);
    }

    private void setButtonEnabled(boolean enabled) {
        mExitBtn.setEnabled(enabled);
        mBoardView.setEnabled(enabled);
        mWifiConfigBtn.setEnabled(enabled);
        if (mode == ENTRY_MODE_PREPARE) {
            mConfirmBtn.setEnabled(enabled);
            mRecheckBtn.setEnabled(enabled);
        } else if (mode == ENTRY_MODE_CONNECTED) {
            mDisconnectBtn.setEnabled(enabled);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (blockBtnEvent && mode == ENTRY_MODE_PREPARE) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected BluetoothConnectContracts.Presenter createPresenter() {
        return BluetoothHelper.isSmartVersion() ? new BluetoothSmartConnectPresenter() : new BluetoothConnectPresenter();
    }

    @Override
    protected BluetoothConnectContracts.UI createUIView() {
        return new BluetoothConnectUI();
    }

    class BluetoothConnectUI extends BluetoothConnectContracts.UI {
        @Override
        public void updateBoardUpgradeable(boolean upgradeable) {
            if (mode != ENTRY_MODE_PREPARE) {
                return;
            }
            mBoardView.markAsFlag(showUpgradeIdentity && upgradeable, false, false, false);
        }

        @Override
        public void updateBoardInfo(URoMainBoardInfo data) {
            mBluetoothConnectItemAdapter.setBoardInfoData(data);
        }

        @Override
        public void updateModelInfo(ModelInfo data) {
            mBluetoothConnectItemAdapter.setModelInfo(data);
        }

        public void refreshUI() {
            initData();
        }

        @Override
        public void updateUpgradeProcess(boolean isBoard, boolean isSteeringGear, URoComponentType sensorType, int process) {
            if (mode != ENTRY_MODE_PREPARE) {
                return;
            }
            if (isBoard) {
                mBoardView.updateUpgradeProcess(process);
            } else {
                mBluetoothConnectItemAdapter.updateUpgradeProcess(isSteeringGear, sensorType, process);
            }
        }

        @Override
        public void updateUpgradeResult(boolean isBoard, boolean isSteeringGear, URoComponentType sensorType, boolean isSuccess) {
            if (mode != ENTRY_MODE_PREPARE) {
                return;
            }
            if (isBoard) {
                mBoardView.updateUpgradeResult(isSuccess);
            } else {
                mBluetoothConnectItemAdapter.updateUpgradeResult(isSteeringGear, sensorType, isSuccess);
            }
        }

        @Override
        public void updateAllProcessFinish(boolean isSuccess) {
            if (mode != ENTRY_MODE_PREPARE) {
                return;
            }
            if (isUpgradeBtnShowing()) {
                UBTReporter.onSessionEvent(Events.Ids.app_peripheral_upgradle_duration, false, null);
                Map<String, String> args = new HashMap<>();
                args.put("isSuccess", String.valueOf(isSuccess));
                URoSerialNumberInfo data = BluetoothHelper.getSerialNumberData();
                if (data != null && data.isValid()) {
                    args.put("serialNumber", data.getSerialNumber());
                }
                UBTReporter.onEvent(Events.Ids.app_peripheral_upgradle_result, args);
            }
            if (isSuccess) {
                mConfirmBtn.setText(R.string.bluetooth_connect_confirm_btn);
            }
            mBluetoothConnectItemAdapter.clearUpgradeIdentity();
            mBoardView.updateUpgradeResult(false);
            setButtonBlocked(false);
            if (isSuccess) {
                if (!BluetoothHelper.isSmartVersion() || !BluetoothReconnectTask.getInstance().isRebooting()) {
                    getPresenter().reloadBoardInfo();
                }
            } else {
                if (!isUpgradeAbort) {
                    showUpgradeFailureDialog();
                }
                if (BluetoothHelper.isSmartVersion()) {
                    refreshUpgradeBtnByWiFiState(getPresenter().isBoardNetworkConnected());
                }
            }
        }

        @Override
        public void restartBoardAfterUpgrade() {
            getUIDelegate().showLoading(false, getString(R.string.bluetooth_is_restarting_message));
        }

        @Override
        public void updateWifiInfo(URoWiFiStatusInfo wifiInfo, URoNetworkState networkState) {
            if (wifiInfo == null || wifiInfo.getState() != URoWiFiStatusInfo.URoWiFiState.CONNECTED) {
                mWifiNameTv.setText(R.string.bluetooth_wifi_disconnect);
                mWifiNameTv.setTextColor(0xFFFE6F7B);
                mWifiIconIv.setImageResource(R.drawable.network_ic_wifi1);
                refreshUpgradeBtnByWiFiState(false);
            } else {
                mWifiNameTv.setText(wifiInfo.getSsid());
                mWifiNameTv.setTextColor(0xFF555B67);
                if (networkState == URoNetworkState.CONNECTED) {
                    mWifiIconIv.setImageResource(R.drawable.network_ic_wifi2);
                    refreshUpgradeBtnByWiFiState(true);
                } else {
                    mWifiIconIv.setImageResource(R.drawable.network_ic_wifi3);
                    refreshUpgradeBtnByWiFiState(false);
                }
            }
        }

    }

    private void refreshUpgradeBtnByWiFiState(boolean enable) {
        if (getPresenter().isUpgrading()) {
            return;
        }
        if (isUpgradeBtnShowing()) {
            if (enable) {
                mNeedWiFiConfigMessage.setVisibility(View.INVISIBLE);
            } else {
                mNeedWiFiConfigMessage.setVisibility(View.VISIBLE);
            }
            mConfirmBtn.setEnabled(enable);
        }
    }


}
