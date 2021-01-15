/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.account;

import com.ubtedu.alpha1x.core.mvp.BasePresenter;
import com.ubtedu.alpha1x.core.mvp.BaseUI;
/**
 * @author qinicy
 * @date 2018/12/12
 */
public interface MenuAccountContracts {

    abstract class UI extends BaseUI<Presenter> {
        public abstract void onRenameResult(boolean success);

        public abstract void onDeleteAccountSuccess();

        public abstract void onModifyAvatarSuccess(String success);

    }

    abstract class Presenter extends BasePresenter<UI> {
       public abstract void rename(String newName);


        public abstract void updateUserInfo(String avatarPath);
    }
}
