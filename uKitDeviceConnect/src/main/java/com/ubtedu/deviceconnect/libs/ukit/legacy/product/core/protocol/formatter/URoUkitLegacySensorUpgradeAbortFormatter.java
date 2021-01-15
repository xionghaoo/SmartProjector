package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter;

import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoUkitDummyData;
import com.ubtedu.deviceconnect.libs.ukit.legacy.model.URoUkitLegacyComponentMap;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacySensorUpgradeAbortFormatter extends URoUkitLegacyCommandFormatter<URoUkitDummyData> {

    private URoUkitLegacySensorUpgradeAbortFormatter(){}

    public static final URoUkitLegacySensorUpgradeAbortFormatter INSTANCE = new URoUkitLegacySensorUpgradeAbortFormatter();

    @Override
    public URoResponse<URoUkitDummyData> decodeResponseMessage(byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = bizData != null && bizData.length == 2 && bizData[1] == 0x00;
        URoUkitDummyData data = null;
        if(success) {
            data = URoUkitDummyData.INSTANCE;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        URoComponentType componentType = request.getParameter("sensorTypeObj", null);
        int sensorType = URoUkitLegacyComponentMap.getExecutionValue(componentType);
        return new byte[] { (byte)sensorType, 0x00 };
    }

}
