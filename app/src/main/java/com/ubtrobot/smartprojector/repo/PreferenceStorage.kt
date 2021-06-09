package com.ubtrobot.smartprojector.repo

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * key - value 存储
 */
interface PreferenceStorage {
    var loginUsername: String?
    var loginPassword: String?
    var isScreenLocked: Boolean
    var screenLockPwd: String?
    var wifiPwd: String?
    var tuyaHomeId: Long
    var tuyaHomeName: String?
    var isEyesProtectOpen: Boolean
    var isPostureDetectOpen: Boolean
    var grade: Int
    var serialNumber: String?
//    var deviceId: String?
    var rtmToken: String?
    var rtcToken: String?
    var agoraUID: String?
}

class SharedPreferenceStorage @Inject constructor(@ApplicationContext context: Context) : PreferenceStorage {
    private val prefs: Lazy<SharedPreferences> = lazy {
        context.applicationContext.getSharedPreferences(
            PREFS_NAME, Context.MODE_PRIVATE
        ).apply {

        }
    }

    override var loginUsername by StringPreference(prefs, PREF_LOGIN_USERNAME, null)
    override var loginPassword by StringPreference(prefs, PREF_LOGIN_PASSWORD, null)
    override var isScreenLocked by BooleanPreference(prefs, PREF_IS_SCREEN_LOCKED, false)
    override var screenLockPwd by StringPreference(prefs, PREF_SCREEN_LOCK_PWD, null)
    override var wifiPwd: String? by StringPreference(prefs, PREF_WIFI_PWD, null)
    override var tuyaHomeId: Long by LongPreference(prefs, PREF_HOME_ID)
    override var tuyaHomeName: String? by StringPreference(prefs, PREF_HOME_NAME, null)
    override var isEyesProtectOpen: Boolean by BooleanPreference(prefs, PREF_IS_EYES_PROTECT_OPEN, false)
    override var isPostureDetectOpen: Boolean by BooleanPreference(prefs, PREF_IS_POSTURE_DETECT_OPEN, false)
    // 年级，默认是一年级
    override var grade: Int by IntPreference(prefs, PREF_GRADE, 1)
    override var serialNumber: String? by StringPreference(prefs, PREF_SERIAL_NUMBER, null)
//    override var deviceId: String? by StringPreference(prefs, PREF_DEVICE_ID, null)
    override var rtmToken: String? by StringPreference(prefs, PREF_RTM_TOKEN, null)
    override var rtcToken: String? by StringPreference(prefs, PREF_RTC_TOKEN, null)
    override var agoraUID: String? by StringPreference(prefs, PREF_AGORA_UID, null)

    init {
//        deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    // 登出时清理缓存
    fun clearCache() {
        loginUsername = null
        loginPassword = null
    }

    companion object {
        const val PREFS_NAME = "ubt_pref"
        const val PREF_LOGIN_USERNAME = "pref_login_username"
        const val PREF_LOGIN_PASSWORD = "pref_login_password"
        const val PREF_IS_SCREEN_LOCKED = "pref_is_screen_locked"
        const val PREF_SCREEN_LOCK_PWD = "pref_screen_lock_pwd"
        const val PREF_WIFI_PWD = "pref_wifi_pwd"
        const val PREF_HOME_ID = "pref_home_id"
        const val PREF_HOME_NAME = "pref_home_name"
        const val PREF_IS_EYES_PROTECT_OPEN = "pref_is_eyes_protect_open"
        const val PREF_IS_POSTURE_DETECT_OPEN = "pref_is_posture_detect_open"
        const val PREF_GRADE = "pref_grade"
        const val PREF_SERIAL_NUMBER = "pref_serial_number"
//        const val PREF_DEVICE_ID = "pref_device_id"
        const val PREF_RTM_TOKEN = "pref_rtm_token"
        const val PREF_RTC_TOKEN = "pref_rtc_token"
        const val PREF_AGORA_UID = "pref_agora_uid"
    }
}

class BooleanPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean> {
    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.value.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.value.edit { putBoolean(name, value) }
    }
}

class StringPreference(private val preferences: Lazy<SharedPreferences>,
                       private val key: String,
                       private val defaultValue: String?) : ReadWriteProperty<Any, String?> {
    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): String? {
        return preferences.value.getString(key, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        preferences.value.edit { putString(key, value) }
    }
}

class IntPreference(private val preferences: Lazy<SharedPreferences>,
                    private val key: String,
                    private val defaultValue: Int = 0) : ReadWriteProperty<Any, Int> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Int {
        return preferences.value.getInt(key, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
        preferences.value.edit { putInt(key, value) }
    }
}

class LongPreference(private val preferences: Lazy<SharedPreferences>,
                    private val key: String,
                    private val defaultValue: Long = -1) : ReadWriteProperty<Any, Long> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Long {
        return preferences.value.getLong(key, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
        preferences.value.edit { putLong(key, value) }
    }
}
