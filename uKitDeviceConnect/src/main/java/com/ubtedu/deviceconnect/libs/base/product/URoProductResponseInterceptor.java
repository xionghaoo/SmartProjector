package com.ubtedu.deviceconnect.libs.base.product;

import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommand;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public abstract class URoProductResponseInterceptor implements URoProductInterceptor {

    public final URoResponse onInterceptResponse(URoResponse response) {
        return onInterceptResponse(response.getCmd(), response);
    }

    public final URoRequest onInterceptRequest(URoRequest request) {
        return null;
    }

    public abstract URoResponse onInterceptResponse(URoCommand cmd, URoResponse response);

}
