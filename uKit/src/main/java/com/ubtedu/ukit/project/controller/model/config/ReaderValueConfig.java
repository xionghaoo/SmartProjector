package com.ubtedu.ukit.project.controller.model.config;

import com.ubtedu.ukit.project.controller.model.define.WidgetType;

/**
 * @Author naOKi
 * @Date 2018-12-03
 **/
public class ReaderValueConfig extends WidgetConfig {
	
	{
		type = WidgetType.WIDGET_UKVALUEDISPLAY.getWidgetName();
		name = WidgetType.WIDGET_UKVALUEDISPLAY.getDefaultName();
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
