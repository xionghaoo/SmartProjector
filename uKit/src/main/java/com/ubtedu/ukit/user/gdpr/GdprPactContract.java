package com.ubtedu.ukit.user.gdpr;

import com.ubtedu.alpha1x.core.mvp.BasePresenter;
import com.ubtedu.alpha1x.core.mvp.BaseUI;

/**
 * Created by qinicy on 2017/6/30.
 */

public interface GdprPactContract {
    abstract class UI extends BaseUI<Presenter> {
    }

    abstract class Presenter extends BasePresenter<UI> {
        public abstract void checkPack(String account, int type);
    }
}
