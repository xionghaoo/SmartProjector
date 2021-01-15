package com.ubtedu.deviceconnect.libs.ukit.smart.mission;

import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommandConstants;
import com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.URoUkitSmartCommand;

/**
 * @Author naOKi
 * @Date 2020/06/22
 **/
public class URoFileCommandHelper {

    private URoFileCommandHelper() {}

    public enum FileCommandType {
        FILE_SEND_MAX_PACK_SIZE,
        FILE_SEND_START,
        FILE_SENDING,
        FILE_SEND_OVER,
        FILE_SEND_STOP,
        FILE_RECV_MAX_PACK_SIZE,
        FILE_RECV_START,
        FILE_RECVING,
        FILE_RECV_OVER,
        FILE_RECV_STOP,
        FILE_DEL,
        FILE_RENAME,
        FILE_STATE,
        DIR_MAKE,
        DIR_DEL,
        DIR_F_CNT_GET,
        DIR_F_INFO_GET;
    }

    public static URoUkitSmartCommand getCommandByDev(FileCommandType commandType) {
        return getCommandByDev(commandType, null);
    }

    public static URoUkitSmartCommand getCommandByDev(FileCommandType commandType, URoComponentType componentType) {
        boolean isLcdDev = URoComponentType.LCD.equals(componentType);
        switch (commandType) {
        case FILE_SEND_MAX_PACK_SIZE:
            return isLcdDev ? URoCommandConstants.CMD_LCD_FILE_SEND_MAX_PACK_SIZE : URoCommandConstants.CMD_FILE_SEND_MAX_PACK_SIZE;
        case FILE_SEND_START:
            return isLcdDev ? URoCommandConstants.CMD_LCD_FILE_SEND_START : URoCommandConstants.CMD_FILE_SEND_START;
        case FILE_SENDING:
            return isLcdDev ? URoCommandConstants.CMD_LCD_FILE_SENDING : URoCommandConstants.CMD_FILE_SENDING;
        case FILE_SEND_OVER:
            return isLcdDev ? URoCommandConstants.CMD_LCD_FILE_SEND_OVER : URoCommandConstants.CMD_FILE_SEND_OVER;
        case FILE_SEND_STOP:
            return isLcdDev ? URoCommandConstants.CMD_LCD_FILE_SEND_STOP : URoCommandConstants.CMD_FILE_SEND_STOP;
        case FILE_RECV_MAX_PACK_SIZE:
            return isLcdDev ? URoCommandConstants.CMD_LCD_FILE_RECV_MAX_PACK_SIZE : URoCommandConstants.CMD_FILE_RECV_MAX_PACK_SIZE;
        case FILE_RECV_START:
            return isLcdDev ? URoCommandConstants.CMD_LCD_FILE_RECV_START : URoCommandConstants.CMD_FILE_RECV_START;
        case FILE_RECVING:
            return isLcdDev ? URoCommandConstants.CMD_LCD_FILE_RECVING : URoCommandConstants.CMD_FILE_RECVING;
        case FILE_RECV_OVER:
            return isLcdDev ? URoCommandConstants.CMD_LCD_FILE_RECV_OVER : URoCommandConstants.CMD_FILE_RECV_OVER;
        case FILE_RECV_STOP:
            return isLcdDev ? URoCommandConstants.CMD_LCD_FILE_RECV_STOP : URoCommandConstants.CMD_FILE_RECV_STOP;
        case FILE_DEL:
            return isLcdDev ? URoCommandConstants.CMD_LCD_FILE_DEL : URoCommandConstants.CMD_FILE_DEL;
        case FILE_RENAME:
            return isLcdDev ? URoCommandConstants.CMD_LCD_FILE_RENAME : URoCommandConstants.CMD_FILE_RENAME;
        case FILE_STATE:
            return isLcdDev ? URoCommandConstants.CMD_LCD_FILE_STATE : URoCommandConstants.CMD_FILE_STATE;
        case DIR_MAKE:
            return isLcdDev ? URoCommandConstants.CMD_LCD_DIR_MAKE : URoCommandConstants.CMD_DIR_MAKE;
        case DIR_DEL:
            return isLcdDev ? URoCommandConstants.CMD_LCD_DIR_DEL : URoCommandConstants.CMD_DIR_DEL;
        case DIR_F_CNT_GET:
            return isLcdDev ? URoCommandConstants.CMD_LCD_DIR_F_CNT_GET : URoCommandConstants.CMD_DIR_F_CNT_GET;
        case DIR_F_INFO_GET:
            return isLcdDev ? URoCommandConstants.CMD_LCD_DIR_F_INFO_GET : URoCommandConstants.CMD_DIR_F_INFO_GET;
        }
        return null;
    }

}
