package pl.jojczak.penmouses.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import pl.jojczak.penmouses.service.AppToServiceEvent

class NotificationsActionReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.getIntExtra(ACTION, -1)
        if (action == ACTION_TYPE_STOP) {
            Log.d(TAG, "Stopping AirMouse")
            AppToServiceEvent.event.tryEmit(AppToServiceEvent.Event.Stop)
        }
    }

    companion object {
        private const val TAG = "NotificationsActionReceiver"

        const val ACTION = "parameter_action"
        const val ACTION_TYPE_STOP = 0
    }
}