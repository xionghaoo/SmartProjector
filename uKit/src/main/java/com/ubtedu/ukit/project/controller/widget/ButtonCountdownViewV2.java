package com.ubtedu.ukit.project.controller.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.ubtedu.ukit.project.controller.utils.ControllerConstValue;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @Author naOKi
 * @Date 2018-12-07
 **/
public class ButtonCountdownViewV2 extends AppCompatTextView {

    private int countDownTimeMs = 5 * 1000;
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

    private static float totalPathSize;
    private static float linearPathSize;
    private static float roundPathSize;
    private static float halfLinearPathSize;
    private static float halfRoundPathSize;
    private static float quarterLinearPathSize;
    private static float iRadius;
    private static float oRadius;

    private Path path = new Path();

    private int lastPathSize = -1;

    private static class PathInflection {
        private static final int TYPE_LINE = 1;
        private static final int TYPE_ARC = 2;
        private static final int DIRECTION_R_TO_L = 1;
        private static final int DIRECTION_L_TO_R = -1;
        private int type;
        private int direction;
        private PointF endPoint;
        private RectF pathRect;
        private float endAngle;
        private float maxSize;
        private float maxAngle;
        private float fastLeftSize;
        private static PathInflection newLinePoint(float fastLeftSize, float x, float y, int direction, float maxSize) {
            PathInflection result = new PathInflection();
            result.endPoint = new PointF(x, y);
            result.maxSize = Math.abs(maxSize);
            result.fastLeftSize = fastLeftSize;
            result.direction = direction;
            result.type = TYPE_LINE;
            return result;
        }
        private static PathInflection newArcPoint(float fastLeftSize, float left, float top, float right, float bottom, float endAngle, float maxAngle) {
            PathInflection result = new PathInflection();
            result.pathRect = new RectF(left, top, right, bottom);
            result.endAngle = endAngle;
            result.maxAngle = Math.abs(maxAngle);
            result.fastLeftSize = fastLeftSize;
            result.type = TYPE_ARC;
            return result;
        }
        private void addToPath(float leftSize, Path path) {
            if(leftSize <= fastLeftSize) {
                return;
            }
            float rangeLeftSize = leftSize - fastLeftSize;
            if(type == TYPE_LINE) {
                float offsetSize = Math.copySign(Math.min(rangeLeftSize, maxSize), direction);
                path.moveTo(endPoint.x, endPoint.y);
                path.rLineTo(offsetSize, 0);
            } else if(type == TYPE_ARC) {
                float sweepAngle = Math.min(rangeLeftSize * 360 / roundPathSize, maxAngle);
                float startAngle = endAngle - sweepAngle;
                path.arcTo(pathRect, startAngle, sweepAngle, true);
            }
        }
    }

    private static final ArrayList<PathInflection> pathInflections = new ArrayList<>();

    public ButtonCountdownViewV2(Context context) {
        this(context, null);
    }

    public ButtonCountdownViewV2(Context context, @Nullable AttributeSet attrs) {
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
        innerBorderPaint.setDither(true);
        innerBorderPaint.setStyle(Paint.Style.STROKE);
        innerBorderPaint.setColor(innerBorderColor);
        innerBorderPaint.setStrokeWidth(innerBorderSize + 1f);
        outerBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outerBorderPaint.setStyle(Paint.Style.STROKE);
        outerBorderPaint.setColor(outerBorderColor);
        outerBorderPaint.setStrokeWidth(outerBorderSize + 1f);
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
        oRadius = outerRectF.height() / 2;
        iRadius = innerRectF.height() / 2;
        halfLinearPathSize = innerRectF.width() - innerRectF.height();
        linearPathSize = halfLinearPathSize * 2;
        quarterLinearPathSize = halfLinearPathSize / 2;
        roundPathSize = (float)(2 * Math.PI * iRadius);
        halfRoundPathSize = roundPathSize / 2;
        totalPathSize = roundPathSize + linearPathSize;
        pathInflections.clear();
        pathInflections.add(PathInflection.newLinePoint(0, innerRectF.centerX(), innerRectF.top, PathInflection.DIRECTION_L_TO_R, quarterLinearPathSize));
        pathInflections.add(PathInflection.newArcPoint(quarterLinearPathSize, innerRectF.left, innerRectF.top, innerRectF.left + innerRectF.height(), innerRectF.bottom, 270, 180));
        pathInflections.add(PathInflection.newLinePoint(quarterLinearPathSize + halfRoundPathSize, innerRectF.left + iRadius, innerRectF.bottom, PathInflection.DIRECTION_R_TO_L, halfLinearPathSize));
        pathInflections.add(PathInflection.newArcPoint(quarterLinearPathSize + halfRoundPathSize + halfLinearPathSize, innerRectF.right - innerRectF.height(), innerRectF.top, innerRectF.right, innerRectF.bottom, 90, 180));
        pathInflections.add(PathInflection.newLinePoint(quarterLinearPathSize + roundPathSize + halfLinearPathSize, innerRectF.right - iRadius, innerRectF.top, PathInflection.DIRECTION_L_TO_R, quarterLinearPathSize));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        int radius = Math.min(halfWidth, halfHeight);
        canvas.drawRoundRect(0, 0, width, height, radius, radius, bgPaint);
        canvas.drawRoundRect(outerRectF, oRadius, oRadius, outerBorderPaint);
        path.reset();
        for(PathInflection pathInflection : pathInflections) {
            pathInflection.addToPath(lastPathSize, path);
        }
        canvas.drawPath(path, innerBorderPaint);
        super.onDraw(canvas);
    }

    public void updateProcess(int currentValue) {
        int pathSize = (int)(totalPathSize * currentValue / countDownTimeMs);
//		int second = (currentValue + 999) / 1000;
        String second = String.format(Locale.US, "%.1fs", currentValue / 1000.0f);
        if (!TextUtils.equals(lastSecond, second)) {
            lastSecond = second;
            setText(lastSecond);
        }
        if (lastPathSize != pathSize) {
            lastPathSize = pathSize;
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