package com.ubtedu.alpha1x.core.mvp;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.ubtedu.alpha1x.core.base.delegate.ClickEventDelegate;
import com.ubtedu.alpha1x.core.base.widget.ILoadingController;
import com.ubtedu.alpha1x.core.base.widget.IToastView;

/**
 * Created by qinicy on 2017/5/2.
 */

public interface UIDelegate {
    Context getContext();

    void setToastView(IToastView toastView);

    void resume();

    void pause();

    void destroy();

    void setClickEventDelegate(ClickEventDelegate delegate);

    void visible(boolean flag, View view);
    void gone(boolean flag, View view);
    void inVisible(View view);
    void toastShort(String msg);
    void toastLong(String msg);
    void showLoading(boolean cancelAble);
    void showLoading(boolean cancelAble,String message);
    void hideLoading();
    boolean isLoadingShowing();
    void cancel();

    void bindSafeClickListener(View view);

    View.OnClickListener getSafeClickListener();

    void bindClickListener(View view);

    View.OnClickListener getNormalClickListener();

    void onClick(View v, boolean isSafeClick);

    void setLoadingController(ILoadingController loadingController);
    void setLoadingCancelListener(DialogInterface.OnCancelListener listener);
}
