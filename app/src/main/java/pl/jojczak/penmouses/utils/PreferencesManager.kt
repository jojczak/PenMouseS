@file:Suppress("SpellCheckingInspection")

package pl.jojczak.penmouses.utils

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.core.content.edit
import pl.jojczak.penmouses.R

class PreferencesManager(
    context: Context
) {
    private val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    fun <T> put(key: PrefKey<T>, value: T) {
        Log.d(TAG, "Writing pref ${key.name} with value $value")
        prefs.edit {
            when (value) {
                is Boolean -> {
                    putBoolean(key.name, value)
                }

                is Float -> {
                    putFloat(key.name, value)
                }

                is Int -> {
                    putInt(key.name, value)
                }

                is CursorType -> {
                    putString(key.name, value.name)
                }
            }
            this.apply()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: PrefKey<T>): T {
        Log.d(TAG, "Reading pref ${key.name}")
        return when (key.default) {
            is Boolean -> {
                prefs.getBoolean(key.name, key.default) as T
            }

            is Float -> {
                prefs.getFloat(key.name, key.default) as T
            }

            is Int -> {
                prefs.getInt(key.name, key.default) as T
            }

            is CursorType -> {
                CursorType.valueOf(
                    prefs.getString(key.name, key.default.name) ?: key.default.name
                ) as T
            }

            else -> {
                throw IllegalArgumentException("Unsupported type")
            }
        }
    }

    companion object {
        private const val TAG = "PreferencesManager"
    }
}

object PrefKeys {
    val SPEN_FEATURES_DISABLED = PrefKey("spen_features_disabled", false)
    val SPEN_SENSITIVITY = PrefKey("spen_sensitivity", 50f, 1f, 1f..100f)
    val CURSOR_SIZE = PrefKey("cursor_size", 60f, 1f, 20f..250f)
    val CURSOR_TYPE = PrefKey("cursor_type", CursorType.LIGHT)
    val CURSOR_HIDE_DELAY = PrefKey("time_to_hide_cursor", 10f, 5f, 5f..305f)
    val SPEN_SLEEP_ENABLED = PrefKey("spen_sleep_enabled", true)
}

data class PrefKey<T>(
    val name: String,
    val default: T,
    val step: Float = 1f,
    val range: ClosedFloatingPointRange<Float> = 0f..1f
)

enum class CursorType(
    val fileName: String,
    @StringRes val label: Int
) {
    LIGHT("light.png", R.string.settings_cursor_type_light),
    DARK("dark.png", R.string.settings_cursor_type_dark),
    RETRO("retro.png", R.string.settings_cursor_type_retro),
    CUSTOM("", R.string.settings_cursor_type_custom)
}