package com.ubtedu.ukit.project.controller.factory;

import android.content.Context;

import com.ubtedu.ukit.project.controller.model.config.Joystick2DirectionHConfig;
import com.ubtedu.ukit.project.controller.model.config.Joystick2DirectionVConfig;
import com.ubtedu.ukit.project.controller.model.config.WidgetConfig;
import com.ubtedu.ukit.project.controller.model.define.WidgetType;
import com.ubtedu.ukit.project.controller.model.item.WidgetItem;
import com.ubtedu.ukit.project.controller.widget.ButtonCustomView;
import com.ubtedu.ukit.project.controller.widget.ButtonCustomViewV2;
import com.ubtedu.ukit.project.controller.widget.ControllerWidgetView;
import com.ubtedu.ukit.project.controller.widget.Joystick2DirectionViewH;
import com.ubtedu.ukit.project.controller.widget.Joystick2DirectionViewV;
import com.ubtedu.ukit.project.controller.widget.Joystick4DirectionView;
import com.ubtedu.ukit.project.controller.widget.ReaderColorView;
import com.ubtedu.ukit.project.controller.widget.ReaderValueView;
import com.ubtedu.ukit.project.controller.widget.SliderValueView;
import com.ubtedu.ukit.project.controller.widget.SwitchToggleView;
import com.ubtedu.ukit.project.controller.widget.SwitchTouchView;
import com.ubtedu.ukit.project.controller.widget.SwitchTouchViewV2;

/**
 * @Author naOKi
 * @Date 2018-11-17
 **/
public class ControllerWidgetViewFactory {
	
	private ControllerWidgetViewFactory() {}
	
	public static ControllerWidgetView createViewByWidgetItem(Context context, WidgetItem widgetItem) {
		if(widgetItem == null) {
			return null;
		}
		return createViewByWidgetType(context, widgetItem.getWidgetType());
	}

	public static ControllerWidgetView createViewByWidgetType(Context context, WidgetType widgetType) {
		if(widgetType == null) {
			return null;
		}
		ControllerWidgetView result = null;
		switch (widgetType) {
		case WIDGET_UKROUNDJOYSTICK:
			result = new Joystick4DirectionView(context);
			break;
		case WIDGET_UKTWOWAYJOYSTICKH:
			result = new Joystick2DirectionViewH(context);
			break;
		case WIDGET_UKTWOWAYJOYSTICKV:
			result = new Joystick2DirectionViewV(context);
			break;
		case WIDGET_UKINTSLIDER:
			result = new SliderValueView(context);
			break;
		case WIDGET_UKCOLORDISPLAY:
			result = new ReaderColorView(context);
			break;
		case WIDGET_UKVALUEDISPLAY:
			result = new ReaderValueView(context);
			break;
		case WIDGET_UKTOGGLESWITCH:
			result = new SwitchToggleView(context);
			break;
		case WIDGET_UKTOUCHSWITCH:
			result = new SwitchTouchView(context);
			break;
		case WIDGET_UKCUSTOMBUTTON:
			result = new ButtonCustomView(context);
			break;
		case WIDGET_UKCUSTOMBUTTON_V2:
			result = new ButtonCustomViewV2(context);
			break;
		case WIDGET_UKTOUCHSWITCH_V2:
			result = new SwitchTouchViewV2(context);
			break;
		default:
			return result;
		}
		return result;
	}
	
	public static ControllerWidgetView createViewByWidgetConfig(Context context, WidgetConfig widgetConfig) {
		if(widgetConfig == null) {
			return null;
		}
		WidgetType widgetType = WidgetType.findWidgetTypeByWidgetName(widgetConfig.type);
		if(widgetType == null) {
			return null;
		}
		ControllerWidgetView result = createViewByWidgetType(context, widgetType);
		if(result == null) {
			return null;
		}
		result.setWidgetConfig(WidgetConfig.conversionWidgetConfig(widgetConfig));
		return result;
	}

	public static Joystick2DirectionViewH conversionView(Context context, Joystick2DirectionViewV viewV) {
		Joystick2DirectionViewH viewH = new Joystick2DirectionViewH(context);
		Joystick2DirectionHConfig hConfig = Joystick2DirectionVConfig.conversion(viewV.getWidgetConfig());
		viewH.setWidgetConfig(hConfig);
		return viewH;
	}

	public static Joystick2DirectionViewV conversionView(Context context, Joystick2DirectionViewH viewH) {
		Joystick2DirectionViewV viewV = new Joystick2DirectionViewV(context);
		Joystick2DirectionVConfig vConfig = Joystick2DirectionHConfig.conversion(viewH.getWidgetConfig());
		viewV.setWidgetConfig(vConfig);
		return viewV;
	}

	public static ControllerWidgetView clone(Context context, ControllerWidgetView sourceView) {
		return createViewByWidgetConfig(context, sourceView.getWidgetConfig());
	}
	
}
