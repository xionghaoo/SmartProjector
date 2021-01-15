/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.bluetooth.peripheral.setting.modifymode;

import com.ubtedu.ukit.project.Workspace;
import com.ubtedu.ukit.project.vo.ModelInfo;

/**
 * @Author naOKi
 * @Date 2018/12/04
 **/
public class PeripheralSettingModifyModePresenter extends PeripheralSettingModifyModeContracts.Presenter {

    @Override
    public ModelInfo getModelInfo() {
        if(Workspace.getInstance() == null || Workspace.getInstance().getProject() == null) {
            return null;
        }
        return Workspace.getInstance().getProject().modelInfo;
    }

    @Override
    public void updateModelInfo(ModelInfo modelInfo) {
        if(Workspace.getInstance() == null) {
            return;
        }
        Workspace.getInstance().saveProjectFile(modelInfo);
    }
}
