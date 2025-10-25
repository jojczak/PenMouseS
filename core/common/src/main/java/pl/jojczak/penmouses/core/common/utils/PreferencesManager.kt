@file:Suppress("SpellCheckingInspection")

package pl.jojczak.penmouses.core.common.utils

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.core.content.edit
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent
import pl.jojczak.penmouses.core.ui.R

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
                CursorType.fromValue(
                    prefs.getString(key.name, key.default.name) ?: key.default.name
                ) as T
            }

            else -> {
                throw IllegalArgumentException("Unsupported type")
            }
        }
    }

    fun reset() {
        prefs.edit {
            put(PrefKeys.MOUSE_SENSITIVITY, PrefKeys.MOUSE_SENSITIVITY.default)
            put(PrefKeys.MOUSE_CURSOR_SIZE, PrefKeys.MOUSE_CURSOR_SIZE.default)
            put(PrefKeys.MOUSE_CURSOR_TYPE, PrefKeys.MOUSE_CURSOR_TYPE.default)
            put(PrefKeys.MOUSE_CURSOR_HIDE_DELAY, PrefKeys.MOUSE_CURSOR_HIDE_DELAY.default)
            put(PrefKeys.MOUSE_SLEEP_ENABLED, PrefKeys.MOUSE_SLEEP_ENABLED.default)
            put(PrefKeys.MOUSE_CURSOR_ALPHA, PrefKeys.MOUSE_CURSOR_ALPHA.default)
            put(PrefKeys.POINT_CURSOR_TYPE, PrefKeys.POINT_CURSOR_TYPE.default)
            put(PrefKeys.POINT_CURSOR_ALPHA, PrefKeys.POINT_CURSOR_ALPHA.default)
            put(PrefKeys.POINT_CURSOR_SIZE, PrefKeys.POINT_CURSOR_SIZE.default)
            put(PrefKeys.SCROLL_EXPERIMENTAL_MODE, PrefKeys.SCROLL_EXPERIMENTAL_MODE.default)
            this.apply()
        }
        Log.d(TAG, "Preferences reset")
    }

    companion object {
        private const val TAG = "PreferencesManager"
    }
}

object PrefKeys {
    val MOUSE_SENSITIVITY = PrefKey("spen_sensitivity", 50f, 1f, 1f..100f)
    val MOUSE_CURSOR_SIZE = PrefKey("cursor_size", 60f, 1f, 20f..250f)
    val MOUSE_CURSOR_TYPE = PrefKey("cursor_type", CursorType.Light)
    val MOUSE_CURSOR_ALPHA = PrefKey("cursor_alpha", 1f, step = 0.05f, range = 0.05f..1f)
    val MOUSE_CURSOR_HIDE_DELAY = PrefKey("time_to_hide_cursor", 10f, 5f, 5f..305f)
    val MOUSE_SLEEP_ENABLED = PrefKey("spen_sleep_enabled", true)
    val POINT_CURSOR_TYPE = PrefKey("point_cursor_type", CursorType.Light)
    val POINT_CURSOR_ALPHA = PrefKey("point_cursor_alpha", 1f, step = 0.05f, range = 0.05f..1f)
    val POINT_CURSOR_SIZE = PrefKey("point_cursor_size", 60f, 1f, 20f..250f)
    val SCROLL_EXPERIMENTAL_MODE = PrefKey("scroll_experimental_mode", false)
    val FIRST_RUN = PrefKey("first_run", true)
    val FIRST_MOUSE_LAUNCH = PrefKey("first_mouse_launch", true)
}

data class PrefKey<T>(
    val name: String,
    val default: T,
    val step: Float = 1f,
    val range: ClosedFloatingPointRange<Float> = 0f..1f
)

enum class CursorType(
    private val fileName: String,
    @param:StringRes val label: Int
) {
    Light("light.png", R.string.cursor_type_light),
    Dark("dark.png", R.string.cursor_type_dark),
    Retro("retro.png", R.string.cursor_type_retro),
    Custom("custom.png", R.string.cursor_type_custom);

    fun getFileName(mode: AppToServiceEvent.PenMode? = null) = when (mode) {
        AppToServiceEvent.PenMode.Mouse -> "mouse_" + this.fileName
        AppToServiceEvent.PenMode.Point -> "point_" + this.fileName
        else -> this.fileName
    }

    companion object {
        fun fromValue(value: String) = entries.firstOrNull {
            it.name.lowercase() == value.lowercase()
        } ?: Light
    }
}