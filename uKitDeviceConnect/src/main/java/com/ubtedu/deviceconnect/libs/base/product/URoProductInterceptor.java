package com.ubtedu.deviceconnect.libs.base.product;

import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public interface URoProductInterceptor {
    URoRequest onInterceptRequest(URoRequest request);
    URoResponse onInterceptResponse(URoResponse response);
}
