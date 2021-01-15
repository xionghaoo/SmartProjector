/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.about;

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
 * @Date 2018/12/15
 **/
public class ContractsDialogFragment extends UKitBaseDialogFragment {
    private View mCloseBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.dialog_contract_us, null);
        if (isCancelable()) {
            super.mRootView = root.findViewById(R.id.dialog_fragment_root_view);
        }
        mCloseBtn = root.findViewById(R.id.menu_about_contract_us_close_btn);
        bindSafeClickListener(mCloseBtn);
        return root;
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mCloseBtn) {
            dismiss();
        }
    }
}
