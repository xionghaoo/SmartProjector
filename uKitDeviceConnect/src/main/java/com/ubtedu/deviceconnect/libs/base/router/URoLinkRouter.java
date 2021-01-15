package com.ubtedu.deviceconnect.libs.base.router;

import com.ubtedu.deviceconnect.libs.base.link.URoLink;
import com.ubtedu.deviceconnect.libs.base.link.URoLinkDelegate;
import com.ubtedu.deviceconnect.libs.base.link.URoLinkManager;
import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;
import com.ubtedu.deviceconnect.libs.base.model.event.URoConnectChangeEvent;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoProductManager;
import com.ubtedu.deviceconnect.libs.base.product.URoProductType;
import com.ubtedu.deviceconnect.libs.base.product.UroProductInitDelegate;
import com.ubtedu.deviceconnect.libs.base.product.core.URoCoreProduct;
import com.ubtedu.deviceconnect.libs.base.product.core.message.URoLinkMessage;
import com.ubtedu.deviceconnect.libs.base.product.core.message.URoMessageHandler;
import com.ubtedu.deviceconnect.libs.base.product.core.message.URoProductMessage;
import com.ubtedu.deviceconnect.libs.base.product.core.message.URoUnknownMessageHandler;
import com.ubtedu.deviceconnect.libs.ukit.legacy.model.URoUkitLegacyBtLinkModel;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.URoCoreUkitLegacy;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoUkitSmartBtLinkModel;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoUkitSmartUartLinkModel;
import com.ubtedu.deviceconnect.libs.ukit.smart.product.core.URoCoreUkitSmart;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public class URoLinkRouter implements URoLinkDelegate {

    private static URoLinkRouter instance;

    public static URoLinkRouter getInstance() {
        synchronized (URoLinkRouter.class) {
            if(instance == null) {
                instance = new URoLinkRouter();
            }
            return instance;
        }
    }

    private ConcurrentHashMap<URoLink, URoMessageHandler> linksMap;
    private URoLinkManager linkManager;

    private URoLinkRouter() {
        linksMap = new ConcurrentHashMap<>();
        linkManager = new URoLinkManager();
    }

    public boolean registerLink(URoLink link, UroProductInitDelegate initDelegate) {
        if(link == null || linkManager.isExistLink(link.model())) {
            return false;
        }
        if(!linkManager.addLink(link)) {
            return false;
        }
        link.setLinkDelegate(this);
        URoMessageHandler handler;
        URoLinkModel linkModel = link.model();
        if(linkModel instanceof URoUkitSmartBtLinkModel || linkModel instanceof URoUkitSmartUartLinkModel) {
            handler = URoProductManager.getInstance().getProduct(linkModel.linkID);
            if(handler == null) {
                URoCoreProduct product = new URoCoreUkitSmart(linkModel.linkID, URoProductType.UKIT_SMART, linkModel, URoConnectStatus.CONNECTING);
                product.setInitDelegate(initDelegate);
                boolean result = createPath(link, product);
                URoProductManager.getInstance().addProduct(product);
                return result;
            }else{
                return createPath(link, handler);
            }
        } else if(linkModel instanceof URoUkitLegacyBtLinkModel) {
            handler = URoProductManager.getInstance().getProduct(linkModel.linkID);
            if(handler == null) {
                URoCoreProduct product = new URoCoreUkitLegacy(linkModel.linkID, URoProductType.UKIT_LEGACY, linkModel, URoConnectStatus.CONNECTING);
                product.setInitDelegate(initDelegate);
                boolean result = createPath(link, product);
                URoProductManager.getInstance().addProduct(product);
                return result;
            }else{
                return createPath(link, handler);
            }
        } else {
            handler = new URoUnknownMessageHandler();
            return createPath(link, handler);
        }
    }

    private void unregisterLink(URoLink link) {
        if(link == null || !linkManager.isExistLink(link.model())) {
            return;
        }
        URoMessageHandler messageHandler = linksMap.get(link);
        linksMap.remove(link);
        linkManager.removeLink(link);
        link.setLinkDelegate(null);
        disconnectLink(link);
        if(messageHandler != null && !linksMap.containsValue(messageHandler)) {
            disconnectMessageHandler(messageHandler);
        }
    }

    public boolean createPath(URoLink link, URoMessageHandler messageHandler) {
        linksMap.put(link, messageHandler);
        return true;
    }

    public boolean transmitMessage(URoProductMessage message) {
        if(message == null) {
            return false;
        }
        URoLink link = findLink(message.getDestination());
        if(link == null) {
            return false;
        }
        boolean result = false;
        try {
            link.write(message.data(), 0, message.length());
            result = true;
        } catch (Exception e) {
            URoLogUtils.e(e);
        }
        return result;
    }

    public void disconnectLink(URoLink link) {
        link.disconnect();
    }

    public boolean disconnectMessageHandler(URoMessageHandler messageHandler) {
        if(messageHandler == null) {
            return false;
        }
        boolean result = false;
        HashMap<URoLink, URoMessageHandler> map = new HashMap<>(linksMap);
        for(Map.Entry<URoLink, URoMessageHandler> entry : map.entrySet()) {
            if(messageHandler.equals(entry.getValue())) {
                disconnectLink(entry.getKey());
                result = true;
            }
        }
        if(messageHandler instanceof URoCoreProduct) {
            ((URoCoreProduct)messageHandler).cleanup();
            URoProductManager.getInstance().removeProduct((URoCoreProduct)messageHandler);
        }
        return result;
    }

    public void replaceMessageHandler(URoMessageHandler oldHandler, URoMessageHandler newHandler) {
        if(oldHandler == null || newHandler == null) {
            return;
        }
        HashMap<URoLink, URoMessageHandler> map = new HashMap<>(linksMap);
        for(Map.Entry<URoLink, URoMessageHandler> entry : map.entrySet()) {
            if(oldHandler.equals(entry.getValue())) {
                linksMap.put(entry.getKey(), newHandler);
            }
        }
    }

    private void remove(URoMessageHandler messageHandler) {
        disconnectMessageHandler(messageHandler);
    }

    private ArrayList<URoLink> findLink(URoMessageHandler messageHandler) {
        ArrayList<URoLink> result = new ArrayList<>();
        if(messageHandler == null) {
            return result;
        }
        HashMap<URoLink, URoMessageHandler> map = new HashMap<>(linksMap);
        for(Map.Entry<URoLink, URoMessageHandler> entry : map.entrySet()) {
            if(messageHandler.equals(entry.getValue())) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    public URoLink findLink(URoLinkModel linkModel) {
        if(linkModel == null) {
            return null;
        }
        HashMap<URoLink, URoMessageHandler> map = new HashMap<>(linksMap);
        for(URoLink link : map.keySet()) {
            if(linkModel.equals(link.model())) {
                return link;
            }
        }
        return null;
    }

    public ArrayList<URoLinkModel> findLinkModel(URoMessageHandler messageHandler) {
        ArrayList<URoLinkModel> result = new ArrayList<>();
        if(messageHandler == null) {
            return result;
        }
        HashMap<URoLink, URoMessageHandler> map = new HashMap<>(linksMap);
        for(Map.Entry<URoLink, URoMessageHandler> entry : map.entrySet()) {
            if(messageHandler.equals(entry.getValue())) {
                result.add(entry.getKey().model());
            }
        }
        return result;
    }

    @Override
    public void onConnectivityChange(URoLink link, URoConnectStatus status) {
        if(URoConnectStatus.DISCONNECTED.equals(status)) {
            unregisterLink(link);
            EventBus.getDefault().post(new URoConnectChangeEvent(link.model(), status));
        } else if(URoConnectStatus.CONNECTED.equals(status)) {
            EventBus.getDefault().post(new URoConnectChangeEvent(link.model(), status));
        }
    }

    @Override
    public void onReceiveData(URoLink link, byte[] data) {
        if(link == null || data == null || data.length == 0) {
            return;
        }
        URoMessageHandler handler = linksMap.get(link);
        if(handler == null) {
            return;
        }
        URoLinkMessage message = new URoLinkMessage(data, link.model(), handler);
        handler.receiveMessage(message);
    }

}
