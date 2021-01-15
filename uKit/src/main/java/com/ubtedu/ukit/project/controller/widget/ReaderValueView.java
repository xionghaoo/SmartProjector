package com.ubtedu.ukit.project.controller.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.project.controller.model.config.ReaderValueConfig;
import com.ubtedu.ukit.project.controller.model.define.WidgetType;

import java.io.File;

/**
 * @Author naOKi
 * @Date 2018-11-29
 **/
public class ReaderValueView extends ControllerWidgetView<ReaderValueConfig> {
	
	private static final String FONT_PATH = "font" + File.separator + "BebasNeue-Regular.otf";
	private static final Typeface TYPEFACE_FONT;
	static {
		TYPEFACE_FONT = Typeface.createFromAsset(UKitApplication.getInstance().getAssets(), FONT_PATH);
	}

	private TextView name;
	private TextView display;

	public ReaderValueView(Context context) {
		this(context, null);
	}

	public ReaderValueView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		initReaderValueView();
	}

	private void initReaderValueView() {
		View view = inflateView(R.layout.item_project_controller_reader_value);
		name = view.findViewById(R.id.item_project_controller_reader_name);
		display = view.findViewById(R.id.item_project_controller_reader_display);
		display.setTypeface(TYPEFACE_FONT);
		addWidgetView(view);
		cleanValue();
	}

	public void cleanValue() {
		display.setText(null);
	}

	public void setValue(float value) {
		//String text = String.format("%.1f", value);
		//String text = String.valueOf(Math.round(value));
		String text = String.format("%4s", String.valueOf(Math.round(value)));
		//String text = String.format("%04d", Math.round(value));
		display.setText(text);
	}
	
	@Override
	public void setWidgetName(String widgetName) {
		ReaderValueConfig config = getWidgetConfig();
		config.setName(widgetName);
		setWidgetConfig(config);
	}
	
	@Override
	public String getWidgetName() {
		return getWidgetConfig().getName();
	}

	@Override
	protected void onExecutionModeChanged(boolean isExecution) {
		int color = isExecution ? 0xFF555B67 : 0xCCFFFFFF;//UI颜色
		name.setTextColor(color);
		name.setHintTextColor(color);
		cleanValue();
	}
	
	@Override
	protected ReaderValueConfig newConfig() {
		return new ReaderValueConfig();
	}
	
	@Override
	protected WidgetType getType() {
		return WidgetType.WIDGET_UKVALUEDISPLAY;
	}
	
	@Override
	protected void applyConfig(ReaderValueConfig config) {
		name.setText(config.getName());
	}
	
	@Override
	public void resetState() {
		cleanValue();
	}
	
	@Override
	protected void startExecute() {}

	@Override
	protected void stopExecute() {}

}
