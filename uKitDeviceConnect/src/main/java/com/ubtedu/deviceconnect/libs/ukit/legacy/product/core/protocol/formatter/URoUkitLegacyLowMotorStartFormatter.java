package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter;

import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationParamKeys;
import com.ubtedu.deviceconnect.libs.base.model.URoRotateMotorCommand;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoUkitDummyData;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacyLowMotorStartFormatter extends URoUkitLegacyCommandFormatter<URoUkitDummyData> {

    private URoUkitLegacyLowMotorStartFormatter(){}

    public static final URoUkitLegacyLowMotorStartFormatter INSTANCE = new URoUkitLegacyLowMotorStartFormatter();

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
        URoRotateMotorCommand[] commands = request.getParameter(URoInvocationParamKeys.Motor.PARAM_KEY_ROTATE_COMMAND, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] result;
        try {
            byte index = 0x01;
            HashSet<Integer> ids = new HashSet<>();
            for(URoRotateMotorCommand command : commands) {
                ids.add(command.getId());
            }
            int idByte = getIdByte(ids);
            baos.write(index);
            baos.write(idByte);
            ArrayList<URoRotateMotorCommand> commandList = new ArrayList<>(Arrays.asList(commands));
            Collections.sort(commandList, new Comparator<URoRotateMotorCommand>() {
                @Override
                public int compare(URoRotateMotorCommand t1, URoRotateMotorCommand t2) {
                    return Integer.compare(t1.getId(), t2.getId());
                }
            });
            Collections.reverse(commandList);
            for(URoRotateMotorCommand command : commandList) {
                int speed = command.getSpeed();
                int time = command.getTime() / 100;
                baos.write((speed >> 8) & 0xFF);
                baos.write(speed & 0xFF);
                baos.write((time >> 8) & 0xFF);
                baos.write(time & 0xFF);
            }
            result = baos.toByteArray();
            baos.reset();
        } finally {
            baos.close();
        }
        return result;
    }

}
