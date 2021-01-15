package com.ubtedu.ukit.project.controller.widget;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.project.controller.ControllerModeHolder;
import com.ubtedu.ukit.project.controller.interfaces.IControllerModeChangeListener;
import com.ubtedu.ukit.project.controller.interfaces.IControllerWidgetListener;
import com.ubtedu.ukit.project.controller.manager.ControllerManager;
import com.ubtedu.ukit.project.controller.model.config.WidgetConfig;
import com.ubtedu.ukit.project.controller.model.define.WidgetType;
import com.ubtedu.ukit.project.controller.model.item.WidgetItem;

import java.util.HashMap;

import static com.ubtedu.ukit.project.controller.utils.ControllerConstValue.CELL_SIZE;

/**
 * @Author naOKi
 * @Date 2018-11-17
 **/
public abstract class ControllerWidgetView<T extends WidgetConfig> extends RelativeLayout implements Runnable, IControllerModeChangeListener {

	protected static final int MIN_MOVE_THRESHOLD = 5;

	protected static final int MIN_DELAY_TIMEMS = 100;
	protected static final int MAX_DELAY_TIMEMS = 500;
	protected static final int NORMAL_DELAY_TIMEMS = 250;

	private Point downPoint = null;
	private int touchStatus = MotionEvent.ACTION_CANCEL;

	private T widgetConfig;

	private IControllerWidgetListener widgetListener = null;

	private boolean isConditionSatisfy = false;
    private boolean showBackground = true;
    private boolean isSelected = false;
    private boolean isInvalid = false;
	private boolean showForeground = false;

	private long lastTapTimeMs = 0;
	private final static long TAP_THRESHOLD_TIME_MS = 100L;
	private final Object tapLock = new Object();

	private final HashMap<String, Object> cacheData = new HashMap<>();

	private final Object conditionSatisfyLock = new Object();

    private ImageView foregroundModeIv;
	private ImageView foregroundColorIv;
    private View foregroundLayout;
    private LayoutParams foregroundLayoutParams;
    private boolean adjustForegroundLayout = false;

	public ControllerWidgetView(Context context) {
		this(context, null);
	}

	public ControllerWidgetView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		if(isInEditMode()) {
			return;
		}
        widgetConfig = newConfig();
	}

	protected View inflateView(@LayoutRes int layoutRes) {
		return LayoutInflater.from(getContext()).inflate(layoutRes, null);
	}

	public final WidgetItem getWidgetItem() {
		return WidgetItem.findWidgetItemByWidgetType(getType());
	}

	public IControllerWidgetListener getWidgetTouchListener() {
		return widgetListener;
	}

	public void setWidgetTouchListener(IControllerWidgetListener touchListener) {
		this.widgetListener = touchListener;
	}

	protected boolean tryTap() {
		synchronized (tapLock) {
			long timeMs = System.currentTimeMillis();
			boolean result = timeMs - lastTapTimeMs > TAP_THRESHOLD_TIME_MS;
			if(result) {
				lastTapTimeMs = timeMs;
			}
			return result;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(ControllerManager.getBackgroundFlag()) {
			return super.onTouchEvent(event);
		}
		if(ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_EDIT) {
			if(event.getAction() == MotionEvent.ACTION_DOWN) {
				downPoint = new Point((int)event.getX(), (int)event.getY());
				touchStatus = MotionEvent.ACTION_DOWN;
			} else if(event.getAction() == MotionEvent.ACTION_UP) {
				if(touchStatus == MotionEvent.ACTION_DOWN) {
					if(widgetListener != null) {
						widgetListener.onWidgetClick(this);
					}
				}
				downPoint = null;
				touchStatus = MotionEvent.ACTION_UP;
			} else if(event.getAction() == MotionEvent.ACTION_MOVE) {
				if(touchStatus == MotionEvent.ACTION_DOWN && Math.pow(event.getX() - downPoint.x, 2) + Math.pow(event.getY() - downPoint.y, 2) >= Math.pow(MIN_MOVE_THRESHOLD, 2)) {
					touchStatus = MotionEvent.ACTION_MOVE;
					if(widgetListener != null) {
						widgetListener.onWidgetMove(this);
					}
				}
			} else {
				touchStatus = MotionEvent.ACTION_CANCEL;
			}
			return true;
		} else {
			return super.onTouchEvent(event);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(ControllerManager.getBackgroundFlag()) {
			return false;
		}
		if(ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_EDIT) {
			return true;
		}
		if(ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_EXECUTION) {
			return false;
		}
		return super.onInterceptTouchEvent(ev);
	}

	protected void addWidgetView(View view) {
		Size spanSize = WidgetItem.getSpanSizeByWidgetType(getType());
		LayoutParams layoutParams = new LayoutParams(spanSize.getWidth() * CELL_SIZE, spanSize.getHeight() * CELL_SIZE);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		view.setLayoutParams(layoutParams);
		addView(view);
		if(widgetConfig != null) {
		    applyConfig(widgetConfig);
        }
		initForegroundView();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Size spanSize = WidgetItem.getSpanSizeByWidgetType(getType());
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		int _cellWidth = width / spanSize.getWidth();
		int _cellHeight = height / spanSize.getHeight();
		int _cellSize = Math.min(_cellWidth, _cellHeight);
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if(child.getVisibility() != GONE && child.getId()!=R.id.controller_widget_foreground_layout) {
				LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
				layoutParams.width = spanSize.getWidth() * _cellSize;
				layoutParams.height = spanSize.getHeight() * _cellSize;
				child.setLayoutParams(layoutParams);
			}
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	protected void setConditionSatisfy(boolean isConditionSatisfy) {
		synchronized (conditionSatisfyLock) {
			if (this.isConditionSatisfy == isConditionSatisfy) {
				return;
			}
			this.isConditionSatisfy = isConditionSatisfy;
			removeCallbacks(this);
			if (isConditionSatisfy) {
				postDelayed(this, MIN_DELAY_TIMEMS);
			} else {
				post(this);
				postDelayed(this, NORMAL_DELAY_TIMEMS);
			}
		}
	}

	protected boolean isConditionSatisfy() {
		synchronized (conditionSatisfyLock) {
			return isConditionSatisfy;
		}
	}

	@Override
	public void run() {
		if(isConditionSatisfy()) {
			int delayMillis = startExecuteV2();
//			RcLogUtils.e("delayMillis: %d", delayMillis);
			if(delayMillis > 0) {
				postDelayed(this, delayMillis);
			} else {
				setConditionSatisfy(false);
			}
		} else {
			stopExecute();
		}
	}

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(showBackground) {
            ControllerModeHolder.addControllerModeChangeListener(this);
            updateBackgroundByState();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(showBackground) {
            ControllerModeHolder.removeControllerModeChangeListener(this);
        }
    }

	public boolean isShowBackground() {
		return showBackground;
	}

	public void setShowBackground(boolean showBackground) {
        this.showBackground = showBackground;
        if(showBackground) {
            ControllerModeHolder.addControllerModeChangeListener(this);
            updateBackgroundByState();
        } else {
            ControllerModeHolder.removeControllerModeChangeListener(this);
            setBackgroundColor(0);
            updateForegroundColor();
        }
    }
    private void initForegroundView() {
		foregroundLayout = LayoutInflater.from(getContext()).inflate(R.layout.view_controller_widget_foreground, null);
        foregroundModeIv = foregroundLayout.findViewById(R.id.controller_widget_foreground_mode_iv);
        foregroundColorIv = foregroundLayout.findViewById(R.id.controller_widget_foreground_color_iv);
        addView(foregroundLayout);
    }


	public boolean isShowForeground() {
		return showForeground;
	}

    public void setShowForeground(boolean showForeground) {
        this.showForeground = showForeground;
        if (showBackground) {
            updateForeground();
        } else {
            foregroundModeIv.setImageDrawable(getContext().getDrawable(R.drawable.drawable_null));
            foregroundColorIv.setImageDrawable(getContext().getDrawable(R.drawable.drawable_null));
        }
    }
    protected void updateForeground() {
        if (!showForeground) {
            return;
        }
        if (getWidgetConfig() != null && getWidgetConfig().mode != null) {
            if (getWidgetConfig().mode == WidgetConfig.MODE_EXECUTION_BLOCKLY) {
                foregroundModeIv.setImageResource(R.drawable.project_remote_blockly_icon);
            } else if (getWidgetConfig().mode == WidgetConfig.MODE_EXECUTION_MOTION) {
                foregroundModeIv.setImageResource(R.drawable.project_remote_act_icon);
            } else {
                foregroundModeIv.setImageDrawable(getContext().getDrawable(R.drawable.drawable_null));
            }
        } else {
            foregroundModeIv.setImageDrawable(getContext().getDrawable(R.drawable.drawable_null));
        }
        updateForegroundColor();
    }

    protected void updateForegroundColor() {
        if (!showForeground) {
            return;
        }
        if (getWidgetConfig() != null && getWidgetConfig().mode != null && (getWidgetConfig().mode == WidgetConfig.MODE_EXECUTION_BLOCKLY || getWidgetConfig().mode == WidgetConfig.MODE_EXECUTION_MOTION)) {
            if (ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_EXECUTION) {
                foregroundColorIv.setImageResource(R.drawable.project_remote_icon_bg1);
            } else if (isSelected) {
                foregroundColorIv.setImageResource(R.drawable.project_remote_icon_bg3);
            } else {
                foregroundColorIv.setImageResource(R.drawable.project_remote_icon_bg2);
            }
            if (adjustForegroundLayout) {
                adjustForegroundLayoutParams();
            }
        } else {
            foregroundColorIv.setImageDrawable(getContext().getDrawable(R.drawable.drawable_null));
        }
    }

	public void setForegroundExecutionModeLayoutParams(LayoutParams layoutParams) {
        adjustForegroundLayout = true;
		foregroundLayoutParams = layoutParams;
	}

    private void adjustForegroundLayoutParams(){
		if (ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_EXECUTION){
            foregroundLayout.setLayoutParams(foregroundLayoutParams);
        }else{
            foregroundLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
	}

	public boolean isSelected() {
		return isSelected;
	}

    public void toggleSelectedFlag() {
        setSelectedFlag(!isSelected);
    }

    public void setSelectedFlag(boolean selected) {
	    if(isSelected == selected) {
	        return;
        }
        isSelected = selected;
        updateBackgroundByState();
    }

	public boolean isInvalid() {
		return isInvalid;
	}

	public void setInvalidFlag(boolean invalid) {
        if(isInvalid == invalid) {
            return;
        }
        isInvalid = invalid;
        updateBackgroundByState();
    }

    @Override
    public void onControllerModeChanged(int controllerMode) {
        updateBackgroundByState();
		onExecutionModeChanged(controllerMode == ControllerModeHolder.MODE_EXECUTION);
    }

    protected void onExecutionModeChanged(boolean isExecution) {}

	protected void updateBackgroundByState() {
	    if(!showBackground) {
	        return;
        }
        if(ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_EXECUTION) {
	        int bgResId = getType().getBgResId();
            if(bgResId > 0) {
                setBackgroundResource(bgResId);
            } else {
                setBackgroundColor(0);
            }
        } else if(isSelected) {
            setBackgroundResource(R.drawable.layer_controller_widget_select_bg);
        } else if(isInvalid) {
            setBackgroundResource(R.drawable.layer_controller_widget_error_bg);
        } else {
            setBackgroundResource(R.drawable.layer_controller_widget_edit_bg);
        }

        updateForegroundColor();
    }

	public void resetState() {}

	public T getWidgetConfig() {
		return widgetConfig;
	}

	public void setWidgetConfig(T config) {
		setWidgetConfig(config, true);
		updateForeground();
	}

	protected void setWidgetConfig(T config, boolean apply) {
		if(config == null) {
			return;
		}
		widgetConfig = config;
		if(apply) {
			applyConfig(config);
		}
	}

	public void setWidgetName(String widgetName) {}

	public String getWidgetName() {
		return null;
	}

	public void setWidgetIconIndex(int iconIndex) {}

	public int getWidgetIconIndex() {
		return -1;
	}

	public void setValueRange(int minValue, int maxValue) {}

	public int getMaxRange() {
		return Integer.MAX_VALUE;
	}

	public int getMinRange() {
		return Integer.MIN_VALUE;
	}

	public String getMotionId() {
		return null;
	}

	public void setMotionId(String motionId) {}

	public String getBlockId() {
		return null;
	}

	public void setBlockId(String blockId) {}

	public ImageView getForegroundModeIv() {
		return foregroundModeIv;
	}

	public ImageView getForegroundColorIv() {
		return foregroundColorIv;
	}

	protected int startExecuteV2() {
		startExecute();
		return 250;
	}

	protected <T> void writeCacheData(String key, T value) {
		synchronized (cacheData) {
			cacheData.put(key, value);
		}
	}

	protected <T> T readCacheData(String key) {
		synchronized (cacheData) {
			try {
				return (T)cacheData.get(key);
			} catch (Exception e) {
				return null;
			}
		}
	}

	protected abstract T newConfig();
	protected abstract void applyConfig(T config);
	protected void startExecute() {}
	protected abstract void stopExecute();
	protected abstract WidgetType getType();

}
