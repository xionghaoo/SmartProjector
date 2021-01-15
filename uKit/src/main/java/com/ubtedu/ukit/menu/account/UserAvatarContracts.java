/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.account;

import android.graphics.Bitmap;

import com.ubtedu.alpha1x.core.mvp.BasePresenter;
import com.ubtedu.alpha1x.core.mvp.BaseUI;

import io.reactivex.Observable;

/**
 * @author binghua.qin
 * @date 2019/02/26
 */
public interface UserAvatarContracts {

    abstract class UI extends BaseUI<Presenter> {
    }

    abstract class Presenter extends BasePresenter<UI> {
        public abstract void save();

        public abstract Observable<Bitmap> loadAvatar(String url);
    }
}
