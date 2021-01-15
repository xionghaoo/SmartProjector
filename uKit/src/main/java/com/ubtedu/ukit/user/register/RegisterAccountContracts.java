/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.user.register;

import com.ubtedu.alpha1x.core.mvp.BasePresenter;
import com.ubtedu.alpha1x.core.mvp.BaseUI;
import com.ubtedu.ukit.common.locale.UBTLocale;

import java.util.List;

/**
 * @author binghua.qin
 * @date 2019/02/22
 */
public interface RegisterAccountContracts {

    abstract class UI extends BaseUI<Presenter> {
        public abstract void onLoadLocaleInfosFinish(List<UBTLocale> localeList);

        public abstract void showAccountExistDialog();

        public abstract void toRegisterVerify();
        //abstract void templateMethod();
    }

    abstract class Presenter extends BasePresenter<UI> {

        public abstract void initRegisterViewModel();

        public abstract void checkUserExists(RegisterAccountInfo account);

        public abstract void loadLocaleInfos();
        //abstract void templateMethod();
     }
}
