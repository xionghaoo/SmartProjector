/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.controller.model.define;

import android.text.TextUtils;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.UKitApplication;

/**
 * @Author naOKi
 * @Date 2018/11/28
 **/
public enum WidgetType {
    WIDGET_UKROUNDJOYSTICK("UKRoundJoystick", R.string.project_controller_widget_joystick_round_name, -1, -1),
    WIDGET_UKTWOWAYJOYSTICKH("UKTwoWayJoystickH", R.string.project_controller_widget_joystick_2way_name, R.string.project_controller_widget_joystick_2way_rename_hint, R.drawable.layer_controller_widget_other_bg),
    WIDGET_UKTWOWAYJOYSTICKV("UKTwoWayJoystickV", R.string.project_controller_widget_joystick_2way_name, R.string.project_controller_widget_joystick_2way_rename_hint, R.drawable.layer_controller_widget_other_bg),
    WIDGET_UKCUSTOMBUTTON("UKCustomButton", R.string.project_controller_widget_custom_button_name, R.string.project_controller_widget_custom_button_rename_hint, -1),
    WIDGET_UKCUSTOMBUTTON_V2("UKCustomButtonV2", R.string.project_controller_widget_custom_button_name, R.string.project_controller_widget_custom_button_rename_hint, -1),
    WIDGET_UKINTSLIDER("UKIntSlider", R.string.project_controller_widget_slider_int_name, R.string.project_controller_widget_slider_int_rename_hint, R.drawable.layer_controller_widget_other_bg),
    WIDGET_UKTOGGLESWITCH("UKToggleSwitch", R.string.project_controller_widget_switch_toggle_name, R.string.project_controller_widget_switch_toggle_rename_hint, R.drawable.layer_controller_widget_other_bg),
    WIDGET_UKTOUCHSWITCH("UKTouchSwitch", R.string.project_controller_widget_switch_touch_name, R.string.project_controller_widget_switch_touch_rename_hint, R.drawable.layer_controller_widget_other_bg),
    WIDGET_UKTOUCHSWITCH_V2("UKTouchSwitchV2", R.string.project_controller_widget_switch_touch_name, R.string.project_controller_widget_switch_touch_rename_hint, R.drawable.layer_controller_widget_other_bg),
    WIDGET_UKVALUEDISPLAY("UKValueDisplay", R.string.project_controller_widget_reader_value_name, R.string.project_controller_widget_reader_value_rename_hint, R.drawable.layer_controller_widget_other_bg, R.string.project_controller_setting_value_usage_msg),
    WIDGET_UKCOLORDISPLAY("UKColorDisplay", R.string.project_controller_widget_reader_color_name, R.string.project_controller_widget_reader_color_rename_hint, R.drawable.layer_controller_widget_other_bg, R.string.project_controller_setting_color_usage_msg);
    String widgetName;
    @StringRes
    int nameResId;
    @StringRes
    int renameHintId;
    @StringRes
    int usageId;
    @DrawableRes
    int bgResId;

    public String getWidgetName() {
        return widgetName;
    }

    public int getBgResId() {
        return bgResId;
    }

    WidgetType(String widgetName, int nameResId, int renameHintId, int bgResId) {
        this.widgetName = widgetName;
        this.nameResId = nameResId;
        this.renameHintId = renameHintId;
        this.bgResId = bgResId;
    }

    WidgetType(String widgetName, int nameResId, int renameHintId, int bgResId, int usageId) {
        this.widgetName = widgetName;
        this.nameResId = nameResId;
        this.renameHintId = renameHintId;
        this.bgResId = bgResId;
        this.usageId = usageId;
    }


    public String getDefaultName() {
        return UKitApplication.getInstance().getApplicationContext().getString(nameResId);
    }

    public String getRenameHint() {
        try {
            return UKitApplication.getInstance().getApplicationContext().getString(renameHintId);
        } catch (Exception e) {
            return "";
        }
    }

    public String getUsage() {
        try {
            return UKitApplication.getInstance().getApplicationContext().getString(usageId);
        } catch (Exception e) {
            return "";
        }
    }

    public static WidgetType findWidgetTypeByWidgetName(String widgetName) {
        for (WidgetType type : WidgetType.values()) {
            if (TextUtils.equals(type.getWidgetName(), widgetName)) {
                return type;
            }
        }
        return null;
    }
}
