package com.ubtedu.deviceconnect.libs.base.product.core.message;

import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;
import com.ubtedu.deviceconnect.libs.base.product.core.URoCoreProduct;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public class URoProductMessage extends URoBaseMessage {

    private URoCoreProduct source;
    private URoLinkModel destination;

    public URoProductMessage(byte[] encodedData, URoCoreProduct source, URoLinkModel destination) {
        super(encodedData);
        this.source = source;
        this.destination = destination;
    }

    public URoCoreProduct getSource() {
        return source;
    }

    public URoLinkModel getDestination() {
        return destination;
    }

}
