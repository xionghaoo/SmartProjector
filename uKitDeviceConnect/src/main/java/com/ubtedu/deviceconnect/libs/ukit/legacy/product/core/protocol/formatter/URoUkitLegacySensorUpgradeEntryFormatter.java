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
public class URoUkitLegacySensorUpgradeEntryFormatter extends URoUkitLegacyCommandFormatter<URoUkitDummyData> {

    private URoUkitLegacySensorUpgradeEntryFormatter(){}

    public static final URoUkitLegacySensorUpgradeEntryFormatter INSTANCE = new URoUkitLegacySensorUpgradeEntryFormatter();

    private static final int MAX_FRAME_SIZE = 100;

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
        int id = request.getParameter("id", -1);
        URoComponentType componentType = request.getParameter("sensorTypeObj", null);
        int sensorType = URoUkitLegacyComponentMap.getExecutionValue(componentType);
        String frameworkName = request.getParameter("frameworkNameStr", null);
        long frameworkSize = request.getParameter("frameworkSizeLong", -1L);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] result;
        try {
            baos.write(sensorType);
            baos.write(id);
            int frameCount = (int)((frameworkSize + MAX_FRAME_SIZE - 1) / MAX_FRAME_SIZE);
            baos.write(frameCount & 0xFF);
            baos.write((frameCount >> 8) & 0xFF);
            byte[] name = frameworkName.getBytes("GBK");
            int length = Math.min(name.length, 50);
            baos.write((byte)length);
            baos.write(name, 0, length);
            result = baos.toByteArray();
            baos.reset();
        } finally {
            baos.close();
        }
        return result;
    }

}
