package com.ubtedu.deviceconnect.libs.base.product.core.message;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public class URoMessageHandlerFactory {

    private static URoMessageHandlerFactory instance;

    public static URoMessageHandlerFactory getInstance() {
        synchronized (URoMessageHandlerFactory.class) {
            if(instance == null) {
                instance = new URoMessageHandlerFactory();
            }
            return instance;
        }
    }

    public URoMessageHandler make(URoLinkMessage message) {
        // unsupported now
        return null;
    }

}
