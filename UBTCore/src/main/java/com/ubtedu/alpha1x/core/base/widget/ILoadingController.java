package com.ubtedu.alpha1x.core.base.widget;


import android.content.DialogInterface;

public interface ILoadingController {
    ILoadingController setLabel(String label);
    ILoadingController setCancellable(boolean cancellable);
    void setCancelListener(DialogInterface.OnCancelListener listener);
    void show();
    void dismiss();
    boolean isShowing();
}
