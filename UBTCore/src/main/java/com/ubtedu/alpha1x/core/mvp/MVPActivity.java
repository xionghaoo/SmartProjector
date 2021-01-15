package com.ubtedu.alpha1x.core.mvp;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.ubtedu.alpha1x.core.base.delegate.BaseUIDelegate;


/**
 * Created by qinicy on 2017/5/2.
 */

public abstract class MVPActivity<Presenter extends IPresenter, UI extends IView> extends AppCompatActivity {

    private UIDelegate vDelegate;
    private Presenter mPresenter;
    private UI mUI;
    protected Activity context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initPresenter();
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getUIDelegate().resume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        getUIDelegate().pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Presenter presenter = getPresenter();
        if (presenter != null) {
            presenter.release();
            presenter.detachV();
        }
        getUIDelegate().destroy();
        mPresenter = null;
        vDelegate = null;
    }

    protected UIDelegate getUIDelegate() {
        if (vDelegate == null) {
            vDelegate = createUIDelegate();
            if (vDelegate == null) {
                vDelegate = new BaseUIDelegate(context);
            }
        }
        return vDelegate;
    }

    protected UIDelegate createUIDelegate() {
        return new BaseUIDelegate(context);
    }

    protected UI getUIView() {
        if (mUI == null) {
            mUI = createUIView();
        }
        return mUI;
    }

    protected Presenter getPresenter() {
        if (mPresenter == null) {
            mPresenter = createPresenter();

        }
        return mPresenter;
    }

    protected void initPresenter() {
        if (getUIView() != null && getPresenter() != null) {
            getLifecycle().addObserver(getPresenter());
            getPresenter().attachV(getUIView());
            getPresenter().attachLifecycleOwner(this);
            getPresenter().attachViewModelProvider(ViewModelProviders.of(this,getViewModelFactory()));
            getPresenter().setContext(this);
            getUIView().setContext(context);
            getUIView().setUIDelegate(getUIDelegate());
        }
    }

    protected abstract ViewModelFactory getViewModelFactory();
    protected abstract Presenter createPresenter();

    protected abstract UI createUIView();
}
