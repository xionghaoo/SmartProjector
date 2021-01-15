/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.user.register;

import com.ubtedu.alpha1x.core.mvp.BasePresenter;
import com.ubtedu.alpha1x.core.mvp.BaseUI;
/**
 * @author binghua.qin
 * @date 2019/02/23
 */
public interface RegisterPasswordContracts {

    abstract class UI extends BaseUI<Presenter> {
        public abstract void onLoginSuccess(boolean isGuest);
    }

    abstract class Presenter extends BasePresenter<UI> {
        public abstract void register(RegisterAccountInfo account, String password);

        public abstract void resetPassword(RegisterAccountInfo account, String password);
     }
}
