package com.ubtedu.deviceconnect.libs.base.invocation.constants;

public interface URoInvocationNames {
    String INVOCATION_MODIFY_ID = "modifyId";

    String INVOCATION_SERVOS_TURN = "turnServos";
    String INVOCATION_BATCH_SERVOS_TURN_SIMULTANEOUS = "turnBatchServosSimultaneous";
    String INVOCATION_SERVOS_ROTATE = "rotateServos";
    String INVOCATION_BATCH_SERVOS_ROTATE = "rotateBatchServos";
    String INVOCATION_SERVOS_READBACK = "readbackServos";
    String INVOCATION_BATCH_SERVOS_READBACK = "readbackBatchServos";
    String INVOCATION_BATCH_SERVOS_READBACK_SIMULTANEOUS = "readbackBatchServosSimultaneous";
    String INVOCATION_SERVOS_STOP = "stopServos";
    String INVOCATION_SERVOS_FAULT_CLEAR = "servosFaultClear";
//    String INVOCATION_SERVOS_READ_VERSION = "readServosVersion";
//    String INVOCATION_SERVOS_READ_OFFSET = "readServosOffset";
//    String INVOCATION_SERVOS_SET_OFFSET = "setServosOffset";

    String INVOCATION_MOTOR_ROTATE = "rotateMotor";
    String INVOCATION_BATCH_MOTOR_ROTATE = "rotateBatchMotor";
    //    String INVOCATION_MOTOR_ROTATE_WITH_PWM = "rotateMotorWithPWM";
    String INVOCATION_MOTOR_STOP = "stopMotor";
    String INVOCATION_MOTOR_FAULT_CLEAR = "motorFaultClear";

    String INVOCATION_COMPONENT_CHECK="checkComponent";
    String INVOCATION_DEV_FAULT_CLEAR="devFaultClear";

    String INVOCATION_SENSOR_ENABLE="enableSensor";
    String INVOCATION_SENSOR_READ="readSensor";
    String INVOCATION_BATCH_SENSOR_READ="readBatchSensor";
    String INVOCATION_SENSOR_SUBSCRIBE="subscribeSensor";
    String INVOCATION_SENSOR_CALIBRATE_COLOR="calibrateSensorColor";
    String INVOCATION_SENSOR_CALIBRATE_SOUND="calibrateSensorSound";
    String INVOCATION_SENSOR_CALIBRATE_BRIGHTNESS="calibrateSensorBrightness";
    String INVOCATION_SENSOR_SET_ULTRASOUND_COLOR = "setUltrasoundColor";

    String INVOCATION_LED_SET_COLOR = "setLedColor";
    String INVOCATION_LED_SHOW_EFFECTS = "showLedEffects";

    String INVOCATION_SPEAKER_INFO = "readSpeakerInfo";

    String INVOCATION_TAKE_MOTION = "takeMotion";

    String INVOCATION_UPGRADE = "upgrade";
    String INVOCATION_UPGRADE_COMPONENT = "upgradeComponent";

    String INVOCATION_SEND_FILE = "sendFile";
    String INVOCATION_SEND_FILE_DATA = "sendFileData";
    String INVOCATION_RECV_FILE = "recvFile";
    String INVOCATION_RENAME_FILE = "renameFile";
    String INVOCATION_DELETE_FILE = "deleteFile";
    String INVOCATION_GET_FILE_STAT = "getFileStat";

    String INVOCATION_START_EXEC_SCRIPT = "startExecScript";
    String INVOCATION_STOP_EXEC_SCRIPT = "stopExecScript";
    String INVOCATION_UPDATE_SCRIPT_VALUE = "updateScriptValue";
    String INVOCATION_CLEANUP_SCRIPT_VALUE = "cleanupScriptValue";
    String INVOCATION_UPDATE_SCRIPT_EVENT = "updateScriptEvent";
    String INVOCATION_CLEANUP_SCRIPT_EVENT = "cleanupScriptEvent";

    String INVOCATION_INVOKE_SEQUENCE = "invokeSequence";

    String INVOCATION_MAIN_BOARD_READ = "readMainBoardInfo";
    String INVOCATION_STOP_RUNNING = "stopRunning";

    String INVOCATION_SELF_CHECK = "setSelfCheck";

    String INVOCATION_GET_WIFI_LIST = "getWiFiList";
    String INVOCATION_SET_WIFI_CONFIG = "setWiFiConfig";
    String INVOCATION_GET_WIFI_STATE = "getWiFiState";
    String INVOCATION_GET_NETWORK_STATE = "getNetworkState";
    String INVOCATION_SET_WIFI_CONNECT_ENABLE = "setWiFiConnectEnable";

    String INVOCATION_REBOOT = "reboot";

    String INVOCATION_GET_AUDIO_RECORD_LIST = "getAudioRecordList";
    String INVOCATION_GET_AUDIO_RECORD_NUM = "getAudioRecordNum";
    String INVOCATION_START_AUDIO_PLAY = "startAudioPlay";
    String INVOCATION_START_SOUND_PLAY = "startSoundPlay";
    String INVOCATION_STOP_AUDIO_PLAY = "stopAudioPlay";
    String INVOCATION_START_AUDIO_RECORD = "startAudioRecord";
    String INVOCATION_STOP_AUDIO_RECORD = "stopAudioRecord";
    String INVOCATION_RENAME_AUDIO_RECORD = "renameAudioRecord";
    String INVOCATION_DELETE_AUDIO_RECORD = "deleteAudioRecord";
    String INVOCATION_AUDIO_SET_VOLUME = "audioSetVolume";
    String INVOCATION_CANCEL_AUDIO_RECORD = "cancelAudioRecord";

    String INVOCATION_CMD_RESET_STATE_SET = "resetStateSet";

    /* ==================== UKIT LEGACY =================== */

    /* BOARD */
    String INVOCATION_CMD_BOARD_HANDSHAKE = "functionBoardHandshake";
    String INVOCATION_CMD_BOARD_HEARTBEAT = "functionBoardHeartbeat";
    String INVOCATION_CMD_BOARD_INFO = "functionBoardInfo";
    String INVOCATION_CMD_BOARD_BATTERY = "functionBoardBattery";
    String INVOCATION_CMD_BOARD_SERIAL_NUMBER = "functionBoardSerialNumber";
    String INVOCATION_CMD_BOARD_STOP = "functionBoardStop";
    String INVOCATION_CMD_BOARD_SELF_CHECK = "functionBoardSelfCheck";

    /* BOARD UPGRADE */
    String INVOCATION_CMD_BOARD_UPGRADE_ENTRY = "functionBoardUpgradeEntry";
    String INVOCATION_CMD_BOARD_UPGRADE_COMMIT = "functionBoardUpgradeCommit";
    String INVOCATION_CMD_BOARD_UPGRADE_DATA = "functionBoardUpgradeData";
    String INVOCATION_CMD_BOARD_UPGRADE_ABORT = "functionBoardUpgradeAbort";
    String INVOCATION_CMD_BOARD_UPGRADE_FLASH_BEGIN = "functionBoardUpgradeFlashBegin";
    String INVOCATION_CMD_BOARD_UPGRADE_FLASH_END = "functionBoardUpgradeFlashEnd";

    /* SENSOR */
    String INVOCATION_CMD_SENSOR_SPEAKER_INFO = "functionSensorSpeakerInfo";
    String INVOCATION_CMD_SENSOR_SWITCH = "functionSensorSwitch";
    String INVOCATION_CMD_SENSOR_MODIFY = "functionSensorModify";
    String INVOCATION_CMD_SENSOR_VALUE = "functionSensorValue";

    /* SENSOR UPGRADE */
    String INVOCATION_CMD_SENSOR_UPGRADE_ENTRY = "functionSensorUpgradeEntry";
    String INVOCATION_CMD_SENSOR_UPGRADE_DATA = "functionSensorUpgradeData";
    String INVOCATION_CMD_SENSOR_UPGRADE_ABORT = "functionSensorUpgradeAbort";
    String INVOCATION_CMD_SENSOR_UPGRADE_COMMIT = "functionSensorUpgradeCommit";

    /* STEERING GEAR */
    String INVOCATION_CMD_STEERING_GEAR_LOOP = "functionSteeringGearLoop";
    String INVOCATION_CMD_STEERING_GEAR_ANGLE = "functionSteeringGearAngle";
    String INVOCATION_CMD_STEERING_GEAR_ANGLE_FEEDBACK = "functionSteeringGearAngleFeedback";
    String INVOCATION_CMD_STEERING_GEAR_MODIFY = "functionSteeringGearModify";
    String INVOCATION_CMD_STEERING_GEAR_FIX = "functionSteeringGearFix";

    /* STEERING GEAR UPGRADE */
    String INVOCATION_CMD_STEERING_GEAR_UPGRADE_ENTRY = "functionSteeringGearUpgradeEntry";
    String INVOCATION_CMD_STEERING_GEAR_UPGRADE_DATA = "functionSteeringGearUpgradeData";
    String INVOCATION_CMD_STEERING_GEAR_UPGRADE_ABORT = "functionSteeringGearUpgradeAbort";
    String INVOCATION_CMD_STEERING_GEAR_UPGRADE_COMMIT = "functionSteeringGearUpgradeCommit";

    /* PERIPHERAL_SENSOR_LED */
    String INVOCATION_CMD_LED_EMOTION = "functionLedEmotion";
    String INVOCATION_CMD_LED_FACE = "functionLedFace";

    /* LOW MOTOR */
    String INVOCATION_CMD_LOW_MOTOR_START = "functionLowMotorStart";
    String INVOCATION_CMD_LOW_MOTOR_STOP = "functionLowMotorStop";
    String INVOCATION_CMD_LOW_MOTOR_FIX = "functionLowMotorFix";
    String INVOCATION_CMD_LOW_MOTOR_PWM = "functionLowMotorPwm";

    /* ==================== UKIT SMART =================== */

    String INVOCATION_CMD_SN_GET = "functionSnGet";
    String INVOCATION_CMD_SN_SET = "functionSnSet";
    String INVOCATION_CMD_HW_VER = "functionHwVer";
    String INVOCATION_CMD_SW_VER = "functionSwVer";
    String INVOCATION_CMD_PT_VER = "functionPtVer";
    String INVOCATION_CMD_RECOVER = "functionRecover";
    String INVOCATION_CMD_RESET = "functionReset";
    String INVOCATION_CMD_HEARTBEAT = "functionHeartbeat";
    String INVOCATION_CMD_START = "functionStart";
    String INVOCATION_CMD_STOP = "functionStop";
    String INVOCATION_CMD_STATE_GET = "functionStateGet";
    String INVOCATION_CMD_TYPE_GET = "functionTypeGet";
    String INVOCATION_CMD_RANDOM_GET = "functionRandomGet";
    String INVOCATION_CMD_SUBTYPE_SET = "functionSubtypeSet";
    String INVOCATION_CMD_PARA_SET = "functionParaSet";
    String INVOCATION_CMD_PARA_GET = "functionParaGet";
    String INVOCATION_CMD_DEV_STATUS_LED_SET = "functionDevStatusLedSet";
    String INVOCATION_CMD_MCU_SN_GET = "functionMcuSnGet";
    String INVOCATION_CMD_FILE_SEND_MAX_PACK_SIZE = "functionFileSendMaxPackSize";
    String INVOCATION_CMD_FILE_SEND_START = "functionFileSendStart";
    String INVOCATION_CMD_FILE_SENDING = "functionFileSending";
    String INVOCATION_CMD_FILE_SEND_OVER = "functionFileSendOver";
    String INVOCATION_CMD_FILE_SEND_STOP = "functionFileSendStop";
    String INVOCATION_CMD_FILE_RECV_MAX_PACK_SIZE = "functionFileRecvMaxPackSize";
    String INVOCATION_CMD_FILE_RECV_START = "functionFileRecvStart";
    String INVOCATION_CMD_FILE_RECVING = "functionFileRecving";
    String INVOCATION_CMD_FILE_RECV_OVER = "functionFileRecvOver";
    String INVOCATION_CMD_FILE_RECV_STOP = "functionFileRecvStop";
    String INVOCATION_CMD_FILE_DEL = "functionFileDel";
    String INVOCATION_CMD_FILE_RENAME = "functionFileRename";
    String INVOCATION_CMD_FILE_STATE = "functionFileState";
    String INVOCATION_CMD_DIR_MAKE = "functionDirMake";
    String INVOCATION_CMD_DIR_DEL = "functionDirDel";
    String INVOCATION_CMD_DIR_F_CNT_GET = "functionDirFCntGet";
    String INVOCATION_CMD_DIR_F_INFO_GET = "functionDirFInfoGet";
    String INVOCATION_CMD_SRV_ANGLE_SET = "functionSrvAngleSet";
    String INVOCATION_CMD_SRV_ANGLE_GET = "functionSrvAngleGet";
    String INVOCATION_CMD_SRV_PWM_SET = "functionSrvPwmSet";
    String INVOCATION_CMD_SRV_WHEEL_SET = "functionSrvWheelSet";
    String INVOCATION_CMD_SRV_STOP = "functionSrvStop";
    String INVOCATION_CMD_SRV_CALIBRATION_SET = "functionSrvCalibrationSet";
    String INVOCATION_CMD_SRV_CALIBRATION_GET = "functionSrvCalibrationGet";
    String INVOCATION_CMD_SRV_FAULT_GET = "functionSrvFaultGet";
    String INVOCATION_CMD_SRV_FAULT_CLEAR = "functionSrvFaultClear";
    String INVOCATION_CMD_SRV_LIMIT_SET = "functionSrvLimitSet";
    String INVOCATION_CMD_SRV_STATUS_GET = "functionSrvStatusGet";
    String INVOCATION_CMD_SRV_INFO_GET = "functionSrvInfoGet";
    String INVOCATION_CMD_MTR_SPEED_SET = "functionMtrSpeedSet";
    String INVOCATION_CMD_MTR_STOP = "functionMtrStop";
    String INVOCATION_CMD_MTR_SPEED_GET = "functionMtrSpeedGet";
    String INVOCATION_CMD_MTR_PWM_UPPER_LIMIT_SET = "functionMtrPwmUpperLimitSet";
    String INVOCATION_CMD_MTR_PWM_SET = "functionMtrPwmSet";
    String INVOCATION_CMD_MTR_PWM_GET = "functionMtrPwmGet";
    String INVOCATION_CMD_MTR_CNT_GET = "functionMtrCntGet";
    String INVOCATION_CMD_MTR_CNT_RESET = "functionMtrCntReset";
    String INVOCATION_CMD_MTR_FAULT_GET = "functionMtrFaultGet";
    String INVOCATION_CMD_MTR_FAULT_CLEAR = "functionMtrFaultClear";
    String INVOCATION_CMD_IR_ADC_GET = "functionIrAdcGet";
    String INVOCATION_CMD_IR_DIS_GET = "functionIrDisGet";
    String INVOCATION_CMD_IR_ONOFF_GET = "functionIrOnoffGet";
    String INVOCATION_CMD_TH_GET = "functionThGet";
    String INVOCATION_CMD_SND_ADC_VALUE_GET = "functionSndAdcValueGet";
    String INVOCATION_CMD_SND_HYSTERESIS_TIME_SET = "functionSndHysteresisTimeSet";
    String INVOCATION_CMD_SND_HYSTERESIS_TIME_GET = "functionSndHysteresisTimeGet";
    String INVOCATION_CMD_SND_CALIBRATE_SET = "functionSndCalibrateSet";
    String INVOCATION_CMD_SND_CALIBRATE_GET = "functionSndCalibrateGet";
    String INVOCATION_CMD_LED_FIX_EXP_SET = "functionLedFixExpSet";
    String INVOCATION_CMD_LED_EXP_SET = "functionLedExpSet";
    String INVOCATION_CMD_ULT_DIS_GET = "functionUltDisGet";
    String INVOCATION_CMD_TCH_TIMES_GET = "functionTchTimesGet";
    String INVOCATION_CMD_TCH_TYPE_GET = "functionTchTypeGet";
    String INVOCATION_CMD_TCH_HYSTERESIS_TIME_SET = "functionTchHysteresisTimeSet";
    String INVOCATION_CMD_TCH_HYSTERESIS_TIME_GET = "functionTchHysteresisTimeGet";
    String INVOCATION_CMD_TCH_STATE_GET = "functionTchStateGet";
    String INVOCATION_CMD_CLR_RGB_GET = "functionClrRgbGet";
    String INVOCATION_CMD_CLR_CAL_ONOFF = "functionClrCalOnoff";
    String INVOCATION_CMD_CLR_CAL_STATE_GET = "functionClrCalStateGet";
    String INVOCATION_CMD_BAT_PWR_PCT = "functionBatPwrPct";
    String INVOCATION_CMD_BAT_STATUS_GET = "functionBatStatusGet";
    String INVOCATION_CMD_BAT_TEMPERATURE = "functionBatTemperature";
    String INVOCATION_CMD_BAT_VOL = "functionBatVol";
    String INVOCATION_CMD_BAT_CHG_CUR = "functionBatChgCur";
    String INVOCATION_CMD_BAT_DCHG_CUR = "functionBatDchgCur";
    String INVOCATION_CMD_BAT_CAPACITY = "functionBatCapacity";
    String INVOCATION_CMD_LGT_VALUE_GET = "functionLgtValueGet";
    String INVOCATION_CMD_LGT_STDVALUE_SET = "functionLgtStdvalueSet";
    String INVOCATION_CMD_LGT_STDVALUE_GET = "functionLgtStdvalueGet";
    String INVOCATION_CMD_LGT_COE_GET = "functionLgtCoeGet";
    String INVOCATION_CMD_INTELLIGENT_DEV_COMBO_OFF = "functionIntelligentDevComboOff";
    String INVOCATION_CMD_INTELLIGENT_DEV_SCRIPT_EXEC = "functionIntelligentDevScriptExec";
    String INVOCATION_CMD_INTELLIGENT_DEV_SCRIPT_STOP = "functionIntelligentDevScriptStop";
    String INVOCATION_CMD_INTELLIGENT_DEV_SCRIPT_EXEC_REPORT = "functionIntelligentDevScriptExecReport";
    String INVOCATION_CMD_INTELLIGENT_DEV_SCRIPT_PARAM_UPDATE = "functionIntelligentDevScriptParamUpdate";
    String INVOCATION_CMD_INTELLIGENT_DEV_SCRIPT_PARAM_CLEAR = "functionIntelligentDevScriptParamClear";
    String INVOCATION_CMD_INTELLIGENT_DEV_SCRIPT_EVENT_UPDATE = "functionIntelligentDevScriptEventUpdate";
    String INVOCATION_CMD_INTELLIGENT_DEV_SCRIPT_EVENT_CLEAR = "functionIntelligentDevScriptEventClear";
    String INVOCATION_CMD_INTELLIGENT_DEV_SENSOR_READ = "functionIntelligentDevSensorRead";
    String INVOCATION_CMD_SRV_ID_SET = "functionSrvIdSet";
    String INVOCATION_CMD_MTR_ID_SET = "functionMtrIdSet";
    String INVOCATION_CMD_IR_ID_SET = "functionIrIdSet";
    String INVOCATION_CMD_TH_ID_SET = "functionThIdSet";
    String INVOCATION_CMD_SND_ID_SET = "functionSndIdSet";
    String INVOCATION_CMD_LED_ID_SET = "functionLedIdSet";
    String INVOCATION_CMD_ULT_ID_SET = "functionUltIdSet";
    String INVOCATION_CMD_TCH_ID_SET = "functionTchIdSet";
    String INVOCATION_CMD_CLR_ID_SET = "functionClrIdSet";
    String INVOCATION_CMD_LGT_ID_SET = "functionLgtIdSet";
    String INVOCATION_CMD_SUK_ID_SET = "functionSukIdSet";
    String INVOCATION_CMD_LED_BELT_ID_SET = "functionLedBeltIdSet";
    String INVOCATION_CMD_ACTION_COMPLETED_REPORT = "functionActionCompletedReport";
    String INVOCATION_CMD_UPDATE_INFO_SET = "functionUpdateInfoSet";
    String INVOCATION_CMD_FILE_DOWNLOAD_PROGRESS_REPORT = "functionFileDownloadProgressReport";
    String INVOCATION_CMD_UPDATE_STATUS_REPORT = "functionUpdateStatusReport";
    String INVOCATION_CMD_ULT_LIGHT_SET = "functionUltLightSet";
    String INVOCATION_CMD_SRV_ON = "functionSrvOn";
    String INVOCATION_CMD_SRV_OFF = "functionSrvOff";
    String INVOCATION_CMD_MTR_ON = "functionMtrOn";
    String INVOCATION_CMD_MTR_OFF = "functionMtrOff";
    String INVOCATION_CMD_IR_ON = "functionIrOn";
    String INVOCATION_CMD_IR_OFF = "functionIrOff";
    String INVOCATION_CMD_TH_ON = "functionThOn";
    String INVOCATION_CMD_TH_OFF = "functionThOff";
    String INVOCATION_CMD_SND_ON = "functionSndOn";
    String INVOCATION_CMD_SND_OFF = "functionSndOff";
    String INVOCATION_CMD_LED_ON = "functionLedOn";
    String INVOCATION_CMD_LED_OFF = "functionLedOff";
    String INVOCATION_CMD_ULT_ON = "functionUltOn";
    String INVOCATION_CMD_ULT_OFF = "functionUltOff";
    String INVOCATION_CMD_TCH_ON = "functionTchOn";
    String INVOCATION_CMD_TCH_OFF = "functionTchOff";
    String INVOCATION_CMD_CLR_ON = "functionClrOn";
    String INVOCATION_CMD_CLR_OFF = "functionClrOff";
    String INVOCATION_CMD_LGT_ON = "functionLgtOn";
    String INVOCATION_CMD_LGT_OFF = "functionLgtOff";
    String INVOCATION_CMD_SPK_GET = "functionSpkGet";
    String INVOCATION_CMD_SUK_ON = "functionSukOn";
    String INVOCATION_CMD_SUK_OFF = "functionSukOff";
    String INVOCATION_CMD_LCD_ON = "functionLcdOn";
    String INVOCATION_CMD_LCD_OFF = "functionLcdOff";


    String INVOCATION_CMD_SRV_RESET = "functionSrvReset";
    String INVOCATION_CMD_MTR_RESET = "functionMtrReset";
    String INVOCATION_CMD_IR_RESET = "functionIrReset";
    String INVOCATION_CMD_TH_RESET = "functionThReset";
    String INVOCATION_CMD_SND_RESET = "functionSndReset";
    String INVOCATION_CMD_LED_RESET = "functionLedReset";
    String INVOCATION_CMD_ULT_RESET = "functionUltReset";
    String INVOCATION_CMD_TCH_RESET = "functionTchReset";
    String INVOCATION_CMD_CLR_RESET = "functionClrReset";
    String INVOCATION_CMD_LGT_RESET = "functionLgtReset";
    String INVOCATION_CMD_SPK_RESET = "functionSpkReset";
    String INVOCATION_CMD_SUK_RESET = "functionSukReset";
    String INVOCATION_CMD_LED_BELT_RESET = "functionLedBeltReset";
    String INVOCATION_CMD_LCD_RESET = "functionLcdReset";
    String INVOCATION_CMD_VISIONMODULE_RESET = "functionVisionmoduleReset";


    String INVOCATION_CMD_SRV_SW_VER = "functionSrvSwVer";
    String INVOCATION_CMD_MTR_SW_VER = "functionMtrSwVer";
    String INVOCATION_CMD_IR_SW_VER = "functionIrSwVer";
    String INVOCATION_CMD_TH_SW_VER = "functionThSwVer";
    String INVOCATION_CMD_SND_SW_VER = "functionSndSwVer";
    String INVOCATION_CMD_LED_SW_VER = "functionLedSwVer";
    String INVOCATION_CMD_ULT_SW_VER = "functionUltSwVer";
    String INVOCATION_CMD_TCH_SW_VER = "functionTchSwVer";
    String INVOCATION_CMD_CLR_SW_VER = "functionClrSwVer";
    String INVOCATION_CMD_LGT_SW_VER = "functionLgtSwVer";
    String INVOCATION_CMD_SPK_SW_VER = "functionSpkSwVer";
    String INVOCATION_CMD_SUK_SW_VER = "functionSukSwVer";
    String INVOCATION_CMD_LED_BELT_SW_VER = "functionLedBeltSwVer";
    String INVOCATION_CMD_LCD_SW_VER = "functionLcdSwVer";
    String INVOCATION_CMD_VISIONMODULE_SW_VER = "functionVisionmoduleSwVer";


    String INVOCATION_CMD_INTELLIGENT_DEV_SELF_CHECKING_REPORT = "functionIntelligentDevSelfCheckingReport";
    String INVOCATION_CMD_INTELLIGENT_DEV_INFO_GET = "functionIntelligentDevInfoGet";
    String INVOCATION_CMD_INTELLIGENT_DEV_UPDATE_REPORT = "functionIntelligentDevUpdateReport";
    String INVOCATION_CMD_INTELLIGENT_DEV_DEBUG_SWITCH = "functionIntelligentDevDebugSwitch";
    String INVOCATION_CMD_INTELLIGENT_DEV_WIFI_SSID_SET = "functionIntelligentDevWifiSsidSet";
    String INVOCATION_CMD_INTELLIGENT_DEV_WIFI_LIST_GET = "functionIntelligentDevWifiListGet";
    String INVOCATION_CMD_INTELLIGENT_DEV_RECORDING_FILE_REPORT = "functionIntelligentDevRecordingFileReport";
    String INVOCATION_CMD_INTELLIGENT_DEV_RECORDING_FILE_INFO = "functionIntelligentDevRecordingFileInfo";
    String INVOCATION_CMD_INTELLIGENT_DEV_FAULT_CLEAR = "functionIntelligentDevFaultClear";


    String INVOCATION_CMD_INTELLIGENT_DEV_CHANGE_SWITCH_SET = "functionIntelligentDevChangeSwitchSet";
    String INVOCATION_CMD_INTELLIGENT_DEV_WIFI_INFO_GET = "functionIntelligentDevWifiInfoGet";
    String INVOCATION_CMD_INTELLIGENT_DEV_WIFI_INFO_REPORT = "functionIntelligentDevWifiInfoReport";
    String INVOCATION_CMD_INTELLIGENT_DEV_WIFI_CONNECT_SET = "functionIntelligentDevWifiConnectSet";


    String INVOCATION_CMD_INTELLIGENT_DEV_NETWORK_INFO_GET = "functionIntelligentDevNetworkInfoGet";
    String INVOCATION_CMD_INTELLIGENT_DEV_NETWORK_INFO_REPORT = "functionIntelligentDevNetworkInfoReport";
    String INVOCATION_CMD_INTELLIGENT_DEV_VERSION_INFO_GET = "functionIntelligentDevVersionInfoGet";


    String INVOCATION_CMD_BAT_STATUS_REPORT = "functionBatStatusReport";


//    String INVOCATION_CMD_WORK_MODE_GET = "functionWorkModeGet";
//    String INVOCATION_CMD_WORK_MODE_SET = "functionWorkModeSet";
//    String INVOCATION_CMD_UPLOAD_TIME_SET = "functionUploadTimeSet";
//    String INVOCATION_CMD_UPLOAD_THRESHOLD_SET = "functionUploadThresholdSet";
//    String INVOCATION_CMD_UPLOAD_OFFSET_SET = "functionUploadOffsetSet";
    String INVOCATION_CMD_IR_DIS_PUSH = "functionIrDisPush";
    String INVOCATION_CMD_TH_PUSH = "functionThPush";
    String INVOCATION_CMD_SND_ADC_VALUE_PUSH = "functionSndAdcValuePush";
    String INVOCATION_CMD_ULT_DIS_PUSH = "functionUltDisPush";
    String INVOCATION_CMD_TCH_TYPE_PUSH = "functionTchTypePush";
    String INVOCATION_CMD_CLR_RGB_PUSH = "functionClrRgbPush";
    String INVOCATION_CMD_LGT_VALUE_PUSH = "functionLgtValuePush";

    String INVOCATION_CMD_WORK_MODE_IR_GET = "functionWorkModeIrGet";
    String INVOCATION_CMD_WORK_MODE_IR_SET = "functionWorkModeIrSet";
    String INVOCATION_CMD_UPLOAD_TIME_IR_SET = "functionUploadTimeIrSet";
    String INVOCATION_CMD_UPLOAD_THRESHOLD_IR_SET = "functionUploadThresholdIrSet";
    String INVOCATION_CMD_UPLOAD_OFFSET_IR_SET = "functionUploadOffsetIrSet";
    String INVOCATION_CMD_WORK_MODE_TH_GET = "functionWorkModeThGet";
    String INVOCATION_CMD_WORK_MODE_TH_SET = "functionWorkModeThSet";
    String INVOCATION_CMD_UPLOAD_TIME_TH_SET = "functionUploadTimeThSet";
    String INVOCATION_CMD_UPLOAD_THRESHOLD_TH_SET = "functionUploadThresholdThSet";
    String INVOCATION_CMD_UPLOAD_OFFSET_TH_SET = "functionUploadOffsetThSet";
    String INVOCATION_CMD_WORK_MODE_SND_GET = "functionWorkModeSndGet";
    String INVOCATION_CMD_WORK_MODE_SND_SET = "functionWorkModeSndSet";
    String INVOCATION_CMD_UPLOAD_TIME_SND_SET = "functionUploadTimeSndSet";
    String INVOCATION_CMD_UPLOAD_THRESHOLD_SND_SET = "functionUploadThresholdSndSet";
    String INVOCATION_CMD_UPLOAD_OFFSET_SND_SET = "functionUploadOffsetSndSet";
    String INVOCATION_CMD_WORK_MODE_ULT_GET = "functionWorkModeUltGet";
    String INVOCATION_CMD_WORK_MODE_ULT_SET = "functionWorkModeUltSet";
    String INVOCATION_CMD_UPLOAD_TIME_ULT_SET = "functionUploadTimeUltSet";
    String INVOCATION_CMD_UPLOAD_THRESHOLD_ULT_SET = "functionUploadThresholdUltSet";
    String INVOCATION_CMD_UPLOAD_OFFSET_ULT_SET = "functionUploadOffsetUltSet";
    String INVOCATION_CMD_WORK_MODE_TCH_GET = "functionWorkModeTchGet";
    String INVOCATION_CMD_WORK_MODE_TCH_SET = "functionWorkModeTchSet";
    String INVOCATION_CMD_UPLOAD_TIME_TCH_SET = "functionUploadTimeTchSet";
    String INVOCATION_CMD_UPLOAD_THRESHOLD_TCH_SET = "functionUploadThresholdTchSet";
    String INVOCATION_CMD_UPLOAD_OFFSET_TCH_SET = "functionUploadOffsetTchSet";
    String INVOCATION_CMD_WORK_MODE_CLR_GET = "functionWorkModeClrGet";
    String INVOCATION_CMD_WORK_MODE_CLR_SET = "functionWorkModeClrSet";
    String INVOCATION_CMD_UPLOAD_TIME_CLR_SET = "functionUploadTimeClrSet";
    String INVOCATION_CMD_UPLOAD_THRESHOLD_CLR_SET = "functionUploadThresholdClrSet";
    String INVOCATION_CMD_UPLOAD_OFFSET_CLR_SET = "functionUploadOffsetClrSet";
    String INVOCATION_CMD_WORK_MODE_LGT_GET = "functionWorkModeLgtGet";
    String INVOCATION_CMD_WORK_MODE_LGT_SET = "functionWorkModeLgtSet";
    String INVOCATION_CMD_UPLOAD_TIME_LGT_SET = "functionUploadTimeLgtSet";
    String INVOCATION_CMD_UPLOAD_THRESHOLD_LGT_SET = "functionUploadThresholdLgtSet";
    String INVOCATION_CMD_UPLOAD_OFFSET_LGT_SET = "functionUploadOffsetLgtSet";


    String INVOCATION_CMD_UPDATE_STOP = "functionUpdateStop";
    String INVOCATION_CMD_DEV_UPDATE_STOP = "functionDevUpdateStop";


    String INVOCATION_CMD_VR_FILE_DEL = "functionVrFileDel";
    String INVOCATION_CMD_VR_FILE_RENAME = "functionVrFileRename";
    String INVOCATION_CMD_VR_FILE_LIST = "functionVrFileList";
    String INVOCATION_CMD_VR_START_RECORD = "functionVrStartRecord";
    String INVOCATION_CMD_VR_STOP_RECORD = "functionVrStopRecord";
    String INVOCATION_CMD_VR_RECORD_INFO_REPORT = "functionVrRecordInfoReport";
    String INVOCATION_CMD_VR_EFFECT_PLAY = "functionVrEffectPlay";
    String INVOCATION_CMD_VR_RECORD_PLAY_START = "functionVrRecordPlayStart";
    String INVOCATION_CMD_VR_FILE_PLAY_STOP = "functionVrFilePlayStop";
    String INVOCATION_CMD_VR_PLAY_INFO_REPORT = "functionVrPlayInfoReport";
    String INVOCATION_CMD_VR_TTS_PLAY = "functionVrTtsPlay";
    String INVOCATION_CMD_VR_TTS_INFO_REPORT = "functionVrTtsInfoReport";
    String INVOCATION_CMD_VR_ASR_START = "functionVrAsrStart";
    String INVOCATION_CMD_VR_ASR_STOP = "functionVrAsrStop";
    String INVOCATION_CMD_VR_ASR_INFO_REPORT = "functionVrAsrInfoReport";
    String INVOCATION_CMD_VR_SET_VOLUME = "functionVrSetVolume";
    String INVOCATION_CMD_VR_GET_VOLUME = "functionVrGetVolume";
    String INVOCATION_CMD_VR_SET_LANGUAGE = "functionVrSetLanguage";
    String INVOCATION_CMD_VR_GET_LANGUAGE = "functionVrGetLanguage";


    String INVOCATION_CMD_VR_FILE_NUM = "functionVrFileNum";
    String INVOCATION_CMD_INTELLIGENT_DEV_BLE_MAC_GET = "functionIntelligentDevBleMacGet";
    String INVOCATION_CMD_INTELLIGENT_DEV_WIFI_MAC_GET = "functionIntelligentDevWifiMacGet";
    String INVOCATION_CMD_INTELLIGENT_DEV_IP_INFO_GET = "functionIntelligentDevIpInfoGet";


    String INVOCATION_CMD_SUCKER_STATUS_SET = "functionSuckerStatusSet";
    String INVOCATION_CMD_SUCKER_STATUS_GET = "functionSuckerStatusGet";


    String INVOCATION_CMD_INTELLIGENT_DEV_DEBUG_INFO_REPORT = "functionIntelligentDevDebugInfoReport";
    
    
    String INVOCATION_CMD_INTELLIGENT_DEV_REDIRECT_SWITCH_SET = "functionIntelligentDevRedirectSwitchSet";
    String INVOCATION_CMD_INTELLIGENT_DEV_STREAM_SWITCH_SET = "functionIntelligentDevStreamSwitchSet";


    String INVOCATION_CMD_LED_BELT_EXP_SET = "functionLedBeltExpSet";
    String INVOCATION_CMD_LED_BELT_LEDS_NUM_GET = "functionLedBeltLedsNumGet";
    String INVOCATION_CMD_LED_BELT_FIX_EXP_SET = "functionLedBeltFixExpSet";
    String INVOCATION_CMD_LED_BELT_BRIGHTNESS_SET = "functionLedBeltBrightnessSet";
    String INVOCATION_CMD_LED_BELT_OFF_SET = "functionLedBeltOffSet";
    String INVOCATION_CMD_LED_BELT_MOVE_SET = "functionLedBeltMoveSet";
    String INVOCATION_CMD_LED_BELT_EXPRESSIONS_CONTINUOUS_SET = "functionLedBeltExpressionsContinuousSet";
    String INVOCATION_CMD_LED_BELT_EXPRESSIONS_CONTINUOUS_BREATH_SET = "functionLedBeltExpressionsContinuousBreathSet";


    String INVOCATION_CMD_LCD_GUI_VER_GET = "functionLcdGuiVerGet";
    String INVOCATION_CMD_LCD_CLEAR = "functionLcdClear";
    String INVOCATION_CMD_LCD_BACK_LIGHT_SET = "functionLcdBackLightSet";
    String INVOCATION_CMD_LCD_BACKGROUND_COLOR_SET = "functionLcdBackgroundColorSet";
    String INVOCATION_CMD_LCD_BAR_DISPLAY = "functionLcdBarDisplay";
    String INVOCATION_CMD_LCD_STATIC_TEXT = "functionLcdStaticText";
    String INVOCATION_CMD_LCD_ROLL_TEXT = "functionLcdRollText";
    String INVOCATION_CMD_LCD_ICON = "functionLcdIcon";
    String INVOCATION_CMD_LCD_INNER_PIC = "functionLcdInnerPic";
    String INVOCATION_CMD_LCD_PIC_DISPLAY = "functionLcdPicDisplay";
    String INVOCATION_CMD_LCD_SENSOR_DISPLAY = "functionLcdSensorDisplay";
    String INVOCATION_CMD_LCD_SWITCH_PAGE = "functionLcdSwitchPage";
    String INVOCATION_CMD_LCD_GET_PAGE = "functionLcdGetPage";
    String INVOCATION_CMD_LCD_TEST_PIC = "functionLcdTestPic";
    String INVOCATION_CMD_LCD_RECOVER = "functionLcdRecover";


    String INVOCATION_CMD_VISIONMODULE_MID_OFFSET = "functionVisionmoduleMidOffset";
    String INVOCATION_CMD_VISIONMODULE_JPEG_QUALITY_SET = "functionVisionmoduleJpegQualitySet";
    String INVOCATION_CMD_VISIONMODULE_IDENTIFY = "functionVisionmoduleIdentify";
    String INVOCATION_CMD_VISIONMODULE_FACE_RECORD = "functionVisionmoduleFaceRecord";
    String INVOCATION_CMD_VISIONMODULE_FACE_INFOLIST_GET = "functionVisionmoduleFaceInfolistGet";
    String INVOCATION_CMD_VISIONMODULE_MODEL_SWITCH = "functionVisionmoduleModelSwitch";
    String INVOCATION_CMD_VISIONMODULE_FACE_NAME_MODIFY = "functionVisionmoduleFaceNameModify";
    String INVOCATION_CMD_VISIONMODULE_FACE_DELETE = "functionVisionmoduleFaceDelete";
    String INVOCATION_CMD_VISIONMODULE_ONLINE_IDENTIFY = "functionVisionmoduleOnlineIdentify";
    String INVOCATION_CMD_VISIONMODULE_WIFI_SSID_SET = "functionVisionmoduleWifiSsidSet";
    String INVOCATION_CMD_VISIONMODULE_WIFI_LIST_GET = "functionVisionmoduleWifiListGet";
    String INVOCATION_CMD_VISIONMODULE_WIFI_CONNECT_SET = "functionVisionmoduleWifiConnectSet";
    String INVOCATION_CMD_VISIONMODULE_WIFI_INFO_GET = "functionVisionmoduleWifiInfoGet";
    String INVOCATION_CMD_VISIONMODULE_WIFI_INFO_REPORT = "functionVisionmoduleWifiInfoReport";
    String INVOCATION_CMD_VISIONMODULE_NETWORK_INFO_GET = "functionVisionmoduleNetworkInfoGet";
    String INVOCATION_CMD_VISIONMODULE_NETWORK_INFO_REPORT = "functionVisionmoduleNetworkInfoReport";
    String INVOCATION_CMD_VISIONMODULE_WIFI_MAC_GET = "functionVisionmoduleWifiMacGet";
    String INVOCATION_CMD_VISIONMODULE_IP_INFO_GET = "functionVisionmoduleIpInfoGet";
    String INVOCATION_CMD_VISIONMODULE_BT_INFO_GET = "functionVisionmoduleBtInfoGet";
    String INVOCATION_CMD_VISIONMODULE_BT_INFO_REPORT = "functionVisionmoduleBtInfoReport";
    String INVOCATION_CMD_VISIONMODULE_BLE_MAC_GET = "functionVisionmoduleBleMacGet";
    String INVOCATION_CMD_VISIONMODULE_PC_LINK_INFO_GET = "functionVisionmodulePcLinkInfoGet";
    String INVOCATION_CMD_VISIONMODULE_PC_LINK_INFO_REPORT = "functionVisionmodulePcLinkInfoReport";
    String INVOCATION_CMD_VISIONMODULE_PRODUCT_INFO_GET = "functionVisionmoduleProductInfoGet";
    String INVOCATION_CMD_VISIONMODULE_VERSION_INFO_GET = "functionVisionmoduleVersionInfoGet";
    String INVOCATION_CMD_VISIONMODULE_PART_INFO_SET = "functionVisionmodulePartInfoSet";
    String INVOCATION_CMD_VISIONMODULE_PART_DOWNLOAD_PROGRESS_QUERY = "functionVisionmodulePartDownloadProgressQuery";
    String INVOCATION_CMD_VISIONMODULE_PART_SW_VER = "functionVisionmodulePartSwVer";

}
