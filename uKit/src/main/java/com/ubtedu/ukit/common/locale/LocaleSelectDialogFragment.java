package com.ubtedu.ukit.common.locale;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.alpha1x.ui.layoutmanager.CenterSnapHelper;
import com.ubtedu.alpha1x.ui.layoutmanager.ViewPagerLayoutManager;
import com.ubtedu.alpha1x.ui.recyclerview.CommonAdapter;
import com.ubtedu.alpha1x.ui.recyclerview.base.ViewHolder;
import com.ubtedu.alpha1x.utils.AppUtil;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseDialogFragment;

import java.util.List;


/**
 * @author qinicy
 * @data 2018/7/7
 */
public class LocaleSelectDialogFragment extends UKitBaseDialogFragment implements View.OnClickListener {
    private static final int TEXT_COLOR_NORMAL = Color.parseColor("#929AA8");
    private static final int TEXT_COLOR_SELECTED = Color.parseColor("#68BCFF");
    private RecyclerView mLocaleRcv;
    private List<UBTLocale> mLocaleList;
    private UBTLocale mSelectLocale;
    private UBTLocale mSourceLocale;
    private LocaleAdapter mLocaleAdapter;
    private View mCancelTv;
    private View mSubmitTv;
    private boolean isSelect;
    private boolean isChinese;
    private int mSelectPosition;
    private boolean isFirstSelect = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isChinese = AppUtil.isSimpleChinese(AppUtil.getAppLanguage(getContext()));
    }

    public void setData(List<UBTLocale> localeList, UBTLocale currentLocale) {
        mLocaleList = localeList;
        mSourceLocale = currentLocale;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_locale_select, null);
        if (isCancelable()) {
            super.mRootView = root.findViewById(R.id.dialog_fragment_root_view);
        }
        mLocaleRcv = root.findViewById(R.id.frag_locale_select_rcv);
        mCancelTv = root.findViewById(R.id.frag_locale_select_cancel_tv);
        mSubmitTv = root.findViewById(R.id.frag_locale_select_submit_tv);
        mCancelTv.setOnClickListener(this);
        mSubmitTv.setOnClickListener(this);
        mLocaleAdapter = new LocaleAdapter(getContext(), R.layout.item_locale, mLocaleList);
        final LocaleSelectLayoutManager layoutManager = new LocaleSelectLayoutManager(getContext(),
                getContext().getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_16px));
        layoutManager.setInfinite(false);
        layoutManager.setMinAlpha(0.5f);

        layoutManager.setMinScale(0.6f);
        mLocaleRcv.setLayoutManager(layoutManager);

        layoutManager.setOnPageChangeListener(new ViewPagerLayoutManager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                if (isFirstSelect) {
                    isFirstSelect = false;
                    return;
                }
                if (position >= 0 && position < mLocaleList.size()) {
                    mSelectLocale = mLocaleList.get(position);
                    LogUtil.d("onPageSelected:" + mSelectLocale.dial_code);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mLocaleRcv.setAdapter(mLocaleAdapter);

        int defaultIndex = getCurrentLocalePosition();
        if (mLocaleList != null && mLocaleList.size() >= defaultIndex) {
            mLocaleRcv.scrollToPosition(defaultIndex);
        } else {
            mLocaleRcv.scrollToPosition(0);
        }

        new CenterSnapHelper().attachToRecyclerView(mLocaleRcv);
        return root;
    }

    private int getCurrentLocalePosition() {
        if (mSourceLocale != null && mSourceLocale.code != null && mLocaleList != null) {
            for (int i = 0; i < mLocaleList.size(); i++) {
                if (mSourceLocale.code.equals(mLocaleList.get(i).code)) {
                    return i;
                }
            }
        }
        return 42;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mDismissListener != null) {
            UBTLocale locale = isSelect ? mSelectLocale : mSourceLocale;
            mDismissListener.onDismiss(locale);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mSubmitTv) {
            isSelect = true;
            dismiss();
        }
        if (v == mCancelTv) {
            dismiss();
        }
    }

    private class LocaleAdapter extends CommonAdapter<UBTLocale> {

        public LocaleAdapter(Context context, int layoutId, List<UBTLocale> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, UBTLocale nsLocale, int position) {
            holder.itemView.setFocusable(false);
            if (!isChinese) {
                holder.setText(R.id.locale_name_tv, nsLocale.name);
                holder.setText(R.id.locale_code_tv, nsLocale.dial_code);
            } else {
                holder.setText(R.id.locale_name_tv, nsLocale.chineseName);
                holder.setText(R.id.locale_code_tv, nsLocale.dial_code);
            }
            if (position == mSelectPosition) {
                holder.setTextColor(R.id.locale_name_tv, TEXT_COLOR_SELECTED);
                holder.setTextColor(R.id.locale_code_tv, TEXT_COLOR_SELECTED);
            } else {
                holder.setTextColor(R.id.locale_name_tv, TEXT_COLOR_NORMAL);
                holder.setTextColor(R.id.locale_code_tv, TEXT_COLOR_NORMAL);
            }
        }
    }
}
