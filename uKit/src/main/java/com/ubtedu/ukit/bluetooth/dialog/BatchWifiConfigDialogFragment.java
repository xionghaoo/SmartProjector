/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.bluetooth.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.bluetooth.dialog.widget.DialogProgressView;
import com.ubtedu.ukit.common.base.UKitBaseDialogFragment;
import com.ubtedu.ukit.common.view.FixedLottieAnimationView;

/**
 * @Author naOKi
 * @Date 2019/12/27
 **/
public class BatchWifiConfigDialogFragment extends UKitBaseDialogFragment {

    private DialogProgressView progressView;

    private FixedLottieAnimationView animation;

    public BatchWifiConfigDialogFragment() {
    }

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
        if (args != null) {
        }
    }

    @Override
    public void dismiss() {
        if (animation.isAnimating()) {
            animation.pauseAnimation();
        }
        if (isVisible()) {
            super.dismiss();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_batch_config_wifi, null);
        if (isCancelable()) {
            mRootView = view.findViewById(R.id.dialog_fragment_root_view);
        }
        progressView = view.findViewById(R.id.dialog_batch_wifi_config_progress_view);
        animation = view.findViewById(R.id.dialog_batch_wifi_config_animation);
        progressView.setOnProgressFullListener(new DialogProgressView.OnProgressFullListener() {
            @Override
            public void onProgressFull() {
                dismiss();
            }
        });
        return view;
    }

    public void setProgress(int progress) {
        progressView.setProgress(progress);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        public BatchWifiConfigDialogFragment build() {
            BatchWifiConfigDialogFragment fragment = new BatchWifiConfigDialogFragment();
            fragment.setCancelable(false);
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }
    }

}
