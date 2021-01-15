package com.ubtedu.ukit.project.controller.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.BtInvocationFactory;
import com.ubtedu.ukit.bluetooth.UKitCommandPriority;
import com.ubtedu.ukit.bluetooth.error.BluetoothCommonErrorHelper;
import com.ubtedu.ukit.bluetooth.model.SteeringGear;
import com.ubtedu.ukit.bluetooth.processor.PeripheralErrorCollector;
import com.ubtedu.ukit.menu.settings.Settings;
import com.ubtedu.ukit.project.controller.ControllerModeHolder;
import com.ubtedu.ukit.project.controller.manager.ControllerManager;
import com.ubtedu.ukit.project.controller.model.config.Joystick2DirectionHConfig;
import com.ubtedu.ukit.project.controller.model.config.Joystick2DirectionVConfig;
import com.ubtedu.ukit.project.controller.model.config.WidgetConfig;
import com.ubtedu.ukit.project.controller.model.define.WidgetType;

/**
 * @Author naOKi
 * @Date 2018-11-29
 **/
public class Joystick2DirectionViewV extends ControllerWidgetView<Joystick2DirectionVConfig> implements SeekBar.OnSeekBarChangeListener {

    private TextView name;
    private ControllerSeekBar slider;

    private boolean showSetupDialog = false;
    private boolean showBluetoothNotConnectDialog = true;
    public Joystick2DirectionViewV(Context context) {
        this(context, null);
    }

    public Joystick2DirectionViewV(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initJoystick2DirectionViewV();
    }

    private void initJoystick2DirectionViewV() {
        View view = inflateView(R.layout.item_project_controller_joystick_2direction_vertical);
        name = view.findViewById(R.id.item_project_controller_joystick_name);
        slider = view.findViewById(R.id.item_project_controller_joystick_slider);
        addWidgetView(view);
        slider.setOnSeekBarChangeListener(this);
    }

    public void setNameVisibility(boolean visibility) {
        name.setVisibility(visibility ? VISIBLE : GONE);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_EXECUTION || ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_PREVIEW) {
            if (!showSetupDialog && fromUser && BluetoothHelper.isConnected() && getWidgetConfig().getControlId() != -1) {
                ControllerManager.updateJoystickStatus(getWidgetConfig().id, true);
                setConditionSatisfy(true);
            }
        } else {
            ControllerManager.updateJoystickStatus(getWidgetConfig().id, false);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (ControllerManager.getBackgroundFlag()) {
            setConditionSatisfy(false);
            return;
        }
        if (!BluetoothHelper.isConnected()) {
            if (showBluetoothNotConnectDialog && ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_PREVIEW){
                showBluetoothNotConnectDialog = false;
                BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.BLUETOOTH_NOT_CONNECTED);
            }
            setConditionSatisfy(false);
            return;
        }
        if (Settings.isChargingProtection() && BluetoothHelper.isCharging()) {
            BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.CHARGING_PROTECTION);
            setConditionSatisfy(false);
            return;
        }
        if (ControllerModeHolder.getControllerMode() != ControllerModeHolder.MODE_EXECUTION && ControllerModeHolder.getControllerMode() != ControllerModeHolder.MODE_PREVIEW) {
            setConditionSatisfy(false);
            ControllerManager.updateJoystickStatus(getWidgetConfig().id, false);
            return;
        }
        if (getWidgetConfig().getControlId() == -1) {
            setConditionSatisfy(false);
            if (!showSetupDialog && ControllerModeHolder.getControllerMode() != ControllerModeHolder.MODE_PREVIEW) {
                showSetupDialog = true;
                BluetoothHelper.terminateExecution();
                BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.JOYSTICK_UNSET);
            }
            return;
        }
        ControllerManager.updateJoystickStatus(getWidgetConfig().id, true);
        setConditionSatisfy(true);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        setConditionSatisfy(false);
        resetProgress();
        ControllerManager.updateJoystickStatus(getWidgetConfig().id, false);
    }

    private void resetProgress() {
        int progress = slider.getMax() / 2;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            slider.setProgress(progress, slider.isDownPressed());
        } else {
            slider.setProgress(progress);
        }
    }

    @Override
    protected void onExecutionModeChanged(boolean isExecution) {
        int color = isExecution ? 0xFF555B67 : 0xCCFFFFFF;//UI颜色
        name.setTextColor(color);
        name.setHintTextColor(color);
        if (isExecution) {
            showSetupDialog = false;
        } else {
            slider.resetDownPressed();
        }
    }

    @Override
    public void setWidgetName(String widgetName) {
        Joystick2DirectionVConfig config = getWidgetConfig();
        config.setName(widgetName);
        setWidgetConfig(config);
    }

    @Override
    public String getWidgetName() {
        return getWidgetConfig().getName();
    }

    @Override
    protected Joystick2DirectionVConfig newConfig() {
        return new Joystick2DirectionVConfig();
    }

    @Override
    protected WidgetType getType() {
        return WidgetType.WIDGET_UKTWOWAYJOYSTICKV;
    }

    @Override
    protected void applyConfig(Joystick2DirectionVConfig config) {
        name.setText(config.getName());
    }

    @Override
    public void resetState() {
        setConditionSatisfy(false);
        resetProgress();
    }

    @Override
    protected int startExecuteV2() {
        if (ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_EDIT) {
            return -1;
        }
        if (!slider.isDownPressed()) {
            return -1;
        }
        if (Settings.isChargingProtection() && BluetoothHelper.isCharging()) {
            return -1;
        }
        int id = getWidgetConfig().getControlId();
        if (id == -1) {
            return -1;
        }
        int baseLine = slider.getMax() / 2;
        int progress = slider.getProgress();
        int direction = getWidgetConfig().getDirection();
        int delayMillis = getDelayMillis(baseLine, progress);
        if (getWidgetConfig().getControlType() == WidgetConfig.CONTROLTYPE_MOTOR) {
            sendMotorRunCmd(id, baseLine, progress, direction);
        } else {
            sendSteeringGearRunCmd(id, baseLine, progress, direction);
        }
        return delayMillis;
    }

    private int getDelayMillis(int baseLine, int progress) {
        int delayMillis;
        Long cacheTime = readCacheData("cacheTime");
        Integer cacheBaseLine = readCacheData("cacheBaseLine");
        Integer cacheProgress = readCacheData("cacheProgress");
        Integer cacheDelayMillis = readCacheData("cacheDelayMillis");
        if (cacheTime == null || cacheBaseLine == null || cacheProgress == null || cacheDelayMillis == null || (cacheTime - System.currentTimeMillis()) > 1000L) {
            delayMillis = NORMAL_DELAY_TIMEMS;
        } else {
            if (cacheBaseLine == baseLine && Math.abs(progress - cacheProgress) < MIN_MOVE_THRESHOLD) {
                delayMillis = Math.min(cacheDelayMillis + 50, MAX_DELAY_TIMEMS);
            } else {
                delayMillis = NORMAL_DELAY_TIMEMS;
            }
        }
        writeCacheData("cacheBaseLine", baseLine);
        writeCacheData("cacheProgress", progress);
        writeCacheData("cacheTime", System.currentTimeMillis());
        writeCacheData("cacheDelayMillis", delayMillis);
        return delayMillis;
    }

    @Override
    protected void stopExecute() {
//		if(!slider.isDownPressed()) {
//			return;
//		}
        int id = getWidgetConfig().getControlId();
        if (id == -1) {
            return;
        }
        if (getWidgetConfig().getControlType() == WidgetConfig.CONTROLTYPE_MOTOR) {
            sendMotorStopCmd(id);
        } else {
            sendSteeringGearStopCmd(id);
        }
    }

    private void sendSteeringGearRunCmd(int id, int baseLine, int progress, int direction) {
//		UKitCmdSteeringGearLoop.Mode mode;
        int speed = SteeringGear.Speed.VF.getValue() * Math.abs(progress - baseLine) / baseLine;
        if(progress < baseLine) {
            if(direction == Joystick2DirectionHConfig.CLOCKWISE) {
                speed *= 1;
            } else {
                speed *= -1;
            }
        } else {
            if(direction == Joystick2DirectionHConfig.CLOCKWISE) {
                speed *= -1;
            } else {
                speed *= 1;
            }
        }
        PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.SERVOS, id);
        String tag = getWidgetConfig().getId();
        BluetoothHelper.removeByTag(tag);
        BluetoothHelper.addCommand(BtInvocationFactory.rotateServos(new int[]{id}, speed), UKitCommandPriority.MIDDLE, tag);
    }

    private void sendMotorRunCmd(int id, int baseLine, int progress, int direction) {
        int speed = 140 * Math.abs(progress - baseLine) / baseLine;
		if(progress < baseLine) {
			if(direction == Joystick2DirectionHConfig.CLOCKWISE) {
				speed *= 1;
			} else {
				speed *= -1;
			}
		} else {
			if(direction == Joystick2DirectionHConfig.CLOCKWISE) {
				speed *= -1;
			} else {
				speed *= 1;
			}
		}
        PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.MOTOR, id);
        String tag = getWidgetConfig().getId();
        BluetoothHelper.removeByTag(tag);
        BluetoothHelper.addCommand(BtInvocationFactory.rotateMotors(new int[]{id}, speed), UKitCommandPriority.MIDDLE, tag);
    }

    private void sendSteeringGearStopCmd(int id) {
        String tag = getWidgetConfig().getId();
        BluetoothHelper.removeByTag(tag);
        BluetoothHelper.addCommand(BtInvocationFactory.stopServos(new int[]{id}), UKitCommandPriority.MIDDLE, tag);

    }

    private void sendMotorStopCmd(int id) {
        String tag = getWidgetConfig().getId();
        BluetoothHelper.removeByTag(tag);
        BluetoothHelper.addCommand(BtInvocationFactory.stopMotors(new int[]{id}), UKitCommandPriority.MIDDLE, tag);
    }

    public boolean isDownPressed() {
        return slider.isDownPressed();
    }
}
