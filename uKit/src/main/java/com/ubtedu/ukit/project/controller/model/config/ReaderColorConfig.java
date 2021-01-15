package com.ubtedu.ukit.project.controller.model.config;

import com.ubtedu.ukit.project.controller.model.define.WidgetType;

/**
 * @Author naOKi
 * @Date 2018-12-03
 **/
public class ReaderColorConfig extends WidgetConfig {
	
	{
		type = WidgetType.WIDGET_UKCOLORDISPLAY.getWidgetName();
		name = WidgetType.WIDGET_UKCOLORDISPLAY.getDefaultName();
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
