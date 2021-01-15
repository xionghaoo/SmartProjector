/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.home;

import com.ubtedu.alpha1x.core.mvp.BasePresenter;
import com.ubtedu.alpha1x.core.mvp.BaseUI;

/**
 * @author qinicy
 * @date 2018/11/07
 */
public interface HomeContracts {

    abstract class UI extends BaseUI<Presenter> {

    }

    abstract class Presenter extends BasePresenter<UI> {

        public abstract void checkLoginOverdue();

        public abstract void reportActiveUserEvent();

        public abstract void loadUnityInfo();

        public abstract void checkLoginTokenExpired();

        public abstract void otaUpgrade();

        public abstract void skipDisconnectBt(boolean skip);
    }
}
