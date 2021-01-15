package com.ubtedu.ukit.bluetooth.wifi;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.utils.KeyBoardHelper;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseDialogFragment;

public class PasswordInputDialog extends UKitBaseDialogFragment {
    private final static String CONTENT_KEY = "message";

    protected EditText mPasswordEdt;
    private View mClearBtn;
    private Button mNegativeBtn;
    private Button mPositiveBtn;
    private String mContentText;
    private boolean isPositive;
    private View mContentView;
    private PasswordInputDialog.OnConfirmClickListener mOnConfirmClickListener;
    private View mRootLayout;
    private CheckBox mPasswordEyeCheckBox;
    private TextView mTitleTv;
    private String mTitleText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            initArguments(savedInstanceState);
        } else {
            initArguments(getArguments());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.dialog_input_password, null);
        mRootLayout = layout.findViewById(R.id.dialog_fragment_root_view);
        bindClickListener(mRootLayout);
        if (isCancelable()) {
            mRootView = mRootLayout;
        }
        mContentView = layout.findViewById(R.id.dialog_password_content_lyt);
        bindSafeClickListener(mContentView);
        mTitleTv = layout.findViewById(R.id.dialog_password_title_tv);
        mClearBtn = layout.findViewById(R.id.dialog_password_input_clear_btn);
        mNegativeBtn = layout.findViewById(R.id.dialog_password_negative_btn);
        mPositiveBtn = layout.findViewById(R.id.dialog_password_positive_btn);
        mPasswordEdt = layout.findViewById(R.id.dialog_password_input_edt);

        mPasswordEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mContentText = s.toString().trim();
                updateButtonsState();
            }
        });
        bindClickListener(mClearBtn);
        bindSafeClickListener(mPositiveBtn);
        bindSafeClickListener(mNegativeBtn);
        mPasswordEyeCheckBox = layout.findViewById(R.id.dialog_password_input_eye_btn);
        mPasswordEyeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPasswordEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    mPasswordEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
        mPasswordEdt.setText(TextUtils.isEmpty(mContentText) ? "" : mContentText);
        if (!TextUtils.isEmpty(mTitleText)) {
            mTitleTv.setText(mTitleText);
        }
        updateButtonsState();
        return layout;

    }

    @Override
    public boolean isCancelable() {
        return false;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    private void initArguments(Bundle args) {
        if (args != null) {
            mContentText = args.getString(CONTENT_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CONTENT_KEY, mContentText);
    }


    public void setContentText(String text) {
        mContentText = text;
    }

    public void setTitleText(String text) {
        mTitleText = text;
    }

    public void setOnConfirmClickListener(PasswordInputDialog.OnConfirmClickListener onConfirmClickListener) {
        mOnConfirmClickListener = onConfirmClickListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mDismissListener != null) {
            mDismissListener.onDismiss(isPositive, mContentText);
        }
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mClearBtn) {
            mPasswordEdt.setText("");
        }
        if (v == mNegativeBtn) {
            isPositive = false;
            dismiss();
        }
        if (v == mPositiveBtn) {
            handlePositive();
        }

        if (v == mContentView || v == mRootLayout) {
            KeyBoardHelper.hideSoftKeyBoard(v);
        }
    }

    private void handlePositive() {
        if (mOnConfirmClickListener != null) {
            boolean close = !mOnConfirmClickListener.confirm(mContentText);
            if (close) {
                isPositive = true;
                dismiss();
            }
        }
    }

    private void updateButtonsState() {
        boolean hasText = mContentText != null && mContentText.length() > 0;
        mClearBtn.setVisibility(hasText ? View.VISIBLE : View.INVISIBLE);
        if (mIgnoreEmpty || hasText) {
            mPositiveBtn.setEnabled(true);
            mPositiveBtn.setAlpha(1.0f);
        } else {
            mPositiveBtn.setEnabled(false);
            mPositiveBtn.setAlpha(0.5f);
        }
    }

    private boolean mIgnoreEmpty = false;

    public void ignoreTextEmpty(boolean ignore) {
        mIgnoreEmpty = ignore;
    }

    public interface OnConfirmClickListener {
        boolean confirm(String text);
    }

}
