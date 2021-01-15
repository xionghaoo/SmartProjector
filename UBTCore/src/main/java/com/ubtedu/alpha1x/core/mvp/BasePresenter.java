package com.ubtedu.alpha1x.core.mvp;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Created by qinicy on 2017/5/3.
 */

public class BasePresenter<UI extends IView> implements IPresenter<UI> {
    public UI mUI;
    protected Context mContext;
    protected LifecycleOwner mLifecycleOwner;
    private ViewModelProvider mViewModelProvider;

    @Override
    public void attachV(UI view) {
        mUI = view;
    }

    @Override
    public void detachV() {
        mUI = null;
    }

    /**
     * 返回MVP中的V。
     *
     * @return MVP中的V，如果已经{@link #detachV()}，V == null.
     */
    @Override
    public UI getView() {
        return mUI;
    }

    @Override
    public void setContext(Context context) {
        this.mContext = context;
    }


    @Override
    public void release() {
        mContext = null;
        mLifecycleOwner = null;
        mViewModelProvider = null;
    }

    @Override
    public void attachLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.mLifecycleOwner = lifecycleOwner;
    }

    @Override
    public void attachViewModelProvider(ViewModelProvider provider) {
        this.mViewModelProvider = provider;
    }

    public <T extends ViewModel> T createViewModel(@NonNull Class<T> modelClass) {

        if (mViewModelProvider != null) {
            return mViewModelProvider.get(modelClass);
        }
        return null;
    }

    @MainThread
    public <T> void observe(LiveData<T> data, Observer<T> observer) {
        if (data != null && observer != null) {
            data.observe(mLifecycleOwner, observer);
        }
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
