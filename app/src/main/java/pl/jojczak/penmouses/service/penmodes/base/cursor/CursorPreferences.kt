package pl.jojczak.penmouses.service.penmodes.base.cursor

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import pl.jojczak.penmouses.utils.CursorType
import pl.jojczak.penmouses.utils.PrefKeys
import pl.jojczak.penmouses.utils.PreferencesManager
import pl.jojczak.penmouses.utils.getCursorBitmap

class CursorPreferences(
    private val context: Context,
    private val preferences: PreferencesManager,
) {

    fun getBitmap(): Bitmap? {
        Log.d(TAG, "Getting cursor image")

        val cursorType = preferences.get(PrefKeys.CURSOR_TYPE)
        return getCursorBitmap(context, cursorType)
            ?: getCursorBitmap(context, CursorType.LIGHT)
    }

    fun getSize(bitmap: Bitmap): Pair<Int, Int> {
        Log.d(TAG, "Getting cursor size")

        val prefSize = preferences.get(PrefKeys.CURSOR_SIZE)
        val density = context.resources.displayMetrics.density
        val cursorSize = (prefSize * density).toInt()

        val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val width = (cursorSize * ratio).toInt()

        Log.d(TAG, "Cursor size: $width x $cursorSize")

        return Pair(width, cursorSize)
    }

    fun getSensitivity() = preferences.get(PrefKeys.SPEN_SENSITIVITY)

    fun getHideDelay() =
        preferences.get(PrefKeys.CURSOR_HIDE_DELAY).toLong() * DELAY_TO_MS_MULTIPLIER

    fun isSleepEnabled() = preferences.get(PrefKeys.SPEN_SLEEP_ENABLED)

    companion object {
        private const val TAG = "CursorPreferences"

        private const val DELAY_TO_MS_MULTIPLIER = 1000
    }
}