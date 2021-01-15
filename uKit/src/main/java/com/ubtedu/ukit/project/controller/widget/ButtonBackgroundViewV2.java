package com.ubtedu.ukit.project.controller.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

/**
 * @Author naOKi
 * @Date 2018-12-07
 **/
public class ButtonBackgroundViewV2 extends ButtonBackgroundView {

	public ButtonBackgroundViewV2(Context context) {
		super(context);
	}

	public ButtonBackgroundViewV2(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		Paint bgPaint = getBgPaint();
		int width = getWidth();
		int height = getHeight();
		int halfWidth = width / 2;
		int halfHeight = height / 2;
		int radius = Math.min(halfWidth, halfHeight);
		canvas.drawRoundRect(0, 0, width, height, radius, radius, bgPaint);
		super.onDraw(canvas);
	}
	
}