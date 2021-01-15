/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.controller;

import com.ubtedu.alpha1x.core.mvp.BasePresenter;
import com.ubtedu.alpha1x.core.mvp.BaseUI;
import com.ubtedu.ukit.project.controller.model.define.ToolbarType;
import com.ubtedu.ukit.project.controller.model.item.ToolbarItem;
import com.ubtedu.ukit.project.controller.model.item.WidgetItem;

import java.util.ArrayList;

/**
 * @author naOKi
 * @date 2018/11/17
 */
public interface ControllerContracts {

    abstract class UI extends BaseUI<Presenter> {
        public abstract void showAllOperationBar();
        public abstract void hideAllOperationBar();
        public abstract void showWidgetGroup(ToolbarType group);
        public abstract void hideWidgetGroup();
        public abstract void showWidgetAction(WidgetItem item);
        public abstract void hideWidgetAction();
    }

    abstract class Presenter extends BasePresenter<UI> {
        public abstract ArrayList<ToolbarItem> loadToolbarItems();
        public abstract ArrayList<WidgetItem> loadButtonItems();
        public abstract void entryControllerEditMode();
        public abstract void entryControllerExecutionMode();
    }
}
