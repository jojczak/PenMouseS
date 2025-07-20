package pl.jojczak.penmouses.shortcuts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import pl.jojczak.penmouses.service.MouseService // Import MouseService
import pl.jojczak.penmouses.ui.main.MainActivity // Import MainActivity
import android.view.accessibility.AccessibilityManager // For AccessibilityManager
import android.accessibilityservice.AccessibilityServiceInfo // For AccessibilityServiceInfo
import android.content.Context
import android.provider.Settings // For Settings.ACTION_ACCESSIBILITY_SETTINGS

const val EXTRA_MINIMIZE_APP = "extra_minimize_app" // Define a constant for the extra

class StartMouseShortcutActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "StartToggleAct"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: StartMouseShortcutActivity launched.")

        // 1. Check if the Accessibility Service is enabled in system settings
        val isServiceEnabledInSettings = isAccessibilityServiceEnabled(
            this,
            MouseService::class.java.name
        )
        Log.d(TAG, "MouseService enabled in settings: $isServiceEnabledInSettings")

        if (!isServiceEnabledInSettings) {
            // If the service is NOT enabled in settings, open Accessibility Settings
            Log.d(TAG, "Service not enabled in settings. Opening Accessibility Settings.")
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS) // Add android.provider.Settings import
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            // If the service IS enabled in settings, proceed with the toggle logic via broadcast
            Log.d(TAG, "Service enabled in settings. Sending toggle broadcast.")
            val toggleIntent = Intent(ACTION_TOGGLE_MOUSE_SERVICE).apply {
                setPackage(this@StartMouseShortcutActivity.packageName)
            }
            sendBroadcast(toggleIntent)
            Log.d(TAG, "Toggle broadcast sent.")

            // NOW, launch MainActivity with a flag to tell it to minimize
            val mainActivityIntent = Intent(this, MainActivity::class.java).apply {
                // This flag is crucial when starting an Activity from a context that isn't already an Activity (like from a BroadcastReceiver, or a proxy Activity that's immediately finishing)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                // This flag ensures that if MainActivity is already running, it's brought to the front and its onNewIntent() is called
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                // Add our custom extra to signal MainActivity to minimize itself
                putExtra(EXTRA_MINIMIZE_APP, true)
            }
            startActivity(mainActivityIntent)
            Log.d(TAG, "Launched MainActivity with minimize flag.")
        }

        // Finish this activity immediately.
        finish()
    }

    private fun isAccessibilityServiceEnabled(context: Context, serviceClassName: String): Boolean {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)

        for (service in enabledServices) {
            if (service.resolveInfo.serviceInfo.name == serviceClassName) {
                return true
            }
        }
        return false
    }
}