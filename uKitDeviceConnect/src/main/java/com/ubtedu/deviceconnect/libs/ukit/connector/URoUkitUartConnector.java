package com.ubtedu.deviceconnect.libs.ukit.connector;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbId;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionCallbackHelper;
import com.ubtedu.deviceconnect.libs.base.URoCompletionCallbackWrap;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.URoSDK;
import com.ubtedu.deviceconnect.libs.base.connector.URoConnectMission;
import com.ubtedu.deviceconnect.libs.base.connector.URoConnector;
import com.ubtedu.deviceconnect.libs.base.connector.URoDiscoveryCallback;
import com.ubtedu.deviceconnect.libs.base.link.URoLinkType;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.product.UroProductInitDelegate;
import com.ubtedu.deviceconnect.libs.base.router.URoLinkRouter;
import com.ubtedu.deviceconnect.libs.ukit.smart.connector.URoUkitSmartUartConnectMission;
import com.ubtedu.deviceconnect.libs.ukit.smart.link.URoUkitSmartUartLink;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoUkitSmartUartLinkModel;
import com.ubtedu.deviceconnect.libs.utils.URoIoUtils;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public class URoUkitUartConnector extends URoConnector<URoUkitUartInfo> {

    private UsbManager usbManager;

    private URoConnectMission connectMission;

    static final class SingleHolder {
        static URoUkitUartConnector INSTANCE = null;
    }

    public static URoUkitUartConnector getInstance() {
        synchronized (SingleHolder.class) {
            if(SingleHolder.INSTANCE == null) {
                SingleHolder.INSTANCE = new URoUkitUartConnector();
            }
            return SingleHolder.INSTANCE;
        }
    }

    public URoUkitUartConnector() {
        this.usbManager = (UsbManager)URoSDK.getInstance().getContext().getSystemService(Context.USB_SERVICE);
    }

    @Override
    public boolean isSearching() {
        return false;
    }

    @Override
    public void startSearch(URoDiscoveryCallback<URoUkitUartInfo> callback, long timeoutMs) {
        if(callback == null) {
            return;
        }
        for(UsbDevice v : usbManager.getDeviceList().values()) {
            if(v.getVendorId() == UsbId.VENDOR_FTDI && v.getProductId() == UsbId.FTDI_FT232R) {
                callback.onDeviceFound(new URoUkitUartInfo(v));
            }
        }
        callback.onDiscoveryFinish();
    }

    @Override
    public void stopSearch() {
    }

    public void connect(long timeout, URoCompletionCallback<Void> callback) {
        for(UsbDevice v : usbManager.getDeviceList().values()) {
            if(v.getVendorId() == UsbId.VENDOR_FTDI && v.getProductId() == UsbId.FTDI_FT232R) {
                connect(new URoUkitUartInfo(v), timeout, callback);
                return;
            }
        }
        URoCompletionCallbackHelper.sendErrorCallback(URoError.NO_TARGET, callback);
    }

    @Override
    public void connect(URoUkitUartInfo connectItem, long timeout, URoCompletionCallback<Void> callback) {
        connect(connectItem, timeout, callback,null);
    }

    public void connect(URoUkitUartInfo connectItem, long timeout, URoCompletionCallback<Void> callback, UroProductInitDelegate initDelegate) {
        if(!connectReset()) {
            URoCompletionCallbackHelper.sendErrorCallback(URoError.NOT_ALLOWED, callback);
            return;
        }
        UsbDevice device = connectItem.device;
        String name = device.getProductName();
        URoCompletionCallbackWrap<?> callbackWarp = null;
        connectMission = new URoUkitSmartUartConnectMission(connectItem);
        callbackWarp = new URoCompletionCallbackWrap<UsbSerialPort>(callback) {
            @Override
            public boolean onCompleteInternal(URoCompletionResult<UsbSerialPort> result) {
                if(!result.isSuccess()) {
                    return true;
                }
                boolean resultBoolean = false;
                try {
                    UsbSerialPort serialPort = result.getData();
                    UsbDevice device = serialPort.getDevice();
                    URoLinkType linkType = URoLinkType.COM;
                    String linkName = String.format("Uart_%s", device.getSerialNumber());
                    URoUkitSmartUartLinkModel linkModel = new URoUkitSmartUartLinkModel(linkType, linkName, device);
                    URoUkitSmartUartLink link = new URoUkitSmartUartLink(linkModel, serialPort);
                    URoLinkRouter.getInstance().registerLink(link, initDelegate);
                    resultBoolean = true;
                } catch (Throwable e) {
                    URoLogUtils.e(e);
                    URoIoUtils.close(result.getData());
                }
                return resultBoolean;
            }
        };
        if(connectMission == null) {
            URoCompletionCallbackHelper.sendErrorCallback(URoError.UNKNOWN, callback);
            return;
        }
        connectMission.setTimeout(timeout);
        connectMission.setCallback(callbackWarp);
        connectMission.start();
    }

    @Override
    protected boolean connectReset() {
        if(connectMission != null && connectMission.stop()) {
            connectMission = null;
            return true;
        }
        return true;
    }

}
