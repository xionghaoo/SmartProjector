/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.base;

import android.content.Context;

import com.ubtedu.alpha1x.core.base.delegate.BaseUIDelegate;
import com.ubtedu.alpha1x.utils.LogUtil;

/**
 * @Author qinicy
 * @Date 2018/11/6
 **/
public class UKitUIDelegate extends BaseUIDelegate {

    private UKitLoadingController mLoadingController;

    public UKitUIDelegate(Context context) {
        super(context);
        if (context != null) {
            mLoadingController = new UKitLoadingController(context);
            setLoadingController(mLoadingController);
            setToastView(new UKitToastViewProxy(context));
        }
    }

    @Override
    public void showLoading(boolean cancelAble) {
        LogUtil.d("");
        super.showLoading(cancelAble);
    }

    @Override
    public void showLoading(boolean cancelAble, String message) {
        LogUtil.d("");
        super.showLoading(cancelAble, message);
    }

    public void showLoading(boolean cancelAble, String message, int lottieAnimationRawId) {
        if (mLoadingController != null && lottieAnimationRawId > 0) {
            mLoadingController.setLottieAnimation(lottieAnimationRawId);
        }
        super.showLoading(cancelAble, message);
    }

    @Override
    public void hideLoading() {
        LogUtil.d("");
        super.hideLoading();
    }
}
