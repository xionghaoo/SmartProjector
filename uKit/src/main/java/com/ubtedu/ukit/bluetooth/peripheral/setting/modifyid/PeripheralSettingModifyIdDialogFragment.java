/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.bluetooth.peripheral.setting.modifyid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aigestudio.wheelpicker.WheelPicker;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.bluetooth.connect.widget.PeripheralView;
import com.ubtedu.ukit.common.base.UKitBaseDialogFragment;

import java.util.ArrayList;

/**
 * @Author naOKi
 * @Date 2018/12/20
 **/
public class PeripheralSettingModifyIdDialogFragment extends UKitBaseDialogFragment {
    private final static String EXTRA_ID = "_extra_id";
    private final static String EXTRA_TARGET_ID = "_extra_id";
    private final static String EXTRA_VALUE = "_extra_value";
    private final static String EXTRA_IS_STEERINGGEAR = "_extra_is_steeringgear";
    private final static String EXTRA_SENSORTYPE = "_extra_sensortype";
    
    private TextView mTargetTv;
    private Button mCancelBtn;
    private Button mConfirmBtn;
    private ImageView mSelectCancelIv;
    private ImageView mSelectConfirmIv;

    private PeripheralView mIcon;
    private WheelPicker mWheelPicker;

    private View mWheelArea;

    private int targetId;
    private int sourceId;
    private boolean isSteeringGear;
    private URoComponentType type;
    private ArrayList<Integer> values;

    private IModifyIdCallback modifyIdCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            initArguments(savedInstanceState);
        } else {
            initArguments(getArguments());
        }
    }

    private void initArguments(Bundle args) {
        if(args != null) {
            sourceId = args.getInt(EXTRA_ID);
            targetId = args.getInt(EXTRA_TARGET_ID, sourceId);
            isSteeringGear = args.getBoolean(EXTRA_IS_STEERINGGEAR);
            type = (URoComponentType) args.getSerializable(EXTRA_SENSORTYPE);
            values = args.getIntegerArrayList(EXTRA_VALUE);
        }
        if(values == null) {
            values = new ArrayList<>();
            values.add(sourceId);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_ID, sourceId);
        outState.putInt(EXTRA_TARGET_ID, targetId);
        outState.putBoolean(EXTRA_IS_STEERINGGEAR, isSteeringGear);
        outState.putSerializable(EXTRA_SENSORTYPE, type);
        outState.putIntegerArrayList(EXTRA_VALUE, values);
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_peripheral_modify_id,null);
        if (isCancelable()) {
            mRootView = view.findViewById(R.id.dialog_fragment_root_view);
        }

        mCancelBtn = view.findViewById(R.id.dialog_peripheral_modify_id_negative_btn);
        bindSafeClickListener(mCancelBtn);
        mConfirmBtn = view.findViewById(R.id.dialog_peripheral_modify_id_positive_btn);
        bindSafeClickListener(mConfirmBtn);
        mSelectCancelIv = view.findViewById(R.id.dialog_peripheral_modify_id_wheel_cancel_btn);
        bindClickListener(mSelectCancelIv);
        mSelectConfirmIv = view.findViewById(R.id.dialog_peripheral_modify_id_wheel_confirm_btn);
        bindClickListener(mSelectConfirmIv);

        mIcon = view.findViewById(R.id.dialog_peripheral_modify_id_icon);
        mWheelPicker = view.findViewById(R.id.dialog_peripheral_modify_id_wheel_view);
        mWheelArea = view.findViewById(R.id.dialog_peripheral_modify_id_wheel_area);

        mTargetTv = view.findViewById(R.id.dialog_peripheral_modify_id_target_tv);
        bindClickListener(mTargetTv);

        mWheelPicker.setData(values);

        if(isSteeringGear) {
            mIcon.setAsSteeringGear(sourceId);
        } else {
            mIcon.setAsSensor(type, sourceId);
        }
        mTargetTv.setText(String.valueOf(sourceId));

        updateButtonsState();

        return view;
    }
    
    @Override
    public void onClick(View v, boolean isSafeClick) {
        if (v == mCancelBtn) {
            dismiss();
        }
        if (v == mConfirmBtn) {
            if(modifyIdCallback != null) {
                modifyIdCallback.onModifyIdConfirmed(isSteeringGear, type, sourceId, targetId);
            }
            dismiss();
        }
        if (v == mSelectCancelIv) {
            mWheelArea.setVisibility(View.GONE);
        }
        if (v == mSelectConfirmIv) {
            targetId = values.get(mWheelPicker.getCurrentItemPosition());
            mTargetTv.setText(String.valueOf(targetId));
            mWheelArea.setVisibility(View.GONE);
            updateButtonsState();
        }
        if (v == mTargetTv) {
            int selectedIndex = values.indexOf(targetId);
            mWheelPicker.setSelectedItemPosition(selectedIndex);
            mWheelArea.setVisibility(View.VISIBLE);
        }
    }
    
    private void updateButtonsState() {
        boolean result = targetId != sourceId;
        if (result) {
            mConfirmBtn.setEnabled(true);
            mConfirmBtn.setAlpha(1.0f);
        } else {
            mConfirmBtn.setEnabled(false);
            mConfirmBtn.setAlpha(0.5f);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private int id;
        private boolean isSteeringGear;
        private URoComponentType type;
        private ArrayList<Integer> values;
        private IModifyIdCallback modifyIdCallback;
        public Builder() {
        }
        public Builder setSteeringGearId(int id) {
            this.id = id;
            isSteeringGear = true;
            type = null;
            return this;
        }
        public Builder setSensorId(URoComponentType type, int id) {
            this.id = id;
            isSteeringGear = false;
            this.type = type;
            return this;
        }
        public Builder setValues(ArrayList<Integer> values) {
            this.values = values;
            return this;
        }
        public Builder setModifyIdCallback(IModifyIdCallback modifyIdCallback) {
            this.modifyIdCallback = modifyIdCallback;
            return this;
        }
        public PeripheralSettingModifyIdDialogFragment build() {
            PeripheralSettingModifyIdDialogFragment fragment = new PeripheralSettingModifyIdDialogFragment();
            fragment.modifyIdCallback = modifyIdCallback;
            fragment.setCancelable(false);
            Bundle args = new Bundle();
            args.putInt(EXTRA_ID, id);
            args.putBoolean(EXTRA_IS_STEERINGGEAR, isSteeringGear);
            args.putSerializable(EXTRA_SENSORTYPE, type);
            args.putIntegerArrayList(EXTRA_VALUE, values);
            fragment.setArguments(args);
            return fragment;
        }
    }

    public interface IModifyIdCallback {
        void onModifyIdConfirmed(boolean isSteeringGear, URoComponentType type, int sourceId, int targetId);
    }

}
