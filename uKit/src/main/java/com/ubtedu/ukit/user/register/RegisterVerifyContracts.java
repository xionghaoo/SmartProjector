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
public interface RegisterVerifyContracts {

    abstract class UI extends BaseUI<Presenter> {
        public abstract void onSendCodeComplete();

        public abstract void updateVerificationCodeTime(int second);
    }

    abstract class Presenter extends BasePresenter<UI> {
        public abstract void initVerificationCodeTimer();


        public abstract void requestVerificationCode(boolean isRegister);

        public abstract void checkVerifyCode(String verifyCode, boolean register);
     }
}
