package com.ubtedu.deviceconnect.libs.base.product.core.protocol;

import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommand;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public abstract class URoProtocolFormatter<T> {

    public abstract URoResponse<T> decodeResponseMessage(URoCommand cmdType, int errorCode, byte[] bizData, byte[] rawResponse) throws Exception;
    public abstract byte[] encodeRequestMessage(URoRequest request, int sequenceId) throws Exception;

}
