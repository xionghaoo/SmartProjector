/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.user.edu;

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
public interface ModifyEduPasswordContracts {

    abstract class UI extends BaseUI<Presenter> {
        public abstract void onUpdatePasswordSuccess();

    }

    abstract class Presenter extends BasePresenter<UI> {
        public abstract void updatePassword(String oldPwd,String newPwd);
    }
}
