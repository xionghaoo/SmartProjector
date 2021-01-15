package com.ubtedu.ukit.project.controller.settings.joystick.two;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.BtInvocationFactory;
import com.ubtedu.ukit.bluetooth.model.SteeringGear;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.menu.settings.Settings;
import com.ubtedu.ukit.project.Workspace;
import com.ubtedu.ukit.project.controller.ControllerModeHolder;
import com.ubtedu.ukit.project.controller.interfaces.IControllerItemClickListener;
import com.ubtedu.ukit.project.controller.manager.ControllerManager;
import com.ubtedu.ukit.project.controller.model.config.Joystick2DirectionHConfig;
import com.ubtedu.ukit.project.controller.model.config.Joystick2DirectionVConfig;
import com.ubtedu.ukit.project.controller.model.config.WidgetConfig;
import com.ubtedu.ukit.project.controller.model.define.WidgetType;
import com.ubtedu.ukit.project.controller.settings.adapter.ControlItemAdapter;
import com.ubtedu.ukit.project.controller.settings.adapter.SimpleControlItemAdapter;
import com.ubtedu.ukit.project.controller.widget.Joystick2DirectionViewH;
import com.ubtedu.ukit.project.controller.widget.Joystick2DirectionViewV;

/**
 * @Author naOKi
 * @Date 2018/12/04
 **/
public class Joystick2DirectionSettingActivity extends UKitBaseActivity implements RadioGroup.OnCheckedChangeListener {

    private static final int SELECTED_POSITION = 0;

    public final static String EXTRA_CONTROL_ID = "_extra_control_id";
    public final static String EXTRA_CONTROL_TYPE = "_extra_control_type";
    public final static String EXTRA_JOYSTICK_MODE = "_extra_joystick_mode";
    public final static String EXTRA_JOYSTICK_DIRECTION = "_extra_joystick_direction";

    private WidgetType mode = null;
    private int id = -1;
    private int direction = -1;
    private int type = -1;

    private RadioGroup mButtonRg;
    private RadioButton mClockwiseBtn;
    private RadioButton mCounterclockwiseBtn;

    private ImageView mCloseIv;
    private ImageView mConfirmIv;

    private RecyclerView mSteeringGearList;

    private RadioButton mModeSteeringGear;
    private RadioButton mModeMotor;
    private RadioGroup mModeGroup;

    private View mEmptyView;
    private ImageView mEmptyIcon;
    private TextView mEmptyText;

    private View mVModeIcon;
    private View mHModeIcon;

    private SimpleControlItemAdapter mAdapter;

    private Joystick2DirectionViewV mJoystick2DirectionViewV;
    private Joystick2DirectionViewH mJoystick2DirectionViewH;

    private View.OnTouchListener mOnRadioButtonTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return isJoystickDownPressed();
        }
    };

    private static Intent makeSettingIntent(Context context, WidgetType mode, int id, int direction, int type) {
        Intent intent = new Intent(context, Joystick2DirectionSettingActivity.class);
        intent.putExtra(EXTRA_JOYSTICK_MODE, mode);
        intent.putExtra(EXTRA_CONTROL_ID, id);
        intent.putExtra(EXTRA_CONTROL_TYPE, type);
        intent.putExtra(EXTRA_JOYSTICK_DIRECTION, direction);
        return intent;
    }

    public static void openSetting(Fragment fragment, int requestCode, Joystick2DirectionVConfig config) {
        Intent intent = makeSettingIntent(fragment.getContext(), WidgetType.WIDGET_UKTWOWAYJOYSTICKV, config.getControlId(), config.getDirection(), config.getControlType());
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void openSetting(Fragment fragment, int requestCode, Joystick2DirectionHConfig config) {
        Intent intent = makeSettingIntent(fragment.getContext(), WidgetType.WIDGET_UKTWOWAYJOYSTICKH, config.getControlId(), config.getDirection(), config.getControlType());
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void openSetting(Activity activity, int requestCode, Joystick2DirectionVConfig config) {
        Intent intent = makeSettingIntent(activity, WidgetType.WIDGET_UKTWOWAYJOYSTICKV, config.getControlId(), config.getDirection(), config.getControlType());
        activity.startActivityForResult(intent, requestCode);
    }

    public static void openSetting(Activity activity, int requestCode, Joystick2DirectionHConfig config) {
        Intent intent = makeSettingIntent(activity, WidgetType.WIDGET_UKTWOWAYJOYSTICKH, config.getControlId(), config.getDirection(), config.getControlType());
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_controller_setting_joystick_two);
        initView();
        if (savedInstanceState == null) {
            initData(getIntent().getExtras());
        } else {
            initData(savedInstanceState);
        }
        ControllerManager.setBackgroundFlag(false);
        ControllerModeHolder.setControllerMode(ControllerModeHolder.MODE_PREVIEW);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_CONTROL_ID, id);
        outState.putInt(EXTRA_CONTROL_TYPE, type);
        outState.putInt(EXTRA_JOYSTICK_DIRECTION, direction);
        outState.putSerializable(EXTRA_JOYSTICK_MODE, mode);
    }

    private void initView() {
        mButtonRg = findViewById(R.id.project_controller_setting_btn_rg);
        mClockwiseBtn = findViewById(R.id.project_controller_setting_clockwise_btn);
        mCounterclockwiseBtn = findViewById(R.id.project_controller_setting_counterclockwise_btn);

        mCloseIv = findViewById(R.id.project_controller_nav_close_btn);
        mConfirmIv = findViewById(R.id.project_controller_nav_confirm_btn);

        mSteeringGearList = findViewById(R.id.project_controller_steering_gear_list);

        mVModeIcon = findViewById(R.id.project_controller_steering_gear_vertical);
        mHModeIcon = findViewById(R.id.project_controller_steering_gear_horizontal);

        mModeSteeringGear = findViewById(R.id.project_controller_steering_gear_list_type_steering_gear_rb);
        mModeMotor = findViewById(R.id.project_controller_steering_gear_list_type_motor_rb);
        mModeGroup = findViewById(R.id.project_controller_steering_gear_list_type_rg);

        mModeMotor.setChecked(type == WidgetConfig.CONTROLTYPE_MOTOR);
        mModeGroup.setOnCheckedChangeListener(this);

        mEmptyView = findViewById(R.id.project_controller_steering_gear_list_empty_view);
        mEmptyIcon = findViewById(R.id.project_controller_steering_gear_list_empty_iv);
        mEmptyText = findViewById(R.id.project_controller_steering_gear_list_empty_tv);

        mAdapter = new SimpleControlItemAdapter();
        mSteeringGearList.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mSteeringGearList.setAdapter(mAdapter);
        mSteeringGearList.setLayoutManager(new GridLayoutManager(this, 5));
        mSteeringGearList.setHasFixedSize(true);

        mAdapter.setJoystickMode(ControlItemAdapter.JOYSTICK_MODE_2DIRECTION);
        mAdapter.setSelectedPosition(SELECTED_POSITION);

        mAdapter.setItemClickListener(new IControllerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!isJoystickDownPressed()) {
                    int id = mAdapter.getItem(position);
                    updateId(id);
                    type = mAdapter.getJoystickType() == ControlItemAdapter.JOYSTICK_TYPE_MOTOR ? WidgetConfig.CONTROLTYPE_MOTOR : WidgetConfig.CONTROLTYPE_STEERINGGEAR;
                    updateWidgetConfig();

                    if (type == WidgetConfig.CONTROLTYPE_MOTOR) {
                        shakeMotor();
                    } else {
                        shakeSteeringGear();
                    }
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

        bindSafeClickListener(mCloseIv);
        bindSafeClickListener(mConfirmIv);

        mButtonRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateDirection(mClockwiseBtn.isChecked() ? Joystick2DirectionHConfig.CLOCKWISE : Joystick2DirectionHConfig.COUNTERCLOCKWISE);
                updateWidgetConfig();
            }
        });

        mJoystick2DirectionViewV = findViewById(R.id.controller_widget_action_joystick_vertical);
        mJoystick2DirectionViewH = findViewById(R.id.controller_widget_action_joystick_horizontal);
        mJoystick2DirectionViewV.setShowBackground(false);
        mJoystick2DirectionViewH.setShowBackground(false);
        mJoystick2DirectionViewV.setNameVisibility(false);
        mJoystick2DirectionViewH.setNameVisibility(false);

        mModeSteeringGear.setOnTouchListener(mOnRadioButtonTouchListener);
        mModeMotor.setOnTouchListener(mOnRadioButtonTouchListener);

        mClockwiseBtn.setOnTouchListener(mOnRadioButtonTouchListener);
        mCounterclockwiseBtn.setOnTouchListener(mOnRadioButtonTouchListener);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (mModeMotor.isChecked()) {
            mAdapter.setJoystickType(ControlItemAdapter.JOYSTICK_TYPE_MOTOR);
        } else {
            mAdapter.setJoystickType(ControlItemAdapter.JOYSTICK_TYPE_STEERINGGEAR);
        }
        if ((type == WidgetConfig.CONTROLTYPE_MOTOR && mModeMotor.isChecked()) || (type == WidgetConfig.CONTROLTYPE_STEERINGGEAR && mModeSteeringGear.isChecked())) {
            mAdapter.setSelectedItem(SELECTED_POSITION, id);
        }
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        if (v == mCloseIv) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (v == mConfirmIv) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_CONTROL_ID, id);
            intent.putExtra(EXTRA_CONTROL_TYPE, type);
            intent.putExtra(EXTRA_JOYSTICK_DIRECTION, direction);
            intent.putExtra(EXTRA_JOYSTICK_MODE, mode);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Workspace.getInstance() != null && Workspace.getInstance().getProject() != null) {
            mAdapter.setModelInfo(Workspace.getInstance().getProject().modelInfo);
        }
    }

    private void updateId(int id) {
        this.id = id;
        mAdapter.setSelectedItem(SELECTED_POSITION, id);
    }

    private void updateDirection(int direction) {
        this.direction = direction;
        if (direction == Joystick2DirectionHConfig.CLOCKWISE) {
            mClockwiseBtn.setChecked(true);
        } else {
            mCounterclockwiseBtn.setChecked(true);
        }
        mAdapter.setRotateDirection(direction);
    }

    private void updateControlType(int type) {
        this.type = type;
        if (type == WidgetConfig.CONTROLTYPE_MOTOR) {
            mModeMotor.setChecked(true);
        } else {
            mModeSteeringGear.setChecked(true);
        }
        mAdapter.setJoystickType(type == WidgetConfig.CONTROLTYPE_MOTOR ? ControlItemAdapter.JOYSTICK_TYPE_MOTOR : ControlItemAdapter.JOYSTICK_TYPE_STEERINGGEAR);
    }

    private void initData(Bundle bundle) {
        if (bundle != null) {
            id = bundle.getInt(EXTRA_CONTROL_ID, -1);
            direction = bundle.getInt(EXTRA_JOYSTICK_DIRECTION, Joystick2DirectionHConfig.CLOCKWISE);
            type = bundle.getInt(EXTRA_CONTROL_TYPE, WidgetConfig.CONTROLTYPE_STEERINGGEAR);
            mode = (WidgetType) bundle.getSerializable(EXTRA_JOYSTICK_MODE);
        } else {
            id = -1;
            direction = Joystick2DirectionHConfig.CLOCKWISE;
            type = WidgetConfig.CONTROLTYPE_STEERINGGEAR;
            mode = WidgetType.WIDGET_UKTWOWAYJOYSTICKH;
        }
        if (WidgetType.WIDGET_UKTWOWAYJOYSTICKH.equals(mode)) {
            mVModeIcon.setVisibility(View.GONE);
            mHModeIcon.setVisibility(View.VISIBLE);
        } else {
            mVModeIcon.setVisibility(View.VISIBLE);
            mHModeIcon.setVisibility(View.GONE);
        }
        updateControlType(type);
        updateId(id);
        updateDirection(direction);

        updateWidgetConfig();
    }

    private void updateWidgetConfig() {
        if (WidgetType.WIDGET_UKTWOWAYJOYSTICKH.equals(mode)) {
            Joystick2DirectionHConfig widgetConfig = mJoystick2DirectionViewH.getWidgetConfig();
            if (widgetConfig == null) {
                widgetConfig = new Joystick2DirectionHConfig();
            }
            widgetConfig.setControlType(type);
            widgetConfig.setControlId(id);
            widgetConfig.setDirection(direction);
            mJoystick2DirectionViewH.setWidgetConfig(widgetConfig);
        } else {
            Joystick2DirectionVConfig widgetConfig = mJoystick2DirectionViewV.getWidgetConfig();
            if (widgetConfig == null) {
                widgetConfig = new Joystick2DirectionVConfig();
            }
            widgetConfig.setControlType(type);
            widgetConfig.setControlId(id);
            widgetConfig.setDirection(direction);
            mJoystick2DirectionViewV.setWidgetConfig(widgetConfig);
        }
    }

    @Override
    protected void onDestroy() {
        ControllerModeHolder.setControllerMode(ControllerModeHolder.MODE_EDIT);
        super.onDestroy();
    }

    private void shakeMotor() {
        if (!BluetoothHelper.isConnected()) {
            return;
        }
        if (Settings.isChargingProtection() && BluetoothHelper.isCharging()) {
            return;
        }
        BluetoothHelper.addCommand(BtInvocationFactory.rotateMotors(new int[]{id}, SteeringGear.getSpeedInMode(80, direction == Joystick2DirectionHConfig.CLOCKWISE ? SteeringGear.Mode.CLOCKWISE : SteeringGear.Mode.COUNTERCLOCKWISE),400));
    }

    private void shakeSteeringGear() {
        if (!BluetoothHelper.isConnected()) {
            return;
        }
        if (Settings.isChargingProtection() && BluetoothHelper.isCharging()) {
            return;
        }
        BluetoothHelper.addCommand(BtInvocationFactory.rotateServos(new int[]{id}, SteeringGear.getSpeedInMode(SteeringGear.Speed.F.getValue(), direction == Joystick2DirectionHConfig.CLOCKWISE ? SteeringGear.Mode.CLOCKWISE : SteeringGear.Mode.COUNTERCLOCKWISE), 400));
    }

    private boolean isJoystickDownPressed() {
        return WidgetType.WIDGET_UKTWOWAYJOYSTICKH.equals(mode) ? mJoystick2DirectionViewH.isDownPressed() : mJoystick2DirectionViewV.isDownPressed();
    }
}
