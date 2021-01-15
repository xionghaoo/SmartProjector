/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.device;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.ubtedu.base.net.rxretrofit.subscriber.SimpleRxSubscriber;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseFragment;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;
import com.ubtedu.ukit.menu.settings.Settings;
import com.ubtedu.ukit.menu.settings.TargetDevice;
import com.ubtedu.ukit.project.UserDataSynchronizer;

/**
 * 为了实现两种不同的交互和UI又不想写两套，实现起来比较奇怪
 * @Author qinicy
 * @Date 2018/12/12
 **/
public class DeviceSelectFragment extends UKitBaseFragment {

    private View mUKit1Item;
    private View mUKit2Item;
    private View mUKit1SelectedIcon;
    private View mUKit2SelectedIcon;
    private View mConfirmBtn;
    @TargetDevice.Values
    private int mTargetDevice;
    private View.OnClickListener mOnConfirmClickListener;
    private boolean isDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTargetDevice = Settings.getTargetDevice();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(getUILayout(), null);
        mConfirmBtn = root.findViewById(R.id.menu_device_select_confirm_btn);
        mUKit1Item = root.findViewById(R.id.menu_device_select_item_ukit1_lyt);
        mUKit2Item = root.findViewById(R.id.menu_device_select_item_ukit2_lyt);
        mUKit1SelectedIcon = root.findViewById(R.id.menu_device_select_ukit1_icon);
        mUKit2SelectedIcon = root.findViewById(R.id.menu_device_select_ukit2_icon);

        bindSafeClickListener(mConfirmBtn);
        bindSafeClickListener(mUKit1Item);
        bindSafeClickListener(mUKit2Item);
        updateUI(mTargetDevice);
        return root;
    }

    private int getUILayout() {
        return isDialog ? R.layout.fragment_menu_device_dialog : R.layout.fragment_menu_device_menu;
    }

    public void setUIMode(boolean isDialog) {
        this.isDialog = isDialog;
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mConfirmBtn) {
            onConfirm();
        }
        if (v == mUKit1Item) {
            onSelectChanged(TargetDevice.UKIT1);
        }
        if (v == mUKit2Item) {
            onSelectChanged(TargetDevice.UKIT2);
        }
    }

    public void setOnConfirmClickListener(View.OnClickListener onConfirmClickListener) {
        mOnConfirmClickListener = onConfirmClickListener;
    }

    private void onSelectChanged(@TargetDevice.Values int device){
        if (device == mTargetDevice){
            return;
        }

        if (isDialog){
            updateUI(device);
        }else {
            PromptDialogFragment.newBuilder(getContext())
                    .title(getString(R.string.menu_device_prompt_title))
                    .message(getString(R.string.menu_device_prompt_desc))
                    .negativeButtonText(getString(R.string.menu_device_prompt_negative))
                    .positiveButtonText(getString(R.string.menu_device_prompt_position))
                    .cancelable(false)
                    .onPositiveClickListener(v -> {
                        updateUI(device);
                        onConfirm();
                    })
                    .build()
                    .show(getFragmentManager(), "RegionChangePromptDialogFragment");
        }
    }
    private void updateUI(@TargetDevice.Values int device) {

        mTargetDevice = device;

        if (TargetDevice.NONE == mTargetDevice) {
            mConfirmBtn.setEnabled(false);
        } else {
            mConfirmBtn.setEnabled(true);
            if (TargetDevice.UKIT1 == mTargetDevice) {
                mUKit1SelectedIcon.setVisibility(View.VISIBLE);
                mUKit2SelectedIcon.setVisibility(View.GONE);
            }
            if (TargetDevice.UKIT2 == mTargetDevice) {
                mUKit1SelectedIcon.setVisibility(View.GONE);
                mUKit2SelectedIcon.setVisibility(View.VISIBLE);
            }
        }
    }


    private void onConfirm() {
        Settings.setTargetDevice(mTargetDevice);
        UserDataSynchronizer.getInstance().sync(false).subscribe(new SimpleRxSubscriber<>());
        if (mOnConfirmClickListener != null) {
            mOnConfirmClickListener.onClick(mConfirmBtn);
        }
    }
}
