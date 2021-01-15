package com.ubtedu.ukit.project.controller.widget;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.project.controller.model.config.ReaderColorConfig;
import com.ubtedu.ukit.project.controller.model.define.WidgetType;

import static com.ubtedu.ukit.project.controller.utils.ControllerConstValue.COLOR_READER_RADIUS;

/**
 * @Author naOKi
 * @Date 2018-11-29
 **/
public class ReaderColorView extends ControllerWidgetView<ReaderColorConfig> {

	private TextView name;
	private ImageView display;

	public ReaderColorView(Context context) {
		this(context, null);
	}

	public ReaderColorView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		initReaderColorView();
	}

	private void initReaderColorView() {
		View view = inflateView(R.layout.item_project_controller_reader_color);
		name = view.findViewById(R.id.item_project_controller_reader_name);
		display = view.findViewById(R.id.item_project_controller_reader_display);
		addWidgetView(view);
		//setColor(0xFFF100);
		setColor(0xFFFFFF);
	}

	public void setColor(int color) {
		GradientDrawable gradientDrawable = new GradientDrawable();
		gradientDrawable.setColor(color | 0xFF000000);
		gradientDrawable.setCornerRadius(COLOR_READER_RADIUS);
		display.setImageDrawable(gradientDrawable);
	}

	@Override
	protected void onExecutionModeChanged(boolean isExecution) {
		int color = isExecution ? 0xFF555B67 : 0xCCFFFFFF;//UI颜色
		name.setTextColor(color);
		name.setHintTextColor(color);
		setColor(0xFFFFFF);
	}
	
	@Override
	public void setWidgetName(String widgetName) {
		ReaderColorConfig config = getWidgetConfig();
		config.setName(widgetName);
		setWidgetConfig(config);
	}
	
	@Override
	public String getWidgetName() {
		return getWidgetConfig().getName();
	}
	
	@Override
	protected ReaderColorConfig newConfig() {
		return new ReaderColorConfig();
	}
	
	@Override
	protected WidgetType getType() {
		return WidgetType.WIDGET_UKCOLORDISPLAY;
	}
	
	@Override
	protected void applyConfig(ReaderColorConfig config) {
		name.setText(config.getName());
	}
	
	@Override
	public void resetState() {
		setColor(0xFFFFFF);
	}
	
	@Override
	protected void startExecute() {

	}

	@Override
	protected void stopExecute() {

	}

}
