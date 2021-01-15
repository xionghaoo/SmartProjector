package com.ubtedu.ukit.project.controller.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * @Author naOKi
 * @Date 2018-12-07
 **/
public class VerticalRelativeLayout extends RelativeLayout {
	
	public VerticalRelativeLayout(Context context) {
		super(context);
	}
	
	public VerticalRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(heightMeasureSpec, widthMeasureSpec);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		int w = r - l;
		int h = b - t;
		setRotation(90);
		setTranslationX((h - w) / 2f);
		setTranslationY((w - h) / 2f);
	}
}
