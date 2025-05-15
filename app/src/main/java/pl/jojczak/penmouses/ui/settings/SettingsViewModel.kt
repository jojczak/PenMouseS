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
import pl.jojczak.penmouses.utils.CURSOR_IMAGE_WIDTH
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
        loadInitialSettings()
    }

    private fun loadInitialSettings() {
        _state.update {
            it.copy(
                sPenSensitivity = preferencesManager.get(PrefKeys.SPEN_SENSITIVITY),
                cursorHideDelay = preferencesManager.get(PrefKeys.CURSOR_HIDE_DELAY),
                sPenSleepEnabled = preferencesManager.get(PrefKeys.SPEN_SLEEP_ENABLED),
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

            PrefKeys.CURSOR_HIDE_DELAY -> {
                _state.update { it.copy(cursorHideDelay = value as Float) }
            }

            PrefKeys.CURSOR_SIZE -> {
                _state.update { it.copy(cursorSize = value as Float) }
            }
        }
    }

    fun <T> savePreference(key: PrefKey<T>, value: T) {
        preferencesManager.put(key, value)

        if (AppToServiceEvent.serviceStatus.value != AppToServiceEvent.ServiceStatus.ON) return
        when (key) {
            PrefKeys.SPEN_SENSITIVITY -> {
                AppToServiceEvent.event.tryEmit(AppToServiceEvent.Event.UpdateSensitivity)
            }

            PrefKeys.CURSOR_HIDE_DELAY -> {
                AppToServiceEvent.event.tryEmit(AppToServiceEvent.Event.UpdateHideDelay)
            }

            PrefKeys.CURSOR_SIZE -> {
                AppToServiceEvent.event.tryEmit(AppToServiceEvent.Event.UpdateCursorSize)
            }
        }
    }

    fun onSPenSleepEnabledChange(sPenSleepEnabled: Boolean) {
        _state.update { it.copy(sPenSleepEnabled = sPenSleepEnabled) }
        preferencesManager.put(PrefKeys.SPEN_SLEEP_ENABLED, sPenSleepEnabled)

        if (AppToServiceEvent.serviceStatus.value != AppToServiceEvent.ServiceStatus.ON) return
        AppToServiceEvent.event.tryEmit(AppToServiceEvent.Event.UpdateSPenSleepEnabled)
    }

    fun onCursorTypeChange(cursorType: CursorType) {
        _state.update { it.copy(cursorType = cursorType) }
        preferencesManager.put(PrefKeys.CURSOR_TYPE, cursorType)

        if (AppToServiceEvent.serviceStatus.value != AppToServiceEvent.ServiceStatus.ON) return
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

            val targetWidth = CURSOR_IMAGE_WIDTH
            val scaleFactor = targetWidth / originalBitmap.width.toFloat()
            val targetHeight = (originalBitmap.height * scaleFactor).toInt()
            val resizedBitmap = originalBitmap.scale(targetWidth, targetHeight)

            val outputFile = File(context.filesDir, CursorType.CUSTOM.fileName)
            val outputStream = FileOutputStream(outputFile)
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

            outputStream.flush()
            outputStream.close()
            resizedBitmap.recycle()
            originalBitmap.recycle()

            Log.d(TAG, "Custom cursor image loaded successfully")

            if (AppToServiceEvent.serviceStatus.value != AppToServiceEvent.ServiceStatus.ON) return
            AppToServiceEvent.event.tryEmit(AppToServiceEvent.Event.UpdateCursorType)
        } catch (e: IOException) {
            Log.e(TAG, "Error loading custom cursor image", e)
            Toast.makeText(
                context,
                context.getString(R.string.settings_change_cursor_file_load_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun toggleSettingsResetDialog(show: Boolean) {
        _state.update { it.copy(showSettingsResetDialog = show) }
    }

    fun resetSettings() {
        preferencesManager.reset()
        File(context.filesDir, CursorType.CUSTOM.fileName).delete()
        loadInitialSettings()
        onCursorTypeChange(state.value.cursorType)
    }

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}