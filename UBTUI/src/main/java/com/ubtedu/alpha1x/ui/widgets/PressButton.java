package com.ubtedu.alpha1x.ui.widgets;

import android.content.Context;
import android.graphics.LightingColorFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

/**
 * @author binghua.qin
 * @data 2017/12/29
 */

public class PressButton extends AppCompatButton {
    public PressButton(Context context) {
        super(context);
        initState();
    }

    public PressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initState();
    }

    public PressButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initState();
    }

    private void initState() {
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xAA121212));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 000000000));
                }
                return false;
            }
        });
    }


}
