package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter;

import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.legacy.model.URoUkitLegacyComponentMap;
import com.ubtedu.deviceconnect.libs.ukit.legacy.model.URoUkitLegacySpeakerInfoData;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacySensorSpeakerInfoFormatter extends URoUkitLegacyCommandFormatter<URoUkitLegacySpeakerInfoData> {

    private URoUkitLegacySensorSpeakerInfoFormatter(){}

    public static final URoUkitLegacySensorSpeakerInfoFormatter INSTANCE = new URoUkitLegacySensorSpeakerInfoFormatter();

    @Override
    public URoResponse<URoUkitLegacySpeakerInfoData> decodeResponseMessage(byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = bizData.length > 9 && URoComponentType.SPEAKER.equals(URoUkitLegacyComponentMap.getTypeByValue(bizData[0])) && bizData[1] == 0 && bizData[2] == 0x01;
        URoUkitLegacySpeakerInfoData data = null;
        if(success) {
            data = new URoUkitLegacySpeakerInfoData(bizData);
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        return new byte[] {0x08, 0x01, 0x00};
    }

}
