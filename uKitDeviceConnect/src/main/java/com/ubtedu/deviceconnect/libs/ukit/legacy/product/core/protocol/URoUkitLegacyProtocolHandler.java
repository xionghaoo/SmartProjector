package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol;

import com.ubtedu.deviceconnect.libs.base.product.core.protocol.URoProtocolFormatter;
import com.ubtedu.deviceconnect.libs.base.product.core.protocol.URoProtocolHandler;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommand;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommandConstants;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacyBoardBatteryFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacyBoardHandshakeFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacyBoardInfoFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacyBoardSelfCheckFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacyBoardSerialNumberFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacyBoardUpgradeDataFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacyBoardUpgradeEntryFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacyLedEmotionFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacyLedFaceFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacyLowMotorPwmFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacyLowMotorStartFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacyLowMotorStopFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacySensorModifyFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacySensorSpeakerInfoFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacySensorSwitchFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacySensorUpgradeAbortFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacySensorUpgradeDataFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacySensorUpgradeEntryFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacySensorValueFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacySimpleFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacySteeringGearAngleFeedbackFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacySteeringGearAngleFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacySteeringGearLoopFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacySteeringGearModifyFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacySteeringGearUpgradeDataFormatter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter.URoUkitLegacySteeringGearUpgradeEntryFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * @Author naOKi
 * @Date 2019/06/17
 **/
public class URoUkitLegacyProtocolHandler extends URoProtocolHandler {

    public static final byte[] HEAD1 = {(byte)0xFB};
    public static final byte[] HEAD2 = {(byte)0xBF};
    public static final byte[] TAIL = {(byte)0xED};

    private static final HashMap<URoUkitLegacyCommand, URoProtocolFormatter> dictType2Formatter = new HashMap<>();

    private static void registerCommandType(URoUkitLegacyCommand cmd, URoProtocolFormatter formatter) {
        dictType2Formatter.put(cmd, formatter);
    }

    static {
        /* BOARD */
        registerCommandType(URoCommandConstants.CMD_BOARD_HANDSHAKE, URoUkitLegacyBoardHandshakeFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_BOARD_HEARTBEAT, URoUkitLegacySimpleFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_BOARD_INFO, URoUkitLegacyBoardInfoFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_BOARD_BATTERY, URoUkitLegacyBoardBatteryFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_BOARD_SERIAL_NUMBER, URoUkitLegacyBoardSerialNumberFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_BOARD_STOP, URoUkitLegacySimpleFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_BOARD_SELF_CHECK, URoUkitLegacyBoardSelfCheckFormatter.INSTANCE);

        /* BOARD UPGRADE */
        registerCommandType(URoCommandConstants.CMD_BOARD_UPGRADE_ENTRY, URoUkitLegacyBoardUpgradeEntryFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_BOARD_UPGRADE_COMMIT, URoUkitLegacyBoardUpgradeDataFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_BOARD_UPGRADE_DATA, URoUkitLegacyBoardUpgradeDataFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_BOARD_UPGRADE_ABORT, URoUkitLegacySimpleFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_BOARD_UPGRADE_FLASH_BEGIN, URoUkitLegacySimpleFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_BOARD_UPGRADE_FLASH_END, URoUkitLegacySimpleFormatter.INSTANCE);

        /* SENSOR */
        registerCommandType(URoCommandConstants.CMD_SENSOR_SPEAKER_INFO, URoUkitLegacySensorSpeakerInfoFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SENSOR_SWITCH, URoUkitLegacySensorSwitchFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SENSOR_MODIFY, URoUkitLegacySensorModifyFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SENSOR_VALUE, URoUkitLegacySensorValueFormatter.INSTANCE);

        /* SENSOR UPGRADE */
        registerCommandType(URoCommandConstants.CMD_SENSOR_UPGRADE_ENTRY, URoUkitLegacySensorUpgradeEntryFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SENSOR_UPGRADE_DATA, URoUkitLegacySensorUpgradeDataFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SENSOR_UPGRADE_ABORT, URoUkitLegacySensorUpgradeAbortFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SENSOR_UPGRADE_COMMIT, URoUkitLegacySensorUpgradeDataFormatter.INSTANCE);

        /* STEERING GEAR */
        registerCommandType(URoCommandConstants.CMD_STEERING_GEAR_LOOP, URoUkitLegacySteeringGearLoopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_STEERING_GEAR_ANGLE, URoUkitLegacySteeringGearAngleFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_STEERING_GEAR_ANGLE_FEEDBACK, URoUkitLegacySteeringGearAngleFeedbackFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_STEERING_GEAR_MODIFY, URoUkitLegacySteeringGearModifyFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_STEERING_GEAR_FIX, URoUkitLegacySimpleFormatter.INSTANCE);

        /* STEERING GEAR UPGRADE */
        registerCommandType(URoCommandConstants.CMD_STEERING_GEAR_UPGRADE_ENTRY, URoUkitLegacySteeringGearUpgradeEntryFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_STEERING_GEAR_UPGRADE_DATA, URoUkitLegacySteeringGearUpgradeDataFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_STEERING_GEAR_UPGRADE_ABORT, URoUkitLegacySimpleFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_STEERING_GEAR_UPGRADE_COMMIT, URoUkitLegacySteeringGearUpgradeDataFormatter.INSTANCE);

        /* PERIPHERAL_SENSOR_LED */
        registerCommandType(URoCommandConstants.CMD_LED_EMOTION, URoUkitLegacyLedEmotionFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LED_FACE, URoUkitLegacyLedFaceFormatter.INSTANCE);

        /* LOW MOTOR */
        registerCommandType(URoCommandConstants.CMD_LOW_MOTOR_START, URoUkitLegacyLowMotorStartFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LOW_MOTOR_STOP, URoUkitLegacyLowMotorStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LOW_MOTOR_FIX, URoUkitLegacySimpleFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LOW_MOTOR_PWM, URoUkitLegacyLowMotorPwmFormatter.INSTANCE);
    }

    @Override
    public URoResponse decodeResponseMessage(URoCommand cmd, int errorCode, byte[] _bizData, byte[] _rawResponse) throws Exception {
        byte[] rawResponse = _rawResponse;
        byte[] bizData = _bizData;
        URoProtocolFormatter formatter = conversionCommandFormatter(cmd);
        if(formatter == null) {
            return null;
        }
        URoResponse response = formatter.decodeResponseMessage(cmd, errorCode, bizData, rawResponse);
        if(response.getBizData() == null) {
            response.setBizData(bizData);
        }
        if(response.getRawResponse() == null) {
            response.setRawResponse(rawResponse);
        }
        response.setCmd(cmd);
        return response;
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request, int sequenceId) throws Exception {
        Byte cmdCode = conversionCommandCode(request.getCmd());
        if(cmdCode == null) {
            return null;
        }
        URoProtocolFormatter formatter = conversionCommandFormatter(request.getCmd());
        if(formatter == null) {
            return null;
        }
        byte[] bizData = formatter.encodeRequestMessage(request, sequenceId);
        byte[] rawRequest = packageDataFormat(cmdCode, bizData);
        if (!ignorePrintData(request.getCmd())) {
            URoProtocolHandler.printByteData("Message data request: " + request.getCmd().getName(), " >>>>> ", rawRequest);
        }
        return rawRequest;
    }

    public static int byte2Int(byte b) {
        return Integer.parseInt(String.format(Locale.US,"%x", b), 16);
    }

    public static URoCommand conversionCommandType(byte cmdCode) {
        ArrayList<URoUkitLegacyCommand> listCommand = new ArrayList<>(dictType2Formatter.keySet());
        int index = listCommand.indexOf(new URoUkitLegacyCommand("", (int)cmdCode));
        if(index < 0) {
            return null;
        }
        return listCommand.get(index);
    }

    public static Byte conversionCommandCode(URoCommand cmd) {
        if(!(cmd instanceof URoUkitLegacyCommand)) {
            return -1;
        }
        return (byte)((URoUkitLegacyCommand)cmd).getCmdCode();
    }

    private URoProtocolFormatter conversionCommandFormatter(URoCommand cmdType) {
        if(!(cmdType instanceof URoUkitLegacyCommand)) {
            return null;
        }
        return dictType2Formatter.get(cmdType);
    }

    public static byte checksum(byte length, byte cmdCode, byte[] bizData) {
        byte result = (byte)(length + cmdCode);
        for(byte data : bizData) {
            result = (byte)(result + data);
        }
        return result;
    }

    private byte[] packageDataFormat(byte _cmdCode, byte[] _bizData) {
        if(_bizData == null) {
            return null;
        }
        byte[] result = new byte[_bizData.length + 6];
        byte[] length = {(byte)(result.length - 1)};
        byte[] cmdCode = {_cmdCode};
        byte[] checksum = {checksum(length[0], cmdCode[0], _bizData)};
        int destPos = 0;
        System.arraycopy(HEAD1, 0, result, destPos, HEAD1.length); // 头字节：1字节 0xFB
        destPos += HEAD1.length;
        System.arraycopy(HEAD2, 0, result, destPos, HEAD2.length); // 头字节：1字节 0xBF
        destPos += HEAD2.length;
        System.arraycopy(length, 0, result, destPos, length.length); // 长度：头字节 + 长度 + 命令 + 参数 + Checksum，即报文长度-1
        destPos += length.length;
        System.arraycopy(cmdCode, 0, result, destPos, cmdCode.length); // 命令：1字节
        destPos += cmdCode.length;
        System.arraycopy(_bizData, 0, result, destPos, _bizData.length); // 参数
        destPos += _bizData.length;
        System.arraycopy(checksum, 0, result, destPos, checksum.length); // Checksum：长度 + 命令 + 参数的累加和
        destPos += checksum.length;
        System.arraycopy(TAIL, 0, result, destPos, TAIL.length); // 结束符：1字节 0xED
        return result;
    }

    public boolean ignorePrintData(URoCommand cmd) {
        boolean result = super.ignorePrintData(cmd);
        return result;
    }

}
