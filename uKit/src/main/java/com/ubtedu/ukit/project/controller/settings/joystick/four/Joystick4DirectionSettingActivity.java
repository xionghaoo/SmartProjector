package com.ubtedu.ukit.project.controller.settings.joystick.four;

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

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.project.controller.ControllerModeHolder;
import com.ubtedu.ukit.project.controller.manager.ControllerManager;
import com.ubtedu.ukit.project.controller.model.config.Joystick4DirectionConfig;
import com.ubtedu.ukit.project.controller.model.config.WidgetConfig;
import com.ubtedu.ukit.project.controller.settings.interfaces.IControllerControlSelectListener;
import com.ubtedu.ukit.project.controller.settings.joystick.ControllerControlDialogFragment;
import com.ubtedu.ukit.project.controller.widget.Joystick4DirectionView;
import com.ubtedu.ukit.project.controller.widget.MotorView;
import com.ubtedu.ukit.project.controller.widget.SteeringGearView;

/**
 * @Author naOKi
 * @Date 2018/12/04
 **/
public class Joystick4DirectionSettingActivity extends UKitBaseActivity {

    public final static String EXTRA_JOYSTICK_CONFIG = "_extra_joystick_config";
    private final static String EXTRA_JOYSTICK_CONFIG_2WHEEL = "_extra_joystick_config_2wheel";
    private final static String EXTRA_JOYSTICK_CONFIG_4WHEEL = "_extra_joystick_config_4wheel";

    private Joystick4DirectionConfig config = null;
    private Joystick4DirectionConfig config_2wheel = new Joystick4DirectionConfig();
    private Joystick4DirectionConfig config_4wheel = new Joystick4DirectionConfig();

    private RadioGroup mButtonRg;
    private RadioButton m4WheelBtn;
    private RadioButton m2WheelBtn;

    private ImageView mCloseIv;
    private ImageView mConfirmIv;

    private View m4WheelViewArea;
    private SteeringGearView mFlSteeringGear;
    private SteeringGearView mFrSteeringGear;
    private SteeringGearView mBlSteeringGear;
    private SteeringGearView mBrSteeringGear;
    private MotorView mFlMotor;
    private MotorView mFrMotor;
    private MotorView mBlMotor;
    private MotorView mBrMotor;
    private TextView mFlTv;
    private TextView mFrTv;
    private TextView mBlTv;
    private TextView mBrTv;

    private View m2WheelViewArea;
    private SteeringGearView mLSteeringGear;
    private SteeringGearView mRSteeringGear;
    private MotorView mLMotor;
    private MotorView mRMotor;
    private TextView mLTv;
    private TextView mRTv;

    private Joystick4DirectionView mJoystick4DirectionView;

    private View.OnTouchListener mOnRadioButtonTouchListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return mJoystick4DirectionView.isDownPressed();
        }
    };

    private static Intent makeSettingIntent(Context context, Joystick4DirectionConfig config) {
        Intent intent = new Intent(context, Joystick4DirectionSettingActivity.class);
        intent.putExtra(EXTRA_JOYSTICK_CONFIG, config);
        return intent;
    }

    public static void openSetting(Fragment fragment, int requestCode, Joystick4DirectionConfig config) {
        Intent intent = makeSettingIntent(fragment.getContext(), config);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void openSetting(Activity activity, int requestCode, Joystick4DirectionConfig config) {
        Intent intent = makeSettingIntent(activity, config);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_controller_setting_joystick_four);
        initView();
        if(savedInstanceState == null) {
            initData(getIntent().getExtras());
        } else {
            initData(savedInstanceState);
        }
        mJoystick4DirectionView.setWidgetConfig(config);
        ControllerManager.setBackgroundFlag(false);
        ControllerModeHolder.setControllerMode(ControllerModeHolder.MODE_PREVIEW);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_JOYSTICK_CONFIG, config);
        outState.putSerializable(EXTRA_JOYSTICK_CONFIG_2WHEEL, config_2wheel);
        outState.putSerializable(EXTRA_JOYSTICK_CONFIG_4WHEEL, config_4wheel);
    }

    private void initView() {
        mButtonRg = findViewById(R.id.project_controller_setting_btn_rg);
        m2WheelBtn = findViewById(R.id.project_controller_setting_two_wheel_drive_btn);
        m4WheelBtn = findViewById(R.id.project_controller_setting_four_wheel_drive_btn);

        mCloseIv = findViewById(R.id.project_controller_nav_close_btn);
        mConfirmIv = findViewById(R.id.project_controller_nav_confirm_btn);

        m4WheelViewArea = findViewById(R.id.project_controller_setting_4wheel_mode_area);
        mFlSteeringGear = findViewById(R.id.project_controller_setting_fl_steering_gear);
        mFrSteeringGear = findViewById(R.id.project_controller_setting_fr_steering_gear);
        mBlSteeringGear = findViewById(R.id.project_controller_setting_bl_steering_gear);
        mBrSteeringGear = findViewById(R.id.project_controller_setting_br_steering_gear);
        mFlMotor = findViewById(R.id.project_controller_setting_fl_motor);
        mFrMotor = findViewById(R.id.project_controller_setting_fr_motor);
        mBlMotor = findViewById(R.id.project_controller_setting_bl_motor);
        mBrMotor = findViewById(R.id.project_controller_setting_br_motor);
        mFlTv = findViewById(R.id.project_controller_setting_fl_steering_gear_tv);
        mFrTv = findViewById(R.id.project_controller_setting_fr_steering_gear_tv);
        mBlTv = findViewById(R.id.project_controller_setting_bl_steering_gear_tv);
        mBrTv = findViewById(R.id.project_controller_setting_br_steering_gear_tv);
        mFlSteeringGear.setMode(SteeringGearView.MODE_WHEEL);
        mFrSteeringGear.setMode(SteeringGearView.MODE_WHEEL);
        mBlSteeringGear.setMode(SteeringGearView.MODE_WHEEL);
        mBrSteeringGear.setMode(SteeringGearView.MODE_WHEEL);

        m2WheelViewArea = findViewById(R.id.project_controller_setting_2wheel_mode_area);
        mLSteeringGear = findViewById(R.id.project_controller_setting_l_steering_gear);
        mRSteeringGear = findViewById(R.id.project_controller_setting_r_steering_gear);
        mLMotor = findViewById(R.id.project_controller_setting_l_motor);
        mRMotor = findViewById(R.id.project_controller_setting_r_motor);
        mLTv = findViewById(R.id.project_controller_setting_l_steering_gear_tv);
        mRTv = findViewById(R.id.project_controller_setting_r_steering_gear_tv);
        mLSteeringGear.setMode(SteeringGearView.MODE_WHEEL);
        mRSteeringGear.setMode(SteeringGearView.MODE_WHEEL);

        mJoystick4DirectionView=findViewById(R.id.controller_widget_action_joystick);
        mJoystick4DirectionView.setShowBackground(false);

        bindSafeClickListener(mCloseIv);
        bindSafeClickListener(mConfirmIv);

        bindSafeClickListener(mFlSteeringGear);
        bindSafeClickListener(mFrSteeringGear);
        bindSafeClickListener(mBlSteeringGear);
        bindSafeClickListener(mBrSteeringGear);
        bindSafeClickListener(mFlMotor);
        bindSafeClickListener(mFrMotor);
        bindSafeClickListener(mBlMotor);
        bindSafeClickListener(mBrMotor);

        bindSafeClickListener(mLSteeringGear);
        bindSafeClickListener(mRSteeringGear);
        bindSafeClickListener(mLMotor);
        bindSafeClickListener(mRMotor);
        mButtonRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mJoystick4DirectionView.isDownPressed()){
                    return;
                }
                if(m2WheelBtn.isChecked()) {
                    config = config_2wheel;
                } else {
                    config = config_4wheel;
                }
                updateView();
                mJoystick4DirectionView.setWidgetConfig(config);
            }
        });
        m4WheelBtn.setOnTouchListener(mOnRadioButtonTouchListener);
        m2WheelBtn.setOnTouchListener(mOnRadioButtonTouchListener);
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        if(v == mCloseIv) {
            setResult(RESULT_CANCELED);
            finish();
        } else if(v == mConfirmIv) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_JOYSTICK_CONFIG, config);
            setResult(RESULT_OK, intent);
            finish();
        } else if(v == mFlSteeringGear || v == mFlMotor) {
            openSettingDialog(Joystick4DirectionConfig.LEFT_FRONT_ID);
        } else if(v == mFrSteeringGear || v == mFrMotor) {
            openSettingDialog(Joystick4DirectionConfig.RIGHT_FRONT_ID);
        } else if(v == mBlSteeringGear || v == mBlMotor) {
            openSettingDialog(Joystick4DirectionConfig.LEFT_BEHIND_ID);
        } else if(v == mBrSteeringGear || v == mBrMotor) {
            openSettingDialog(Joystick4DirectionConfig.RIGHT_BEHIND_ID);
        } else if(v == mLSteeringGear || v == mLMotor) {
            openSettingDialog(Joystick4DirectionConfig.LEFT_ID);
        } else if(v == mRSteeringGear || v == mRMotor) {
            openSettingDialog(Joystick4DirectionConfig.RIGHT_ID);
        }
    }

    private void updateConfig(Joystick4DirectionConfig config) {
        if(config == null || this.config.getMode() != config.getMode()) {
            return;
        }
        this.config.setControlType(config.getControlType());
        if(this.config.getMode() == Joystick4DirectionConfig.MODE_TWO_WHEEL_DRIVE) {
            this.config.setLeftId(config.getLeftId());
            this.config.setRightId(config.getRightId());
        } else {
            this.config.setLeftFrontId(config.getLeftFrontId());
            this.config.setRightFrontId(config.getRightFrontId());
            this.config.setLeftBehindId(config.getLeftBehindId());
            this.config.setRightBehindId(config.getRightBehindId());
        }
        updateView();
    }

    private void openSettingDialog(int selectedPosition) {
        if (mJoystick4DirectionView.isDownPressed()){
            return;
        }
        ControllerControlDialogFragment.newBuilder(this)
            .setSelectedPosition(selectedPosition)
            .setConfig(config)
            .setSteeringGearSelectListener(new IControllerControlSelectListener() {
                @Override
                public void onControlItemSelected(int selectedPosition, Joystick4DirectionConfig config) {
                    updateConfig(config);
                }
            })
            .build()
            .show(getSupportFragmentManager(), "select_steering_gear");
    }

    private void updateView() {
        mLSteeringGear.setVisibility(config.getControlType() != WidgetConfig.CONTROLTYPE_MOTOR ? View.VISIBLE : View.INVISIBLE);
        mRSteeringGear.setVisibility(config.getControlType() != WidgetConfig.CONTROLTYPE_MOTOR ? View.VISIBLE : View.INVISIBLE);
        mFlSteeringGear.setVisibility(config.getControlType() != WidgetConfig.CONTROLTYPE_MOTOR ? View.VISIBLE : View.INVISIBLE);
        mFrSteeringGear.setVisibility(config.getControlType() != WidgetConfig.CONTROLTYPE_MOTOR ? View.VISIBLE : View.INVISIBLE);
        mBlSteeringGear.setVisibility(config.getControlType() != WidgetConfig.CONTROLTYPE_MOTOR ? View.VISIBLE : View.INVISIBLE);
        mBrSteeringGear.setVisibility(config.getControlType() != WidgetConfig.CONTROLTYPE_MOTOR ? View.VISIBLE : View.INVISIBLE);
        mLMotor.setVisibility(config.getControlType() == WidgetConfig.CONTROLTYPE_MOTOR ? View.VISIBLE : View.INVISIBLE);
        mRMotor.setVisibility(config.getControlType() == WidgetConfig.CONTROLTYPE_MOTOR ? View.VISIBLE : View.INVISIBLE);
        mFlMotor.setVisibility(config.getControlType() == WidgetConfig.CONTROLTYPE_MOTOR ? View.VISIBLE : View.INVISIBLE);
        mFrMotor.setVisibility(config.getControlType() == WidgetConfig.CONTROLTYPE_MOTOR ? View.VISIBLE : View.INVISIBLE);
        mBlMotor.setVisibility(config.getControlType() == WidgetConfig.CONTROLTYPE_MOTOR ? View.VISIBLE : View.INVISIBLE);
        mBrMotor.setVisibility(config.getControlType() == WidgetConfig.CONTROLTYPE_MOTOR ? View.VISIBLE : View.INVISIBLE);
        if(config.getMode() == Joystick4DirectionConfig.MODE_TWO_WHEEL_DRIVE) {
            m2WheelBtn.setChecked(true);
            m4WheelViewArea.setVisibility(View.GONE);
            m2WheelViewArea.setVisibility(View.VISIBLE);
            if(config.getControlType() == WidgetConfig.CONTROLTYPE_MOTOR) {
                mLMotor.setIdValue(config.getLeftId());
                mRMotor.setIdValue(config.getRightId());
                mLTv.setText(config.getLeftId() == -1 ? null : String.format("Motor %s", String.valueOf(config.getLeftId())));
                mRTv.setText(config.getRightId() == -1 ? null : String.format("Motor %s", String.valueOf(config.getRightId())));
            } else {
                mLSteeringGear.setIdValue(config.getLeftId());
                mRSteeringGear.setIdValue(config.getRightId());
                mLTv.setText(config.getLeftId() == -1 ? null : String.format("Servo %s", String.valueOf(config.getLeftId())));
                mRTv.setText(config.getRightId() == -1 ? null : String.format("Servo %s", String.valueOf(config.getRightId())));
            }
        } else {
            m4WheelBtn.setChecked(true);
            m4WheelViewArea.setVisibility(View.VISIBLE);
            m2WheelViewArea.setVisibility(View.GONE);
            if(config.getControlType() == WidgetConfig.CONTROLTYPE_MOTOR) {
                mFlMotor.setIdValue(config.getLeftFrontId());
                mFrMotor.setIdValue(config.getRightFrontId());
                mBlMotor.setIdValue(config.getLeftBehindId());
                mBrMotor.setIdValue(config.getRightBehindId());
                mFlTv.setText(config.getLeftFrontId() == -1 ? null : String.format("Motor %s", String.valueOf(config.getLeftFrontId())));
                mFrTv.setText(config.getRightFrontId() == -1 ? null : String.format("Motor %s", String.valueOf(config.getRightFrontId())));
                mBlTv.setText(config.getLeftBehindId() == -1 ? null : String.format("Motor %s", String.valueOf(config.getLeftBehindId())));
                mBrTv.setText(config.getRightBehindId() == -1 ? null : String.format("Motor %s", String.valueOf(config.getRightBehindId())));
            } else {
                mFlSteeringGear.setIdValue(config.getLeftFrontId());
                mFrSteeringGear.setIdValue(config.getRightFrontId());
                mBlSteeringGear.setIdValue(config.getLeftBehindId());
                mBrSteeringGear.setIdValue(config.getRightBehindId());
                mFlTv.setText(config.getLeftFrontId() == -1 ? null : String.format("Servo %s", String.valueOf(config.getLeftFrontId())));
                mFrTv.setText(config.getRightFrontId() == -1 ? null : String.format("Servo %s", String.valueOf(config.getRightFrontId())));
                mBlTv.setText(config.getLeftBehindId() == -1 ? null : String.format("Servo %s", String.valueOf(config.getLeftBehindId())));
                mBrTv.setText(config.getRightBehindId() == -1 ? null : String.format("Servo %s", String.valueOf(config.getRightBehindId())));
            }
        }
    }

    private void updateMode(int mode) {
        config.setMode(mode);
        updateView();
    }

    private void initData(Bundle bundle) {
        config_4wheel.setMode(Joystick4DirectionConfig.MODE_FOUR_WHEEL_DRIVE);
        if(bundle != null) {
            config = (Joystick4DirectionConfig)bundle.getSerializable(EXTRA_JOYSTICK_CONFIG);
            if(config != null) {
                if(config.getMode() == Joystick4DirectionConfig.MODE_TWO_WHEEL_DRIVE) {
                    config_2wheel = config;
                } else {
                    config_4wheel = config;
                }
            }
        }
        if(config == null) {
            config = config_2wheel;
        }
        updateView();
//        updateMode(config.getJoystickMode());
    }

    @Override
    protected void onDestroy() {
        ControllerModeHolder.setControllerMode(ControllerModeHolder.MODE_EDIT);
        super.onDestroy();
    }
}
