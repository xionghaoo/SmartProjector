/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.utils.SharedPreferenceUtils;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.ServerConfig;
import com.ubtedu.ukit.application.ServerEnv;
import com.ubtedu.ukit.common.base.UKitBaseDialogFragment;

/**
 * @Author qinicy
 * @Date 2019/4/22
 **/
public class ServerConfigDialogFragment extends UKitBaseDialogFragment {
    private RadioGroup mRadioGroup;
    private Button mPositiveBtn;
    private Button mNegativeBtn;
    private ServerEnv mServerEnv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_fragment_server_config, null);
        mRadioGroup = root.findViewById(R.id.server_config_rg);
        mPositiveBtn = root.findViewById(R.id.server_config_positive_btn);
        mNegativeBtn = root.findViewById(R.id.server_config_negative_btn);
        bindSafeClickListener(mPositiveBtn);
        bindSafeClickListener(mNegativeBtn);
        ServerEnv env = ServerConfig.getServerEnvConfig();
        switch (env) {
            case RELEASE:
                ((RadioButton) mRadioGroup.getChildAt(0)).setChecked(true);
                break;
            case PRERELEASE:
                ((RadioButton) mRadioGroup.getChildAt(1)).setChecked(true);
                break;
            case TEST:
                ((RadioButton) mRadioGroup.getChildAt(2)).setChecked(true);
                break;
        }
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.server_config_release_rb) {
                    mServerEnv = ServerEnv.RELEASE;
                } else if (checkedId == R.id.server_config_prerelease_rb) {
                    mServerEnv = ServerEnv.PRERELEASE;
                } else if (checkedId == R.id.server_config_test_rb) {
                    mServerEnv = ServerEnv.TEST;
                }
            }
        });
        return root;
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mNegativeBtn) {
            dismiss();
        }
        if (v == mPositiveBtn) {
            if(mServerEnv != null){
                SharedPreferenceUtils.getInstance(getContext()).setValue(ServerConfig.SP_SERVER_CONFIG, mServerEnv.name());
            }
            dismiss();
        }
    }
}
