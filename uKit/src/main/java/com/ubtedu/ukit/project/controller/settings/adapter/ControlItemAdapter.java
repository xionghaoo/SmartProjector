package com.ubtedu.ukit.project.controller.settings.adapter;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.project.controller.interfaces.IControllerItemClickListener;
import com.ubtedu.ukit.project.controller.model.config.Joystick2DirectionHConfig;
import com.ubtedu.ukit.project.controller.model.config.Joystick4DirectionConfig;
import com.ubtedu.ukit.project.controller.utils.ControllerConstValue;
import com.ubtedu.ukit.project.controller.widget.MotorView;
import com.ubtedu.ukit.project.controller.widget.SquareViewLayout;
import com.ubtedu.ukit.project.controller.widget.SteeringGearView;
import com.ubtedu.ukit.project.vo.ModelInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @Author naOKi
 * @Date 2018-12-04
 **/
public class ControlItemAdapter extends RecyclerView.Adapter<ControlItemAdapter.SettingItemViewHolder> {
	
	private ModelInfo modelInfo = null;
	
	private int[] selectedIds = {-1, -1, -1, -1};
	private int selectedPosition = -1;
	
	private int workMode = SteeringGearView.MODE_ALL;
	private ArrayList<Integer> list = new ArrayList<>();
	private ArrayList<Integer> angularList = new ArrayList<>();
	private ArrayList<Integer> wheelList = new ArrayList<>();

	private ArrayList<Integer> shakeList = new ArrayList<>();
	
	public static final int JOYSTICK_MODE_2DIRECTION = 1;
	public static final int JOYSTICK_MODE_4DIRECTION = 2;

	public static final int JOYSTICK_TYPE_STEERINGGEAR = 1;
	public static final int JOYSTICK_TYPE_MOTOR = 2;
	
	private int joystickMode = -1;
	private int joystickType = -1;
	
	private int rotateDirection = Joystick2DirectionHConfig.CLOCKWISE;
	
	private int wheelMode = Joystick4DirectionConfig.MODE_TWO_WHEEL_DRIVE;
	
	private IControllerItemClickListener itemClickListener;
	
	@NonNull
	@Override
	public SettingItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
		SquareViewLayout itemView;
		if(viewType == JOYSTICK_TYPE_MOTOR) {
			itemView = (SquareViewLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_peripheral_setting_motor, viewGroup, false);
		} else {
			itemView = (SquareViewLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_peripheral_setting_steering_gear, viewGroup, false);
		}
		itemView.setHeightAsWidth();
		SettingItemViewHolder vh = new SettingItemViewHolder(itemView);
		return vh;
	}
	
	public void setItemClickListener(IControllerItemClickListener itemClickListener) {
		this.itemClickListener = itemClickListener;
	}
	
	public IControllerItemClickListener getItemClickListener() {
		return itemClickListener;
	}

	public void setModelInfo(ModelInfo modelInfo) {
		if(modelInfo == null || modelInfo.equals(this.modelInfo)) {
			return;
		}
		this.modelInfo = ModelInfo.newInstance(modelInfo);
		list.clear();
		angularList.clear();
		wheelList.clear();
		if(JOYSTICK_TYPE_MOTOR == joystickType) {
			if (modelInfo.motor != null) {
				list.addAll(modelInfo.motor);
			}
			Collections.sort(list);
		} else {
			if (workMode == SteeringGearView.MODE_ALL) {
				if (modelInfo.steeringGear != null) {
					list.addAll(modelInfo.steeringGear);
				}
			} else if (workMode == SteeringGearView.MODE_ANGULAR) {
				if (modelInfo.steeringGearAngular != null) {
					list.addAll(modelInfo.steeringGearAngular);
				}
			} else {
				if (modelInfo.steeringGearWheel != null) {
					list.addAll(modelInfo.steeringGearWheel);
				}
			}
			Collections.sort(list);
			if (modelInfo.steeringGearAngular != null) {
				angularList.addAll(modelInfo.steeringGearAngular);
			}
			if (modelInfo.steeringGearWheel != null) {
				wheelList.addAll(modelInfo.steeringGearWheel);
			}
		}
		notifyDataSetChanged();
	}
	
	public ModelInfo getModelInfo() {
		return modelInfo;
	}
	
	public void setWorkMode(int workMode) {
		if(JOYSTICK_TYPE_MOTOR == joystickType) {
			return;
		}
		if(SteeringGearView.MODE_ALL != workMode
				&& SteeringGearView.MODE_ANGULAR != workMode
				&& SteeringGearView.MODE_WHEEL != workMode) {
			return;
		}
		if(this.workMode == workMode) {
			return;
		}
		this.workMode = workMode;
		if(modelInfo != null) {
			list.clear();
			angularList.clear();
			wheelList.clear();
			if (workMode == SteeringGearView.MODE_ALL) {
				if (modelInfo.steeringGear != null) {
					list.addAll(modelInfo.steeringGear);
				}
			} else if (workMode == SteeringGearView.MODE_ANGULAR) {
				if (modelInfo.steeringGearAngular != null) {
					list.addAll(modelInfo.steeringGearAngular);
				}
			} else {
				if (modelInfo.steeringGearWheel != null) {
					list.addAll(modelInfo.steeringGearWheel);
				}
			}
			Collections.sort(list);
			if (modelInfo.steeringGearAngular != null) {
				angularList.addAll(modelInfo.steeringGearAngular);
			}
			if (modelInfo.steeringGearWheel != null) {
				wheelList.addAll(modelInfo.steeringGearWheel);
			}
		}
		notifyDataSetChanged();
	}
	
	public int getWorkMode() {
		return workMode;
	}
	
	public void setWheelMode(int wheelMode) {
		if(Joystick4DirectionConfig.MODE_TWO_WHEEL_DRIVE != wheelMode
			   && Joystick4DirectionConfig.MODE_FOUR_WHEEL_DRIVE != wheelMode) {
			return;
		}
		if(this.wheelMode == wheelMode) {
			return;
		}
		this.wheelMode = wheelMode;
		if(JOYSTICK_MODE_4DIRECTION != joystickMode) {
			return;
		}
		notifyDataSetChanged();
	}
	
	public int getWheelMode() {
		return wheelMode;
	}
	
	public void setRotateDirection(int rotateDirection) {
		if(Joystick2DirectionHConfig.CLOCKWISE != rotateDirection
			   && Joystick2DirectionHConfig.COUNTERCLOCKWISE != rotateDirection) {
			return;
		}
		if(this.rotateDirection == rotateDirection) {
			return;
		}
		this.rotateDirection = rotateDirection;
		if(JOYSTICK_MODE_2DIRECTION != joystickMode) {
			return;
		}
		notifyDataSetChanged();
	}
	
	public int getRotateDirection() {
		return rotateDirection;
	}
	
	public void setJoystickMode(int mode) {
		if(JOYSTICK_MODE_2DIRECTION != mode && JOYSTICK_MODE_4DIRECTION != mode) {
			return;
		}
		if(this.joystickMode == mode) {
			return;
		}
		this.joystickMode = mode;
		selectedIds = new int[]{-1, -1, -1, -1};
		selectedPosition = -1;
		notifyDataSetChanged();
	}
	
	public int getJoystickMode() {
		return joystickMode;
	}

	public void setJoystickType(int joystickType) {
		if(JOYSTICK_TYPE_STEERINGGEAR != joystickType && JOYSTICK_TYPE_MOTOR != joystickType) {
			return;
		}
		if(this.joystickType == joystickType) {
			return;
		}
		this.joystickType = joystickType;
		selectedIds = new int[]{-1, -1, -1, -1};
//		selectedPosition = -1;
		list.clear();
		angularList.clear();
		wheelList.clear();
		if(modelInfo != null) {
			if (JOYSTICK_TYPE_MOTOR == joystickType) {
				if (modelInfo.motor != null) {
					list.addAll(modelInfo.motor);
				}
			} else {
				if (modelInfo.steeringGear != null) {
					list.addAll(modelInfo.steeringGear);
				}
				if (modelInfo.steeringGearAngular != null) {
					angularList.addAll(modelInfo.steeringGearAngular);
				}
				if (modelInfo.steeringGearWheel != null) {
					wheelList.addAll(modelInfo.steeringGearWheel);
				}
			}
			Collections.sort(list);
		}
		notifyDataSetChanged();
	}

	public int getJoystickType() {
		return joystickType;
	}
	
	public void setSelectedPosition(int selectedPosition) {
		if(-1 == joystickMode || selectedPosition < 0) {
			return;
		}
		if((JOYSTICK_MODE_2DIRECTION == joystickMode && selectedPosition >= 1)
			   || JOYSTICK_MODE_4DIRECTION == joystickMode
			   && ((Joystick4DirectionConfig.MODE_TWO_WHEEL_DRIVE == wheelMode && selectedPosition >= 2)
			   || (Joystick4DirectionConfig.MODE_FOUR_WHEEL_DRIVE == wheelMode && selectedPosition >= 4))) {
			return;
		}
		if(this.selectedPosition == selectedPosition) {
			return;
		}
		this.selectedPosition = selectedPosition;
		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(int position) {
		int viewType = getJoystickType();
		return viewType == -1 ?  JOYSTICK_TYPE_STEERINGGEAR : viewType;
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}
	
	public void setSelectedItem(int index, int id) {
		if(-1 == joystickMode || index < 0) {
			return;
		}
		if(id < 1 || id > 32) {
			id = -1;
		}
		if((JOYSTICK_MODE_2DIRECTION == joystickMode && index >= 1)
			   || JOYSTICK_MODE_4DIRECTION == joystickMode
			   && ((Joystick4DirectionConfig.MODE_TWO_WHEEL_DRIVE == wheelMode && index >= 2)
			   || (Joystick4DirectionConfig.MODE_FOUR_WHEEL_DRIVE == wheelMode && index >= 4))) {
			return;
		}
		this.selectedIds[index] = id;
		notifyDataSetChanged();
	}

	public boolean hasSelectedItem() {
		return !Arrays.equals(selectedIds, new int[]{-1, -1, -1, -1});
	}
	
	public int getSelectedItem(int index) {
		if(-1 == joystickMode || index < 0) {
			return -1;
		}
		if((JOYSTICK_MODE_2DIRECTION == joystickMode && index >= 1)
			   || JOYSTICK_MODE_4DIRECTION == joystickMode
			   && ((Joystick4DirectionConfig.MODE_TWO_WHEEL_DRIVE == wheelMode && index >= 2)
			   || (Joystick4DirectionConfig.MODE_FOUR_WHEEL_DRIVE == wheelMode && index >= 4))) {
			return -1;
		}
		return selectedIds[index];
	}

	public boolean isSelectedItem() {
		if(selectedPosition == -1) {
			return false;
		}
		return selectedIds[selectedPosition] != -1;
	}

	public boolean isSelectedItemAngularMode() {
		if(selectedPosition == -1) {
			return false;
		}
		int id = selectedIds[selectedPosition];
		return id != -1 && isAngularMode(id);
	}

	public boolean isAngularMode(int id) {
		return angularList.contains(id);
	}
	
	private int[] get2DirectionMaskRes(int id, boolean isSelected, boolean isAngularMode) {
		int[] result = {-1, -1, -1, -1};
		if(!isSelected) {
			return result;
		}
		if(rotateDirection == Joystick2DirectionHConfig.CLOCKWISE) {
			result[3] = isAngularMode ? R.drawable.popup_servo_red_tips_clockwise : R.drawable.popup_servo_green_tips_clockwise;
		} else {
			result[3] = isAngularMode ? R.drawable.popup_servo_red_tips_anticlockwise : R.drawable.popup_servo_green_tips_anticlockwise;
		}
		return result;
	}
	
	private int[] get2WheelMaskRes(int id, boolean isSelected, boolean isAngularMode) {
		int[] result = {-1, -1, -1, -1};
		if(selectedIds[0] == id) {
			if(selectedPosition == Joystick4DirectionConfig.LEFT_ID) {
				result[2] = isAngularMode ? R.drawable.popup_servo_red_tips_l : R.drawable.popup_servo_green_tips_l;
			} else {
				result[2] = R.drawable.popup_servo_grey_tips_l;
			}
		}
		if(selectedIds[1] == id) {
			if(selectedPosition == Joystick4DirectionConfig.RIGHT_ID) {
				result[3] = isAngularMode ? R.drawable.popup_servo_red_tips_r : R.drawable.popup_servo_green_tips_r;
			} else {
				result[3] = R.drawable.popup_servo_grey_tips_r;
			}
		}
		return result;
	}
	
	private int[] get4WheelMaskRes(int id, boolean isSelected, boolean isAngularMode) {
		int[] result = {-1, -1, -1, -1};
		if(selectedIds[0] == id) {
			if(selectedPosition == Joystick4DirectionConfig.LEFT_FRONT_ID) {
				result[0] = isAngularMode ? R.drawable.popup_servo_red_tips_rl : R.drawable.popup_servo_green_tips_fl;
			} else {
				result[0] = R.drawable.popup_servo_grey_tips_fl;
			}
		}
		if(selectedIds[1] == id) {
			if(selectedPosition == Joystick4DirectionConfig.RIGHT_FRONT_ID) {
				result[1] = isAngularMode ? R.drawable.popup_servo_red_tips_fr : R.drawable.popup_servo_green_tips_fr;
			} else {
				result[1] = R.drawable.popup_servo_grey_tips_fr;
			}
		}
		if(selectedIds[2] == id) {
			if(selectedPosition == Joystick4DirectionConfig.LEFT_BEHIND_ID) {
				result[2] = isAngularMode ? R.drawable.popup_servo_red_tips_bl : R.drawable.popup_servo_green_tips_bl;
			} else {
				result[2] = R.drawable.popup_servo_grey_tips_bl;
			}
		}
		if(selectedIds[3] == id) {
			if(selectedPosition == Joystick4DirectionConfig.RIGHT_BEHIND_ID) {
				result[3] = isAngularMode ? R.drawable.popup_servo_red_tips_br : R.drawable.popup_servo_green_tips_br;
			} else {
				result[3] = R.drawable.popup_servo_grey_tips_br;
			}
		}
		return result;
	}
	
	private int[] getMaskRes(int id, boolean isSelected, boolean isAngularMode) {
		if(joystickMode == -1) {
			return new int[]{-1, -1, -1, -1};
		} else if(joystickMode == JOYSTICK_MODE_2DIRECTION) {
			return get2DirectionMaskRes(id, isSelected, isAngularMode);
		} else if(wheelMode == Joystick4DirectionConfig.MODE_TWO_WHEEL_DRIVE) {
			return get2WheelMaskRes(id, isSelected, isAngularMode);
		} else {
			return get4WheelMaskRes(id, isSelected, isAngularMode);
		}
	}
	
	@Override
	public void onBindViewHolder(@NonNull SettingItemViewHolder viewHolder, int i) {
		Integer id = list.get(i);
		boolean isSelected = selectedPosition != -1 && selectedIds[selectedPosition] == id;
		if(getJoystickType() == JOYSTICK_TYPE_MOTOR) {
			viewHolder.motorView.setIdValue(id);
			int[] maskRes = getMaskRes(id, isSelected, false);
			viewHolder.updateMask(maskRes, isSelected, false);
		} else {
			boolean isAngularMode = isAngularMode(id);
			viewHolder.steeringGearView.setIdValue(id);
			viewHolder.steeringGearView.setMode(isAngularMode ? SteeringGearView.MODE_ANGULAR : SteeringGearView.MODE_WHEEL);
			int[] maskRes = getMaskRes(id, isSelected, isAngularMode);
			viewHolder.updateMask(maskRes, isSelected, isAngularMode);
		}
		viewHolder.itemView.setTag(i);
        if(viewHolder.animator.isRunning()) {
            viewHolder.animator.cancel();
        }
		if(shakeList.contains(id)) {
			viewHolder.animator.start();
		} else {
			viewHolder.itemView.setRotation(0);
		}
		if(itemClickListener != null) {
			viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(itemClickListener != null) {
						itemClickListener.onItemClick(v, (int)v.getTag());
					}
				}
			});
		}
	}

	public void cleanShakeItem() {
		shakeList.clear();
		notifyDataSetChanged();
	}

	public void setShakeItem(Collection<Integer> id) {
		shakeList.clear();
		shakeList.addAll(id);
		notifyDataSetChanged();
	}

	public void addShakeItem(Integer id) {
		if(id < 0 || id > 32) {
			return;
		}
		if(shakeList.contains(id)) {
			return;
		}
		shakeList.add(id);
		notifyDataSetChanged();
	}

	public void removeShakeItem(Integer id) {
		if(id < 0 || id > 32) {
			return;
		}
		if(!shakeList.contains(id)) {
			return;
		}
		shakeList.remove(id);
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return list == null ? 0 : list.size();
	}
	
	public Integer getItem(int position) {
		return list.get(position);
	}
	
	public static class SettingItemViewHolder extends RecyclerView.ViewHolder {
		private SettingItemViewHolder(@NonNull View itemView) {
			super(itemView);
			steeringGearView = itemView.findViewById(R.id.peripheral_setting_steering_gear);
			motorView = itemView.findViewById(R.id.peripheral_setting_motor);
			bg = itemView.findViewById(R.id.peripheral_setting_mask_bg);
			flMark = itemView.findViewById(R.id.peripheral_setting_mask_fl_iv);
			frMark = itemView.findViewById(R.id.peripheral_setting_mask_fr_iv);
			blMark = itemView.findViewById(R.id.peripheral_setting_mask_bl_iv);
			brMark = itemView.findViewById(R.id.peripheral_setting_mask_br_iv);
			animator = ObjectAnimator.ofFloat(itemView, "rotation", 0, 5.0f, -5.0f, 0);
			animator.setDuration(ControllerConstValue.DEFAULT_ANIMATION_DURATION);
			animator.setRepeatMode(ValueAnimator.RESTART);
			animator.setRepeatCount(ValueAnimator.INFINITE);
		}
		private final SteeringGearView steeringGearView;
		private final MotorView motorView;
		private final ImageView bg;
		private final ImageView flMark;
		private final ImageView frMark;
		private final ImageView blMark;
		private final ImageView brMark;
		private final ObjectAnimator animator;
		private void updateMask(int[] maskRes, boolean isSelected, boolean isAngularMode) {
			if(maskRes.length >= 4) {
				if(maskRes[0] != -1) {
					flMark.setVisibility(View.VISIBLE);
					flMark.setImageResource(maskRes[0]);
				} else {
					flMark.setVisibility(View.GONE);
				}
				if(maskRes[1] != -1) {
					frMark.setVisibility(View.VISIBLE);
					frMark.setImageResource(maskRes[1]);
				} else {
					frMark.setVisibility(View.GONE);
				}
				if(maskRes[2] != -1) {
					blMark.setVisibility(View.VISIBLE);
					blMark.setImageResource(maskRes[2]);
				} else {
					blMark.setVisibility(View.GONE);
				}
				if(maskRes[3] != -1) {
					brMark.setVisibility(View.VISIBLE);
					brMark.setImageResource(maskRes[3]);
				} else {
					brMark.setVisibility(View.GONE);
				}
			}
			bg.setVisibility(isSelected ? View.VISIBLE : View.GONE);
			if(isSelected) {
				if(isAngularMode) {
					bg.setImageResource(R.drawable.popup_servo_sel_red_bg);
				} else {
					bg.setImageResource(R.drawable.popup_servo_sel_green_bg);
				}
			}
		}
	}
	
}
