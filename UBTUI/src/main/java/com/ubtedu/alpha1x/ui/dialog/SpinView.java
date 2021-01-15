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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.ubtedu.base.ui.R;

class SpinView extends AppCompatImageView implements Indeterminate {

    private float mRotateDegrees;
    private int mFrameTime;
    private boolean mNeedToUpdateView;
    private Runnable mUpdateViewRunnable;
    private AnimationDrawable mAnimationDrawable;

    public SpinView(Context context) {
        super(context.getApplicationContext());
        init();
    }

    public SpinView(Context context, AttributeSet attrs) {
        super(context.getApplicationContext(), attrs);
        init();
    }

    private void init() {
        mFrameTime = 1000 / 15;
        try {
            setBackgroundResource(R.drawable.anim_loading_view);
        } catch (OutOfMemoryError e) {
        }
        mAnimationDrawable = (AnimationDrawable) getBackground();

//        setImageResource(R.drawable.kprogresshud_spinner);
//        mFrameTime = 1000 / 12;
//        mUpdateViewRunnable = new Runnable() {
//            @Override
//            public void run() {
//                mRotateDegrees += 30;
//                mRotateDegrees = mRotateDegrees < 360 ? mRotateDegrees : mRotateDegrees - 360;
//                invalidate();
//                if (mNeedToUpdateView) {
//                    postDelayed(this, mFrameTime);
//                }
//            }
//        };
    }

    @Override
    public void setAnimationSpeed(float scale) {
        mFrameTime = (int) (1000 / 15 / scale);

    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.rotate(mRotateDegrees, getWidth() / 2, getHeight() / 2);
        super.onDraw(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        mNeedToUpdateView = true;
//        post(mUpdateViewRunnable);
        startAnim();
    }

    @Override
    protected void onDetachedFromWindow() {
//        mNeedToUpdateView = false;
        stopAnim();
        super.onDetachedFromWindow();
    }

    public void startAnim() {
        if (mAnimationDrawable != null && !mAnimationDrawable.isRunning()) {
            mAnimationDrawable.start();
        }
    }

    public void stopAnim() {
        if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
    }

}
