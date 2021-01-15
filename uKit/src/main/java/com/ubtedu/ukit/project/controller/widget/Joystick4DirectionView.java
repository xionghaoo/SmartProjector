package com.ubtedu.ukit.project.controller.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoRotateMotorCommand;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.BtInvocationFactory;
import com.ubtedu.ukit.bluetooth.UKitCommandPriority;
import com.ubtedu.ukit.bluetooth.error.BluetoothCommonErrorHelper;
import com.ubtedu.ukit.bluetooth.model.SteeringGear;
import com.ubtedu.ukit.bluetooth.processor.PeripheralErrorCollector;
import com.ubtedu.ukit.menu.settings.Settings;
import com.ubtedu.ukit.project.controller.ControllerModeHolder;
import com.ubtedu.ukit.project.controller.manager.ControllerManager;
import com.ubtedu.ukit.project.controller.model.config.Joystick4DirectionConfig;
import com.ubtedu.ukit.project.controller.model.config.WidgetConfig;
import com.ubtedu.ukit.project.controller.model.define.WidgetType;
import com.ubtedu.ukit.project.controller.utils.RcLogUtils;

import java.util.ArrayList;
import java.util.Collections;

import static com.ubtedu.ukit.project.controller.utils.ControllerConstValue.DEFAULT_ANIMATION_DURATION;

/**
 * @Author naOKi
 * @Date 2018-11-29
 **/
public class Joystick4DirectionView extends ControllerWidgetView<Joystick4DirectionConfig> {

    private ImageView bg;
    private ImageView lever;

    int cx, cy, outerRadius, innerRadius, maxRadius, maxRadius2;

    int posX, posY;

    private boolean showSetupDialog = false;

    private boolean downPressed = false;

    private boolean showBluetoothNotConnectDialog = true;

    public boolean isDownPressed() {
        return downPressed;
    }

    public Joystick4DirectionView(Context context) {
        this(context, null);
    }

    public Joystick4DirectionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initJoystick4DirectionView();
    }

    private void initJoystick4DirectionView() {
        View view = inflateView(R.layout.item_project_controller_joystick_4direction);
        bg = view.findViewById(R.id.item_project_controller_joystick_bg);
        lever = view.findViewById(R.id.item_project_controller_joystick_lever);
        addWidgetView(view);
    }

    @Override
    protected Joystick4DirectionConfig newConfig() {
        return new Joystick4DirectionConfig();
    }

    @Override
    protected WidgetType getType() {
        return WidgetType.WIDGET_UKROUNDJOYSTICK;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        cx = getWidth() / 2;
        cy = getHeight() / 2;
        outerRadius = Math.min(bg.getWidth(), bg.getHeight()) / 2;
        innerRadius = Math.min(lever.getWidth(), lever.getHeight()) / 2;
        maxRadius = outerRadius - innerRadius;
        maxRadius2 = (int) Math.pow(maxRadius, 2);
    }

    private void moveLever(int posX, int posY) {
        int transX, transY;
        int distance2 = (int) (Math.pow(Math.abs(posX - cx), 2) + Math.pow(Math.abs(posY - cy), 2));
        if (distance2 > maxRadius2) {
            int distance = (int) Math.sqrt(distance2);
            if (posY == cy) {
                transY = 0;
                transX = posX > cx ? maxRadius : -maxRadius;
            } else if (posX == cx) {
                transX = 0;
                transY = posY > cy ? maxRadius : -maxRadius;
            } else {
                int modulusX = posX > cx ? 1 : -1;
                int modulusY = posY > cy ? 1 : -1;
                transX = modulusX * (maxRadius * Math.abs(cx - posX) / distance);
                transY = modulusY * (maxRadius * Math.abs(cy - posY) / distance);
            }
        } else {
            transX = posX - cx;
            transY = posY - cy;
        }
        this.posX = posX;
        this.posY = posY;
        lever.setTranslationX(transX);
        lever.setTranslationY(transY);
    }

    private void resetLever() {
        Animator[] animators = {
                ObjectAnimator.ofFloat(lever, "translationX", lever.getTranslationX(), 0),
                ObjectAnimator.ofFloat(lever, "translationY", lever.getTranslationY(), 0)
        };
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(DEFAULT_ANIMATION_DURATION);
        animatorSet.playTogether(animators);
        animatorSet.start();
    }

    @Override
    protected void onExecutionModeChanged(boolean isExecution) {
        if (isExecution) {
            showSetupDialog = false;
        } else {
            downPressed = false;
            ControllerManager.updateJoystickStatus(getWidgetConfig().id, false);
        }
        resetLever();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (ControllerManager.getBackgroundFlag()) {
            downPressed = false;
            setConditionSatisfy(false);
            return false;
        }
        if (ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_EXECUTION || ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_PREVIEW) {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                if (!BluetoothHelper.isConnected()) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        downPressed = true;
                        if (showBluetoothNotConnectDialog && ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_PREVIEW){
                            showBluetoothNotConnectDialog = false;
                            BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.BLUETOOTH_NOT_CONNECTED);
                        }
                    }
                    if (downPressed) {
                        moveLever((int) event.getX(), (int) event.getY());
                        setConditionSatisfy(false);
                    }
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (Settings.isChargingProtection() && BluetoothHelper.isCharging()) {
                        BluetoothHelper.terminateExecution();
                        BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.CHARGING_PROTECTION);
                        setConditionSatisfy(false);
                        return false;
                    }
                    if (!showSetupDialog && ControllerModeHolder.getControllerMode() != ControllerModeHolder.MODE_PREVIEW) {
                        boolean needShowDialog = false;
                        if (getWidgetConfig().getMode() == Joystick4DirectionConfig.MODE_TWO_WHEEL_DRIVE) {
                            if (getWidgetConfig().getLeftId() == -1 && getWidgetConfig().getRightId() == -1) {
                                needShowDialog = true;
                            }
                        } else {
                            if (getWidgetConfig().getRightBehindId() == -1 && getWidgetConfig().getLeftBehindId() == -1
                                    && getWidgetConfig().getRightFrontId() == -1 && getWidgetConfig().getLeftFrontId() == -1) {
                                needShowDialog = true;
                            }
                        }
                        if (needShowDialog) {
                            showSetupDialog = true;
                            BluetoothHelper.terminateExecution();
                            BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.JOYSTICK_UNSET);
                            setConditionSatisfy(false);
                            return false;
                        } else {
                            downPressed = true;
                        }
                    } else {
                        downPressed = true;
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (!downPressed) {
                        setConditionSatisfy(false);
                        return false;
                    }
                }
                moveLever((int) event.getX(), (int) event.getY());
                if (!showSetupDialog) {
                    ControllerManager.updateJoystickStatus(getWidgetConfig().id, true);
                    setConditionSatisfy(true);
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                downPressed = false;
                resetLever();
                setConditionSatisfy(false);
                ControllerManager.updateJoystickStatus(getWidgetConfig().id, false);
                return false;
            } else {
                return false;
            }
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ControllerManager.getBackgroundFlag()) {
            return false;
        }
        if (ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_EXECUTION || ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_PREVIEW) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void applyConfig(Joystick4DirectionConfig config) {
    }

    @Override
    public void resetState() {
        setConditionSatisfy(false);
        resetLever();
    }


    @Override
    protected int startExecuteV2() {
        if (ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_EDIT) {
            return -1;
        }
        if (!downPressed) {
            return -1;
        }
        int mode = getWidgetConfig().getMode();
        if (mode != Joystick4DirectionConfig.MODE_TWO_WHEEL_DRIVE
                && mode != Joystick4DirectionConfig.MODE_FOUR_WHEEL_DRIVE) {
            return -1;
        }
        if (mode == Joystick4DirectionConfig.MODE_TWO_WHEEL_DRIVE) {
            if (getWidgetConfig().getLeftId() == -1 && getWidgetConfig().getRightId() == -1) {
                return -1;
            }
        } else {
            if (getWidgetConfig().getLeftFrontId() == -1
                    && getWidgetConfig().getRightFrontId() == -1
                    && getWidgetConfig().getLeftBehindId() == -1
                    && getWidgetConfig().getRightBehindId() == -1) {
                return -1;
            }
        }
        int distance2 = (int) (Math.pow(Math.abs(posX - cx), 2) + Math.pow(Math.abs(posY - cy), 2));
        int distance = (int) Math.sqrt(distance2);
        int angle = ((int) Math.toDegrees(Math.atan2((double) cy - posY, (double)cx - posX)) + 270) % 360;
        int maxSpeedLeft, maxSpeedRight;
        float modulus = distance >= maxRadius ? 1.0f : (1.0f * distance / maxRadius);
        RcLogUtils.e("angle = %d", angle);
        SpeedRange speedRange = findSpeedRange(angle);
        if (speedRange == null) {
            return -1;
        }
        RcLogUtils.e("speedRange = %s", speedRange.toString());
        maxSpeedLeft = speedRange.getSpeedValue(angle, SpeedType.LEFT, getWidgetConfig().getControlType());
        maxSpeedRight = speedRange.getSpeedValue(angle, SpeedType.RIGHT, getWidgetConfig().getControlType());
        RcLogUtils.e("maxSpeedLeft = %d, maxSpeedRight = %d", maxSpeedLeft, maxSpeedRight);
        int speedLeft = (int) (modulus * maxSpeedLeft);
        int speedRight = (int) (modulus * maxSpeedRight);
        int delayMillis = getDelayMillis(speedLeft, speedRight);
        if (getWidgetConfig().getControlType() == WidgetConfig.CONTROLTYPE_MOTOR) {
            sendMotorRunCmd(mode, speedLeft, speedRight);
        } else {
            sendSteeringGearRunCmd(mode, speedLeft, speedRight);
        }
        return delayMillis;
    }

    private int getDelayMillis(int speedLeft, int speedRight) {
        int delayMillis;
        Long cacheTime = readCacheData("cacheTime");
        Integer cacheSpeedLeft = readCacheData("cacheSpeedLeft");
        Integer cacheSpeedRight = readCacheData("cacheSpeedRight");
        Integer cacheDelayMillis = readCacheData("cacheDelayMillis");
        if (cacheTime == null || cacheSpeedLeft == null || cacheSpeedRight == null || cacheDelayMillis == null || (cacheTime - System.currentTimeMillis()) > 1000L) {
            delayMillis = NORMAL_DELAY_TIMEMS;
        } else {
            if (Math.abs(speedLeft - cacheSpeedLeft) < MIN_MOVE_THRESHOLD && Math.abs(speedRight - cacheSpeedRight) < MIN_MOVE_THRESHOLD) {
                delayMillis = Math.min(cacheDelayMillis + 50, MAX_DELAY_TIMEMS);
            } else {
                delayMillis = NORMAL_DELAY_TIMEMS;
            }
        }
        writeCacheData("cacheSpeedLeft", speedLeft);
        writeCacheData("cacheSpeedRight", speedRight);
        writeCacheData("cacheTime", System.currentTimeMillis());
        writeCacheData("cacheDelayMillis", delayMillis);
        return delayMillis;
    }

    @Override
    protected void stopExecute() {
//        if(!downPressed) {
//            return;
//        }
        int mode = getWidgetConfig().getMode();
        if (mode != Joystick4DirectionConfig.MODE_TWO_WHEEL_DRIVE
                && mode != Joystick4DirectionConfig.MODE_FOUR_WHEEL_DRIVE) {
            return;
        }
        if (mode == Joystick4DirectionConfig.MODE_TWO_WHEEL_DRIVE) {
            if (getWidgetConfig().getLeftId() == -1 && getWidgetConfig().getRightId() == -1) {
                return;
            }
        } else {
            if (getWidgetConfig().getLeftFrontId() == -1
                    && getWidgetConfig().getRightFrontId() == -1
                    && getWidgetConfig().getLeftBehindId() == -1
                    && getWidgetConfig().getRightBehindId() == -1) {
                return;
            }
        }
        if (getWidgetConfig().getControlType() == WidgetConfig.CONTROLTYPE_MOTOR) {
            sendMotorStopCmd(mode);
        } else {
            sendSteeringGearStopCmd(mode);
        }
    }

    private void sendSteeringGearRunCmd(int mode, int speedLeft, int speedRight) {
        int[] leftIds;
        int[] rightIds;
        if (mode == Joystick4DirectionConfig.MODE_TWO_WHEEL_DRIVE) {
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.SERVOS, getWidgetConfig().getLeftId());
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.SERVOS, getWidgetConfig().getRightId());
            leftIds = new int[]{getWidgetConfig().getLeftId()};
            rightIds = new int[]{getWidgetConfig().getRightId()};
        } else {
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.SERVOS, getWidgetConfig().getLeftFrontId());
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.SERVOS, getWidgetConfig().getLeftBehindId());
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.SERVOS, getWidgetConfig().getRightFrontId());
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.SERVOS, getWidgetConfig().getRightBehindId());
            leftIds = new int[]{getWidgetConfig().getLeftFrontId(), getWidgetConfig().getLeftBehindId()};
            rightIds = new int[]{getWidgetConfig().getRightFrontId(), getWidgetConfig().getRightBehindId()};
        }
        String tag = getWidgetConfig().getId();
        BluetoothHelper.removeByTag(tag);
        ArrayMap<Integer, Integer> idSpeedMap = new ArrayMap<>();
        for (int i = 0; i < leftIds.length; i++) {
            idSpeedMap.put(leftIds[i], speedLeft);
        }
        for (int i = 0; i < rightIds.length; i++) {
            idSpeedMap.put(rightIds[i], speedRight);
        }
        BluetoothHelper.addCommand(BtInvocationFactory.rotateServos(idSpeedMap, null), UKitCommandPriority.MIDDLE, tag);
    }

    private void sendMotorRunCmd(int mode, int speedLeft, int speedRight) {
        int[] leftIds;
        int[] rightIds;
        if (mode == Joystick4DirectionConfig.MODE_TWO_WHEEL_DRIVE) {
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.MOTOR, getWidgetConfig().getLeftId());
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.MOTOR, getWidgetConfig().getRightId());
            leftIds = new int[]{getWidgetConfig().getLeftId()};
            rightIds = new int[]{getWidgetConfig().getRightId()};
        } else {
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.MOTOR, getWidgetConfig().getLeftFrontId());
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.MOTOR, getWidgetConfig().getLeftBehindId());
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.MOTOR, getWidgetConfig().getRightFrontId());
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.MOTOR, getWidgetConfig().getRightBehindId());
            leftIds = new int[]{getWidgetConfig().getLeftFrontId(), getWidgetConfig().getLeftBehindId()};
            rightIds = new int[]{getWidgetConfig().getRightFrontId(), getWidgetConfig().getRightBehindId()};
        }
        String tag = getWidgetConfig().getId();
        BluetoothHelper.removeByTag(tag);
        ArrayList<URoRotateMotorCommand> commands = new ArrayList<>();
        for (int i = 0; i < leftIds.length; i++) {
            commands.add(new URoRotateMotorCommand(leftIds[i], speedLeft, Integer.MAX_VALUE));
        }
        for (int i = 0; i < rightIds.length; i++) {
            commands.add(new URoRotateMotorCommand(rightIds[i], speedRight, Integer.MAX_VALUE));
        }
        URoRotateMotorCommand[] commandArray = new URoRotateMotorCommand[commands.size()];
        BluetoothHelper.addCommand(BtInvocationFactory.rotateMotors(commands.toArray(commandArray), null), UKitCommandPriority.MIDDLE, tag);
    }

    private void sendSteeringGearStopCmd(int mode) {
        int ids[];
        if (mode == Joystick4DirectionConfig.MODE_TWO_WHEEL_DRIVE) {
            ids = new int[]{getWidgetConfig().getLeftId(), getWidgetConfig().getRightId()};
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.SERVOS, getWidgetConfig().getLeftId());
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.SERVOS, getWidgetConfig().getRightId());
        } else {
            ids = new int[]{getWidgetConfig().getLeftFrontId(), getWidgetConfig().getRightFrontId(), getWidgetConfig().getLeftBehindId(), getWidgetConfig().getRightBehindId()};
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.SERVOS, getWidgetConfig().getLeftFrontId());
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.SERVOS, getWidgetConfig().getLeftBehindId());
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.SERVOS, getWidgetConfig().getRightFrontId());
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.SERVOS, getWidgetConfig().getRightBehindId());
        }
        String tag = getWidgetConfig().getId();
        BluetoothHelper.removeByTag(tag);
        BluetoothHelper.addCommand(BtInvocationFactory.stopServos(ids), UKitCommandPriority.MIDDLE, tag);
    }

    private void sendMotorStopCmd(int mode) {
        int ids[];
        if (mode == Joystick4DirectionConfig.MODE_TWO_WHEEL_DRIVE) {
            ids = new int[]{getWidgetConfig().getLeftId(), getWidgetConfig().getRightId()};
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.MOTOR, getWidgetConfig().getLeftId());
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.MOTOR, getWidgetConfig().getRightId());
        } else {
            ids = new int[]{getWidgetConfig().getLeftFrontId(), getWidgetConfig().getRightFrontId(), getWidgetConfig().getLeftBehindId(), getWidgetConfig().getRightBehindId()};
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.MOTOR, getWidgetConfig().getLeftFrontId());
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.MOTOR, getWidgetConfig().getLeftBehindId());
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.MOTOR, getWidgetConfig().getRightFrontId());
            PeripheralErrorCollector.getInstance().appendUsedPeripheral(URoComponentType.MOTOR, getWidgetConfig().getRightBehindId());
        }
        String tag = getWidgetConfig().getId();
        BluetoothHelper.removeByTag(tag);
        BluetoothHelper.addCommand(BtInvocationFactory.stopMotors(ids), UKitCommandPriority.MIDDLE, tag);
    }

    private SpeedRange findSpeedRange(int angle) {
        for (SpeedRange speedRange : speedRanges) {
            if (speedRange.belongTo(angle)) {
                return speedRange;
            }
        }
        return null;
    }

    private static class SpeedInflection implements Comparable<SpeedInflection> {
        private int angle;
        private int sLeftSpeed;
        private int sRightSpeed;
        private int mLeftSpeed;
        private int mRightSpeed;

        public SpeedInflection(int angle, int sLeftSpeed, int sRightSpeed, int mLeftSpeed, int mRightSpeed) {
            this.angle = angle;
            this.sLeftSpeed = sLeftSpeed;
            this.sRightSpeed = sRightSpeed;
            this.mLeftSpeed = mLeftSpeed;
            this.mRightSpeed = mRightSpeed;
        }

        @Override
        public int compareTo(@NonNull SpeedInflection o) {
            return Integer.compare(angle, o.angle);
        }

        @Override
        public String toString() {
            return "SpeedInflection{" +
                    "angle=" + angle +
                    ", sLeftSpeed=" + sLeftSpeed +
                    ", sRightSpeed=" + sRightSpeed +
                    ", mLeftSpeed=" + mLeftSpeed +
                    ", mRightSpeed=" + mRightSpeed +
                    '}';
        }
    }

    private static class SpeedRange {
        private SpeedInflection angleFrom;
        private SpeedInflection angleTo;

        public SpeedRange(SpeedInflection angleFrom, SpeedInflection angleTo) {
            this.angleFrom = angleFrom;
            this.angleTo = angleTo;
        }

        public boolean belongTo(int angle) {
            angle = angle % 360;
            return angle >= angleFrom.angle && angle < angleTo.angle;
        }

        public int getSpeedValue(int angle, SpeedType type, int controlType) {
            int fromValue, toValue;
            int fromAngle, toAngle;
            angle = angle % 360;
            if (controlType == WidgetConfig.CONTROLTYPE_MOTOR) {
                if (SpeedType.LEFT.equals(type)) {
                    fromValue = angleFrom.mLeftSpeed;
                    toValue = angleTo.mLeftSpeed;
                } else {
                    fromValue = angleFrom.mRightSpeed;
                    toValue = angleTo.mRightSpeed;
                }
            } else {
                if (SpeedType.LEFT.equals(type)) {
                    fromValue = angleFrom.sLeftSpeed;
                    toValue = angleTo.sLeftSpeed;
                } else {
                    fromValue = angleFrom.sRightSpeed;
                    toValue = angleTo.sRightSpeed;
                }
            }
            fromAngle = angleFrom.angle;
            toAngle = angleTo.angle;
            return fromValue + ((toValue - fromValue) * (angle - fromAngle) / (toAngle - fromAngle));
        }

        @Override
        public String toString() {
            return "SpeedRange{" +
                    "angleFrom=" + angleFrom +
                    ", angleTo=" + angleTo +
                    '}';
        }
    }

    private enum SpeedType {
        LEFT, RIGHT
    }

    private static final ArrayList<SpeedRange> speedRanges = new ArrayList<>();

	static {
		int STEERINGGEAR_FAST_C = SteeringGear.Speed.VF.getValue();
		int STEERINGGEAR_MIDDLE_C = SteeringGear.Speed.M.getValue();
		int STEERINGGEAR_SLOW_C = SteeringGear.Speed.VS.getValue();
		int MOTOR_FAST_C = 140;
		int MOTOR_MIDDLE_C = 80;
		int MOTOR_SLOW_C = 40;
		int STEERINGGEAR_FAST_R = -SteeringGear.Speed.VF.getValue();
		int STEERINGGEAR_MIDDLE_R = -SteeringGear.Speed.M.getValue();
		int STEERINGGEAR_SLOW_R = -SteeringGear.Speed.VS.getValue();
		int MOTOR_FAST_R = -140;
		int MOTOR_MIDDLE_R = -80;
		int MOTOR_SLOW_R = -40;
        ArrayList<SpeedInflection> inflections = new ArrayList<>();
        inflections.add(new SpeedInflection(0, STEERINGGEAR_FAST_R, STEERINGGEAR_FAST_C, MOTOR_FAST_R, MOTOR_FAST_C));
        inflections.add(new SpeedInflection(5, STEERINGGEAR_FAST_R, STEERINGGEAR_FAST_C, MOTOR_FAST_R, MOTOR_FAST_C));
        inflections.add(new SpeedInflection(30, STEERINGGEAR_FAST_R, STEERINGGEAR_MIDDLE_C, MOTOR_FAST_R, MOTOR_MIDDLE_C));
        inflections.add(new SpeedInflection(85, STEERINGGEAR_FAST_R, STEERINGGEAR_FAST_R, MOTOR_FAST_R, MOTOR_FAST_R));
        inflections.add(new SpeedInflection(95, STEERINGGEAR_FAST_R, STEERINGGEAR_FAST_R, MOTOR_FAST_R, MOTOR_FAST_R));
        inflections.add(new SpeedInflection(135, STEERINGGEAR_MIDDLE_C, STEERINGGEAR_FAST_R, MOTOR_MIDDLE_C, MOTOR_FAST_R));
        inflections.add(new SpeedInflection(175, STEERINGGEAR_FAST_C, STEERINGGEAR_FAST_R, MOTOR_FAST_C, MOTOR_FAST_R));
        inflections.add(new SpeedInflection(185, STEERINGGEAR_FAST_C, STEERINGGEAR_FAST_R, MOTOR_FAST_C, MOTOR_FAST_R));
        inflections.add(new SpeedInflection(225, STEERINGGEAR_FAST_C, STEERINGGEAR_MIDDLE_R, MOTOR_FAST_C, MOTOR_MIDDLE_R));
        inflections.add(new SpeedInflection(265, STEERINGGEAR_FAST_C, STEERINGGEAR_FAST_C, MOTOR_FAST_C, MOTOR_FAST_C));
        inflections.add(new SpeedInflection(275, STEERINGGEAR_FAST_C, STEERINGGEAR_FAST_C, MOTOR_FAST_C, MOTOR_FAST_C));
        inflections.add(new SpeedInflection(330, STEERINGGEAR_MIDDLE_R, STEERINGGEAR_FAST_C, MOTOR_MIDDLE_R, MOTOR_FAST_C));
        inflections.add(new SpeedInflection(355, STEERINGGEAR_FAST_R, STEERINGGEAR_FAST_C, MOTOR_FAST_R, MOTOR_FAST_C));
        inflections.add(new SpeedInflection(360, STEERINGGEAR_FAST_R, STEERINGGEAR_FAST_C, MOTOR_FAST_R, MOTOR_FAST_C));
//		inflections.add(new SpeedInflection(0, -SteeringGearLoop.Speed.VF.getValue(), SteeringGearLoop.Speed.VF.getValue(), -140, 140));
//		inflections.add(new SpeedInflection(30, -SteeringGearLoop.Speed.VF.getValue(), SteeringGearLoop.Speed.M.getValue(), -140, 40));
//		inflections.add(new SpeedInflection(90, -SteeringGearLoop.Speed.VF.getValue(), -SteeringGearLoop.Speed.VF.getValue(), -100, -100));
//		inflections.add(new SpeedInflection(120, 0, 0, 0, 0));
//		inflections.add(new SpeedInflection(135, SteeringGearLoop.Speed.VF.getValue(), -SteeringGearLoop.Speed.M.getValue(), 140, -40));
//		inflections.add(new SpeedInflection(180, SteeringGearLoop.Speed.VF.getValue(), -SteeringGearLoop.Speed.VF.getValue(), 140, -140));
//		inflections.add(new SpeedInflection(225, SteeringGearLoop.Speed.M.getValue(), -SteeringGearLoop.Speed.VF.getValue(), 40, -140));
//		inflections.add(new SpeedInflection(240, 0, 0, 0, 0));
//		inflections.add(new SpeedInflection(270, SteeringGearLoop.Speed.VF.getValue(), SteeringGearLoop.Speed.VF.getValue(), 100, 100));
//		inflections.add(new SpeedInflection(330, -SteeringGearLoop.Speed.M.getValue(), SteeringGearLoop.Speed.VF.getValue(), -40, 140));
//		inflections.add(new SpeedInflection(360, -SteeringGearLoop.Speed.VF.getValue(), SteeringGearLoop.Speed.VF.getValue(), -140, 140));
        Collections.sort(inflections);
        for (int i = 0; i < (inflections.size() - 1); i++) {
            SpeedInflection angleFrom = inflections.get(i);
            SpeedInflection angleTo = inflections.get(i + 1);
            speedRanges.add(new SpeedRange(angleFrom, angleTo));
        }
    }

}
