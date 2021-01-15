/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.feedback;

import com.ubtedu.alpha1x.core.mvp.BasePresenter;
import com.ubtedu.alpha1x.core.mvp.BaseUI;
/**
 * @author qinicy
 * @date 2018/12/13
 */
public interface MenuFeedbackContracts {

    abstract class UI extends BaseUI<Presenter> {
        public abstract void onFeedbackResult(boolean isSuccess);
    }

    abstract class Presenter extends BasePresenter<UI> {
        public abstract void feedback(String email,String content);
     }
}
