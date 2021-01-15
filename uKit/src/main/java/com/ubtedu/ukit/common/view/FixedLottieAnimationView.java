package com.ubtedu.ukit.common.view;

import android.content.Context;
import android.util.AttributeSet;

import com.airbnb.lottie.LottieAnimationView;

/**
 * @Author naOKi
 * @Date 2019/07/23
 **/
public class FixedLottieAnimationView extends LottieAnimationView {

    public FixedLottieAnimationView(Context context) {
        super(context);
        initFix();
    }

    public FixedLottieAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFix();
    }

    public FixedLottieAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFix();
    }

    private void initFix() {
        LottieAnimationViewFixer.fixGrallocError(this);
    }

}
