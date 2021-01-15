package com.ubtedu.alpha1x.core.base.fragment;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ubtedu.alpha1x.core.base.IExtraInfo;
import com.ubtedu.alpha1x.core.base.delegate.BaseUIDelegate;
import com.ubtedu.alpha1x.core.base.delegate.ClickEventDelegate;
import com.ubtedu.alpha1x.core.base.delegate.EventDelegate;
import com.ubtedu.alpha1x.core.base.delegate.IEventDelegateWrapper;
import com.ubtedu.alpha1x.core.mvp.UIDelegate;

import java.lang.reflect.Field;

/**
 * Created by qinicy on 2017/6/23.
 */

public class UbtBaseDialogFragment extends DialogFragment implements ClickEventDelegate, IExtraInfo {

    private UIDelegate vDelegate;
    protected View mRootView;
    private Context mContext;
    protected OnDialogFragmentDismissListener mDismissListener;
    private EventDelegate mEventDelegate;
    private String mExtraInfo;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initEventDelegate();
        this.mContext = context;
        getUIDelegate();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mContext = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUIDelegate().setClickEventDelegate(this);
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isCancelable() && mRootView != null){
            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
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

    protected UIDelegate getUIDelegate() {
        if (vDelegate == null) {
            vDelegate = createUIDelegate();
            if (vDelegate == null) {
                vDelegate = new BaseUIDelegate(mContext);
            }
        }
        return vDelegate;
    }

    protected UIDelegate createUIDelegate() {
        return new BaseUIDelegate(mContext);
    }

    private void initEventDelegate() {
        Application application = getActivity().getApplication();
        if (application instanceof IEventDelegateWrapper) {
            IEventDelegateWrapper delegateWrapper = (IEventDelegateWrapper) application;
            mEventDelegate = delegateWrapper.getEventDelegate();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog =  super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null){
            Window window = dialog.getWindow();
            View decorView = window.getDecorView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                decorView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                        hideSystemUI();
                        return windowInsets;
                    }
                });
            }else {
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
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }

        return dialog;
    }


    @Override
    public void dismiss() {
        if (getFragmentManager().isStateSaved()) {
            super.dismissAllowingStateLoss();
        } else {
            super.dismiss();
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (manager.isStateSaved()) {
            showAllowingStateLoss(manager, tag);
        } else {
            super.show(manager, tag);
        }
        showImmersive(manager);
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        int result = super.show(transaction, tag);
        showImmersive(getFragmentManager());
        return result;
    }

    private void showAllowingStateLoss(FragmentManager manager, String tag) {
        try {
            Field mDismissed = this.getClass().getSuperclass().getDeclaredField("mDismissed");
            Field mShownByMe = this.getClass().getSuperclass().getDeclaredField("mShownByMe");
            mDismissed.setAccessible(true);
            mShownByMe.setAccessible(true);
            mDismissed.setBoolean(this, false);
            mShownByMe.setBoolean(this, true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
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

    /**
     * 隐藏系统UI
     */
    public void hideSystemUI() {
        if (getDialog() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                getDialog().getWindow().getInsetsController().hide(WindowInsets.Type.systemBars());
            } else {
                getDialog().getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }

    }

    protected void showImmersive(FragmentManager manager) {
        // It is necessary to call executePendingTransactions() on the FragmentManager
        // before hiding the navigation bar, because otherwise getWindow() would raise a
        // NullPointerException since the window was not yet created.
        manager.executePendingTransactions();

        // Copy flags from the activity, assuming it's fullscreen.
        // It is important to do this after show() was called. If we would do this in onCreateDialog(),
        // we would get a requestFeature() error.
        if (getDialog() != null) {
            getDialog().getWindow().getDecorView().setSystemUiVisibility(
                    getActivity().getWindow().getDecorView().getSystemUiVisibility()
            );

            // Make the dialogs window focusable again
            getDialog().getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            );
        }

    }

    public void setDismissListener(OnDialogFragmentDismissListener dismissListener) {
        mDismissListener = dismissListener;
    }

    @Override
    public String getExtraInfo() {
        return mExtraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        mExtraInfo = extraInfo;
    }

}
