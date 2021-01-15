/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.controller.settings.slider.range;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.ui.widgets.seekbar.OnRangeChangedListener;
import com.ubtedu.alpha1x.ui.widgets.seekbar.RangeSeekBar;
import com.ubtedu.alpha1x.utils.KeyBoardHelper;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseDialogFragment;
import com.ubtedu.ukit.project.bridge.api.ToastHelper;
import com.ubtedu.ukit.project.controller.settings.interfaces.IControllerRangeConfirmListener;

import java.text.DecimalFormat;

/**
 * @Author naOKi
 * @Date 2018/11/30
 **/
public class ControllerRangeDialogFragment extends UKitBaseDialogFragment implements KeyBoardHelper.OnKeyBoardStatusChangeListener {
    private final static String EXTRA_MAX_VALUE = "_extra_max_value";
    private final static String EXTRA_MIN_VALUE = "_extra_min_value";

    private EditText mMaxValueEdt;
    private EditText mMinValueEdt;
    private View mNegativeBtn;
    private View mPositiveBtn;
    private View mContentView;
    private RangeSeekBar mRangeSeekBar;
    private DecimalFormat mRangeSeekBarIndicatorFormatter = new DecimalFormat("0");

    private int mMaxValue = DEFAULT_MAX_VALUE;
    private int mMinValue = DEFAULT_MIN_VALUE;

    private static final int MAX_RANGE = 1000;
    private static final int MIN_RANGE = -1000;

    private static final int DEFAULT_MAX_VALUE = 500;
    private static final int DEFAULT_MIN_VALUE = -500;

    private IControllerRangeConfirmListener mControllerRangeListener;

    private KeyBoardHelper mKeyBoardHelper;

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
            mMaxValue = args.getInt(EXTRA_MAX_VALUE, DEFAULT_MAX_VALUE);
            mMinValue = args.getInt(EXTRA_MIN_VALUE, DEFAULT_MIN_VALUE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_MAX_VALUE, mMaxValue);
        outState.putInt(EXTRA_MIN_VALUE, mMinValue);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_project_controller_range_setting, null);
        if (isCancelable()) {
            mRootView = view.findViewById(R.id.dialog_fragment_root_view);
        }

        mContentView = view.findViewById(R.id.dialog_project_controller_range_content_lyt);
        bindSafeClickListener(mContentView);

        mRangeSeekBar = view.findViewById(R.id.dialog_project_controller_range_seek_bar);
        mRangeSeekBar.setRange(MIN_RANGE, MAX_RANGE, 1);
        mRangeSeekBar.setProgress(mMinValue, mMaxValue);
        mRangeSeekBar.setIndicatorTextDecimalFormat(mRangeSeekBarIndicatorFormatter);
        mRangeSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                mMaxValueEdt.setText(mRangeSeekBarIndicatorFormatter.format(rightValue));
                mMinValueEdt.setText(mRangeSeekBarIndicatorFormatter.format(leftValue));
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });

        mNegativeBtn = view.findViewById(R.id.dialog_project_controller_range_negative_btn);
        mPositiveBtn = view.findViewById(R.id.dialog_project_controller_range_positive_btn);
        bindSafeClickListener(mPositiveBtn);
        bindSafeClickListener(mNegativeBtn);

        mMaxValueEdt = view.findViewById(R.id.dialog_project_controller_range_max_edt);
        mMinValueEdt = view.findViewById(R.id.dialog_project_controller_range_min_edt);
        if (mMaxValue > MAX_RANGE || mMaxValue < MIN_RANGE) {
            mMaxValueEdt.setText("");
        } else {
            mMaxValueEdt.setText(String.valueOf(mMaxValue));
        }
        if (mMinValue > MAX_RANGE || mMinValue < MIN_RANGE) {
            mMinValueEdt.setText("");
        } else {
            mMinValueEdt.setText(String.valueOf(mMinValue));
        }
        updateButtonsState();

        mMaxValueEdt.clearFocus();
        mMinValueEdt.clearFocus();

        initMaxEdtListeners();
        initMinEdtListeners();

        mKeyBoardHelper = new KeyBoardHelper(getActivity());
        mKeyBoardHelper.onCreate();
        mKeyBoardHelper.setOnKeyBoardStatusChangeListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        mKeyBoardHelper.onDestory();
        super.onDestroyView();
    }


    private void showToast(String msg) {
        ToastHelper.toastShort(msg);
    }

    TextWatcher mMinWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                String numberStr = s.toString();
                String fixNumberStr = numberStr.replaceAll("[^\\-0-9]", "");
                if (fixNumberStr.length() > 1) {
                    fixNumberStr = fixNumberStr.substring(0, 1) + fixNumberStr.substring(1).replaceAll("-", "");
                }
                if (!TextUtils.equals(numberStr, fixNumberStr)) {
                    s.clear();
                    s.append(fixNumberStr);
                    return;
                }
                int number = Integer.parseInt(numberStr);
                if (number < MIN_RANGE) {
                    s.clear();
                    s.append(String.valueOf(MIN_RANGE));
                    showToast(getString(R.string.project_controller_out_of_range_error_msg));
                }
            } catch (Exception e) {
                // ignore
            }
            updateButtonsState();
        }
    };

    TextWatcher mMaxWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                String numberStr = s.toString();
                String fixNumberStr = numberStr.replaceAll("[^\\-0-9]", "");
                if (fixNumberStr.length() > 1) {
                    fixNumberStr = fixNumberStr.substring(0, 1) + fixNumberStr.substring(1).replaceAll("-", "");
                }
                if (!TextUtils.equals(numberStr, fixNumberStr)) {
                    s.clear();
                    s.append(fixNumberStr);
                    return;
                }
                int number = Integer.parseInt(numberStr);
                if (number > MAX_RANGE) {
                    s.clear();
                    s.append(String.valueOf(MAX_RANGE));
                    showToast(getString(R.string.project_controller_out_of_range_error_msg));
                }
            } catch (Exception e) {
                // ignore
            }
            updateButtonsState();
        }
    };

    private boolean checkRange() {
        String maxText = mMaxValueEdt.getText().toString();
        String minText = mMinValueEdt.getText().toString();
        int maxValue, minValue;
        try {
            maxValue = Integer.parseInt(maxText);
            minValue = Integer.parseInt(minText);
            if (maxValue > minValue && minValue >= MIN_RANGE && maxValue <= MAX_RANGE) {
                mMaxValue = maxValue;
                mMinValue = minValue;
                return true;
            } else {
                showToast(getString(R.string.project_controller_range_invalid_msg));
            }
        } catch (Exception e) {
            showToast(getString(R.string.project_controller_out_of_range_error_msg));
        }
        return false;
    }

    private boolean checkMax() {
        if (TextUtils.isEmpty(mMaxValueEdt.getText().toString())) {
            showToast(getString(R.string.project_controller_max_out_of_range_error_msg, String.valueOf(getMinEdtValue() + 1)));
            mMaxValueEdt.setText(String.valueOf(getMinEdtValue() + 1));
            mMaxValueEdt.setSelection(mMaxValueEdt.getText().length());
            return false;
        } else {
            try {
                if (getMaxEdtValue() <= getMinEdtValue()) {
                    showToast(getString(R.string.project_controller_max_out_of_range_error_msg, String.valueOf(getMinEdtValue() + 1)));
                    mMaxValueEdt.setText(String.valueOf(getMinEdtValue() + 1));
                    mMaxValueEdt.setSelection(mMaxValueEdt.getText().length());
                    return false;
                } else {
                    return true;
                }
            } catch (NumberFormatException e) {
                showToast(getString(R.string.project_controller_max_out_of_range_error_msg, String.valueOf(getMinEdtValue() + 1)));
                mMaxValueEdt.setText(String.valueOf(getMinEdtValue() + 1));
                mMaxValueEdt.setSelection(mMaxValueEdt.getText().length());
                return false;
            }

        }
    }

    private boolean checkMin() {
        if (TextUtils.isEmpty(mMinValueEdt.getText().toString())) {
            showToast(getString(R.string.project_controller_min_out_of_range_error_msg, String.valueOf(getMaxEdtValue() - 1)));
            mMinValueEdt.setText(String.valueOf(getMaxEdtValue() - 1));
            mMinValueEdt.setSelection(mMinValueEdt.getText().length());
            return false;
        } else {
            try {
                if (getMaxEdtValue() <= getMinEdtValue()) {
                    showToast(getString(R.string.project_controller_min_out_of_range_error_msg, String.valueOf(getMaxEdtValue() - 1)));
                    mMinValueEdt.setText(String.valueOf(getMaxEdtValue() - 1));
                    mMinValueEdt.setSelection(mMinValueEdt.getText().length());
                    return false;
                } else {
                    return true;
                }
            } catch (NumberFormatException e) {
                showToast(getString(R.string.project_controller_min_out_of_range_error_msg, String.valueOf(getMaxEdtValue() - 1)));
                mMinValueEdt.setText(String.valueOf(getMaxEdtValue() - 1));
                mMinValueEdt.setSelection(mMinValueEdt.getText().length());
                return false;
            }

        }
    }

    private int getMinEdtValue() {
        return Integer.parseInt(mMinValueEdt.getText().toString());
    }

    private int getMaxEdtValue() {
        return Integer.parseInt(mMaxValueEdt.getText().toString());
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        if (v == mNegativeBtn) {
            dismiss();
        }
        if (v == mPositiveBtn) {
            if (!checkRange()) {
                return;
            }
            if (mControllerRangeListener != null) {
                mControllerRangeListener.onRangeConfirmed(mMinValue, mMaxValue);
            }
            dismiss();
        }
        if (v == mContentView) {
            boolean canCloseInput = true;
            if (mMinValueEdt.hasFocus()) {
                canCloseInput = checkMin();
            }
            if (mMaxValueEdt.hasFocus()) {
                canCloseInput = checkMax();
            }
            if (canCloseInput) {
                InputMethodManager imm =
                        (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                setRangeSeekBarAndClearEdtFocus();
            }
        }
    }

    private void updateButtonsState() {
        String maxText = mMaxValueEdt.getText().toString();
        String minText = mMinValueEdt.getText().toString();
        if (TextUtils.isEmpty(maxText) || TextUtils.isEmpty(minText)) {
            mPositiveBtn.setEnabled(false);
            mPositiveBtn.setAlpha(0.5f);
        } else {
            mPositiveBtn.setEnabled(true);
            mPositiveBtn.setAlpha(1.0f);
        }
    }

    private void initMaxEdtListeners() {
        mMaxValueEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == KeyEvent.KEYCODE_ENDCALL) {
                    boolean isValueValid = checkMax();
                    if (isValueValid) {
                        setRangeSeekBarAndClearEdtFocus();
                    }
                    return !isValueValid;
                }
                return false;
            }
        });
        mMaxValueEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkMax();
                    mMaxValueEdt.setFocusableInTouchMode(false);
                }
            }
        });
        mMaxValueEdt.addTextChangedListener(mMaxWatcher);
        mMaxValueEdt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mMaxValueEdt.setFocusableInTouchMode(true);
                return false;
            }
        });
    }

    private void initMinEdtListeners() {
        mMinValueEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == KeyEvent.KEYCODE_ENDCALL) {
                    boolean isValueValid = checkMin();
                    if (isValueValid) {
                        setRangeSeekBarAndClearEdtFocus();
                    }
                    return !isValueValid;
                }
                return false;
            }
        });

        mMinValueEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkMin();
                    mMinValueEdt.setFocusableInTouchMode(false);
                }
            }
        });
        mMinValueEdt.addTextChangedListener(mMinWatcher);

        mMinValueEdt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mMinValueEdt.setFocusableInTouchMode(true);
                return false;
            }
        });
    }

    @Override
    public void OnKeyBoardPop(int keyBoardheight) {
    }

    @Override
    public void OnKeyBoardClose(int oldKeyBoardheight) {
        if (mMinValueEdt.hasFocus()) {
            if (!checkMin()) {
                KeyBoardHelper.openSoftKeyboard(getActivity());
            } else {
                setRangeSeekBarAndClearEdtFocus();
            }
        } else if (mMaxValueEdt.hasFocus()) {
            if (!checkMax()) {
                KeyBoardHelper.openSoftKeyboard(getActivity());
            } else {
                setRangeSeekBarAndClearEdtFocus();
            }
        } else {
            setRangeSeekBarAndClearEdtFocus();
        }
    }


    private void setRangeSeekBarAndClearEdtFocus() {
        mRangeSeekBar.setProgress(getMinEdtValue(), getMaxEdtValue());
        if (mMaxValueEdt.hasFocus()){
            mMaxValueEdt.clearFocus();
        }
        if (mMinValueEdt.hasFocus()) {
            mMinValueEdt.clearFocus();
        }
    }

    public static Builder newBuilder(Context context) {
        return new Builder(context);
    }

    public static class Builder {
        private Context mContext;
        private int mMaxValue = Integer.MAX_VALUE;
        private int mMinValue = Integer.MIN_VALUE;
        private IControllerRangeConfirmListener mControllerRangeConfirmListener;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setMaxValue(int maxValue) {
            this.mMaxValue = maxValue;
            return this;
        }

        public Builder setMinValue(int minValue) {
            this.mMinValue = minValue;
            return this;
        }

        public Builder setRangeConfirmListener(IControllerRangeConfirmListener rangeConfirmListener) {
            this.mControllerRangeConfirmListener = rangeConfirmListener;
            return this;
        }

        public ControllerRangeDialogFragment build() {
            ControllerRangeDialogFragment fragment = new ControllerRangeDialogFragment();
            fragment.setCancelable(false);
            fragment.mControllerRangeListener = mControllerRangeConfirmListener;
            Bundle args = new Bundle();
            args.putInt(EXTRA_MAX_VALUE, mMaxValue);
            args.putInt(EXTRA_MIN_VALUE, mMinValue);
            fragment.setArguments(args);
            return fragment;
        }
    }

}
