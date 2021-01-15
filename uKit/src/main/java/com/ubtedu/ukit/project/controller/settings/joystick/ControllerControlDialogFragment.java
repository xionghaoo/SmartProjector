/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.controller.settings.joystick;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.BtInvocationFactory;
import com.ubtedu.ukit.bluetooth.model.SteeringGear;
import com.ubtedu.ukit.common.base.UKitBaseDialogFragment;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;
import com.ubtedu.ukit.menu.settings.Settings;
import com.ubtedu.ukit.project.Workspace;
import com.ubtedu.ukit.project.controller.interfaces.IControllerItemClickListener;
import com.ubtedu.ukit.project.controller.model.config.Joystick4DirectionConfig;
import com.ubtedu.ukit.project.controller.model.config.WidgetConfig;
import com.ubtedu.ukit.project.controller.settings.adapter.ControlItemAdapter;
import com.ubtedu.ukit.project.controller.settings.adapter.SimpleControlItemAdapter;
import com.ubtedu.ukit.project.controller.settings.interfaces.IControllerControlSelectListener;

/**
 * @Author naOKi
 * @Date 2018/12/04
 **/
public class ControllerControlDialogFragment extends UKitBaseDialogFragment implements RadioGroup.OnCheckedChangeListener {

    private final static String EXTRA_JOYSTICK_CONFIG = "_extra_joystick_config";
    private final static String EXTRA_SETTING_POSITION = "_extra_setting_position";

    private Joystick4DirectionConfig config = null;
    private int selectedPosition = -1;
    private boolean hasReset = false;

    private Button mConfirmBtn;
    private Button mCancelBtn;
    private RecyclerView mControlListRv;

    private RadioButton mModeSteeringGear;
    private RadioButton mModeMotor;
    private RadioGroup mModeGroup;

    private View mEmptyView;
    private ImageView mEmptyIcon;
    private TextView mEmptyText;

    private SimpleControlItemAdapter mAdapter;

    private IControllerControlSelectListener mControllerControlItemSelectListener;

//    private BluetoothActionSequence mMotorActionSequence;
//    private BluetoothActionSequence mSteeringGearActionSequence;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            initArguments(savedInstanceState);
        } else {
            initArguments(getArguments());
        }
    }

    private void initArguments(Bundle args) {
        if (args != null) {
            config = (Joystick4DirectionConfig) args.getSerializable(EXTRA_JOYSTICK_CONFIG);
            selectedPosition = args.getInt(EXTRA_SETTING_POSITION, -1);
        }
        if (selectedPosition == -1) {
            selectedPosition = 0;
        }
        if (config == null) {
            config = new Joystick4DirectionConfig();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_JOYSTICK_CONFIG, config);
        outState.putInt(EXTRA_SETTING_POSITION, selectedPosition);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_project_controller_steering_gear, null);
        if (isCancelable()) {
            super.mRootView = view.findViewById(R.id.dialog_fragment_root_view);
        }
        mConfirmBtn = view.findViewById(R.id.dialog_project_controller_steering_gear_confirm_btn);
        bindSafeClickListener(mConfirmBtn);
        mCancelBtn = view.findViewById(R.id.dialog_project_controller_steering_gear_cancel_btn);
        bindSafeClickListener(mCancelBtn);
        mControlListRv = view.findViewById(R.id.dialog_project_controller_steering_gear_list);

        mModeSteeringGear = view.findViewById(R.id.dialog_project_controller_list_type_steering_gear_rb);
        mModeMotor = view.findViewById(R.id.dialog_project_controller_list_type_motor_rb);
        mModeGroup = view.findViewById(R.id.dialog_project_controller_list_type_rg);

        mModeMotor.setChecked(config.getControlType() == WidgetConfig.CONTROLTYPE_MOTOR);
        mModeGroup.setOnCheckedChangeListener(this);

        mEmptyView = view.findViewById(R.id.dialog_project_controller_steering_gear_list_empty_view);
        mEmptyIcon = view.findViewById(R.id.dialog_project_controller_steering_gear_list_empty_iv);
        mEmptyText = view.findViewById(R.id.dialog_project_controller_steering_gear_list_empty_tv);

        mAdapter = new SimpleControlItemAdapter();
        mControlListRv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mControlListRv.setAdapter(mAdapter);
        mControlListRv.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mControlListRv.setHasFixedSize(true);

        mAdapter.setItemClickListener(new IControllerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int id = mAdapter.getItem(position);
                config.setControlId(selectedPosition, id);
                mAdapter.setSelectedItem(selectedPosition, id);

                if (mAdapter.getJoystickType() == ControlItemAdapter.JOYSTICK_TYPE_MOTOR) {
                    shakeMotor(id, mAdapter.getSelectedPosition());
                } else {
                    shakeSteeringGear(id, mAdapter.getSelectedPosition());
                }
            }
        });

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (mAdapter.getItemCount() == 0) {
                    if (mAdapter.getJoystickType() == ControlItemAdapter.JOYSTICK_TYPE_MOTOR) {
                        mEmptyIcon.setImageResource(R.drawable.remote_popup_pic_disconnection2);
                        mEmptyText.setText(R.string.project_controller_joystick_empty_motor);
                    } else {
                        mEmptyIcon.setImageResource(R.drawable.remote_popup_pic_disconnection1);
                        mEmptyText.setText(R.string.project_controller_joystick_empty_steering_gear);
                    }
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        });

        if (Workspace.getInstance() != null && Workspace.getInstance().getProject() != null) {
            mAdapter.setModelInfo(Workspace.getInstance().getProject().modelInfo);
        }
        mAdapter.setJoystickMode(ControlItemAdapter.JOYSTICK_MODE_4DIRECTION);
        mAdapter.setJoystickType(config.getControlType() == WidgetConfig.CONTROLTYPE_MOTOR ? ControlItemAdapter.JOYSTICK_TYPE_MOTOR : ControlItemAdapter.JOYSTICK_TYPE_STEERINGGEAR);
        mAdapter.setWheelMode(config.getMode());
        mAdapter.setSelectedPosition(selectedPosition);
        if (config.getMode() == Joystick4DirectionConfig.MODE_FOUR_WHEEL_DRIVE) {
            mAdapter.setSelectedItem(Joystick4DirectionConfig.LEFT_FRONT_ID, config.getLeftFrontId());
            mAdapter.setSelectedItem(Joystick4DirectionConfig.RIGHT_FRONT_ID, config.getRightFrontId());
            mAdapter.setSelectedItem(Joystick4DirectionConfig.LEFT_BEHIND_ID, config.getLeftBehindId());
            mAdapter.setSelectedItem(Joystick4DirectionConfig.RIGHT_BEHIND_ID, config.getRightBehindId());
        } else {
            mAdapter.setSelectedItem(Joystick4DirectionConfig.LEFT_ID, config.getLeftId());
            mAdapter.setSelectedItem(Joystick4DirectionConfig.RIGHT_ID, config.getRightId());
        }

        return view;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (mAdapter.getJoystickType() == ControlItemAdapter.JOYSTICK_TYPE_MOTOR && mModeMotor.isChecked()) {
            return;
        }
        if (mAdapter.getJoystickType() != ControlItemAdapter.JOYSTICK_TYPE_MOTOR && mModeSteeringGear.isChecked()) {
            return;
        }
        if (mAdapter.hasSelectedItem()) {
            PromptDialogFragment.newBuilder(getContext())
                    .type(PromptDialogFragment.Type.NORMAL)
                    .title(getString(R.string.project_controller_joystick_mode_change_title))
                    .message(getString(R.string.project_controller_joystick_mode_change_msg))
                    .cancelable(true)
                    .positiveButtonText(getString(R.string.project_controller_joystick_mode_change_btn))
                    .onPositiveClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hasReset = true;
                            if (mModeMotor.isChecked()) {
                                mAdapter.setJoystickType(ControlItemAdapter.JOYSTICK_TYPE_MOTOR);
                                config.setControlType(WidgetConfig.CONTROLTYPE_MOTOR);
                            } else {
                                mAdapter.setJoystickType(ControlItemAdapter.JOYSTICK_TYPE_STEERINGGEAR);
                                config.setControlType(WidgetConfig.CONTROLTYPE_STEERINGGEAR);
                            }
                        }
                    })
                    .negativeButtonText(getString(R.string.prompt_cancel))
                    .onDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (mAdapter.getJoystickType() == ControlItemAdapter.JOYSTICK_TYPE_MOTOR) {
                                mModeMotor.setChecked(true);
                            } else {
                                mModeSteeringGear.setChecked(true);
                            }
                        }
                    })
                    .build()
                    .show(getChildFragmentManager(), "mode_change");
        } else {
            hasReset = true;
            if (mModeMotor.isChecked()) {
                mAdapter.setJoystickType(ControlItemAdapter.JOYSTICK_TYPE_MOTOR);
                config.setControlType(WidgetConfig.CONTROLTYPE_MOTOR);
            } else {
                mAdapter.setJoystickType(ControlItemAdapter.JOYSTICK_TYPE_STEERINGGEAR);
                config.setControlType(WidgetConfig.CONTROLTYPE_STEERINGGEAR);
            }
        }
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        if (v == mConfirmBtn) {
            if (mControllerControlItemSelectListener != null) {
                //替换id
                int selectedId = config.getControlId(selectedPosition);
                if (hasReset) {
                    if (config.getMode() == Joystick4DirectionConfig.MODE_FOUR_WHEEL_DRIVE) {
                        config.setLeftFrontId(selectedPosition != Joystick4DirectionConfig.LEFT_FRONT_ID ? -1 : selectedId);
                        config.setRightFrontId(selectedPosition != Joystick4DirectionConfig.RIGHT_FRONT_ID ? -1 : selectedId);
                        config.setLeftBehindId(selectedPosition != Joystick4DirectionConfig.LEFT_BEHIND_ID ? -1 : selectedId);
                        config.setRightBehindId(selectedPosition != Joystick4DirectionConfig.RIGHT_BEHIND_ID ? -1 : selectedId);
                    } else {
                        config.setLeftId(selectedPosition != Joystick4DirectionConfig.LEFT_ID ? -1 : selectedId);
                        config.setRightId(selectedPosition != Joystick4DirectionConfig.RIGHT_ID ? -1 : selectedId);
                    }
                } else {
                    if (config.getMode() == Joystick4DirectionConfig.MODE_FOUR_WHEEL_DRIVE) {
                        config.setLeftFrontId(selectedId == config.getLeftFrontId() && selectedPosition != Joystick4DirectionConfig.LEFT_FRONT_ID ? -1 : config.getLeftFrontId());
                        config.setRightFrontId(selectedId == config.getRightFrontId() && selectedPosition != Joystick4DirectionConfig.RIGHT_FRONT_ID ? -1 : config.getRightFrontId());
                        config.setLeftBehindId(selectedId == config.getLeftBehindId() && selectedPosition != Joystick4DirectionConfig.LEFT_BEHIND_ID ? -1 : config.getLeftBehindId());
                        config.setRightBehindId(selectedId == config.getRightBehindId() && selectedPosition != Joystick4DirectionConfig.RIGHT_BEHIND_ID ? -1 : config.getRightBehindId());
                    } else {
                        config.setLeftId(selectedId == config.getLeftId() && selectedPosition != Joystick4DirectionConfig.LEFT_ID ? -1 : config.getLeftId());
                        config.setRightId(selectedId == config.getRightId() && selectedPosition != Joystick4DirectionConfig.RIGHT_ID ? -1 : config.getRightId());
                    }
                }
                mControllerControlItemSelectListener.onControlItemSelected(selectedPosition, config);
            }
            dismiss();
        }
        if (v == mCancelBtn) {
            dismiss();
        }
    }

    @Override
    public void dismiss() {
        if (mDismissListener != null) {
            mDismissListener.onDismiss();
        }
        if (getActivity() != null && !getActivity().isFinishing()) {
            super.dismissAllowingStateLoss();
        }
    }

    private void shakeMotor(int id, int selectedPosition) {
        if (!BluetoothHelper.isConnected()) {
            return;
        }
        if (Settings.isChargingProtection() && BluetoothHelper.isCharging()) {
            return;
        }
//        if (mMotorActionSequence != null&&!mMotorActionSequence.hasFinish()) {
//            mMotorActionSequence.abort();
//            BluetoothHelper.stopRobot();
//        }
//        mMotorActionSequence = ActionSequenceFactory.createShakeMotorActionSequence(id, getDirection(selectedPosition));
//        mMotorActionSequence.execute();
        BluetoothHelper.addCommand(BtInvocationFactory.rotateMotors(new int[]{id}, SteeringGear.getSpeedInMode(80, getDirection(selectedPosition)),400));

    }

    private void shakeSteeringGear(int id, int selectedPosition) {
        if (!BluetoothHelper.isConnected()) {
            return;
        }
        if (Settings.isChargingProtection() && BluetoothHelper.isCharging()) {
            return;
        }
//        if (mSteeringGearActionSequence != null&&!mSteeringGearActionSequence.hasFinish()) {
//            mSteeringGearActionSequence.abort();
//            BluetoothHelper.stopRobot();
//        }
//        mSteeringGearActionSequence = ActionSequenceFactory.createShakeSteeringGearActionSequence(id, getDirection(selectedPosition));
//        mSteeringGearActionSequence.execute();
        BluetoothHelper.addCommand(BtInvocationFactory.rotateServos(new int[]{id}, SteeringGear.getSpeedInMode(SteeringGear.Speed.F.getValue(), getDirection(selectedPosition)), 400));

    }

    private SteeringGear.Mode getDirection(int selectedPosition) {
        if (config.getMode() == Joystick4DirectionConfig.MODE_FOUR_WHEEL_DRIVE) {
            if (selectedPosition == Joystick4DirectionConfig.RIGHT_FRONT_ID || selectedPosition == Joystick4DirectionConfig.RIGHT_BEHIND_ID) {
                return SteeringGear.Mode.CLOCKWISE;
            } else {
                return SteeringGear.Mode.COUNTERCLOCKWISE;
            }
        } else {
            if (selectedPosition == Joystick4DirectionConfig.RIGHT_ID) {
                return SteeringGear.Mode.CLOCKWISE;
            } else {
                return SteeringGear.Mode.COUNTERCLOCKWISE;
            }
        }
    }

    public static Builder newBuilder(Context context) {
        return new Builder(context);
    }

    public static class Builder {
        private Context mContext;
        private Joystick4DirectionConfig config = new Joystick4DirectionConfig();
        private int selectedPosition = -1;
        private IControllerControlSelectListener mControllerControlItemSelectListener;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setSelectedPosition(int selectedPosition) {
            this.selectedPosition = selectedPosition;
            return this;
        }

        public Builder setConfig(Joystick4DirectionConfig config) {
            this.config.setControlType(config.getControlType());
            this.config.setMode(config.getMode());
            this.config.setLeftId(config.getLeftId());
            this.config.setRightId(config.getRightId());
            this.config.setLeftFrontId(config.getLeftFrontId());
            this.config.setRightFrontId(config.getRightFrontId());
            this.config.setLeftBehindId(config.getLeftBehindId());
            this.config.setRightBehindId(config.getRightBehindId());
            return this;
        }

        public Builder setSteeringGearSelectListener(IControllerControlSelectListener controlItemSelectListener) {
            this.mControllerControlItemSelectListener = controlItemSelectListener;
            return this;
        }

        public ControllerControlDialogFragment build() {
            ControllerControlDialogFragment fragment = new ControllerControlDialogFragment();
            fragment.setCancelable(false);
            fragment.mControllerControlItemSelectListener = mControllerControlItemSelectListener;
            Bundle args = new Bundle();
            args.putSerializable(EXTRA_JOYSTICK_CONFIG, config);
            args.putInt(EXTRA_SETTING_POSITION, selectedPosition);
            fragment.setArguments(args);
            return fragment;
        }
    }

}
