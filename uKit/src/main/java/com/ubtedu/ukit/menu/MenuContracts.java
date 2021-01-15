/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu;

import com.ubtedu.alpha1x.core.mvp.BasePresenter;
import com.ubtedu.alpha1x.core.mvp.BaseUI;
/**
 * @author qinicy
 * @date 2018/11/29
 */
public interface MenuContracts {

    abstract class UI extends BaseUI<Presenter> {
       //abstract void templateMethod();
    }

    abstract class Presenter extends BasePresenter<UI> {

     }
}
