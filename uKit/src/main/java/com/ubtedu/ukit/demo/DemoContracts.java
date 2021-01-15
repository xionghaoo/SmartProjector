/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.demo;


import com.ubtedu.alpha1x.core.mvp.BasePresenter;
import com.ubtedu.alpha1x.core.mvp.BaseUI;

/**
 *@Author qinicy
 *@Date 2018/9/4
 **/
public interface DemoContracts {
    abstract class UI extends BaseUI<Presenter> {
       abstract void onUpdateUserInfo(User user);
       abstract void onLoading(String msg);
       abstract void onError(String msg);
    }

    abstract class Presenter extends BasePresenter<UI> {
        abstract void requestUserInfo();
    }
}
