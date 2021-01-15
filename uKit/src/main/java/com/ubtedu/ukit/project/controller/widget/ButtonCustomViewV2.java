package com.ubtedu.ukit.project.controller.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.project.controller.ControllerModeHolder;
import com.ubtedu.ukit.project.controller.manager.ControllerManager;
import com.ubtedu.ukit.project.controller.model.config.ButtonCustomConfigV2;
import com.ubtedu.ukit.project.controller.model.config.WidgetConfig;
import com.ubtedu.ukit.project.controller.model.define.WidgetType;
import com.ubtedu.ukit.project.controller.utils.ControllerConstValue;

/**
 * @Author naOKi
 * @Date 2018-11-29
 **/
public class ButtonCustomViewV2 extends ControllerWidgetView<ButtonCustomConfigV2> implements View.OnClickListener {

	private TextView name;
	private ImageView icon;
	private SquareViewLayout squareLayout;
	private ButtonBackgroundViewV2 bg;
	private ButtonCountdownViewV2 countdown;

	//private UKitDeviceActionSequence bas = null;

	public ButtonCustomViewV2(Context context) {
		this(context, null);
	}

	public ButtonCustomViewV2(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		initButtonCustomViewV2();
	}

	private void initButtonCustomViewV2() {
		View view = inflateView(R.layout.item_project_controller_button_custom_v2);
		bg = view.findViewById(R.id.item_project_controller_button_bg);
		bg.setBgColor(Color.WHITE);
		name = view.findViewById(R.id.item_project_controller_button_name);
		squareLayout = view.findViewById(R.id.item_project_controller_button_sv);
		icon = view.findViewById(R.id.item_project_controller_button_icon);
		countdown = view.findViewById(R.id.item_project_controller_button_countdown);
		squareLayout.setWidthAsHeight();
		addWidgetView(view);

		LayoutParams layoutParams=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_RIGHT,R.id.item_project_controller_button_layout);
		layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.item_project_controller_button_layout);
		layoutParams.bottomMargin=view.getPaddingBottom();
		layoutParams.rightMargin=view.getPaddingRight();

		setShowForeground(true);
		setForegroundExecutionModeLayoutParams(layoutParams);

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
		ButtonCustomConfigV2 config = getWidgetConfig();
		config.setName(widgetName);
		setWidgetConfig(config);
	}
	
	@Override
	public String getWidgetName() {
		return getWidgetConfig().getName();
	}
	
	@Override
	public void setWidgetIconIndex(int iconIndex) {
		ButtonCustomConfigV2 config = getWidgetConfig();
		config.setIconIndex(iconIndex);
		setWidgetConfig(config);
	}

	@Override
	public void setMotionId(String motionId) {
		ButtonCustomConfigV2 config = getWidgetConfig();
		config.setMode(WidgetConfig.MODE_EXECUTION_MOTION);
		config.setMotionId(motionId);
		setWidgetConfig(config);
	}

	@Override
	public String getMotionId() {
		ButtonCustomConfigV2 config = getWidgetConfig();
		return config.getMotionId();
	}

	@Override
	public void setBlockId(String blockId) {
		ButtonCustomConfigV2 config = getWidgetConfig();
		config.setMode(WidgetConfig.MODE_EXECUTION_BLOCKLY);
		config.setBlocklyId(blockId);
		setWidgetConfig(config);
	}

	@Override
	public String getBlockId() {
		ButtonCustomConfigV2 config = getWidgetConfig();
		return config.getBlocklyId();
	}

	@Override
	public int getWidgetIconIndex() {
		return getWidgetConfig().getIconIndex();
	}
	
	@Override
	protected ButtonCustomConfigV2 newConfig() {
		return new ButtonCustomConfigV2();
	}
	
	@Override
	protected WidgetType getType() {
		return WidgetType.WIDGET_UKCUSTOMBUTTON_V2;
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
	protected void applyConfig(ButtonCustomConfigV2 config) {
		if(config.getIconIndex() == -1) {
			icon.setImageResource(R.drawable.project_remote_sidebar_keys_add1_icon);
		} else {
			icon.setImageResource(ControllerConstValue.ICON_RES_ARRAY[config.getIconIndex()]);
		}
		name.setText(config.getName());
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