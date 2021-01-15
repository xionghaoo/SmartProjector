package com.ubtedu.deviceconnect.libs.base.product.core;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionCallbackV2;
import com.ubtedu.deviceconnect.libs.base.URoDataWrapper;
import com.ubtedu.deviceconnect.libs.base.component.URoBrightnessSensor;
import com.ubtedu.deviceconnect.libs.base.component.URoColorSensor;
import com.ubtedu.deviceconnect.libs.base.component.URoComponent;
import com.ubtedu.deviceconnect.libs.base.component.URoEnvironmentSensor;
import com.ubtedu.deviceconnect.libs.base.component.URoInfraredSensor;
import com.ubtedu.deviceconnect.libs.base.component.URoLcd;
import com.ubtedu.deviceconnect.libs.base.component.URoLed;
import com.ubtedu.deviceconnect.libs.base.component.URoLedBelt;
import com.ubtedu.deviceconnect.libs.base.component.URoMotor;
import com.ubtedu.deviceconnect.libs.base.component.URoServos;
import com.ubtedu.deviceconnect.libs.base.component.URoSoundSensor;
import com.ubtedu.deviceconnect.libs.base.component.URoSpeaker;
import com.ubtedu.deviceconnect.libs.base.component.URoTouchSensor;
import com.ubtedu.deviceconnect.libs.base.component.URoUltrasoundSensor;
import com.ubtedu.deviceconnect.libs.base.component.URoVision;
import com.ubtedu.deviceconnect.libs.base.mission.URoProductInitMission;
import com.ubtedu.deviceconnect.libs.base.model.URoBatteryInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentID;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;
import com.ubtedu.deviceconnect.libs.base.model.URoMainBoardInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoSerialNumberInfo;
import com.ubtedu.deviceconnect.libs.base.model.event.URoBatteryUpdateEvent;
import com.ubtedu.deviceconnect.libs.base.model.event.URoComponentChangeEvent;
import com.ubtedu.deviceconnect.libs.base.model.event.URoConnectChangeEvent;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoInitStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.base.product.URoProductInterceptor;
import com.ubtedu.deviceconnect.libs.base.product.URoProductType;
import com.ubtedu.deviceconnect.libs.base.product.UroProductInitDelegate;
import com.ubtedu.deviceconnect.libs.base.product.core.message.URoLinkMessage;
import com.ubtedu.deviceconnect.libs.base.product.core.message.URoMessageHandler;
import com.ubtedu.deviceconnect.libs.base.product.core.protocol.URoProtocolHandler;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequestCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequestPriority;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequestQueueManager;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.base.router.URoLinkRouter;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public abstract class URoCoreProduct<Q extends URoRequest, S extends URoResponse, P extends URoProtocolHandler> implements URoMessageHandler, URoProductInterceptor {

    private final String productID;
    private final URoProductType productType;
//    private URoProductInfo productInfo;
    private URoMainBoardInfo mainBoardInfo;
    private URoSerialNumberInfo serialNumberInfo = URoSerialNumberInfo.INVALID;
    private URoBatteryInfo batteryInfo;
    private URoConnectStatus connectStatus;
    private URoInitStatus initStatus = URoInitStatus.PREPARE;
    private URoProtocolHandler protocolHandler;
    private URoRequestQueueManager requestQueueManager;
    private final URoLinkModel linkModel;

    private final HashMap<URoComponentID, URoComponent> components = new HashMap<>();

    private Disposable heartBeatDisposable = null;
    private Disposable batteryDisposable = null;
    private Disposable disconnectDisposable = null;

    private final HashSet<URoProductInterceptor> interceptors = new HashSet<>();

    public URoCoreProduct(String productID, URoProductType productType, URoLinkModel linkModel, URoConnectStatus connectStatus) {
        this.productID = productID;
        this.productType = productType;
        this.linkModel = linkModel;
        this.connectStatus = connectStatus;
//        this.productInfo = new URoProductInfo();
        this.protocolHandler = createProtocolHandler();
        this.requestQueueManager = createRequestQueueManager(linkModel, protocolHandler);
        this.requestQueueManager.setInterceptor(this);
        onCreate();
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

    public URoLinkModel getLinkModel() {
        return linkModel;
    }

//    protected URoProductInfo getProductInfo() {
//        return productInfo;
//    }

    protected void resetComponents() {
        synchronized (components) {
            ArrayList<URoComponent> componentList = new ArrayList<>(components.values());
            for(URoComponent component : componentList) {
                component.unregisterEvent();
            }
            components.clear();
        }
    }

    protected void addComponent(URoComponent component) {
        synchronized (components) {
            if(component == null) {
                return;
            }
            URoComponentID componentId = new URoComponentID(component.getComponentType(), component.getComponentId());
            if(components.containsKey(componentId)) {
                URoComponent removeComponent = components.remove(componentId);
                if(removeComponent != null) {
                    removeComponent.unregisterEvent();
                }
            }
            components.put(componentId, component);
            component.registerEvent();
        }
    }

    protected <T extends URoComponent> T findComponent(@NonNull URoComponentType componentType, int id) {
        synchronized (components) {
            URoComponentID componentId = new URoComponentID(componentType, id);
            return (T)components.get(componentId);
        }
    }

    protected URoComponent newComponent(URoComponentType type, int id, URoProduct product, URoError... errors) {
        return newComponent(type, id, null, product, errors);
    }

    protected URoComponent newComponent(URoComponentType type, int id, String version, URoProduct product, URoError... errors) {
        URoComponent component = null;
        String name = String.format(Locale.US, "ID-%d", id);
        switch (type) {
            case SERVOS:
                component = new URoServos(id, name, version, product);
                break;
            case INFRAREDSENSOR:
                component = new URoInfraredSensor(id, name, version, product);
                break;
            case TOUCHSENSOR:
                component = new URoTouchSensor(id, name, version, product);
                break;
            case ULTRASOUNDSENSOR:
                component = new URoUltrasoundSensor(id, name, version, product);
                break;
            case LED:
                component = new URoLed(id, name, version, product);
                break;
            case SPEAKER:
                component = new URoSpeaker(id, name, version, product);
                break;
            case COLORSENSOR:
                component = new URoColorSensor(id, name, version, product);
                break;
            case ENVIRONMENTSENSOR:
                component = new URoEnvironmentSensor(id, name, version, product);
                break;
            case BRIGHTNESSSENSOR:
                component = new URoBrightnessSensor(id, name, version, product);
                break;
            case SOUNDSENSOR:
                component = new URoSoundSensor(id, name, version, product);
                break;
            case MOTOR:
                component = new URoMotor(id, name, version, product);
                break;
            case LED_BELT:
                component = new URoLedBelt(id, name, version, product);
                break;
            case LCD:
                component = new URoLcd(id, name, version, product);
                break;
            case VISION:
                component = new URoVision(id, name, version, product);
                break;
        }
        if(errors != null && errors.length > 0) {
            for(URoError error : errors) {
                component.addError(error);
            }
        }
        return component;
    }

    public URoMainBoardInfo getMainBoardInfo() {
        return mainBoardInfo;
    }

    protected void setMainBoardInfo(URoMainBoardInfo mainBoardInfo) {
        this.mainBoardInfo = mainBoardInfo;
        if(mainBoardInfo != null) {
            URoLogUtils.e("Update MainBoardInfo:\n%s", mainBoardInfo.toString());
            resetComponents();
            for(URoComponentType type : URoComponentType.values()) {
                URoComponentInfo componentInfo = mainBoardInfo.getComponentInfo(type);
                if(componentInfo == null) {
                    continue;
                }
                ArrayList<Integer> availableIds = componentInfo.getAvailableIds();
                ArrayList<Integer> abnormalIds = componentInfo.getAbnormalIds();
                ArrayList<Integer> conflictIds = componentInfo.getConflictIds();
                for(Integer id : availableIds) {
                    URoComponent component = newComponent(type, id, componentInfo.getVersionById(id), asProduct());
                    addComponent(component);
                }
                for(Integer id : abnormalIds) {
                    URoComponent component = newComponent(type, id, componentInfo.getVersionById(id), asProduct(), URoError.ABNORMAL);
                    addComponent(component);
                }
                for(Integer id : conflictIds) {
                    URoComponent component = newComponent(type, id, componentInfo.getVersionById(id), asProduct(), URoError.CONFLICT);
                    addComponent(component);
                }
            }
            EventBus.getDefault().post(new URoComponentChangeEvent(getLinkModel(), getComponents()));
        }
    }

    public URoSerialNumberInfo getSerialNumberInfo() {
        return serialNumberInfo;
    }

    protected void setSerialNumberInfo(URoSerialNumberInfo serialNumberInfo) {
        this.serialNumberInfo = serialNumberInfo;
        if(serialNumberInfo != null) {
            URoLogUtils.e("Update SerialNumberInfo:\n%s", serialNumberInfo.toString());
        }
    }

    public URoBatteryInfo getBatteryInfo() {
        return batteryInfo;
    }

    protected void setBatteryInfo(URoBatteryInfo batteryInfo) {
        URoBatteryInfo lastBatteryInfo = this.batteryInfo;
        this.batteryInfo = batteryInfo;
        if(batteryInfo != null && !batteryInfo.equals(lastBatteryInfo)) {
            URoLogUtils.e("Update BatteryInfo:\n%s", batteryInfo.toString());
            EventBus.getDefault().post(new URoBatteryUpdateEvent(getLinkModel(), batteryInfo));
        }
    }

    public ArrayList<URoComponent> getComponents() {
        return new ArrayList<>(components.values());
    }

    @Override
    public boolean receiveMessage(URoLinkMessage message) {
        return false;
    }

    public <E extends URoProduct> E asProduct() {
        return null;
    }

    private void setConnectStatus(URoConnectStatus connectStatus) {
        this.connectStatus = connectStatus;
    }

    public URoConnectStatus getConnectStatus() {
        return connectStatus;
    }

    private void setInitStatus(URoInitStatus initStatus) {
        this.initStatus = initStatus;
    }

    protected void onInitComplete(URoInitStatus initStatus) {
    }

    public boolean isConnected() {
        return URoConnectStatus.CONNECTED.equals(connectStatus) && URoInitStatus.SUCCESS.equals(initStatus);
    }

    public void disconnect() {
        URoLinkRouter.getInstance().disconnectMessageHandler(this);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onConnectChangeEvent(URoConnectChangeEvent event) {
        if(!linkModel.equals(event.linkModel)) {
            return;
        }
        if(URoConnectStatus.DISCONNECTED.equals(event.status)) {
            EventBus.getDefault().unregister(this);
            disconnect();
        }
        setConnectStatus(event.status);
    }

    public void addInterceptor(URoProductInterceptor interceptor) {
        if(interceptor == null) {
            return;
        }
        synchronized (interceptors) {
            interceptors.add(interceptor);
        }
    }

    public void removeInterceptor(URoProductInterceptor interceptor) {
        if(interceptor == null) {
            return;
        }
        synchronized (interceptors) {
            interceptors.remove(interceptor);
        }
    }

    @Override
    public final URoRequest onInterceptRequest(URoRequest request) {
        return handleRequest(request);
    }

    @Override
    public final URoResponse onInterceptResponse(URoResponse response) {
        return handleResponse(response);
    }

    private URoResponse handleResponse(final URoResponse orgResponse) {
        if(orgResponse == null) {
            return null;
        }
        synchronized (interceptors) {
            HashSet<URoProductInterceptor> interceptors = new HashSet<>(this.interceptors);
            URoResponse response = orgResponse;
            for(URoProductInterceptor interceptor : interceptors) {
                if(response.isAborted()) {
                    break;
                }
                URoResponse newResponse = interceptor.onInterceptResponse(response);
                if(newResponse != null) {
                    response = newResponse;
                }
            }
            return Objects.equals(orgResponse, response) ? null : response;
        }
    }

    private URoRequest handleRequest(final URoRequest orgRequest) {
        if(orgRequest == null) {
            return null;
        }
        synchronized (interceptors) {
            HashSet<URoProductInterceptor> interceptors = new HashSet<>(this.interceptors);
            URoRequest request = orgRequest;
            for(URoProductInterceptor interceptor : interceptors) {
                if(request.isAborted()) {
                    break;
                }
                URoRequest newRequest = interceptor.onInterceptRequest(request);
                if(newRequest != null) {
                    request = newRequest;
                }
            }
            return Objects.equals(orgRequest, request) ? null : request;
        }
    }

    protected abstract URoProductInitMission createInitMission();
    protected abstract URoProtocolHandler createProtocolHandler();
    protected abstract URoRequestQueueManager createRequestQueueManager(URoLinkModel link, URoProtocolHandler protocolHandler);

    protected void onInitInterceptor() {}

    protected boolean isQueueContain(Object tag) {
        return requestQueueManager.isQueueContain(tag);
    }

    protected long getLastSendTime() {
        return requestQueueManager.getLastSendTime();
    }

    protected long getInitDelay() {
        return 0;
    }

    public final void init() {
        Observable.just(new URoDataWrapper<Object>(null)).delay(getInitDelay(), TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe(new Consumer<URoDataWrapper<Object>>() {
                @Override
                public void accept(URoDataWrapper<Object> warpper) throws Exception {
                    initImpl();
                }
            });
    }

    private void initImpl() {
        resetHeartBeatTimer();
        resetBatteryTimer();
        resetDisconnectTimer();
        onInitInterceptor();
        if (initDelegate != null) {
            initDelegate.onProductInit(asProduct());
            return;
        }
        URoProductInitMission initMission = createInitMission();
        if(initMission != null) {
            initMission.setCallback(new URoCompletionCallbackV2<Void>() {
                @Override
                public void onSuccess(Void result) {
                    setInitStatus(URoInitStatus.SUCCESS);
                    onInitComplete(URoInitStatus.SUCCESS);
                    EventBus.getDefault().post(new URoConnectChangeEvent(linkModel, URoConnectStatus.CONNECTED));
                    if(requestQueueManager != null) {
                        requestQueueManager.prepare();
                    }
                }

                @Override
                public void onError(URoError error) {
                    setInitStatus(URoInitStatus.FAILURE);
                    onInitComplete(URoInitStatus.FAILURE);
                    disconnect();
                }
            });
            initMission.start();
        } else {
            setInitStatus(URoInitStatus.SUCCESS);
            onInitComplete(URoInitStatus.SUCCESS);
            EventBus.getDefault().post(new URoConnectChangeEvent(linkModel, URoConnectStatus.CONNECTED));
            if(requestQueueManager != null) {
                requestQueueManager.prepare();
            }
        }
    }

    public <K> URoError addRequest(URoRequest request, URoRequestPriority priority, Object tag, URoCompletionCallback<K> completionCallback) {
        boolean result = requestQueueManager.addRequest(request, priority, tag, completionCallback != null ? new URoRequestCompletionCallback<>(completionCallback) : null);
        return result ? URoError.SUCCESS : URoError.NOT_ALLOWED;
    }

    protected void stopBatteryTimer() {
        if(batteryDisposable != null) {
            batteryDisposable.dispose();
            batteryDisposable = null;
        }
    }

    protected void resetBatteryTimer() {
        stopBatteryTimer();
        batteryDisposable = resetBatteryTimerInternal();
    }

    protected void stopHeartBeatTimer() {
        if(heartBeatDisposable != null) {
            heartBeatDisposable.dispose();
            heartBeatDisposable = null;
        }
    }

    protected void resetHeartBeatTimer() {
        stopHeartBeatTimer();
        heartBeatDisposable = resetHeartBeatTimerInternal();
    }

    protected void stopDisconnectTimer() {
        if(disconnectDisposable != null) {
            disconnectDisposable.dispose();
            disconnectDisposable = null;
        }
    }

    protected void resetDisconnectTimer() {
        stopDisconnectTimer();
        disconnectDisposable = resetDisconnectTimerInternal();
    }

    protected Disposable resetHeartBeatTimerInternal() {
        return null;
    }

    protected Disposable resetBatteryTimerInternal() {
        return null;
    }

    protected Disposable resetDisconnectTimerInternal() {
        return null;
    }

    protected void onCreate() {
    }

    public void cleanup() {
        stopBatteryTimer();
        stopHeartBeatTimer();
        stopDisconnectTimer();
        resetComponents();
        requestQueueManager.cleanup();
    }

    private UroProductInitDelegate initDelegate = null;

    public void setInitDelegate(UroProductInitDelegate initDelegate) {
        this.initDelegate = initDelegate;
    }

}
