package pl.jojczak.penmouses.ui.main

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