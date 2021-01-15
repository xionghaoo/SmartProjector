/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.utils.KeyBoardHelper;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseDialogFragment;
import com.ubtedu.ukit.common.view.UKitCharsInputFilter;

/**
 * @Author qinicy
 * @Date 2018/11/17
 **/
public class PromptEditDialogFragment extends UKitBaseDialogFragment {
    private final static String TITTLE_KEY = "tittle";
    private final static String CONTENT_KEY = "message";
    private final static String POSITIVE_TEXT_KEY = "positive_text";
    private final static String NEGATIVE_TEXT_KEY = "negative_text";
    private final static String ALLOW_CONTENT_UNCHANGED_KEY = "allow_unchanged";
    private final static String AUTO_SHOW_SOFT_KEYBOARD_KEY = "auto_show_soft_keyboard";
    private final static String CONTENT_HINT_KEY = "message_hint";
    private final static String MAX_LENGTH_KEY = "max_length";
    private static final int DEFAULT_LENGTH = 20;

    private EditText mProjectNameEdt;
    private View mClearBtn;
    private Button mNegativeBtn;
    private Button mPositiveBtn;
    private String mContentText;
    private String mTitle;
    private boolean isPositive;
    private boolean mAllowUnchanged;
    private boolean mAutoShowSoftKeyboard;

    private View mContentView;
    private OnConfirmClickListener mOnConfirmClickListener;
    private View.OnClickListener mCancelClickListener;
    private View mRootLayout;
    private TextView mTittleTv;
    private String mContentTextHint;
    private int mMaxLength;
    private String mSourceText;
    private String mPositiveText;
    private String mNegativeText;
    private InputFilter[] mEdtInputFilters;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            initArguments(savedInstanceState);
        } else {
            initArguments(getArguments());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.dialog_edit_text, null);
        mRootLayout = layout.findViewById(R.id.dialog_fragment_root_view);
        bindClickListener(mRootLayout);
        if (isCancelable()) {
            mRootView = mRootLayout;
        }
        mContentView = layout.findViewById(R.id.dialog_project_rename_content_lyt);
        bindSafeClickListener(mContentView);
        mTittleTv = layout.findViewById(R.id.dialog_project_rename_title_tv);
        mTittleTv.setText(mTitle);
        mClearBtn = layout.findViewById(R.id.dialog_project_rename_input_clear_btn);
        mNegativeBtn = layout.findViewById(R.id.dialog_project_rename_negative_btn);
        mPositiveBtn = layout.findViewById(R.id.dialog_project_rename_positive_btn);
        mProjectNameEdt = layout.findViewById(R.id.dialog_project_rename_input_edt);
        if (!TextUtils.isEmpty(mPositiveText)) {
            mPositiveBtn.setText(mPositiveText);
        }
        if (!TextUtils.isEmpty(mNegativeText)) {
            mNegativeBtn.setText(mNegativeText);
        }
        if (mMaxLength == 0) {
            mMaxLength = DEFAULT_LENGTH;
        }
        if (mEdtInputFilters == null || mEdtInputFilters.length == 0) {
            mProjectNameEdt.setFilters(new InputFilter[]{new UKitCharsInputFilter(mMaxLength)});
        } else {
            mProjectNameEdt.setFilters(mEdtInputFilters);
        }
        if (!TextUtils.isEmpty(mContentTextHint)) {
            mProjectNameEdt.setHint(mContentTextHint);
        }

        mProjectNameEdt.post(new Runnable() {
            @Override
            public void run() {
                if (mAutoShowSoftKeyboard) {
                    mProjectNameEdt.setFocusable(true);
                    mProjectNameEdt.setFocusableInTouchMode(true);
                    mProjectNameEdt.requestFocus();
                    KeyBoardHelper.openSoftKeyboard(mProjectNameEdt.getContext());
                }

                if (mContentText != null) {
                    mProjectNameEdt.setText(mContentText);
                    mProjectNameEdt.setSelection(mContentText.length());
                }
            }
        });

        mProjectNameEdt.addTextChangedListener(new TextWatcher() {
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
        updateButtonsState();
        return layout;

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN|WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    private void initArguments(Bundle args) {
        if (args != null) {
            mTitle = args.getString(TITTLE_KEY);
            mContentText = args.getString(CONTENT_KEY);
            mSourceText = mContentText;
            mContentTextHint = args.getString(CONTENT_HINT_KEY);
            mMaxLength = args.getInt(MAX_LENGTH_KEY);
            mAllowUnchanged = args.getBoolean(ALLOW_CONTENT_UNCHANGED_KEY);
            mAutoShowSoftKeyboard = args.getBoolean(AUTO_SHOW_SOFT_KEYBOARD_KEY);
            mPositiveText = args.getString(POSITIVE_TEXT_KEY);
            mNegativeText = args.getString(NEGATIVE_TEXT_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TITTLE_KEY, mTitle);
        outState.putString(CONTENT_KEY, mContentText);
        outState.putString(CONTENT_HINT_KEY, mContentTextHint);
        outState.putInt(MAX_LENGTH_KEY, mMaxLength);
        outState.putBoolean(ALLOW_CONTENT_UNCHANGED_KEY, mAllowUnchanged);
        outState.putBoolean(AUTO_SHOW_SOFT_KEYBOARD_KEY, mAutoShowSoftKeyboard);
        outState.putString(POSITIVE_TEXT_KEY, mPositiveText);
        outState.putString(NEGATIVE_TEXT_KEY, mNegativeText);
    }


    public void setTitle(String title) {
        mTitle = title;
    }

    public void setContentText(String text) {
        mContentText = text;
    }

    public void setContentTextHint(String hint) {
        mContentTextHint = hint;
    }

    public void setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        mOnConfirmClickListener = onConfirmClickListener;
    }

    public void setCancelClickListener(View.OnClickListener cancelClickListener) {
        mCancelClickListener = cancelClickListener;
    }

    public void setEditInputFilters(InputFilter[] filters) {
        mEdtInputFilters = filters;
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
            mProjectNameEdt.setText("");
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
        boolean isSameToSource = mSourceText != null && mSourceText.equals(mContentText);
        boolean hasText = mContentText != null && mContentText.length() > 0&& mContentText.length()<=mMaxLength;
        mClearBtn.setVisibility(hasText ? View.VISIBLE : View.INVISIBLE);
        if (!hasText || (isSameToSource && !mAllowUnchanged)) {
            mPositiveBtn.setEnabled(false);
            mPositiveBtn.setAlpha(0.5f);
        } else {
            mPositiveBtn.setEnabled(true);
            mPositiveBtn.setAlpha(1.0f);
        }
    }

    public interface OnConfirmClickListener {
        boolean confirm(String text);
    }

    public static PromptEditDialogFragment.Builder newBuilder() {
        return new PromptEditDialogFragment.Builder();
    }

    public static class Builder {

        private String mTitle;
        private String mMessage;
        private String mMessageHint;
        private String mNegativeText;
        private String mPositiveText;
        private boolean mAllowUnchanged;
        private boolean mAutoShowSoftKeyboard = true;
        private int mMaxLength;
        private boolean isCancelable = true;
        private OnConfirmClickListener mConfirmClickListener;
        private View.OnClickListener mCancelClickListener;
        private InputFilter[] mEdtInputFilters;

        public PromptEditDialogFragment.Builder title(String title) {
            mTitle = title;
            return this;
        }

        public PromptEditDialogFragment.Builder message(String message) {
            mMessage = message;
            mAllowUnchanged = false;
            return this;
        }

        public PromptEditDialogFragment.Builder allowMessageUnchanged(boolean allowUnchanged) {
            mAllowUnchanged = allowUnchanged;
            return this;
        }

        public PromptEditDialogFragment.Builder hint(String hint) {
            mMessageHint = hint;
            return this;
        }

        public PromptEditDialogFragment.Builder maxLength(int len) {
            mMaxLength = len;
            return this;
        }

        public PromptEditDialogFragment.Builder positiveButtonText(String text) {
            mPositiveText = text;
            return this;
        }

        public PromptEditDialogFragment.Builder negativeButtonText(String text) {
            mNegativeText = text;
            return this;
        }

        public PromptEditDialogFragment.Builder cancelable(boolean isCancelable) {
            this.isCancelable = isCancelable;
            return this;
        }

        public PromptEditDialogFragment.Builder autoShowSoftKeyboard(boolean show) {
            this.mAutoShowSoftKeyboard = show;
            return this;
        }

        public PromptEditDialogFragment.Builder onConfirmClickListener(OnConfirmClickListener listener) {
            mConfirmClickListener = listener;
            return this;
        }

        public PromptEditDialogFragment.Builder onCancelClickListener(View.OnClickListener listener) {
            mCancelClickListener = listener;
            return this;
        }

        public PromptEditDialogFragment.Builder editInputFilters(InputFilter[] filters) {
            mEdtInputFilters = filters;
            return this;
        }

        public PromptEditDialogFragment build() {
            PromptEditDialogFragment fragment = new PromptEditDialogFragment();
            fragment.setCancelable(isCancelable);
            fragment.setOnConfirmClickListener(mConfirmClickListener);
            fragment.setEditInputFilters(mEdtInputFilters);
            Bundle args = new Bundle();
            args.putString(TITTLE_KEY, mTitle);
            args.putString(CONTENT_KEY, mMessage);
            args.putString(CONTENT_HINT_KEY, mMessageHint);
            args.putInt(MAX_LENGTH_KEY, mMaxLength);
            args.putBoolean(ALLOW_CONTENT_UNCHANGED_KEY, mAllowUnchanged);
            args.putBoolean(AUTO_SHOW_SOFT_KEYBOARD_KEY, mAutoShowSoftKeyboard);
            args.putString(POSITIVE_TEXT_KEY, mPositiveText);
            args.putString(NEGATIVE_TEXT_KEY, mNegativeText);
            fragment.setArguments(args);
            return fragment;
        }
    }
}
