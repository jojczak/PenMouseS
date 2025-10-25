package pl.jojczak.penmouses.screen.settings.mvi

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.graphics.scale
import dagger.hilt.android.qualifiers.ApplicationContext
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent.PenMode
import pl.jojczak.penmouses.core.common.utils.CURSOR_IMAGE_WIDTH
import pl.jojczak.penmouses.core.common.utils.CursorType
import pl.jojczak.penmouses.core.common.utils.PrefKeys
import pl.jojczak.penmouses.core.common.utils.PreferencesManager
import pl.jojczak.penmouses.screen.settings.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

internal abstract class SettingsTabCursorViewModel<StateType: SettingsTabCursorState>(
    @param:ApplicationContext private val context: Context,
    private val penMode: PenMode,
    preferencesManager: PreferencesManager,
    startState: StateType,
): SettingsTabViewModel<StateType>(
    preferencesManager = preferencesManager,
    startState = startState
) {
    protected fun loadCustomCursorImage(uri: Uri) {
        try {
            val mimeType = context.contentResolver.getType(uri)
            if (mimeType == null || !mimeType.startsWith(MIME_TYPE)) {
                showToast(R.string.settings_change_cursor_file_type_error)
                return
            }

            val inputStream = context.contentResolver.openInputStream(uri) ?: run {
                showToast(R.string.settings_change_cursor_file_load_error)
                return
            }
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (originalBitmap == null) {
                showToast(R.string.settings_change_cursor_file_load_error)
                return
            }

            val targetWidth = CURSOR_IMAGE_WIDTH
            val scaleFactor = targetWidth / originalBitmap.width.toFloat()
            val targetHeight = (originalBitmap.height * scaleFactor).toInt()
            val resizedBitmap = originalBitmap.scale(targetWidth, targetHeight)

            val outputFile = File(context.filesDir, CursorType.Custom.getFileName(penMode))
            val outputStream = FileOutputStream(outputFile)
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

            outputStream.flush()
            outputStream.close()
            resizedBitmap.recycle()
            originalBitmap.recycle()

            Log.d(TAG, "Custom cursor image loaded successfully")

            tryToPingService()
        } catch (e: IOException) {
            Log.e(TAG, "Error loading custom cursor image", e)
            showToast(R.string.settings_change_cursor_file_load_error)
        }
    }

    protected open fun onCursorTypeChange(cursorType: CursorType) {
        when (penMode) {
            PenMode.Mouse -> preferencesManager.put(PrefKeys.MOUSE_CURSOR_TYPE, cursorType)
            else /*PenMode.Point*/ -> preferencesManager.put(PrefKeys.POINT_CURSOR_TYPE, cursorType)
        }
        tryToPingService()
    }

    private fun showToast(@StringRes textId: Int) {
        Toast.makeText(
            context,
            context.getString(textId),
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        private const val TAG = "SettingsTabViewModel"

        private const val MIME_TYPE = "image/"
    }
}