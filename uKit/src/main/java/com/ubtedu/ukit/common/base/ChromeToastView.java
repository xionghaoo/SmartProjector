package com.ubtedu.ukit.common.base;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ubtedu.alpha1x.core.base.widget.IToastView;
import com.ubtedu.ukit.R;

/**
 * @Author qinicy
 * @Date 2019/5/6
 **/
public class ChromeToastView extends RelativeLayout implements IToastView {

    public static final int SHORT_DURATION = 2000;
    public static final int LONG_DURATION = 4000;
    private static final String TAG = "toast";
    private TextView mToastTv;
    private String mToastTxt;
    private View mToastView;
    private int mDuration = SHORT_DURATION;
    private ViewGroup mParentView;
    private boolean mIsAttached;

    public ChromeToastView(Context context) {
        super(context);
        initView(context);
    }


    public ChromeToastView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ChromeToastView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mToastView = LayoutInflater.from(context).inflate(R.layout.view_toast, this);
        mToastTv = mToastView.findViewById(R.id.message_tv);
        setTag(TAG);
    }

    public void setParentView(ViewGroup parent) {
        mParentView = parent;
    }

    @Override
    public void setText(String message) {
        mToastTxt = message;
        if (mToastTv != null) {
            mToastTv.setText(message);
        }
    }

    @Override
    public void setGravity(int gravity, int xOffset, int yOffset) {

    }

    /**
     * 默认是{@link #SHORT_DURATION}
     *
     * @param duration
     */
    @Override
    public void setDuration(int duration) {
        if (duration == Toast.LENGTH_SHORT) {
            mDuration = SHORT_DURATION;
        } else if (duration == Toast.LENGTH_LONG) {
            mDuration = LONG_DURATION;
        }
    }

    @Override
    public void show() {

        if (mToastView != null && mParentView != null && mToastTv != null) {
            if (!isAttached()) {
                attach();
            }
            if (TextUtils.isEmpty(mToastTv.getText())) {
                mToastTv.setText(mToastTxt);
            }
            mToastView.setVisibility(VISIBLE);
            mToastView.bringToFront();
            mToastView.removeCallbacks(mToastAutoDismissTask);
            mToastView.postDelayed(mToastAutoDismissTask, mDuration);
        }
    }

    private boolean isAttached() {
        if (mIsAttached) {
            return true;
        }
        if (mParentView != null) {
            mIsAttached = mParentView.findViewWithTag(TAG) != null;
        }
        return mIsAttached;
    }

    private void attach() {
        if (mParentView != null && mToastView != null) {
            mParentView.addView(mToastView);
        }
    }

    @Override
    public void cancel() {
        dismiss();
    }

    private void dismiss() {
        if (mToastView != null) {
            mToastView.setVisibility(GONE);
            mToastView.removeCallbacks(mToastAutoDismissTask);
        }
    }

    private Runnable mToastAutoDismissTask = new Runnable() {
        @Override
        public void run() {
            dismiss();
        }
    };
}
