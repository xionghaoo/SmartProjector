package com.ubtedu.deviceconnect.libs.base.product.core.message;

import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public class URoLinkMessage extends URoBaseMessage {

    private URoLinkModel source;
    private URoMessageHandler destination;

    public URoLinkMessage(byte[] encodedData, URoLinkModel source, URoMessageHandler destination) {
        super(encodedData);
        this.source = source;
        this.destination = destination;
    }

    public URoLinkModel getSource() {
        return source;
    }

    public URoMessageHandler getDestination() {
        return destination;
    }

}
