/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.controller.model.define;

import android.text.TextUtils;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.ubtedu.ukit.R;

/**
 * @Author naOKi
 * @Date 2018/11/28
 **/
public enum ToolbarType {
    TOOLBAR_JOYSTICK("Joystick", R.string.project_controller_toolbar_joystick, R.drawable.project_remote_sidebar_rocker_icon, 0xFF4779BB),
    TOOLBAR_BUTTON("Button", R.string.project_controller_toolbar_button, R.drawable.project_remote_sidebar_keys_icon, 0xFF4779BB),
    TOOLBAR_SLIDER("Slider", R.string.project_controller_toolbar_slider, R.drawable.project_remote_sidebar_bar_icon, 0xFF929AA8),
    TOOLBAR_SWITCH("Switch", R.string.project_controller_toolbar_switch, R.drawable.project_remote_sidebar_switch_icon, 0xFF60D064),
    TOOLBAR_DISPLAY("Display", R.string.project_controller_toolbar_display, R.drawable.project_remote_sidebar_reading_icon, 0xFF8297F5);
    String categoryName;
    @StringRes int stringId;
    @DrawableRes int resId;
    int selectColor;

    public String getCategoryName() {
        return categoryName;
    }

    public int getStringId() {
        return stringId;
    }

    public int getResId() {
        return resId;
    }

    public int getSelectColor() {
        return selectColor;
    }

    ToolbarType(String categoryName, int stringId, int resId, int selectColor) {
        this.categoryName = categoryName;
        this.stringId = stringId;
        this.resId = resId;
        this.selectColor = selectColor;
    }

    public static ToolbarType findToolbarTypeByCategoryName(String categoryName) {
        for (ToolbarType type : ToolbarType.values()) {
            if (TextUtils.equals(type.getCategoryName(), categoryName)) {
                return type;
            }
        }
        return null;
    }

}
