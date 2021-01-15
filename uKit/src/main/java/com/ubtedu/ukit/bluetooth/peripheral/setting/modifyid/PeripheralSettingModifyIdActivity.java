package com.ubtedu.ukit.bluetooth.peripheral.setting.modifyid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.deviceconnect.libs.base.model.URoComponentInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoMainBoardInfo;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.connect.adapter.BluetoothConnectItemAdapter;
import com.ubtedu.ukit.bluetooth.error.BluetoothCommonErrorHelper;
import com.ubtedu.ukit.bluetooth.interfaces.IBluetoothItemClickListener;
import com.ubtedu.ukit.bluetooth.search.BluetoothSearchActivity;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @Author naOKi
 * @Date 2018/12/20
 **/
public class PeripheralSettingModifyIdActivity extends UKitBaseActivity<PeripheralSettingModifyIdContracts.Presenter, PeripheralSettingModifyIdContracts.UI> {

    public static final int ENTRY_MODE_SETTING = 1;
    public static final int ENTRY_MODE_CONNECT = 2;

    public static final String EXTRA_ENTRY_MODE = "_extra_entry_mode";

    private ImageView mBackIv;
    private Button mDisconnectBtn;
    private Button mConnectBtn;

    private RecyclerView mPeripheralList;

    private View mListArea;
    private View mRefreshArea;
    private View mEmptyArea;

    private URoMainBoardInfo mBoardData;

    private int mode = ENTRY_MODE_SETTING;
    private boolean isConnected = false;

    private BluetoothConnectItemAdapter mPeripheralAdapter;

    private boolean isBackground = false;
    private boolean needShowFailure = false;

    public static void openActivity(Context context, int mode) {
        Intent intent = new Intent(context, PeripheralSettingModifyIdActivity.class);
        intent.putExtra(EXTRA_ENTRY_MODE, mode);
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
        setContentView(R.layout.activity_peripheral_setting_modify_id);
        initView();
        initData();
    }

    private void initArguments(Bundle args) {
        if (args != null) {
            mode = args.getInt(EXTRA_ENTRY_MODE, ENTRY_MODE_SETTING);
            isConnected = mode == ENTRY_MODE_CONNECT;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_ENTRY_MODE, mode);
    }

    private void initView() {
        mPeripheralList = findViewById(R.id.project_peripheral_setting_modify_id_list);

        mBackIv = findViewById(R.id.peripheral_setting_modify_back_btn);
        mDisconnectBtn = findViewById(R.id.peripheral_setting_modify_disconnect_btn);
        mConnectBtn = findViewById(R.id.peripheral_setting_modify_connect_btn);

        mListArea = findViewById(R.id.project_peripheral_setting_modify_id_list_area);
        mRefreshArea = findViewById(R.id.project_peripheral_setting_modify_id_refresh_area);
        mEmptyArea = findViewById(R.id.project_peripheral_setting_modify_id_empty_area);

        bindSafeClickListener(mBackIv);
        bindSafeClickListener(mDisconnectBtn);
        bindSafeClickListener(mConnectBtn);

        mPeripheralAdapter = new BluetoothConnectItemAdapter();
        mPeripheralAdapter.setShowIdentity(true);
        mPeripheralAdapter.setShowUpgradeable(false);
        mPeripheralList.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mPeripheralList.setAdapter(mPeripheralAdapter);
        mPeripheralList.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));
        mPeripheralList.setHasFixedSize(true);
        mPeripheralAdapter.setItemClickListener(new IBluetoothItemClickListener() {
            @Override
            public void onBluetoothItemClick(View view, int position) {
                BluetoothConnectItemAdapter.PeripheralItem peripheralItem = mPeripheralAdapter.getItem(position);
                onItemClicked(peripheralItem);
            }
        });
        if (BluetoothHelper.isConnected()) {
            BluetoothHelper.disconnect();
        }
    }

    @Override
    protected void onPause() {
        isBackground = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isBackground = false;
        if (needShowFailure) {
            needShowFailure = false;
            showFailureMsg();
        }
    }

    private void onItemClicked(BluetoothConnectItemAdapter.PeripheralItem peripheralItem) {
        if (!BluetoothHelper.isConnected()) {
            BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.BLUETOOTH_NOT_CONNECTED_IGNORE_UPGRADE_AND_WIFI);
            return;
        }
        if (peripheralItem.isAbnormal()) {
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
            return;
        }
        if (URoComponentType.SPEAKER.equals(peripheralItem.getType())) {
            showSpeakerMsg();
            return;
        }
        PeripheralSettingModifyIdDialogFragment.Builder builder = PeripheralSettingModifyIdDialogFragment.newBuilder();
        if (peripheralItem.isSteeringGear()) {
            builder.setSteeringGearId(peripheralItem.getId());
        } else {
            builder.setSensorId(peripheralItem.getType(), peripheralItem.getId());
        }
        builder.setValues(prepareAvailableIds(peripheralItem.isSteeringGear(), peripheralItem.getType(), peripheralItem.getId()));
        builder.setModifyIdCallback(new PeripheralSettingModifyIdDialogFragment.IModifyIdCallback() {
            @Override
            public void onModifyIdConfirmed(boolean isSteeringGear, URoComponentType type, int sourceId, int targetId) {
                PeripheralSettingModifyIdActivity.this.getUIDelegate().showLoading(false);
                getPresenter().modifyId(isSteeringGear, type, sourceId, targetId);
            }
        });
        builder.build().show(getSupportFragmentManager(), "modify_id");
    }

    private ArrayList<Integer> prepareAvailableIds(boolean isSteeringGear, URoComponentType type, int currentId) {
        ArrayList<Integer> result = new ArrayList<>();
        if (mBoardData != null) {
            int count = isSteeringGear ? 32 : 8;
            if (type == URoComponentType.LED_BELT) {
                count = 2;
            }
            for (int i = 1; i <= count; i++) {
                result.add(i);
            }
            if (isSteeringGear) {
                result.removeAll(mBoardData.getServosInfo().getAvailableIds());
                result.removeAll(mBoardData.getServosInfo().getAbnormalIds());
                result.removeAll(mBoardData.getServosInfo().getConflictIds());
            } else {
                result.removeAll(mBoardData.getComponentInfo(type).getAvailableIds());
                result.removeAll(mBoardData.getComponentInfo(type).getAbnormalIds());
                result.removeAll(mBoardData.getComponentInfo(type).getConflictIds());
            }
        }
        if (!result.contains(currentId)) {
            result.add(currentId);
        }
        Collections.sort(result);
        return result;
    }

    private void initData() {
        if (isConnected) {
            getPresenter().init();
        } else {
            mPeripheralAdapter.clear();
        }
        updateView(isConnected);
    }

    private void updateView(boolean isConnect) {
        if (isConnect) {
            mListArea.setVisibility(View.VISIBLE);
            mEmptyArea.setVisibility(View.GONE);
            mConnectBtn.setVisibility(View.GONE);
            mDisconnectBtn.setVisibility(View.VISIBLE);
        } else {
            mListArea.setVisibility(View.GONE);
            mEmptyArea.setVisibility(View.VISIBLE);
            mConnectBtn.setVisibility(View.VISIBLE);
            mDisconnectBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void showSpeakerMsg() {
        getUIDelegate().toastShort("蓝牙音箱不允许修改ID，产品补充弹出提示");
    }

    private void showSuccessMsg() {
        getUIDelegate().toastShort(getString(R.string.project_peripheral_setting_modify_id_success_msg));
    }

    private void showFailureMsg() {
        if (isBackground) {
            needShowFailure = true;
            return;
        }
        PromptDialogFragment.newBuilder(this)
                .type(PromptDialogFragment.Type.BIG)
                .title(getString(R.string.project_peripheral_setting_modify_id_title))
                .message(getString(R.string.project_peripheral_setting_modify_id_failure_msg))
                .positiveButtonText(getString(R.string.bluetooth_connect_confirm_btn))
                .showNegativeButton(false)
                .image(R.drawable.bluetooth_popup_pic_idrepeat)
                .cancelable(true)
                .build()
                .show(getSupportFragmentManager(), "modify_failure");
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        if (v == mBackIv) {
            finish();
        } else if (v == mDisconnectBtn) {
            getPresenter().disconnect();
            isConnected = false;
            initData();
        } else if (v == mConnectBtn) {
            Intent intent = new Intent(this, BluetoothSearchActivity.class);
            intent.putExtra(BluetoothSearchActivity.EXTRA_SHOW_UPGRADE_IDENTITY, false);
            intent.putExtra(BluetoothSearchActivity.EXTRA_SHOW_WIFI_CONFIG_IDENTITY, false);
            startActivity(intent);
        }
    }

    private void updateSteeringGearId(int sourceId, int targetId) {
        if (mBoardData == null) {
            return;
        }
        if (sourceId < 1 || sourceId > 32 || targetId < 1 || targetId > 32 || sourceId == targetId) {
            return;
        }
        mBoardData.getServosInfo().updateId(sourceId, targetId);
        mPeripheralAdapter.setBoardInfoData(mBoardData);
    }

    private void updateSensorId(URoComponentType sensorType, int sourceId, int targetId) {
        if (mBoardData == null) {
            return;
        }
        if (sourceId < 1 || sourceId > 8 || targetId < 1 || targetId > 8 || sourceId == targetId) {
            return;
        }
        mBoardData.getComponentInfo(sensorType).updateId(sourceId, targetId);
        mPeripheralAdapter.setBoardInfoData(mBoardData);
    }

    @Override
    protected PeripheralSettingModifyIdPresenter createPresenter() {
        return new PeripheralSettingModifyIdPresenter();
    }

    @Override
    protected PeripheralSettingModifyIdUI createUIView() {
        return new PeripheralSettingModifyIdUI();
    }

    class PeripheralSettingModifyIdUI extends PeripheralSettingModifyIdContracts.UI {
        @Override
        public void updateBoardInfo(URoMainBoardInfo data) {
            mBoardData = data;
            if (data != null) {
                //修改ID操作，不显示蓝牙音箱外设
                data.speaker = new URoComponentInfo();
                if (data.ledBelt!=null){
                    data.ledBelt.clearSubComponent();
                }
            }
            mPeripheralAdapter.setBoardInfoData(data);
        }

        public void refreshUI() {
            initData();
        }

        @Override
        public void showRefreshView() {
            if (isConnected) {
                mPeripheralAdapter.clear();
                mRefreshArea.setVisibility(View.VISIBLE);
                mDisconnectBtn.setEnabled(false);
            }
        }

        @Override
        public void hideRefreshView() {
            if (isConnected) {
                mRefreshArea.setVisibility(View.GONE);
                mDisconnectBtn.setEnabled(true);
            }
        }

        @Override
        public void updateConnectStatus(boolean connected) {
            isConnected = connected;
            initData();
        }

        @Override
        public void updateModifyResult(boolean isSuccess, boolean isSteeringGear, URoComponentType type, int sourceId, int targetId) {
            getUIDelegate().hideLoading();
            if (isSuccess) {
                if (isSteeringGear) {
                    updateSteeringGearId(sourceId, targetId);
                } else {
                    updateSensorId(type, sourceId, targetId);
                }
                showSuccessMsg();
            } else {
                showFailureMsg();
            }
        }
    }
}
