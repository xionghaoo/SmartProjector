package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter;

import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationParamKeys;
import com.ubtedu.deviceconnect.libs.base.model.URoColor;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoUkitDummyData;

import java.io.ByteArrayOutputStream;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacyLedEmotionFormatter extends URoUkitLegacyCommandFormatter<URoUkitDummyData> {

    private URoUkitLegacyLedEmotionFormatter(){}

    public static final URoUkitLegacyLedEmotionFormatter INSTANCE = new URoUkitLegacyLedEmotionFormatter();

    @Override
    public URoResponse<URoUkitDummyData> decodeResponseMessage(byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = bizData != null && bizData[2] == 0x00;
        URoUkitDummyData data = null;
        if(success) {
            data = URoUkitDummyData.INSTANCE;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        int[] ids = request.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, null);
        byte sensorType = 0x04;
        int emotionIndex = request.getParameter(URoInvocationParamKeys.Sensor.PARAM_KEY_EFFECT_ID, -1);
        URoColor color = request.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_COLOR, null);
        int times = request.getParameter(URoInvocationParamKeys.Sensor.PARAM_KEY_TIMES, -1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] result;
        try {
            baos.write(sensorType);
            baos.write(getIdByte(ids));
            baos.write(emotionIndex);
            baos.write((times >> 8) & 0xFF);
            baos.write(times & 0xFF);
            baos.write(color.getRed());
            baos.write(color.getGreen());
            baos.write(color.getBlue());
            result = baos.toByteArray();
            baos.reset();
        } finally {
            baos.close();
        }
        return result;
    }

}
