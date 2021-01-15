package com.ubtedu.ukit.project.controller.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @Author naOKi
 * @Date 2018-12-07
 **/
public class ButtonBackgroundView extends View {

	private int bgColor = 0xFFFFFFFF;
	
	private Paint bgPaint;
	
	public ButtonBackgroundView(Context context) {
		this(context, null);
	}

	public ButtonBackgroundView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		bgPaint.setColor(bgColor);
	}
	
	public int getBgColor() {
		return bgColor;
	}
	
	public void setBgColor(int bgColor) {
		this.bgColor = bgColor;
		bgPaint.setColor(bgColor);
		invalidate();
	}

	protected Paint getBgPaint() {
		return bgPaint;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		Paint bgPaint = getBgPaint();
		int halfWidth = getWidth() / 2;
		int halfHeight = getHeight() / 2;
		int radius = Math.min(halfWidth, halfHeight);
		canvas.drawCircle(halfWidth, halfHeight, radius, bgPaint);
		super.onDraw(canvas);
	}
	
}