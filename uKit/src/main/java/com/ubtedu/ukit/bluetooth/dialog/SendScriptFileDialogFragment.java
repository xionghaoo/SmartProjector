/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.bluetooth.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubtedu.deviceconnect.libs.base.interfaces.URoConnectStatusChangeListener;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.dialog.widget.DialogProgressView;
import com.ubtedu.ukit.common.base.UKitBaseDialogFragment;
import com.ubtedu.ukit.common.view.FixedLottieAnimationView;

/**
 * @Author naOKi
 * @Date 2019/12/27
 **/
public class SendScriptFileDialogFragment extends UKitBaseDialogFragment implements URoConnectStatusChangeListener {
    private final static String PROCESSING_TITLE_KEY = "processing_title_key";
    private final static String PROCESSING_MESSAGE_KEY = "processing_message_key";
    private final static String FAILURE_TITLE_KEY = "failure_title_key";
    private final static String FAILURE_MESSAGE_KEY = "failure_message_key";
    private final static String FAILURE_CONFIRM_BTN_TEXT_KEY = "failure_confirm_btn_text_key";
    private final static String FAILURE_EXTRA_MESSAGE_KEY = "failure_message_extra_key";
    private final static String SUCCESS_TITLE_KEY = "success_title_key";
    private final static String SUCCESS_MESSAGE_KEY = "success_message_key";
    private final static String SUCCESS_EXTRA_MESSAGE_KEY = "success_message_extra_key";
    private final static String SUCCESS_CONFIRM_BTN_TEXT_KEY = "success_confirm_btn_text_key";
    private final static String SUCCESS_CANCEL_BTN_TEXT_KEY = "success_cancel_btn_text_key";
    private final static String AUTO_CLOSE_FLAG_KEY = "auto_close_flag_key";
    private String mProcessingTitle;
    private String mProcessingMessage;
    private String mFailureTitle;
    private String mFailureMessage;
    private String mFailureConfirmBtnText;
    private String mFailureExtraMessage;
    private String mSuccessTitle;
    private String mSuccessMessage;
    private String mSuccessExtraMessage;
    private String mSuccessConfirmBtnText;
    private String mSuccessCancelBtnText;

    private TextView titleTv;
    private TextView msgTv;
    private TextView extraMsgTv;
    private DialogProgressView dialogProgressView;
    private FixedLottieAnimationView animation1;
    private FixedLottieAnimationView animation2;

    private View sendingView;
    private View resultView;
    private ImageView resultIv;
    private Button positiveBtn;
    private Button navigateBtn;

    private Status mStatus = Status.NONE;
    private boolean markSuccess = false;
    private boolean mAutoClose = false;

    public enum Status {
        NONE, PROGRESSING, SUCCESS, FAILURE, CLOSE
    }

    private SendScriptDialogUI mUI;

    public SendScriptFileDialogFragment() {
        mUI = new SendScriptDialogUI() {
            @Override
            public void updateUiPercent(int percent) {
                updateSendingPercent(percent);
            }

            @Override
            public void updateUiStatus(Status status) {
                updateStatus(status);
            }
        };
    }

    private OnResultConfirmBtnClickListener mConfirmBtnClickListener;

    public void setConfirmBtnClickListener(OnResultConfirmBtnClickListener mConfirmBtnClickListener) {
        this.mConfirmBtnClickListener = mConfirmBtnClickListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            initArguments(savedInstanceState);
        } else {
            initArguments(getArguments());
        }
        BluetoothHelper.addConnectStatusChangeListener(this);
    }

    @Override
    public void dismiss() {
        BluetoothHelper.removeConnectStatusChangeListener(this);
        if (animation1.isAnimating()) {
            animation1.pauseAnimation();
        }
        if (animation2.isAnimating()) {
            animation2.pauseAnimation();
        }
        if (mDismissListener != null) {
            mDismissListener.onDismiss(mStatus);
            mDismissListener = null;
        }
        if (isVisible()) {
            super.dismiss();
        }
    }

    public SendScriptDialogUI getUI() {
        return mUI;
    }

    private void initArguments(Bundle args) {
        if (args != null) {
            mAutoClose = args.getBoolean(AUTO_CLOSE_FLAG_KEY, false);
            mProcessingTitle = args.getString(PROCESSING_TITLE_KEY, "");
            mProcessingMessage = args.getString(PROCESSING_MESSAGE_KEY, "");
            mSuccessTitle = args.getString(SUCCESS_TITLE_KEY, "");
            mSuccessMessage = args.getString(SUCCESS_MESSAGE_KEY, "");
            mSuccessExtraMessage = args.getString(SUCCESS_EXTRA_MESSAGE_KEY, "");
            mSuccessConfirmBtnText = args.getString(SUCCESS_CONFIRM_BTN_TEXT_KEY, getString(R.string.blockly_sending_script_file_ok));
            mSuccessCancelBtnText = args.getString(SUCCESS_CANCEL_BTN_TEXT_KEY, "");
            mFailureTitle = args.getString(FAILURE_TITLE_KEY, "");
            mFailureMessage = args.getString(FAILURE_MESSAGE_KEY, "");
            mFailureConfirmBtnText = args.getString(FAILURE_CONFIRM_BTN_TEXT_KEY, getString(R.string.blockly_sending_script_file_ok));
            mFailureExtraMessage = args.getString(FAILURE_EXTRA_MESSAGE_KEY, "");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PROCESSING_TITLE_KEY, mProcessingTitle);
        outState.putString(PROCESSING_MESSAGE_KEY, mProcessingMessage);
        outState.putString(FAILURE_TITLE_KEY, mFailureTitle);
        outState.putString(FAILURE_MESSAGE_KEY, mFailureMessage);
        outState.putString(FAILURE_CONFIRM_BTN_TEXT_KEY, mFailureConfirmBtnText);
        outState.putString(SUCCESS_TITLE_KEY, mSuccessTitle);
        outState.putString(SUCCESS_MESSAGE_KEY, mSuccessMessage);
        outState.putString(SUCCESS_CONFIRM_BTN_TEXT_KEY, mSuccessConfirmBtnText);
        outState.putBoolean(AUTO_CLOSE_FLAG_KEY, mAutoClose);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_send_script_file, null);
        if (isCancelable()) {
            mRootView = view.findViewById(R.id.dialog_fragment_root_view);
        }

        titleTv = view.findViewById(R.id.dialog_send_script_file_title_tv);
        msgTv = view.findViewById(R.id.dialog_send_script_file_msg_tv);
        extraMsgTv = view.findViewById(R.id.dialog_send_script_file_extra_msg_tv);
        dialogProgressView = view.findViewById(R.id.dialog_send_script_file_sending_progress_view);
        sendingView = view.findViewById(R.id.dialog_send_script_file_sending_lyt);
        resultView = view.findViewById(R.id.dialog_send_script_file_result_lyt);
        resultIv = view.findViewById(R.id.dialog_send_script_file_result_icon);
        positiveBtn = view.findViewById(R.id.dialog_send_script_file_result_positive_btn);
        navigateBtn = view.findViewById(R.id.dialog_send_script_file_result_negative_btn);

        animation1 = view.findViewById(R.id.dialog_send_script_file_sending_animation1);
        animation2 = view.findViewById(R.id.dialog_send_script_file_sending_animation2);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConfirmBtnClickListener!=null){
                    mConfirmBtnClickListener.onConfirmBtnClick(Status.SUCCESS.equals(mStatus));
                }else{
                    dismiss();
                }
            }
        });

        navigateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        dialogProgressView.setOnProgressFullListener(new DialogProgressView.OnProgressFullListener() {
            @Override
            public void onProgressFull() {
                if (markSuccess) {
                    updateStatus(Status.SUCCESS);
                }
            }
        });
        updateStatus(Status.PROGRESSING);

        return view;
    }

    private synchronized void updateStatus(Status status) {
        if (status == null || mStatus.equals(status)) {
            return;
        }
        if (Status.CLOSE.equals(status)){
            dismiss();
        }
        if (Status.SUCCESS.equals(mStatus) || Status.FAILURE.equals(mStatus)) {
            return;
        }
        /*
         * 这里的isFullProgress应该可以拿掉了，
         * dialogProgressView中主动animation.pause()，所以dialogProgressView.isProgressRunning()能准确反映出是否已经进度达到100，并且动画走完
         * 当需要更新为Success时，如果dialogProgressView.isProgressRunning()=true，说明正在走进度，走进度包括两层含义，一是进度没达到100，而是进度设置了100，但是动画没播放完
         * 所以设置markSuccess标志位，当进度走完时就自动走Success流程，这样改动的好处是哪怕先设置Success，再设置进度，也不影响结果
         * 如果dialogProgressView.isProgressRunning()=false，则之间进入Success流程
         */
        if (Status.SUCCESS.equals(status) && dialogProgressView.isProgressRunning()) {
            markSuccess = true;
            return;
        }
        mStatus = status;
        if (Status.PROGRESSING.equals(mStatus)) {
            titleTv.setText(mProcessingTitle);
            msgTv.setText(mProcessingMessage);
            if (!animation1.isAnimating()) {
                animation1.playAnimation();
            }
            if (!animation2.isAnimating()) {
                animation2.playAnimation();
            }
            sendingView.setVisibility(View.VISIBLE);
            resultView.setVisibility(View.GONE);
            extraMsgTv.setVisibility(View.GONE);
        } else {
            if (Status.FAILURE.equals(mStatus)) {
                titleTv.setText(mFailureTitle);
                msgTv.setText(mFailureMessage);
                positiveBtn.setText(mFailureConfirmBtnText);
                navigateBtn.setVisibility(View.GONE);
                resultIv.setImageResource(R.drawable.blockly_popup_pic_burn_fail);
                extraMsgTv.setText(mFailureExtraMessage);
            } else if (Status.SUCCESS.equals(mStatus)) {
                if (mAutoClose) {
                    dismiss();
                    return;
                }
                titleTv.setText(mSuccessTitle);
                msgTv.setText(mSuccessMessage);
                extraMsgTv.setText(mSuccessExtraMessage);
                positiveBtn.setText(mSuccessConfirmBtnText);
                if (TextUtils.isEmpty(mSuccessCancelBtnText)){
                    navigateBtn.setVisibility(View.GONE);
                }else{
                    navigateBtn.setVisibility(View.VISIBLE);
                    navigateBtn.setText(mSuccessCancelBtnText);
                }
                resultIv.setImageResource(R.drawable.blockly_popup_pic_burn_success);
            }
            if (animation1.isAnimating()) {
                animation1.pauseAnimation();
            }
            if (animation2.isAnimating()) {
                animation2.pauseAnimation();
            }
            sendingView.setVisibility(View.GONE);
            resultView.setVisibility(View.VISIBLE);
            extraMsgTv.setVisibility(View.VISIBLE);
        }
    }

    private synchronized void updateSendingPercent(int percent) {
        if (!Status.PROGRESSING.equals(mStatus)) {
            return;
        }
        dialogProgressView.setProgress(percent);
    }

    @Override
    public void onConnectStatusChanged(URoProduct product, URoConnectStatus connectStatus) {
        if (URoConnectStatus.DISCONNECTED.equals(connectStatus)) {
            updateStatus(Status.FAILURE);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String mProcessingTitle;
        private String mProcessingMessage;
        private String mFailureTitle;
        private String mFailureMessage;
        private String mFailureConfirmBtnText;
        private String mFailureExtraMessage;
        private String mSuccessTitle;
        private String mSuccessMessage;
        private String mSuccessConfirmBtnText;
        private String mSuccessCancelBtnText;
        private String mSuccessExtraMessage;
        private boolean mAutoClose = false;

        public SendScriptFileDialogFragment build() {
            SendScriptFileDialogFragment fragment = new SendScriptFileDialogFragment();
            fragment.setCancelable(false);
            Bundle args = new Bundle();
            args.putString(PROCESSING_TITLE_KEY, mProcessingTitle);
            args.putString(PROCESSING_MESSAGE_KEY, mProcessingMessage);
            args.putString(FAILURE_TITLE_KEY, mFailureTitle);
            args.putString(FAILURE_MESSAGE_KEY, mFailureMessage);
            args.putString(FAILURE_CONFIRM_BTN_TEXT_KEY, mFailureConfirmBtnText);
            args.putString(FAILURE_EXTRA_MESSAGE_KEY, mFailureExtraMessage);
            args.putString(SUCCESS_TITLE_KEY, mSuccessTitle);
            args.putString(SUCCESS_MESSAGE_KEY, mSuccessMessage);
            args.putString(SUCCESS_EXTRA_MESSAGE_KEY,mSuccessExtraMessage);
            args.putString(SUCCESS_CONFIRM_BTN_TEXT_KEY, mSuccessConfirmBtnText);
            args.putString(SUCCESS_CANCEL_BTN_TEXT_KEY, mSuccessCancelBtnText);
            args.putBoolean(AUTO_CLOSE_FLAG_KEY, mAutoClose);
            fragment.setArguments(args);
            return fragment;
        }

        public Builder processingText(String title, String message) {
            mProcessingTitle = title;
            mProcessingMessage = message;
            return this;
        }

        public Builder failureText(String title, String message, String confirmBtnText) {
            mFailureTitle = title;
            mFailureMessage = message;
            mFailureConfirmBtnText = confirmBtnText;
            return this;
        }

        public Builder successText(String title, String message, String confirmBtnText) {
            mSuccessTitle = title;
            mSuccessMessage = message;
            mSuccessConfirmBtnText = confirmBtnText;
            return this;
        }

        public Builder successCancelBtnText(String cancelBtnText) {
            mSuccessCancelBtnText = cancelBtnText;
            return this;
        }

        public Builder successExtraText(String message) {
            mSuccessExtraMessage = message;
            return this;
        }

        public Builder failureExtraText(String message) {
            mFailureExtraMessage = message;
            return this;
        }

        public Builder autoClose(boolean autoClose) {
            mAutoClose = autoClose;
            return this;
        }
    }

    public interface SendScriptDialogUI {
        void updateUiPercent(int percent);

        void updateUiStatus(Status status);
    }

    public interface OnResultConfirmBtnClickListener {
        void onConfirmBtnClick(boolean isSuccess);
    }
}
