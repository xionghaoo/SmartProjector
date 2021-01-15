package com.ubtedu.ukit.bluetooth.dialog.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.UKitApplication;

import java.io.File;
import java.util.ArrayList;

public class DialogProgressView extends FrameLayout {

    private static final String FONT_PATH = "font" + File.separator + "BebasNeue-Regular.otf";
    private static final Typeface TYPEFACE_FONT;

    private TextView percentTv;
    private View progressView;

    private int paddingSize;

    private int mPercent = -1;
    private int mPercentTranslationX = 0;
    private int mProgressTranslationX = 0;

    private boolean isReadyToUpdate = false;

    private AnimatorSet mAnimatorSet;

    static {
        TYPEFACE_FONT = Typeface.createFromAsset(UKitApplication.getInstance().getAssets(), FONT_PATH);
    }

    public DialogProgressView(Context context) {
        super(context);
        init(context);
    }

    public DialogProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DialogProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_dialog_progress, this);
        percentTv = findViewById(R.id.dialog_percent_tv);
        progressView = findViewById(R.id.dialog_progress_view);
        percentTv.setTypeface(TYPEFACE_FONT);
        paddingSize = getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_3px);
        percentTv.setText("0%");
        mPercentTranslationX = paddingSize;
        percentTv.setTranslationX(mPercentTranslationX);
        progressView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (oldRight - oldLeft == 0 && oldBottom - oldTop == 0) {
                    //界面已经布局成功，更新状态标识
                    isReadyToUpdate = true;
                    mProgressTranslationX = left - right;
                    progressView.setTranslationX(mProgressTranslationX);
                    progressView.setVisibility(View.VISIBLE);
                    if (mPercent > 0) {
                        //如果已经设置了百分比，则进行动画更新
                        updateSendingPercent(mPercent);
                    }
                }
            }
        });
        mProgressTranslationX = progressView.getWidth();
        if (mProgressTranslationX != 0) {
            progressView.setTranslationX(mProgressTranslationX);
            progressView.setVisibility(View.VISIBLE);
        }
    }

    public void setProgress(int percent) {
        if (mPercent != percent) {
            mPercent = percent;
            updateSendingPercent(percent);
        }
    }

    private synchronized void updateSendingPercent(int percent) {
        if(!isReadyToUpdate) {
            return;
        }

        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            return;
        }

        int progressWidth = progressView.getWidth();
        int percentWidth;

        String newText = String.format("%s%%", Integer.toString(percent));
        int oldLength = percentTv.getText().toString().length();
        int newLength = newText.length();
        if (oldLength != 0 && oldLength != newLength) {
            percentWidth = ((newLength * percentTv.getWidth()) / oldLength) + (2 * paddingSize);
        } else {
            percentWidth = percentTv.getWidth() + (2 * paddingSize);
        }

        int pointX = (progressWidth * percent) / 100;
        int targetPercentX = Math.max(paddingSize, Math.min(progressWidth - percentWidth, pointX - (percentWidth / 2)));
        int targetProgressX = pointX - progressWidth;

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500L);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimatorSet = null;//主动暂停，解决状态错误的问题，如果本身已经停止也没有影响
                if (percent != mPercent) {
                    updateSendingPercent(mPercent);
                }
                if (percent == 100) {
                    if (mOnProgressFullListener != null) {
                        mOnProgressFullListener.onProgressFull();
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        ArrayList<Animator> animators = new ArrayList<>();

        ObjectAnimator percentObjectAnimator = ObjectAnimator.ofFloat(percentTv, "translationX", mPercentTranslationX, targetPercentX);
        percentObjectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPercentTranslationX = Math.round((float) animation.getAnimatedValue());
            }
        });
        animators.add(percentObjectAnimator);
        ObjectAnimator progressObjectAnimator = ObjectAnimator.ofFloat(progressView, "translationX", mProgressTranslationX, targetProgressX);
        progressObjectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgressTranslationX = Math.round((float) animation.getAnimatedValue());
                int percent = (100 * (progressWidth + mProgressTranslationX)) / progressWidth;
                percentTv.setText(String.format("%s%%", Integer.toString(percent)));
            }
        });
        animators.add(progressObjectAnimator);
        animatorSet.playTogether(animators);
        animatorSet.start();
        mAnimatorSet = animatorSet;
    }

    /**
     * 进度是否走完。需要同时满足进度=100%，动画播放结束，界面初始化完成
     */
    public boolean isProgressRunning() {
        //哪怕动画没有在执行，但是进度还没到100，也是属于正在走进度的过程
        //界面没有初始化完，也是表示进度没有走完
        return !isReadyToUpdate || mPercent != 100 || (mAnimatorSet != null && mAnimatorSet.isRunning());
    }

    private OnProgressFullListener mOnProgressFullListener;

    public void setOnProgressFullListener(OnProgressFullListener mOnProgressFullListener) {
        this.mOnProgressFullListener = mOnProgressFullListener;
    }

    public interface OnProgressFullListener {
        void onProgressFull();
    }
}
