/*
 *    Copyright 2015 Kaopiz Software Co., Ltd.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ubtedu.alpha1x.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubtedu.alpha1x.ui.image.UtilBitmap;
import com.ubtedu.alpha1x.ui.image.UtilScreenCapture;
import com.ubtedu.base.ui.R;

/**
 * <h3>Indeterminate</h3>
 * {@code
 * ProgressDialogController.create(MainActivity.this)
 * .setStyle(ProgressDialogController.Style.SPIN_INDETERMINATE)
 * .setLabel("Please wait")
 * .setDetailsLabel("Downloading data");
 * .setCancellable(true)
 * .setAnimationSpeed(2)
 * .setDimAmount(0.5f)
 * .show();}
 * <h3>Indeterminate</h3>
 * {@code
 * ProgressDialogController hud = KProgressHUD.create(MainActivity.this)
 * .setStyle(ProgressDialogController.Style.ANNULAR_DETERMINATE)
 * .setLabel("Please wait")
 * .setMaxProgress(100)
 * .show();
 * hud.setProgress(90);}
 * <p>Alternatively, you can create a custom view and pass to the ProgressDialogController to display it.</p>
 * {@code ImageView imageView = new ImageView(this);
 * imageView.setImageResource(R.mipmap.ic_launcher);
 * ProgressDialogController.create(MainActivity.this)
 * .setCustomView(imageView)
 * .setLabel("This is a custom view")
 * .show();}
 * <p><a href="https://github.com/liangchengcheng/android-loading-dialog"/>RefUrl</p>
 */
public class ProgressDialogController {
    public static final int TOAST_LENGTH_SHORT = 1200;
    public static final int TOAST_LENGTH_LONG = 2000;
    private int mAutoDismissMillis;

    public enum Style {
        SPIN_INDETERMINATE, PIE_DETERMINATE, ANNULAR_DETERMINATE, BAR_DETERMINATE
    }

    // To avoid redundant APIs, all HUD functions will be forward to
    // a custom dialog
    private ProgressDialog mProgressDialog;
    private float mDimAmount;
    private int mWindowColor;
    private float mCornerRadius;
    private boolean mCancellable;
    private DialogInterface.OnCancelListener mOnCancelListener;
    private boolean mCanTouchOutSide;
    private Context mContext;

    private int mAnimateSpeed;
    private String mLabel;
    private String mDetailsLabel;

    private int mMaxProgress;
    private boolean mIsAutoDismiss;
    private CountTimer mCountTimer;

    public ProgressDialogController(Context context) {
        mContext = context;
        mProgressDialog = new ProgressDialog(context, R.style.ProgressDialog);

        mDimAmount = 0;
        //noinspection deprecation
        mWindowColor = context.getResources().getColor(R.color.kprogresshud_default_color);
        mAnimateSpeed = 1;
        mCornerRadius = 10;
        mIsAutoDismiss = false;
        setStyle(Style.SPIN_INDETERMINATE);
    }

    /**
     * Create a new HUD. Have the same effect as the constructor.
     * For convenient only.
     *
     * @param context Activity context that the HUD bound to
     * @return An unique HUD instance
     */
    public static ProgressDialogController create(Context context) {
        return new ProgressDialogController(context);
    }

    /**
     * Specify the HUD style (not needed if you use a custom view)
     *
     * @param style One of the ProgressDialogController.Style values
     * @return Current HUD
     */
    public ProgressDialogController setStyle(Style style) {
        View view = null;
        switch (style) {
            case SPIN_INDETERMINATE:
                view = new SpinView(mContext);
                break;
            case PIE_DETERMINATE:
                view = new PieView(mContext);
                break;
            case ANNULAR_DETERMINATE:
                view = new AnnularView(mContext);
                break;
            case BAR_DETERMINATE:
                view = new BarView(mContext);
                break;
            // No custom view style here, because view will be added later
        }
        mProgressDialog.setView(view);
        return this;
    }

    /**
     * Specify the dim area around the HUD, like in Dialog
     *
     * @param dimAmount May take value from 0 to 1.
     *                  0 means no dimming, 1 mean darkness
     * @return Current HUD
     */
    public ProgressDialogController setDimAmount(float dimAmount) {
        if (dimAmount >= 0 && dimAmount <= 1) {
            mDimAmount = dimAmount;
        }
        return this;
    }

    /**
     * Specify the HUD background color
     *
     * @param color ARGB color
     * @return Current HUD
     */
    public ProgressDialogController setWindowColor(int color) {
        mWindowColor = color;
        return this;
    }

    /**
     * Specify corner radius of the HUD (default is 10)
     *
     * @param radius Corner radius in dp
     * @return Current HUD
     */
    public ProgressDialogController setCornerRadius(float radius) {
        mCornerRadius = radius;
        return this;
    }

    /**
     * Change animate speed relative to default. Only have effect when use with indeterminate style
     *
     * @param scale 1 is default, 2 means double speed, 0.5 means half speed..etc.
     * @return Current HUD
     */
    public ProgressDialogController setAnimationSpeed(int scale) {
        mAnimateSpeed = scale;
        return this;
    }

    /**
     * Optional label to be displayed on the HUD
     *
     * @return Current HUD
     */
    public ProgressDialogController setLabel(String label) {
        mLabel = label;
        mProgressDialog.setLabel(label);
        return this;
    }

    /**
     * Optional detail description to be displayed on the HUD
     *
     * @return Current HUD
     */
    public ProgressDialogController setDetailsLabel(String detailsLabel) {
        mDetailsLabel = detailsLabel;
        return this;
    }

    /**
     * Max value for use in one of the determinate styles
     *
     * @return Current HUD
     */
    public ProgressDialogController setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
        return this;
    }

    /**
     * Set current progress. Only have effect when use with a determinate style, or a custom
     * view which implements Determinate interface.
     */
    public void setProgress(int progress) {
        mProgressDialog.setProgress(progress);
    }

    /**
     * Provide a custom view to be displayed.
     *
     * @param view Must not be null
     * @return Current HUD
     */
    public ProgressDialogController setCustomView(View view) {
        if (view != null) {
            mProgressDialog.setView(view);
        } else {
            throw new RuntimeException("Custom view must not be null!");
        }
        return this;
    }

    /**
     * Specify whether this HUD can be cancelled by using back button (default is false)
     *
     * @return Current HUD
     */
    public ProgressDialogController setCancellable(boolean isCancellable) {
        mCancellable = isCancellable;
        return this;
    }

    public ProgressDialogController setCancelListener(DialogInterface.OnCancelListener listener) {
        mOnCancelListener = listener;
        return this;
    }

    public ProgressDialogController setCanTouchOutSide(boolean isCanTouchOutSide) {
        mCanTouchOutSide = isCanTouchOutSide;
        return this;
    }

    /**
     * Specify whether this HUD closes itself if progress reaches max. Default is true.
     *
     * @return Current HUD
     */
    public ProgressDialogController setAutoDismiss(boolean isAutoDismiss, int millis) {
        mIsAutoDismiss = isAutoDismiss;
        mAutoDismissMillis = millis;
        mCountTimer = new CountTimer(millis, millis);
        return this;
    }

    public ProgressDialogController show() {
        if (!isShowing()) {
            mProgressDialog.show();
            if (mIsAutoDismiss) {
                if (mCountTimer == null) {
                    mCountTimer = new CountTimer(TOAST_LENGTH_SHORT, TOAST_LENGTH_SHORT);
                }
                mCountTimer.cancel();
                mCountTimer.start();
            }
        }
        return this;
    }

    public boolean isShowing() {
        return mProgressDialog != null && mProgressDialog.isShowing();
    }

    public void dismiss() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog.gcBitmap();
        }
    }

    class CountTimer extends CountDownTimer {

        /**
         * 构造函数
         *
         * @param millisInFuture    倒计的时间数,以毫秒为单位
         * @param countDownInterval 倒计每秒中间的间隔时间,以毫秒为单位
         */
        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            dismiss();
        }

    }

    private class ProgressDialog extends UbtBaseDialog {

        private Determinate mDeterminateView;
        private Indeterminate mIndeterminateView;
        private View mView;
        private ImageView mBlurIv;
        private Bitmap mBitmap = null;

        public ProgressDialog(Context context) {
            super(context);
        }

        public ProgressDialog(Context context, int themeId) {
            super(context, themeId);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
//            requestWindowFeature(Window.FEATURE_NO_TITLE);

            setContentView(R.layout.kprogresshud_hud);


            Window window = getWindow();
            window.setBackgroundDrawable(new ColorDrawable(0));


            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);


            if (mCanTouchOutSide) {
                window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            }
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.dimAmount = mDimAmount;
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE;
            window.setAttributes(layoutParams);

            setCanceledOnTouchOutside(false);
            setCancelable(mCancellable);
            setOnCancelListener(mOnCancelListener);

            initViews();

        }


        @Override
        public void show() {

            super.show();
            /**
             * 设置宽度全屏，要设置在show的后面
             */
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.gravity = Gravity.BOTTOM;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

            getWindow().getDecorView().setPadding(0, 0, 0, 0);

            getWindow().setAttributes(layoutParams);

            if (mContext instanceof Activity) {
                Activity activity = (Activity) mContext;
                mBitmap = UtilScreenCapture.getDrawing(activity);
            }
            if (mBitmap != null) {
                // 将截屏Bitma放入ImageView
                mBlurIv.setImageBitmap(mBitmap);
                // 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
                UtilBitmap.blurImageView(mContext, mBlurIv, 5, 0x77000000);
            } else {
                // 获取的Bitmap为null时，用半透明代替
                mBlurIv.setBackgroundColor(0x77000000);
            }
        }

        public void gcBitmap() {
            if (mBitmap != null && !mBitmap.isRecycled()) {
                mBitmap.recycle();
                mBitmap = null;
            }
            System.gc();
        }

        private void initViews() {
//            BackgroundLayout background = (BackgroundLayout) findViewById(R.id.background);
//            background.setBaseColor(mWindowColor);
//            background.setCornerRadius(mCornerRadius);

            FrameLayout containerFrame = findViewById(R.id.container);
            int wrapParam = ViewGroup.LayoutParams.WRAP_CONTENT;
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    getContext().getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_450px),
                    getContext().getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_450px));

            containerFrame.addView(mView, params);

            if (mDeterminateView != null) {
                mDeterminateView.setMax(mMaxProgress);
            }
            if (mIndeterminateView != null) {
                mIndeterminateView.setAnimationSpeed(mAnimateSpeed);
            }

            if (mLabel != null) {
                TextView labelText = findViewById(R.id.label);
                labelText.setText(mLabel);
                labelText.setVisibility(View.VISIBLE);
            }
            if (mDetailsLabel != null) {
                TextView detailsText = findViewById(R.id.details_label);
                detailsText.setText(mDetailsLabel);
                detailsText.setVisibility(View.VISIBLE);
            }

            mBlurIv = findViewById(R.id.prompt_blur_iv);
        }

        public void setProgress(int progress) {
            if (mDeterminateView != null) {
                mDeterminateView.setProgress(progress);
                if (mIsAutoDismiss && progress >= mMaxProgress) {
                    dismiss();
                }
            }
        }

        public void setView(View view) {
            if (view != null) {
                if (view instanceof Determinate) {
                    mDeterminateView = (Determinate) view;
                }
                if (view instanceof Indeterminate) {
                    mIndeterminateView = (Indeterminate) view;
                }
                mView = view;
            }
        }

        public void setLabel(String label) {
            TextView labelText = findViewById(R.id.label);
            if (labelText != null) {
                if (label != null) {
                    labelText.setText(label);
                    labelText.setVisibility(View.VISIBLE);
                } else {
                    labelText.setVisibility(View.GONE);
                }
            }
        }
    }


}
