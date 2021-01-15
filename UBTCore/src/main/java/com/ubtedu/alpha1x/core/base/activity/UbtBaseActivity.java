package com.ubtedu.alpha1x.core.base.activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.core.ActivityStack;
import com.ubtedu.alpha1x.core.base.IExtraInfo;
import com.ubtedu.alpha1x.core.base.delegate.BaseUIDelegate;
import com.ubtedu.alpha1x.core.base.delegate.ClickEventDelegate;
import com.ubtedu.alpha1x.core.base.delegate.EventDelegate;
import com.ubtedu.alpha1x.core.base.delegate.IEventDelegateWrapper;
import com.ubtedu.alpha1x.core.base.fragment.BackHandlerHelper;
import com.ubtedu.alpha1x.core.mvp.IPresenter;
import com.ubtedu.alpha1x.core.mvp.IView;
import com.ubtedu.alpha1x.core.mvp.MVPActivity;
import com.ubtedu.alpha1x.core.mvp.ViewModelFactory;


/**
 * Created by qinicy on 2017/5/3.
 */

public abstract class UbtBaseActivity<P extends IPresenter, V extends IView> extends MVPActivity<P, V> implements ClickEventDelegate, IExtraInfo {
    protected EventDelegate mEventDelegate;

    private String mExtraInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initEventDelegate();
        getUIDelegate().setClickEventDelegate(this);
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            decorView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    hideSystemUI();
                    return windowInsets;
                }
            });
        }else{
            decorView.setOnSystemUiVisibilityChangeListener
                    (new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            // Note that system bars will only be "visible" if none of the
                            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                hideSystemUI();
                            }
                        }
                    });
        }

        ActivityStack.add(this);
        onInitViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    @Override
    protected void attachBaseContext(Context base) {
        Context newBase = null;
        if (mEventDelegate != null && mEventDelegate.isDelegate()) {
            newBase = mEventDelegate.onActivityAttachBaseContext(this, base);
        }
        if (newBase == null) {
            newBase = base;
        }
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStack.remove(this);
        if (mEventDelegate != null && mEventDelegate.isDelegate()) {
            getSupportFragmentManager().unregisterFragmentLifecycleCallbacks(mEventDelegate);
        }
    }


    public EventDelegate getEventDelegate() {
        return mEventDelegate;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mEventDelegate != null && mEventDelegate.isDelegate()) {
            mEventDelegate.onNewIntent(this, intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mEventDelegate != null && mEventDelegate.isDelegate()) {
            mEventDelegate.onActivityResult(this, requestCode, resultCode, data);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (shouldSystemUiUseImmersiveStickyMode() && hasFocus) {
            hideSystemUI();
        }
    }


    @Override
    public void onBackPressed() {
        if (mEventDelegate != null && mEventDelegate.isDelegate()) {
            mEventDelegate.onActivityBackPressed(this);
        }
        if (!BackHandlerHelper.handleBackPress(this)) {
            super.onBackPressed();
        }
    }

    private void initEventDelegate() {
        Application application = getApplication();
        if (application instanceof IEventDelegateWrapper) {
            IEventDelegateWrapper delegateWrapper = (IEventDelegateWrapper) application;
            mEventDelegate = delegateWrapper.getEventDelegate();
            if (mEventDelegate != null && mEventDelegate.isDelegate()) {
                getSupportFragmentManager().registerFragmentLifecycleCallbacks(mEventDelegate, true);
            }
        }
    }

    /**
     * 是否自动隐藏系统UI：StatueBar，NavigationBar
     *
     * @return
     */
    protected boolean shouldSystemUiUseImmersiveStickyMode() {
        return true;
    }


    protected void onInitViews() {
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
    public String getExtraInfo() {
        return mExtraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        mExtraInfo = extraInfo;
    }

    @Deprecated
    protected <T extends View> T findView(@IdRes int resId) {
        return (T) findViewById(resId);
    }

    protected <T extends View> T findIncludeView(View layout, @IdRes int resId) {
        return (T) layout.findViewById(resId);
    }


    /**
     * 隐藏系统UI
     */
    protected void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().getInsetsController().hide(WindowInsets.Type.systemBars());
        }else{
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected ViewModelFactory getViewModelFactory() {
        return null;
    }

    @Override
    protected P createPresenter() {
        return null;
    }

    @Override
    protected V createUIView() {
        return null;
    }
}
