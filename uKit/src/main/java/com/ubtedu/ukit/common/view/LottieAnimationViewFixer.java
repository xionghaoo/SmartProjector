package com.ubtedu.ukit.common.view;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.RenderMode;

/**
 * @Author naOKi
 * @Date 2019/07/23
 **/
public class LottieAnimationViewFixer {

    private LottieAnimationViewFixer() {}

    public static void fixGrallocError(LottieAnimationView lottieAnimationView) {
        if(lottieAnimationView == null) {
            return;
        }
//        if(Build.MANUFACTURER.equals("HUAWEI")) {
//            lottieAnimationView.setRenderMode(RenderMode.SOFTWARE);
//        }
        // 统一改为软件层渲染，硬件层渲染会导致Gralloc分配内存失败
        lottieAnimationView.setRenderMode(RenderMode.SOFTWARE);
    }

}
