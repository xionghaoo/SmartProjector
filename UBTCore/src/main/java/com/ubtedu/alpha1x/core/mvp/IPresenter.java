package com.ubtedu.alpha1x.core.mvp;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.ubtedu.alpha1x.core.base.Lifecycle.LifecycleCallback;

/**
 * Created by qinicy on 2017/5/2.
 */

public interface IPresenter<V extends IView> extends LifecycleCallback {

    void attachV(V view);

    void detachV();

    V getView();

    void setContext(Context context);

    /**
     * 释放资源
     */
    void release();

    void attachLifecycleOwner(LifecycleOwner lifecycleOwner);

    void attachViewModelProvider(ViewModelProvider provider);
}
