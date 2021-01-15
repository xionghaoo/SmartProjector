package com.ubtedu.deviceconnect.libs.base.model.event;

/**
 * @Author naOKi
 * @Date 2019/10/17
 **/
public enum URoPushMessageType {
    SCRIPT_REPORT,      // 脚本运行回调
    WIFI_STATUS_CHANGE, // WiFi状态变化
    NETWORK_STATUS_CHANGE, // 网络状态变化
    AUDIO_RECORD_REPORT, // 录音结果上报
    AUDIO_PLAY_REPORT, // 播放结果上报
    TTS_REPORT, // TTS结果上报
    ASR_REPORT, // ASR结果上报
    SCRIPT_RUNNING_FAULT, // 脚本运行异常
}
