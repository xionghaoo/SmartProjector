/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.blockly;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseDialogFragment;
import com.ubtedu.ukit.project.blockly.model.AudioItem;

/**
 * @Author qinicy
 * @Date 2019/4/17
 **/
public abstract class AudioRecordDialogFragment extends UKitBaseDialogFragment implements AudioRecordUI {
    private Button mNegativeBtn;
    private Button mPositiveBtn;
    private LottieAnimationView mAnimationView;
    private TextView mRecordProgressTv;
    private View mAudioProcessingArea;
    private View mAudioProcessingIcon;
    private ObjectAnimator mProcessingAnim;
    private AudioRecordPresenter mPresenter;
    private OnViewCreatedCallback mViewCreatedCallback;
    private RecordResult recordResult = FAILURE_RECORD_RESULT;
    private static final RecordResult FAILURE_RECORD_RESULT = new RecordResult(false, null, null);
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        mPresenter = createPresenter(this);
        mPresenter.init();
    }

    protected abstract @NonNull AudioRecordPresenter createPresenter(AudioRecordUI ui);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_fragment_audio_record, null);
        mNegativeBtn = root.findViewById(R.id.audio_record_negative_btn);
        mPositiveBtn = root.findViewById(R.id.audio_record_positive_btn);
        mAnimationView = root.findViewById(R.id.audio_record_lottie_view);
        mRecordProgressTv = root.findViewById(R.id.audio_record_time_tv);
        mAudioProcessingArea = root.findViewById(R.id.audio_processing_area);
        mAudioProcessingIcon = root.findViewById(R.id.audio_processing_icon);
        bindSafeClickListener(mNegativeBtn);
        bindSafeClickListener(mPositiveBtn);
        return root;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.startRecordAudio();
        if (mViewCreatedCallback != null){
            mViewCreatedCallback.onViewCreated();
        }
    }

    @Override
    protected void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
        if (!isVisibleToUser){
            cancel();
        }
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mNegativeBtn) {
            cancel();
        }
        if (v == mPositiveBtn) {
            mPresenter.stopRecordAudio();
        }
    }

    public void cancel(){
        mPresenter.cancel();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mProcessingAnim != null && mProcessingAnim.isRunning()) {
            mProcessingAnim.cancel();
        }
        super.onDismiss(dialog);
        if (mDismissListener != null) {
            mDismissListener.onDismiss(recordResult.success, recordResult.msg, recordResult.audioItem);
        }
        mPresenter.release();
    }

    @Override
    public void updateRecordProgress(String second) {
        mRecordProgressTv.setText(second);
    }

    @Override
    public void onRecordStarted() {
        mAnimationView.playAnimation();
    }

    @Override
    public void onRecordFinish(boolean success, String msg, AudioItem audioItem) {
        recordResult = new RecordResult(success, msg, audioItem);
        mAnimationView.cancelAnimation();
        dismiss();
    }

    @Override
    public void onRecordProcessing() {
        mPositiveBtn.setVisibility(View.GONE);
        mAudioProcessingArea.setVisibility(View.VISIBLE);
        if (mProcessingAnim == null) {
            mProcessingAnim = ObjectAnimator.ofFloat(mAudioProcessingIcon, "rotation", 0f, 360f);
            mProcessingAnim.setRepeatMode(ValueAnimator.RESTART);
            mProcessingAnim.setRepeatCount(ValueAnimator.INFINITE);
            mProcessingAnim.setDuration(1000);
        }
        if (!mProcessingAnim.isRunning()) {
            mProcessingAnim.start();
        }
    }

    public void setViewCreatedCallback(OnViewCreatedCallback viewCreatedCallback) {
        mViewCreatedCallback = viewCreatedCallback;
    }

    public interface OnViewCreatedCallback{
        void onViewCreated();
    }

    private static class RecordResult {
        private Boolean success;
        private String msg;
        private AudioItem audioItem;
        public RecordResult(Boolean success, String msg, AudioItem audioItem) {
            this.success = success;
            this.msg = msg;
            this.audioItem = audioItem;
        }
    }
}
