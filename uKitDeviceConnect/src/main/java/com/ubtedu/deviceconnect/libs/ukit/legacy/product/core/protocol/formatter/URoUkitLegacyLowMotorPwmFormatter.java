package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter;

import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoUkitDummyData;

import java.io.ByteArrayOutputStream;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacyLowMotorPwmFormatter extends URoUkitLegacyCommandFormatter<URoUkitDummyData> {

    private URoUkitLegacyLowMotorPwmFormatter(){}

    public static final URoUkitLegacyLowMotorPwmFormatter INSTANCE = new URoUkitLegacyLowMotorPwmFormatter();

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
        int pwm = request.getParameter("pwmInt", -1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] result;
        try {
            byte pwmLow = (byte)(pwm & 0xFF);
            byte pwmHigh = (byte)((pwm >> 8) & 0xFF);
            byte[] data = new byte[] {pwmHigh, pwmLow};
            byte index = 0x01;
            byte idByte;
            int count;
            if(id == 0x00) {
                idByte = (byte)0xFF;
                count = 8;
            } else {
                idByte = (byte)(1 << (id - 1));
                count = 1;
            }
            baos.write(index);
            baos.write(idByte);
            for(int i = 0; i < count; i++) {
                baos.write(data);
            }
            result = baos.toByteArray();
            baos.reset();
        } finally {
            baos.close();
        }
        return result;
    }

}
