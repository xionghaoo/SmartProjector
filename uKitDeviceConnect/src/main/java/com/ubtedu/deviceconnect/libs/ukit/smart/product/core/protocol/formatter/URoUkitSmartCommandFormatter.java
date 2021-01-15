package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.ubtedu.deviceconnect.libs.base.product.core.protocol.URoProtocolFormatter;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommand;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public abstract class URoUkitSmartCommandFormatter<T> extends URoProtocolFormatter<T> {

    public abstract URoResponse<T> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception;
    public abstract byte[] encodeRequestMessage(URoRequest request) throws Exception;

    public static final byte[] EMPTY_REQUEST_DATA = {};

    public static byte getIdByte(Collection<Integer> _ids) {
        Integer[] ids = _ids.toArray(new Integer[_ids.size()]);
        return getIdByte(ids);
    }

    public static int getIdInt(Collection<Integer> _ids) {
        Integer[] ids = _ids.toArray(new Integer[_ids.size()]);
        return getIdInt(ids);
    }

    public static byte getIdByte(Integer... ids) {
        if(ids.length == 1 && ids[0] == 0) {
            return 0;
        }
        byte result = 0;
        for(Integer id : ids) {
            result |= 0x01 << (id - 1);
        }
        return result;
    }

    public static int getIdInt(Integer... ids) {
        if(ids.length == 1 && ids[0] == 0) {
            return 0;
        }
        int result = 0;
        for(Integer id : ids) {
            result |= 0x01 << (id - 1);
        }
        return result;
    }

    public static Integer[] toIdList(int id_map) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 1; i <= 32; i++) {
            if ((id_map & (1 << (i - 1))) != 0) {
                result.add(i);
            }
        }
        return result.toArray(new Integer[result.size()]);
    }

    public URoResponse<T> decodeResponseMessage(URoCommand cmd, int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        return decodeResponseMessage(errorCode, bizData, rawResponse);
    }

    public byte[] encodeRequestMessage(URoRequest request, int sequenceId) throws Exception {
        return encodeRequestMessage(request);
    }

}
