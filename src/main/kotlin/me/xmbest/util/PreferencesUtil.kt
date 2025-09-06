package me.xmbest.util

import com.russhwolf.settings.PreferencesSettings
import me.xmbest.nodeName
import java.util.prefs.Preferences

object PreferencesUtil {
    private val settings = PreferencesSettings(Preferences.userRoot().node(nodeName))

    /**
     * 选中的adb路径
     */
    const val PREFERENCES_ADB_PATH = "settings.adb_path"

    /**
     * 自定义的adb路径
     */
    const val PREFERENCES_CUSTOMER_ADB_PATH = "settings.customer_adb_path"

    /**
     * 选中的主题
     */
    const val PREFERENCES_THEME = "settings.theme"

    fun set(key: String, value: Any) {
        when (value) {
            is Boolean -> settings.putBoolean(key, value)
            is Int -> settings.putInt(key, value)
            is Float -> settings.putFloat(key, value)
            is Long -> settings.putLong(key, value)
            is Double -> settings.putDouble(key, value)
            is String -> settings.putString(key, value)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, default: T): T {
        return when (default) {
            is Boolean -> settings.getBoolean(key, default)
            is Int -> settings.getInt(key, default)
            is Float -> settings.getFloat(key, default)
            is Long -> settings.getLong(key, default)
            is Double -> settings.getDouble(key, default)
            is String -> settings.getString(key, default)
            else -> default
        } as T
    }

    fun clear() {
        settings.clear()
    }
}