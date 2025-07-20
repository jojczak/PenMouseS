package pl.jojczak.penmouses.shortcuts

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityManager
import pl.jojczak.penmouses.service.AppToServiceEvent
import pl.jojczak.penmouses.service.MouseService // Import MouseService

const val ACTION_TOGGLE_MOUSE_SERVICE = "pl.jojczak.penmouses.action.TOGGLE_MOUSE_SERVICE"

class ToggleMouseReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "ToggleReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive: Broadcast received. Action: ${intent.action}")

        if (intent.action == ACTION_TOGGLE_MOUSE_SERVICE) {
            Log.d(TAG, "Action matches $ACTION_TOGGLE_MOUSE_SERVICE. Proceeding with toggle logic.")

            // NOW WE CHECK THE INTERNAL RUNNING STATE OF THE MOUSE SERVICE
            val isMouseServiceInternallyRunning = MouseService.isServiceInternallyRunning
            Log.d(TAG, "MouseService current INTERNAL status: $isMouseServiceInternallyRunning")

            if (isMouseServiceInternallyRunning) {
                Log.d(TAG, "Service is internally active. Attempting to send STOP event.")
                AppToServiceEvent.event.tryEmit(AppToServiceEvent.Event.Stop)
            } else {
                Log.d(TAG, "Service is NOT internally active. Attempting to send START event.")
                AppToServiceEvent.event.tryEmit(AppToServiceEvent.Event.Start)
            }
            Log.d(TAG, "Toggle logic completed.")
        } else {
            Log.d(TAG, "Received unexpected action: ${intent.action}")
        }
    }

    // You can remove the 'isAccessibilityServiceEnabled' helper function from here
    // as we are no longer relying on the system's accessibility setting for toggling.
    // However, if you want to keep it for initial checks or other purposes, you can.
    // For a pure internal toggle, it's not strictly necessary.
    /*
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
    */
}