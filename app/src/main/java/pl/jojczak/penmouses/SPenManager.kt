package pl.jojczak.penmouses

import android.content.Context
import android.util.Log
import com.samsung.android.sdk.penremote.AirMotionEvent
import com.samsung.android.sdk.penremote.ButtonEvent
import com.samsung.android.sdk.penremote.SpenEvent
import com.samsung.android.sdk.penremote.SpenEventListener
import com.samsung.android.sdk.penremote.SpenRemote
import com.samsung.android.sdk.penremote.SpenRemote.Error.CONNECTION_FAILED
import com.samsung.android.sdk.penremote.SpenRemote.Error.UNSUPPORTED_DEVICE
import com.samsung.android.sdk.penremote.SpenUnit
import com.samsung.android.sdk.penremote.SpenUnitManager


class SPenManager(
    private val activityContext: Context
) {
    private var sPenUnitManager: SpenUnitManager? = null

    fun connectToSPen(callback: (x: Float, y: Float) -> Unit) {
        Log.i(TAG, "Connecting to S-Pen...")
        val sPenRemote = SpenRemote.getInstance()

        if (sPenRemote.isFeatureEnabled(SpenRemote.FEATURE_TYPE_BUTTON) &&
            sPenRemote.isFeatureEnabled(SpenRemote.FEATURE_TYPE_AIR_MOTION)) {
            if (!sPenRemote.isConnected) {
                sPenRemote.connect(activityContext, ConnectionResultCallback(callback))
            }
        }
    }

    private inner class ConnectionResultCallback(
        private val callback: (x: Float, y: Float) -> Unit
    ) : SpenRemote.ConnectionResultCallback {
        override fun onSuccess(sPenUnitManager: SpenUnitManager?) {
            Log.i(TAG, "Connection successful")
            this@SPenManager.sPenUnitManager = sPenUnitManager
            registerAirMotionEventListener(callback)
        }

        override fun onFailure(errorCode: Int) {
            Log.e(TAG, "Connection failed: ${mapConnectionError(errorCode)}")
        }
    }

    fun registerButtonEventListener() {
        sPenUnitManager?.let {
            val buttonUnit = it.getUnit(SpenUnit.TYPE_BUTTON)
            it.registerSpenEventListener(ButtonEventListener(), buttonUnit)

            Log.i(TAG, "Button event listener registered")
        }
    }

    private inner class ButtonEventListener : SpenEventListener {
        override fun onEvent(event: SpenEvent) {
            val mEvent = ButtonEvent(event)

            when (mEvent.action) {
                ButtonEvent.ACTION_DOWN -> {
                    Log.d(TAG, "Button pressed")
                }
                ButtonEvent.ACTION_UP -> {
                    Log.d(TAG, "Button released")
                }
            }
        }
    }

    fun registerAirMotionEventListener(callback: (x: Float, y: Float) -> Unit) {
        sPenUnitManager?.let {
            val airMotionUnit = it.getUnit(SpenUnit.TYPE_AIR_MOTION)
            it.registerSpenEventListener(AirMotionEventListener(callback), airMotionUnit)
        }
    }

    private inner class AirMotionEventListener(
        private val callback: (x: Float, y: Float) -> Unit
    ) : SpenEventListener {
        override fun onEvent(event: SpenEvent) {
            val mEvent = AirMotionEvent(event)
            callback(mEvent.deltaX, mEvent.deltaY)

        }
    }

    private fun mapConnectionError(errorCode: Int) = when (errorCode) {
        UNSUPPORTED_DEVICE -> "Unsupported device"
        CONNECTION_FAILED -> "Connection failed"
        else -> "Unknown error"
    }

    companion object {
        private const val TAG = "SPenManager"
    }
}