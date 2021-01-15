package com.ubtedu.deviceconnect.libs.base.product;

import android.text.TextUtils;
import android.util.ArrayMap;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.component.URoBrightnessSensor;
import com.ubtedu.deviceconnect.libs.base.component.URoColorSensor;
import com.ubtedu.deviceconnect.libs.base.component.URoComponent;
import com.ubtedu.deviceconnect.libs.base.component.URoEnvironmentSensor;
import com.ubtedu.deviceconnect.libs.base.component.URoInfraredSensor;
import com.ubtedu.deviceconnect.libs.base.component.URoLed;
import com.ubtedu.deviceconnect.libs.base.component.URoMotor;
import com.ubtedu.deviceconnect.libs.base.component.URoServos;
import com.ubtedu.deviceconnect.libs.base.component.URoSoundSensor;
import com.ubtedu.deviceconnect.libs.base.component.URoSpeaker;
import com.ubtedu.deviceconnect.libs.base.component.URoTouchSensor;
import com.ubtedu.deviceconnect.libs.base.component.URoUltrasoundSensor;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocation;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocationSequence;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvokeHandler;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationNames;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationParamKeys;
import com.ubtedu.deviceconnect.libs.base.mission.URoMission;
import com.ubtedu.deviceconnect.libs.base.mission.URoSequenceMission;
import com.ubtedu.deviceconnect.libs.base.model.URoBatteryInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoColor;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentID;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;
import com.ubtedu.deviceconnect.libs.base.model.URoMainBoardInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoMotionModel;
import com.ubtedu.deviceconnect.libs.base.model.URoRotateMotorCommand;
import com.ubtedu.deviceconnect.libs.base.model.URoSensorData;
import com.ubtedu.deviceconnect.libs.base.model.URoSerialNumberInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoUkitAngleFeedbackInfo;
import com.ubtedu.deviceconnect.libs.base.model.event.URoBatteryUpdateEvent;
import com.ubtedu.deviceconnect.libs.base.model.event.URoComponentChangeEvent;
import com.ubtedu.deviceconnect.libs.base.model.event.URoComponentErrorEvent;
import com.ubtedu.deviceconnect.libs.base.model.event.URoConnectChangeEvent;
import com.ubtedu.deviceconnect.libs.base.model.event.URoPushMessageEvent;
import com.ubtedu.deviceconnect.libs.base.product.core.URoCoreProduct;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.router.URoLinkRouter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public abstract class URoProduct implements URoInvokeHandler {

    private final String productID;
    private final String name;
    private URoProductDelegate delegate = null;
    private final HashSet<URoMission> missions;

    public URoProduct(String name, String productID) {
        this.name = name;
        this.productID = productID;
        this.missions = new HashSet<>();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void finalize() throws Throwable {
        EventBus.getDefault().unregister(this);
        super.finalize();
    }

    public String getProductID() {
        return productID;
    }

    public String getName() {
        return name;
    }

    public boolean disconnect() {
        URoCoreProduct coreProduct = getCoreProduct();
        return URoLinkRouter.getInstance().disconnectMessageHandler(coreProduct);
    }

    public boolean isConnected() {
        URoCoreProduct coreProduct = getCoreProduct();
        return coreProduct != null && coreProduct.isConnected();
    }

    public void addMission(URoMission mission) {
        synchronized (missions) {
            missions.add(mission);
        }
    }

    private void cleanup() {
        synchronized (missions) {
            for(URoMission mission : missions) {
                mission.abort();
            }
            missions.clear();
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onConnectChangeEvent(URoConnectChangeEvent event) {
        if(!productID.equals(event.linkModel.linkID)) {
            return;
        }
        if(URoConnectStatus.CONNECTED.equals(event.status) && !isConnected()) {
            return;
        }
        if(delegate != null) {
            delegate.onConnectStatusChanged(this, event.status);
        }
        if(URoConnectStatus.DISCONNECTED.equals(event.status)) {
            cleanup();
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onComponentChangeEvent(URoComponentChangeEvent event) {
        if(!productID.equals(event.linkModel.linkID)) {
            return;
        }
        if(delegate != null) {
            delegate.onComponentChanged(this, event.components);
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onBatteryChangeEvent(URoBatteryUpdateEvent event) {
        if(!productID.equals(event.linkModel.linkID)) {
            return;
        }
        if(delegate != null) {
            delegate.onBatteryInfoUpdated(this, event.batteryInfo);
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onComponentErrorEvent(URoComponentErrorEvent event) {
        if(!productID.equals(event.linkModel.linkID)) {
            return;
        }
        if(delegate != null) {
            delegate.onReportComponentError(this, event.componentId, event.error);
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onPushMessageEvent(URoPushMessageEvent event) {
        if(!productID.equals(event.linkModel.linkID)) {
            return;
        }
        if(delegate != null) {
            delegate.onPushMessageReceived(this, event.type, event.subType, event.data);
        }
    }

    private Method findInvokeMethod(Class<?> clz, String methodName) throws Throwable {
        if(clz == null || TextUtils.isEmpty(methodName)) {
            throw new NoSuchMethodException("No such method [" + methodName + "]");
        }
        try {
            return clz.getDeclaredMethod(methodName, URoInvocation.class);
        } catch (NoSuchMethodException e) {
            return findInvokeMethod(clz.getSuperclass(), methodName);
        } catch (Throwable e) {
            throw e;
        }
    }

    @Override
    public URoError invoke(URoInvocation invocation) {
        URoError error;
        try {
            String methodName = invocation.getInvocationName();
            Method method = findInvokeMethod(this.getClass(), methodName);
            method.setAccessible(true);
            error = (URoError)method.invoke(this, invocation);
        } catch (NoSuchMethodException e) {
            error = URoError.NO_SUCH_METHOD;
        } catch (Throwable e) {
            error = URoError.UNKNOWN;
        }
        return error;
    }

    public URoLinkModel getLinkModel() {
        URoCoreProduct coreProduct = getCoreProduct();
        return coreProduct != null ? coreProduct.getLinkModel() : null;
    }

    public URoMainBoardInfo getMainBoardInfo() {
        URoCoreProduct coreProduct = getCoreProduct();
        return coreProduct != null ? coreProduct.getMainBoardInfo() : null;
    }

    public @NonNull URoSerialNumberInfo getSerialNumberInfo() {
        URoCoreProduct coreProduct = getCoreProduct();
        return coreProduct != null ? coreProduct.getSerialNumberInfo() : URoSerialNumberInfo.EMPTY_DATA;
    }

    public URoBatteryInfo getBatteryInfo() {
        URoCoreProduct coreProduct = getCoreProduct();
        return coreProduct != null ? coreProduct.getBatteryInfo() : null;
    }

    public ArrayList<URoComponent> getComponents() {
        URoCoreProduct coreProduct = getCoreProduct();
        return coreProduct != null ? coreProduct.getComponents() : new ArrayList<>();
    }

    public <T extends URoComponent> T getComponent(URoComponentType componentType, int id) {
        if(componentType == null) {
            return null;
        }
        if((URoComponentType.SERVOS.equals(componentType) && !checkServosId(id))
                || (!URoComponentType.SERVOS.equals(componentType) && !checkOtherId(id))) {
            return null;
        }
        return getComponent(new URoComponentID(componentType, id));
    }

    public <T extends URoComponent> T getComponent(URoComponentID componentID) {
        if(componentID == null) {
            return null;
        }
        ArrayList<URoComponent> components = getComponents();
        for(URoComponent component : components) {
            if(component.isMatch(componentID)) {
                return (T)component;
            }
        }
        return null;
    }

    public URoServos getServosById(int id) {
        return getComponent(URoComponentType.SERVOS, id);
    }

    public URoInfraredSensor geInfraredSensorById(int id) {
        return getComponent(URoComponentType.INFRAREDSENSOR, id);
    }

    public URoTouchSensor getTouchSensorById(int id) {
        return getComponent(URoComponentType.TOUCHSENSOR, id);
    }

    public URoUltrasoundSensor getUltrasoundSensorById(int id) {
        return getComponent(URoComponentType.ULTRASOUNDSENSOR, id);
    }

    public URoLed getLedById(int id) {
        return getComponent(URoComponentType.LED, id);
    }

    public URoColorSensor getColorSensorById(int id) {
        return getComponent(URoComponentType.COLORSENSOR, id);
    }

    public URoEnvironmentSensor getEnvironmentSensorById(int id) {
        return getComponent(URoComponentType.ENVIRONMENTSENSOR, id);
    }

    public URoBrightnessSensor getBrightnessSensorById(int id) {
        return getComponent(URoComponentType.BRIGHTNESSSENSOR, id);
    }

    public URoSoundSensor getSoundSensorById(int id) {
        return getComponent(URoComponentType.SOUNDSENSOR, id);
    }

    public URoMotor getMotorById(int id) {
        return getComponent(URoComponentType.MOTOR, id);
    }

    private <T extends URoComponent> T[] getComponentByType(URoComponentType componentType) {
        ArrayList<T> result = new ArrayList<>();
        if(componentType != null) {
            ArrayList<URoComponent> components = getComponents();
            if(components != null && !components.isEmpty()) {
                for (URoComponent component : components) {
                    if (componentType.equals(component.getComponentType())) {
                        result.add((T) component);
                    }
                }
            }
        }
        if(result.isEmpty()) {
            return null;
        }
        return result.toArray((T[])Array.newInstance(result.get(0).getClass(), result.size()));
    }

    public URoServos[] getAllServos() {
        URoComponent[] result = getComponentByType(URoComponentType.SERVOS);
        return result == null ? new URoServos[0] : (URoServos[])result;
    }

    public URoInfraredSensor[] getAllInfraredSensor() {
        URoComponent[] result = getComponentByType(URoComponentType.INFRAREDSENSOR);
        return result == null ? new URoInfraredSensor[0] : (URoInfraredSensor[])result;
    }

    public URoTouchSensor[] getAllTouchSensor() {
        URoComponent[] result = getComponentByType(URoComponentType.TOUCHSENSOR);
        return result == null ? new URoTouchSensor[0] : (URoTouchSensor[])result;
    }

    public URoUltrasoundSensor[] getAllUltrasoundSensor() {
        URoComponent[] result = getComponentByType(URoComponentType.ULTRASOUNDSENSOR);
        return result == null ? new URoUltrasoundSensor[0] : (URoUltrasoundSensor[])result;
    }

    public URoLed[] getAllLed() {
        URoComponent[] result = getComponentByType(URoComponentType.LED);
        return result == null ? new URoLed[0] : (URoLed[])result;
    }

    public URoSpeaker getSpeaker() {
        URoComponent[] result = getComponentByType(URoComponentType.SPEAKER);
        URoSpeaker[] speakers = result == null ? new URoSpeaker[0] : (URoSpeaker[])result;
        return speakers.length != 0 ? speakers[0] : null;
    }

    public URoColorSensor[] getAllColorSensor() {
        URoComponent[] result = getComponentByType(URoComponentType.COLORSENSOR);
        return result == null ? new URoColorSensor[0] : (URoColorSensor[])result;
    }

    public URoEnvironmentSensor[] getAllEnvironmentSensor() {
        URoComponent[] result = getComponentByType(URoComponentType.ENVIRONMENTSENSOR);
        return result == null ? new URoEnvironmentSensor[0] : (URoEnvironmentSensor[])result;
    }

    public URoBrightnessSensor[] getAllBrightnessSensor() {
        URoComponent[] result = getComponentByType(URoComponentType.BRIGHTNESSSENSOR);
        return result == null ? new URoBrightnessSensor[0] : (URoBrightnessSensor[])result;
    }

    public URoSoundSensor[] getAllSoundSensor() {
        URoComponent[] result = getComponentByType(URoComponentType.SOUNDSENSOR);
        return result == null ? new URoSoundSensor[0] : (URoSoundSensor[])result;
    }

    public URoMotor[] getAllMotor() {
        URoComponent[] result = getComponentByType(URoComponentType.MOTOR);
        return result == null ? new URoMotor[0] : (URoMotor[])result;
    }

    public URoProductDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(URoProductDelegate delegate) {
        this.delegate = delegate;
    }

    private URoCoreProduct getCoreProduct() {
        return URoProductManager.getInstance().getProduct(productID);
    }

    protected void sendComponentError(@NonNull URoComponentType componentType, int id, @NonNull URoError error) {
        EventBus.getDefault().post(new URoComponentErrorEvent(getCoreProduct().getLinkModel(), componentType, id, error));
    }

    protected <K> URoError sendRequest(URoRequest request, URoCompletionCallback<K> completionCallback) {
        URoError error;
        if(request == null) {
            error = URoError.INVALID;
        } else {
            URoCoreProduct coreProduct = getCoreProduct();
            if (coreProduct != null) {
                error = coreProduct.addRequest(request, request.getPriority(), request.getTag(), completionCallback);
            } else {
                error = URoError.NOT_CONNECTED;
            }
        }
        return error;
    }

    public void addInterceptor(URoProductInterceptor interceptor) {
        URoCoreProduct coreProduct = getCoreProduct();
        if (coreProduct != null) {
            coreProduct.addInterceptor(interceptor);
        }
    }

    public void removeInterceptor(URoProductInterceptor interceptor) {
        URoCoreProduct coreProduct = getCoreProduct();
        if (coreProduct != null) {
            coreProduct.removeInterceptor(interceptor);
        }
    }

    protected int restrictRange(int number, int minValue, int maxValue) {
        return Math.max(minValue, Math.min(maxValue, number));
    }

    protected float restrictRange(float number, float minValue, float maxValue) {
        return Math.max(minValue, Math.min(maxValue, number));
    }

    protected double restrictRange(double number, double minValue, double maxValue) {
        return Math.max(minValue, Math.min(maxValue, number));
    }

    protected long restrictRange(long number, long minValue, long maxValue) {
        return Math.max(minValue, Math.min(maxValue, number));
    }

    protected boolean checkServosId(int id) {
        return id >= 1 && id <= 32;
    }

    protected boolean checkOtherId(int id) {
        return id >= 1 && id <= 8;
    }

    public boolean readMainBoardInfo(URoCompletionCallback<URoMainBoardInfo> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_MAIN_BOARD_READ);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean stopRunning(URoCompletionCallback<Void> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_STOP_RUNNING);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean turnServos(ArrayMap<Integer, Integer> idAnglePairs, int time, URoCompletionCallback<Void> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_SERVOS_TURN);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_TIME, time);
        invocation.setParameter(URoInvocationParamKeys.Servos.PARAM_KEY_TURN_ID_ANGLE_PAIR, idAnglePairs);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean rotateServos(int[] ids, int speed, int time, URoCompletionCallback<Void> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_SERVOS_ROTATE);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, ids);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_TIME, time);
        invocation.setParameter(URoInvocationParamKeys.Servos.PARAM_KEY_ROTATE_SPEED, speed);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean stopServos(int[] ids, URoCompletionCallback<Void> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_SERVOS_STOP);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, ids);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean readbackServos(int[] ids, boolean powerOff, URoCompletionCallback<URoUkitAngleFeedbackInfo> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_BATCH_SERVOS_READBACK);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, ids);
        invocation.setParameter(URoInvocationParamKeys.Servos.PARAM_KEY_READBACK_POWEROFF, powerOff);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean rotateMotors(int[] ids, int speed, int time, URoCompletionCallback<Void> callback) {
        if (ids == null || ids.length == 0) {
            return false;
        }
        URoRotateMotorCommand[] commands = new URoRotateMotorCommand[ids.length];
        for (int i = 0; i < ids.length; i++) {
            commands[i] = new URoRotateMotorCommand(ids[i], speed, time);
        }
        return rotateMotors(commands, callback);
    }

    public boolean rotateMotors(URoRotateMotorCommand[] commands, URoCompletionCallback<Void> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_MOTOR_ROTATE);
        invocation.setParameter(URoInvocationParamKeys.Motor.PARAM_KEY_ROTATE_COMMAND, commands);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean stopMotors(int[] ids, URoCompletionCallback<Void> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_MOTOR_STOP);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, ids);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean takeMotion(URoMotionModel motion, URoCompletionCallback<Void> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_TAKE_MOTION);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_MOTION, motion);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean readSensors(ArrayList<URoComponentID> ids, URoCompletionCallback<URoSensorData> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_BATCH_SENSOR_READ);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, ids);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean setUltrasoundColor(int[] ids, URoColor color, URoCompletionCallback<Void> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_SENSOR_SET_ULTRASOUND_COLOR);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, ids);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_COLOR, color);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean setLEDColor(int[] ids, ArrayList<URoColor> colors, long time, URoCompletionCallback<Void> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_LED_SET_COLOR);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, ids);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_TIME, time);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_COLOR, colors);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean setLEDEffect(int effectID, int[] ids, URoColor color, int times, URoCompletionCallback<Void> callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_LED_SHOW_EFFECTS);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, ids);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_COLOR, color);
        invocation.setParameter(URoInvocationParamKeys.Sensor.PARAM_KEY_EFFECT_ID, effectID);
        invocation.setParameter(URoInvocationParamKeys.Sensor.PARAM_KEY_TIMES, times);
        invocation.setCompletionCallback(callback);
        return invocation.sendToTarget(this);
    }

    public boolean setSelfCheck(boolean enabled) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_SELF_CHECK);
        invocation.setParameter(URoInvocationParamKeys.Component.PARAM_KEY_ENABLE, enabled);
        return invocation.sendToTarget(this);
    }

    public boolean servosFaultClear() {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_SERVOS_FAULT_CLEAR);
        return invocation.sendToTarget(this);
    }

    public boolean motorFaultClear() {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_MOTOR_FAULT_CLEAR);
        return invocation.sendToTarget(this);
    }

    public boolean devFaultClear() {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_DEV_FAULT_CLEAR);
        return invocation.sendToTarget(this);
    }
    
    private URoError invokeSequence(URoInvocation invocation) {
        if(!(invocation instanceof URoInvocationSequence)) {
            return URoError.INVALID;
        }
        boolean sendTogether = ((URoInvocationSequence)invocation).isSendTogether();
        return invokeSequence(invocation, sendTogether);
    }

    protected URoError invokeSequence(URoInvocation invocation, boolean sendTogether) {
        if(!(invocation instanceof URoInvocationSequence)) {
            return URoError.INVALID;
        }
        ArrayList<URoInvocationSequence.URoInvokeParameter> invokes = ((URoInvocationSequence)invocation).getInvokes();
        boolean loop = ((URoInvocationSequence)invocation).isLoop();
        int loopTime = ((URoInvocationSequence)invocation).loopTime();
        URoSequenceMission mission = new URoSequenceMission(this, invokes, loop, loopTime, sendTogether);
        mission.setCallback(invocation.getCompletionCallback());
        mission.start();
        return URoError.SUCCESS;
    }

}
