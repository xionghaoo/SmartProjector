package com.ubtedu.alpha1x.core.mvp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.ubtedu.alpha1x.core.base.delegate.BaseUIDelegate;


/**
 * Created by qinicy on 2017/5/2.
 */

public abstract class MVPFragment<Presenter extends IPresenter, UI extends IView> extends Fragment {

    private UIDelegate vDelegate;
    private Presenter mPresenter;
    private UI mUI;
    protected Context context;
    private View rootView;
    protected LayoutInflater layoutInflater;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context =  context;
        initPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutInflater = inflater;

        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();


    }

    @Override
    public void onDetach() {
        if (vDelegate != null && vDelegate.isLoadingShowing()) {
            vDelegate.hideLoading();
        }
        if (getPresenter() != null) {
            getPresenter().release();
            getPresenter().detachV();
        }
        getUIDelegate().destroy();

        mPresenter = null;
        vDelegate = null;
        context = null;
        super.onDetach();
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

    protected Presenter getPresenter() {
        if (mPresenter == null) {
            mPresenter = createPresenter();
        }
        return mPresenter;
    }

    protected UI getUIView() {
        if (mUI == null) {
            mUI = createUIView();
        }
        return mUI;
    }

    protected void initPresenter() {
        if (getUIView() != null && getPresenter() != null) {
            getLifecycle().addObserver(getPresenter());
            getPresenter().attachV(getUIView());
            getPresenter().attachLifecycleOwner(this);
            getPresenter().attachViewModelProvider(ViewModelProviders.of(this, getViewModelFactory()));
            getPresenter().setContext(getContext());
            getUIView().setContext(context);
            getUIView().setUIDelegate(getUIDelegate());
        }
    }

    protected abstract ViewModelFactory getViewModelFactory();

    protected abstract Presenter createPresenter();

    protected abstract UI createUIView();
}
