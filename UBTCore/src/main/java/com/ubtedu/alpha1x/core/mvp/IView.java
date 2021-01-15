package com.ubtedu.alpha1x.core.mvp;


import android.content.Context;

/**
 * Created by qinicy on 2017/5/2.
 */

public interface IView<P extends IPresenter> {
//    void setPresenter(P presenter);
//    P getPresenter();
    void setContext(Context context);

    @Deprecated
    Context getContext();
    void setUIDelegate(UIDelegate delegate);
    UIDelegate getUIDelegate();
}
