/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.user;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.core.mvp.IPresenter;
import com.ubtedu.alpha1x.core.mvp.IView;
import com.ubtedu.ukit.common.base.UKitBaseActivity;

/**
 * @Author qinicy
 * @Date 2019/2/23
 **/
public class UserBaseActivity<P extends IPresenter, V extends IView> extends UKitBaseActivity<P,V> {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserActivityStack.add(this);
    }

    @Override
    public void finish() {
        super.finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserActivityStack.remove(this);
    }
}
