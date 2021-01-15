/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.bridge;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.Surface;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.bridge.BridgeBoolean;
import com.ubtedu.bridge.BridgeResult;
import com.ubtedu.bridge.OnCallback;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.project.bridge.arguments.PhoneStateArguments;

import java.util.Arrays;
import java.util.List;

/**
 * @Author qinicy
 * @Date 2018/12/24
 **/
public class DeviceDirectionHelper implements SensorEventListener {
    private final static int DIRECTION_NONE = 0;
    private final static int DIRECTION_LEFT = 1;
    private final static int DIRECTION_UP = 2;
    private final static int DIRECTION_RIGHT = 3;
    private final static int DIRECTION_DOWN = 4;
    private static final int INTERVAL_TIME = 300;//检测时间间隔
    private static final int SPEED_SHRESHOLD = 12;//搖一搖加速度临界值
    private long mLastUpdateTime;


    private static final float CHANGE_RADIO = 2.5f;
    private static final float SHAKE_RADIO = 1.5f;
    private SensorManager mSensorManager;
    private OnCallback mCallback;
    private boolean isStartDetected;
    private Display mDisplay;
    private DeviceDirection mDeviceDirection = DeviceDirection.NONE;
    private BridgeResult mResult;
    private PhoneStateArguments mStateArguments;

    public DeviceDirectionHelper() {
        mStateArguments = new PhoneStateArguments();
        mResult = BridgeResult.SUCCESS();
    }


    public void init(Activity activity) {
        mSensorManager = (SensorManager) UKitApplication.getInstance().getSystemService(Context.SENSOR_SERVICE);
        if (activity != null) {
            mDisplay = activity.getWindowManager().getDefaultDisplay();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentTimeMillis = System.currentTimeMillis();
        long timeInterval = currentTimeMillis - mLastUpdateTime;
        if (timeInterval < INTERVAL_TIME) {
            return;
        }
        mLastUpdateTime = currentTimeMillis;
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (Math.abs(x) >= SPEED_SHRESHOLD
                || Math.abs(y) >= SPEED_SHRESHOLD
                || Math.abs(z) >= SPEED_SHRESHOLD) {
            mDeviceDirection = DeviceDirection.SWING;
            onShake(event.values);
            return;
        }

        int direction = DIRECTION_NONE;
        if (Math.abs(x) > Math.abs(y)) {
            if (x < 0) {
                direction = DIRECTION_RIGHT;
            }
            if (x > 0) {
                direction = DIRECTION_LEFT;
            }
        } else {
            if (y < 0) {
                direction = DIRECTION_UP;
            }
            if (y > 0) {
                direction = DIRECTION_DOWN;
            }
        }
        if (x > -CHANGE_RADIO && x < CHANGE_RADIO && y > -CHANGE_RADIO && y < CHANGE_RADIO) {
            direction = DIRECTION_NONE;
        }

        //LogUtil.d("direction1:" + direction);
        if (direction != DIRECTION_NONE) {
            direction = (direction + 4 - getRotationOffset()) % 4;
            if (direction == DIRECTION_NONE) {
                direction = DIRECTION_DOWN;
            }
        }
        DeviceDirection lastDeviceDirection = mDeviceDirection;
        switch (direction) {
            case DIRECTION_NONE:
                mDeviceDirection = DeviceDirection.NONE;
                break;
            case DIRECTION_LEFT:
                mDeviceDirection = DeviceDirection.LEFT;
                break;
            case DIRECTION_RIGHT:
                mDeviceDirection = DeviceDirection.RIGHT;
                break;
            case DIRECTION_UP:
                mDeviceDirection = DeviceDirection.UP;
                break;
            case DIRECTION_DOWN:
                mDeviceDirection = DeviceDirection.DOWN;
                break;
        }
        //LogUtil.d("direction2:" + direction);
        if(lastDeviceDirection != null && mDeviceDirection != lastDeviceDirection) {
            LogUtil.d(lastDeviceDirection.getValue() + " -> " + mDeviceDirection.getValue());
        }
        onDirectionChange(mDeviceDirection);
    }

    private int getRotationOffset() {
        if (mDisplay != null) {
            return mDisplay.getRotation();
        }
        return Surface.ROTATION_0;
    }

    private void onShake(float[] values) {
        mStateArguments.state = DeviceDirection.SWING.getValue();
        if (values != null) {
            mStateArguments.values = Arrays.asList(values);
        }
        mResult.data = mStateArguments;
        if (mCallback != null) {
            mCallback.onCallback(mResult);
        }
    }

    public void onDirectionChange(DeviceDirection direction) {
        mResult.complete = BridgeBoolean.FALSE();
        mStateArguments.state = direction.getValue();
        mResult.data = mStateArguments;
        if (mCallback != null) {
            mCallback.onCallback(mResult);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public void startDetect(OnCallback callback) {
        LogUtil.d("");
        stopDetect();
        this.mCallback = callback;
        isStartDetected = registerListener();
        if (!isStartDetected && mCallback != null) {
            BridgeResult result = BridgeResult.FAIL();
            result.msg = "Start detect phone state fail";
            mCallback.onCallback(result);
        }
    }

    public void stopDetect() {

        clearLastCallback();
        if (isStartDetected) {
            unRegisterListener();
        }
        isStartDetected = false;
    }

    private void clearLastCallback() {
        if (mCallback != null) {
            BridgeResult ret = BridgeResult.SUCCESS();
            PhoneStateArguments arguments = new PhoneStateArguments();
            arguments.state = DeviceDirection.NONE.getValue();
            ret.data = arguments;
            ret.msg = "Remove callback";
            mCallback.onCallback(ret);
        }
        this.mCallback = null;
    }


    private boolean registerListener() {
        if (!hasAccelerometerSensor()) {
            return false;
        }
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor == null) {
            return false;
        }

        return mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unRegisterListener() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    private boolean hasAccelerometerSensor() {
        int sensorType = Sensor.TYPE_ACCELEROMETER;
        List<Sensor> all = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        if (all == null || all.size() == 0) {
            return false;
        }
        for (Sensor s : all) {
            if (s.getType() == sensorType) {
                return true;
            }
        }
        return false;
    }

    public void release() {
        mDisplay = null;
        mSensorManager = null;
        unRegisterListener();
    }
}
