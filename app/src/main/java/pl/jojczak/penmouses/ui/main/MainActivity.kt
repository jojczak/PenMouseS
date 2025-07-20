package pl.jojczak.penmouses.ui.main

import android.content.Intent // Import Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import pl.jojczak.penmouses.notifications.NotificationsManager
import pl.jojczak.penmouses.service.AppToServiceEvent
import pl.jojczak.penmouses.ui.theme.PenMouseSTheme
import pl.jojczak.penmouses.utils.SPenManager
import javax.inject.Inject
import pl.jojczak.penmouses.shortcuts.EXTRA_MINIMIZE_APP // Import your constant

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sPenManager: SPenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreenHelper = SplashScreenHelper(this, installSplashScreen(), savedInstanceState == null)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AppContent() }
        splashScreenHelper.startExitAnimation()
        NotificationsManager.createNotificationChannels(this)

        // Use this.intent (which is a nullable Intent?)
        handleMinimizeIntent(this.intent)
    }

    // CORRECTED SIGNATURE HERE: Making intent non-nullable to match Java API
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Update the activity's current Intent
        handleMinimizeIntent(intent) // Call new helper function
    }

    private fun handleMinimizeIntent(intent: Intent?) { // This function still accepts nullable Intent
        Log.d(TAG, "handleMinimizeIntent called. Intent: $intent")
        // Safely check if intent is not null and if the extra exists
        if (intent?.getBooleanExtra(EXTRA_MINIMIZE_APP, false) == true) {
            Log.d(TAG, "MINIMIZE_APP flag detected. Moving task to back.")
            moveTaskToBack(true)
        } else {
            Log.d(TAG, "MINIMIZE_APP flag not detected.")
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        if (!isChangingConfigurations) {
            sPenManager.disconnectFromSPen()
            AppToServiceEvent.event.tryEmit(AppToServiceEvent.Event.StopOnDestroy)
        }
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

@Composable
fun AppContent() {
    PenMouseSTheme {
        PenMouseSContent()
    }
}