package com.ubtedu.deviceconnect.libs.base.product.core.protocol;

import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommand;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommandConstants;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import java.util.Locale;

/**
 * @Author naOKi
 **/
public abstract class URoProtocolHandler extends URoProtocolFormatter {

    public boolean ignorePrintData(URoCommand cmd) {
        boolean result = false;
        result = result || URoCommandConstants.CMD_BOARD_HEARTBEAT.equals(cmd);
        result = result || URoCommandConstants.CMD_BOARD_SELF_CHECK.equals(cmd);
        result = result || URoCommandConstants.CMD_SENSOR_VALUE.equals(cmd);
        result = result || URoCommandConstants.CMD_BOARD_UPGRADE_ENTRY.equals(cmd);
        result = result || URoCommandConstants.CMD_BOARD_UPGRADE_COMMIT.equals(cmd);
        result = result || URoCommandConstants.CMD_BOARD_UPGRADE_DATA.equals(cmd);
        result = result || URoCommandConstants.CMD_STEERING_GEAR_UPGRADE_ENTRY.equals(cmd);
        result = result || URoCommandConstants.CMD_STEERING_GEAR_UPGRADE_COMMIT.equals(cmd);
        result = result || URoCommandConstants.CMD_STEERING_GEAR_UPGRADE_DATA.equals(cmd);
        result = result || URoCommandConstants.CMD_SENSOR_UPGRADE_ENTRY.equals(cmd);
        result = result || URoCommandConstants.CMD_SENSOR_UPGRADE_COMMIT.equals(cmd);
        result = result || URoCommandConstants.CMD_SENSOR_UPGRADE_DATA.equals(cmd);
        return result;
    }

    public static void printByteData(String msg, String linePrefix, byte[]... datas) {
        StringBuilder sb = new StringBuilder();
        sb.append(msg);
        int i = 0;
        for (byte[] data : datas) {
            for (byte b : data) {
                if(i++ % 16 == 0) {
                    sb.append("\n").append(linePrefix);
                } else {
                    sb.append(" ");
                }
                sb.append(String.format(Locale.US,"%02X", b));
            }
        }
        URoLogUtils.e(sb.toString());
    }

    public static byte[] arrayJoin(byte[]... datas) {
        int totalLength = 0;
        for (byte[] data : datas) {
            totalLength += data.length;
        }
        byte[] result = new byte[totalLength];
        int copiedLength = 0;
        for (byte[] data : datas) {
            System.arraycopy(data, 0, result, copiedLength, data.length);
            copiedLength += data.length;
        }
        return result;
    }

}
