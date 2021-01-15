/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.dialog;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.ui.image.UtilBitmap;
import com.ubtedu.alpha1x.ui.image.UtilScreenCapture;
import com.ubtedu.ukit.common.base.UKitBaseDialogFragment;

/**
 * @Author qinicy
 * @Date 2018/11/6
 **/
public class BlurDialogFragment extends UKitBaseDialogFragment {
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mRootView != null) {
            if (mRootView instanceof ViewGroup) {
                ViewGroup root = (ViewGroup) mRootView;
                ImageView bgIv = new ImageView(getContext());
                bgIv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                Bitmap bitmap = UtilScreenCapture.getDrawing(getActivity());
                if (bitmap != null) {
                    // 将截屏Bitma放入ImageView
                    bgIv.setImageBitmap(bitmap);
                    // 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
                    UtilBitmap.blurImageView(getContext(), bgIv, 5, 0x77000000);
                } else {
                    // 获取的Bitmap为null时，用半透明代替
                    bgIv.setBackgroundColor(0x77000000);
                }
                root.addView(bgIv, 0);
            }
        }
    }
}
