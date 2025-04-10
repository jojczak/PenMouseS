package pl.jojczak.penmouses.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import pl.jojczak.penmouses.service.AppToServiceEvent
import pl.jojczak.penmouses.ui.home.HomeScreen
import pl.jojczak.penmouses.ui.theme.PenMouseSTheme
import pl.jojczak.penmouses.utils.SPenManager
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sPenManager: SPenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AppContent() }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        sPenManager.disconnectFromSPen()
        AppToServiceEvent.event.tryEmit(AppToServiceEvent.Event.StopOnDestroy)
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

@Composable
fun AppContent() {
    PenMouseSTheme {
        Scaffold { paddings ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddings)
            ) {
                HomeScreen()
            }
        }
    }
}