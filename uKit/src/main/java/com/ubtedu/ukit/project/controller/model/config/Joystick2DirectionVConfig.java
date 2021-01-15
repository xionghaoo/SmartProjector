package com.ubtedu.ukit.project.controller.model.config;

import com.ubtedu.ukit.project.controller.model.define.WidgetType;

/**
 * @Author naOKi
 * @Date 2018-12-03
 **/
public class Joystick2DirectionVConfig extends Joystick2DirectionHConfig {
	
	{
		type = WidgetType.WIDGET_UKTWOWAYJOYSTICKV.getWidgetName();
		name = WidgetType.WIDGET_UKTWOWAYJOYSTICKV.getDefaultName();
		controlSteeringGearId = -1;
		controlMotorId = -1;
		controlType = CONTROLTYPE_STEERINGGEAR;
		direction = COUNTERCLOCKWISE;
	}

	public static Joystick2DirectionHConfig conversion(Joystick2DirectionVConfig vConfig) {
		Joystick2DirectionHConfig hConfig = new Joystick2DirectionHConfig();
		hConfig.setX(vConfig.getX() - 2);
		hConfig.setY(vConfig.getY() + 2);
		hConfig.setName(vConfig.getName());
		hConfig.setControlType(vConfig.getControlType());
		hConfig.setControlId(vConfig.getControlId());
		hConfig.setDirection(vConfig.getDirection());
		return hConfig;
	}
	
}
