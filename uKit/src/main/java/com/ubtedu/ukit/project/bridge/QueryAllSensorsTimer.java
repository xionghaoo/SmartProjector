/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.bridge;


import com.ubtedu.bridge.BridgeBoolean;
import com.ubtedu.bridge.BridgeResult;
import com.ubtedu.bridge.OnCallback;
import com.ubtedu.deviceconnect.libs.base.component.URoComponent;
import com.ubtedu.deviceconnect.libs.base.component.URoEnvironmentSensor;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoComponentErrorListener;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentID;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.model.URoSubscribeSensorInfo;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.BtInvocationFactory;
import com.ubtedu.ukit.bluetooth.UkitDeviceCompact;
import com.ubtedu.ukit.bluetooth.model.SensorValueData;
import com.ubtedu.ukit.project.Workspace;
import com.ubtedu.ukit.project.vo.ModelInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author qinicy
 * @Date 2018/12/4
 **/
public class QueryAllSensorsTimer {
    public final static int DEFAULT_INTERVAL = 501;
    public final static int MIN_INTERVAL = 200;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private int mInterval;
    private OnCallback mCallback;
    private boolean isInSchedule;
    //    private ModelInfo modelInfo;
    private CopyOnWriteArrayList<URoComponentID> mSensors = new CopyOnWriteArrayList<>();
    private ArrayList<URoComponentID> mAllSensors = new ArrayList<>();
    private Map<URoComponentID, Integer> mSensorErrorCount = new HashMap<>();

    private static QueryAllSensorsTimer mInstance;

    private URoComponentErrorListener componentErrorListener = new URoComponentErrorListener() {
        @Override
        public void onReportComponentError(URoProduct product, URoComponentID component, URoError error) {
            if (component == null || !isQuerySensor(component.getComponentType())) {
                return;
            }
            //todo 传感器错误区分
            Integer count = mSensorErrorCount.get(component);
            if (count != null) {
                mSensorErrorCount.put(component, ++count);
                if (count == 3) {
                    mSensors.remove(component);
                }
            } else {
                mSensorErrorCount.put(component, 1);
            }
        }
    };

    public static QueryAllSensorsTimer getInstance() {
        synchronized (QueryAllSensorsTimer.class) {
            if (mInstance == null) {
                mInstance = new QueryAllSensorsTimer(DEFAULT_INTERVAL, null);
            }
            return mInstance;
        }
    }

    private QueryAllSensorsTimer(int interval, OnCallback callback) {
        synchronized (QueryAllSensorsTimer.class) {
            mInterval = interval;
            if (mInterval <= MIN_INTERVAL) {
                mInterval = MIN_INTERVAL;
            }
            mCallback = callback;
        }
    }

//    public void setSensorInvalid(URoComponentType sensorType, int id) {
//        if (modelInfo == null) {
//            return;
//        }
//        if (sensorType == null || id < 1 || id > 8) {
//            return;
//        }
//        switch (sensorType) {
//            case INFRAREDSENSOR:
//                modelInfo.infrared.remove(Integer.valueOf(id));
//                break;
//            case TOUCHSENSOR:
//                modelInfo.touch.remove(Integer.valueOf(id));
//                break;
//            case BRIGHTNESSSENSOR:
//                modelInfo.brightness.remove(Integer.valueOf(id));
//                break;
//            case SOUNDSENSOR:
//                modelInfo.decibel.remove(Integer.valueOf(id));
//                break;
//            case COLORSENSOR:
//                modelInfo.color.remove(Integer.valueOf(id));
//                break;
//            case ENVIRONMENTSENSOR:
//                modelInfo.humidity.remove(Integer.valueOf(id));
//                break;
//            case ULTRASOUNDSENSOR:
//                modelInfo.ultrasound.remove(Integer.valueOf(id));
//                break;
//            default:
//                break;
//        }
//    }

    public void setCallback(OnCallback mCallback) {
        synchronized (QueryAllSensorsTimer.class) {
            this.mCallback = mCallback;
        }
    }

    private void query(boolean firstTimes) {
//        BtCmdApi.Common.readSensorValue();
//        BluetoothCommand cmd = SensorValue.newInstance(modelInfo);
        if(!UkitDeviceCompact.getInstance().isDefaultDeviceUkitSmart()) {
            BluetoothHelper.addCommand(BtInvocationFactory.readSensors(mSensors));
        }
        synchronized (QueryAllSensorsTimer.class) {
            if (!firstTimes && mCallback != null) {
                BridgeResult result = BridgeResult.SUCCESS();
                result.data = getAllSensorValue();
                result.complete = BridgeBoolean.FALSE();
                if (mCallback != null) {
                    mCallback.onCallback(result);
                }
            }
        }
    }

    public boolean isInSchedule() {
        synchronized (QueryAllSensorsTimer.class) {
            return isInSchedule;
        }
    }

    public void start() {
        synchronized (QueryAllSensorsTimer.class) {
            if (isInSchedule) {
                return;
            }
//            if (Workspace.getInstance() != null && Workspace.getInstance().getProject() != null && Workspace.getInstance().getProject().modelInfo != null) {
//                modelInfo = ModelInfo.newInstance(Workspace.getInstance().getProject().modelInfo);
//            }
//            if (modelInfo == null) {
//                return;
//            }
//            if (delay <= 0) {
//                delay = 0;
//            }
            mAllSensors.addAll(getQuerySensors());
            mSensors.addAll(mAllSensors);
            if (mSensors.isEmpty()) {
                return;
            }

            isInSchedule = true;
            if(UkitDeviceCompact.getInstance().isDefaultDeviceUkitSmart()) {
                startSubscribeSensor();
            } else {
                BluetoothHelper.addComponentErrorListener(componentErrorListener);
            }
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                boolean firstTimes = true;

                @Override
                public void run() {
                    query(firstTimes);
                    if (firstTimes) {
                        firstTimes = false;
                    }
                }
            };
            mTimer.schedule(mTimerTask, 0, mInterval);
        }
    }

    public void stop() {
        synchronized (QueryAllSensorsTimer.class) {
            if (!isInSchedule) {
                return;
            }
            isInSchedule = false;
            if(UkitDeviceCompact.getInstance().isDefaultDeviceUkitSmart()) {
                stopSubscribeSensor();
            } else {
                BluetoothHelper.removeComponentErrorListener(componentErrorListener);
            }
            mCallback = null;
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;

            mSensors.clear();
            mSensorErrorCount.clear();
            mAllSensors.clear();
        }
    }

    private SensorValueData getAllSensorValue() {
        SensorValueData result = new SensorValueData();
        result.setBatteryValue(BluetoothHelper.isLowBattery());
        for (int i = 0; i < mAllSensors.size(); i++) {
            URoComponentType type = mAllSensors.get(i).getComponentType();
            int id = mAllSensors.get(i).getId();
            switch (type) {
                case INFRAREDSENSOR:
                    result.addInfraredValue(id, BluetoothHelper.getSensorValue(type, id));
                    break;
                case TOUCHSENSOR:
                    result.addTouchValue(id, BluetoothHelper.getSensorValue(type, id));
                    break;
                case ULTRASOUNDSENSOR:
                    result.addUltrasoundValue(id, BluetoothHelper.getSensorValue(type, id));
                    break;
                case COLORSENSOR:
                    result.addColorValue(id, BluetoothHelper.getSensorValue(type, id));
                    break;
                case ENVIRONMENTSENSOR:
                    URoEnvironmentSensor sensor = (URoEnvironmentSensor) BluetoothHelper.getSensor(URoComponentType.ENVIRONMENTSENSOR, id);
                    if (sensor != null && sensor.getSensorValue() != null) {
                        result.addHumitureValue(id, sensor.getSensorValue().getHumidity(), (int) sensor.getSensorValue().getTemperature());
                    } else {
                        //外设不存在，也要返回无效数据
                        result.addHumitureValue(id, SensorValueData.INVALID_VALUE, SensorValueData.INVALID_VALUE);
                    }
                    break;
                case BRIGHTNESSSENSOR:
                    result.addEnvLightValue(id, BluetoothHelper.getSensorValue(type, id));
                    break;
                case SOUNDSENSOR:
                    result.addSoundValue(id, BluetoothHelper.getSensorValue(type, id));
                    break;
            }
        }
        return result;
    }

    private ArrayList<URoComponentID> getQuerySensors() {
        ArrayList<URoComponentID> sensors = new ArrayList<>();
        ModelInfo modelInfo = null;
        if (Workspace.getInstance() != null && Workspace.getInstance().getProject() != null && Workspace.getInstance().getProject().modelInfo != null) {
            modelInfo = ModelInfo.newInstance(Workspace.getInstance().getProject().modelInfo);
        }
        if (modelInfo == null) {
            return sensors;
        }
        if (modelInfo.infrared != null && modelInfo.infrared.size() > 0) {
            for (int i = 0; i < modelInfo.infrared.size(); i++) {
                URoComponentID componentID = new URoComponentID(URoComponentType.INFRAREDSENSOR, modelInfo.infrared.get(i));
                sensors.add(componentID);
            }
        }
        if (modelInfo.touch != null && modelInfo.touch.size() > 0) {
            for (int i = 0; i < modelInfo.touch.size(); i++) {
                URoComponentID componentID = new URoComponentID(URoComponentType.TOUCHSENSOR, modelInfo.touch.get(i));
                sensors.add(componentID);
            }
        }
        if (modelInfo.ultrasound != null && modelInfo.ultrasound.size() > 0) {
            for (int i = 0; i < modelInfo.ultrasound.size(); i++) {
                URoComponentID componentID = new URoComponentID(URoComponentType.ULTRASOUNDSENSOR, modelInfo.ultrasound.get(i));
                sensors.add(componentID);
            }
        }
        if (modelInfo.color != null && modelInfo.color.size() > 0) {
            for (int i = 0; i < modelInfo.color.size(); i++) {
                URoComponentID componentID = new URoComponentID(URoComponentType.COLORSENSOR, modelInfo.color.get(i));
                sensors.add(componentID);
            }
        }
        if (modelInfo.humidity != null && modelInfo.humidity.size() > 0) {
            for (int i = 0; i < modelInfo.humidity.size(); i++) {
                URoComponentID componentID = new URoComponentID(URoComponentType.ENVIRONMENTSENSOR, modelInfo.humidity.get(i));
                sensors.add(componentID);
            }
        }
        if (modelInfo.brightness != null && modelInfo.brightness.size() > 0) {
            for (int i = 0; i < modelInfo.brightness.size(); i++) {
                URoComponentID componentID = new URoComponentID(URoComponentType.BRIGHTNESSSENSOR, modelInfo.brightness.get(i));
                sensors.add(componentID);
            }
        }
        if (modelInfo.decibel != null && modelInfo.decibel.size() > 0) {
            for (int i = 0; i < modelInfo.decibel.size(); i++) {
                URoComponentID componentID = new URoComponentID(URoComponentType.SOUNDSENSOR, modelInfo.decibel.get(i));
                sensors.add(componentID);
            }
        }
        return sensors;
    }

    private boolean isQuerySensor(URoComponentType type) {
        boolean isQuerySensor = false;
        switch (type) {
            case INFRAREDSENSOR:
            case TOUCHSENSOR:
            case ULTRASOUNDSENSOR:
            case COLORSENSOR:
            case ENVIRONMENTSENSOR:
            case BRIGHTNESSSENSOR:
            case SOUNDSENSOR:
                isQuerySensor = true;
                break;
        }
        return isQuerySensor;
    }

    private void startSubscribeSensor() {
        URoProduct product = UkitDeviceCompact.getInstance().getDefaultDevice();
        if (product == null) {
            return;
        }
        ArrayList<URoComponent> components = product.getComponents();
        if(components == null || components.isEmpty()) {
            return;
        }
        ArrayList<URoSubscribeSensorInfo> subscribeInfos = new ArrayList<>();
        for(URoComponent component : components) {
            if(!isQuerySensor(component.getComponentType())) {
                continue;
            }
            URoSubscribeSensorInfo subscribeInfo = new URoSubscribeSensorInfo();
            subscribeInfo.setId(new URoComponentID(component.getComponentType(), component.getComponentId()));
            subscribeInfo.setMode(URoSubscribeSensorInfo.URoSubscribeSensorMode.TIME);
            subscribeInfo.addValue(mInterval);
            subscribeInfos.add(subscribeInfo);
        }
        if(subscribeInfos.isEmpty()) {
            return;
        }
        BluetoothHelper.addCommand(BtInvocationFactory.subscribeSensors(subscribeInfos.toArray(new URoSubscribeSensorInfo[subscribeInfos.size()]), null));
    }

    private void stopSubscribeSensor() {
        URoProduct product = UkitDeviceCompact.getInstance().getDefaultDevice();
        if (product == null) {
            return;
        }
        ArrayList<URoComponent> components = product.getComponents();
        if(components == null || components.isEmpty()) {
            return;
        }
        ArrayList<URoSubscribeSensorInfo> subscribeInfos = new ArrayList<>();
        for(URoComponent component : components) {
            if(!isQuerySensor(component.getComponentType())) {
                continue;
            }
            URoSubscribeSensorInfo subscribeInfo = new URoSubscribeSensorInfo();
            subscribeInfo.setId(new URoComponentID(component.getComponentType(), component.getComponentId()));
            subscribeInfo.setMode(URoSubscribeSensorInfo.URoSubscribeSensorMode.NONE);
            subscribeInfos.add(subscribeInfo);
        }
        if(subscribeInfos.isEmpty()) {
            return;
        }
        BluetoothHelper.addCommand(BtInvocationFactory.subscribeSensors(subscribeInfos.toArray(new URoSubscribeSensorInfo[subscribeInfos.size()]), null));
    }

}