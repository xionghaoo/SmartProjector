//
// Created by ubtech on 2021/1/26.
//

#include <iostream>
#include <string>
#include <jni.h>
#include <gperf.h>

using namespace std;

extern "C" JNIEXPORT jstring JNICALL
Java_com_ubtrobot_smartprojector_ui_settings_SettingsFragment_helloStr(JNIEnv* env, jobject thiz) {
    auto ticks = GetTicks();
    auto str = "Hello JNI, " + to_string(ticks);
    return env->NewStringUTF(str.c_str());
}