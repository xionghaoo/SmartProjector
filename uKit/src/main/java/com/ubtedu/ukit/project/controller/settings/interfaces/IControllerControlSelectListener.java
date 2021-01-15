package com.ubtedu.ukit.project.controller.settings.interfaces;

import com.ubtedu.ukit.project.controller.model.config.Joystick4DirectionConfig;

/**
 * @Author naOKi
 * @Date 2018-12-05
 **/
public interface IControllerControlSelectListener {
	void onControlItemSelected(int selectedPosition, Joystick4DirectionConfig config);
}
