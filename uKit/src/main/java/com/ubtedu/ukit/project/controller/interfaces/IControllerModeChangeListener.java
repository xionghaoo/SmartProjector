package com.ubtedu.ukit.project.controller.interfaces;

import com.ubtedu.ukit.project.controller.ControllerModeHolder;

/**
 * @Author naOKi
 * @Date 2018/11/17
 **/
public interface IControllerModeChangeListener {
    void onControllerModeChanged(@ControllerModeHolder.ControllerMode int controllerMode);
}
