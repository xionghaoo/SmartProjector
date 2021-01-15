package com.ubtedu.ukit.project.controller.model.config;

import com.ubtedu.ukit.project.controller.model.define.WidgetType;

/**
 * @Author naOKi
 * @Date 2018-12-03
 **/
public class Joystick2DirectionHConfig extends WidgetConfig {
	
	{
		type = WidgetType.WIDGET_UKTWOWAYJOYSTICKH.getWidgetName();
		name = WidgetType.WIDGET_UKTWOWAYJOYSTICKH.getDefaultName();
		controlSteeringGearId = -1;
		controlMotorId = -1;
		controlType = CONTROLTYPE_STEERINGGEAR;
		direction = COUNTERCLOCKWISE;
	}

	public static final int CLOCKWISE = 1;
	public static final int COUNTERCLOCKWISE = 2;

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getControlId() {
		if(getControlType() == CONTROLTYPE_MOTOR) {
			return controlMotorId;
		} else {
			return controlSteeringGearId;
		}
	}
	
	public void setControlId(int controlId) {
		if(getControlType() == CONTROLTYPE_MOTOR) {
			this.controlMotorId = controlId;
		} else {
			this.controlSteeringGearId = controlId;
		}
	}
	
	public int getDirection() {
		return direction == null ? COUNTERCLOCKWISE : direction;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}

	public static Joystick2DirectionVConfig conversion(Joystick2DirectionHConfig hConfig) {
		Joystick2DirectionVConfig vConfig = new Joystick2DirectionVConfig();
		vConfig.setX(hConfig.getX() + 2);
		vConfig.setY(hConfig.getY() - 2);
		vConfig.setName(hConfig.getName());
		vConfig.setControlType(hConfig.getControlType());
		vConfig.setControlId(hConfig.getControlId());
		vConfig.setDirection(hConfig.getDirection());
		return vConfig;
	}

	public int getControlType() {
		return controlType == null ? CONTROLTYPE_STEERINGGEAR : controlType;
	}

	public void setControlType(int type) {
		if(type != CONTROLTYPE_STEERINGGEAR && type != CONTROLTYPE_MOTOR) {
			return;
		}
		if(getControlType() == type) {
			return;
		}
		controlType = type;
		controlSteeringGearId = -1;
		controlMotorId = -1;
	}
	
}
