package com.ubtedu.deviceconnect.libs.base.product;

import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommand;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public abstract class URoProductRequestInterceptor implements URoProductInterceptor {

    public final URoResponse onInterceptResponse(URoResponse response) {
        return null;
    }

    public final URoRequest onInterceptRequest(URoRequest request) {
        return onInterceptRequest(request.getCmd(), request);
    }

    public abstract URoRequest onInterceptRequest(URoCommand cmd, URoRequest request);

}
