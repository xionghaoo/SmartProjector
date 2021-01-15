package com.ubtedu.ukit.bluetooth.peripheral.setting.modifymode;


import com.ubtedu.alpha1x.core.mvp.BasePresenter;
import com.ubtedu.alpha1x.core.mvp.BaseUI;
import com.ubtedu.ukit.project.vo.ModelInfo;

/**
 * @Author naOKi
 * @Date 2018/12/04
 **/
public interface PeripheralSettingModifyModeContracts {
    abstract class UI extends BaseUI<Presenter> {
    }

    abstract class Presenter extends BasePresenter<UI> {
        public abstract ModelInfo getModelInfo();
        public abstract void updateModelInfo(ModelInfo modelInfo);
    }
}
