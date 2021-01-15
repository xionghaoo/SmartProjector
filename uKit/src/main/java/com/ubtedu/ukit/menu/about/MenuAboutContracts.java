/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.about;

import com.ubtedu.alpha1x.core.mvp.BasePresenter;
import com.ubtedu.alpha1x.core.mvp.BaseUI;
import com.ubtedu.ukit.user.vo.GdprUserPactInfo;

/**
 * @author qinicy
 * @date 2018/12/19
 */
public interface MenuAboutContracts {

    abstract class UI extends BaseUI<Presenter> {
        public abstract void showGdprPactDialog(@GdprUserPactInfo.PactType int type,String abstractText,String url);
    }

    abstract class Presenter extends BasePresenter<UI> {
        public abstract void initGdprs();

        public abstract String getBlocklyVersion();

        public abstract void toShowGdprPact(@GdprUserPactInfo.PactType int type);
     }
}
