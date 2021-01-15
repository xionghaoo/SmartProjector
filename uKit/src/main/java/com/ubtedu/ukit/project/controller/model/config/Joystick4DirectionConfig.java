package com.ubtedu.ukit.project.controller.model.config;

import com.ubtedu.ukit.project.controller.model.define.WidgetType;

/**
 * @Author naOKi
 * @Date 2018-12-03
 **/
public class Joystick4DirectionConfig extends WidgetConfig {
	
	{
		type = WidgetType.WIDGET_UKROUNDJOYSTICK.getWidgetName();
		controlSteeringGearIds = new int[]{-1, -1};
		controlMotorIds = new int[]{-1, -1};
		controlType = CONTROLTYPE_STEERINGGEAR;
		mode = MODE_TWO_WHEEL_DRIVE;
	}
	
	public static final int MODE_TWO_WHEEL_DRIVE = 0;
	public static final int MODE_FOUR_WHEEL_DRIVE = 1;
	
	public static final int LEFT_FRONT_ID = 0;
	public static final int RIGHT_FRONT_ID = 1;
	public static final int LEFT_BEHIND_ID = 2;
	public static final int RIGHT_BEHIND_ID = 3;
	
	public static final int LEFT_ID = 0;
	public static final int RIGHT_ID = 1;

	public int getLeftFrontId() {
		if(mode != MODE_FOUR_WHEEL_DRIVE) {
			return -1;
		}
		if(getControlType() == CONTROLTYPE_MOTOR) {
			return controlMotorIds == null ? -1 : controlMotorIds[LEFT_FRONT_ID];
		} else {
			return controlSteeringGearIds == null ? -1 : controlSteeringGearIds[LEFT_FRONT_ID];
		}
	}
	
	public int getRightFrontId() {
		if(mode != MODE_FOUR_WHEEL_DRIVE) {
			return -1;
		}
		if(getControlType() == CONTROLTYPE_MOTOR) {
			return controlMotorIds == null ? -1 : controlMotorIds[RIGHT_FRONT_ID];
		} else {
			return controlSteeringGearIds == null ? -1 : controlSteeringGearIds[RIGHT_FRONT_ID];
		}
	}
	
	public int getLeftBehindId() {
		if(mode != MODE_FOUR_WHEEL_DRIVE) {
			return -1;
		}
		if(getControlType() == CONTROLTYPE_MOTOR) {
			return controlMotorIds == null ? -1 : controlMotorIds[LEFT_BEHIND_ID];
		} else {
			return controlSteeringGearIds == null ? -1 : controlSteeringGearIds[LEFT_BEHIND_ID];
		}
	}
	
	public int getRightBehindId() {
		if(mode != MODE_FOUR_WHEEL_DRIVE) {
			return -1;
		}
		if(getControlType() == CONTROLTYPE_MOTOR) {
			return controlMotorIds == null ? -1 : controlMotorIds[RIGHT_BEHIND_ID];
		} else {
			return controlSteeringGearIds == null ? -1 : controlSteeringGearIds[RIGHT_BEHIND_ID];
		}
	}
	
	public int getLeftId() {
		if(mode != MODE_TWO_WHEEL_DRIVE) {
			return -1;
		}
		if(getControlType() == CONTROLTYPE_MOTOR) {
			return controlMotorIds == null ? -1 : controlMotorIds[LEFT_ID];
		} else {
			return controlSteeringGearIds == null ? -1 : controlSteeringGearIds[LEFT_ID];
		}
	}
	
	public int getRightId() {
		if(mode != MODE_TWO_WHEEL_DRIVE) {
			return -1;
		}
		if(getControlType() == CONTROLTYPE_MOTOR) {
			return controlMotorIds == null ? -1 : controlMotorIds[RIGHT_ID];
		} else {
			return controlSteeringGearIds == null ? -1 : controlSteeringGearIds[RIGHT_ID];
		}
	}

	public int getControlId(int verb) {
		if(verb < 0) {
			return -1;
		}
		if((mode == MODE_FOUR_WHEEL_DRIVE && verb > 3) || (mode == MODE_TWO_WHEEL_DRIVE && verb > 1)) {
			return -1;
		}
		if(getControlType() == CONTROLTYPE_MOTOR) {
			return controlMotorIds == null ? -1 : controlMotorIds[verb];
		} else {
			return controlSteeringGearIds == null ? -1 : controlSteeringGearIds[verb];
		}
	}

	public void setControlId(int verb, int id) {
		if(verb < 0) {
			return;
		}
		if((mode == MODE_FOUR_WHEEL_DRIVE && verb > 3) || (mode == MODE_TWO_WHEEL_DRIVE && verb > 1)) {
			return;
		}
		initIdsIfNull();
		if(getControlType() == CONTROLTYPE_MOTOR) {
			controlMotorIds[verb] = id;
		} else {
			controlSteeringGearIds[verb] = id;
		}
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
		initIds();
	}
	
	public void setLeftFrontId(int id) {
		if(mode != MODE_FOUR_WHEEL_DRIVE) {
			return;
		}
		initIdsIfNull();
		if(getControlType() == CONTROLTYPE_MOTOR) {
			controlMotorIds[LEFT_FRONT_ID] = id;
		} else {
			controlSteeringGearIds[LEFT_FRONT_ID] = id;
		}
	}
	
	public void setRightFrontId(int id) {
		if(mode != MODE_FOUR_WHEEL_DRIVE) {
			return;
		}
		initIdsIfNull();
		if(getControlType() == CONTROLTYPE_MOTOR) {
			controlMotorIds[RIGHT_FRONT_ID] = id;
		} else {
			controlSteeringGearIds[RIGHT_FRONT_ID] = id;
		}
	}
	
	public void setLeftBehindId(int id) {
		if(mode != MODE_FOUR_WHEEL_DRIVE) {
			return;
		}
		initIdsIfNull();
		if(getControlType() == CONTROLTYPE_MOTOR) {
			controlMotorIds[LEFT_BEHIND_ID] = id;
		} else {
			controlSteeringGearIds[LEFT_BEHIND_ID] = id;
		}
	}
	
	public void setRightBehindId(int id) {
		if(mode != MODE_FOUR_WHEEL_DRIVE) {
			return;
		}
		initIdsIfNull();
		if(getControlType() == CONTROLTYPE_MOTOR) {
			controlMotorIds[RIGHT_BEHIND_ID] = id;
		} else {
			controlSteeringGearIds[RIGHT_BEHIND_ID] = id;
		}
	}
	
	public void setLeftId(int id) {
		if(mode != MODE_TWO_WHEEL_DRIVE) {
			return;
		}
		initIdsIfNull();
		if(getControlType() == CONTROLTYPE_MOTOR) {
			controlMotorIds[LEFT_ID] = id;
		} else {
			controlSteeringGearIds[LEFT_ID] = id;
		}
	}
	
	public void setRightId(int id) {
		if(mode != MODE_TWO_WHEEL_DRIVE) {
			return;
		}
		initIdsIfNull();
		if(getControlType() == CONTROLTYPE_MOTOR) {
			controlMotorIds[RIGHT_ID] = id;
		} else {
			controlSteeringGearIds[RIGHT_ID] = id;
		}
	}
	
	public int getMode() {
		return mode;
	}
	
	public void setMode(int mode) {
		if(MODE_TWO_WHEEL_DRIVE != mode && MODE_FOUR_WHEEL_DRIVE != mode) {
			return;
		}
		if(this.mode == mode) {
			return;
		}
		this.mode = mode;
		initIds();
	}

	private void initIds() {
		if(MODE_TWO_WHEEL_DRIVE == mode) {
			controlSteeringGearIds = new int[]{-1, -1};
			controlMotorIds = new int[]{-1, -1};
		} else {
			controlSteeringGearIds = new int[]{-1, -1, -1, -1};
			controlMotorIds = new int[]{-1, -1, -1, -1};
		}
	}

	private void initIdsIfNull() {
		if(MODE_TWO_WHEEL_DRIVE == mode) {
			if(controlSteeringGearIds == null) {
				controlSteeringGearIds = new int[]{-1, -1};
			}
			if(controlMotorIds == null) {
				controlMotorIds = new int[]{-1, -1};
			}
		} else {
			if(controlSteeringGearIds == null) {
				controlSteeringGearIds = new int[]{-1, -1, -1, -1};
			}
			if(controlMotorIds == null) {
				controlMotorIds = new int[]{-1, -1, -1, -1};
			}
		}
	}

}
