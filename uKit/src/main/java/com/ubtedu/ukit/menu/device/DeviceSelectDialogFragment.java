/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.device;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseDialogFragment;

/**
 * @Author qinicy
 * @Date 2019-12-25
 **/
public class DeviceSelectDialogFragment extends UKitBaseDialogFragment {
    private DeviceSelectFragment mSelectFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        mSelectFragment = new DeviceSelectFragment();
        mSelectFragment.setUIMode(true);
        mSelectFragment.setOnConfirmClickListener(view -> dismiss());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.dialog_device_select, null);
        getChildFragmentManager().beginTransaction().add(R.id.device_select_container,mSelectFragment).commit();
        return layout;
    }
}
