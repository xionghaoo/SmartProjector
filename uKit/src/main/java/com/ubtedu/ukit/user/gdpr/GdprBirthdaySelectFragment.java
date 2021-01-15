package com.ubtedu.ukit.user.gdpr;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseFragment;
import com.zyyoona7.picker.DatePickerView;
import com.zyyoona7.picker.ex.DayWheelView;
import com.zyyoona7.picker.ex.MonthWheelView;
import com.zyyoona7.picker.ex.YearWheelView;

import java.util.Calendar;

/**
 * 出生日期选择
 */
public class GdprBirthdaySelectFragment extends UKitBaseFragment {
    private DatePickerView mPicker;
    private Button mConfirmBtn;
    private OnConfirmBirthdayListener mOnConfirmBirthdayListener;

    private final static int MIN_AGE_REQUIRE = 16;

    private static final int DEFAULT_SELECTED_YEAR = 2000;
    private static final int DEFAULT_SELECTED_MONTH = 1;
    private static final int DEFAULT_SELECTED_DAY = 1;

    private static final int DATE_LABEL_TEXT_COLOR = 0xFF555B67;
    private static final int DATE_NORMAL_TEXT_COLOR = 0xAA60D064;
    private static final int DATE_SELECTED_TEXT_COLOR = 0xFF60D064;

    private static final String DATE_YEAR_TEXT_FORMAT = "%d";
    private static final String DATE_MONTH_TEXT_FORMAT = "    %02d";
    private static final String DATE_DAY_TEXT_FORMAT = "    %02d";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_gdpr_age_birthday_select, null);
        mConfirmBtn = layout.findViewById(R.id.gdpr_age_confirm_btn);
        bindClickListener(mConfirmBtn);

        mPicker = layout.findViewById(R.id.gdpr_date_picker);
        mPicker.setTextSize(getResources().getDimension(R.dimen.ubt_dimen_40px), true);
        mPicker.setVisibleItems(5);
        mPicker.setLabelTextSize(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.ubt_dimen_24px));
        mPicker.setShowLabel(true);
        mPicker.setLabelTextColor(DATE_LABEL_TEXT_COLOR);
        mPicker.setNormalItemTextColor(DATE_NORMAL_TEXT_COLOR);
        mPicker.setSelectedItemTextColor(DATE_SELECTED_TEXT_COLOR);
        mPicker.getYearTv().setText(R.string.gdpr_age_birthday_year);
        mPicker.getMonthTv().setText(R.string.gdpr_age_birthday_month);
        mPicker.getDayTv().setText(R.string.gdpr_age_birthday_day);
        YearWheelView yearWv = mPicker.getYearWv();
        MonthWheelView monthWv = mPicker.getMonthWv();
        DayWheelView dayWv = mPicker.getDayWv();
        yearWv.setSelectedYear(DEFAULT_SELECTED_YEAR);
        monthWv.setSelectedMonth(DEFAULT_SELECTED_MONTH);
        dayWv.setSelectedDay(DEFAULT_SELECTED_DAY);
        yearWv.setIntegerNeedFormat(DATE_YEAR_TEXT_FORMAT);
        monthWv.setIntegerNeedFormat(DATE_MONTH_TEXT_FORMAT);
        dayWv.setIntegerNeedFormat(DATE_DAY_TEXT_FORMAT);

        return layout;
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mConfirmBtn) {
            if (mOnConfirmBirthdayListener != null) {
                mOnConfirmBirthdayListener.onBirthdayConfirm(checkAge(MIN_AGE_REQUIRE));
            }
        }
    }

    private boolean checkAge(int minAge) {
        Calendar born = Calendar.getInstance();
        born.set(mPicker.getSelectedYear(), mPicker.getSelectedMonth() - 1, mPicker.getSelectedDay());

        Calendar minAgeBorn = Calendar.getInstance();
        minAgeBorn.set(Calendar.YEAR, minAgeBorn.get(Calendar.YEAR) - minAge);

        if (born.after(minAgeBorn)) {
            return false;
        }
        return true;
    }

    public void setOnConfirmBirthdayListener(OnConfirmBirthdayListener onConfirmBirthdayListener) {
        this.mOnConfirmBirthdayListener = onConfirmBirthdayListener;
    }

    public interface OnConfirmBirthdayListener {
        void onBirthdayConfirm(boolean isAgeAvailable);
    }
}
