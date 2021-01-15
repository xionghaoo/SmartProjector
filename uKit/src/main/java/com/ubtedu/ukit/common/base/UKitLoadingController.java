/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.base;

import android.content.Context;
import android.content.DialogInterface;

import com.ubtedu.alpha1x.core.base.widget.ILoadingController;

/**
 * @Author qinicy
 * @Date 2018/11/6
 **/
public class UKitLoadingController implements ILoadingController{
    private Context mContext;

    private LoadingDialog mLoadingDialog;

    public UKitLoadingController(Context context) {
        mContext = context;
        initLoadingDialog();
    }

    @Override
    public ILoadingController setLabel(String label) {
        if (mLoadingDialog != null) {
            mLoadingDialog.setLabel(label);
        }
        return this;
    }
    public ILoadingController setLottieAnimation(int rawId) {
        if (mLoadingDialog != null) {
            mLoadingDialog.setLottieAnimation(rawId);
        }
        return this;
    }

    @Override
    public ILoadingController setCancellable(boolean cancellable) {
        if (mLoadingDialog != null) {
            mLoadingDialog.setCancellable(cancellable);
        }
        return this;
    }

    @Override
    public void setCancelListener(DialogInterface.OnCancelListener listener) {
        if (mLoadingDialog != null) {
            mLoadingDialog.setOnCancelListener(listener);
        }
    }

    @Override
    public void show() {
        if (mLoadingDialog != null) {
            mLoadingDialog.show();
        }
    }

    @Override
    public void dismiss() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public boolean isShowing() {
        if (mLoadingDialog != null) {
            return mLoadingDialog.isShowing();
        }
        return false;
    }

    private void initLoadingDialog() {
        if (mContext == null) {
            return;
        }
        mLoadingDialog = new LoadingDialog(mContext);
    }
}
