package com.ubtedu.deviceconnect.libs.base.product.core.message;

import com.ubtedu.deviceconnect.libs.base.router.URoLinkRouter;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public class URoUnknownMessageHandler implements URoMessageHandler {

    @Override
    public boolean receiveMessage(URoLinkMessage message) {
        URoMessageHandler newMessageHandler = URoMessageHandlerFactory.getInstance().make(message);
        URoLinkRouter.getInstance().replaceMessageHandler(this, newMessageHandler);
        return false;
    }

}
