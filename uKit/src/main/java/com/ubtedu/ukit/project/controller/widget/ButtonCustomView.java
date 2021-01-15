package com.ubtedu.ukit.project.controller.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.project.controller.ControllerModeHolder;
import com.ubtedu.ukit.project.controller.manager.ControllerManager;
import com.ubtedu.ukit.project.controller.model.config.ButtonCustomConfig;
import com.ubtedu.ukit.project.controller.model.config.WidgetConfig;
import com.ubtedu.ukit.project.controller.model.define.WidgetType;
import com.ubtedu.ukit.project.controller.utils.ControllerConstValue;

/**
 * @Author naOKi
 * @Date 2018-11-29
 **/
public class ButtonCustomView extends ControllerWidgetView<ButtonCustomConfig> implements View.OnClickListener {

	private ImageView icon;
	private ButtonBackgroundView bg;
	private ButtonCountdownView countdown;
	
	//private UKitDeviceActionSequence bas = null;

	public ButtonCustomView(Context context) {
		this(context, null);
	}

	public ButtonCustomView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		initButtonCustomView();
	}

	private void initButtonCustomView() {
		View view = inflateView(R.layout.item_project_controller_button_custom);
		bg = view.findViewById(R.id.item_project_controller_button_bg);
		bg.setBgColor(Color.WHITE);
		icon = view.findViewById(R.id.item_project_controller_button_icon);
		countdown = view.findViewById(R.id.item_project_controller_button_countdown);
		addWidgetView(view);
		setShowForeground(true);
		setOnClickListener(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(ControllerManager.getBackgroundFlag()) {
			return super.onTouchEvent(event);
		}
		if(ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_EXECUTION) {
			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				if(tryTap()) {
					ControllerManager.updateButtonStatus(getWidgetConfig().id, true);
				}
			} else if (MotionEvent.ACTION_UP == event.getAction()) {
				ControllerManager.updateButtonStatus(getWidgetConfig().id, false);
			}
			return super.onTouchEvent(event);
		} else {
			return super.onTouchEvent(event);
		}
	}

	@Override
	public void onClick(View v) {
		startExecute();
	}

	@Override
	protected void onExecutionModeChanged(boolean isExecution) {
		stopExecute();
	}
	
	@Override
	public void setWidgetName(String widgetName) {
		ButtonCustomConfig config = getWidgetConfig();
		config.setName(widgetName);
		setWidgetConfig(config, false);
	}
	
	@Override
	public String getWidgetName() {
		return getWidgetConfig().getName();
	}
	
	@Override
	public void setWidgetIconIndex(int iconIndex) {
		ButtonCustomConfig config = getWidgetConfig();
		config.setIconIndex(iconIndex);
		setWidgetConfig(config);
	}

	@Override
	public void setMotionId(String motionId) {
		ButtonCustomConfig config = getWidgetConfig();
		config.setMode(WidgetConfig.MODE_EXECUTION_MOTION);
		config.setMotionId(motionId);
		setWidgetConfig(config);
	}

	@Override
	public String getMotionId() {
		ButtonCustomConfig config = getWidgetConfig();
		return config.getMotionId();
	}

	@Override
	public void setBlockId(String blockId) {
		ButtonCustomConfig config = getWidgetConfig();
		config.setMode(WidgetConfig.MODE_EXECUTION_BLOCKLY);
		config.setBlocklyId(blockId);
		setWidgetConfig(config);
	}

	@Override
	public String getBlockId() {
		ButtonCustomConfig config = getWidgetConfig();
		return config.getBlocklyId();
	}

	@Override
	public int getWidgetIconIndex() {
		return getWidgetConfig().getIconIndex();
	}
	
	@Override
	protected ButtonCustomConfig newConfig() {
		return new ButtonCustomConfig();
	}
	
	@Override
	protected WidgetType getType() {
		return WidgetType.WIDGET_UKCUSTOMBUTTON;
	}
	
	public void startAnimator(long timeMs) {
		if(countdown != null) {
			countdown.setCountDownTime(timeMs);
			countdown.startAnimator();
		}
	}
	
	public void stopAnimator() {
		if(countdown != null) {
			countdown.stopAnimator();
		}
	}
	
	@Override
	protected void applyConfig(ButtonCustomConfig config) {
		if(config.getIconIndex() == -1) {
			icon.setImageResource(R.drawable.project_remote_sidebar_keys_add1_icon);
		} else {
			icon.setImageResource(ControllerConstValue.ICON_RES_ARRAY[config.getIconIndex()]);
		}
	}
	
	@Override
	public void resetState() {
		//setConditionSatisfy(false);
		stopAnimator();
	}
	
	@Override
	protected void startExecute() {
	}

	@Override
	protected void stopExecute() {
	}

}