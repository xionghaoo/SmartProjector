package com.ubtedu.deviceconnect.libs.base.product.core.queue;

import com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.URoUkitLegacyCommand;
import com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.URoUkitSmartCommand;

/**
 * @Author naOKi
 **/
public interface URoCommandConstants {

    /* ==================== UKIT LEGACY =================== */

    /* BOARD */
    URoUkitLegacyCommand CMD_BOARD_HANDSHAKE = new URoUkitLegacyCommand("CMD_BOARD_HANDSHAKE", (byte)0x01);
    URoUkitLegacyCommand CMD_BOARD_HEARTBEAT = new URoUkitLegacyCommand("CMD_BOARD_HEARTBEAT", (byte)0x03);
    URoUkitLegacyCommand CMD_BOARD_INFO = new URoUkitLegacyCommand("CMD_BOARD_INFO", (byte)0x08);
    URoUkitLegacyCommand CMD_BOARD_BATTERY = new URoUkitLegacyCommand("CMD_BOARD_BATTERY", (byte)0x27);
    URoUkitLegacyCommand CMD_BOARD_SERIAL_NUMBER = new URoUkitLegacyCommand("CMD_BOARD_SERIAL_NUMBER", (byte)0x2B);
    URoUkitLegacyCommand CMD_BOARD_STOP = new URoUkitLegacyCommand("CMD_BOARD_STOP", (byte)0x3C);
    URoUkitLegacyCommand CMD_BOARD_SELF_CHECK = new URoUkitLegacyCommand("CMD_BOARD_SELF_CHECK", (byte)0x05);

    /* BOARD UPGRADE */
    URoUkitLegacyCommand CMD_BOARD_UPGRADE_ENTRY = new URoUkitLegacyCommand("CMD_BOARD_UPGRADE_ENTRY", (byte)0x1A);
    URoUkitLegacyCommand CMD_BOARD_UPGRADE_COMMIT = new URoUkitLegacyCommand("CMD_BOARD_UPGRADE_COMMIT", (byte)0x1B);
    URoUkitLegacyCommand CMD_BOARD_UPGRADE_DATA = new URoUkitLegacyCommand("CMD_BOARD_UPGRADE_DATA", (byte)0x1C);
    URoUkitLegacyCommand CMD_BOARD_UPGRADE_ABORT = new URoUkitLegacyCommand("CMD_BOARD_UPGRADE_ABORT", (byte)0x1D);
    URoUkitLegacyCommand CMD_BOARD_UPGRADE_FLASH_BEGIN = new URoUkitLegacyCommand("CMD_BOARD_UPGRADE_FLASH_BEGIN", (byte)0x1E);
    URoUkitLegacyCommand CMD_BOARD_UPGRADE_FLASH_END = new URoUkitLegacyCommand("CMD_BOARD_UPGRADE_FLASH_END", (byte)0x1F);

    /* SENSOR */
    URoUkitLegacyCommand CMD_SENSOR_SPEAKER_INFO = new URoUkitLegacyCommand("CMD_SENSOR_SPEAKER_INFO", (byte)0x72);
    URoUkitLegacyCommand CMD_SENSOR_SWITCH = new URoUkitLegacyCommand("CMD_SENSOR_SWITCH", (byte)0x71);
    URoUkitLegacyCommand CMD_SENSOR_MODIFY = new URoUkitLegacyCommand("CMD_SENSOR_MODIFY", (byte)0x74);
    URoUkitLegacyCommand CMD_SENSOR_VALUE = new URoUkitLegacyCommand("CMD_SENSOR_VALUE", (byte)0x7E);

    /* SENSOR UPGRADE */
    URoUkitLegacyCommand CMD_SENSOR_UPGRADE_ENTRY = new URoUkitLegacyCommand("CMD_SENSOR_UPGRADE_ENTRY", (byte)0x80);
    URoUkitLegacyCommand CMD_SENSOR_UPGRADE_DATA = new URoUkitLegacyCommand("CMD_SENSOR_UPGRADE_DATA", (byte)0x81);
    URoUkitLegacyCommand CMD_SENSOR_UPGRADE_ABORT = new URoUkitLegacyCommand("CMD_SENSOR_UPGRADE_ABORT", (byte)0x82);
    URoUkitLegacyCommand CMD_SENSOR_UPGRADE_COMMIT = new URoUkitLegacyCommand("CMD_SENSOR_UPGRADE_COMMIT", (byte)0x83);

    /* STEERING GEAR */
    URoUkitLegacyCommand CMD_STEERING_GEAR_LOOP = new URoUkitLegacyCommand("CMD_STEERING_GEAR_LOOP", (byte)0x07);
    URoUkitLegacyCommand CMD_STEERING_GEAR_ANGLE = new URoUkitLegacyCommand("CMD_STEERING_GEAR_ANGLE", (byte)0x09);
    URoUkitLegacyCommand CMD_STEERING_GEAR_ANGLE_FEEDBACK = new URoUkitLegacyCommand("CMD_STEERING_GEAR_ANGLE_FEEDBACK", (byte)0x0B);
    URoUkitLegacyCommand CMD_STEERING_GEAR_MODIFY = new URoUkitLegacyCommand("CMD_STEERING_GEAR_MODIFY", (byte)0x0C);
    URoUkitLegacyCommand CMD_STEERING_GEAR_FIX = new URoUkitLegacyCommand("CMD_STEERING_GEAR_FIX", (byte)0x3B);

    /* STEERING GEAR UPGRADE */
    URoUkitLegacyCommand CMD_STEERING_GEAR_UPGRADE_ENTRY = new URoUkitLegacyCommand("CMD_STEERING_GEAR_UPGRADE_ENTRY", (byte)0x23);
    URoUkitLegacyCommand CMD_STEERING_GEAR_UPGRADE_DATA = new URoUkitLegacyCommand("CMD_STEERING_GEAR_UPGRADE_DATA", (byte)0x24);
    URoUkitLegacyCommand CMD_STEERING_GEAR_UPGRADE_ABORT = new URoUkitLegacyCommand("CMD_STEERING_GEAR_UPGRADE_ABORT", (byte)0x25);
    URoUkitLegacyCommand CMD_STEERING_GEAR_UPGRADE_COMMIT = new URoUkitLegacyCommand("CMD_STEERING_GEAR_UPGRADE_COMMIT", (byte)0x26);

    /* PERIPHERAL_SENSOR_LED */
    URoUkitLegacyCommand CMD_LED_EMOTION = new URoUkitLegacyCommand("CMD_LED_EMOTION", (byte)0x78);
    URoUkitLegacyCommand CMD_LED_FACE = new URoUkitLegacyCommand("CMD_LED_FACE", (byte)0x79);

    /* LOW MOTOR */
    URoUkitLegacyCommand CMD_LOW_MOTOR_START = new URoUkitLegacyCommand("CMD_LOW_MOTOR_START", (byte)0x90);
    URoUkitLegacyCommand CMD_LOW_MOTOR_STOP = new URoUkitLegacyCommand("CMD_LOW_MOTOR_STOP", (byte)0x91);
    URoUkitLegacyCommand CMD_LOW_MOTOR_FIX = new URoUkitLegacyCommand("CMD_LOW_MOTOR_FIX", (byte)0x92);
    URoUkitLegacyCommand CMD_LOW_MOTOR_PWM = new URoUkitLegacyCommand("CMD_LOW_MOTOR_PWM", (byte)0x93);

    /* ==================== UKIT SMART =================== */

    URoUkitSmartCommand CMD_SN_GET = new URoUkitSmartCommand("CMD_SN_GET", 0x2, 1);
    URoUkitSmartCommand CMD_SN_SET = new URoUkitSmartCommand("CMD_SN_SET", 0x2, 2);
    URoUkitSmartCommand CMD_HW_VER = new URoUkitSmartCommand("CMD_HW_VER", 0x2, 3);
    URoUkitSmartCommand CMD_SW_VER = new URoUkitSmartCommand("CMD_SW_VER", 0x2, 4);
    URoUkitSmartCommand CMD_PT_VER = new URoUkitSmartCommand("CMD_PT_VER", 0x2, 5);
    URoUkitSmartCommand CMD_RECOVER = new URoUkitSmartCommand("CMD_RECOVER", 0x2, 6);
    URoUkitSmartCommand CMD_RESET = new URoUkitSmartCommand("CMD_RESET", 0x2, 7);
    URoUkitSmartCommand CMD_HEARTBEAT = new URoUkitSmartCommand("CMD_HEARTBEAT", 0x2, 8);
    URoUkitSmartCommand CMD_START = new URoUkitSmartCommand("CMD_START", 0x2, 9);
    URoUkitSmartCommand CMD_STOP = new URoUkitSmartCommand("CMD_STOP", 0x2, 10);
    URoUkitSmartCommand CMD_STATE_GET = new URoUkitSmartCommand("CMD_STATE_GET", 0x2, 11);
    URoUkitSmartCommand CMD_TYPE_GET = new URoUkitSmartCommand("CMD_TYPE_GET", 0x2, 17);
    URoUkitSmartCommand CMD_RANDOM_GET = new URoUkitSmartCommand("CMD_RANDOM_GET", 0x2, 18);
    URoUkitSmartCommand CMD_SUBTYPE_SET = new URoUkitSmartCommand("CMD_SUBTYPE_SET", 0x2, 19);
    URoUkitSmartCommand CMD_PARA_SET = new URoUkitSmartCommand("CMD_PARA_SET", 0x2, 20);
    URoUkitSmartCommand CMD_PARA_GET = new URoUkitSmartCommand("CMD_PARA_GET", 0x2, 21);
    URoUkitSmartCommand CMD_DEV_STATUS_LED_SET = new URoUkitSmartCommand("CMD_DEV_STATUS_LED_SET", 0x2, 22);
    URoUkitSmartCommand CMD_MCU_SN_GET = new URoUkitSmartCommand("CMD_MCU_SN_GET", 0x2, 23);
    URoUkitSmartCommand CMD_FILE_SEND_MAX_PACK_SIZE = new URoUkitSmartCommand("CMD_FILE_SEND_MAX_PACK_SIZE", 0x2, 100);
    URoUkitSmartCommand CMD_FILE_SEND_START = new URoUkitSmartCommand("CMD_FILE_SEND_START", 0x2, 101);
    URoUkitSmartCommand CMD_FILE_SENDING = new URoUkitSmartCommand("CMD_FILE_SENDING", 0x2, 102);
    URoUkitSmartCommand CMD_FILE_SEND_OVER = new URoUkitSmartCommand("CMD_FILE_SEND_OVER", 0x2, 103);
    URoUkitSmartCommand CMD_FILE_SEND_STOP = new URoUkitSmartCommand("CMD_FILE_SEND_STOP", 0x2, 104);
    URoUkitSmartCommand CMD_FILE_RECV_MAX_PACK_SIZE = new URoUkitSmartCommand("CMD_FILE_RECV_MAX_PACK_SIZE", 0x2, 105);
    URoUkitSmartCommand CMD_FILE_RECV_START = new URoUkitSmartCommand("CMD_FILE_RECV_START", 0x2, 106);
    URoUkitSmartCommand CMD_FILE_RECVING = new URoUkitSmartCommand("CMD_FILE_RECVING", 0x2, 107);
    URoUkitSmartCommand CMD_FILE_RECV_OVER = new URoUkitSmartCommand("CMD_FILE_RECV_OVER", 0x2, 108);
    URoUkitSmartCommand CMD_FILE_RECV_STOP = new URoUkitSmartCommand("CMD_FILE_RECV_STOP", 0x2, 109);
    URoUkitSmartCommand CMD_FILE_DEL = new URoUkitSmartCommand("CMD_FILE_DEL", 0x2, 110);
    URoUkitSmartCommand CMD_FILE_RENAME = new URoUkitSmartCommand("CMD_FILE_RENAME", 0x2, 111);
    URoUkitSmartCommand CMD_FILE_STATE = new URoUkitSmartCommand("CMD_FILE_STATE", 0x2, 112);
    URoUkitSmartCommand CMD_DIR_MAKE = new URoUkitSmartCommand("CMD_DIR_MAKE", 0x2, 113);
    URoUkitSmartCommand CMD_DIR_DEL = new URoUkitSmartCommand("CMD_DIR_DEL", 0x2, 114);
    URoUkitSmartCommand CMD_DIR_F_CNT_GET = new URoUkitSmartCommand("CMD_DIR_F_CNT_GET", 0x2, 115);
    URoUkitSmartCommand CMD_DIR_F_INFO_GET = new URoUkitSmartCommand("CMD_DIR_F_INFO_GET", 0x2, 116);
    URoUkitSmartCommand CMD_SRV_ANGLE_SET = new URoUkitSmartCommand("CMD_SRV_ANGLE_SET", 0x3, 1000);
    URoUkitSmartCommand CMD_SRV_ANGLE_GET = new URoUkitSmartCommand("CMD_SRV_ANGLE_GET", 0x3, 1001);
    URoUkitSmartCommand CMD_SRV_PWM_SET = new URoUkitSmartCommand("CMD_SRV_PWM_SET", 0x3, 1002);
    URoUkitSmartCommand CMD_SRV_WHEEL_SET = new URoUkitSmartCommand("CMD_SRV_WHEEL_SET", 0x3, 1003);
    URoUkitSmartCommand CMD_SRV_STOP = new URoUkitSmartCommand("CMD_SRV_STOP", 0x3, 1004);
    URoUkitSmartCommand CMD_SRV_CALIBRATION_SET = new URoUkitSmartCommand("CMD_SRV_CALIBRATION_SET", 0x3, 1005);
    URoUkitSmartCommand CMD_SRV_CALIBRATION_GET = new URoUkitSmartCommand("CMD_SRV_CALIBRATION_GET", 0x3, 1006);
    URoUkitSmartCommand CMD_SRV_FAULT_GET = new URoUkitSmartCommand("CMD_SRV_FAULT_GET", 0x3, 1007);
    URoUkitSmartCommand CMD_SRV_FAULT_CLEAR = new URoUkitSmartCommand("CMD_SRV_FAULT_CLEAR", 0x3, 1008);
    URoUkitSmartCommand CMD_SRV_LIMIT_SET = new URoUkitSmartCommand("CMD_SRV_LIMIT_SET", 0x3, 1009);
    URoUkitSmartCommand CMD_SRV_STATUS_GET = new URoUkitSmartCommand("CMD_SRV_STATUS_GET", 0x3, 1010);
    URoUkitSmartCommand CMD_SRV_INFO_GET = new URoUkitSmartCommand("CMD_SRV_INFO_GET", 0x3, 1011);
    URoUkitSmartCommand CMD_MTR_SPEED_SET = new URoUkitSmartCommand("CMD_MTR_SPEED_SET", 0x4, 1000);
    URoUkitSmartCommand CMD_MTR_STOP = new URoUkitSmartCommand("CMD_MTR_STOP", 0x4, 1001);
    URoUkitSmartCommand CMD_MTR_SPEED_GET = new URoUkitSmartCommand("CMD_MTR_SPEED_GET", 0x4, 1002);
    URoUkitSmartCommand CMD_MTR_PWM_UPPER_LIMIT_SET = new URoUkitSmartCommand("CMD_MTR_PWM_UPPER_LIMIT_SET", 0x4, 1003);
    URoUkitSmartCommand CMD_MTR_PWM_SET = new URoUkitSmartCommand("CMD_MTR_PWM_SET", 0x4, 1004);
    URoUkitSmartCommand CMD_MTR_PWM_GET = new URoUkitSmartCommand("CMD_MTR_PWM_GET", 0x4, 1005);
    URoUkitSmartCommand CMD_MTR_CNT_GET = new URoUkitSmartCommand("CMD_MTR_CNT_GET", 0x4, 1006);
    URoUkitSmartCommand CMD_MTR_CNT_RESET = new URoUkitSmartCommand("CMD_MTR_CNT_RESET", 0x4, 1007);
    URoUkitSmartCommand CMD_MTR_FAULT_GET = new URoUkitSmartCommand("CMD_MTR_FAULT_GET", 0x4, 1008);
    URoUkitSmartCommand CMD_MTR_FAULT_CLEAR = new URoUkitSmartCommand("CMD_MTR_FAULT_CLEAR", 0x4, 1009);
    URoUkitSmartCommand CMD_IR_ADC_GET = new URoUkitSmartCommand("CMD_IR_ADC_GET", 0x5, 1000);
    URoUkitSmartCommand CMD_IR_DIS_GET = new URoUkitSmartCommand("CMD_IR_DIS_GET", 0x5, 1001);
    URoUkitSmartCommand CMD_IR_ONOFF_GET = new URoUkitSmartCommand("CMD_IR_ONOFF_GET", 0x5, 1002);
    URoUkitSmartCommand CMD_TH_GET = new URoUkitSmartCommand("CMD_TH_GET", 0x6, 1000);
    URoUkitSmartCommand CMD_SND_ADC_VALUE_GET = new URoUkitSmartCommand("CMD_SND_ADC_VALUE_GET", 0x7, 1000);
    URoUkitSmartCommand CMD_SND_HYSTERESIS_TIME_SET = new URoUkitSmartCommand("CMD_SND_HYSTERESIS_TIME_SET", 0x7, 1001);
    URoUkitSmartCommand CMD_SND_HYSTERESIS_TIME_GET = new URoUkitSmartCommand("CMD_SND_HYSTERESIS_TIME_GET", 0x7, 1002);
    URoUkitSmartCommand CMD_SND_CALIBRATE_SET = new URoUkitSmartCommand("CMD_SND_CALIBRATE_SET", 0x7, 1003);
    URoUkitSmartCommand CMD_SND_CALIBRATE_GET = new URoUkitSmartCommand("CMD_SND_CALIBRATE_GET", 0x7, 1004);
    URoUkitSmartCommand CMD_LED_FIX_EXP_SET = new URoUkitSmartCommand("CMD_LED_FIX_EXP_SET", 0x8, 1000);
    URoUkitSmartCommand CMD_LED_EXP_SET = new URoUkitSmartCommand("CMD_LED_EXP_SET", 0x8, 1001);
    URoUkitSmartCommand CMD_ULT_DIS_GET = new URoUkitSmartCommand("CMD_ULT_DIS_GET", 0x9, 1000);
    URoUkitSmartCommand CMD_TCH_TIMES_GET = new URoUkitSmartCommand("CMD_TCH_TIMES_GET", 0xb, 1000);
    URoUkitSmartCommand CMD_TCH_TYPE_GET = new URoUkitSmartCommand("CMD_TCH_TYPE_GET", 0xb, 1001);
    URoUkitSmartCommand CMD_TCH_HYSTERESIS_TIME_SET = new URoUkitSmartCommand("CMD_TCH_HYSTERESIS_TIME_SET", 0xb, 1002);
    URoUkitSmartCommand CMD_TCH_HYSTERESIS_TIME_GET = new URoUkitSmartCommand("CMD_TCH_HYSTERESIS_TIME_GET", 0xb, 1003);
    URoUkitSmartCommand CMD_TCH_STATE_GET = new URoUkitSmartCommand("CMD_TCH_STATE_GET", 0xb, 1004);
    URoUkitSmartCommand CMD_CLR_RGB_GET = new URoUkitSmartCommand("CMD_CLR_RGB_GET", 0xd, 1000);
    URoUkitSmartCommand CMD_CLR_CAL_ONOFF = new URoUkitSmartCommand("CMD_CLR_CAL_ONOFF", 0xd, 1001);
    URoUkitSmartCommand CMD_CLR_CAL_STATE_GET = new URoUkitSmartCommand("CMD_CLR_CAL_STATE_GET", 0xd, 1002);
    URoUkitSmartCommand CMD_BAT_PWR_PCT = new URoUkitSmartCommand("CMD_BAT_PWR_PCT", 0xf, 1000);
    URoUkitSmartCommand CMD_BAT_STATUS_GET = new URoUkitSmartCommand("CMD_BAT_STATUS_GET", 0xf, 1001);
    URoUkitSmartCommand CMD_BAT_TEMPERATURE = new URoUkitSmartCommand("CMD_BAT_TEMPERATURE", 0xf, 1002);
    URoUkitSmartCommand CMD_BAT_VOL = new URoUkitSmartCommand("CMD_BAT_VOL", 0xf, 1003);
    URoUkitSmartCommand CMD_BAT_CHG_CUR = new URoUkitSmartCommand("CMD_BAT_CHG_CUR", 0xf, 1004);
    URoUkitSmartCommand CMD_BAT_DCHG_CUR = new URoUkitSmartCommand("CMD_BAT_DCHG_CUR", 0xf, 1005);
    URoUkitSmartCommand CMD_BAT_CAPACITY = new URoUkitSmartCommand("CMD_BAT_CAPACITY", 0xf, 1006);
    URoUkitSmartCommand CMD_LGT_VALUE_GET = new URoUkitSmartCommand("CMD_LGT_VALUE_GET", 0x11, 1000);
    URoUkitSmartCommand CMD_LGT_STDVALUE_SET = new URoUkitSmartCommand("CMD_LGT_STDVALUE_SET", 0x11, 1001);
    URoUkitSmartCommand CMD_LGT_STDVALUE_GET = new URoUkitSmartCommand("CMD_LGT_STDVALUE_GET", 0x11, 1002);
    URoUkitSmartCommand CMD_LGT_COE_GET = new URoUkitSmartCommand("CMD_LGT_COE_GET", 0x11, 1003);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_COMBO_OFF = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_COMBO_OFF", 0x2, 1013);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_SCRIPT_EXEC = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_SCRIPT_EXEC", 0x2, 2000);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_SCRIPT_STOP = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_SCRIPT_STOP", 0x2, 2001);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_SCRIPT_EXEC_REPORT = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_SCRIPT_EXEC_REPORT", 0x2, 2002);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_SCRIPT_PARAM_UPDATE = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_SCRIPT_PARAM_UPDATE", 0x2, 2003);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_SCRIPT_PARAM_CLEAR = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_SCRIPT_PARAM_CLEAR", 0x2, 2004);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_SCRIPT_EVENT_UPDATE = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_SCRIPT_EVENT_UPDATE", 0x2, 2005);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_SCRIPT_EVENT_CLEAR = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_SCRIPT_EVENT_CLEAR", 0x2, 2006);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_SENSOR_READ = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_SENSOR_READ", 0x2, 2505);
    URoUkitSmartCommand CMD_SRV_ID_SET = new URoUkitSmartCommand("CMD_SRV_ID_SET", 0x3, 201);
    URoUkitSmartCommand CMD_MTR_ID_SET = new URoUkitSmartCommand("CMD_MTR_ID_SET", 0x4, 201);
    URoUkitSmartCommand CMD_IR_ID_SET = new URoUkitSmartCommand("CMD_IR_ID_SET", 0x5, 201);
    URoUkitSmartCommand CMD_TH_ID_SET = new URoUkitSmartCommand("CMD_TH_ID_SET", 0x6, 201);
    URoUkitSmartCommand CMD_SND_ID_SET = new URoUkitSmartCommand("CMD_SND_ID_SET", 0x7, 201);
    URoUkitSmartCommand CMD_LED_ID_SET = new URoUkitSmartCommand("CMD_LED_ID_SET", 0x8, 201);
    URoUkitSmartCommand CMD_ULT_ID_SET = new URoUkitSmartCommand("CMD_ULT_ID_SET", 0x9, 201);
    URoUkitSmartCommand CMD_TCH_ID_SET = new URoUkitSmartCommand("CMD_TCH_ID_SET", 0xb, 201);
    URoUkitSmartCommand CMD_CLR_ID_SET = new URoUkitSmartCommand("CMD_CLR_ID_SET", 0xd, 201);
    URoUkitSmartCommand CMD_LGT_ID_SET = new URoUkitSmartCommand("CMD_LGT_ID_SET", 0x11, 201);
    URoUkitSmartCommand CMD_SUK_ID_SET = new URoUkitSmartCommand("CMD_SUK_ID_SET", 0x18, 201);
    URoUkitSmartCommand CMD_LED_BELT_ID_SET = new URoUkitSmartCommand("CMD_SUK_ID_SET", 0x1A, 201);
    URoUkitSmartCommand CMD_ACTION_COMPLETED_REPORT = new URoUkitSmartCommand("CMD_ACTION_COMPLETED_REPORT", 0x2, 255);
    URoUkitSmartCommand CMD_UPDATE_INFO_SET = new URoUkitSmartCommand("CMD_UPDATE_INFO_SET", 0x2, 256);
    URoUkitSmartCommand CMD_FILE_DOWNLOAD_PROGRESS_REPORT = new URoUkitSmartCommand("CMD_FILE_DOWNLOAD_PROGRESS_REPORT", 0x2, 257);
    URoUkitSmartCommand CMD_UPDATE_STATUS_REPORT = new URoUkitSmartCommand("CMD_UPDATE_STATUS_REPORT", 0x2, 258);
    URoUkitSmartCommand CMD_FILE_DOWNLOAD_PROGRESS_QUERY = new URoUkitSmartCommand("CMD_FILE_DOWNLOAD_PROGRESS_QUERY", 0x2, 260);
    URoUkitSmartCommand CMD_ULT_LIGHT_SET = new URoUkitSmartCommand("CMD_ULT_LIGHT_SET", 0x9, 1001);
    URoUkitSmartCommand CMD_SRV_ON = new URoUkitSmartCommand("CMD_SRV_ON", 0x3, 9);
    URoUkitSmartCommand CMD_SRV_OFF = new URoUkitSmartCommand("CMD_SRV_OFF", 0x3, 10);
    URoUkitSmartCommand CMD_MTR_ON = new URoUkitSmartCommand("CMD_MTR_ON", 0x4, 9);
    URoUkitSmartCommand CMD_MTR_OFF = new URoUkitSmartCommand("CMD_MTR_OFF", 0x4, 10);
    URoUkitSmartCommand CMD_IR_ON = new URoUkitSmartCommand("CMD_IR_ON", 0x5, 9);
    URoUkitSmartCommand CMD_IR_OFF = new URoUkitSmartCommand("CMD_IR_OFF", 0x5, 10);
    URoUkitSmartCommand CMD_TH_ON = new URoUkitSmartCommand("CMD_TH_ON", 0x6, 9);
    URoUkitSmartCommand CMD_TH_OFF = new URoUkitSmartCommand("CMD_TH_OFF", 0x6, 10);
    URoUkitSmartCommand CMD_SND_ON = new URoUkitSmartCommand("CMD_SND_ON", 0x7, 9);
    URoUkitSmartCommand CMD_SND_OFF = new URoUkitSmartCommand("CMD_SND_OFF", 0x7, 10);
    URoUkitSmartCommand CMD_LED_ON = new URoUkitSmartCommand("CMD_LED_ON", 0x8, 9);
    URoUkitSmartCommand CMD_LED_OFF = new URoUkitSmartCommand("CMD_LED_OFF", 0x8, 10);
    URoUkitSmartCommand CMD_ULT_ON = new URoUkitSmartCommand("CMD_ULT_ON", 0x9, 9);
    URoUkitSmartCommand CMD_ULT_OFF = new URoUkitSmartCommand("CMD_ULT_OFF", 0x9, 10);
    URoUkitSmartCommand CMD_TCH_ON = new URoUkitSmartCommand("CMD_TCH_ON", 0xb, 9);
    URoUkitSmartCommand CMD_TCH_OFF = new URoUkitSmartCommand("CMD_TCH_OFF", 0xb, 10);
    URoUkitSmartCommand CMD_CLR_ON = new URoUkitSmartCommand("CMD_CLR_ON", 0xd, 9);
    URoUkitSmartCommand CMD_CLR_OFF = new URoUkitSmartCommand("CMD_CLR_OFF", 0xd, 10);
    URoUkitSmartCommand CMD_LGT_ON = new URoUkitSmartCommand("CMD_LGT_ON", 0x11, 9);
    URoUkitSmartCommand CMD_LGT_OFF = new URoUkitSmartCommand("CMD_LGT_OFF", 0x11, 10);
    URoUkitSmartCommand CMD_SPK_GET = new URoUkitSmartCommand("CMD_SPK_GET", 0x17, 1000);
    URoUkitSmartCommand CMD_SUK_ON = new URoUkitSmartCommand("CMD_SUK_ON", 0x18, 9);
    URoUkitSmartCommand CMD_SUK_OFF = new URoUkitSmartCommand("CMD_SUK_OFF", 0x18, 10);
    URoUkitSmartCommand CMD_LCD_ON = new URoUkitSmartCommand("CMD_LCD_ON", 0x1B, 9);
    URoUkitSmartCommand CMD_LCD_OFF = new URoUkitSmartCommand("CMD_LCD_OFF", 0x1B, 10);


    URoUkitSmartCommand CMD_SRV_RESET = new URoUkitSmartCommand("CMD_SRV_RESET", 0x3, 7);
    URoUkitSmartCommand CMD_MTR_RESET = new URoUkitSmartCommand("CMD_MTR_RESET", 0x4, 7);
    URoUkitSmartCommand CMD_IR_RESET = new URoUkitSmartCommand("CMD_IR_RESET", 0x5, 7);
    URoUkitSmartCommand CMD_TH_RESET = new URoUkitSmartCommand("CMD_TH_RESET", 0x6, 7);
    URoUkitSmartCommand CMD_SND_RESET = new URoUkitSmartCommand("CMD_SND_RESET", 0x7, 7);
    URoUkitSmartCommand CMD_LED_RESET = new URoUkitSmartCommand("CMD_LED_RESET", 0x8, 7);
    URoUkitSmartCommand CMD_ULT_RESET = new URoUkitSmartCommand("CMD_ULT_RESET", 0x9, 7);
    URoUkitSmartCommand CMD_TCH_RESET = new URoUkitSmartCommand("CMD_TCH_RESET", 0xb, 7);
    URoUkitSmartCommand CMD_CLR_RESET = new URoUkitSmartCommand("CMD_CLR_RESET", 0xd, 7);
    URoUkitSmartCommand CMD_LGT_RESET = new URoUkitSmartCommand("CMD_LGT_RESET", 0x11, 7);
    URoUkitSmartCommand CMD_SPK_RESET = new URoUkitSmartCommand("CMD_SPK_RESET", 0X17, 7);
    URoUkitSmartCommand CMD_SUK_RESET = new URoUkitSmartCommand("CMD_SUK_RESET", 0X18, 7);
    URoUkitSmartCommand CMD_LED_BELT_RESET = new URoUkitSmartCommand("CMD_LED_BELT_RESET", 0X1A, 7);
    URoUkitSmartCommand CMD_LCD_RESET = new URoUkitSmartCommand("CMD_LCD_RESET", 0X1B, 7);
    URoUkitSmartCommand CMD_VISIONMODULE_RESET = new URoUkitSmartCommand("CMD_VISIONMODULE_RESET", 0x20, 7);


    URoUkitSmartCommand CMD_SRV_SW_VER = new URoUkitSmartCommand("CMD_SRV_SW_VER", 0x3, 4);
    URoUkitSmartCommand CMD_MTR_SW_VER = new URoUkitSmartCommand("CMD_MTR_SW_VER", 0x4, 4);
    URoUkitSmartCommand CMD_IR_SW_VER = new URoUkitSmartCommand("CMD_IR_SW_VER", 0x5, 4);
    URoUkitSmartCommand CMD_TH_SW_VER = new URoUkitSmartCommand("CMD_TH_SW_VER", 0x6, 4);
    URoUkitSmartCommand CMD_SND_SW_VER = new URoUkitSmartCommand("CMD_SND_SW_VER", 0x7, 4);
    URoUkitSmartCommand CMD_LED_SW_VER = new URoUkitSmartCommand("CMD_LED_SW_VER", 0x8, 4);
    URoUkitSmartCommand CMD_ULT_SW_VER = new URoUkitSmartCommand("CMD_ULT_SW_VER", 0x9, 4);
    URoUkitSmartCommand CMD_TCH_SW_VER = new URoUkitSmartCommand("CMD_TCH_SW_VER", 0xb, 4);
    URoUkitSmartCommand CMD_CLR_SW_VER = new URoUkitSmartCommand("CMD_CLR_SW_VER", 0xd, 4);
    URoUkitSmartCommand CMD_LGT_SW_VER = new URoUkitSmartCommand("CMD_LGT_SW_VER", 0x11, 4);
    URoUkitSmartCommand CMD_SPK_SW_VER = new URoUkitSmartCommand("CMD_SPK_SW_VER", 0x17, 4);
    URoUkitSmartCommand CMD_SUK_SW_VER = new URoUkitSmartCommand("CMD_SUK_SW_VER", 0x18, 4);
    URoUkitSmartCommand CMD_LED_BELT_SW_VER = new URoUkitSmartCommand("CMD_LED_BELT_SW_VER", 0x1A, 4);
    URoUkitSmartCommand CMD_LCD_SW_VER = new URoUkitSmartCommand("CMD_LCD_SW_VER", 0x1B, 4);
    URoUkitSmartCommand CMD_VISIONMODULE_SW_VER = new URoUkitSmartCommand("CMD_VISIONMODULE_SW_VER", 0x20, 4);


    URoUkitSmartCommand CMD_INTELLIGENT_DEV_SELF_CHECKING_REPORT = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_SELF_CHECKING_REPORT", 0x2, 2503);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_INFO_GET = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_INFO_GET", 0x2, 2504);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_UPDATE_REPORT = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_UPDATE_REPORT", 0x2, 2506);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_DEBUG_SWITCH = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_DEBUG_SWITCH", 0x2, 2508);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_WIFI_SSID_SET = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_WIFI_SSID_SET", 0x2, 2509);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_WIFI_LIST_GET = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_WIFI_LIST_GET", 0x2, 2510);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_RECORDING_FILE_REPORT = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_RECORDING_FILE_REPORT", 0x2, 3000);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_RECORDING_FILE_INFO = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_RECORDING_FILE_INFO", 0x2, 3001);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_FAULT_CLEAR = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_FAULT_CLEAR", 0x2, 2501);


    URoUkitSmartCommand CMD_INTELLIGENT_DEV_CHANGE_SWITCH_SET = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_CHANGE_SWITCH_SET", 0x2, 2507);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_WIFI_INFO_GET = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_WIFI_INFO_GET", 0x2, 2512);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_WIFI_INFO_REPORT = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_WIFI_INFO_REPORT", 0x2, 2513);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_WIFI_CONNECT_SET = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_WIFI_CONNECT_SET", 0x2, 2511);

    URoUkitSmartCommand CMD_INTELLIGENT_DEV_NETWORK_INFO_GET = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_NETWORK_INFO_GET", 0x2, 2518);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_NETWORK_INFO_REPORT = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_NETWORK_INFO_REPORT", 0x2, 2519);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_VERSION_INFO_GET = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_VERSION_INFO_GET", 0x2, 2521);


    URoUkitSmartCommand CMD_BAT_STATUS_REPORT = new URoUkitSmartCommand("CMD_BAT_STATUS_REPORT", 0xf, 1007);

//    URoUkitSmartCommand CMD_WORK_MODE_GET = new URoUkitSmartCommand("CMD_WORK_MODE_GET", 0x2, 12);
//    URoUkitSmartCommand CMD_WORK_MODE_SET = new URoUkitSmartCommand("CMD_WORK_MODE_SET", 0x2, 13);
//    URoUkitSmartCommand CMD_UPLOAD_TIME_SET = new URoUkitSmartCommand("CMD_UPLOAD_TIME_SET", 0x2, 14);
//    URoUkitSmartCommand CMD_UPLOAD_THRESHOLD_SET = new URoUkitSmartCommand("CMD_UPLOAD_THRESHOLD_SET", 0x2, 15);
//    URoUkitSmartCommand CMD_UPLOAD_OFFSET_SET = new URoUkitSmartCommand("CMD_UPLOAD_OFFSET_SET", 0x2, 16);
    URoUkitSmartCommand CMD_IR_DIS_PUSH = new URoUkitSmartCommand("CMD_IR_DIS_PUSH", 0x5, 1003);
    URoUkitSmartCommand CMD_TH_PUSH = new URoUkitSmartCommand("CMD_TH_PUSH", 0x6, 1001);
    URoUkitSmartCommand CMD_SND_ADC_VALUE_PUSH = new URoUkitSmartCommand("CMD_SND_ADC_VALUE_PUSH", 0x7, 1005);
    URoUkitSmartCommand CMD_ULT_DIS_PUSH = new URoUkitSmartCommand("CMD_ULT_DIS_PUSH", 0x9, 1002);
    URoUkitSmartCommand CMD_TCH_TYPE_PUSH = new URoUkitSmartCommand("CMD_TCH_TYPE_PUSH", 0xb, 1006);
    URoUkitSmartCommand CMD_CLR_RGB_PUSH = new URoUkitSmartCommand("CMD_CLR_RGB_PUSH", 0xd, 1003);
    URoUkitSmartCommand CMD_LGT_VALUE_PUSH = new URoUkitSmartCommand("CMD_LGT_VALUE_PUSH", 0x11, 1004);URoUkitSmartCommand CMD_WORK_MODE_IR_GET = new URoUkitSmartCommand("CMD_WORK_MODE_IR_GET", 0x5, 12);
    URoUkitSmartCommand CMD_WORK_MODE_IR_SET = new URoUkitSmartCommand("CMD_WORK_MODE_IR_SET", 0x5, 13);
    URoUkitSmartCommand CMD_UPLOAD_TIME_IR_SET = new URoUkitSmartCommand("CMD_UPLOAD_TIME_IR_SET", 0x5, 14);
    URoUkitSmartCommand CMD_UPLOAD_THRESHOLD_IR_SET = new URoUkitSmartCommand("CMD_UPLOAD_THRESHOLD_IR_SET", 0x5, 15);
    URoUkitSmartCommand CMD_UPLOAD_OFFSET_IR_SET = new URoUkitSmartCommand("CMD_UPLOAD_OFFSET_IR_SET", 0x5, 16);
    URoUkitSmartCommand CMD_WORK_MODE_TH_GET = new URoUkitSmartCommand("CMD_WORK_MODE_TH_GET", 0x6, 12);
    URoUkitSmartCommand CMD_WORK_MODE_TH_SET = new URoUkitSmartCommand("CMD_WORK_MODE_TH_SET", 0x6, 13);
    URoUkitSmartCommand CMD_UPLOAD_TIME_TH_SET = new URoUkitSmartCommand("CMD_UPLOAD_TIME_TH_SET", 0x6, 14);
    URoUkitSmartCommand CMD_UPLOAD_THRESHOLD_TH_SET = new URoUkitSmartCommand("CMD_UPLOAD_THRESHOLD_TH_SET", 0x6, 15);
    URoUkitSmartCommand CMD_UPLOAD_OFFSET_TH_SET = new URoUkitSmartCommand("CMD_UPLOAD_OFFSET_TH_SET", 0x6, 16);
    URoUkitSmartCommand CMD_WORK_MODE_SND_GET = new URoUkitSmartCommand("CMD_WORK_MODE_SND_GET", 0x7, 12);
    URoUkitSmartCommand CMD_WORK_MODE_SND_SET = new URoUkitSmartCommand("CMD_WORK_MODE_SND_SET", 0x7, 13);
    URoUkitSmartCommand CMD_UPLOAD_TIME_SND_SET = new URoUkitSmartCommand("CMD_UPLOAD_TIME_SND_SET", 0x7, 14);
    URoUkitSmartCommand CMD_UPLOAD_THRESHOLD_SND_SET = new URoUkitSmartCommand("CMD_UPLOAD_THRESHOLD_SND_SET", 0x7, 15);
    URoUkitSmartCommand CMD_UPLOAD_OFFSET_SND_SET = new URoUkitSmartCommand("CMD_UPLOAD_OFFSET_SND_SET", 0x7, 16);
    URoUkitSmartCommand CMD_WORK_MODE_ULT_GET = new URoUkitSmartCommand("CMD_WORK_MODE_ULT_GET", 0x9, 12);
    URoUkitSmartCommand CMD_WORK_MODE_ULT_SET = new URoUkitSmartCommand("CMD_WORK_MODE_ULT_SET", 0x9, 13);
    URoUkitSmartCommand CMD_UPLOAD_TIME_ULT_SET = new URoUkitSmartCommand("CMD_UPLOAD_TIME_ULT_SET", 0x9, 14);
    URoUkitSmartCommand CMD_UPLOAD_THRESHOLD_ULT_SET = new URoUkitSmartCommand("CMD_UPLOAD_THRESHOLD_ULT_SET", 0x9, 15);
    URoUkitSmartCommand CMD_UPLOAD_OFFSET_ULT_SET = new URoUkitSmartCommand("CMD_UPLOAD_OFFSET_ULT_SET", 0x9, 16);
    URoUkitSmartCommand CMD_WORK_MODE_TCH_GET = new URoUkitSmartCommand("CMD_WORK_MODE_TCH_GET", 0xb, 12);
    URoUkitSmartCommand CMD_WORK_MODE_TCH_SET = new URoUkitSmartCommand("CMD_WORK_MODE_TCH_SET", 0xb, 13);
    URoUkitSmartCommand CMD_UPLOAD_TIME_TCH_SET = new URoUkitSmartCommand("CMD_UPLOAD_TIME_TCH_SET", 0xb, 14);
    URoUkitSmartCommand CMD_UPLOAD_THRESHOLD_TCH_SET = new URoUkitSmartCommand("CMD_UPLOAD_THRESHOLD_TCH_SET", 0xb, 15);
    URoUkitSmartCommand CMD_UPLOAD_OFFSET_TCH_SET = new URoUkitSmartCommand("CMD_UPLOAD_OFFSET_TCH_SET", 0xb, 16);
    URoUkitSmartCommand CMD_WORK_MODE_CLR_GET = new URoUkitSmartCommand("CMD_WORK_MODE_CLR_GET", 0xd, 12);
    URoUkitSmartCommand CMD_WORK_MODE_CLR_SET = new URoUkitSmartCommand("CMD_WORK_MODE_CLR_SET", 0xd, 13);
    URoUkitSmartCommand CMD_UPLOAD_TIME_CLR_SET = new URoUkitSmartCommand("CMD_UPLOAD_TIME_CLR_SET", 0xd, 14);
    URoUkitSmartCommand CMD_UPLOAD_THRESHOLD_CLR_SET = new URoUkitSmartCommand("CMD_UPLOAD_THRESHOLD_CLR_SET", 0xd, 15);
    URoUkitSmartCommand CMD_UPLOAD_OFFSET_CLR_SET = new URoUkitSmartCommand("CMD_UPLOAD_OFFSET_CLR_SET", 0xd, 16);
    URoUkitSmartCommand CMD_WORK_MODE_LGT_GET = new URoUkitSmartCommand("CMD_WORK_MODE_LGT_GET", 0x11, 12);
    URoUkitSmartCommand CMD_WORK_MODE_LGT_SET = new URoUkitSmartCommand("CMD_WORK_MODE_LGT_SET", 0x11, 13);
    URoUkitSmartCommand CMD_UPLOAD_TIME_LGT_SET = new URoUkitSmartCommand("CMD_UPLOAD_TIME_LGT_SET", 0x11, 14);
    URoUkitSmartCommand CMD_UPLOAD_THRESHOLD_LGT_SET = new URoUkitSmartCommand("CMD_UPLOAD_THRESHOLD_LGT_SET", 0x11, 15);
    URoUkitSmartCommand CMD_UPLOAD_OFFSET_LGT_SET = new URoUkitSmartCommand("CMD_UPLOAD_OFFSET_LGT_SET", 0x11, 16);


    URoUkitSmartCommand CMD_UPDATE_STOP = new URoUkitSmartCommand("CMD_UPDATE_STOP", 0x2, 154);
    URoUkitSmartCommand CMD_DEV_UPDATE_STOP = new URoUkitSmartCommand("CMD_DEV_UPDATE_STOP", 0x2, 262);


    URoUkitSmartCommand CMD_VR_FILE_DEL = new URoUkitSmartCommand("CMD_VR_FILE_DEL", 0xe, 1020);
    URoUkitSmartCommand CMD_VR_FILE_RENAME = new URoUkitSmartCommand("CMD_VR_FILE_RENAME", 0xe, 1021);
    URoUkitSmartCommand CMD_VR_FILE_LIST = new URoUkitSmartCommand("CMD_VR_FILE_LIST", 0xe, 1022);
    URoUkitSmartCommand CMD_VR_START_RECORD = new URoUkitSmartCommand("CMD_VR_START_RECORD", 0xe, 1023);
    URoUkitSmartCommand CMD_VR_STOP_RECORD = new URoUkitSmartCommand("CMD_VR_STOP_RECORD", 0xe, 1024);
    URoUkitSmartCommand CMD_VR_RECORD_INFO_REPORT = new URoUkitSmartCommand("CMD_VR_RECORD_INFO_REPORT", 0xe, 1025);
    URoUkitSmartCommand CMD_VR_EFFECT_PLAY = new URoUkitSmartCommand("CMD_VR_EFFECT_PLAY", 0xe, 1026);
    URoUkitSmartCommand CMD_VR_RECORD_PLAY_START = new URoUkitSmartCommand("CMD_VR_RECORD_PLAY_START", 0xe, 1027);
    URoUkitSmartCommand CMD_VR_FILE_PLAY_STOP = new URoUkitSmartCommand("CMD_VR_FILE_PLAY_STOP", 0xe, 1028);
    URoUkitSmartCommand CMD_VR_PLAY_INFO_REPORT = new URoUkitSmartCommand("CMD_VR_PLAY_INFO_REPORT", 0xe, 1029);
    URoUkitSmartCommand CMD_VR_TTS_PLAY = new URoUkitSmartCommand("CMD_VR_TTS_PLAY", 0xe, 1030);
    URoUkitSmartCommand CMD_VR_TTS_INFO_REPORT = new URoUkitSmartCommand("CMD_VR_TTS_INFO_REPORT", 0xe, 1031);
    URoUkitSmartCommand CMD_VR_ASR_START = new URoUkitSmartCommand("CMD_VR_ASR_START", 0xe, 1032);
    URoUkitSmartCommand CMD_VR_ASR_STOP = new URoUkitSmartCommand("CMD_VR_ASR_STOP", 0xe, 1033);
    URoUkitSmartCommand CMD_VR_ASR_INFO_REPORT = new URoUkitSmartCommand("CMD_VR_ASR_INFO_REPORT", 0xe, 1034);
    URoUkitSmartCommand CMD_VR_SET_VOLUME = new URoUkitSmartCommand("CMD_VR_SET_VOLUME", 0xe, 1035);
    URoUkitSmartCommand CMD_VR_GET_VOLUME = new URoUkitSmartCommand("CMD_VR_GET_VOLUME", 0xe, 1036);
    URoUkitSmartCommand CMD_VR_SET_LANGUAGE = new URoUkitSmartCommand("CMD_VR_SET_LANGUAGE", 0xe, 1037);
    URoUkitSmartCommand CMD_VR_GET_LANGUAGE = new URoUkitSmartCommand("CMD_VR_GET_LANGUAGE", 0xe, 1038);


    URoUkitSmartCommand CMD_VR_FILE_NUM = new URoUkitSmartCommand("CMD_VR_FILE_NUM", 0xe, 1039);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_BLE_MAC_GET = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_BLE_MAC_GET", 0x2, 2522);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_WIFI_MAC_GET = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_WIFI_MAC_GET", 0x2, 2523);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_IP_INFO_GET = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_IP_INFO_GET", 0x2, 2524);


    URoUkitSmartCommand CMD_SUCKER_STATUS_SET = new URoUkitSmartCommand("CMD_SUCKER_STATUS_SET", 0x18, 1000);
    URoUkitSmartCommand CMD_SUCKER_STATUS_GET = new URoUkitSmartCommand("CMD_SUCKER_STATUS_GET", 0x18, 1001);


    URoUkitSmartCommand CMD_INTELLIGENT_DEV_DEBUG_INFO_REPORT = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_DEBUG_INFO_REPORT", 0x2, 2527);
    
    
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_REDIRECT_SWITCH_SET = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_REDIRECT_SWITCH_SET", 0x2, 2528);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_STREAM_SWITCH_SET = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_STREAM_SWITCH_SET", 0x2, 2529);

    URoUkitSmartCommand CMD_RESET_STATE_SET = new URoUkitSmartCommand("CMD_RESET_STATE_SET", 0x2, 26);

    URoUkitSmartCommand CMD_INTELLIGENT_DEV_COMPLEX_SET = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_COMPLEX_SET", 0x2, 3500);
    URoUkitSmartCommand CMD_INTELLIGENT_DEV_COMPLEX_GET = new URoUkitSmartCommand("CMD_INTELLIGENT_DEV_COMPLEX_GET", 0x2, 3501);

    URoUkitSmartCommand CMD_VR_RECORD_CANCEL = new URoUkitSmartCommand("CMD_VR_RECORD_CANCEL", 0xe, 1040);


    URoUkitSmartCommand CMD_LED_BELT_EXP_SET = new URoUkitSmartCommand("CMD_LED_BELT_EXP_SET", 0x1a, 1000);
    URoUkitSmartCommand CMD_LED_BELT_LEDS_NUM_GET = new URoUkitSmartCommand("CMD_LED_BELT_LEDS_NUM_GET", 0x1a, 1001);
    URoUkitSmartCommand CMD_LED_BELT_FIX_EXP_SET = new URoUkitSmartCommand("CMD_LED_BELT_FIX_EXP_SET", 0x1a, 1002);
    URoUkitSmartCommand CMD_LED_BELT_BRIGHTNESS_SET = new URoUkitSmartCommand("CMD_LED_BELT_BRIGHTNESS_SET", 0x1a, 1003);
    URoUkitSmartCommand CMD_LED_BELT_OFF_SET = new URoUkitSmartCommand("CMD_LED_BELT_OFF_SET", 0x1a, 1004);
    URoUkitSmartCommand CMD_LED_BELT_MOVE_SET = new URoUkitSmartCommand("CMD_LED_BELT_MOVE_SET", 0x1a, 1005);
    URoUkitSmartCommand CMD_LED_BELT_EXPRESSIONS_CONTINUOUS_SET = new URoUkitSmartCommand("CMD_LED_BELT_EXPRESSIONS_CONTINUOUS_SET", 0x1a, 1006);
    URoUkitSmartCommand CMD_LED_BELT_EXPRESSIONS_CONTINUOUS_BREATH_SET = new URoUkitSmartCommand("CMD_LED_BELT_EXPRESSIONS_CONTINUOUS_BREATH_SET", 0x1a, 1007);


    URoUkitSmartCommand CMD_LCD_GUI_VER_GET = new URoUkitSmartCommand("CMD_LCD_GUI_VER_GET", 0x1b, 1000);
    URoUkitSmartCommand CMD_LCD_CLEAR = new URoUkitSmartCommand("CMD_LCD_CLEAR", 0x1b, 1001);
    URoUkitSmartCommand CMD_LCD_BACK_LIGHT_SET = new URoUkitSmartCommand("CMD_LCD_BACK_LIGHT_SET", 0x1b, 1002);
    URoUkitSmartCommand CMD_LCD_BACKGROUND_COLOR_SET = new URoUkitSmartCommand("CMD_LCD_BACKGROUND_COLOR_SET", 0x1b, 1003);
    URoUkitSmartCommand CMD_LCD_BAR_DISPLAY = new URoUkitSmartCommand("CMD_LCD_BAR_DISPLAY", 0x1b, 1004);
    URoUkitSmartCommand CMD_LCD_STATIC_TEXT = new URoUkitSmartCommand("CMD_LCD_STATIC_TEXT", 0x1b, 1005);
    URoUkitSmartCommand CMD_LCD_ROLL_TEXT = new URoUkitSmartCommand("CMD_LCD_ROLL_TEXT", 0x1b, 1006);
    URoUkitSmartCommand CMD_LCD_ICON = new URoUkitSmartCommand("CMD_LCD_ICON", 0x1b, 1007);
    URoUkitSmartCommand CMD_LCD_INNER_PIC = new URoUkitSmartCommand("CMD_LCD_INNER_PIC", 0x1b, 1008);
    URoUkitSmartCommand CMD_LCD_PIC_DISPLAY = new URoUkitSmartCommand("CMD_LCD_PIC_DISPLAY", 0x1b, 1009);
    URoUkitSmartCommand CMD_LCD_SENSOR_DISPLAY = new URoUkitSmartCommand("CMD_LCD_SENSOR_DISPLAY", 0x1b, 1010);
    URoUkitSmartCommand CMD_LCD_SWITCH_PAGE = new URoUkitSmartCommand("CMD_LCD_SWITCH_PAGE", 0x1b, 1011);
    URoUkitSmartCommand CMD_LCD_GET_PAGE = new URoUkitSmartCommand("CMD_LCD_GET_PAGE", 0x1b, 1012);
    URoUkitSmartCommand CMD_LCD_TEST_PIC = new URoUkitSmartCommand("CMD_LCD_TEST_PIC", 0x1b, 1013);
    URoUkitSmartCommand CMD_LCD_FILE_SEND_MAX_PACK_SIZE = new URoUkitSmartCommand("CMD_LCD_FILE_SEND_MAX_PACK_SIZE", 0x1b, 100);
    URoUkitSmartCommand CMD_LCD_FILE_SEND_START = new URoUkitSmartCommand("CMD_LCD_FILE_SEND_START", 0x1b, 101);
    URoUkitSmartCommand CMD_LCD_FILE_SENDING = new URoUkitSmartCommand("CMD_LCD_FILE_SENDING", 0x1b, 102);
    URoUkitSmartCommand CMD_LCD_FILE_SEND_OVER = new URoUkitSmartCommand("CMD_LCD_FILE_SEND_OVER", 0x1b, 103);
    URoUkitSmartCommand CMD_LCD_FILE_SEND_STOP = new URoUkitSmartCommand("CMD_LCD_FILE_SEND_STOP", 0x1b, 104);
    URoUkitSmartCommand CMD_LCD_FILE_RECV_MAX_PACK_SIZE = new URoUkitSmartCommand("CMD_LCD_FILE_RECV_MAX_PACK_SIZE", 0x1b, 105);
    URoUkitSmartCommand CMD_LCD_FILE_RECV_START = new URoUkitSmartCommand("CMD_LCD_FILE_RECV_START", 0x1b, 106);
    URoUkitSmartCommand CMD_LCD_FILE_RECVING = new URoUkitSmartCommand("CMD_LCD_FILE_RECVING", 0x1b, 107);
    URoUkitSmartCommand CMD_LCD_FILE_RECV_OVER = new URoUkitSmartCommand("CMD_LCD_FILE_RECV_OVER", 0x1b, 108);
    URoUkitSmartCommand CMD_LCD_FILE_RECV_STOP = new URoUkitSmartCommand("CMD_LCD_FILE_RECV_STOP", 0x1b, 109);
    URoUkitSmartCommand CMD_LCD_FILE_DEL = new URoUkitSmartCommand("CMD_LCD_FILE_DEL", 0x1b, 110);
    URoUkitSmartCommand CMD_LCD_FILE_RENAME = new URoUkitSmartCommand("CMD_LCD_FILE_RENAME", 0x1b, 111);
    URoUkitSmartCommand CMD_LCD_FILE_STATE = new URoUkitSmartCommand("CMD_LCD_FILE_STATE", 0x1b, 112);
    URoUkitSmartCommand CMD_LCD_DIR_MAKE = new URoUkitSmartCommand("CMD_LCD_DIR_MAKE", 0x1b, 113);
    URoUkitSmartCommand CMD_LCD_DIR_DEL = new URoUkitSmartCommand("CMD_LCD_DIR_DEL", 0x1b, 114);
    URoUkitSmartCommand CMD_LCD_DIR_F_CNT_GET = new URoUkitSmartCommand("CMD_LCD_DIR_F_CNT_GET", 0x1b, 115);
    URoUkitSmartCommand CMD_LCD_DIR_F_INFO_GET = new URoUkitSmartCommand("CMD_LCD_DIR_F_INFO_GET", 0x1b, 116);
    URoUkitSmartCommand CMD_LCD_RECOVER = new URoUkitSmartCommand("CMD_LCD_RECOVER", 0x1b, 6);


    URoUkitSmartCommand CMD_VISIONMODULE_MID_OFFSET = new URoUkitSmartCommand("CMD_VISIONMODULE_MID_OFFSET", 0x20, 1003);
    URoUkitSmartCommand CMD_VISIONMODULE_JPEG_QUALITY_SET = new URoUkitSmartCommand("CMD_VISIONMODULE_JPEG_QUALITY_SET", 0x20, 1004);
    URoUkitSmartCommand CMD_VISIONMODULE_IDENTIFY = new URoUkitSmartCommand("CMD_VISIONMODULE_IDENTIFY", 0x20, 1005);
    URoUkitSmartCommand CMD_VISIONMODULE_FACE_RECORD = new URoUkitSmartCommand("CMD_VISIONMODULE_FACE_RECORD", 0x20, 1006);
    URoUkitSmartCommand CMD_VISIONMODULE_FACE_INFOLIST_GET = new URoUkitSmartCommand("CMD_VISIONMODULE_FACE_INFOLIST_GET", 0x20, 1007);
    URoUkitSmartCommand CMD_VISIONMODULE_MODEL_SWITCH = new URoUkitSmartCommand("CMD_VISIONMODULE_MODEL_SWITCH", 0x20, 1008);
    URoUkitSmartCommand CMD_VISIONMODULE_FACE_NAME_MODIFY = new URoUkitSmartCommand("CMD_VISIONMODULE_FACE_NAME_MODIFY", 0x20, 1009);
    URoUkitSmartCommand CMD_VISIONMODULE_FACE_DELETE = new URoUkitSmartCommand("CMD_VISIONMODULE_FACE_DELETE", 0x20, 1010);
    URoUkitSmartCommand CMD_VISIONMODULE_ONLINE_IDENTIFY = new URoUkitSmartCommand("CMD_VISIONMODULE_ONLINE_IDENTIFY", 0x20, 1011);
    URoUkitSmartCommand CMD_VISIONMODULE_WIFI_SSID_SET = new URoUkitSmartCommand("CMD_VISIONMODULE_WIFI_SSID_SET", 0x20, 300);
    URoUkitSmartCommand CMD_VISIONMODULE_WIFI_LIST_GET = new URoUkitSmartCommand("CMD_VISIONMODULE_WIFI_LIST_GET", 0x20, 301);
    URoUkitSmartCommand CMD_VISIONMODULE_WIFI_CONNECT_SET = new URoUkitSmartCommand("CMD_VISIONMODULE_WIFI_CONNECT_SET", 0x20, 302);
    URoUkitSmartCommand CMD_VISIONMODULE_WIFI_INFO_GET = new URoUkitSmartCommand("CMD_VISIONMODULE_WIFI_INFO_GET", 0x20, 303);
    URoUkitSmartCommand CMD_VISIONMODULE_WIFI_INFO_REPORT = new URoUkitSmartCommand("CMD_VISIONMODULE_WIFI_INFO_REPORT", 0x20, 304);
    URoUkitSmartCommand CMD_VISIONMODULE_NETWORK_INFO_GET = new URoUkitSmartCommand("CMD_VISIONMODULE_NETWORK_INFO_GET", 0x20, 305);
    URoUkitSmartCommand CMD_VISIONMODULE_NETWORK_INFO_REPORT = new URoUkitSmartCommand("CMD_VISIONMODULE_NETWORK_INFO_REPORT", 0x20, 306);
    URoUkitSmartCommand CMD_VISIONMODULE_WIFI_MAC_GET = new URoUkitSmartCommand("CMD_VISIONMODULE_WIFI_MAC_GET", 0x20, 307);
    URoUkitSmartCommand CMD_VISIONMODULE_IP_INFO_GET = new URoUkitSmartCommand("CMD_VISIONMODULE_IP_INFO_GET", 0x20, 308);
    URoUkitSmartCommand CMD_VISIONMODULE_BT_INFO_GET = new URoUkitSmartCommand("CMD_VISIONMODULE_BT_INFO_GET", 0x20, 350);
    URoUkitSmartCommand CMD_VISIONMODULE_BT_INFO_REPORT = new URoUkitSmartCommand("CMD_VISIONMODULE_BT_INFO_REPORT", 0x20, 351);
    URoUkitSmartCommand CMD_VISIONMODULE_BLE_MAC_GET = new URoUkitSmartCommand("CMD_VISIONMODULE_BLE_MAC_GET", 0x20, 352);
    URoUkitSmartCommand CMD_VISIONMODULE_PC_LINK_INFO_GET = new URoUkitSmartCommand("CMD_VISIONMODULE_PC_LINK_INFO_GET", 0x20, 400);
    URoUkitSmartCommand CMD_VISIONMODULE_PC_LINK_INFO_REPORT = new URoUkitSmartCommand("CMD_VISIONMODULE_PC_LINK_INFO_REPORT", 0x20, 401);
    URoUkitSmartCommand CMD_VISIONMODULE_PRODUCT_INFO_GET = new URoUkitSmartCommand("CMD_VISIONMODULE_PRODUCT_INFO_GET", 0x20, 402);
    URoUkitSmartCommand CMD_VISIONMODULE_VERSION_INFO_GET = new URoUkitSmartCommand("CMD_VISIONMODULE_VERSION_INFO_GET", 0x20, 403);
    URoUkitSmartCommand CMD_VISIONMODULE_UPDATE_STOP = new URoUkitSmartCommand("CMD_UPDATE_STOP", 0x20, 154);
    URoUkitSmartCommand CMD_VISIONMODULE_UPDATE_INFO_SET = new URoUkitSmartCommand("CMD_UPDATE_INFO_SET", 0x20, 256);
    URoUkitSmartCommand CMD_VISIONMODULE_FILE_DOWNLOAD_PROGRESS_QUERY = new URoUkitSmartCommand("CMD_FILE_DOWNLOAD_PROGRESS_QUERY", 0x20, 260);
    URoUkitSmartCommand CMD_VISIONMODULE_PART_INFO_SET = new URoUkitSmartCommand("CMD_VISIONMODULE_PART_INFO_SET", 0x20, 205);
    URoUkitSmartCommand CMD_VISIONMODULE_PART_DOWNLOAD_PROGRESS_QUERY = new URoUkitSmartCommand("CMD_VISIONMODULE_PART_DOWNLOAD_PROGRESS_QUERY", 0x20, 208);
    URoUkitSmartCommand CMD_VISIONMODULE_PART_SW_VER = new URoUkitSmartCommand("CMD_VISIONMODULE_PART_SW_VER", 0x20, 210);
    URoUkitSmartCommand CMD_VISIONMODULE_PART_UPDATE_STOP = new URoUkitSmartCommand("CMD_VISIONMODULE_PART_UPDATE_STOP", 0x20, 211);
    URoUkitSmartCommand CMD_VISIONMODULE_DEV_UPDATE_STOP = new URoUkitSmartCommand("CMD_VISIONMODULE_DEV_UPDATE_STOP", 0x20, 262);

}
