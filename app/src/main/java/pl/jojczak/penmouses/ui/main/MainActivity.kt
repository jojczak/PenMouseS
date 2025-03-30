package pl.jojczak.penmouses.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import pl.jojczak.penmouses.service.MouseService
import pl.jojczak.penmouses.ui.home.HomeScreen
import pl.jojczak.penmouses.ui.theme.PenMouseSTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startService(Intent(this, MouseService::class.java))
        setShowWhenLocked(true)

        setContent { AppContent() }
    }
}

@Composable
fun AppContent() {
    PenMouseSTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            HomeScreen()
        }
    }
}