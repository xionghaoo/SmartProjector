/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.controller;

import com.ubtedu.ukit.project.controller.manager.ControllerManager;
import com.ubtedu.ukit.project.controller.model.item.ToolbarItem;
import com.ubtedu.ukit.project.controller.model.item.WidgetItem;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author naOKi
 * @date 2018/11/17
 */
public class ControllerPresenter extends ControllerContracts.Presenter {

    @Override
    public ArrayList<ToolbarItem> loadToolbarItems() {
        return new ArrayList<>(Arrays.asList(ToolbarItem.loadFromConfig()));
    }

    @Override
    public ArrayList<WidgetItem> loadButtonItems() {
        return new ArrayList<>(Arrays.asList(WidgetItem.loadFromConfig()));
    }
    
    @Override
    public void entryControllerEditMode() {
        ControllerModeHolder.setControllerMode(ControllerModeHolder.MODE_EDIT);
    }
    
    @Override
    public void entryControllerExecutionMode() {
        ControllerManager.enterExecutionMode();
    }
    
}
