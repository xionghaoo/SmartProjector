package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter;

import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationParamKeys;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoUkitDummyData;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacySteeringGearAngleFormatter extends URoUkitLegacyCommandFormatter<URoUkitDummyData> {

    private URoUkitLegacySteeringGearAngleFormatter(){}

    public static final URoUkitLegacySteeringGearAngleFormatter INSTANCE = new URoUkitLegacySteeringGearAngleFormatter();

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
        Map<Integer, Integer> parameters = request.getParameter(URoInvocationParamKeys.Servos.PARAM_KEY_TURN_ID_ANGLE_PAIR, new HashMap<>());
        int rotateTime = request.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_TIME, 0);
        int paddingTime = request.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_PADDING_TIME, 0);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] result;
        try {
            int ids = getIdInt(parameters.keySet());
            baos.write((ids >> 24) & 0xFF);
            baos.write((ids >> 16) & 0xFF);
            baos.write((ids >> 8) & 0xFF);
            baos.write((ids >> 0) & 0xFF);
            Integer[] idArrays = parameters.keySet().toArray(new Integer[parameters.size()]);
            Arrays.sort(idArrays);
            for(int id : idArrays) {
                baos.write(parameters.get(Integer.valueOf(id)) & 0xFF);
            }
            int actionRotateTime = rotateTime / 20;
            baos.write(actionRotateTime & 0xFF);
            int actionTotalTime = rotateTime + paddingTime;
            actionTotalTime = Math.min(actionTotalTime, 0xFFFF);
            actionTotalTime = Math.max(actionTotalTime, 0);
            baos.write((actionTotalTime >> 8) & 0xFF);
            baos.write(actionTotalTime & 0xFF);
            result = baos.toByteArray();
            baos.reset();
        } finally {
            baos.close();
        }
        return result;

    }

}
