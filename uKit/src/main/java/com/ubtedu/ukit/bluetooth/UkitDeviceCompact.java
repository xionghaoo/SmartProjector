package com.ubtedu.ukit.bluetooth;

import android.text.TextUtils;

import com.ubtedu.deviceconnect.libs.base.component.URoComponent;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoBatteryChangeListener;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoComponentErrorListener;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoConnectStatusChangeListener;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoPushMessageReceivedListener;
import com.ubtedu.deviceconnect.libs.base.model.URoBatteryInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentID;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.model.URoMainBoardInfo;
import com.ubtedu.deviceconnect.libs.base.model.event.URoPushMessageData;
import com.ubtedu.deviceconnect.libs.base.model.event.URoPushMessageType;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.base.product.URoProductDelegate;
import com.ubtedu.deviceconnect.libs.base.product.URoProductManager;
import com.ubtedu.deviceconnect.libs.base.product.URoProductManagerDelegate;
import com.ubtedu.deviceconnect.libs.ukit.smart.product.URoUkitSmart;
import com.ubtedu.deviceconnect.libs.utils.URoListenerGroup;

import java.util.ArrayList;
import java.util.HashSet;

public class UkitDeviceCompact extends URoProductDelegate implements URoProductManagerDelegate {

    public static final int UKIT_LEGACY_DEVICE = 1;
    public static final int UKIT_SMART_DEVICE = 2;

    private HashSet<URoProduct> devices;
    private URoProduct device;
    private boolean isVerified=false;
    private URoListenerGroup<URoConnectStatusChangeListener, Object[]> mConnectStatusChangeListeners;
    private URoListenerGroup<URoBatteryChangeListener, Object[]> mBatteryChangeListeners;
    private URoListenerGroup<URoComponentErrorListener, Object[]> mComponentErrorListeners;
    private URoListenerGroup<URoPushMessageReceivedListener, Object[]> mPushMessageReceivedListeners;

    private UkitDeviceCompact() {
        devices = new HashSet<>();
        mConnectStatusChangeListeners = new URoListenerGroup<URoConnectStatusChangeListener, Object[]>() {
            @Override
            public void notifyListener(URoConnectStatusChangeListener listener, Object[] data) {
                URoProduct product = (URoProduct)data[0];
                URoConnectStatus connectStatus = (URoConnectStatus)data[1];
                if(listener != null) {
                    listener.onConnectStatusChanged(product, connectStatus);
                }
            }
        };
        mBatteryChangeListeners = new URoListenerGroup<URoBatteryChangeListener, Object[]>() {
            @Override
            public void notifyListener(URoBatteryChangeListener listener, Object[] data) {
                URoProduct product = (URoProduct)data[0];
                URoBatteryInfo batteryInfo = (URoBatteryInfo)data[1];
                if(listener != null) {
                    listener.onBatteryInfoUpdated(product, batteryInfo);
                }
            }
        };
        mComponentErrorListeners = new URoListenerGroup<URoComponentErrorListener, Object[]>() {
            @Override
            public void notifyListener(URoComponentErrorListener listener, Object[] data) {
                URoProduct product = (URoProduct)data[0];
                URoComponentID component = (URoComponentID)data[1];
                URoError error = (URoError)data[2];
                if(listener != null) {
                    listener.onReportComponentError(product, component, error);
                }
            }
        };
        mPushMessageReceivedListeners = new URoListenerGroup<URoPushMessageReceivedListener, Object[]>() {
            @Override
            public void notifyListener(URoPushMessageReceivedListener listener, Object[] data) {
                URoProduct product = (URoProduct)data[0];
                URoPushMessageType type = (URoPushMessageType)data[1];
                int subType = (int)data[2];
                URoPushMessageData msgData = (URoPushMessageData)data[3];
                if(listener != null) {
                    listener.onPushMessageReceived(product, type, subType, msgData);
                }
            }
        };
    }

    private static final Object lock = new Object();

    private static UkitDeviceCompact instance = null;

    public static UkitDeviceCompact getInstance() {
        synchronized (UkitDeviceCompact.class) {
            if(instance == null) {
                instance = new UkitDeviceCompact();
                instance.devices.addAll(URoProductManager.getInstance().getAllProduct());
            }
            return instance;
        }
    }

    @Override
    public void onProductAdd(URoProduct product) {
        synchronized (lock) {
            if(devices.contains(product)) {
                return;
            }
            if(device != null) {
                //不是同一个设备，连上新的则断开旧的
                device.setDelegate(null);
                if(!TextUtils.equals(device.getProductID(), product.getProductID())) {
                    device.disconnect();
                }
            }
            device = product;
            isVerified=false;
            product.setDelegate(this);
            if(product.isConnected()) {
                //对已处于连接状态的product，直接广播已连接状态
                mConnectStatusChangeListeners.sendNotifyToListener(new Object[]{product, URoConnectStatus.CONNECTED});
            }
            devices.add(product);
        }
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    @Override
    public void onProductRemove(URoProduct product) {
        synchronized (lock) {
            if(!devices.contains(product)) {
                return;
            }
//            product.setDelegate(null);
            devices.remove(product);
//            if(device != null && TextUtils.equals(device.getProductID(), product.getProductID())) {
//                device = null;
//            }
        }
    }

    public URoProduct getDefaultDevice() {
//        if(devices != null && devices.size() == 1) {
//            return devices.iterator().next();
//        }
//        return null;
        return device != null && device.isConnected() ? device : null;
    }

    public synchronized URoMainBoardInfo getMainBoardInfo() {
        URoProduct device = getDefaultDevice();
        if (device == null) {
            return null;
        }
        URoMainBoardInfo mainBoardInfo = device.getMainBoardInfo();
        if (mainBoardInfo != null) {
            combineAvailableIds(mainBoardInfo, URoComponentType.SERVOS, false);
            combineAvailableIds(mainBoardInfo, URoComponentType.INFRAREDSENSOR, false);
            combineAvailableIds(mainBoardInfo, URoComponentType.TOUCHSENSOR, false);
            combineAvailableIds(mainBoardInfo, URoComponentType.SPEAKER, false);
            combineAvailableIds(mainBoardInfo, URoComponentType.LED, false);
            combineAvailableIds(mainBoardInfo, URoComponentType.LED_BELT, false);
            combineAvailableIds(mainBoardInfo, URoComponentType.MOTOR);
            combineAvailableIds(mainBoardInfo, URoComponentType.ULTRASOUNDSENSOR);
            combineAvailableIds(mainBoardInfo, URoComponentType.ENVIRONMENTSENSOR);
            combineAvailableIds(mainBoardInfo, URoComponentType.BRIGHTNESSSENSOR);
            combineAvailableIds(mainBoardInfo, URoComponentType.SOUNDSENSOR);
            combineAvailableIds(mainBoardInfo, URoComponentType.COLORSENSOR);
        }
        return mainBoardInfo;
    }

    private void combineAvailableIds(URoMainBoardInfo mainBoardInfo, URoComponentType componentType) {
        combineAvailableIds(mainBoardInfo, componentType, true);
    }

    private void combineAvailableIds(URoMainBoardInfo mainBoardInfo, URoComponentType componentType, boolean clearConflict) {
        if (mainBoardInfo == null) {
            return;
        }
        URoComponentInfo<Integer> componentInfo = mainBoardInfo.getComponentInfo(componentType);
        if (componentInfo == null
                || componentInfo.getConflictIds().isEmpty()
                || componentInfo.getAvailableIds().containsAll(componentInfo.getConflictIds())) {
            return;
        }
        for (Integer id : componentInfo.getConflictIds()) {
            if (!componentInfo.getAvailableIds().contains(id)) {
                componentInfo.addAvailableId(id);
            }
        }
        if (clearConflict) {
            componentInfo.ignoreConflict();
        }
    }

    public boolean isDefaultDeviceUkitSmart() {
        return getDefaultDevice() instanceof URoUkitSmart;
    }

    @Override
    public final void onBatteryInfoUpdated(URoProduct product, URoBatteryInfo batteryInfo) {
        mBatteryChangeListeners.sendNotifyToListener(new Object[]{product, batteryInfo});
    }

    @Override
    public final void onReportComponentError(URoProduct product, URoComponentID component, URoError error) {
        mComponentErrorListeners.sendNotifyToListener(new Object[]{product, component, error});
    }

    @Override
    public final void onConnectStatusChanged(URoProduct product, URoConnectStatus connectStatus) {
        mConnectStatusChangeListeners.sendNotifyToListener(new Object[]{product, connectStatus});
    }

    @Override
    public void onPushMessageReceived(URoProduct product, URoPushMessageType type, int subType, URoPushMessageData data) {
        mPushMessageReceivedListeners.sendNotifyToListener(new Object[]{product, type, subType, data});
    }

    @Override
    public void onComponentChanged(URoProduct product, ArrayList<URoComponent> components) {
        // empty
    }

    public void addConnectStatusChangeListener(URoConnectStatusChangeListener listener) {
        mConnectStatusChangeListeners.addListener(listener);
    }

    public void removeConnectStatusChangeListener(URoConnectStatusChangeListener listener) {
        mConnectStatusChangeListeners.removeListener(listener);
    }

    public void addBatteryChangeListener(URoBatteryChangeListener listener) {
        mBatteryChangeListeners.addListener(listener);
    }

    public void removeBatteryChangeListener(URoBatteryChangeListener listener) {
        mBatteryChangeListeners.removeListener(listener);
    }

    public void addComponentErrorListener(URoComponentErrorListener listener) {
        mComponentErrorListeners.addListener(listener);
    }

    public void removeComponentErrorListener(URoComponentErrorListener listener) {
        mComponentErrorListeners.removeListener(listener);
    }

    public void addPushMessageReceivedListener(URoPushMessageReceivedListener listener) {
        mPushMessageReceivedListeners.addListener(listener);
    }

    public void removePushMessageReceivedListener(URoPushMessageReceivedListener listener) {
        mPushMessageReceivedListeners.removeListener(listener);
    }

}
