package com.ubtedu.alpha1x.core.mvp;

import android.content.Context;

/**
 * Created by qinicy on 2017/5/3.
 */

public class BaseUI<Presenter extends IPresenter> implements IView<Presenter> {
    private Context mContext;
    private Presenter mPresenter;
    private UIDelegate mUIDelegate;
    @Override
    public void setContext(Context mContext) {
        this.mContext = mContext;
    }
    @Override
    public Context getContext() {
        return mContext;
    }

//    @Override
//    public void setPresenter(Presenter presenter) {
//        this.mPresenter = presenter;
//    }
//    @Override
//    public Presenter getPresenter() {
//        return mPresenter;
//    }

    @Override
    public UIDelegate getUIDelegate() {
        return mUIDelegate;
    }

    @Override
    public void setUIDelegate(UIDelegate UIDelegate) {
        mUIDelegate = UIDelegate;
    }

}
