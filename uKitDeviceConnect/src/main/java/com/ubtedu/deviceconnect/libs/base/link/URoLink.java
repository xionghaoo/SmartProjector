package com.ubtedu.deviceconnect.libs.base.link;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public abstract class URoLink {

    private URoLinkType linkType;
    private String linkName;
    private URoLinkReceiveMode receiveMode;
    private URoLinkDelegate delegate;
    private URoConnectStatus connectStatus;

    private Handler handler;
    private URoLinkModel model;

    private long transferTimeout = 0;

    protected URoLink(@NonNull URoLinkModel model) {
        this.model = model;
        this.linkType = model.linkType;
        this.linkName = model.linkName;
        this.handler = new Handler(Looper.getMainLooper());
        receiveMode = setupLinkReceiveMode();
        initReceiveTunnel();
    }

    private final Runnable TRANSFER_TIMEOUT_CALLBACK = () -> {
        disconnect();
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof URoLink)) return false;

        URoLink uRoLink = (URoLink) o;

        if (linkType != uRoLink.linkType) return false;
        return linkName.equals(uRoLink.linkName);
    }

    @Override
    public int hashCode() {
        int result = linkType.hashCode();
        result = 31 * result + linkName.hashCode();
        return result;
    }

    public void setLinkDelegate(URoLinkDelegate delegate) {
        this.delegate = delegate;
    }

//    protected void setLinkReceiveMode(URoLinkReceiveMode receiveMode) {
//        this.receiveMode = receiveMode;
//    }

    protected URoLinkReceiveMode setupLinkReceiveMode() {
        return URoLinkReceiveMode.INITIATIVE;
    }

    protected URoLinkReceiveMode getLinkReceiveMode() {
        return receiveMode;
    }

    public final URoLinkModel model() {
        return model;
    }

    public URoConnectStatus getConnectStatus() {
        return connectStatus;
    }

    public final boolean matching(URoLinkModel model) {
        return this.model.equals(model);
    }

    public abstract boolean isConnected();

    protected abstract void writeInternal(byte[] b, int off, int len) throws Exception;
    protected abstract int readInternal(byte[] b, int off, int len) throws Exception;
    protected abstract boolean disconnectInternal() throws Exception;

    public final void write(byte[] b, int off, int len) throws Exception {
        writeInternal(b, off, len);
    }

    public final int read(byte[] b, int off, int len) throws Exception {
        resetTransferTimeout();
        return readInternal(b, off, len);
    }

    public final boolean disconnect() {
        boolean result = false;
        try {
            boolean oldState = isConnected();
            result = disconnectInternal();
            boolean newState = isConnected();
            if (oldState != newState) {
                connectStatus = URoConnectStatus.DISCONNECTED;
                onConnectivityChange(this, connectStatus);
            }
        } catch (Exception e) {
            URoLogUtils.e(e);
        }
        return result;
    }

    protected Handler getHandler() {
        return handler;
    }

    private void resetTransferTimeout() {
        clearTransferTimeout();
        if(transferTimeout > 0) {
            getHandler().postDelayed(TRANSFER_TIMEOUT_CALLBACK, transferTimeout);
        }
    }

    private void clearTransferTimeout() {
        getHandler().removeCallbacks(TRANSFER_TIMEOUT_CALLBACK);
    }

    protected void initReceiveTunnel() {
    }

    protected void onConnectivityChange(URoLink link, URoConnectStatus status) {
        if (delegate != null) {
            delegate.onConnectivityChange(link, status);
        }
    }

    protected void onReceiveData(URoLink link, byte[] data) {
        if (delegate != null) {
            delegate.onReceiveData(link, data);
        }
    }

}
