package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter;

import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationParamKeys;
import com.ubtedu.deviceconnect.libs.base.model.URoColor;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoUkitDummyData;
import com.ubtedu.deviceconnect.libs.ukit.legacy.model.URoUkitLegacyComponentMap;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacyLedFaceFormatter extends URoUkitLegacyCommandFormatter<URoUkitDummyData> {

    private URoUkitLegacyLedFaceFormatter(){}

    public static final URoUkitLegacyLedFaceFormatter INSTANCE = new URoUkitLegacyLedFaceFormatter();

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
        URoComponentType componentType = request.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_TYPE, null);
        if(URoComponentType.ULTRASOUNDSENSOR.equals(componentType)) {
            return encodeRequestUltrasoundMessage(componentType, request);
        } else {
            return encodeRequestLedMessage(componentType, request);
        }
    }

    private byte[] encodeRequestLedMessage(URoComponentType componentType, URoRequest request) throws Exception {
        int[] ids = request.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, null);
        ArrayList<URoColor> colors = request.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_COLOR, null);
        long time = request.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_TIME, Long.MIN_VALUE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] result;
        try {
            baos.write(URoUkitLegacyComponentMap.getExecutionValue(componentType));
            baos.write(getIdByte(ids));
            int t = (int)(time / 100);
            baos.write((byte) t);
            baos.write(8);
            for (int i = 0; i < colors.size(); i++) {
                URoColor color = colors.get(i);
                baos.write(0x01 << i);
                baos.write(color.getRed());
                baos.write(color.getGreen());
                baos.write(color.getBlue());
            }
            result = baos.toByteArray();
            baos.reset();
        } finally {
            baos.close();
        }
        return result;
    }

    private byte[] encodeRequestUltrasoundMessage(URoComponentType componentType, URoRequest request) throws Exception {
        int[] ids = request.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, null);
        URoColor color = request.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_COLOR, null);
        int time = request.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_TIME, 0);
        int mode = color.getColor() == 0 ? 0 : 1;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] result;
        try {
            baos.write(URoUkitLegacyComponentMap.getExecutionValue(componentType));
            baos.write(getIdByte(ids));
            baos.write(color.getRed());
            baos.write(color.getGreen());
            baos.write(color.getBlue());
            baos.write(mode);
            baos.write(time == 0xFFFF ? 0xFF : 0);
            baos.write((time >> 8) & 0xFF);
            baos.write(time & 0xFF);
            result = baos.toByteArray();
            baos.reset();
        } finally {
            baos.close();
        }
        return result;
    }

}
