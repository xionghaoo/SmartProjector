/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.base;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.core.base.fragment.UbtBaseDialogFragment;
import com.ubtedu.alpha1x.core.mvp.UIDelegate;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.common.utils.PlatformUtil;
import com.ubtedu.ukit.project.bridge.api.ToastHelper;

/**
 * @Author qinicy
 * @Date 2018/11/6
 **/
public class UKitBaseDialogFragment extends UbtBaseDialogFragment {
    private Handler mHandler;
    private boolean isFirstVisible = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }


    @Override
    protected UIDelegate createUIDelegate() {
        return new UKitUIDelegate(getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (PlatformUtil.isChromebookDevice()){
            ChromeToastView toastView = new ChromeToastView(getContext());
            toastView.setParentView((ViewGroup) view);
            getUIDelegate().setToastView(toastView);
        }
    }

    @Override
    protected UKitUIDelegate getUIDelegate() {
        return (UKitUIDelegate) super.getUIDelegate();
    }

    @Override
    protected void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);

        ToastHelper.updateDialogUIDelegate(getUIDelegate(), isVisibleToUser);

        LogUtil.d("");
        //修复在Unity界面弹窗回来后，Unity因为得不到焦点导致画面黑屏的问题
        if (!isFirstVisible) {
            if (isVisibleToUser) {
                if (getDialog() != null && getDialog().getWindow() != null) {
                    final Window window = getDialog().getWindow();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                        }
                    }, 200);
                }
            } else {
                mHandler.removeCallbacksAndMessages(null);
                if (getDialog() != null && getDialog().getWindow() != null) {
                    Window window = getDialog().getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                }
            }
        } else {
            isFirstVisible = false;
        }
    }
}
