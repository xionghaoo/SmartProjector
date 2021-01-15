/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.ubtedu.alpha1x.ui.dialog.UbtBaseDialog;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.R;

/**
 * @Author qinicy
 * @Date 2018/12/1
 **/
public class LoadingDialog extends UbtBaseDialog {

    private LottieAnimationView mLottieAnimationView;
    private TextView mLabelTv;
    private boolean mCanTouchOutSide;
    private float mDimAmount;
    private boolean mCancellable;
    private OnCancelListener mOnCancelListener;
    private String mLabel;
    private int mLottieAnimationId = mDefaultLottieAnimationId;
    private final static int mDefaultLottieAnimationId = R.raw.refresh;

    public LoadingDialog(Context context) {
        super(context);
    }

    public LoadingDialog(Context context, int themeId) {
        super(context, themeId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//            requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_loading);

        mLottieAnimationView = findViewById(R.id.loading_dialog_lottie_view);
        if (mLottieAnimationId <= 0) {
            mLottieAnimationId = mDefaultLottieAnimationId;
        }
        LogUtil.d("mLottieAnimationId:"+mLottieAnimationId);
        mLottieAnimationView.setAnimation(mLottieAnimationId);
        mLottieAnimationView.playAnimation();
        mLabelTv = findViewById(R.id.loading_label_tv);
        if (!TextUtils.isEmpty(mLabel)) {
            mLabelTv.setText(mLabel);
        }
        if (getWindow() != null){
            Window window = getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#99000000")));

            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            if (mCanTouchOutSide) {
                window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            }
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.dimAmount = mDimAmount;
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE;
            window.setAttributes(layoutParams);
        }


        setCanceledOnTouchOutside(false);
        setCancelable(mCancellable);
        setOnCancelListener(mOnCancelListener);
    }


    @Override
    public void dismiss() {
        super.dismiss();
        if(mLottieAnimationView != null && mLottieAnimationView.isAnimating()) {
            mLottieAnimationView.cancelAnimation();
        }
        mLottieAnimationId = -1;
        mLabel = null;
    }

    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;

        layoutParams.flags =  WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE ;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        if (getWindow() != null) {
            getWindow().getDecorView().setPadding(0, 0, 0, 0);

            getWindow().setAttributes(layoutParams);
        }
    }

    public void setCanTouchOutSide(boolean canTouchOutSide) {
        mCanTouchOutSide = canTouchOutSide;
        //更新触摸外部标识 !Modify by naOKi 20190227!
        if (getWindow() != null) {
            Window window = getWindow();
            if (mCanTouchOutSide) {
                window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            }
        }
    }

    public void setDimAmount(float dimAmount) {
        mDimAmount = dimAmount;
        //更新背景dimAmount !Modify by naOKi 20190227!
        if (getWindow() != null) {
            Window window = getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.dimAmount = mDimAmount;
            window.setAttributes(layoutParams);
        }
    }

    public void setCancellable(boolean cancellable) {
        mCancellable = cancellable;
        //设置能否取消 !Modify by naOKi 20190227!
        super.setCancelable(cancellable);//可能是设计成不调用父类的方法，来防止loading窗口被取消，这里就先注释掉
    }

    @Override
    public void setOnCancelListener(OnCancelListener onCancelListener) {
        mOnCancelListener = onCancelListener;
        //设置取消回调 !Modify by naOKi 20190227!
        super.setOnCancelListener(onCancelListener);//可能是设计成不调用父类的方法，来防止loading窗口被取消，这里就先注释掉
    }

    public void setLottieAnimation(int id) {
        if (id > 0) {
            mLottieAnimationId = id;
            //更新动画 !Modify by naOKi 20190227!
            if(mLottieAnimationView != null) {
                if(mLottieAnimationView.isAnimating()) {
                    mLottieAnimationView.cancelAnimation();
                }
                mLottieAnimationView.setAnimation(mLottieAnimationId);
                mLottieAnimationView.enableMergePathsForKitKatAndAbove(true);
                mLottieAnimationView.playAnimation();
            }
        }
    }

    public void setLabel(String label) {
        mLabel = label;
        //更新文字后，需要设置到TextView !Modify by naOKi 20190227!
        if (mLabelTv != null) {
            mLabelTv.setText(mLabel);
        }
    }
}
