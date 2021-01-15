package com.ubtedu.alpha1x.core.base.fragment;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.core.base.IExtraInfo;
import com.ubtedu.alpha1x.core.base.delegate.BaseUIDelegate;
import com.ubtedu.alpha1x.core.base.delegate.ClickEventDelegate;
import com.ubtedu.alpha1x.core.base.delegate.EventDelegate;
import com.ubtedu.alpha1x.core.base.delegate.IEventDelegateWrapper;
import com.ubtedu.alpha1x.core.mvp.IPresenter;
import com.ubtedu.alpha1x.core.mvp.IView;
import com.ubtedu.alpha1x.core.mvp.MVPFragment;
import com.ubtedu.alpha1x.core.mvp.ViewModelFactory;

/**
 * Created by qinicy on 2017/5/3.
 */

public class UbtBaseFragment<P extends IPresenter, V extends IView> extends MVPFragment<P, V> implements ClickEventDelegate, IExtraInfo, FragmentBackHandler {


    private EventDelegate mEventDelegate;
    private String mExtraInfo;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initEventDelegate();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUIDelegate().setClickEventDelegate(this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            onVisibilityChangedToUser(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onVisibilityChangedToUser(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setExtraInfo(String extraInfo) {
        mExtraInfo = extraInfo;
    }

    @Override
    public String getExtraInfo() {
        return mExtraInfo;
    }

    /**
     * 用以处理Fragment在ViewPager中的对用户可视
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed()) {
            onVisibilityChangedToUser(isVisibleToUser);
        }
    }

    protected void onVisibilityChangedToUser(boolean isVisibleToUser) {
        if (mEventDelegate != null) {
            mEventDelegate.onFragmentVisibilityChangedToUser(getFragmentManager(), this, isVisibleToUser);
        }
    }

    private void initEventDelegate() {
        Application application = getActivity().getApplication();
        if (application instanceof IEventDelegateWrapper) {
            IEventDelegateWrapper delegateWrapper = (IEventDelegateWrapper) application;
            mEventDelegate = delegateWrapper.getEventDelegate();
        }
    }

    @Override
    public boolean onBackPressed() {
        return interceptBackPressed() || BackHandlerHelper.handleBackPress(this);

    }

    public boolean interceptBackPressed() {
        return false;
    }

    /**
     * 绑定防抖动点击事件，触发在{@link #onClick(View, boolean)}
     * {@link #onClick(View, boolean)}
     * {@link BaseUIDelegate#SAFE_CLICK_INTERNAL_TIME}
     *
     * @param view The target view
     */
    final protected void bindSafeClickListener(View view) {
        getUIDelegate().bindSafeClickListener(view);
    }

    final protected View.OnClickListener getSafeClickListener() {
        return getUIDelegate().getSafeClickListener();
    }

    /**
     * 绑定普通点击事件，触发{@link #onClick(View, boolean)}
     *
     * @param view The target view
     */
    final protected void bindClickListener(View view) {
        getUIDelegate().bindClickListener(view);
    }

    final protected View.OnClickListener getNormalClickListener() {
        return getUIDelegate().getNormalClickListener();
    }


    @Override
    public void onClick(View v, boolean isSafeClick) {

    }

    @Override
    protected ViewModelFactory getViewModelFactory() {
        return new ViewModelFactory();
    }

    @Override
    protected P createPresenter() {
        return null;
    }

    @Override
    protected V createUIView() {
        return null;
    }

    @Deprecated
    protected <T> T findView(View root, @IdRes int resId) {
        return (T) root.findViewById(resId);
    }


}
