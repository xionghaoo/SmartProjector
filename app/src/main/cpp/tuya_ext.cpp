//
// Created by ubtech on 2021/1/26.
//

#include <iostream>
#include <string>
#include <jni.h>
#include <android/log.h>
// 测试
#include <gperf.h>

#include <tuya_gw_infra_api.h>

// 定义日志类型
#ifndef LOG_TAG
#define LOG_TAG "Tuya_Ext"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG ,__VA_ARGS__) // 定义LOGD类型
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG ,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,LOG_TAG ,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG ,__VA_ARGS__) // 定义LOGE类型
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,LOG_TAG ,__VA_ARGS__) // 定义LOGF类型
#endif

/* must apply for uuid, authkey and product key from tuya iot develop platform */
#define UUID         "003tuyatestf7f149185"
#define AUTHKEY      "NeA8Wc7srpAZHEMuru867oblOLN2QCC1"
#define PRODUCT_KEY  "GXxoKf27eVjA7x1c"

static char g_pid[64]     = {0};
static char g_uuid[64]    = {0};
static char g_authkey[64] = {0};

using namespace std;

extern "C" JNIEXPORT jstring JNICALL
Java_com_ubtrobot_smartprojector_ui_settings_SettingsFragment_helloStr(JNIEnv* env, jobject thiz) {
    LOGD("log form jni");
    ty_gw_attr_s gw_attr = {
            .storage_path = "./",
            .cache_path = "/tmp/",
            .tty_device = "/dev/ttyS1",
            .tty_baudrate = 115200,
            .eth_ifname = "br0",
            .ver = "1.0.0",
            .log_level = TY_LOG_DEBUG
    };
    auto ticks = GetTicks();
    auto str = "Hello JNI, " + to_string(TY_LOG_DEBUG);

    return env->NewStringUTF(str.c_str());
}
