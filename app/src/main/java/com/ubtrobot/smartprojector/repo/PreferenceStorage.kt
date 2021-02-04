package com.ubtrobot.smartprojector.repo

import android.content.Context
import android.content.SharedPreferences
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
