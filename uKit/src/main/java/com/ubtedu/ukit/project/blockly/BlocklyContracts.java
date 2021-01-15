/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.blockly;

import com.ubtedu.alpha1x.core.mvp.BasePresenter;
import com.ubtedu.alpha1x.core.mvp.BaseUI;

import io.reactivex.Observable;

/**
 * @author binghua.qin
 * @date 2019/02/21
 */
public interface BlocklyContracts {

    abstract class UI extends BaseUI<Presenter> {
        public abstract void loadUrl(String url);
        //abstract void templateMethod();
    }

    abstract class Presenter extends BasePresenter<UI> {
        public abstract void loadBlockly();

        public abstract Observable<String> initBlockly();
        //abstract void templateMethod();
     }
}
