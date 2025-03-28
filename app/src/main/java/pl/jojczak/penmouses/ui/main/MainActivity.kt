package pl.jojczak.penmouses.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pl.jojczak.penmouses.PenMouseSApp
import pl.jojczak.penmouses.utils.SPenManager
import pl.jojczak.penmouses.service.MouseService
import pl.jojczak.penmouses.ui.theme.PenMouseSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as PenMouseSApp).sPenManager = SPenManager(this)

        startService(Intent(this, MouseService::class.java))
        setShowWhenLocked(true)

        setContent(
            content = {
                PenMouseSTheme {
                    Greeting("Android")
                }
            }
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PenMouseSTheme {
        Greeting("Android")
    }
}