package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter;

import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoUkitDummyData;

import java.io.ByteArrayOutputStream;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacySteeringGearUpgradeEntryFormatter extends URoUkitLegacyCommandFormatter<URoUkitDummyData> {

    private URoUkitLegacySteeringGearUpgradeEntryFormatter(){}

    public static final URoUkitLegacySteeringGearUpgradeEntryFormatter INSTANCE = new URoUkitLegacySteeringGearUpgradeEntryFormatter();

    private static final int MAX_FRAME_SIZE = 100;

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
        int id = request.getParameter("id", -1);
        long frameworkSize = request.getParameter("frameworkSizeLong", -1L);
        int crc32 = request.getParameter("crc32Int", 0);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] result;
        try {
            baos.write(id);
            int frameCount = (int)((frameworkSize + MAX_FRAME_SIZE - 1) / MAX_FRAME_SIZE);
            baos.write(frameCount & 0xFF);
            baos.write((frameCount >> 8) & 0xFF);
            baos.write(crc32 & 0xFF);
            baos.write((crc32 >> 8) & 0xFF);
            baos.write((crc32 >> 16) & 0xFF);
            baos.write((crc32 >> 24) & 0xFF);
            result = baos.toByteArray();
            baos.reset();
        } finally {
            baos.close();
        }
        return result;
    }

}
