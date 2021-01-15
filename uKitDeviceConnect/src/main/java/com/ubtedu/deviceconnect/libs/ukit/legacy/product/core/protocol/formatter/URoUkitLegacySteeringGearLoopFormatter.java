package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter;

import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationParamKeys;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoUkitDummyData;

import java.io.ByteArrayOutputStream;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacySteeringGearLoopFormatter extends URoUkitLegacyCommandFormatter<URoUkitDummyData> {

    private URoUkitLegacySteeringGearLoopFormatter(){}

    public static final URoUkitLegacySteeringGearLoopFormatter INSTANCE = new URoUkitLegacySteeringGearLoopFormatter();

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
        int[] ids = request.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, null);
        int speedInt = request.getParameter(URoInvocationParamKeys.Servos.PARAM_KEY_ROTATE_SPEED, 0);
        int speed = Math.abs(speedInt);
        int mode = speedInt == 0 ? 0 : speedInt > 0 ? 0x01 : 0x02;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] result;
        try {
            baos.write(ids.length);
            for(int id : ids) {
                baos.write(id);
            }
            baos.write(mode);
            baos.write((speed >> 8) & 0xff);
            baos.write(speed & 0xff);
            result = baos.toByteArray();
            baos.reset();
        } finally {
            baos.close();
        }
        return result;
    }

}
