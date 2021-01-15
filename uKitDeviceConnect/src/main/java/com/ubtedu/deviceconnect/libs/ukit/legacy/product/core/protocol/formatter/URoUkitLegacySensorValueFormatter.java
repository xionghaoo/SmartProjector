package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter;

import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationParamKeys;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentID;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoUkitDummyData;
import com.ubtedu.deviceconnect.libs.ukit.legacy.model.URoUkitLegacyComponentMap;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacySensorValueFormatter extends URoUkitLegacyCommandFormatter<URoUkitDummyData> {

    private URoUkitLegacySensorValueFormatter(){}

    public static final URoUkitLegacySensorValueFormatter INSTANCE = new URoUkitLegacySensorValueFormatter();

    @Override
    public URoResponse<URoUkitDummyData> decodeResponseMessage(byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success;
        if(bizData.length < 6) {
            success = false;
        } else {
            if(bizData[2] == 0x01 && bizData[0] == bizData[1]) {
                success = bizData[4] == 0;
            } else {
                success = true;
            }
        }
        URoUkitDummyData data = null;
        if(success) {
            data = URoUkitDummyData.INSTANCE;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        URoComponentID[] ids = request.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, null);
        HashMap<URoComponentType, HashSet<Integer>> parameters = new HashMap<>();
        for(URoComponentID id : ids) {
            URoComponentType componentType = id.getComponentType();
            HashSet<Integer> list = parameters.get(componentType);
            if(list == null) {
                list = new HashSet<>();
                parameters.put(componentType, list);
            }
            list.add(Integer.valueOf(id.getId()));
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] requestData;
        try {
            for(Map.Entry<URoComponentType, HashSet<Integer>> entry : parameters.entrySet()) {
                URoComponentType componentType = entry.getKey();
                HashSet<Integer> idSet = entry.getValue();
                baos.write(URoUkitLegacyComponentMap.getExecutionValue(componentType));
                baos.write(getIdByte(idSet));
            }
            byte[] bizData = baos.toByteArray();
            if(bizData.length == 0) {
                return null;
            }
            requestData = new byte[bizData.length + 1];
            requestData[0] = (byte)(bizData.length / 2);
            System.arraycopy(bizData, 0, requestData, 1, bizData.length);
        } finally {
            baos.close();
        }
        return requestData;
    }

}
