/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.user.login;

import com.ubtedu.alpha1x.core.mvp.BasePresenter;
import com.ubtedu.alpha1x.core.mvp.BaseUI;
import com.ubtedu.ukit.common.locale.UBTLocale;
import com.ubtedu.ukit.user.vo.GdprUserPactInfo;
import com.ubtedu.ukit.user.vo.LoginAccountInfo;

import java.util.List;

/**
 * @author qinicy
 * @Description: $description
 * @date 2018/11/06
 */
public interface LoginContracts {

    abstract class UI extends BaseUI<Presenter> {
        public abstract void onLoginSuccess(boolean isGuest);

        public abstract void showGdprPactDialog(int type, String abstractText, String url);

        public abstract void onLoadLocaleInfosFinish(List<UBTLocale> localeList);
    }

    abstract class Presenter extends BasePresenter<UI> {
        public abstract void initGdprPacts();

        public abstract void loadLocaleInfos();

        public abstract void login(LoginAccountInfo account, String password);

       public abstract void loginGuest();

        public abstract void toRegister();

        public abstract void toShowReadOnlyGdprPact(@GdprUserPactInfo.PactType int type);

        public abstract void onAcceptGdprPact();
    }
}
