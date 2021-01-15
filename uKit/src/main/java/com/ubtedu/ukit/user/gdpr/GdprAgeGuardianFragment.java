package com.ubtedu.ukit.user.gdpr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseFragment;

/**
 * 监护人选择界面
 */
public class GdprAgeGuardianFragment extends UKitBaseFragment {
    private Button mNegativeBtn;
    private Button mPositionBtn;

    private OnBtnClickListener mOnBtnClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_gdpr_age_guardian, null);
        mNegativeBtn = layout.findViewById(R.id.gdpr_age_negative_btn);
        mPositionBtn = layout.findViewById(R.id.gdpr_age_position_btn);

        bindSafeClickListener(mNegativeBtn);
        bindSafeClickListener(mPositionBtn);

        return layout;
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mPositionBtn) {
            if (mOnBtnClickListener != null) {
                mOnBtnClickListener.onPositiveClick();
            }
        } else if (v == mNegativeBtn) {
            if (mOnBtnClickListener != null) {
                mOnBtnClickListener.onNegativeClick();
            }
        }

    }

    public void setOnBtnClickListener(OnBtnClickListener listener) {
        this.mOnBtnClickListener = listener;
    }

    public interface OnBtnClickListener {
        void onPositiveClick();

        void onNegativeClick();
    }
}
