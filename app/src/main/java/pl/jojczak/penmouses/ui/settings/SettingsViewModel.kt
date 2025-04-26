package pl.jojczak.penmouses.ui.settings

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.graphics.scale
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pl.jojczak.penmouses.R
import pl.jojczak.penmouses.service.AppToServiceEvent
import pl.jojczak.penmouses.utils.CURSOR_IMAGE_HEIGHT
import pl.jojczak.penmouses.utils.CursorType
import pl.jojczak.penmouses.utils.PrefKey
import pl.jojczak.penmouses.utils.PrefKeys
import pl.jojczak.penmouses.utils.PreferencesManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesManager: PreferencesManager,
) : ViewModel() {

    private val _state: MutableStateFlow<SettingsScreenState> =
        MutableStateFlow(SettingsScreenState())
    val state: StateFlow<SettingsScreenState> = _state.asStateFlow()

    init {
        _state.update {
            it.copy(
                sPenSensitivity = preferencesManager.get(PrefKeys.SPEN_SENSITIVITY),
                cursorSize = preferencesManager.get(PrefKeys.CURSOR_SIZE),
                cursorType = preferencesManager.get(PrefKeys.CURSOR_TYPE),
            )
        }
    }

    fun <T> updatePreference(key: PrefKey<T>, value: T) {
        when (key) {
            PrefKeys.SPEN_SENSITIVITY -> {
                _state.update { it.copy(sPenSensitivity = value as Float) }
            }

            PrefKeys.CURSOR_SIZE -> {
                _state.update { it.copy(cursorSize = value as Float) }
            }
        }
    }

    fun <T> savePreference(key: PrefKey<T>, value: T) {
        preferencesManager.put(key, value)

        when (key) {
            PrefKeys.SPEN_SENSITIVITY -> {
                AppToServiceEvent.event.tryEmit(AppToServiceEvent.Event.UpdateSensitivity)
            }

            PrefKeys.CURSOR_SIZE -> {
                AppToServiceEvent.event.tryEmit(AppToServiceEvent.Event.UpdateCursorSize)
            }
        }
    }

    fun onCursorTypeChange(cursorType: CursorType) {
        _state.update { it.copy(cursorType = cursorType) }
        preferencesManager.put(PrefKeys.CURSOR_TYPE, cursorType)
        AppToServiceEvent.event.tryEmit(AppToServiceEvent.Event.UpdateCursorType)
    }

    fun loadCustomCursorImage(uri: Uri) {
        try {
            val mimeType = context.contentResolver.getType(uri)
            if (mimeType == null || !mimeType.startsWith("image/")) {
                Toast.makeText(
                    context,
                    context.getString(R.string.settings_change_cursor_file_type_error),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            val inputStream = context.contentResolver.openInputStream(uri) ?: run {
                Toast.makeText(
                    context,
                    context.getString(R.string.settings_change_cursor_file_load_error),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (originalBitmap == null) {
                Toast.makeText(
                    context,
                    context.getString(R.string.settings_change_cursor_file_load_error),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            val targetHeight = CURSOR_IMAGE_HEIGHT
            val scaleFactor = targetHeight / originalBitmap.height.toFloat()
            val targetWidth = (originalBitmap.width * scaleFactor).toInt()

            val resizedBitmap = originalBitmap.scale(targetWidth, targetHeight)

            val outputFile = File(context.filesDir, CursorType.CUSTOM.fileName)
            val outputStream = FileOutputStream(outputFile)
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

            outputStream.flush()
            outputStream.close()
            resizedBitmap.recycle()
            originalBitmap.recycle()

            AppToServiceEvent.event.tryEmit(AppToServiceEvent.Event.UpdateCursorType)

            Log.d(TAG, "Custom cursor image loaded successfully")
        } catch (e: IOException) {
            Log.e(TAG, "Error loading custom cursor image", e)
            Toast.makeText(
                context,
                context.getString(R.string.settings_change_cursor_file_load_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}