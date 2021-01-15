package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol;

import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.product.core.protocol.URoProtocolFormatter;
import com.ubtedu.deviceconnect.libs.base.product.core.protocol.URoProtocolHandler;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommand;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoCommandConstants;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoUkitSmartComponentMap;
import com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter.*;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbMessageHeader;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * @Author naOKi
 * @Date 2019/06/17
 **/
public class URoUkitSmartProtocolHandler extends URoProtocolHandler {

    public static final byte[] HEAD = { (byte)0xDB };

    private static final HashMap<URoUkitSmartCommand, URoProtocolFormatter> dictType2Formatter = new HashMap<>();

    private static void registerCommandType(URoUkitSmartCommand cmd, URoProtocolFormatter formatter) {
        dictType2Formatter.put(cmd, formatter);
    }

    static {
        registerCommandType(URoCommandConstants.CMD_SN_GET, URoUkitSmartSnGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SN_SET, URoUkitSmartSnSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_HW_VER, URoUkitSmartHwVerFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SW_VER, URoUkitSmartSwVerFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_PT_VER, URoUkitSmartPtVerFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_RECOVER, URoUkitSmartRecoverFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_RESET, URoUkitSmartResetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_HEARTBEAT, URoUkitSmartHeartbeatFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_START, URoUkitSmartStartFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_STOP, URoUkitSmartStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_STATE_GET, URoUkitSmartStateGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_TYPE_GET, URoUkitSmartTypeGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_RANDOM_GET, URoUkitSmartRandomGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SUBTYPE_SET, URoUkitSmartSubtypeSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_PARA_SET, URoUkitSmartParametersSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_PARA_GET, URoUkitSmartParametersGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_DEV_STATUS_LED_SET, URoUkitSmartDevStatusLedSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_MCU_SN_GET, URoUkitSmartMcuSnGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_FILE_SEND_MAX_PACK_SIZE, URoUkitSmartFileSendMaxPackSizeFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_FILE_SEND_START, URoUkitSmartFileSendStartFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_FILE_SENDING, URoUkitSmartFileSendingFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_FILE_SEND_OVER, URoUkitSmartFileSendOverFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_FILE_SEND_STOP, URoUkitSmartFileSendStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_FILE_RECV_MAX_PACK_SIZE, URoUkitSmartFileRecvMaxPackSizeFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_FILE_RECV_START, URoUkitSmartFileRecvStartFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_FILE_RECVING, URoUkitSmartFileRecvingFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_FILE_RECV_OVER, URoUkitSmartFileRecvOverFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_FILE_RECV_STOP, URoUkitSmartFileRecvStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_FILE_DEL, URoUkitSmartFileDelFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_FILE_RENAME, URoUkitSmartFileRenameFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_FILE_STATE, URoUkitSmartFileStateFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_DIR_MAKE, URoUkitSmartDirMakeFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_DIR_DEL, URoUkitSmartDirDelFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_DIR_F_CNT_GET, URoUkitSmartDirFCntGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_DIR_F_INFO_GET, URoUkitSmartDirFInfoGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SRV_ANGLE_SET, URoUkitSmartServoAngleSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SRV_ANGLE_GET, URoUkitSmartServoAngleGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SRV_PWM_SET, URoUkitSmartServoPwmSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SRV_WHEEL_SET, URoUkitSmartServoWheelSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SRV_STOP, URoUkitSmartServoStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SRV_CALIBRATION_SET, URoUkitSmartServoCalibrationSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SRV_CALIBRATION_GET, URoUkitSmartServoCalibrationGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SRV_FAULT_GET, URoUkitSmartServoFaultGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SRV_FAULT_CLEAR, URoUkitSmartServoFaultClearFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SRV_LIMIT_SET, URoUkitSmartServoLimitSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SRV_STATUS_GET, URoUkitSmartServoStatusGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SRV_INFO_GET, URoUkitSmartServoInfoGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_MTR_SPEED_SET, URoUkitSmartMotorSpeedSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_MTR_STOP, URoUkitSmartMotorStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_MTR_SPEED_GET, URoUkitSmartMotorSpeedGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_MTR_PWM_UPPER_LIMIT_SET, URoUkitSmartMotorPwmUpperLimitSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_MTR_PWM_SET, URoUkitSmartMotorPwmSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_MTR_PWM_GET, URoUkitSmartMotorPwmGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_MTR_CNT_GET, URoUkitSmartMotorCntGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_MTR_CNT_RESET, URoUkitSmartMotorCntResetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_MTR_FAULT_GET, URoUkitSmartMotorFaultGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_MTR_FAULT_CLEAR, URoUkitSmartMotorFaultClearFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_IR_ADC_GET, URoUkitSmartInfraredAdcGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_IR_DIS_GET, URoUkitSmartInfraredDistanceGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_IR_ONOFF_GET, URoUkitSmartInfraredOnoffGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_TH_GET, URoUkitSmartTemperatureHumidityGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SND_ADC_VALUE_GET, URoUkitSmartSoundAdcValueGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SND_HYSTERESIS_TIME_SET, URoUkitSmartSoundHysteresisTimeSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SND_HYSTERESIS_TIME_GET, URoUkitSmartSoundHysteresisTimeGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SND_CALIBRATE_SET, URoUkitSmartSoundCalibrateSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SND_CALIBRATE_GET, URoUkitSmartSoundCalibrateGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LED_FIX_EXP_SET, URoUkitSmartLedFixedExpressionsSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LED_EXP_SET, URoUkitSmartLedExpressionsSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_ULT_DIS_GET, URoUkitSmartUltrasonicDistanceGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_TCH_TIMES_GET, URoUkitSmartTouchTimesGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_TCH_TYPE_GET, URoUkitSmartTouchTypeGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_TCH_HYSTERESIS_TIME_SET, URoUkitSmartTouchHysteresisTimeSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_TCH_HYSTERESIS_TIME_GET, URoUkitSmartTouchHysteresisTimeGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_TCH_STATE_GET, URoUkitSmartTouchStateGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_CLR_RGB_GET, URoUkitSmartColorRgbGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_CLR_CAL_ONOFF, URoUkitSmartColorCalibrateCtrlFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_CLR_CAL_STATE_GET, URoUkitSmartColorCalibrateStateGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_BAT_PWR_PCT, URoUkitSmartBatteryPowerPercentFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_BAT_STATUS_GET, URoUkitSmartBatteryStatusGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_BAT_TEMPERATURE, URoUkitSmartBatteryTemperatureFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_BAT_VOL, URoUkitSmartBatteryVoltageFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_BAT_CHG_CUR, URoUkitSmartBatteryChargingCurrentFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_BAT_DCHG_CUR, URoUkitSmartBatteryDischargeCurrentFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_BAT_CAPACITY, URoUkitSmartBatteryCapacityFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LGT_VALUE_GET, URoUkitSmartLightValueGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LGT_STDVALUE_SET, URoUkitSmartLightStdvalueSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LGT_STDVALUE_GET, URoUkitSmartLightSdtvalueGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LGT_COE_GET, URoUkitSmartLightCoefficientGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_COMBO_OFF, URoUkitSmartIntelligentDevComboOffFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_EXEC, URoUkitSmartIntelligentDevscriptExecFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_STOP, URoUkitSmartIntelligentDevscriptStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_EXEC_REPORT, URoUkitSmartIntelligentDevscriptExecReportFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_PARAM_UPDATE, URoUkitSmartIntelligentDevscriptParamUpdateFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_PARAM_CLEAR, URoUkitSmartIntelligentDevscriptParamClearFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_EVENT_UPDATE, URoUkitSmartIntelligentDevScriptEventUpdateFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_SCRIPT_EVENT_CLEAR, URoUkitSmartIntelligentDevScriptEventClearFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_INFO_GET, URoUkitSmartIntelligentDevInfoGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_SENSOR_READ, URoUkitSmartIntelligentDevSensorReadFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_UPDATE_REPORT, URoUkitSmartIntelligentDevUpdateReportFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SRV_ID_SET, URoUkitSmartIdSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_MTR_ID_SET, URoUkitSmartIdSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_IR_ID_SET, URoUkitSmartIdSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_TH_ID_SET, URoUkitSmartIdSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SND_ID_SET, URoUkitSmartIdSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LED_ID_SET, URoUkitSmartIdSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_ULT_ID_SET, URoUkitSmartIdSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_TCH_ID_SET, URoUkitSmartIdSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_CLR_ID_SET, URoUkitSmartIdSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LGT_ID_SET, URoUkitSmartIdSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SUK_ID_SET, URoUkitSmartIdSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LED_BELT_ID_SET, URoUkitSmartIdSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_ACTION_COMPLETED_REPORT, URoUkitSmartActionCompletedReportFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPDATE_INFO_SET, URoUkitSmartUpdateInfoSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_FILE_DOWNLOAD_PROGRESS_REPORT, URoUkitSmartFileDownloadProgressReportFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPDATE_STATUS_REPORT, URoUkitSmartUpdateStatusReportFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_ULT_LIGHT_SET, URoUkitSmartUltrasonicLightSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SRV_ON, URoUkitSmartStartFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SRV_OFF, URoUkitSmartStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_MTR_ON, URoUkitSmartStartFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_MTR_OFF, URoUkitSmartStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_IR_ON, URoUkitSmartStartFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_IR_OFF, URoUkitSmartStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_TH_ON, URoUkitSmartStartFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_TH_OFF, URoUkitSmartStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SND_ON, URoUkitSmartStartFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SND_OFF, URoUkitSmartStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LED_ON, URoUkitSmartStartFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LED_OFF, URoUkitSmartStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_ULT_ON, URoUkitSmartStartFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_ULT_OFF, URoUkitSmartStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_TCH_ON, URoUkitSmartStartFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_TCH_OFF, URoUkitSmartStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_CLR_ON, URoUkitSmartStartFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_CLR_OFF, URoUkitSmartStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LGT_ON, URoUkitSmartStartFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LGT_OFF, URoUkitSmartStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SPK_GET, URoUkitSmartSpeakerGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SUK_ON, URoUkitSmartStartFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SUK_OFF, URoUkitSmartStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_ON, URoUkitSmartStartFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_OFF, URoUkitSmartStopFormatter.INSTANCE);


        registerCommandType(URoCommandConstants.CMD_SRV_RESET, URoUkitSmartResetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_MTR_RESET, URoUkitSmartResetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_IR_RESET, URoUkitSmartResetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_TH_RESET, URoUkitSmartResetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SND_RESET, URoUkitSmartResetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LED_RESET, URoUkitSmartResetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_ULT_RESET, URoUkitSmartResetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_TCH_RESET, URoUkitSmartResetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_CLR_RESET, URoUkitSmartResetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LGT_RESET, URoUkitSmartResetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SPK_RESET, URoUkitSmartResetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SUK_RESET, URoUkitSmartResetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LED_BELT_RESET, URoUkitSmartResetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_RESET, URoUkitSmartResetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_RESET, URoUkitSmartResetFormatter.INSTANCE);


        registerCommandType(URoCommandConstants.CMD_SRV_SW_VER, URoUkitSmartSwVerFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_MTR_SW_VER, URoUkitSmartSwVerFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_IR_SW_VER, URoUkitSmartSwVerFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_TH_SW_VER, URoUkitSmartSwVerFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SND_SW_VER, URoUkitSmartSwVerFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LED_SW_VER, URoUkitSmartSwVerFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_ULT_SW_VER, URoUkitSmartSwVerFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_TCH_SW_VER, URoUkitSmartSwVerFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_CLR_SW_VER, URoUkitSmartSwVerFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LGT_SW_VER, URoUkitSmartSwVerFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SPK_SW_VER, URoUkitSmartSwVerFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SUK_SW_VER, URoUkitSmartSwVerFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LED_BELT_SW_VER, URoUkitSmartSwVerFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_SW_VER, URoUkitSmartSwVerFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_SW_VER, URoUkitSmartSwVerFormatter.INSTANCE);


        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_SELF_CHECKING_REPORT, URoUkitSmartIntelligentDevSelfCheckingReportFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_INFO_GET, URoUkitSmartIntelligentDevInfoGetFormatter.INSTANCE);
//        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_UPDATE_REPORT, URoUkitSmartIntelligentDevUpdateReportFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_DEBUG_SWITCH, URoUkitSmartIntelligentDevDebugSwitchSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_WIFI_SSID_SET, URoUkitSmartIntelligentDevWifiSsidSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_WIFI_LIST_GET, URoUkitSmartIntelligentDevWifiListGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_RECORDING_FILE_REPORT, URoUkitSmartIntelligentDevRecordingFileReportFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_RECORDING_FILE_INFO, URoUkitSmartIntelligentDevRecordingFileInfoFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_FAULT_CLEAR, URoUkitSmartIntelligentDevFaultClearFormatter.INSTANCE);


        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_CHANGE_SWITCH_SET, URoUkitSmartIntelligentDevChangeSwitchSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_WIFI_INFO_GET, URoUkitSmartIntelligentDevWifiInfoGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_WIFI_INFO_REPORT, URoUkitSmartIntelligentDevWifiInfoReportFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_WIFI_CONNECT_SET, URoUkitSmartIntelligentDevWifiConnectSetFormatter.INSTANCE);


        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_NETWORK_INFO_GET, URoUkitSmartIntelligentDevNetworkInfoGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_NETWORK_INFO_REPORT, URoUkitSmartIntelligentDevNetworkInfoReportFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_VERSION_INFO_GET, URoUkitSmartIntelligentDevVersionInfoGetFormatter.INSTANCE);


        registerCommandType(URoCommandConstants.CMD_BAT_STATUS_REPORT, URoUkitSmartBatteryStatusReportFormatter.INSTANCE);


//        registerCommandType(URoCommandConstants.CMD_WORK_MODE_GET, URoUkitSmartWorkModeGetFormatter.INSTANCE);
//        registerCommandType(URoCommandConstants.CMD_WORK_MODE_SET, URoUkitSmartWorkModeSetFormatter.INSTANCE);
//        registerCommandType(URoCommandConstants.CMD_UPLOAD_TIME_SET, URoUkitSmartUploadTimeSetFormatter.INSTANCE);
//        registerCommandType(URoCommandConstants.CMD_UPLOAD_THRESHOLD_SET, URoUkitSmartUploadThresholdSetFormatter.INSTANCE);
//        registerCommandType(URoCommandConstants.CMD_UPLOAD_OFFSET_SET, URoUkitSmartUploadOffsetSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_IR_DIS_PUSH, URoUkitSmartInfraredDistancePushFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_TH_PUSH, URoUkitSmartTemperatureHumidityPushFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SND_ADC_VALUE_PUSH, URoUkitSmartSoundAdcValuePushFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_ULT_DIS_PUSH, URoUkitSmartUltrasonicDistancePushFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_TCH_TYPE_PUSH, URoUkitSmartTouchTypePushFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_CLR_RGB_PUSH, URoUkitSmartColorRgbPushFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LGT_VALUE_PUSH, URoUkitSmartLightValuePushFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_WORK_MODE_IR_GET, URoUkitSmartWorkModeGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_WORK_MODE_IR_SET, URoUkitSmartWorkModeSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPLOAD_TIME_IR_SET, URoUkitSmartUploadTimeSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPLOAD_THRESHOLD_IR_SET, URoUkitSmartUploadThresholdSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPLOAD_OFFSET_IR_SET, URoUkitSmartUploadOffsetSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_WORK_MODE_TH_GET, URoUkitSmartWorkModeGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_WORK_MODE_TH_SET, URoUkitSmartWorkModeSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPLOAD_TIME_TH_SET, URoUkitSmartUploadTimeSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPLOAD_THRESHOLD_TH_SET, URoUkitSmartUploadThresholdSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPLOAD_OFFSET_TH_SET, URoUkitSmartUploadOffsetSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_WORK_MODE_SND_GET, URoUkitSmartWorkModeGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_WORK_MODE_SND_SET, URoUkitSmartWorkModeSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPLOAD_TIME_SND_SET, URoUkitSmartUploadTimeSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPLOAD_THRESHOLD_SND_SET, URoUkitSmartUploadThresholdSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPLOAD_OFFSET_SND_SET, URoUkitSmartUploadOffsetSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_WORK_MODE_ULT_GET, URoUkitSmartWorkModeGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_WORK_MODE_ULT_SET, URoUkitSmartWorkModeSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPLOAD_TIME_ULT_SET, URoUkitSmartUploadTimeSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPLOAD_THRESHOLD_ULT_SET, URoUkitSmartUploadThresholdSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPLOAD_OFFSET_ULT_SET, URoUkitSmartUploadOffsetSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_WORK_MODE_TCH_GET, URoUkitSmartWorkModeGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_WORK_MODE_TCH_SET, URoUkitSmartWorkModeSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPLOAD_TIME_TCH_SET, URoUkitSmartUploadTimeSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPLOAD_THRESHOLD_TCH_SET, URoUkitSmartUploadThresholdSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPLOAD_OFFSET_TCH_SET, URoUkitSmartUploadOffsetSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_WORK_MODE_CLR_GET, URoUkitSmartWorkModeGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_WORK_MODE_CLR_SET, URoUkitSmartWorkModeSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPLOAD_TIME_CLR_SET, URoUkitSmartUploadTimeSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPLOAD_THRESHOLD_CLR_SET, URoUkitSmartUploadThresholdSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPLOAD_OFFSET_CLR_SET, URoUkitSmartUploadOffsetSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_WORK_MODE_LGT_GET, URoUkitSmartWorkModeGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_WORK_MODE_LGT_SET, URoUkitSmartWorkModeSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPLOAD_TIME_LGT_SET, URoUkitSmartUploadTimeSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPLOAD_THRESHOLD_LGT_SET, URoUkitSmartUploadThresholdSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_UPLOAD_OFFSET_LGT_SET, URoUkitSmartUploadOffsetSetFormatter.INSTANCE);


        registerCommandType(URoCommandConstants.CMD_UPDATE_STOP, URoUkitSmartUpdateStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_DEV_UPDATE_STOP, URoUkitSmartDevUpdateStopFormatter.INSTANCE);


        registerCommandType(URoCommandConstants.CMD_VR_FILE_DEL, URoUkitSmartVoiceFileDelFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VR_FILE_RENAME, URoUkitSmartVoiceFileRenameFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VR_FILE_LIST, URoUkitSmartVoiceFileListFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VR_START_RECORD, URoUkitSmartVoiceStartRecordFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VR_STOP_RECORD, URoUkitSmartVoiceStopRecordFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VR_RECORD_INFO_REPORT, URoUkitSmartVoiceRecordInfoReportFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VR_EFFECT_PLAY, URoUkitSmartVoiceEffectPlayStartFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VR_RECORD_PLAY_START, URoUkitSmartVoiceRecordPlayStartFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VR_FILE_PLAY_STOP, URoUkitSmartVoiceFilePlayStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VR_PLAY_INFO_REPORT, URoUkitSmartVoicePlayInfoReportFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VR_TTS_PLAY, URoUkitSmartVoiceTtsPlayFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VR_TTS_INFO_REPORT, URoUkitSmartVoiceTtsInfoReportFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VR_ASR_START, URoUkitSmartVoiceAsrStartFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VR_ASR_STOP, URoUkitSmartVoiceAsrStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VR_ASR_INFO_REPORT, URoUkitSmartVoiceAsrInfoReportFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VR_SET_VOLUME, URoUkitSmartVoiceSetVolumeFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VR_GET_VOLUME, URoUkitSmartVoiceGetVolumeFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VR_SET_LANGUAGE, URoUkitSmartVoiceSetLanguageFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VR_GET_LANGUAGE, URoUkitSmartVoiceGetLanguageFormatter.INSTANCE);


        registerCommandType(URoCommandConstants.CMD_VR_FILE_NUM, URoUkitSmartVoiceFileNumFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_BLE_MAC_GET, URoUkitSmartIntelligentDevBleMacGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_WIFI_MAC_GET, URoUkitSmartIntelligentDevWifiMacGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_IP_INFO_GET, URoUkitSmartIntelligentDevIpInfoGetFormatter.INSTANCE);


        registerCommandType(URoCommandConstants.CMD_SUCKER_STATUS_SET, URoUkitSmartSuckerStatusSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_SUCKER_STATUS_GET, URoUkitSmartSuckerStatusGetFormatter.INSTANCE);


        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_DEBUG_INFO_REPORT, URoUkitSmartIntelligentDevDebugInfoReportFormatter.INSTANCE);
    
    
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_REDIRECT_SWITCH_SET, URoUkitSmartIntelligentDevRedirectSwitchSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_STREAM_SWITCH_SET, URoUkitSmartIntelligentDevStreamSwitchSetFormatter.INSTANCE);

        registerCommandType(URoCommandConstants.CMD_RESET_STATE_SET, URoUkitSmartResetStateSetFormatter.INSTANCE);

        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_COMPLEX_SET, URoUkitSmartIntelligentDevComplexSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_INTELLIGENT_DEV_COMPLEX_GET, URoUkitSmartIntelligentDevComplexGetFormatter.INSTANCE);


        registerCommandType(URoCommandConstants.CMD_VR_RECORD_CANCEL, URoUkitSmartVoiceRecordCancelFormatter.INSTANCE);


        registerCommandType(URoCommandConstants.CMD_LED_BELT_EXP_SET, URoUkitSmartLedBeltExpressionsSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LED_BELT_LEDS_NUM_GET, URoUkitSmartLedBeltLedsNumGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LED_BELT_FIX_EXP_SET, URoUkitSmartLedBeltFixedExpressionsSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LED_BELT_BRIGHTNESS_SET, URoUkitSmartLedBeltBrightnessSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LED_BELT_OFF_SET, URoUkitSmartLedBeltOffSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LED_BELT_MOVE_SET, URoUkitSmartLedBeltMoveSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LED_BELT_EXPRESSIONS_CONTINUOUS_SET, URoUkitSmartLedBeltExpressionsContinuousSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LED_BELT_EXPRESSIONS_CONTINUOUS_BREATH_SET, URoUkitSmartLedBeltExpressionsContinuousBreathSetFormatter.INSTANCE);


        registerCommandType(URoCommandConstants.CMD_LCD_GUI_VER_GET, URoUkitSmartLcdGuiVerGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_CLEAR, URoUkitSmartLcdClearFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_BACK_LIGHT_SET, URoUkitSmartLcdBackLightSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_BACKGROUND_COLOR_SET, URoUkitSmartLcdBackgroundColorSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_BAR_DISPLAY, URoUkitSmartLcdBarDisplayFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_STATIC_TEXT, URoUkitSmartLcdStaticTextFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_ROLL_TEXT, URoUkitSmartLcdRollTextFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_ICON, URoUkitSmartLcdIconFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_INNER_PIC, URoUkitSmartLcdInnerPicFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_PIC_DISPLAY, URoUkitSmartLcdPicDisplayFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_SENSOR_DISPLAY, URoUkitSmartLcdSensorDisplayFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_SWITCH_PAGE, URoUkitSmartLcdSwitchPageFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_GET_PAGE, URoUkitSmartLcdGetPageFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_TEST_PIC, URoUkitSmartLcdTestPicFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_FILE_SEND_MAX_PACK_SIZE, URoUkitSmartFileSendMaxPackSizeFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_FILE_SEND_START, URoUkitSmartFileSendStartFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_FILE_SENDING, URoUkitSmartFileSendingFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_FILE_SEND_OVER, URoUkitSmartFileSendOverFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_FILE_SEND_STOP, URoUkitSmartFileSendStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_FILE_RECV_MAX_PACK_SIZE, URoUkitSmartFileRecvMaxPackSizeFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_FILE_RECV_START, URoUkitSmartFileRecvStartFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_FILE_RECVING, URoUkitSmartFileRecvingFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_FILE_RECV_OVER, URoUkitSmartFileRecvOverFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_FILE_RECV_STOP, URoUkitSmartFileRecvStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_FILE_DEL, URoUkitSmartFileDelFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_FILE_RENAME, URoUkitSmartFileRenameFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_FILE_STATE, URoUkitSmartFileStateFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_DIR_MAKE, URoUkitSmartDirMakeFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_DIR_DEL, URoUkitSmartDirDelFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_DIR_F_CNT_GET, URoUkitSmartDirFCntGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_DIR_F_INFO_GET, URoUkitSmartDirFInfoGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_LCD_RECOVER, URoUkitSmartRecoverFormatter.INSTANCE);


        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_MID_OFFSET, URoUkitSmartVisionmoduleMidOffsetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_JPEG_QUALITY_SET, URoUkitSmartVisionmoduleJpegQualitySetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_IDENTIFY, URoUkitSmartVisionmoduleIdentifyFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_FACE_RECORD, URoUkitSmartVisionmoduleFaceRecordFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_FACE_INFOLIST_GET, URoUkitSmartVisionmoduleFaceInfolistGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_MODEL_SWITCH, URoUkitSmartVisionmoduleModelSwitchFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_FACE_NAME_MODIFY, URoUkitSmartVisionmoduleFaceNameModifyFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_FACE_DELETE, URoUkitSmartVisionmoduleFaceDeleteFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_ONLINE_IDENTIFY, URoUkitSmartVisionmoduleOnlineIdentifyFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_WIFI_SSID_SET, URoUkitSmartWifiSsidSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_WIFI_LIST_GET, URoUkitSmartWifiListGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_WIFI_CONNECT_SET, URoUkitSmartWifiConnectSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_WIFI_INFO_GET, URoUkitSmartWifiInfoGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_WIFI_INFO_REPORT, URoUkitSmartWifiInfoReportFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_NETWORK_INFO_GET, URoUkitSmartNetworkInfoGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_NETWORK_INFO_REPORT, URoUkitSmartNetworkInfoReportFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_WIFI_MAC_GET, URoUkitSmartWifiMacGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_IP_INFO_GET, URoUkitSmartIpInfoGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_BT_INFO_GET, URoUkitSmartBtInfoGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_BT_INFO_REPORT, URoUkitSmartBtInfoReportFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_BLE_MAC_GET, URoUkitSmartBleMacGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_PC_LINK_INFO_GET, URoUkitSmartPcLinkInfoGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_PC_LINK_INFO_REPORT, URoUkitSmartPcLinkInfoReportFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_PRODUCT_INFO_GET, URoUkitSmartProductInfoGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_VERSION_INFO_GET, URoUkitSmartVersionInfoGetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_UPDATE_STOP, URoUkitSmartUpdateStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_UPDATE_INFO_SET, URoUkitSmartUpdateInfoSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_FILE_DOWNLOAD_PROGRESS_QUERY, URoUkitSmartFileDownloadProgressQueryFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_PART_INFO_SET, URoUkitSmartPartInfoSetFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_PART_DOWNLOAD_PROGRESS_QUERY, URoUkitSmartPartDownloadProgressQueryFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_PART_SW_VER, URoUkitSmartPartSwVerFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_PART_UPDATE_STOP, URoUkitSmartPartUpdateStopFormatter.INSTANCE);
        registerCommandType(URoCommandConstants.CMD_VISIONMODULE_DEV_UPDATE_STOP, URoUkitSmartDevUpdateStopFormatter.INSTANCE);

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
        if(errorCode != 0) {
            URoLogUtils.e("Cmd: %s, ErrorCode: %d", String.valueOf(cmd), errorCode);
        }
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
        int dev = conversionCommandDev(request.getCmd());
        int cmd = conversionCommandId(request.getCmd());
        if(dev == -1 || cmd == -1) {
            return null;
        }
        URoProtocolFormatter formatter = conversionCommandFormatter(request.getCmd());
        if(formatter == null) {
            return null;
        }
        int id;
        URoComponentType componentType = URoUkitSmartComponentMap.getTypeByValue(dev);
        if(URoComponentType.LCD.equals(componentType) || URoComponentType.VISION.equals(componentType)) {
            //LCD、VISION固定id为1
            id = 1;
        } else {
            id = request.getParameter("id", 0);
        }
        request.setKey(String.valueOf(sequenceId));
        byte[] bizData = formatter.encodeRequestMessage(request, sequenceId);
        byte[] rawRequest = packageDataFormat(dev, cmd, id, sequenceId, bizData);
        if (!ignorePrintData(request.getCmd())) {
            URoProtocolHandler.printByteData("Message data request: " + request.getCmd().getName(), " >>>>> ", rawRequest);
        }
        return rawRequest;
    }

    public static int byte2Int(byte b) {
        return Integer.parseInt(String.format(Locale.US,"%x", b), 16);
    }

    public static URoUkitSmartCommand conversionCommandType(int dev, int cmd) {
        ArrayList<URoUkitSmartCommand> listCommand = new ArrayList<>(dictType2Formatter.keySet());
        int index = listCommand.indexOf(new URoUkitSmartCommand("", dev, cmd));
        if(index < 0) {
            return null;
        }
        return listCommand.get(index);
    }

    public static int conversionCommandDev(URoCommand cmd) {
        if(!(cmd instanceof URoUkitSmartCommand)) {
            return -1;
        }
        return ((URoUkitSmartCommand)cmd).getDev();
    }

    public static int conversionCommandId(URoCommand cmd) {
        if(!(cmd instanceof URoUkitSmartCommand)) {
            return -1;
        }
        return ((URoUkitSmartCommand)cmd).getCmd();
    }

    public static URoProtocolFormatter conversionCommandFormatter(URoCommand cmdType) {
        if(!(cmdType instanceof URoUkitSmartCommand)) {
            return null;
        }
        return dictType2Formatter.get(cmdType);
    }

    public static byte checksum(byte[] data) {
        URoUKitSmartDeviceCrc8Sum crc8Sum = new URoUKitSmartDeviceCrc8Sum(0x07, 0);
        crc8Sum.update(data);
        return (byte)(crc8Sum.getValue() ^ 0x55);
    }

    private byte[] getHeaderData(int dev, int cmd, int id, int sequenceId, int bizDataLength) {
        PbMessageHeader.Header.Builder headerBuilder = PbMessageHeader.Header.newBuilder();
        headerBuilder.setDev(dev);
        headerBuilder.setCmd(cmd);
        headerBuilder.setId(id);
        headerBuilder.setDataLen(bizDataLength);
        headerBuilder.setAttr(PbMessageHeader.Header.Attr.REQUEST);
        headerBuilder.setAck(0);
        headerBuilder.setSeq(sequenceId);
        return headerBuilder.build().toByteArray();
    }

    private byte[] packageDataFormat(int dev, int cmd, int id, int sequenceId, byte[] bizData) {
        boolean hasBizData = bizData != null && bizData.length > 0;
        byte[] headerData = getHeaderData(dev, cmd, id, sequenceId, hasBizData ? bizData.length : 0);
        byte[] headerLength = { (byte)headerData.length };
        byte[] headerCrc32 = { checksum(headerData) };
        byte[] result;
        if(hasBizData) {
            byte[] bizCrc32 = { checksum(bizData) };
            result = URoProtocolHandler.arrayJoin(HEAD, headerLength, headerData, headerCrc32, bizData, bizCrc32);
        } else {
            result = URoProtocolHandler.arrayJoin(HEAD, headerLength, headerData, headerCrc32);
        }
        return result;
    }

    public boolean ignorePrintData(URoCommand cmd) {
        boolean result = super.ignorePrintData(cmd);
        result = result || URoCommandConstants.CMD_IR_DIS_PUSH.equals(cmd);
        result = result || URoCommandConstants.CMD_TCH_TYPE_PUSH.equals(cmd);
        result = result || URoCommandConstants.CMD_ULT_DIS_PUSH.equals(cmd);
        result = result || URoCommandConstants.CMD_CLR_RGB_PUSH.equals(cmd);
        result = result || URoCommandConstants.CMD_TH_PUSH.equals(cmd);
        result = result || URoCommandConstants.CMD_LGT_VALUE_PUSH.equals(cmd);
        result = result || URoCommandConstants.CMD_SND_ADC_VALUE_PUSH.equals(cmd);
        result = result || URoCommandConstants.CMD_FILE_SEND_MAX_PACK_SIZE.equals(cmd);
        result = result || URoCommandConstants.CMD_FILE_SENDING.equals(cmd);
        result = result || URoCommandConstants.CMD_FILE_RECV_MAX_PACK_SIZE.equals(cmd);
        result = result || URoCommandConstants.CMD_FILE_RECVING.equals(cmd);
        result = result || URoCommandConstants.CMD_LCD_FILE_SEND_MAX_PACK_SIZE.equals(cmd);
        result = result || URoCommandConstants.CMD_LCD_FILE_SENDING.equals(cmd);
        result = result || URoCommandConstants.CMD_LCD_FILE_RECV_MAX_PACK_SIZE.equals(cmd);
        result = result || URoCommandConstants.CMD_LCD_FILE_RECVING.equals(cmd);
        result = result || URoCommandConstants.CMD_HEARTBEAT.equals(cmd);
        result = result || URoCommandConstants.CMD_FILE_DOWNLOAD_PROGRESS_QUERY.equals(cmd);
        result = result || URoCommandConstants.CMD_VISIONMODULE_FILE_DOWNLOAD_PROGRESS_QUERY.equals(cmd);
        result = result || URoCommandConstants.CMD_BAT_VOL.equals(cmd);
        result = result || URoCommandConstants.CMD_BAT_PWR_PCT.equals(cmd);
        return result;
    }

}
