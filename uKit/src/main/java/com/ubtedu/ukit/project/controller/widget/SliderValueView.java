package com.ubtedu.ukit.project.controller.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.project.controller.ControllerModeHolder;
import com.ubtedu.ukit.project.controller.manager.ControllerManager;
import com.ubtedu.ukit.project.controller.model.config.SliderValueConfig;
import com.ubtedu.ukit.project.controller.model.define.WidgetType;

/**
 * @Author naOKi
 * @Date 2018-11-29
 **/
public class SliderValueView extends ControllerWidgetView<SliderValueConfig> implements SeekBar.OnSeekBarChangeListener {

    private TextView name;
    private TextView value;
    private ControllerSeekBar slider;

    int base;

    public SliderValueView(Context context) {
        this(context, null);
    }

    public SliderValueView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initSliderValueView();
    }

    private void initSliderValueView() {
        View view = inflateView(R.layout.item_project_controller_slider_value);
        name = view.findViewById(R.id.item_project_controller_slider_name);
        value = view.findViewById(R.id.item_project_controller_slider_value);
        slider = view.findViewById(R.id.item_project_controller_slider_slider);
        addWidgetView(view);
        setShowForeground(true);
        slider.setOnSeekBarChangeListener(this);
        SliderValueConfig config = getWidgetConfig();
        int max = config.getMaxValue() - config.getMinValue();
        int progress = max / 2;
        base = config.getMinValue();
        slider.setMax(max);
        slider.setProgress(progress);
        value.setText(String.valueOf(progress + base));
    }

    @Override
    protected void onExecutionModeChanged(boolean isExecution) {
        int color = isExecution ? 0xFF555B67 : 0xCCFFFFFF;//UI颜色
        name.setTextColor(color);
        name.setHintTextColor(color);
        value.setTextColor(color);
        value.setHintTextColor(color);
        resetProgress();
        if (isExecution) {
            SliderValueConfig config = getWidgetConfig();
            int max = config.getMaxValue() - config.getMinValue();
            int progress = max / 2;
            ControllerManager.updateDefaultSliderStatus(config.id, progress + base);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        value.setText(String.valueOf(progress + base));
        if (fromUser) {
            ControllerManager.updateSliderStatus(getWidgetConfig().id, progress + base);
            setConditionSatisfy(true);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (ControllerManager.getBackgroundFlag()) {
            setConditionSatisfy(false);
            return;
        }
        setConditionSatisfy(true);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        setConditionSatisfy(false);
    }

    private void resetProgress() {
        slider.setProgress(slider.getMax() / 2);
    }

    @Override
    public void setWidgetName(String widgetName) {
        SliderValueConfig config = getWidgetConfig();
        config.setName(widgetName);
        setWidgetConfig(config);
    }

    @Override
    public String getBlockId() {
        return getWidgetConfig().getBlocklyId();
    }

    @Override
    public void setBlockId(String blockId) {
        SliderValueConfig config = getWidgetConfig();
        config.setBlocklyId(blockId);
        setWidgetConfig(config);
    }

    @Override
    public String getWidgetName() {
        return getWidgetConfig().getName();
    }

    @Override
    public void setValueRange(int minValue, int maxValue) {
        SliderValueConfig config = getWidgetConfig();
        config.setMaxValue(maxValue);
        config.setMinValue(minValue);
        setWidgetConfig(config);
    }

    @Override
    public int getMaxRange() {
        return getWidgetConfig().getMaxValue();
    }

    @Override
    public int getMinRange() {
        return getWidgetConfig().getMinValue();
    }

    @Override
    protected SliderValueConfig newConfig() {
        return new SliderValueConfig();
    }

    @Override
    protected WidgetType getType() {
        return WidgetType.WIDGET_UKINTSLIDER;
    }

    @Override
    protected void applyConfig(SliderValueConfig config) {
        name.setText(config.getName());
        int max = config.getMaxValue() - config.getMinValue();
        int progress = max / 2;
        base = config.getMinValue();
        slider.setMax(max);
        slider.setProgress(progress);
        value.setText(String.valueOf(progress + base));
    }

    @Override
    public void resetState() {
        resetProgress();
        SliderValueConfig config = getWidgetConfig();
        int max = config.getMaxValue() - config.getMinValue();
        int progress = max / 2;
        ControllerManager.updateDefaultSliderStatus(config.id, progress + base);
    }

    @Override
    protected void startExecute() {

    }

    @Override
    protected void stopExecute() {

    }

    @Override
    protected void updateForeground() {
        if (!isShowForeground()) {
            return;
        }
        if (!TextUtils.isEmpty(getBlockId())) {
            getForegroundModeIv().setImageResource(R.drawable.project_remote_blockly_icon);
        } else {
            getForegroundModeIv().setImageDrawable(getContext().getDrawable(R.drawable.drawable_null));
        }
        updateForegroundColor();
    }

    @Override
    protected void updateForegroundColor() {
        if (!isShowForeground()) {
            return;
        }
        if (!TextUtils.isEmpty(getBlockId())) {
            if (ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_EXECUTION) {
                getForegroundColorIv().setImageResource(R.drawable.project_remote_icon_bg1);
            } else if (isSelected()) {
                getForegroundColorIv().setImageResource(R.drawable.project_remote_icon_bg3);
            } else {
                getForegroundColorIv().setImageResource(R.drawable.project_remote_icon_bg2);
            }
        } else {
            getForegroundColorIv().setImageDrawable(getContext().getDrawable(R.drawable.drawable_null));
        }
    }
}
