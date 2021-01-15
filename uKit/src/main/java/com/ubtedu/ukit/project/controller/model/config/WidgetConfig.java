/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.controller.model.config;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.ukit.common.utils.UuidUtil;
import com.ubtedu.ukit.project.controller.model.define.WidgetType;

import java.io.Serializable;

/**
 * @Author naOKi
 * @Date 2018-12-03
 **/
public class WidgetConfig implements Serializable {

    public static final int MODE_EXECUTION_MOTION = 1;
    public static final int MODE_EXECUTION_BLOCKLY = 2;

    public static final int CONTROLTYPE_STEERINGGEAR = 0;
    public static final int CONTROLTYPE_MOTOR = 1;

    @SerializedName("id")
    public String id = UuidUtil.createUUID();
    
    @SerializedName("spanX")
    public int x = -1;
    
    @SerializedName("spanY")
    public int y = -1;

    @SerializedName("type")
    public String type;

    @SerializedName("name")
    public String name = null;

    @SerializedName("iconIndex")
    public Integer iconIndex = null;

    @SerializedName("mode")
    public Integer mode = null;

    @SerializedName("motionId")
    public String motionId = null;

    @SerializedName("blocklyID")
    public String blocklyId = null;

    @SerializedName("rotateDirection")
    public Integer direction = null;

    @SerializedName("controlId")
    public Integer controlSteeringGearId = null;

    @SerializedName("controlIds")
    public int[] controlSteeringGearIds = null;

    @SerializedName("controlMotorId")
    public Integer controlMotorId = null;

    @SerializedName("controlMotorIds")
    public int[] controlMotorIds = null;

    @SerializedName("controlType")
    public Integer controlType = null;

    @SerializedName("maxValue")
    public Integer maxValue = null;

    @SerializedName("minValue")
    public Integer minValue = null;

    @SerializedName("subscribeId")
    public String subscribeId = null;

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public String getType() {
        return type;
    }
    
    public static WidgetConfig conversionWidgetConfig(WidgetConfig config) {
        if(config == null) {
            return null;
        }
        WidgetType widgetType = WidgetType.findWidgetTypeByWidgetName(config.type);
        if(widgetType == null) {
            return null;
        }
        WidgetConfig result = null;
        switch(widgetType) {
        case WIDGET_UKROUNDJOYSTICK:
            result = new Joystick4DirectionConfig();
            break;
        case WIDGET_UKTWOWAYJOYSTICKH:
            result = new Joystick2DirectionHConfig();
            break;
        case WIDGET_UKTWOWAYJOYSTICKV:
            result = new Joystick2DirectionVConfig();
            break;
        case WIDGET_UKCUSTOMBUTTON:
            result = new ButtonCustomConfig();
            break;
        case WIDGET_UKINTSLIDER:
            result = new SliderValueConfig();
            break;
        case WIDGET_UKTOGGLESWITCH:
            result = new SwitchToggleConfig();
            break;
        case WIDGET_UKTOUCHSWITCH:
            result = new SwitchTouchConfig();
            break;
        case WIDGET_UKVALUEDISPLAY:
            result = new ReaderValueConfig();
            break;
        case WIDGET_UKCOLORDISPLAY:
            result = new ReaderColorConfig();
            break;
        case WIDGET_UKCUSTOMBUTTON_V2:
            result = new ButtonCustomConfigV2();
            break;
        case WIDGET_UKTOUCHSWITCH_V2:
            result = new SwitchTouchConfigV2();
            break;
        default:
            return null;
        }
        result.id = config.id;
        result.x = config.x;
        result.y = config.y;
        result.name = config.name;
        if(config.iconIndex != null) {
            result.iconIndex = Math.min(Math.max(config.iconIndex, 0), 36);
        } else {
            result.iconIndex = null;
        }
        result.mode = config.mode;
        result.motionId = config.motionId;
        result.blocklyId = config.blocklyId;
        result.controlSteeringGearId = config.controlSteeringGearId;
        result.controlSteeringGearIds = config.controlSteeringGearIds;
        result.controlMotorId = config.controlMotorId;
        result.controlMotorIds = config.controlMotorIds;
        result.controlType = config.controlType;
        result.direction = config.direction;
        result.maxValue = config.maxValue;
        result.minValue = config.minValue;
        result.subscribeId = config.subscribeId;
        return result;
    }
    
}
