package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter;

import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoUkitDummyData;

import java.io.ByteArrayOutputStream;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacyBoardUpgradeEntryFormatter extends URoUkitLegacyCommandFormatter<URoUkitDummyData> {

    private URoUkitLegacyBoardUpgradeEntryFormatter(){}

    public static final URoUkitLegacyBoardUpgradeEntryFormatter INSTANCE = new URoUkitLegacyBoardUpgradeEntryFormatter();

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
        String frameworkName = request.getParameter("frameworkNameStr", null);
        long frameworkSize = request.getParameter("frameworkSizeLong", -1L);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] result;
        try {
            byte[] name = frameworkName.getBytes("GBK");
            baos.write((byte)name.length);
            baos.write(name);
            int frameCount = (int)((frameworkSize + MAX_FRAME_SIZE - 1) / MAX_FRAME_SIZE);
            baos.write(frameCount & 0xff);
            baos.write((frameCount >> 8) & 0xff);
            result = baos.toByteArray();
            baos.reset();
        } finally {
            baos.close();
        }
        return result;
    }

}
