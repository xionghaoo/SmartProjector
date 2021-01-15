package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter;

import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationParamKeys;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.legacy.model.URoUkitLegacyAngleFeedbackInfo;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacySteeringGearAngleFeedbackFormatter extends URoUkitLegacyCommandFormatter<URoUkitLegacyAngleFeedbackInfo> {

    private URoUkitLegacySteeringGearAngleFeedbackFormatter(){}

    public static final URoUkitLegacySteeringGearAngleFeedbackFormatter INSTANCE = new URoUkitLegacySteeringGearAngleFeedbackFormatter();

    @Override
    public URoResponse<URoUkitLegacyAngleFeedbackInfo> decodeResponseMessage(byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = bizData.length != 1 && bizData.length % 6 == 0;
        URoUkitLegacyAngleFeedbackInfo data = null;
        if(success) {
            data = new URoUkitLegacyAngleFeedbackInfo(bizData);
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        boolean dropPower = request.getParameter(URoInvocationParamKeys.Servos.PARAM_KEY_READBACK_POWEROFF, false);
        int id = request.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID, -1);
        byte idByte = getIdByte(new int[]{id});
        byte dropPowerByte = (byte)(dropPower ? 0x00 : 0x01);
        return new byte[] { idByte, dropPowerByte };
    }

}
