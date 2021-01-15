package com.ubtedu.ukit.project.controller.model.config;

import com.ubtedu.ukit.project.controller.model.define.WidgetType;

/**
 * @Author naOKi
 * @Date 2018-12-03
 **/
public class SliderValueConfig extends WidgetConfig {

	{
		type = WidgetType.WIDGET_UKINTSLIDER.getWidgetName();
		name = WidgetType.WIDGET_UKINTSLIDER.getDefaultName();
		maxValue = 500;
		minValue = -500;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = Math.min(maxValue, 1000);
	}

	public int getMinValue() {
		return minValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = Math.max(minValue, -1000);
	}

	public int getDefaultValue() {
		return getMinValue() + (int) (getMaxValue() - getMinValue()) / 2;
	}

	public String getBlocklyId() {
		return blocklyId;
	}

	public void setBlocklyId(String blocklyId) {
		this.blocklyId = blocklyId;
	}

}
