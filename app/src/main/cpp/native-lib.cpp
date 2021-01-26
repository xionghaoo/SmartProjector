//
// Created by ubtech on 2021/1/26.
//

#include <string.h>
#include <jni.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_ubtrobot_smartprojector_ui_settings_SettingsFragment_helloStr(JNIEnv* env, jobject thiz) {
    return env->NewStringUTF("Hello JNI");
}