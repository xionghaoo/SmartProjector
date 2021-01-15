package com.ubtedu.ukit.launcher;


import com.ubtedu.alpha1x.core.mvp.BasePresenter;
import com.ubtedu.alpha1x.core.mvp.BaseUI;


public interface LauncherContracts {
    abstract class UI extends BaseUI<Presenter> {
        public abstract void finishActivity();
    }

    abstract class Presenter extends BasePresenter<UI> {
        public abstract void onAllPermissionsGranted();
        public abstract void skip();
    }
}
