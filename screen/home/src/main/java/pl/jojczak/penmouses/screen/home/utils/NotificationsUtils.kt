package pl.jojczak.penmouses.screen.home.utils

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat

@Composable
internal fun getNotificationLauncher(afterCheck: () -> Unit) = rememberLauncherForActivityResult(
ActivityResultContracts.RequestPermission()
) {
    Log.d(TAG, "Notification permission ${if (it) "granted" else "denied"}")
    afterCheck()
}

internal fun checkNotifications(
    context: Context,
    notificationLauncher: ManagedActivityResultLauncher<String, Boolean>,
    afterCheck: () -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }
    }
    afterCheck()
}