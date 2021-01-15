package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter;

import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoUkitDummyData;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacySteeringGearFixFormatter extends URoUkitLegacyCommandFormatter<URoUkitDummyData> {

    private URoUkitLegacySteeringGearFixFormatter(){}

    public static final URoUkitLegacySteeringGearFixFormatter INSTANCE = new URoUkitLegacySteeringGearFixFormatter();

    @Override
    public URoResponse<URoUkitDummyData> decodeResponseMessage(byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = bizData != null && bizData[0] == 0x00;
        URoUkitDummyData data = null;
        if(success) {
            data = URoUkitDummyData.INSTANCE;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        int srcId = request.getParameter("srcIdInt", -1);
        int destId = request.getParameter("dstIdInt", -1);
        return new byte[]{(byte)srcId, (byte)destId};
    }

}
