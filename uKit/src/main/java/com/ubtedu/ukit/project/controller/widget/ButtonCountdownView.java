package com.ubtedu.ukit.project.controller.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.ubtedu.ukit.project.controller.utils.ControllerConstValue;

import java.util.Locale;

/**
 * @Author naOKi
 * @Date 2018-12-07
 **/
public class ButtonCountdownView extends AppCompatTextView {

    private int countDownTimeMs = 5 * 1000;
    private int lastSweepAngle = -1;
    private String lastSecond = null;

    private static final int innerBorderSize = ControllerConstValue.COUNTDOWN_BORDER_SIZE;
    private static final int outerBorderSize = ControllerConstValue.COUNTDOWN_BORDER_SIZE;
    private static final int innerBorderColor = 0xFF60D064;
    private static final int outerBorderColor = 0x51171E2B;
    private static final int bgColor = 0x99171E2B;

    private Paint bgPaint;
    private Paint innerBorderPaint;
    private Paint outerBorderPaint;

    private RectF innerRectF = new RectF();
    private RectF outerRectF = new RectF();

    private ValueAnimator animator;

    public ButtonCountdownView(Context context) {
        this(context, null);
    }

    public ButtonCountdownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setCountDownTime(long timeMs) {
        countDownTimeMs = Math.min((int) timeMs, Integer.MAX_VALUE / 360);
    }

    private void init() {
        setGravity(Gravity.CENTER);
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(bgColor);
        innerBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerBorderPaint.setStyle(Paint.Style.STROKE);
        innerBorderPaint.setColor(innerBorderColor);
        innerBorderPaint.setStrokeWidth(innerBorderSize);
        outerBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outerBorderPaint.setStyle(Paint.Style.STROKE);
        outerBorderPaint.setColor(outerBorderColor);
        outerBorderPaint.setStrokeWidth(outerBorderSize);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        float halfOuterBorderSize = outerBorderSize / 2f;
        float halfInnerBorderSize = innerBorderSize / 2f;
        outerRectF.left = halfOuterBorderSize;
        outerRectF.right = right - left - halfOuterBorderSize;
        outerRectF.top = halfOuterBorderSize;
        outerRectF.bottom = bottom - top - halfOuterBorderSize;
        innerRectF.left = outerBorderSize + halfInnerBorderSize;
        innerRectF.right = right - left - outerBorderSize - halfInnerBorderSize;
        innerRectF.top = outerBorderSize + halfInnerBorderSize;
        innerRectF.bottom = bottom - top - outerBorderSize - halfInnerBorderSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int halfWidth = getWidth() / 2;
        int halfHeight = getHeight() / 2;
        int radius = Math.min(halfWidth, halfHeight);
        canvas.drawCircle(innerRectF.centerX(), innerRectF.centerY(), radius, bgPaint);
        int sweepAngle = lastSweepAngle;
        int startAngle = 270 - sweepAngle;
        canvas.drawArc(outerRectF, 360, 360, false, outerBorderPaint);
        canvas.drawArc(innerRectF, startAngle, sweepAngle, false, innerBorderPaint);
        super.onDraw(canvas);
    }

    public void updateProcess(int currentValue) {
        int sweepAngle = 360 * currentValue / countDownTimeMs;
//		int second = (currentValue + 999) / 1000;
        String second = String.format(Locale.US, "%.1fs", currentValue / 1000.0f);
        if (!TextUtils.equals(lastSecond, second)) {
            lastSecond = second;
            setText(lastSecond);
        }
        if (lastSweepAngle != sweepAngle) {
            lastSweepAngle = sweepAngle;
            invalidate();
        }
    }

    public boolean isPlaying() {
        return animator != null && animator.isRunning();
    }

    public void startAnimator() {
        if (isPlaying()) {
            animator.cancel();
        }
        setVisibility(VISIBLE);
        animator = ValueAnimator.ofInt(countDownTimeMs, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                updateProcess((int) animation.getAnimatedValue());
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                updateProcess(countDownTimeMs);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.setInterpolator(null);
        animator.setDuration(countDownTimeMs);
        animator.start();
    }

    public void stopAnimator() {
        if (isPlaying()) {
            animator.cancel();
        }
        setVisibility(GONE);
    }

}