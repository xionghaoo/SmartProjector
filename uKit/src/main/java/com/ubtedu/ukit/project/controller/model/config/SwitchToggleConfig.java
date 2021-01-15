package com.ubtedu.ukit.project.controller.model.config;

import com.ubtedu.ukit.project.controller.model.define.WidgetType;

/**
 * @Author naOKi
 * @Date 2018-12-03
 **/
public class SwitchToggleConfig extends WidgetConfig {
	
	{
		type = WidgetType.WIDGET_UKTOGGLESWITCH.getWidgetName();
		name = WidgetType.WIDGET_UKTOGGLESWITCH.getDefaultName();
		mode = -1;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getMotionId() {
		if(MODE_EXECUTION_MOTION != getMode()) {
			return null;
		}
		return motionId;
	}

	public void setMotionId(String motionId) {
		this.motionId = motionId;
	}

	public int getMode() {
		return mode == null ? -1 : mode;
	}

	public void setMode(int mode) {
		if(this.mode != null && this.mode == mode) {
			return;
		}
		this.mode = mode;
	}

	public String getBlocklyId() {
		return blocklyId;
	}

	public void setBlocklyId(String blocklyId) {
		this.blocklyId = blocklyId;
	}
}
