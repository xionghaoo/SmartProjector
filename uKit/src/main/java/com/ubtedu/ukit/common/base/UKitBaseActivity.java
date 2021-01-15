/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.base;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.core.base.activity.UbtBaseActivity;
import com.ubtedu.alpha1x.core.mvp.IPresenter;
import com.ubtedu.alpha1x.core.mvp.IView;
import com.ubtedu.alpha1x.core.mvp.UIDelegate;
import com.ubtedu.alpha1x.utils.KeyBoardHelper;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.application.BasicEventDelegate;
import com.ubtedu.ukit.common.locale.LanguageUtil;
import com.ubtedu.ukit.common.utils.PlatformUtil;
import com.ubtedu.ukit.home.HomeActivity;

/**
 * @Author qinicy
 * @Date 2018/11/6
 **/
public class UKitBaseActivity<P extends IPresenter, V extends IView> extends UbtBaseActivity<P, V> {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageUtil.attachBaseContext(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        if (getEventDelegate() instanceof BasicEventDelegate) {
            BasicEventDelegate delegate = (BasicEventDelegate) getEventDelegate();
            if (delegate.isAppRestartBySystem() && !(this instanceof HomeActivity)) {
                LogUtil.d("finish " + getClass().getSimpleName() + " because app is restarted by system!");
                finish();
            }
        }
    }

    @Override
    protected UIDelegate createUIDelegate() {
        UKitUIDelegate delegate = new UKitUIDelegate(this);
        //UKitUIDelegate默认使用的是UKitToastView，如果是chromeOS则需要用ChromeToastView
        if (PlatformUtil.isChromebookDevice()){
            View content = getWindow().getDecorView().findViewById(android.R.id.content);
            ChromeToastView toast = new ChromeToastView(this);
            toast.setParentView((ViewGroup) content);
            delegate.setToastView(toast);
        }
        return delegate;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        if (getWindow() != null && getWindow().getDecorView() != null) {
            KeyBoardHelper.hideSoftKeyBoard(getWindow().getDecorView());
        }
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //全局屏蔽返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public UKitUIDelegate getUIDelegate() {
        return (UKitUIDelegate) super.getUIDelegate();
    }
}
