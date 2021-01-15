package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter;

import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoUkitDummyData;
import com.ubtedu.deviceconnect.libs.ukit.legacy.model.URoUkitLegacyComponentMap;

import java.io.ByteArrayOutputStream;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacySensorUpgradeDataFormatter extends URoUkitLegacyCommandFormatter<URoUkitDummyData> {

    private URoUkitLegacySensorUpgradeDataFormatter(){}

    public static final URoUkitLegacySensorUpgradeDataFormatter INSTANCE = new URoUkitLegacySensorUpgradeDataFormatter();

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
        byte[] data = request.getParameter("dataByteArray", null);
        int index = request.getParameter("indexInt", -1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] result;
        try {
            baos.write(sensorType);
            baos.write(index & 0xFF);
            baos.write((index >> 8) & 0xFF);
            baos.write(data);
            result = baos.toByteArray();
            baos.reset();
        } finally {
            baos.close();
        }
        return result;
    }

}
