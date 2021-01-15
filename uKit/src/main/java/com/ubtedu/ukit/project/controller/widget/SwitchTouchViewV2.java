package com.ubtedu.ukit.project.controller.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.project.controller.manager.ControllerManager;
import com.ubtedu.ukit.project.controller.model.config.SwitchTouchConfigV2;
import com.ubtedu.ukit.project.controller.model.config.WidgetConfig;
import com.ubtedu.ukit.project.controller.model.define.WidgetType;

/**
 * @Author naOKi
 * @Date 2018-11-29
 **/
public class SwitchTouchViewV2 extends ControllerWidgetView<SwitchTouchConfigV2> implements Checkable, View.OnClickListener {

	private TextView name;
	private ImageView icon;
	private boolean isChecked = false;

	public SwitchTouchViewV2(Context context) {
		this(context, null);
	}

	public SwitchTouchViewV2(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		initSwitchTouchViewV2();
	}

	private void initSwitchTouchViewV2() {
		View view = inflateView(R.layout.item_project_controller_switch_touch_v2);
		name = view.findViewById(R.id.item_project_controller_switch_name);
		icon = view.findViewById(R.id.item_project_controller_switch_icon);
		addWidgetView(view);
		setShowForeground(true);
		icon.setOnClickListener(this);
	}

	@Override
	protected void onExecutionModeChanged(boolean isExecution) {
		int color = isExecution ? 0xFF555B67 : 0xCCFFFFFF;//UI颜色
		name.setTextColor(color);
		name.setHintTextColor(color);
		setChecked(false, false);
	}

	@Override
	public void onClick(View v) {
		if(ControllerManager.getBackgroundFlag()) {
			return;
		}
		if(tryTap()) {
			toggle();
		}
	}

	@Override
	public void setChecked(boolean checked) {
		setChecked(checked, true);
	}
	
	private void setChecked(boolean checked, boolean notify) {
		if(isChecked == checked) {
			return;
		}
		isChecked = checked;
		icon.setImageResource(isChecked ? R.drawable.project_remote_sidebar_switch_b_close : R.drawable.project_remote_sidebar_switch_b_open);
		if(notify) {
			ControllerManager.updateSwitchStatus(getWidgetConfig().id, checked);
		}
	}

	@Override
	public boolean isChecked() {
		return isChecked;
	}

	@Override
	public void toggle() {
		setChecked(!isChecked);
	}
	
	@Override
	public String getBlockId() {
		return getWidgetConfig().getBlocklyId();
	}
	
	@Override
	public void setBlockId(String blockId) {
		SwitchTouchConfigV2 config = getWidgetConfig();
		config.setMode(WidgetConfig.MODE_EXECUTION_BLOCKLY);
		config.setBlocklyId(blockId);
		setWidgetConfig(config);
	}

	@Override
	public void setMotionId(String motionId) {
		SwitchTouchConfigV2 config = getWidgetConfig();
		config.setMode(WidgetConfig.MODE_EXECUTION_MOTION);
		config.setMotionId(motionId);
		setWidgetConfig(config);
	}

	@Override
	public String getMotionId() {
		SwitchTouchConfigV2 config = getWidgetConfig();
		return config.getMotionId();
	}
	
	@Override
	public void setWidgetName(String widgetName) {
		SwitchTouchConfigV2 config = getWidgetConfig();
		config.setName(widgetName);
		setWidgetConfig(config);
	}
	
	@Override
	public String getWidgetName() {
		return getWidgetConfig().getName();
	}
	
	@Override
	protected SwitchTouchConfigV2 newConfig() {
		return new SwitchTouchConfigV2();
	}
	
	@Override
	protected WidgetType getType() {
		return WidgetType.WIDGET_UKTOUCHSWITCH_V2;
	}
	
	@Override
	protected void applyConfig(SwitchTouchConfigV2 config) {
		if(config == null) {
			name.setText(null);
			return;
		}
		name.setText(config.getName());
	}
	
	@Override
	public void resetState() {
		setChecked(false, false);
	}
	
	@Override
	protected void startExecute() {

	}

	@Override
	protected void stopExecute() {

	}

}
