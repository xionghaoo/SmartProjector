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
public enum WidgetProperty {
    PROPERTY_JOYSTICKSETTING("JoystickSetting", R.drawable.project_remote_float_rocker_btn, R.string.project_controller_property_setting),
    PROPERTY_RENAME("Rename", R.drawable.project_remote_float_rename_btn, R.string.project_controller_property_rename),
    PROPERTY_SWITCHDIRECTION("SwitchDirection", R.drawable.project_remote_float_switch_btn, R.string.project_controller_property_rotate),
    PROPERTY_BLOCKLYCODE("BlocklyCode", R.drawable.project_remote_float_blockly_btn, R.string.project_controller_property_programming),
    PROPERTY_PERFORMACTION("PerformAction", R.drawable.project_remote_float_act_btn, R.string.project_controller_property_action),
    PROPERTY_SETTINGSICON("SettingsIcon", R.drawable.project_remote_float_acticon_btn, R.string.project_controller_property_icon),
    PROPERTY_USAGE("Usage", R.drawable.project_remote_float_usage_btn, R.string.project_controller_property_usage),
    PROPERTY_RANGESETTING("RangeSetting", R.drawable.project_remote_float_range_btn, R.string.project_controller_property_range);
    String propertyName;
    @DrawableRes int resId;
    @StringRes int nameResId;

    public String getPropertyName() {
        return propertyName;
    }

    public int getResId() {
        return resId;
    }

    public String getName() {
        return UKitApplication.getInstance().getApplicationContext().getString(nameResId);
    }

    WidgetProperty(String propertyName, int resId, int nameResId) {
        this.propertyName = propertyName;
        this.resId = resId;
        this.nameResId = nameResId;
    }

    public static WidgetProperty findWidgetPropertyByPropertyName(String propertyName) {
        for (WidgetProperty type : WidgetProperty.values()) {
            if (TextUtils.equals(type.getPropertyName(), propertyName)) {
                return type;
            }
        }
        return null;
    }
}
