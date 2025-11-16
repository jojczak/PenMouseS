package pl.jojczak.penmouses.mousemode.basecursor

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent.ModeStatus
import pl.jojczak.penmouses.core.common.utils.CursorType
import pl.jojczak.penmouses.core.common.utils.PrefKeys
import pl.jojczak.penmouses.core.common.utils.PreferencesManager
import pl.jojczak.penmouses.core.common.utils.getCursorBitmap

class CursorPreferences(
    private val context: Context,
    private val preferences: PreferencesManager,
) {

    fun getBitmap(modeStatus: ModeStatus): Bitmap? {
        Log.d(TAG, "Getting cursor image")

        val cursorType = when (modeStatus) {
            ModeStatus.Mouse -> preferences.get(PrefKeys.MOUSE_CURSOR_TYPE)
            else -> preferences.get(PrefKeys.POINT_CURSOR_TYPE)
        }
        return getCursorBitmap(context, cursorType, modeStatus)
            ?: getCursorBitmap(context, CursorType.Light, modeStatus)
    }

    fun getSize(modeStatus: ModeStatus, bitmap: Bitmap): Pair<Int, Int> {
        Log.d(TAG, "Getting cursor size")

        val prefSize = when (modeStatus) {
            ModeStatus.Mouse -> preferences.get(PrefKeys.MOUSE_CURSOR_SIZE)
            else -> preferences.get(PrefKeys.POINT_CURSOR_SIZE)
        }
        val density = context.resources.displayMetrics.density
        val cursorSize = (prefSize * density).toInt()

        val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val width = (cursorSize * ratio).toInt()

        Log.d(TAG, "Cursor size: $width x $cursorSize")

        return Pair(width, cursorSize)
    }

    fun getOpacity(modeStatus: ModeStatus) = when (modeStatus) {
        ModeStatus.Mouse -> preferences.get(PrefKeys.MOUSE_CURSOR_ALPHA)
        else -> preferences.get(PrefKeys.POINT_CURSOR_ALPHA)
    }

    fun getSensitivity() = preferences.get(PrefKeys.MOUSE_SENSITIVITY)

    fun getHideDelay() =
        preferences.get(PrefKeys.MOUSE_CURSOR_HIDE_DELAY).toLong() * DELAY_TO_MS_MULTIPLIER

    fun isSleepEnabled() = preferences.get(PrefKeys.MOUSE_SLEEP_ENABLED)

    companion object {
        private const val TAG = "CursorPreferences"

        private const val DELAY_TO_MS_MULTIPLIER = 1000
    }
}